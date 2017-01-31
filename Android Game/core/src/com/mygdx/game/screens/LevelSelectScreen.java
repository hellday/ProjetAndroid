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
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.List;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mygdx.game.Database.DataBaseTest;
import com.mygdx.game.GameTest;

/**
 * Created by Terry on 28/01/2017.
 */

public class LevelSelectScreen implements Screen {
    private Skin skin;
    private Stage stage;
    private Table container;

    protected GameTest game;
    private String usernameSession;
    private SpriteBatch batch;
    private Viewport viewport;
    private OrthographicCamera camera;

    private DataBaseTest db;
    private TextureAtlas atlas;

    private Label title;

    private Texture TextureLeft, TextureRight;
    private TextureRegion TextureRegionLeft, TextureRegionRight;
    private TextureRegionDrawable TextRegionDrawableLeft, TextRegionDrawableRight;
    private ImageButton leftArrow, rightArrow;

    public LevelSelectScreen(GameTest pgame, String user) {

        this.game = pgame;
        this.usernameSession = user;
        atlas = new TextureAtlas("skin/uiskin.atlas");
        skin = new Skin(Gdx.files.internal("skin/uiskin.json"), atlas);

        batch = new SpriteBatch();
        camera = new OrthographicCamera();
        viewport = new FitViewport(GameTest.V_WIDTH, GameTest.V_HEIGHT, camera);
        viewport.apply();

        camera.position.set(camera.viewportWidth / 2, camera.viewportHeight / 2, 0);
        camera.update();

        stage = new Stage(viewport, batch);

        //Database
        db = new DataBaseTest();

        //Stage should control input:
        Gdx.input.setInputProcessor(stage);

    }

    public void render(float delta) {
        Gdx.gl.glClearColor(.1f, .12f, .16f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.act();
        stage.draw();
    }

    @Override
    public void show() {

        //ImageButton
        TextureLeft = new Texture(Gdx.files.internal("controller/left_controller.png"));
        TextureRegionLeft = new TextureRegion(TextureLeft);
        TextRegionDrawableLeft = new TextureRegionDrawable(TextureRegionLeft);
        leftArrow = new ImageButton(TextRegionDrawableLeft); //Set the button up
        leftArrow.setPosition(10, 10);

        leftArrow.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                System.out.println("Image gauche pressé");
            }
        });


        //Database
        db.createDatabase();
        db.selectData();

        //Create Table
        Table mainTable = new Table();
        //Set table to fill stage
        mainTable.setFillParent(true);
        //Set alignment of contents in the table.
        mainTable.top();

        //Create buttons
        TextButton returnButton = new TextButton("Retour", skin);
        returnButton.setPosition(30, 180);

        //Add listeners to buttons
        returnButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                ((Game)Gdx.app.getApplicationListener()).setScreen(new MainMenuScreen(game, usernameSession));
            }
        });

        //Label
        title = new Label("Niveaux", skin);
        title.setFontScale(2);
        title.setPosition(150, 180);

        //Add buttons to table
        mainTable.add(returnButton).width(100).height(25);
        mainTable.add(title).expandX();

        //Add table to stage
        //stage.addActor(mainTable);
        stage.addActor(returnButton);
        stage.addActor(title);
        stage.addActor(leftArrow);
    }

    @Override
    public void pause() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void resume() {

    }

    public void resize (int width, int height) {
        viewport.update(width, height);
        camera.position.set(camera.viewportWidth / 2, camera.viewportHeight / 2, 0);
        camera.update();
    }

    public void dispose () {
        stage.dispose();
        skin.dispose();
    }

    public void update(float dt){
        camera.update();
    }



}
