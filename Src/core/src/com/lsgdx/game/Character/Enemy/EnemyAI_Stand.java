package com.lsgdx.game.Character.Enemy;

import java.util.Random;

public class EnemyAI_Stand implements EnemyState {
    private EnemyAI ai;

    private float waitTime;
    private Random random;


    public EnemyAI_Stand(EnemyAI enemyAI){
        ai = enemyAI;
        random = new Random();
    }

    @Override
    public void update(float dt) {
        if(waitTime > 0){
            waitTime -= dt;
            return;
        }
        ai.switchAI(EnemyAI.State.RUN);
    }

    public void setRandomTime(float max, float min){
        waitTime = (random.nextFloat() * (max - min)) + min;
    }
}
