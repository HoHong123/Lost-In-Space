package com.lsgdx.game.Algorithm;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.lsgdx.game.LostInSpace;
import com.lsgdx.game.Screen.Level_One;

public class NodeGraph {
    protected Array<Node> nodes;

    private Level_One level;

    // 노드를 받으면 입력하는 생성자
    public NodeGraph(Array<Node> nodes, Level_One screen) {
        this.nodes = nodes;
        level = screen;

        if(nodes != null){
            // 각 노드에 번호 부여 (좌 -> 우, 상 -> 하)
            for (int x = 0; x < nodes.size; ++x) {
                nodes.get(x).index = x;
            }
        }
    }

    public Vector2 getNodeVector(Node node){
        int index = node.index;
        float x = ((index % level.getMapWidth()) * level.getTilePixelWidth()) / LostInSpace.PPM;
        float y = (((index - (index % level.getMapWidth())) / level.getMapWidth()) * level.getTilePixelHeight()) / LostInSpace.PPM;

        return new Vector2(x,y);
    }

    // x, y 좌표값 위치의 노드 반환
    public Node getNodeByXY(int x, int y) {
        int modX = x / level.getTilePixelWidth();
        int modY = y / level.getTilePixelHeight();

        return nodes.get(level.getMapWidth() * modY + modX);
    }

    public Node getNodeByXY(Vector2 v2) {
        int modX = (int)v2.x / level.getTilePixelWidth();
        int modY = (int)v2.y / level.getTilePixelHeight();

        return nodes.get(level.getMapWidth() * modY + modX);
    }

    public int getIndex(Node node) {
        return nodes.indexOf(node, true);
    }

    public int getNodeCount() {
        return nodes.size;
    }

    public Array<Node> getNodes(){
        return nodes;
    }
}
