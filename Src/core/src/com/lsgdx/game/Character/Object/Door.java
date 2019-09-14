package com.lsgdx.game.Character.Object;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.World;
import com.lsgdx.game.LostInSpace;


public class Door extends InteractiveObject {
    public Door(World world, TiledMap map, Rectangle bounds, boolean isSensor) {
        super(world, map, bounds, isSensor);
        fixture.setUserData(this);
        setCategoryFilter(LostInSpace.DOOR_BIT);
    }

    @Override
    public void collideWithPlayer() {
        Gdx.app.log("Door", "Collision Enter");
    }
}
