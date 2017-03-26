package com.mygdx.game.screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
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
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.ScalingViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mygdx.game.Database.DataBaseTest;
import com.mygdx.game.GameTest;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.fadeIn;


/**
 * Created by Terry on 11/01/2017.
 */

public class SettingsScreen implements Screen {

    private SpriteBatch batch;
    protected Stage stage;
    private Viewport viewport;
    private OrthographicCamera camera;
    private TextureAtlas atlas;
    protected Skin skin;
    protected GameTest game;

    private boolean vibreurIsOn;
    private TextButton onOffButton;
    private String usernameSession;
    private Texture background;

    public DataBaseTest db;

    private Label title, vibreur, volume;

    public SettingsScreen(GameTest pgame, String user)
    {
        this.game = pgame;
        this.usernameSession = user;
        System.out.println("User connecté : " + usernameSession);

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

        //Stage should control input:
        Gdx.input.setInputProcessor(stage);

        //Database
        db = new DataBaseTest();


    }


    @Override
    public void show() {
        //Fade In
        stage.getRoot().getColor().a = 0;
        stage.getRoot().addAction(fadeIn(0.5f));

        //Vibreur
        db.createDatabase();
        db.selectData();

        if(db.returnData("settings", "vibreur", "idUser", db.getIdFromNameUser(usernameSession)).equalsIgnoreCase("on")) {
            vibreurIsOn = true;
        }else vibreurIsOn = false;

        //Create Table
        Table mainTable = new Table();
        //Set table to fill stage
        mainTable.setFillParent(true);
        //Set alignment of contents in the table.
        mainTable.top();

        //Create buttons
        TextButton returnButton = new TextButton("Retour", skin);

        TextButton logoutButton = new TextButton("Deconnexion", skin);
        logoutButton.setPosition(150, 10);
        logoutButton.setColor(Color.BLACK);



        if(vibreurIsOn) {
            onOffButton = new TextButton("On", skin);
            //onOffButton.setColor(Color.GREEN);
        }else {
            onOffButton = new TextButton("Off", skin);
            //onOffButton.setColor(Color.RED);
        }

        //Add listeners to buttons
        returnButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                ((Game)Gdx.app.getApplicationListener()).setScreen(new MainMenuScreen(game, usernameSession));
            }
        });

        logoutButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                ((Game)Gdx.app.getApplicationListener()).setScreen(new MainMenuScreen(game, null));
            }
        });

        onOffButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if(vibreurIsOn){
                    //Vibreur désactivé
                    onOffButton.setText("Off");
                    vibreurIsOn = false;
                    db.updateData("settings", "vibreur", "off", "idUser", db.getIdFromNameUser(usernameSession));
                }else {
                    //Vibreur activé
                    onOffButton.setText("On");
                    vibreurIsOn = true;
                    db.updateData("settings", "vibreur", "on", "idUser", db.getIdFromNameUser(usernameSession));
                }
                GameTest.manager.get("audio/sounds/switch.ogg", Sound.class).play();
            }
        });


        //Label
        title = new Label("Parametres", skin);
        title.setFontScale(2);
        vibreur = new Label("Vibreur :", skin);
        volume = new Label("Volume :", skin);

        //Add buttons to table
        mainTable.add(returnButton).width(100).height(25);
        mainTable.add(title).expandX();
        mainTable.row();
        mainTable.add(volume).expandX().padTop(25);
        mainTable.row();
        mainTable.add(vibreur).expandX().padTop(25);
        mainTable.add(onOffButton).width(100).height(25).padTop(25);
        mainTable.row();


        /// adding background image
        background = new Texture("img/background.png");

        //Add table to stage
        stage.addActor(logoutButton);
        stage.addActor(mainTable);
    }

    @Override
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


