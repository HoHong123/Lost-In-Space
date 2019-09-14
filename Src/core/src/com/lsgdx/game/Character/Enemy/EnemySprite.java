package com.lsgdx.game.Character.Enemy;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import com.lsgdx.game.LostInSpace;

public class EnemySprite extends Sprite {
    public enum State { STAND, RUN, CHASE, CLIMING };

    public State currentState;
    public State preState;

    private float stateTimer;

    public boolean onLadder, onHideSpot;
    private boolean facingRight;

    private Animation<TextureRegion> enemyStand;
    private Animation<TextureRegion> animRun;
    private Animation<TextureRegion> animLadder;

    private Enemy enemy;


    public EnemySprite(Enemy enemy){
        super(enemy.screen.getAtlas().findRegion("alienStand"));
        this.enemy = enemy;

        currentState = preState = State.STAND;

        stateTimer = 0;
        facingRight = true;
        onHideSpot = onLadder = false;


        // Setup the animation array
        Array<TextureRegion> frames = new Array<TextureRegion>();
        for(int i = 0; i < 5; i++){
            frames.add(new TextureRegion(getTexture(), i * 42 + 2, 2, 42,24));
        }
        animRun = new Animation(0.2f, frames);
        frames.clear();

        for(int i = 0; i < 2; i++){
            frames.add(new TextureRegion(getTexture(), i * 20 + 84, 80, 20,32));
        }
        animLadder = new Animation(0.2f, frames);
        frames.clear();

        frames.add(new TextureRegion(getTexture(), 84, 54, 42, 24));
        enemyStand = new Animation(1f,frames);
        frames.clear();

        // Define Sprite width and height
        setBounds(84, 27, 38 / LostInSpace.PPM, 30 / LostInSpace.PPM);
        setRegion(getFrame(0));
    }

    public void update(float dt){
        setPosition(enemy.body.getPosition().x - (getWidth()/2), enemy.body.getPosition().y - (getHeight()/2) + 0.02f);
        setRegion(getFrame(dt));
    }

    private TextureRegion getFrame(float dt){
        currentState = getState();

        TextureRegion region = new TextureRegion();
        switch (currentState){
            case RUN :
            case CHASE:
                region = animRun.getKeyFrame(stateTimer, true);
                break;
            case CLIMING:
                region = animLadder.getKeyFrame(stateTimer, true);
                break;
            default:
                region = enemyStand.getKeyFrame(stateTimer, true);
                break;
        }

        // Heading left, when velocity is negative or isn't facing left and X axis of region is not flipped
        // isFlipX == True : facing left, isFlipX == false : facing right
        if(currentState != State.STAND){
            if((!facingRight) && !region.isFlipX()){ // 왼쪽을 목표, 오른쪽을 바라봄
                region.flip(true, false); // 전환
            } else if((facingRight) && region.isFlipX()){ // 오른쪽을 목표, 왼쪽을 바라봄
                region.flip(true, false);
            }
        }

        stateTimer = (currentState == preState) ? stateTimer + dt : 0;
        preState = currentState;

        return region;
    }

    public State getState() {
        if (currentState == State.RUN || currentState == State.CHASE) {
            return State.RUN;
        } else if(currentState == State.CLIMING){
            return State.CLIMING;
        } else {
            return State.STAND;
        }
    }
    public void setState(State state){
        currentState = state;
    }
    public void setFacingRight(boolean set){facingRight = set;}
    public float getSpriteCenter(){
        return enemy.body.getPosition().y + (getHeight()/2);
    }
}
