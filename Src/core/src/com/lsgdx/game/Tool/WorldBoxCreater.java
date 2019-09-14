package com.lsgdx.game.Tool;

import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.lsgdx.game.Character.Object.Door;
import com.lsgdx.game.Character.Object.Ladder;
import com.lsgdx.game.LostInSpace;
import com.lsgdx.game.Screen.Level_One;

public class WorldBoxCreater {
    public WorldBoxCreater(Level_One screen){
        World world = screen.getWorld();
        TiledMap map = screen.getMap();

        BodyDef bdef = new BodyDef();
        PolygonShape shape = new PolygonShape();
        FixtureDef fdef = new FixtureDef();
        Body body;

        //create ground bodies/fixtures
        for(MapObject object : map.getLayers().get(4).getObjects().getByType(RectangleMapObject.class)){
            Rectangle rect = ((RectangleMapObject) object).getRectangle();

            bdef.type = BodyDef.BodyType.StaticBody;
            bdef.position.set((rect.getX() + rect.getWidth() / 2) / LostInSpace.PPM, (rect.getY() + rect.getHeight() / 2) / LostInSpace.PPM);

            body = world.createBody(bdef);

            shape.setAsBox(rect.getWidth() / 2 / LostInSpace.PPM, rect.getHeight() / 2 / LostInSpace.PPM);
            fdef.filter.categoryBits = LostInSpace.GROUND_BIT;

            fdef.shape = shape;
            body.createFixture(fdef).setUserData("Ground");
        }

        //create Ladder bodies/fixtures
        for(MapObject object : map.getLayers().get(5).getObjects().getByType(RectangleMapObject.class)){
            Rectangle rect = ((RectangleMapObject) object).getRectangle();
            new Ladder(world, map, rect, true);
        }

        // create Doors bodies/fixtures
        for(MapObject object : map.getLayers().get(6).getObjects().getByType(RectangleMapObject.class)){
            Rectangle rect = ((RectangleMapObject) object).getRectangle();
            new Door(world, map, rect, false);
        }
    }
}
