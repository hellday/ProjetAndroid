package com.mygdx.game.sprites.CollisionWall;

import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.tiled.TiledMapTileSet;
import com.mygdx.game.GameTest;
import com.mygdx.game.screens.PlayScreen;
import com.mygdx.game.sprites.Objects.InteractiveTileObject;
import com.mygdx.game.sprites.Player.Player;

/**
 * Created by Terry on 11/11/2016.
 */

/** Murs invisibles de collision pour les ennemies */
public class EnemyInvisibleWall extends InteractiveTileObject {
    private static TiledMapTileSet tileSet;

    public EnemyInvisibleWall(PlayScreen screen, MapObject object){
        super(screen, object);
        tileSet = map.getTileSets().getTileSet("tileset_gutter");
        fixture.setUserData(this);
        setCategoryFilter(GameTest.ENEMY_WALL_BIT);

    }

    @Override
    public void onHeadHit(Player player) {

    }

    @Override
    public void areaEffect(Player player) {

    }

}
