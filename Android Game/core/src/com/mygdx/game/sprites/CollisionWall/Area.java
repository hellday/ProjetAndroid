package com.mygdx.game.sprites.CollisionWall;

import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.tiled.TiledMapTileSet;
import com.mygdx.game.GameTest;
import com.mygdx.game.screens.PlayScreen;
import com.mygdx.game.sprites.Objects.InteractiveTileObject;
import com.mygdx.game.sprites.Player.Mario;

/**
 * Created by Terry on 19/11/2016.
 */

public class Area extends InteractiveTileObject {
    private static TiledMapTileSet tileSet;

    public Area(PlayScreen screen, MapObject object){
        super(screen, object);
        tileSet = map.getTileSets().getTileSet("tileset_gutter");
        fixture.setUserData(this);
        setCategoryFilter(GameTest.AREA_BIT);

    }

    @Override
    public void onHeadHit(com.mygdx.game.sprites.Player.Mario mario) {

    }

    public void areaEffect(Mario mario){ //Si Mario touche une Area of Effect
        if(object.getProperties().containsKey("endLevel")) {
            System.out.println("Fin du level");
            setCategoryFilter(GameTest.DESTROYED_BIT);
            PlayScreen.setEndLevel();

        }
        }





    }


