package com.lsgdx.game.Algorithm;

import com.badlogic.gdx.ai.pfa.Connection;
import com.badlogic.gdx.utils.Array;

public class Node {
    // 노드와 연결된 노드 정보 입력
    private Array<Connection<Node>> connections = new Array<Connection<Node>>();
    public int type;
    public int index;
    public float f, g, h;
    public Node parent;

    public Array<Connection<Node>> getConnections() {
        return connections;
    }

    public void createConnection(Node toNode, float cost) {
        parent = new Node();
        resetNode();
        connections.add(new ConnectingNode(this, toNode, cost));
    }

    public void resetNode(){
        h = 0;
        f = g = 99999;
        parent = null;
    }

    public static class Type {
        public static final int REGULAR = 1;
        public static final int BLOCK = -1;
    }
}
