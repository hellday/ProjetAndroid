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
import com.mygdx.game.scenes.Hud;

/**
 * Created by Terry on 23/02/2017.
 */

public class EndLevelScreen implements Screen {
    private Viewport viewport;
    private Stage stage;
    private OrthographicCamera gamecam;
    //private SpriteBatch batch;

    protected GameTest game;
    private String usernameSession;

    private TextureAtlas atlas;
    private Skin skin;
    private TextButton playButton;

    private Hud hud;
    private int level;

    public EndLevelScreen(final GameTest game, String user, int lvl) {
        this.game = game;
        this.usernameSession = user;
        this.level = lvl;

        DataBaseTest db = new DataBaseTest();
        db.createDatabase();
        int id = db.getIdFromNameUser(usernameSession);


        atlas = new TextureAtlas("skin/uiskin.atlas");
        skin = new Skin(Gdx.files.internal("skin/uiskin.json"), atlas);
        //batch = new SpriteBatch();

        gamecam = new OrthographicCamera();
        viewport = new FitViewport(GameTest.V_WIDTH, GameTest.V_HEIGHT, new OrthographicCamera());
        //viewport.setCamera(gamecam);
        stage = new Stage(viewport, game.batch);

        //Stage should control input:
        Gdx.input.setInputProcessor(stage);

        hud = new Hud();

        //Label.LabelStyle font = new Label.LabelStyle(new BitmapFont(), Color.WHITE);

        Table table = new Table();
        table.center();
        table.setFillParent(true);

        Label endLabel = new Label("Fin du niveau " + level, skin);
        Label scoreLabel = new Label("Score : " + hud.getScore(), skin);
        Label intScoreLabel = new Label("Meilleur score : " + db.returnLevelScore(1, "level"+level), skin);

        playButton = new TextButton("Rejouer", skin);
        playButton.setWidth(100);
        playButton.setHeight(25);
        playButton.setPosition(150, 0);
        playButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                    System.out.println("Rejouer");
                ((Game) Gdx.app.getApplicationListener()).setScreen(new PlayScreen(game, usernameSession, level));

            }
        });

        table.add(endLabel).expandX();
        table.row();
        table.add(scoreLabel).expandX().padTop(10f);
        table.row();
        table.add(intScoreLabel).expandX().padTop(10f);

        stage.addActor(table);
        stage.addActor(playButton);


    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
//        if(Gdx.input.justTouched()){
//            game.setScreen(new PlayScreen(game, usernameSession));
//            dispose();
//        }
        Gdx.gl.glClearColor(0, 0 ,0 ,1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
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
