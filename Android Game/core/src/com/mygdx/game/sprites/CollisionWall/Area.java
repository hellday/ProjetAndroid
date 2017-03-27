package com.mygdx.game.sprites.CollisionWall;

import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTileSet;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.mygdx.game.GameTest;
import com.mygdx.game.screens.PlayScreen;
import com.mygdx.game.sprites.Objects.InteractiveTileObject;
import com.mygdx.game.sprites.Player.Player;

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
    public void onHeadHit(Player player) {

    }


    public void areaEffect(Player player){ //Si le Player touche une Area of Effect
        if(object.getProperties().containsKey("endLevel")) {
            System.out.println("Fin du level");
            setCategoryFilter(GameTest.DESTROYED_BIT);
            PlayScreen.setEndLevel();
        }

        if(object.getProperties().containsKey("startBossFight")) {

            System.out.println("Start Boss Fight");
            setCategoryFilter(GameTest.DESTROYED_BIT);

            TiledMapTileLayer.Cell cell = new TiledMapTileLayer.Cell();
            cell.setTile(tileSet.getTile(910));
            layer.setCell(99, 6, cell);
            cell.setTile(tileSet.getTile(910));
            layer.setCell(99, 7, cell);
            cell.setTile(tileSet.getTile(910));
            layer.setCell(99, 8, cell);
            cell.setTile(tileSet.getTile(910));
            layer.setCell(99, 9, cell);

            cell.setTile(tileSet.getTile(910));
            layer.setCell(100, 6, cell);
            cell.setTile(tileSet.getTile(910));
            layer.setCell(100, 7, cell);
            cell.setTile(tileSet.getTile(910));
            layer.setCell(100, 8, cell);
            cell.setTile(tileSet.getTile(910));
            layer.setCell(100, 9, cell);

            PlayScreen.cameraChangeBoss(true);
            setCategoryFilterFixture(GameTest.GROUND_BIT, PlayScreen.getFixtureStartBoss());

        }

        if(object.getProperties().containsKey("blueKnight")) {
            System.out.println("Changement en bleu");
            setCategoryFilter(GameTest.DESTROYED_BIT);
            getCell().setTile(null);
            PlayScreen.setColorKnight("blue");
        }

        if(object.getProperties().containsKey("greyKnight")) {
            System.out.println("Changement en gris");
            setCategoryFilter(GameTest.DESTROYED_BIT);
            getCell().setTile(null);
            PlayScreen.setColorKnight("grey");
        }

        if(object.getProperties().containsKey("redKnight")) {
            System.out.println("Changement en rouge");
            setCategoryFilter(GameTest.DESTROYED_BIT);
            getCell().setTile(null);
            PlayScreen.setColorKnight("red");
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


