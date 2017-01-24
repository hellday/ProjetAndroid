package com.mygdx.game.screens;
  import com.badlogic.gdx.Game;
  import com.badlogic.gdx.Gdx;
        import com.badlogic.gdx.Screen;
        import com.badlogic.gdx.graphics.GL20;
  import com.badlogic.gdx.graphics.OrthographicCamera;
  import com.badlogic.gdx.graphics.Texture;
  import com.badlogic.gdx.scenes.scene2d.InputEvent;
        import com.badlogic.gdx.scenes.scene2d.Stage;
        import com.badlogic.gdx.scenes.scene2d.actions.Actions;
        import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
        import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
        import com.badlogic.gdx.scenes.scene2d.ui.Skin;
        import com.badlogic.gdx.scenes.scene2d.ui.Table;
        import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
        import com.badlogic.gdx.scenes.scene2d.ui.TextField;
        import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
  import com.badlogic.gdx.utils.viewport.FitViewport;
  import com.badlogic.gdx.utils.viewport.Viewport;
  import com.mygdx.game.GameTest;

public class MainMenuScreen implements Screen {

    Skin skin;
    private Viewport viewport;
    Stage stage;
    GameTest game;
    Texture background, settings;

    // constructor to keep a reference to the main Game class
    public MainMenuScreen(GameTest pgame){
        this.game = pgame;

        background = new Texture("ui/bg.png");
        settings = new Texture("ui/settings.png");


        viewport = new FitViewport(GameTest.V_WIDTH, GameTest.V_HEIGHT, new OrthographicCamera());
        stage = new Stage(viewport, game.batch);
        Gdx.input.setInputProcessor(stage);

        skin = new Skin( Gdx.files.internal( "ui/defaultskin.json" ));

        Table table=new Table();
        table.center();
        table.setFillParent(true);

        final TextButton startGame=new TextButton("Jouer", skin);
        startGame.getLabel().setFontScale(0.8f, 0.8f);
        table.add(startGame).width(100).height(25);
        table.row();

        TextButton score=new TextButton("Score", skin);
        //score.getLabel().setFontScale(0.8f, 0.8f);
        table.add(score).width(100).height(25).padTop(10);
        table.row();

        stage.addActor(table);

        startGame.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                startGame.addAction(Actions.fadeOut(0.7f));
                game.setScreen(new PlayScreen(game));
            }
        });

        startGame.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                startGame.addAction(Actions.fadeOut(0.7f));
            }
        });
    }

    @Override
    public void render(float delta) {
        // clear the screen
        Gdx.gl.glClearColor(0 ,0 ,0 , 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        game.batch.begin();
        game.batch.draw(background, 0, 0, GameTest.V_WIDTH, GameTest.V_HEIGHT);
        game.batch.draw(settings, 530, 10, 100, 100);
        game.batch.end();

        // let the stage act and draw
        stage.act(delta);
        stage.draw();


    }

    @Override
    public void resize(int width, int height) {
    }

    @Override
    public void show() {
        // called when this screen is set as the screen with game.setScreen();
    }

    @Override
    public void hide() {
        // called when current screen changes from this to a different screen
        stage.dispose();
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void dispose() {
        // never called automatically
        stage.dispose();
    }
}




