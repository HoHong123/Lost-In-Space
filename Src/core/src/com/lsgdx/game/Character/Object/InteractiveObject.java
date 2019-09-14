package com.lsgdx.game.Character.Object;

import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.lsgdx.game.LostInSpace;

public abstract class InteractiveObject {
    protected World world;
    protected TiledMap map;
    protected TiledMapTile tile;
    protected Rectangle bounds;
    protected Body body;

    protected Fixture fixture;

    public void setSensor(boolean isSensor){
        fixture.setSensor(isSensor);
    }
    public Boolean getSensor(){
        return fixture.isSensor();
    }

    public InteractiveObject(World world, TiledMap map, Rectangle bounds, boolean isSensor){
        this.world = world;
        this.map = map;
        this.bounds = bounds;

        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.KinematicBody;
        bodyDef.position.set((bounds.getX() + bounds.getWidth() / 2) / LostInSpace.PPM, (bounds.getY() + bounds.getHeight() / 2) / LostInSpace.PPM);
        body = world.createBody(bodyDef);

        FixtureDef fixtureDef = new FixtureDef();
        PolygonShape polygonShape = new PolygonShape();
        polygonShape.setAsBox(bounds.getWidth() / 2 / LostInSpace.PPM, bounds.getHeight() / 2 / LostInSpace.PPM);
        fixtureDef.shape = polygonShape;
        fixtureDef.isSensor = isSensor;

        fixture = body.createFixture(fixtureDef);
    }

    // What does the object do when it collide with player?
    public abstract void collideWithPlayer();

    public void setCategoryFilter(short filterBit){
        Filter filter = new Filter();
        filter.categoryBits = filterBit;
        fixture.setFilterData(filter);
    }

    public TiledMapTileLayer.Cell getCell() {
        TiledMapTileLayer layer = (TiledMapTileLayer) map.getLayers().get(1);
        return layer.getCell((int)(body.getPosition().x * LostInSpace.PPM / bounds.getWidth()),
                (int)(body.getPosition().y * LostInSpace.PPM / bounds.getHeight()));
    }
}
