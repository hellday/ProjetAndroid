package com.mygdx.game.sprites.CollisionWall;

import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.tiled.TiledMapTileSet;
import com.mygdx.game.GameTest;
import com.mygdx.game.screens.PlayScreen;
import com.mygdx.game.sprites.Objects.InteractiveTileObject;
import com.mygdx.game.sprites.Player.Mario;

/**
 * Created by Terry on 18/11/2016.
 */

public class DeadZone extends InteractiveTileObject {
    private static TiledMapTileSet tileSet;

    public DeadZone(PlayScreen screen, MapObject object){
        super(screen, object);
        tileSet = map.getTileSets().getTileSet("tileset_gutter");
        fixture.setUserData(this);
        setCategoryFilter(GameTest.DEAD_ZONE_BIT);

    }

    @Override
    public void onHeadHit(com.mygdx.game.sprites.Player.Mario mario) {

    }

    @Override
    public void areaEffect(Mario mario) {

    }

}
