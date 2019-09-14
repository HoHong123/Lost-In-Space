package com.lsgdx.game.Character.Object;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.lsgdx.game.LostInSpace;
import com.lsgdx.game.Screen.Level_One;

import java.util.Random;

public class Oxygen {
    private World world;
    private Level_One level_one;

    public Body body;
    public Fixture fixture;

    private Array<Vector2> deploy;
    private int current = 11;

    private Sprite sprite;
    private Vector2 v;


    public Oxygen(World world, Level_One level_one){
        this.world = world;
        this.level_one = level_one;

        v = new Vector2();

        deploy = new Array<Vector2>();
        // SearchSpot 위치 받기
        for(MapObject object : level_one.getMap().getLayers().get(10).getObjects().getByType(RectangleMapObject.class)){
            Vector2 v = new Vector2(((RectangleMapObject) object).getRectangle().getX(), ((RectangleMapObject) object).getRectangle().getY());
            deploy.add(v);
        }

        sprite = new Sprite(new Texture("Sprites/Input/oxygen.png"));
        setBody();
    }

    public Sprite getSprite(){

        return sprite;
    }

    private void setBody(){
        // Box2D body defined
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.KinematicBody;
        body = world.createBody(bodyDef);
        body.setGravityScale(0);
        body.setAwake(true);

        FixtureDef fixtureDef = new FixtureDef();
        CircleShape shape = new CircleShape();
        shape.setRadius(6 / LostInSpace.PPM);
        fixtureDef.shape = shape;
        fixtureDef.filter.categoryBits = LostInSpace.OXYGEN_BIT;
        fixtureDef.isSensor = true;

        fixture = body.createFixture(fixtureDef);
        fixture.setUserData(this);

        // Define Sprite width and height
        sprite.setBounds(0, 0, 24 / LostInSpace.PPM, 24 / LostInSpace.PPM);
        reset();
    }

    public void update(){
        body.setTransform( new Vector2(v.x / LostInSpace.PPM, (v.y + level_one.getTilePixelHeight()) / LostInSpace.PPM), 0);
        sprite.setPosition((body.getPosition().x - (sprite.getWidth()/2)), (body.getPosition().y - (sprite.getHeight()/2)));
    }

    public void rePosition(){
        v = deploy.get(level_one.random.nextInt(deploy.size));
    }

    public void reset(){
        v = deploy.get(11);
    }
}
