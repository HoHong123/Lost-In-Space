package com.lsgdx.game.Algorithm;

import com.badlogic.gdx.ai.pfa.Connection;

public class ConnectingNode implements Connection<Node> {
    private Node toNode;
    private Node fromNode;
    private float cost;

    public ConnectingNode(Node fromNode, Node toNode, float cost) {
        this.fromNode = fromNode;
        this.toNode = toNode;
        this.cost = cost;
    }

    @Override
    public float getCost() {
        return cost;
    }

    @Override
    public Node getFromNode() {
        return fromNode;
    }

    @Override
    public Node getToNode() {
        return toNode;
    }
}
