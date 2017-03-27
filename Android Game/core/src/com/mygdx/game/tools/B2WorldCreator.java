package com.mygdx.game.tools;

import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.mygdx.game.GameTest;
import com.mygdx.game.screens.PlayScreen;
import com.mygdx.game.sprites.CollisionWall.Area;
import com.mygdx.game.sprites.CollisionWall.DeadZone;
import com.mygdx.game.sprites.Enemies.Boss;
import com.mygdx.game.sprites.Enemies.Ghost;
import com.mygdx.game.sprites.Objects.Brick;
import com.mygdx.game.sprites.Objects.Coin;
import com.mygdx.game.sprites.Enemies.Enemy;
import com.mygdx.game.sprites.CollisionWall.EnemyInvisibleWall;

/**
 * Created by Terry on 10/11/2016.
 */

public class B2WorldCreator {
    private static Array<Ghost> ghosts;
    private static Array<Boss> boss;

    private Vector2 startPosition;

    public B2WorldCreator(PlayScreen screen){
        World world = screen.getWorld();
        TiledMap map = screen.getMap();
        //Création des body et fixtures
        BodyDef bdef = new BodyDef();
        PolygonShape shape = new PolygonShape();
        FixtureDef fdef = new FixtureDef();
        Body body;

        //Création du Ground et des bodies/fixtures
        for(MapObject object : map.getLayers().get(2).getObjects().getByType(RectangleMapObject.class)){
            Rectangle rect = ((RectangleMapObject) object).getRectangle();

            bdef.type = BodyDef.BodyType.StaticBody;
            bdef.position.set((rect.getX() + rect.getWidth() / 2) / GameTest.PPM, (rect.getY() + rect.getHeight()/2)/ GameTest.PPM);

            body = world.createBody(bdef);
            shape.setAsBox(rect.getWidth() / 2 / GameTest.PPM, rect.getHeight() / 2 / GameTest.PPM);
            fdef.shape = shape;
            body.createFixture(fdef);
        }

        //Création des Tubes et des bodies/fixtures
        for(MapObject object : map.getLayers().get(3).getObjects().getByType(RectangleMapObject.class)){
            Rectangle rect = ((RectangleMapObject) object).getRectangle();

            bdef.type = BodyDef.BodyType.StaticBody;
            bdef.position.set((rect.getX() + rect.getWidth() / 2) / GameTest.PPM, (rect.getY() + rect.getHeight()/2)/ GameTest.PPM);

            body = world.createBody(bdef);
            shape.setAsBox(rect.getWidth() / 2 / GameTest.PPM, rect.getHeight() / 2 / GameTest.PPM);
            fdef.shape = shape;
            fdef.filter.categoryBits = GameTest.OBJECT_BIT;
            body.createFixture(fdef);
        }

        //Création des Briques et des bodies/fixtures
        for(MapObject object : map.getLayers().get(5).getObjects().getByType(RectangleMapObject.class)){

            new Brick(screen, object);
        }

        //Création des Pieces et des bodies/fixtures
        for(MapObject object : map.getLayers().get(4).getObjects().getByType(RectangleMapObject.class)){

            new Coin(screen, object);
        }

        //Création des Ghost
        ghosts = new Array<Ghost>();

        for(MapObject object : map.getLayers().get(6).getObjects().getByType(RectangleMapObject.class)){
            Rectangle rect = ((RectangleMapObject) object).getRectangle();
            ghosts.add(new Ghost(screen, rect.getX() / GameTest.PPM, rect.getY() / GameTest.PPM));
        }


        //Création des Murs invisibles
        for(MapObject object : map.getLayers().get(7).getObjects().getByType(RectangleMapObject.class)){

            new EnemyInvisibleWall(screen, object);
        }

        //Création des Dead Zone
        for(MapObject object : map.getLayers().get(8).getObjects().getByType(RectangleMapObject.class)){

            new DeadZone(screen, object);
        }

        //Création des Area
        for(MapObject object : map.getLayers().get(9).getObjects().getByType(RectangleMapObject.class)){

            new Area(screen, object);
        }

        //Création du Boss
        boss = new Array<Boss>();

        for(MapObject object : map.getLayers().get(10).getObjects().getByType(RectangleMapObject.class)){
            Rectangle rect = ((RectangleMapObject) object).getRectangle();
            boss.add(new Boss(screen, rect.getX() / GameTest.PPM, rect.getY() / GameTest.PPM));
        }


        startPosition = new Vector2(64.0f, 64.0f);

    }

    public Array<Ghost> getGhosts() {
        return ghosts;
    }


    public  Array<Enemy> getEnemies(){
        Array<Enemy> enemies = new Array<Enemy>();
        enemies.addAll(ghosts);
        enemies.addAll(boss);
        return enemies;
    }

    public static void removeGhost(Ghost ghost){
        ghosts.removeValue(ghost, true);
    }
    public static void removeBoss(Boss bosse){
        boss.removeValue(bosse, true);
    }

    public Vector2 getStartPosition() {
        return startPosition;
    }


}
