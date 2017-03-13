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
    private Fixture fixtureStartBoss, fixtureEndBoss;

    public Area(PlayScreen screen, MapObject object){
        super(screen, object);
        layer = (TiledMapTileLayer)map.getLayers().get(1);
        tileSet = map.getTileSets().getTileSet("Tileset");
        fixture.setUserData(this);

        if(object.getProperties().containsKey("blockBoss")) {
            setCategoryFilter(GameTest.DESTROYED_BIT);
            fixtureStartBoss = fixture;
            PlayScreen.setFixtureStartBoss(fixtureStartBoss);
        }else if(object.getProperties().containsKey("endBlockBoss")) {
            setCategoryFilter(GameTest.GROUND_BIT);
            fixtureEndBoss = fixture;
            PlayScreen.setFixtureEndBoss(fixtureEndBoss);
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

            System.out.println("Start Boss Fight");
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
            setCategoryFilterFixture(GameTest.GROUND_BIT, PlayScreen.getFixtureStartBoss());

        }

    }

    public void drawEndBossWall(){
        TiledMapTileLayer.Cell cell = new TiledMapTileLayer.Cell();
        cell.setTile(null);
        layer.setCell(47, 7, cell);
        cell.setTile(null);
        layer.setCell(47, 8, cell);
        cell.setTile(null);
        layer.setCell(47, 9, cell);
        cell.setTile(null);
        layer.setCell(47, 10, cell);

        cell.setTile(null);
        layer.setCell(48, 7, cell);
        cell.setTile(null);
        layer.setCell(48, 8, cell);
        cell.setTile(null);
        layer.setCell(48, 9, cell);
        cell.setTile(null);
        layer.setCell(48, 10, cell);
    }
}


