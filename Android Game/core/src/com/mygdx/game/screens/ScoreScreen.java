package com.mygdx.game.screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mygdx.game.GameTest;


/**
 * Created by Terry on 11/01/2017.
 */

public class ScoreScreen implements Screen {

    private SpriteBatch batch;
    protected Stage stage;
    private Viewport viewport;
    private OrthographicCamera camera;
    private TextureAtlas atlas;
    protected Skin skin;
    protected GameTest game;

    private Label title, nomLabel, pointsLabel, bonusLabel, scoreLabel;

    public ScoreScreen(GameTest pgame)
    {
        this.game = pgame;
        atlas = new TextureAtlas("ui/defaultskin.atlas");
        skin = new Skin(Gdx.files.internal("ui/defaultskin.json"), atlas);

        batch = new SpriteBatch();
        camera = new OrthographicCamera();
        viewport = new FitViewport(GameTest.V_WIDTH, GameTest.V_HEIGHT, camera);
        viewport.apply();

        camera.position.set(camera.viewportWidth / 2, camera.viewportHeight / 2, 0);
        camera.update();

        stage = new Stage(viewport, batch);

        //Stage should control input:
        Gdx.input.setInputProcessor(stage);
    }


    @Override
    public void show() {
        //Create Table
        Table mainTable = new Table();
        //Set table to fill stage
        mainTable.setFillParent(true);
        //Set alignment of contents in the table.
        mainTable.top();

        //Create buttons
        TextButton returnButton = new TextButton("Retour", skin);


        //Add listeners to buttons
        returnButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                ((Game)Gdx.app.getApplicationListener()).setScreen(new TestScreen(game));
            }
        });


        //Label
        title = new Label("Score", new Label.LabelStyle(new BitmapFont(), Color.WHITE));
        title.setFontScale(2);
        nomLabel = new Label("Nom", new Label.LabelStyle(new BitmapFont(), Color.GRAY));
        pointsLabel = new Label("Points", new Label.LabelStyle(new BitmapFont(), Color.GRAY));
        bonusLabel = new Label("Bonus", new Label.LabelStyle(new BitmapFont(), Color.GRAY));
        scoreLabel = new Label("Score", new Label.LabelStyle(new BitmapFont(), Color.GRAY));

        //Add buttons to table
        mainTable.add(returnButton).width(100).height(25);
        mainTable.add(title).expandX();
        mainTable.row();
        mainTable.add(nomLabel).expandX().padTop(25);
        mainTable.add(pointsLabel).expandX().padTop(25);
        mainTable.add(bonusLabel).expandX().padTop(25);
        mainTable.add(scoreLabel).expandX().padTop(25);
//        mainTable.add(volume).expandX();
//        mainTable.row();
//        mainTable.add(vibreur).expandX();
//        mainTable.row();
//        mainTable.add(playButton).width(100).height(25);
//        mainTable.row();
//        mainTable.add(optionsButton).width(100).height(25).padTop(10);
//        mainTable.row();
//        mainTable.add(exitButton).width(100).height(25).padTop(10);

        //Add table to stage
        stage.addActor(mainTable);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(.1f, .12f, .16f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.act();
        stage.draw();
    }

    /** Actualise les informations à l'écran */
    public void update(float dt){
        camera.update();
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
        skin.dispose();
        atlas.dispose();
    }
}


