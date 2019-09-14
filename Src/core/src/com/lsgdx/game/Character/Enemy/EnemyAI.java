package com.lsgdx.game.Character.Enemy;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.lsgdx.game.LostInSpace;

import java.util.Random;

public class EnemyAI implements EnemyState {
    public enum State { STAND, RUN };

    public Enemy enemy;
    private boolean playerFound = false;
    private boolean playerInRange = false;
    public boolean isPlayerFound() {
        return playerFound;
    }
    public boolean isPlayerInRange() {
        return playerInRange;
    }
    public void setPlayerFound(boolean playerFound) {
        this.playerFound = playerFound;
    }
    public void setPlayerInRange(boolean playerInRange) {
        this.playerInRange = playerInRange;
    }

    private EnemyState state;
    private EnemyAI_Run enemyAIRun;
    private EnemyAI_Stand enemyAIStand;

    private Random random;

    public Sound[] sound;
    private float soundDelay;
    private final float soundDelayMax = 1.5f;


    public EnemyAI(Enemy enemy){
        this.enemy = enemy;

        random = new Random();

        enemyAIRun = new EnemyAI_Run(this);
        enemyAIStand = new EnemyAI_Stand(this);
        switchAI(State.STAND);

        sound = new Sound[4];
        sound[0] = Gdx.audio.newSound(Gdx.files.internal("Sounds/alien_chase1.mp3"));
        sound[1] = Gdx.audio.newSound(Gdx.files.internal("Sounds/alien_chase2.wav"));
        sound[2] = Gdx.audio.newSound(Gdx.files.internal("Sounds/alien_chase3.wav"));
        sound[3] = Gdx.audio.newSound(Gdx.files.internal("Sounds/alien_chase4.wav"));
    }

    @Override
    public void update(float dt) {
        state.update(dt);
    }

    public void switchAI(State state){
        //Gdx.app.debug("AI State", state.name());
        switch (state){
            case RUN :
                this.state = enemyAIRun;
                break;
            case STAND:
                enemyAIStand.setRandomTime(5, 3);
                enemy.getEnemySpriteInfo().setState(EnemySprite.State.STAND);
                this.state = enemyAIStand;
                break;
        }
    }

    public void playSound(float dt){
        if(soundDelay > 0){
            soundDelay -= dt;
            return;
        }

        sound[random.nextInt(sound.length)].play(LostInSpace.EFFECT_VOLUM);
        soundDelay = soundDelayMax;
    }

    public void Dispose(){
        for(int i = sound.length-1; i > 0; i--)
            sound[i].dispose();
        sound = null;
    }

    public void reset(){
        enemyAIRun.reset();
        enemy.getEnemyAI().setPlayerFound(false);
        enemy.getEnemyAI().setPlayerInRange(false);
        switchAI(State.STAND);
    }
}
