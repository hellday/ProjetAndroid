package com.mygdx.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.ScalingViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mygdx.game.GameTest;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.fadeIn;

/**
 * Created by Terry on 27/03/2017.
 */

public class TutorialScreen implements Screen {
    private Viewport viewport;
    private Stage stage;

    private GameTest game;
    private String usernameSession;
    private int level;

    private Texture background;
    private OrthographicCamera camera;

    private TextureAtlas atlas;
    private Skin skin;

    public TutorialScreen(GameTest game, String user, int lvl){
        this.game = game;
        this.usernameSession = user;
        this.level = lvl;

        atlas = new TextureAtlas("skin/uiskin.atlas");
        skin = new Skin(Gdx.files.internal("skin/uiskin.json"), atlas);

        camera = new OrthographicCamera();
        viewport = new ScalingViewport(Scaling.stretch, GameTest.V_WIDTH, GameTest.V_HEIGHT);
        viewport.apply();

        camera.position.set(camera.viewportWidth / 2, camera.viewportHeight / 2, 0);
        camera.update();

        stage = new Stage(viewport, game.batch);

        //Stage should control input:
        Gdx.input.setInputProcessor(stage);

    }

    @Override
    public void show() {
        //Fade In
        stage.getRoot().getColor().a = 0;
        stage.getRoot().addAction(fadeIn(1f));

        TextButton title = new TextButton("Test", skin);
        title.setPosition(-100, -100);

        ///Background image
        background = new Texture("img/tutorial.png");

        stage.addActor(title);
    }

    @Override
    public void render(float delta) {
        if(Gdx.input.justTouched()){
            game.setScreen(new PlayScreen(game, usernameSession, level));
            dispose();
        }

        Gdx.gl.glClearColor(.1f, .12f, .16f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.getBatch().begin();
        stage.getBatch().draw(background,0,0,GameTest.V_WIDTH,GameTest.V_HEIGHT);
        stage.getBatch().end();

        stage.act();
        stage.draw();

    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
        camera.position.set(camera.viewportWidth / 2, camera.viewportHeight / 2, 0);
        camera.update();
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
        stage.dispose();
    }
}
