package com.mygdx.game.Items;

import com.badlogic.gdx.math.Vector2;

/**
 * Created by Terry on 11/11/2016.
 */

/** Définition général des Item */
public class ItemDef {
    public Vector2 position;
    public Class<?> type;

    public ItemDef(Vector2 position, Class<?> type){
        this.position = position;
        this.type = type;

    }
}
