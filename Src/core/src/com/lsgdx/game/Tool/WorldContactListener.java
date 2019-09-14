package com.lsgdx.game.Tool;

import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.lsgdx.game.Character.Enemy.Enemy;
import com.lsgdx.game.Character.Object.InteractiveObject;
import com.lsgdx.game.Character.Object.Ladder;
import com.lsgdx.game.Character.Object.Oxygen;
import com.lsgdx.game.Character.Player;
import com.lsgdx.game.LostInSpace;

public class WorldContactListener implements ContactListener {
    Player player;
    Enemy enemy;

    public WorldContactListener(Player player, Enemy enemy){
        this.player = player;
        this.enemy = enemy;
    }

    @Override
    public void beginContact(Contact contact) {
        Fixture f1 = contact.getFixtureA();
        Fixture f2 = contact.getFixtureB();

        // get combined binary value
        int cDef = f1.getFilterData().categoryBits | f2.getFilterData().categoryBits;

        switch (cDef){
            case (LostInSpace.PLAYER_BIT | LostInSpace.LADDER_BIT) :
                if(f1.getFilterData().categoryBits == LostInSpace.LADDER_BIT){
                    ((Player) f2.getUserData()).setCurrentLadder(((Ladder) f1.getUserData())); // set the information of the current ladder
                    ((Player) f2.getUserData()).setOnLadder(true); // player is in the ladder and get the X position and top Y position of the ladder
                    ((Ladder) f1.getUserData()).playerPositionCheck(((Player) f2.getUserData()).body.getPosition().y); // Find out where does the collision start, above the ladder? or not
                } else if(f2.getFilterData().categoryBits == LostInSpace.LADDER_BIT) {
                    ((Player) f1.getUserData()).setCurrentLadder(((Ladder) f2.getUserData())); // set the information of the current ladder
                    ((Player) f1.getUserData()).setOnLadder(true); // player is in the ladder collider
                    ((Ladder) f2.getUserData()).playerPositionCheck(((Player) f1.getUserData()).body.getPosition().y); // Find out where does the collision start, above the ladder? or not
                }
                break;
            case (LostInSpace.PLAYER_BIT | LostInSpace.DOOR_BIT) :
                if(f1.getFilterData().categoryBits == LostInSpace.DOOR_BIT){
                } else if(f2.getFilterData().categoryBits == LostInSpace.DOOR_BIT) {
                }
                break;
            case (LostInSpace.PLAYER_BIT | LostInSpace.GROUND_BIT) :
                if(f1.getFilterData().categoryBits == LostInSpace.PLAYER_BIT){
                    ((Player) f1.getUserData()).setIsClimbing(false); // player is no longer in the ladder collider
                    ((Player) f1.getUserData()).setOnGround(true);
                } else if(f2.getFilterData().categoryBits == LostInSpace.PLAYER_BIT) {
                    ((Player) f2.getUserData()).setIsClimbing(false); // player is no longer in the ladder collider
                    ((Player) f2.getUserData()).setOnGround(true);
                }
                break;
            case (LostInSpace.PLAYER_BIT | LostInSpace.ENEMY_BIT) :
                if(f1.getFilterData().categoryBits == LostInSpace.PLAYER_BIT){
                    ((Player) f1.getUserData()).isDead();
                } else if(f2.getFilterData().categoryBits == LostInSpace.PLAYER_BIT) {
                    ((Player) f2.getUserData()).isDead();
                }
                break;
            case (LostInSpace.PLAYER_BIT | LostInSpace.ENEMYRAIDER_BIT) :
                if(f1.getFilterData().categoryBits == LostInSpace.ENEMYRAIDER_BIT){
                    ((Enemy) f1.getUserData()).detectPlayer();
                } else if(f2.getFilterData().categoryBits == LostInSpace.ENEMYRAIDER_BIT) {
                    ((Enemy) f2.getUserData()).detectPlayer();
                }
                break;
            case (LostInSpace.PLAYER_BIT | LostInSpace.OXYGEN_BIT) :
                if(f1.getFilterData().categoryBits == LostInSpace.PLAYER_BIT){
                    ((Player) f1.getUserData()).addOxygen();
                    ((Oxygen) f2.getUserData()).rePosition();
                } else if(f2.getFilterData().categoryBits == LostInSpace.PLAYER_BIT) {
                    ((Player) f2.getUserData()).addOxygen();
                    ((Oxygen) f1.getUserData()).rePosition();
                }
                break;
        }
    }

    @Override
    public void endContact(Contact contact) {
        Fixture f1 = contact.getFixtureA();
        Fixture f2 = contact.getFixtureB();

        // get combined binary value
        int cDef = f1.getFilterData().categoryBits | f2.getFilterData().categoryBits;

        switch (cDef){
            case (LostInSpace.PLAYER_BIT | LostInSpace.LADDER_BIT) :
                if(f1.getFilterData().categoryBits == LostInSpace.LADDER_BIT){
                    ((Player) f2.getUserData()).setCurrentLadder(null); // realize ladder
                    ((Player) f2.getUserData()).setIsClimbing(false); // player is no longer in the ladder collider
                    ((Player) f2.getUserData()).setOnLadder(false); // player is not using the ladder
                    ((Ladder) f1.getUserData()).setSensor(true); // ladder is not the sensor
                } else if(f2.getFilterData().categoryBits == LostInSpace.LADDER_BIT) {
                    ((Player) f1.getUserData()).setCurrentLadder(null); // realize ladder
                    ((Player) f1.getUserData()).setIsClimbing(false); // player is no longer in the ladder collider
                    ((Player) f1.getUserData()).setOnLadder(false); // player is not using the ladder
                    ((Ladder) f2.getUserData()).setSensor(true); // ladder is not the sensor
                }
                break;
            case (LostInSpace.PLAYER_BIT | LostInSpace.DOOR_BIT) :
                if(f1.getFilterData().categoryBits == LostInSpace.DOOR_BIT){
                    ((InteractiveObject) f1.getUserData()).collideWithPlayer();
                } else if(f2.getFilterData().categoryBits == LostInSpace.DOOR_BIT) {
                    ((InteractiveObject) f2.getUserData()).collideWithPlayer();
                }
                break;
            case (LostInSpace.PLAYER_BIT | LostInSpace.GROUND_BIT) :
                if(f1.getFilterData().categoryBits == LostInSpace.PLAYER_BIT){
                    ((Player) f1.getUserData()).setOnGround(false);
                } else if(f2.getFilterData().categoryBits == LostInSpace.PLAYER_BIT) {
                    ((Player) f2.getUserData()).setOnGround(false);
                }
                break;
            case (LostInSpace.PLAYER_BIT | LostInSpace.ENEMYRAIDER_BIT) :
                if(f1.getFilterData().categoryBits == LostInSpace.ENEMYRAIDER_BIT){
                    ((Enemy) f1.getUserData()).getEnemyAI().setPlayerInRange(false);
                } else if(f2.getFilterData().categoryBits == LostInSpace.ENEMYRAIDER_BIT) {
                    ((Enemy) f1.getUserData()).getEnemyAI().setPlayerInRange(false);
                }
                break;
        }
    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {

    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {

    }
}
