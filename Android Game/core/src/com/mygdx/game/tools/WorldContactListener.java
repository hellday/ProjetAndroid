package com.mygdx.game.tools;

import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.mygdx.game.GameTest;
import com.mygdx.game.Items.Item;
import com.mygdx.game.sprites.Enemies.Enemy;
import com.mygdx.game.sprites.Objects.InteractiveTileObject;
import com.mygdx.game.sprites.Player.Mario;
import com.mygdx.game.sprites.Other.FireBall;

/**
 * Created by Terry on 10/11/2016.
 */

public class WorldContactListener implements ContactListener {
    private int numFootContacts;

    @Override
    public void beginContact(Contact contact) {
        Fixture fixA = contact.getFixtureA();
        Fixture fixB = contact.getFixtureB();

        int cDef = fixA.getFilterData().categoryBits | fixB.getFilterData().categoryBits;

        switch (cDef){
            case GameTest.MARIO_HEAD_BIT | GameTest.BRICK_BIT:
            case GameTest.MARIO_HEAD_BIT | GameTest.COIN_BIT:
                if(fixA.getFilterData().categoryBits == GameTest.MARIO_HEAD_BIT)
                    ((InteractiveTileObject) fixB.getUserData()).onHeadHit((Mario) fixA.getUserData());
                else
                    ((InteractiveTileObject) fixA.getUserData()).onHeadHit((Mario) fixB.getUserData());
                break;
            case GameTest.ENEMY_HEAD_BIT | GameTest.MARIO_FOOT_BIT:
                if(fixA.getFilterData().categoryBits == GameTest.ENEMY_HEAD_BIT)
                    ((Enemy)fixA.getUserData()).hitOnHead((Mario) fixB.getUserData());
                else
                    ((Enemy)fixB.getUserData()).hitOnHead((Mario) fixA.getUserData());
                break;
            case GameTest.ENEMY_BIT | GameTest.OBJECT_BIT:
                if(fixA.getFilterData().categoryBits == GameTest.ENEMY_BIT)
                    ((Enemy)fixA.getUserData()).reverseVelocity(true, false);
                else
                    ((Enemy)fixB.getUserData()).reverseVelocity(true, false);
                break;
            case GameTest.ENEMY_BIT | GameTest.GROUND_BIT:
                if(fixA.getFilterData().categoryBits == GameTest.ENEMY_BIT)
                    ((Enemy)fixA.getUserData()).reverseVelocity(true, false);
                else
                    ((Enemy)fixB.getUserData()).reverseVelocity(true, false);
                break;
            case GameTest.MARIO_BIT | GameTest.ENEMY_BIT:
                if(fixA.getFilterData().categoryBits == GameTest.MARIO_BIT)
                    ((Mario) fixA.getUserData()).hit((Enemy)fixB.getUserData());
                else
                    ((Mario) fixB.getUserData()).hit((Enemy)fixA.getUserData());
                break;
            case GameTest.ENEMY_BIT | GameTest.ENEMY_BIT:
                ((Enemy)fixA.getUserData()).onEnemyHit((Enemy)fixB.getUserData());
                ((Enemy)fixB.getUserData()).onEnemyHit((Enemy)fixA.getUserData());
                break;
            case GameTest.ENEMY_BIT | GameTest.ENEMY_WALL_BIT: //Collision Ennemies et Mur Invisible
                if(fixA.getFilterData().categoryBits == GameTest.ENEMY_BIT)
                    ((Enemy)fixA.getUserData()).reverseVelocity(true, false);
                else
                    ((Enemy)fixB.getUserData()).reverseVelocity(true, false);
                break;
            case GameTest.ITEM_BIT | GameTest.OBJECT_BIT:
                if(fixA.getFilterData().categoryBits == GameTest.ITEM_BIT)
                    ((Item)fixA.getUserData()).reverseVelocity(true, false);
                else
                    ((Item)fixB.getUserData()).reverseVelocity(true, false);
                break;
            case GameTest.ITEM_BIT | GameTest.MARIO_BIT: //Collision Mario et Item
                if(fixA.getFilterData().categoryBits == GameTest.ITEM_BIT)
                    ((Item)fixA.getUserData()).use((Mario) fixB.getUserData());
                else
                    ((Item)fixB.getUserData()).use((Mario) fixA.getUserData());
                break;
            case GameTest.FIREBALL_BIT | GameTest.OBJECT_BIT:
                if(fixA.getFilterData().categoryBits == GameTest.FIREBALL_BIT)
                    ((FireBall)fixA.getUserData()).setToDestroy();
                else
                    ((FireBall)fixB.getUserData()).setToDestroy();
                break;
            case GameTest.FIREBALL_BIT | GameTest.ENEMY_BIT:
                if(fixA.getFilterData().categoryBits == GameTest.FIREBALL_BIT)
                    ((FireBall)fixA.getUserData()).onFireBallHit((Enemy) fixB.getUserData(), fixB.getBody());
                else
                    ((FireBall)fixB.getUserData()).onFireBallHit((Enemy) fixA.getUserData(), fixA.getBody());
                break;
            case GameTest.MARIO_BIT | GameTest.DEAD_ZONE_BIT:
                if(fixA.getFilterData().categoryBits == GameTest.MARIO_BIT)
                    ((Mario) fixA.getUserData()).die();
                else
                    ((Mario) fixB.getUserData()).die();
                break;
            case GameTest.MARIO_BIT | GameTest.AREA_BIT:
                if(fixA.getFilterData().categoryBits == GameTest.MARIO_BIT)
                    ((InteractiveTileObject) fixB.getUserData()).areaEffect((Mario) fixA.getUserData());
                else
                    ((InteractiveTileObject) fixA.getUserData()).areaEffect((Mario) fixB.getUserData());
                break;
            case GameTest.MARIO_FOOT_BIT | GameTest.GROUND_BIT:
            case GameTest.MARIO_FOOT_BIT | GameTest.OBJECT_BIT:
            case GameTest.MARIO_FOOT_BIT | GameTest.BRICK_BIT:
            case GameTest.MARIO_FOOT_BIT | GameTest.COIN_BIT:
                if(fixA.getFilterData().categoryBits == GameTest.MARIO_FOOT_BIT)
                    numFootContacts++;
                else
                    numFootContacts++;
                break;


        }
    }

    @Override
    public void endContact(Contact contact) {

        Fixture fixA = contact.getFixtureA();
        Fixture fixB = contact.getFixtureB();

        int cDef = fixA.getFilterData().categoryBits | fixB.getFilterData().categoryBits;

        switch (cDef){
            case GameTest.MARIO_FOOT_BIT | GameTest.GROUND_BIT:
            case GameTest.MARIO_FOOT_BIT | GameTest.OBJECT_BIT:
            case GameTest.MARIO_FOOT_BIT | GameTest.BRICK_BIT:
            case GameTest.MARIO_FOOT_BIT | GameTest.COIN_BIT:
                if(fixA.getFilterData().categoryBits == GameTest.MARIO_FOOT_BIT)
                    numFootContacts--;
                else
                    numFootContacts--;
                break;
        }

    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {

    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {

    }

    public boolean isPlayerIsOnGround(){
        return numFootContacts > 0;
    }
}
