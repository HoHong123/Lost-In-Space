package com.lsgdx.game.Algorithm;

import com.lsgdx.game.Screen.Level_One;

public class HeuristicCalculation {

    private Level_One level;

    public HeuristicCalculation(Level_One screen){
        level = screen;
    }

    public float estimate(Node startNode, Node endNode) {
        // 시작점과 끝점 확인을 위해 현 위치 값 가져오기
        int startPos = startNode.index;
        int endPos = endNode.index;

        // 맵 타일의 가로, 세로 중 시작 위치 찾기
        int startX = startPos / level.getMapWidth();
        int startY = startPos % level.getMapHeight();

        // 맵 타일의 가로, 세로 중 끝 위치 찾
        int endX = endPos / level.getMapWidth();
        int endY = endPos % level.getMapHeight();

        // 시작점과 끝넘의 거리 값 반환
        return Math.abs(startX - endX) + Math.abs(startY - endY);
    }
}
