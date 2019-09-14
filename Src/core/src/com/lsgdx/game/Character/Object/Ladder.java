package com.lsgdx.game.Character.Object;

import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.World;
import com.lsgdx.game.LostInSpace;


public class Ladder extends InteractiveObject {
    private float xPos;

    public float getxPos() {
        return xPos;
    }

    public Ladder(World world, TiledMap map, Rectangle bounds, boolean isSensor) {
        super(world, map, bounds, isSensor);

        xPos = (bounds.getX() + (bounds.getWidth() / 2)) / LostInSpace.PPM;

        fixture.setUserData(this);
        setCategoryFilter(LostInSpace.LADDER_BIT);
    }

    @Override
    public void collideWithPlayer() {
    }

    // When collide with player, Check if player is above the Ladder so it could be solid platform or not
    public void playerPositionCheck(float playerY){
        if(playerY > (bounds.getHeight() / LostInSpace.PPM)/2 + body.getPosition().y){
            setSensor(false);
        } else {
            setSensor(true);
        }
    }
}
