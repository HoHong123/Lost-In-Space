package com.lsgdx.game.Character.Enemy;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.lsgdx.game.Algorithm.HeuristicCalculation;
import com.lsgdx.game.Algorithm.Node;
import com.lsgdx.game.Algorithm.NodeGraph;
import com.lsgdx.game.Algorithm.NodeGraphGenerate;
import com.lsgdx.game.Algorithm.PathFinding;
import com.lsgdx.game.LostInSpace;

import java.util.Random;


public class EnemyAI_Run implements EnemyState {
    private Node targetNode;
    private Node currentNode;

    private boolean arrive = true;

    private float timer;
    private final float WAIT = 30f;
    private final float WAIT_FAST = 20f;

    private int accel;
    private float moveSpeed;
    private float moveX, moveY;
    private float preMoveY;
    private Vector2 moveFrom, moveTo;

    private EnemyAI ai;
    private NodeGraph nodeGraph;

    private Array<Node> movePath;
    private Array<Node> open, close;
    private Array<Vector2> searchSpot;

    private HeuristicCalculation heuristicCalculation;

    private Random random;
    private ShapeRenderer shapeRenderer;


    public EnemyAI_Run(EnemyAI enemyAI){
        timer = 0.1f;

        ai = enemyAI;

        random = new Random();

        targetNode = new Node();
        currentNode = new Node();
        moveFrom = moveTo = new Vector2();

        open = new Array<Node>();
        close = new Array<Node>();
        movePath = new Array<Node>();
        searchSpot = new Array<Vector2>();

        heuristicCalculation = new HeuristicCalculation(ai.enemy.screen);

        shapeRenderer = new ShapeRenderer();

        // 연결된 노드 그래프 생성, 이동이 가능한 Path를 제외한 노드 type을 Block으로 설정
        nodeGraph = new NodeGraphGenerate().generateGroundGraph(ai.enemy.screen);

        // 노드의 이동 속도를 위해 두 노드 사이의 간격을 확인할 두 벡터
        // 노드 크기는 가로/세로 일정하기에 한 축만 빼도 됌
        // 숫자가 작을 수록 빠르다
        setMoveSpeed(15);

        // SearchSpot 위치 받기
        for(MapObject object : ai.enemy.screen.getMap().getLayers().get(10).getObjects().getByType(RectangleMapObject.class)){
            Vector2 v = new Vector2(((RectangleMapObject) object).getRectangle().getX(), ((RectangleMapObject) object).getRectangle().getY());
            searchSpot.add(v);
        }

        // AI Path 위치 받아 Path인 구역 노드 type을 Regular로 변경
        for(MapObject object : ai.enemy.screen.getMap().getLayers().get(11).getObjects().getByType(RectangleMapObject.class)){
            Vector2 v = new Vector2(((RectangleMapObject) object).getRectangle().getX(), ((RectangleMapObject) object).getRectangle().getY());
            nodeGraph.getNodeByXY((int)v.x, (int)v.y).type = Node.Type.REGULAR;
        }

        ai.enemy.setBodyPosition(searchSpot.get(0).x / LostInSpace.PPM, searchSpot.get(0).y / LostInSpace.PPM);
        ai.setPlayerFound(false);
        ai.setPlayerInRange(false);
    }

    @Override
    public void update(float dt) {
        Vector2 v = ai.enemy.body.getPosition();
        currentNode = nodeGraph.getNodeByXY((int) (v.x * LostInSpace.PPM), (int) (v.y * LostInSpace.PPM));

        if(!ai.isPlayerFound()) { // 플레이어를 못 찾으면 실행
            if(arrive){
                arrive = false;

                // 목표와 현재 위치 노드를 확인 및 설정
                v = searchSpot.get(random.nextInt(searchSpot.size - 1));
                targetNode = nodeGraph.getNodeByXY((int) v.x, (int) v.y);
                PathFinding.GeneratePath(currentNode, targetNode, movePath, open, close, heuristicCalculation);
            }

            movementControl();
        }
        else
        { // 플레이어를 찾음
            if(timer < 0){ // 추격 시간이 끝나면
                dispose(); // 초기화
                if(!ai.isPlayerInRange()){ // 플레이어가 사거리 밖인가?
                    // 플레이어 추격 정지 후 다른 위치 선정하여 이동
                    ai.setPlayerFound(false);
                    arrive = true;
                    timer = -1;

                    ai.switchAI(EnemyAI.State.STAND);
                    return;
                }
                // 플레이어가 범위 안에 존재하면 다시 추격 시작
                timer = WAIT_FAST;
            } else {  // 해당 시간동안 플레이어 추격
                ai.playSound(dt);

                timer -= dt;
                Vector2 v1 = ai.enemy.screen.getPlayer().body.getPosition();

                targetNode = nodeGraph.getNodeByXY((int)(v1.x * LostInSpace.PPM), (int)(v1.y * LostInSpace.PPM));
                PathFinding.GeneratePath(currentNode, targetNode, movePath, open, close, heuristicCalculation);

                movementControl();
            }
        }
        draw();
    }

    private void movementControl(){
        // 현재 노드가 도착점이거나 다음 노드가 없으면 종료
        if(currentNode == targetNode || movePath.get(1) == null){
            dispose();
            arrive = true;
            ai.switchAI(EnemyAI.State.STAND);
            return;
        }

        if(currentNode.index == movePath.get(1).index){
            movePath.removeValue(movePath.first(), true);
        }
        moveFrom = nodeGraph.getNodeVector(movePath.first()); // 현재 노드 벡터 입력
        moveTo = nodeGraph.getNodeVector(movePath.get(1)); // 다음 노드 벡터 입력

        moveX = (moveTo.x - moveFrom.x);
        moveY = (moveTo.y - moveFrom.y);
        // 아래에서 위 방향으로 올라가며 발생하는 y축의 오차를 처리
        if((moveY == 0) && (preMoveY > 0)){
            float move = (ai.enemy.screen.getTilePixelHeight()/2) / LostInSpace.PPM;
            ai.enemy.moveBody(0, move);
        }
        ai.enemy.getEnemySpriteInfo().setFacingRight((moveX > 0) ? true : false);


        if(moveX != 0){
            ai.enemy.moveBody((moveX > 0)?moveSpeed:-moveSpeed, 0);
            ai.enemy.getEnemySpriteInfo().setState(EnemySprite.State.RUN);
        }
        if(moveY != 0){
            ai.enemy.moveBody(0, (moveY > 0)?moveSpeed:-moveSpeed);
            ai.enemy.getEnemySpriteInfo().setState(EnemySprite.State.CLIMING);
        }

        preMoveY = moveY;
    }

    private void draw(){
        shapeRenderer.setProjectionMatrix(ai.enemy.screen.getCamera().combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        /*
        // Draw Path2
        // 노드 타입이 Block인 것은 검은색
        // 노드 타입이 Regular인 것은 청록색
        for(Node node : nodeGraph.getNodes()){
            if(node.type == Node.Type.BLOCK){
                shapeRenderer.setColor(0, 0, 0, 1);
            } else {
                shapeRenderer.setColor(0, 1, 1, 1);
            }

            shapeRenderer.circle(nodeGraph.getNodeVector(node).x,
                    nodeGraph.getNodeVector(node).y, .2f);
        }
        */

        shapeRenderer.setColor(0, 0, 1, 1);
        if(movePath != null)
            for(Node node : movePath)
                shapeRenderer.circle(nodeGraph.getNodeVector(node).x, nodeGraph.getNodeVector(node).y, .2f);

        shapeRenderer.setColor(1, 0, 0, 1);
        shapeRenderer.circle(ai.enemy.body.getPosition().x, ai.enemy.body.getPosition().y, .2f);

        shapeRenderer.setColor(0, 0, 1, 1);
        if(movePath != null)
            for(Node node : movePath)
                shapeRenderer.circle(nodeGraph.getNodeVector(node).x, nodeGraph.getNodeVector(node).y, .2f);

        shapeRenderer.setColor(0, 1, 0, 1);
        shapeRenderer.circle(nodeGraph.getNodeVector(targetNode).x,nodeGraph.getNodeVector(targetNode).y, .2f);

        shapeRenderer.setColor(1, 1, 0, 1);
        shapeRenderer.circle(nodeGraph.getNodeVector(currentNode).x,nodeGraph.getNodeVector(currentNode).y, .2f);

        shapeRenderer.end();
    }

    public void setMoveSpeed(int percent){
        if(percent > 0){
            accel = percent;

            moveSpeed = Math.abs((nodeGraph.getNodeVector(nodeGraph.getNodes().get(0)).x -
                    nodeGraph.getNodeVector(nodeGraph.getNodes().get(1)).x)) / accel;
        }
    }

    private void dispose(){
        moveX = moveY = preMoveY = 0;
    }

    public void reset(){
        ai.enemy.setBodyPosition(searchSpot.get(0).x / LostInSpace.PPM, searchSpot.get(0).y / LostInSpace.PPM);
        arrive = true;
        timer = -1;
        movePath.clear();
        close.clear();
        open.clear();
    }

    private Node getCurrenetNode(){
        Vector2 v = ai.enemy.body.getPosition();
        return nodeGraph.getNodeByXY((int)(v.x * LostInSpace.PPM),(int)(v.y * LostInSpace.PPM));
    }
}