package com.mygdx.game.sprites.Objects;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.maps.MapObject;
import com.mygdx.game.GameTest;
import com.mygdx.game.scenes.Hud;
import com.mygdx.game.screens.PlayScreen;
import com.mygdx.game.sprites.Player.Mario;

/**
 * Created by Terry on 10/11/2016.
 */

public class Brick extends InteractiveTileObject {
    public Brick(PlayScreen screen, MapObject object){
        super(screen, object);
        fixture.setUserData(this);
        setCategoryFilter(GameTest.BRICK_BIT);

    }

    @Override
    public void onHeadHit(com.mygdx.game.sprites.Player.Mario mario) { //quand le joueur touche une brique avec sa tête
        if(mario.isBig()) {
            setCategoryFilter(GameTest.DESTROYED_BIT);
            getCell().setTile(null);
            Hud.addScore(100);
            GameTest.manager.get("audio/sounds/breakblock.wav", Sound.class).play();
        }else GameTest.manager.get("audio/sounds/bump.wav", Sound.class).play();
    }

    @Override
    public void areaEffect(Mario mario) {

    }

}
