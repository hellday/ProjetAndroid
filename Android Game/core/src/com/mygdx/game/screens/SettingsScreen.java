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
import com.mygdx.game.Database.DataBaseTest;
import com.mygdx.game.GameTest;


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

    public DataBaseTest db;

    private Label title, vibreur, volume;

    public SettingsScreen(GameTest pgame)
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

        //Database
        db = new DataBaseTest();


    }


    @Override
    public void show() {
        //Vibreur
        db.createDatabase();
        db.selectData();

        if(db.returnData("settings", "vibreur", "idUser", 1).equalsIgnoreCase("on")) {
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


        if(vibreurIsOn) {
            onOffButton = new TextButton("On", skin);
        }else onOffButton = new TextButton("Off", skin);

        //Add listeners to buttons
        returnButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                ((Game)Gdx.app.getApplicationListener()).setScreen(new TestScreen(game));
            }
        });

        onOffButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if(vibreurIsOn){
                    //Vibreur désactivé
                    onOffButton.setText("Off");
                    vibreurIsOn = false;
                    db.updateData("settings", "vibreur", "off", "idUser", 1);
                }else {
                    //Vibreur activé
                    onOffButton.setText("On");
                    vibreurIsOn = true;
                    db.updateData("settings", "vibreur", "on", "idUser", 1);
                }
            }
        });


        //Label
        title = new Label("Paramètres", new Label.LabelStyle(new BitmapFont(), Color.WHITE));
        title.setFontScale(2);
        vibreur = new Label("Vibreur :", new Label.LabelStyle(new BitmapFont(), Color.GRAY));
        volume = new Label("Volume :", new Label.LabelStyle(new BitmapFont(), Color.GRAY));

        //Add buttons to table
        mainTable.add(returnButton).width(100).height(25);
        mainTable.add(title).expandX();
        mainTable.row();
        mainTable.add(volume).expandX().padTop(25);
        mainTable.row();
        mainTable.add(vibreur).expandX().padTop(25);
        mainTable.add(onOffButton).width(100).height(25);
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


