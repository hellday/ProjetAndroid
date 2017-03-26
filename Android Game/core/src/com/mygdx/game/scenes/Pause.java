package com.mygdx.game.scenes;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mygdx.game.GameTest;
import com.mygdx.game.screens.PlayScreen;

/**
 * Created by Terry on 05/03/2017.
 */

public class Pause{
    public Stage stage;
    public Viewport viewport;

    private TextureAtlas atlas;
    private Skin skin;

    private TextButton resumeButton, quitButton;
    private boolean resume, quit;
    private OrthographicCamera cam;

    public Pause(){
        atlas = new TextureAtlas("skin/uiskin.atlas");
        skin = new Skin(Gdx.files.internal("skin/uiskin.json"), atlas);

        cam = new OrthographicCamera();
        viewport = new FitViewport(GameTest.V_WIDTH, GameTest.V_HEIGHT, cam);
        stage = new Stage(viewport, GameTest.batch);

        resume = false;
        quit = false;



        Table table = new Table();
        table.center();
        table.setFillParent(true);

        Label pauseLabel = new Label("Pause", skin);

        resumeButton = new TextButton("Continuer", skin);
        resumeButton.setWidth(100);
        resumeButton.setHeight(25);
        resumeButton.setPosition(100, 0);
        resumeButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                GameTest.manager.get("audio/sounds/pause.wav", Sound.class).play();
                System.out.println("Continuer");
                resume = true;

            }
        });



        quitButton = new TextButton("Quitter", skin);
        quitButton.setWidth(100);
        quitButton.setHeight(25);
        quitButton.setPosition(200, 0);
        quitButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                GameTest.manager.get("audio/sounds/pause.wav", Sound.class).play();
                System.out.println("Quitter");
                quit = true;

            }
        });

        Gdx.input.setInputProcessor(stage);
        table.add(pauseLabel).expandX();
        table.row();
        table.add(resumeButton);
        table.row();
        table.add(quitButton);

        stage.addActor(table);
    }

    public void dispose() {
        stage.dispose();
    }

    public void update(float dt){

    }

    public void draw(){
        stage.draw();
    }

    public boolean isQuit() {
        return quit;
    }

    public void setQuit(boolean quit) {
        this.quit = quit;
    }

    public boolean isResume() {
        return resume;
    }

    public void setResume(boolean resume) {
        this.resume = resume;
    }
}
