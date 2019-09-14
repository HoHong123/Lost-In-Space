package com.lsgdx.game.Character.Enemy;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.lsgdx.game.LostInSpace;
import com.lsgdx.game.Screen.Level_One;

public class Enemy {
    public World world;
    public Body body;
    public Fixture fixture;
    public Level_One screen;
    public Fixture sensorFixture;

    private Body playerSensor;

    private EnemyAI enemyAI;
    public EnemyAI getEnemyAI() {
        return enemyAI;
    }

    private EnemySprite enemySprite;
    public EnemySprite getEnemySpriteInfo() {return enemySprite;}


    public Enemy(World world, Level_One screen){
        this.screen = screen;
        this.world = world;

        setBody();

        enemySprite = new EnemySprite(this);
        enemyAI = new EnemyAI(this);
    }

    private void setBody(){
        // Box2D body defined
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        body = world.createBody(bodyDef);
        body.setAwake(true);
        body.setGravityScale(0);

        FixtureDef fixtureDef = new FixtureDef();
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(21 / LostInSpace.PPM, 12 / LostInSpace.PPM);
        fixtureDef.filter.categoryBits = LostInSpace.ENEMY_BIT;
        fixtureDef.shape = shape;
        fixtureDef.isSensor = true;

        fixture = body.createFixture(fixtureDef);
        fixture.setUserData(this);

        // Box2D Raider defined
        BodyDef bodyDef2 = new BodyDef();
        bodyDef2.type = BodyDef.BodyType.KinematicBody;
        playerSensor = world.createBody(bodyDef2);
        playerSensor.setAwake(true);
        playerSensor.setGravityScale(0);

        FixtureDef fixtureDef2 = new FixtureDef();
        PolygonShape shape2 = new PolygonShape();
        shape2.setAsBox(75 / LostInSpace.PPM, 24 / LostInSpace.PPM);
        fixtureDef2.filter.categoryBits = LostInSpace.ENEMYRAIDER_BIT;
        fixtureDef2.shape = shape2;
        fixtureDef2.isSensor = true;

        sensorFixture = playerSensor.createFixture(fixtureDef2);
        sensorFixture.setUserData(this);
    }

    public void update(float dt){
        enemySprite.update(dt);
        enemyAI.update(dt);
        playerSensor.setAwake(true);
        playerSensor.setTransform(body.getPosition(), 0);
    }

    public void setBodyPosition(float x, float y){
        body.setTransform(x, y + (enemySprite.getHeight()/2), 0);
    }

    public void moveBody(float x, float y){
        float moveX = (body.getPosition().x + x);
        float moveY = (body.getPosition().y + y);
        body.setAwake(true);
        body.setTransform(moveX, moveY, 0);
    }

    public void detectPlayer(){
        enemyAI.setPlayerFound(true);
        enemyAI.setPlayerInRange(true);
        enemyAI.switchAI(EnemyAI.State.RUN);
    }
}
