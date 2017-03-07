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
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.List;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.ScalingViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mygdx.game.Database.DataBaseTest;
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
    protected ScrollPane scrollpane, scrollpane2;
    protected List list, list2;

    private Label title, nomLabel, pointsLabel, bonusLabel, scoreLabel;
    private Texture arrow;
    private Image arr;
    private String usernameSession;
    private Texture background;

    public DataBaseTest db;

    public ScoreScreen(GameTest pgame, String user)
    {
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

    }


    @Override
    public void show() {

        //Image
        arrow = new Texture(Gdx.files.internal("img/arrow.png"));
        arr = new Image(arrow);
        arr.setScale(0.15f, 0.15f);
        arr.setPosition(180, 85);


        db.createDatabase();
        db.selectData();

        //Create Table
        Table mainTable = new Table();
        Table testTable = new Table();
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
        title = new Label("Score", skin);
        title.setFontScale(2);
        title.setPosition(150, 180);


        //Add buttons to table
        mainTable.add(returnButton).width(100).height(25);
        mainTable.add(title).expandX();


        testTable.add(arr).width(50).height(50);

        int test = db.returnLevelScore(db.getIdFromNameUser(usernameSession), "level1");

        //List
        list = new List<String>(skin);
        list.setColor(Color.BLACK);
        list2 = new List<String>(skin);
        list.setItems(new String[] {"Level 1", "Level 2", "Level 3"});
        list2.setItems(new Object[] {"Bonus", "...", "Total", db.returnLevelScore(db.getIdFromNameUser(usernameSession), "level1")});
        list2.setTouchable(Touchable.disabled); //Rendu intouchable

        //Scrollpane
        scrollpane = new ScrollPane(list);
        scrollpane.setPosition(15, 0);
        scrollpane2 = new ScrollPane(list2);
        scrollpane2.setPosition(240, 0);

        /// adding background image
        background = new Texture("img/background.png");

        //Add table to stage
        //stage.addActor(mainTable);
        stage.addActor(returnButton);
        stage.addActor(title);
        stage.addActor(scrollpane);
        stage.addActor(scrollpane2);
        stage.addActor(arr);


    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(.1f, .12f, .16f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.getBatch().begin();
        stage.getBatch().disableBlending();
        stage.getBatch().draw(background,0,0,GameTest.V_WIDTH,GameTest.V_HEIGHT);
        stage.getBatch().enableBlending();
        stage.getBatch().end();

        stage.act();
        stage.draw();

        update(delta);

    }

    /** Actualise les informations à l'écran */
    public void update(float dt){
        camera.update();

        //Mise à jour des Scores selon le niveau choisi
        if(list.getSelected().toString().equalsIgnoreCase("Level 1")){
            list2.setItems(new Object[] {"Bonus", "...", "Total", db.returnLevelScore(db.getIdFromNameUser(usernameSession), "level1")});
        }

        if(list.getSelected().toString().equalsIgnoreCase("Level 2")){
            list2.setItems(new Object[] {"Bonus", "...", "Total", db.returnLevelScore(db.getIdFromNameUser(usernameSession), "level2")});
        }

        if(list.getSelected().toString().equalsIgnoreCase("Level 3")){
            list2.setItems(new Object[] {"Bonus", "...", "Total", db.returnLevelScore(db.getIdFromNameUser(usernameSession), "level3")});
        }
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


