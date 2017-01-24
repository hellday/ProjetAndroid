package com.mygdx.game.screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mygdx.game.GameTest;

/**
 * Created by Terry on 11/01/2017.
 */

public class TestScreen implements Screen {

    private SpriteBatch batch;
    protected Stage stage;
    private Viewport viewport;
    private OrthographicCamera camera;
    private TextureAtlas atlas;
    protected Skin skin, mySkin;
    protected GameTest game;

    private Label title;

    public TestScreen(GameTest pgame)
    {
        this.game = pgame;
        atlas = new TextureAtlas("ui/defaultskin.atlas");
        skin = new Skin(Gdx.files.internal("ui/defaultskin.json"), atlas);
        mySkin = new Skin(Gdx.files.internal("skin/glassy-ui.json"));

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
        TextButton playButton = new TextButton("JOUER", skin);
        TextButton scoreButton = new TextButton("SCORE", skin);
        TextButton settingsButton = new TextButton("OPTIONS", skin);
        TextButton exitButton = new TextButton("QUITTER", skin);

        //Image buttons
        ImageButton testImageButton = new ImageButton(mySkin);
        testImageButton.sizeBy(0.1f);
        testImageButton.getStyle().imageUp = new TextureRegionDrawable(new TextureRegion(new Texture(Gdx.files.internal("switch_off.png"))));
        testImageButton.getStyle().imageDown = new TextureRegionDrawable(new TextureRegion(new Texture(Gdx.files.internal("switch_on.png"))));


        //Labels
        title = new Label("BILLY", new Label.LabelStyle(new BitmapFont(), Color.WHITE));
        title.setFontScale(2);

        //Add listeners to buttons
        playButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                ((Game)Gdx.app.getApplicationListener()).setScreen(new PlayScreen(game));
            }
        });
        scoreButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                ((Game)Gdx.app.getApplicationListener()).setScreen(new ScoreScreen(game));
            }
        });
        settingsButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                ((Game)Gdx.app.getApplicationListener()).setScreen(new SettingsScreen(game));
            }
        });
        exitButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.exit();
            }
        });

        //Add buttons to table
        mainTable.add(title).expandX();
        mainTable.row();
        mainTable.add(playButton).width(100).height(25).padTop(20);
        mainTable.row();
        mainTable.add(scoreButton).width(100).height(25).padTop(10);
        mainTable.row();
        mainTable.add(settingsButton).width(100).height(25).padTop(10);
        mainTable.row();
//        mainTable.add(exitButton).width(100).height(25).padTop(10);
//        mainTable.row();
        mainTable.add(testImageButton).padTop(10);

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