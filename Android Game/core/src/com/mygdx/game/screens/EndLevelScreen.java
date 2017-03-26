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
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
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
import com.mygdx.game.scenes.Hud;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.fadeIn;

/**
 * Created by Terry on 23/02/2017.
 */

public class EndLevelScreen implements Screen {
    private Viewport viewport;
    private Stage stage;
    private OrthographicCamera gamecam;
    private ShapeRenderer shapeRenderer;

    protected GameTest game;
    private String usernameSession;

    private TextureAtlas atlas;
    private Skin skin;
    private TextButton playButton, nextButton;

    private Hud hud;
    private int level;

    private int oldScore;
    private int newScore;

    private Label intScoreLabel;
    private Texture background;

    public EndLevelScreen(final GameTest game, String user, int lvl) {
        this.game = game;
        this.usernameSession = user;
        this.level = lvl;

        DataBaseTest db = new DataBaseTest();
        db.createDatabase();
        shapeRenderer = new ShapeRenderer();


        atlas = new TextureAtlas("skin/uiskin.atlas");
        skin = new Skin(Gdx.files.internal("skin/uiskin.json"), atlas);

        gamecam = new OrthographicCamera();
        //viewport = new FitViewport(GameTest.V_WIDTH, GameTest.V_HEIGHT, new OrthographicCamera());
        viewport = new ScalingViewport(Scaling.stretch, GameTest.V_WIDTH, GameTest.V_HEIGHT);
        stage = new Stage(viewport, game.batch);

        //Stage should control input:
        Gdx.input.setInputProcessor(stage);

        hud = new Hud();

        oldScore = db.returnLevelScore(db.getIdFromNameUser(usernameSession), "level"+level);
        System.out.println("Ancien score : " + oldScore);
        newScore = hud.getScore();

        //shapeRenderer.setProjectionMatrix(gamecam.combined);



        //Label.LabelStyle font = new Label.LabelStyle(new BitmapFont(), Color.WHITE);

        Table table = new Table();
        table.center();
        table.setFillParent(true);

        Label endLabel = new Label("Fin du niveau " + level, skin);
        endLabel.setColor(Color.BLACK);
        Label scoreLabel = new Label("Score : " + hud.getScore(), skin);
        scoreLabel.setColor(Color.BLACK);

        if(newScore > oldScore) {
           intScoreLabel = new Label("Meilleur score : " + hud.getScore(), skin);
            intScoreLabel.setColor(Color.BLACK);
            //Update de la table score
            db.updateScore(hud.getScore(), db.getIdFromNameUser(usernameSession), "level"+level);
        }else {
            intScoreLabel = new Label("Meilleur score : " + db.returnLevelScore(db.getIdFromNameUser(usernameSession), "level" + level), skin);
            intScoreLabel.setColor(Color.BLACK);
        }

        playButton = new TextButton("Rejouer", skin);
        playButton.setWidth(100);
        playButton.setHeight(25);
        playButton.setPosition(100, 10);
        playButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                ((Game) Gdx.app.getApplicationListener()).setScreen(new PlayScreen(game, usernameSession, level));

            }
        });

        if(level != 3) {
            nextButton = new TextButton("Suivant", skin);
            nextButton.setWidth(100);
            nextButton.setHeight(25);
            nextButton.setPosition(200, 10);
            nextButton.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    ((Game) Gdx.app.getApplicationListener()).setScreen(new PlayScreen(game, usernameSession, level + 1));

                }
            });
        }
        table.add(endLabel).expandX().padTop(25f);
        table.row();
        table.add(scoreLabel).expandX().padTop(10f);
        table.row();
        table.add(intScoreLabel).expandX().padTop(10f);

        /// adding background image
        background = new Texture("img/mainmenu.png");

        stage.addActor(table);
        stage.addActor(playButton);
        if(level != 3) {
            stage.addActor(nextButton);
        }


    }

    @Override
    public void show() {
        //Fade In
        stage.getRoot().getColor().a = 0;
        stage.getRoot().addAction(fadeIn(0.5f));

    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0 ,0 ,1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.getBatch().begin();
        stage.getBatch().draw(background,0,0,GameTest.V_WIDTH,GameTest.V_HEIGHT);
        stage.getBatch().end();

        shapeRenderer.setProjectionMatrix(game.batch.getProjectionMatrix());
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(Color.WHITE);
        shapeRenderer.rect(100, 40, 200, 100);
        shapeRenderer.end();

        stage.act();
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
