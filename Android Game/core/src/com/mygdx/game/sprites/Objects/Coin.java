package com.mygdx.game.sprites.Objects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.tiled.TiledMapTileSet;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.GameTest;
import com.mygdx.game.Items.ItemDef;
import com.mygdx.game.Items.Mushroom;
import com.mygdx.game.scenes.Hud;
import com.mygdx.game.screens.PlayScreen;
import com.mygdx.game.sprites.Player.Mario;

/**
 * Created by Terry on 10/11/2016.
 */

public class Coin extends InteractiveTileObject {
    private static TiledMapTileSet tileSet;
    private final int BLANK_COIN = 28;

    public Coin(PlayScreen screen, MapObject object){
        super(screen, object);
        tileSet = map.getTileSets().getTileSet("tileset_gutter");
        fixture.setUserData(this);
        setCategoryFilter(GameTest.COIN_BIT);

    }

    @Override
    public void onHeadHit(com.mygdx.game.sprites.Player.Mario mario) {
        Gdx.app.log("Coin", "Collision");
        if(getCell().getTile().getId() == BLANK_COIN) {
            GameTest.manager.get("audio/sounds/bump.wav", Sound.class).play();
        }else {
            if(object.getProperties().containsKey("mushroom")) {
                screen.spawnItem(new ItemDef(new Vector2(body.getPosition().x, body.getPosition().y + 16 / GameTest.PPM),
                        Mushroom.class));
                GameTest.manager.get("audio/sounds/powerup_spawn.wav", Sound.class).play();
            }
            else
                GameTest.manager.get("audio/sounds/coin.wav", Sound.class).play();
                Hud.addScore(200);
        }


        getCell().setTile(tileSet.getTile(BLANK_COIN));

    }

    @Override
    public void areaEffect(Mario mario) {

    }

}
