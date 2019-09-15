package com.lsgdx.game.Algorithm;

import com.badlogic.gdx.ai.pfa.Connection;
import com.badlogic.gdx.utils.Array;

public class PathFinding {
    /*
    F : total cost of the node
    G : distance between the current and start nodes
    H : heuristic estimated distance from the current to end nodes
     */

    /*
    현 위치 노드에서 부터 4방향(좌/우/상/하)에 이동가능한 블록을 조사
    
    */
    public static void GeneratePath(Node starNode, Node endNode, Array<Node> array, Array<Node> open, Array<Node> close, HeuristicCalculation heuristicCalculation){
        array.clear(); // 최종 경로 리스트
        open.clear();  // 연산이 안된 열린 블록 리스트
        close.clear(); // 연산이 되어 닫힌 블록 리스트

        Node current = starNode;
        current.g = 0; // 시작점은 첫 노드이기에 g 비용을 0으로 초기화
        current.f = current.h = heuristicCalculation.estimate(current, endNode); // h cost & f cost 초기화

        open.add(current); // 연산이 가능한 현재 노드 입력

        // 해당 노드의 연결된 모든 노드를 검사하는 loop
        while(!open.isEmpty()) {
            // 열린 목록 중 최소 비용의 노드를 찾는다
            // loop 노드가 현재노드보다 f값이 낮으면 입력
            Node lowest = open.get(0);
            for(Node node : open){
                if(node.f < lowest.f){
                    lowest = node;
                }
            }
            current = lowest;

            // 현재 노드가 목표 노드면 break
            if(current == endNode){
                reconstructPath(array, open, close, endNode);
                return;
            }

            // f 비용이 최소인 노드를 선택하여 open에서 제거, close에 입력
            open.removeValue(current, true);
            close.add(current);

            // 최소 비용으로 선택된 노드의 인접 노드 확인
            for(Connection<Node> nodes : current.getConnections()) {
                if(nodes.getToNode().type != Node.Type.BLOCK){ // 연결된 노드가 Block이 아닌 경우에만 실행
                    // 해당 노드가 이미 닫혀있는 노드면 무시
                    if(close.contains(nodes.getToNode(), true)){
                        continue;
                    }

                    // 새로운 노드 발견 시 open에 입력
                    if(!open.contains(nodes.getToNode(), true)){
                        open.add(nodes.getToNode());
                    }

                    // 시작부터 current까지 g + crrent부터 현재 노드까지 g = 시작부터 현재 노드까지 g
                    float gCost = current.g + nodes.getCost();
                    // 시작부터 인접까지 g 비용이 current가 인접까지 가는 비용보다 싸면 무시
                    if(gCost >= nodes.getToNode().g){
                        continue;
                    }

                    // current 노드를 거쳐 가는 것이 더 싸면 기록
                    nodes.getToNode().g = gCost;
                    nodes.getToNode().h = heuristicCalculation.estimate(nodes.getToNode(), endNode);
                    nodes.getToNode().f = nodes.getToNode().g + nodes.getToNode().h;
                    nodes.getToNode().parent = current;
                    /*
                                      G cost
                              [인접][시작][현재][인접]
                    시작기준 :   1     0    1     2
                    현재기준 :   2     1    0     1
                    
                        [도착][인접][시작][현재][인접]
                    - 도착점에 왼편에 있으면 기록하지 않음
                    
                              [인접][시작][현재][인접][도착]
                    - 도착점이 오른편에 있으면 기록함
                    */
                }
            }
        }

        return;
    }

    private static void reconstructPath(Array<Node> array, Array<Node> open, Array<Node> close, Node endNode){
        array.add(endNode);

        while(endNode.parent != null){
            endNode = endNode.parent;
            array.add(endNode);
        }

        array.reverse();

        for(Node node : open){
            node.resetNode();
        }
        for(Node node : close){
            node.resetNode();
        }
        System.gc();
    }
}
