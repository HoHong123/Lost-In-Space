package com.lsgdx.game.Algorithm;

import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.utils.Array;
import com.lsgdx.game.Screen.Level_One;

public class NodeGraphGenerate {
    public static NodeGraph generateGroundGraph(Level_One level) {
        TiledMapTileLayer tiles = (TiledMapTileLayer)level.getMap().getLayers().get(0);
        int mapWidth = level.getMapWidth(); // 64
        int mapHeight = level.getMapHeight(); // 32

        Array<Node> nodes = new Array<Node>();

        // 왼편 하단부터 맵을 둘러봄
        // 왼편 -> 오른편, 아래 -> 위
        for (int nodeMax = mapWidth * mapHeight; nodeMax > 0; nodeMax--) {
            Node node = new Node();
            node.type = Node.Type.BLOCK;
            nodes.add(node);
        }

        // 각 셀마다 상하좌우 연결되어 있는 셀 정보를 저장
        for (int y = 0; y < mapHeight; ++y) {
            for (int x = 0; x < mapWidth; ++x) {
                TiledMapTileLayer.Cell target = tiles.getCell(x, y);
                TiledMapTileLayer.Cell up = tiles.getCell(x, y+1);
                TiledMapTileLayer.Cell down = tiles.getCell(x, y-1);
                TiledMapTileLayer.Cell left = tiles.getCell(x-1, y);
                TiledMapTileLayer.Cell right = tiles.getCell(x+1, y);

                Node targetNode = nodes.get(mapWidth * y + x);
                if (target != null) { // x, y 좌표에 셀이 존재하면
                    if ((y > 0 && y < mapHeight) && down != null) { // 아래쪽 셀이 존재하면
                        Node downNode = nodes.get(mapWidth * (y - 1) + x); // 아래 셀 정보를 받는 객체 생성
                        targetNode.createConnection(downNode, 1); // 아래 노드 연결 노드로 입력
                    }
                    if ((y > 0 && y < mapHeight) && up != null) { // 위쪽 셀이 존재하면
                        Node upNode = nodes.get(mapWidth * (y + 1) + x); // 위쪽 셀 정보를 받는 객체 생성
                        targetNode.createConnection(upNode, 1); // 위쪽 노드 연결 노드로 입력
                    }
                    if ((x > 0 && x < mapWidth) && left != null) { // 왼쪽 셀이 존재하면
                        Node leftNode = nodes.get(mapWidth * y + x - 1); // 왼쪽 셀 정보를 받는 객체 생성
                        targetNode.createConnection(leftNode, 1); // 왼쪽 노드 연결 노드로 입력
                    }
                    if ((x > 0 && x < mapWidth) && right != null) { // 오른쪽 셀이 존재하면
                        Node rightNode = nodes.get(mapWidth * y + x + 1); // 오른쪽 셀 정보를 받는 객체 생성
                        targetNode.createConnection(rightNode, 1); // 오른쪽 노드 연결 노드로 입력
                    }
                }
            }
        }
        return new NodeGraph(nodes, level);
    }
}
