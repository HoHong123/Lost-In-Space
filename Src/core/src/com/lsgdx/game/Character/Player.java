package com.lsgdx.game.Character;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.lsgdx.game.Character.Object.Ladder;
import com.lsgdx.game.LostInSpace;
import com.lsgdx.game.Screen.Level_One;


public class Player extends Sprite {
    public enum State { STAND, RUN, HIDE, CLIMING };

    public State currentState;
    public State preState;

    public World world;
    public Body body;
    public Fixture fixture;

    private float stateTimer;
    private float ladderXPos;

    private float oxygen;
    public float getOxygen() {
        return oxygen;
    }
    public void addOxygen() {
        oxygen += 30;

        if(oxygen > 100){
            oxygen = 100;
        }
    }

    private boolean onLadder, onHideSpot;
    private boolean isHidding, isCliming;
    private boolean onGround;
    private boolean facingRight;
    private boolean flashLight;
    public boolean isFlashLight() {
        return flashLight;
    }

    private TextureRegion playerHide;
    private Animation<TextureRegion> animIdle;
    private Animation<TextureRegion> animRun;
    private Animation<TextureRegion> animHide;

    private Sound beep;
    private float beepTimer = 0;

    private Level_One screen;
    private Ladder currentLadder;



    public Player(World world, Level_One screen){
        super(screen.getAtlas().findRegion("playerHide"));
        this.screen = screen;
        this.world = world;

        currentState = preState = State.STAND;
        stateTimer = 0;
        facingRight = true;
        isHidding = isCliming = false;
        onHideSpot = onLadder = false;
        oxygen = 15;

        playerHide = this.screen.getAtlas().findRegion("playerHide");

        // Setup the animation array
        Array<TextureRegion> frames = new Array<TextureRegion>();
        for(int i = 0; i < 5; i++){
            frames.add(new TextureRegion(getTexture(), i * 16 + 2, 28, 16,32));
        }
        animIdle = new Animation(0.3f, frames);
        frames.clear();

        for(int i = 0; i < 3; i++){
            frames.add(new TextureRegion(getTexture(), i * 16 + 2, 62, 16,32));
        }
        animRun = new Animation(0.2f, frames);
        frames.clear();

        frames.add(new TextureRegion(getTexture(), 134, 70, 16, 32));
        animHide = new Animation(1f,frames);
        frames.clear();

        setPlayer();

        beep = Gdx.audio.newSound(Gdx.files.internal("Sounds/beep.wav"));
    }

    private void setPlayer(){
        // Box2D body defined
        BodyDef bodyDef = new BodyDef();
        bodyDef.position.set(60 / LostInSpace.PPM, 30  / LostInSpace.PPM);
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        body = world.createBody(bodyDef);

        FixtureDef fixtureDef = new FixtureDef();
        CircleShape shape = new CircleShape();
        shape.setRadius(6 / LostInSpace.PPM);
        fixtureDef.shape = shape;
        fixtureDef.filter.categoryBits = LostInSpace.PLAYER_BIT;
        fixtureDef.filter.maskBits = LostInSpace.DOOR_BIT |
                LostInSpace.LADDER_BIT |
                LostInSpace.CARD_BIT |
                LostInSpace.GROUND_BIT |
                LostInSpace.ENEMYRAIDER_BIT |
                LostInSpace.ENEMY_BIT;

        fixture = body.createFixture(fixtureDef);
        fixture.setUserData(this);

        // Define Sprite width and height
        setBounds(2, 28, 16 / LostInSpace.PPM, 32 / LostInSpace.PPM);
        setRegion(playerHide);
    }

    public void update(float dt){
        if(oxygen < 0){
            isDead();
        }
        oxygen -= dt * 2f;

        setPosition(body.getPosition().x - (getWidth()/2), body.getPosition().y - (getHeight()/2) + 0.1f);
        setRegion(getFrame(dt));
    }

    private TextureRegion getFrame(float dt){
        currentState = getState();

        TextureRegion region = new TextureRegion();
        switch (currentState){
            case CLIMING:
            case RUN :
                region = animRun.getKeyFrame(stateTimer, true);
                break;
            case HIDE:
                region = animHide.getKeyFrame(stateTimer, false);
                break;
            default:
                region = animIdle.getKeyFrame(stateTimer, true);
                break;
        }

        // Heading left, when velocity is negative or isn't facing left and X axis of region is not flipped
        // isFlipX == True : facing left, isFlipX == false : facing right
        if(currentState != State.CLIMING){
            if((body.getLinearVelocity().x < 0 || !facingRight) && !region.isFlipX()){
                region.flip(true, false);
                facingRight = false;
            } else if((body.getLinearVelocity().x > 0 || facingRight) && region.isFlipX()){
                region.flip(true, false);
                facingRight = true;
            }
        }

        stateTimer = (currentState == preState) ? stateTimer + dt : 0;
        preState = currentState;

        return region;
    }

    public State getState() {
        if (body.getLinearVelocity().x != 0) {
            return State.RUN;
        } else if(isHidding){
            return State.HIDE;
        } else {
            return State.STAND;
        }
    }

    public boolean getOnHideSpot(){
        return onHideSpot;
    }
    public void setOnHidSpot(boolean set){
        onHideSpot = set;
    }

    public boolean getInHideSpot(){
        return isHidding;
    }
    public void setInHidSpot(boolean set){
        isHidding = set;
    }

    public void setCurrentLadder(Ladder currentLadder) {
        this.currentLadder = currentLadder;
        ladderXPos = (currentLadder != null) ? currentLadder.getxPos() : 0;
    }
    public Ladder getCurrentLadder(){
        return currentLadder;
    }

    public boolean getOnLadder(){
        return onLadder;
    }
    public void setOnLadder(boolean set){
        onLadder = set;
    }

    public boolean getIsClimbing(){
        return isCliming;
    }
    public void setIsClimbing(boolean set){
        isCliming = set;
        if(set && currentLadder != null) {
            currentLadder.setSensor(set);
            body.setGravityScale(0);
            return;
        }

        body.setGravityScale(9.8f);
    }

    public boolean getOnGround(){
        return onGround;
    }
    public void setOnGround(boolean set){
        onGround = set;
    }

    public float getLadderXPos(){
        return ladderXPos;
    }

    public float distanceCalculator(Body enemy){
        return Vector2.dst(body.getPosition().x, body.getPosition().y, enemy.getPosition().x, enemy.getPosition().y);
    }

    public void playBeep(float dt, float dis){
        if(beepTimer > 0){
            beepTimer -= dt;
            return;
        }
        beep.play(LostInSpace.EFFECT_VOLUM);
        beepTimer = (dis > 0.2f) ? dis : 1.5f;
    }

    public void isDead(){
        screen.GameOver();
    }

    public void victory(){
        screen.Victory();
    }

    public void Dispose(){
        beep.dispose();
    }

    public void reset(){
        oxygen = 100;
        body.setTransform(60 / LostInSpace.PPM, 30  / LostInSpace.PPM,0);
    }
}
