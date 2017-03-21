package com.mygdx.game.screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Cursor;
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
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.ScalingViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mygdx.game.Database.DataBaseTest;
import com.mygdx.game.GameTest;

import java.util.ArrayList;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.fadeIn;

/**
 * Created by Terry on 28/01/2017.
 */

public class LevelSelectScreen implements Screen {
    private Skin skin;
    private Stage stage;

    protected GameTest game;
    private String usernameSession;
    private SpriteBatch batch;
    private Viewport viewport;
    private OrthographicCamera camera;

    private DataBaseTest db;
    private TextureAtlas atlas;

    private Label title, locked;

    private Texture TextureLeft, TextureRight;
    private Image leftArrow, rightArrow;

    private TextButton playButton;

    private int levelAmount;
    private ArrayList<String> checkLevelLock;
    private Texture background;

    private Texture textLevel1, textLevel2, textLevel3;
    private Image imgLevel1, imgLevel2, imgLevel3;


    public LevelSelectScreen(GameTest pgame, String user) {

        this.game = pgame;
        this.usernameSession = user;
        atlas = new TextureAtlas("skin/uiskin.atlas");
        skin = new Skin(Gdx.files.internal("skin/uiskin.json"), atlas);

        batch = new SpriteBatch();
        camera = new OrthographicCamera();
        //viewport = new FitViewport(GameTest.V_WIDTH, GameTest.V_HEIGHT, camera);
        viewport = new ScalingViewport(Scaling.stretch, GameTest.V_WIDTH, GameTest.V_HEIGHT);
        viewport.apply();

        camera.position.set(camera.viewportWidth / 2, camera.viewportHeight / 2, 0);
        camera.update();

        stage = new Stage(viewport, batch);

        //Database
        db = new DataBaseTest();

        //Stage should control input:
        Gdx.input.setInputProcessor(stage);

        //Initialisation des niveaux
        levelAmount = 1;

    }

    public void render(float delta) {
        Gdx.gl.glClearColor(.1f, .12f, .16f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.getBatch().begin();
        stage.getBatch().draw(background,0,0,GameTest.V_WIDTH,GameTest.V_HEIGHT);
        stage.getBatch().end();

        stage.act();
        stage.draw();

        update(delta);
    }

    @Override
    public void show() {
        //Fade In
        stage.getRoot().getColor().a = 0;
        stage.getRoot().addAction(fadeIn(0.5f));

        //Database
        db.createDatabase();

        //CheckLevel
        checkLevelLock = db.checkLevel(usernameSession);
        System.out.println(checkLevelLock + "avec une taille de " + checkLevelLock.size());

        //ImageLevel
        textLevel1 = new Texture(Gdx.files.internal("img/level1log.png"));
        textLevel2 = new Texture(Gdx.files.internal("img/level2log.png"));
        textLevel3 = new Texture(Gdx.files.internal("img/level3log.png"));

        imgLevel1 = new Image(textLevel1);
        imgLevel1.setScale(0.5f, 0.5f);
        imgLevel1.setPosition(140, 40);

        imgLevel2 = new Image(textLevel2);
        imgLevel2.setScale(0.5f, 0.5f);
        imgLevel2.setPosition(140, 40);
        imgLevel2.setVisible(false);

        imgLevel3 = new Image(textLevel3);
        imgLevel3.setScale(0.5f, 0.5f);
        imgLevel3.setPosition(140, 40);
        imgLevel3.setVisible(false);


        //ImageButton
        TextureLeft = new Texture(Gdx.files.internal("controller/left_controller.png"));
        leftArrow = new Image(TextureLeft); //Set the button up
        leftArrow.setPosition(20, 10);
        leftArrow.setScale(0.5f, 0.5f);
        leftArrow.setVisible(false);

        leftArrow.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                System.out.println("Image gauche pressé");
                levelAmount--;
            }
        });

        TextureRight = new Texture(Gdx.files.internal("controller/right_controller.png"));
        rightArrow = new Image(TextureRight); //Set the button up
        rightArrow.setPosition(340, 10);
        rightArrow.setScale(0.5f, 0.5f);

        rightArrow.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                System.out.println("Image droite pressé");
                levelAmount++;
            }
        });


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

        playButton = new TextButton("Jouer", skin);
        playButton.setWidth(100);
        playButton.setHeight(25);
        playButton.setPosition(150, 5);
        playButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                    ((Game) Gdx.app.getApplicationListener()).setScreen(new PlayScreen(game, usernameSession, levelAmount));
            }
        });

        //Label
        title = new Label("Niveaux", skin);
        title.setFontScale(2);
        title.setPosition(150, 180);

        locked = new Label("Locked", skin);
        locked.setPosition(170, 5);
        locked.setVisible(false);

        //Add buttons to table
        mainTable.add(returnButton).width(100).height(25);
        mainTable.add(title).expandX();

        /// adding background image
        background = new Texture("img/background.png");

        //Add table to stage
        //stage.addActor(mainTable);
        stage.addActor(returnButton);
        stage.addActor(playButton);
        stage.addActor(title);
        stage.addActor(locked);
        stage.addActor(leftArrow);
        stage.addActor(rightArrow);
        stage.addActor(imgLevel1);
        stage.addActor(imgLevel2);
        stage.addActor(imgLevel3);
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

        //Gestion des touches Gauche et Droite de Sélection
        if(levelAmount == 1){
            leftArrow.setVisible(false);
            imgLevel1.setVisible(true);
            imgLevel2.setVisible(false);
            imgLevel3.setVisible(false);
        }else leftArrow.setVisible(true);

        if(levelAmount == 2){
            imgLevel1.setVisible(false);
            imgLevel2.setVisible(true);
            imgLevel3.setVisible(false);
        }

        if(levelAmount == 3){
            rightArrow.setVisible(false);
            imgLevel1.setVisible(false);
            imgLevel2.setVisible(false);
            imgLevel3.setVisible(true);
        }else rightArrow.setVisible(true);

        //En fonction des Level débloqués
        if(checkLevelLock.isEmpty() && levelAmount == 1){
            playButton.setVisible(true);
            locked.setVisible(false);
        }else if(checkLevelLock.size() == 1 || checkLevelLock.size() == 2 || checkLevelLock.size() == 3 && levelAmount == 1) {
            playButton.setVisible(true);
            locked.setVisible(false);
        }else if(checkLevelLock.size() == 1 || checkLevelLock.size() == 2 || checkLevelLock.size() == 3 && levelAmount == 2){
            playButton.setVisible(true);
            locked.setVisible(false);
        }else  if(checkLevelLock.size() == 2 || checkLevelLock.size() == 3 && levelAmount == 3){
            playButton.setVisible(true);
            locked.setVisible(false);
        }else {
            playButton.setVisible(false);
            locked.setVisible(true);
        }

    }

}
