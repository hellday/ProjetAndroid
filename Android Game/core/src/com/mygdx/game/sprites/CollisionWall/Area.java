package com.mygdx.game.sprites.CollisionWall;

import com.badlogic.gdx.graphics.Cursor;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTileSet;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.utils.Timer;
import com.mygdx.game.GameTest;
import com.mygdx.game.screens.PlayScreen;
import com.mygdx.game.sprites.Objects.InteractiveTileObject;
import com.mygdx.game.sprites.Player.Mario;

/**
 * Created by Terry on 19/11/2016.
 */

public class Area extends InteractiveTileObject{
    private static TiledMapTileSet tileSet;
    private TiledMapTileLayer layer;
    private MapObject test;
    private boolean can;
    private Fixture fixt;

    public Area(PlayScreen screen, MapObject object){
        super(screen, object);
        layer = (TiledMapTileLayer)map.getLayers().get(1);
        tileSet = map.getTileSets().getTileSet("Tileset");
        fixture.setUserData(this);

        can = false;

        if(object.getProperties().containsKey("blockBoss")) {
            setCategoryFilter(GameTest.DESTROYED_BIT);
            fixt = fixture;
            PlayScreen.setFixture(fixt);

//            Timer.schedule(new Timer.Task(){
//                               @Override
//                               public void run() {
//                                   setTest();
//                               }
//                           }
//                    , 1        //    (delay)
//                    , 1     //    (seconds)
//            );

//            Timer.schedule(new Timer.Task(){
//                @Override
//                public void run() {
//                    setCategoryFilter(GameTest.GROUND_BIT);
//                    System.out.println("FIXED");
//
//                }
//            }, 5);


        }else setCategoryFilter(GameTest.AREA_BIT);




    }

    @Override
    public void onHeadHit(com.mygdx.game.sprites.Player.Mario mario) {

    }


    public void areaEffect(Mario mario){ //Si le Player touche une Area of Effect
        if(object.getProperties().containsKey("endLevel")) {
            System.out.println("Fin du level");
            setCategoryFilter(GameTest.DESTROYED_BIT);
            PlayScreen.setEndLevel();

        }

        if(object.getProperties().containsKey("startBossFight")) {

            System.out.println("Start Boss Fight : " + fixture + fixt);
            setCategoryFilter(GameTest.DESTROYED_BIT);
            //getCell().setTile(tileSet.getTile(303));

            TiledMapTileLayer.Cell cell = new TiledMapTileLayer.Cell();
            cell.setTile(tileSet.getTile(1073));
            layer.setCell(16, 7, cell);
            cell.setTile(tileSet.getTile(990));
            layer.setCell(16, 8, cell);
            cell.setTile(tileSet.getTile(1073));
            layer.setCell(16, 9, cell);
            cell.setTile(tileSet.getTile(991));
            layer.setCell(16, 10, cell);

            cell.setTile(tileSet.getTile(1074));
            layer.setCell(17, 7, cell);
            cell.setTile(tileSet.getTile(990));
            layer.setCell(17, 8, cell);
            cell.setTile(tileSet.getTile(1074));
            layer.setCell(17, 9, cell);
            cell.setTile(tileSet.getTile(825));
            layer.setCell(17, 10, cell);

            PlayScreen.cameraChangeBoss(true);
            setCategoryFilter2(GameTest.GROUND_BIT, PlayScreen.getFixture());

        }

        if(object.getProperties().containsKey("blockBoss")) {
            //can = true;
        }

    }

}


