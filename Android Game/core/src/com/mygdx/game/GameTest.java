package com.mygdx.game;


import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.mygdx.game.Database.DataBaseTest;
import com.mygdx.game.screens.MainMenuScreen;
import com.mygdx.game.screens.PlayScreen;

public class GameTest extends com.badlogic.gdx.Game {
	public static final int V_WIDTH = 400;
	public static final int V_HEIGHT = 208;
	public static final float PPM = 100;

	//BITS (puissance de 2)
	public static final short NOTHING_BIT = 0;
	public static final short GROUND_BIT = 1;
	public static final short MARIO_BIT = 2;
	public static final short BRICK_BIT = 15;
	public static final short COIN_BIT = 8;
	public static final short DESTROYED_BIT = 16;
	public static final short OBJECT_BIT = 32;
	public static final short ENEMY_BIT = 64;
	public static final short ENEMY_HEAD_BIT = 128;
	public static final short ITEM_BIT = 256;
	public static final short MARIO_HEAD_BIT = 512;
	public static final short FIREBALL_BIT = 1024;
	public static final short ENEMY_WALL_BIT = 2048;
	public static final short DEAD_ZONE_BIT = 4096;
	public static final short MARIO_FOOT_BIT = 8192;
	public static final short AREA_BIT = 16384;
	public static final short ATTACK_BIT = 4;

	public static SpriteBatch batch;

	public static AssetManager manager;

	
	@Override
	/** Création des sons et écran de départ (Playscreen) */
	public void create () {
		batch = new SpriteBatch();
		manager = new AssetManager();
		manager.load("audio/music/mario_music.ogg", Music.class);
		manager.load("audio/music/stage_clear.ogg", Music.class);
		manager.load("audio/sounds/coin.wav", Sound.class);
		manager.load("audio/sounds/bump.wav", Sound.class);
		manager.load("audio/sounds/breakblock.wav", Sound.class);
		manager.load("audio/sounds/powerup_spawn.wav", Sound.class);
		manager.load("audio/sounds/powerup.wav", Sound.class);
		manager.load("audio/sounds/powerdown.wav", Sound.class);
		manager.load("audio/sounds/stomp.wav", Sound.class);
		manager.load("audio/sounds/mariodie.wav", Sound.class);
		manager.load("audio/sounds/fireball.ogg", Sound.class);
		manager.load("audio/sounds/kick.ogg", Sound.class);
		manager.load("audio/sounds/jump_small.wav", Sound.class);
		manager.load("audio/sounds/jump_super.wav", Sound.class);
		manager.finishLoading();

		DataBaseTest db = new DataBaseTest();
		db.createDatabase();
		//db.deleteTable();

		//db.insertUser("yoan");
		//db.insertData();
		//db.selectData();

		//setScreen(new MainMenuScreen(this, null));
		setScreen(new PlayScreen(this, null, 1));
	}

	@Override
	public void render () {
		super.render();
	}
	
	@Override
	/** Désactive les assets */
	public void dispose () {
		super.dispose();
		manager.dispose();
		batch.dispose();
	}
}
