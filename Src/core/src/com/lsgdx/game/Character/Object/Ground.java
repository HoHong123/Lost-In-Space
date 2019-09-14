package com.lsgdx.game.Character.Object;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.World;
import com.lsgdx.game.LostInSpace;


public class Ground extends  InteractiveObject {
    public Ground(World world, TiledMap map, Rectangle bounds) {
        super(world, map, bounds, false);
        fixture.setUserData(this);
        setCategoryFilter(LostInSpace.GROUND_BIT);
    }

    @Override
    public void collideWithPlayer() {
        Gdx.app.log("Ground", "Collision Enter");
    }
}