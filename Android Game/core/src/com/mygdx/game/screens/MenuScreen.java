package com.mygdx.game.screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mygdx.game.GameTest;

/**
 * Created by Terry on 13/11/2016.
 */

public class MenuScreen implements Screen{
    private Viewport viewport;
    private Stage stage;

    private Game game;
    Skin skin;


    /** Contructeur de l'écran de Menu */
    public MenuScreen(Game game){
        this.game = game;
        viewport = new FitViewport(GameTest.V_WIDTH, GameTest.V_HEIGHT, new OrthographicCamera());

        stage = new Stage(viewport, ((GameTest) game).batch);
        Gdx.input.setInputProcessor(stage);// Make the stage consume events
        skin = new Skin( Gdx.files.internal( "ui/defaultskin.json" ));


        Table table=new Table();
        table.center();

        TextButton startGame=new TextButton("start game",skin);
        table.add(startGame).width(200).height(50);
        table.row();

        TextButton options=new TextButton("options",skin);
        table.add(options).width(150).padTop(10).padBottom(3);
        table.row();

        TextButton credits=new TextButton("credits",skin);
        table.add(credits).width(150);
        table.row();

        TextButton quit=new TextButton("quit",skin);
        table.add(quit).width(100).padTop(10);

        stage.addActor(table);
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
//        if(Gdx.input.justTouched()){
//            game.setScreen(new PlayScreen((GameTest) game));
//            dispose();
//        }
        Gdx.gl.glClearColor(1, 1 ,1 ,1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act(delta);
        stage.draw();

    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {

    }

}
