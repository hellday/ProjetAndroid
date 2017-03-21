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
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.ScalingViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mygdx.game.Database.DataBaseTest;
import com.mygdx.game.GameTest;

import java.util.ArrayList;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.fadeIn;

/**
 * Created by Terry on 11/01/2017.
 */

public class MainMenuScreen implements Screen {

    private SpriteBatch batch;
    protected Stage stage;
    private Viewport viewport;
    private OrthographicCamera camera;
    private TextureAtlas atlas;
    protected Skin skin, mySkin;
    protected GameTest game;

    private Label title, user;

    private Dialog pseudo;
    private TextField username;
    private SelectBox selectUsername;
    private String newUsername;
    private ArrayList<String> allUser;
    private String usernameSession;
    private Texture background;

    private DataBaseTest db;

    public MainMenuScreen(GameTest pgame, String user)
    {
        this.game = pgame;
        this.usernameSession = user;
        System.out.println("User connecté : " + usernameSession);

        atlas = new TextureAtlas("skin/uiskin.atlas");
        skin = new Skin(Gdx.files.internal("skin/uiskin.json"), atlas);
        mySkin = new Skin(Gdx.files.internal("skin/glassy-ui.json"));

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
        //Fade In
        stage.getRoot().getColor().a = 0;
        stage.getRoot().addAction(fadeIn(1f));

        //Database
        db.createDatabase();

        //Create Table
        final Table mainTable = new Table();
        //Set table to fill stage
        mainTable.setFillParent(true);
        //Set alignment of contents in the table.
        mainTable.top();

        //Create buttons
        TextButton playButton = new TextButton("JOUER", skin);
        TextButton scoreButton = new TextButton("SCORE", skin);
        final TextButton settingsButton = new TextButton("OPTIONS", skin);
        TextButton exitButton = new TextButton("QUITTER", skin);

        //Image buttons
        ImageButton testImageButton = new ImageButton(mySkin);
        testImageButton.sizeBy(0.1f);
        testImageButton.getStyle().imageUp = new TextureRegionDrawable(new TextureRegion(new Texture(Gdx.files.internal("switch_off.png"))));
        testImageButton.getStyle().imageDown = new TextureRegionDrawable(new TextureRegion(new Texture(Gdx.files.internal("switch_on.png"))));


        //Add listeners to buttons
        playButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                ((Game) Gdx.app.getApplicationListener()).setScreen(new LevelSelectScreen(game, usernameSession));
            }
        });
        scoreButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                ((Game) Gdx.app.getApplicationListener()).setScreen(new ScoreScreen(game, usernameSession));
            }
        });
        settingsButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                ((Game) Gdx.app.getApplicationListener()).setScreen(new SettingsScreen(game, usernameSession));
            }
        });
        exitButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.exit();
            }
        });

        //Add buttons to table
        mainTable.add(title).expandX();
        mainTable.row();
        mainTable.add(playButton).width(100).height(25).padTop(80);
        mainTable.row();
        mainTable.add(scoreButton).width(100).height(25).padTop(10);
        mainTable.row();
        mainTable.add(settingsButton).width(100).height(25).padTop(10);
        mainTable.row();
        mainTable.setVisible(false);

        //POP-UP : Choix/Création > Utilisateur

        if (usernameSession == null){ //Si l'utilisateur n'est pas encore connecté
            pseudo = new Dialog("Pseudo", skin) {

                {
                    this.setMovable(false);

                    allUser = db.returnAllUser();
                    selectUsername = new SelectBox(skin);
                    selectUsername.setItems(allUser.toArray());

                    if(allUser.isEmpty()){
                        //Si il n'y a pas d'utilisateurs
                    }else {
                        this.getButtonTable().add(selectUsername);
                        button("Connexion", "con");
                    }

                    button("Nouveau", "new");

                }

                @Override
                protected void result(final Object object) {

                    if (object.equals("new")) {

                        new Dialog("Nouveau pseudo", skin) {

                            {
                                this.setMovable(false);
                                username = new TextField("", skin);
                                username.setMessageText("");
                                this.getButtonTable().add(username);
                                button("Nouveau", "create");
                                button("Retour", "back");
                            }

                            @Override
                            protected void result(final Object object) {
                                if (object.equals("create")) {
                                    //Création du nouvel utilisateur
                                    this.setMovable(false);
                                    newUsername = username.getText();
                                    db.insertUser(newUsername);
                                    db.newUserData(newUsername);
                                    usernameSession = newUsername;
                                    mainTable.setVisible(true);
                                    db.closeDatabase();
                                }else{
                                    ((Game) Gdx.app.getApplicationListener()).setScreen(new MainMenuScreen(game, null));
                                }
                            }

                        }.show(stage);
                    } else {
                        //Connexion de l'utilisateur
//                        user = new Label(selectUsername.getSelected().toString(), new Label.LabelStyle(new BitmapFont(), Color.WHITE));
//                        mainTable.add(user).expandX();
                        mainTable.setVisible(true);
                        usernameSession = selectUsername.getSelected().toString();

                    }

                }

            }.show(stage);
    }else mainTable.setVisible(true);

        /// adding background image
        background = new Texture("img/mainmenu.png");
        //Add table to stage
        stage.addActor(mainTable);


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