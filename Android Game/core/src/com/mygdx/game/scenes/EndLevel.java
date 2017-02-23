package com.mygdx.game.scenes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mygdx.game.GameTest;

/**
 * Created by Terry on 23/02/2017.
 */

public class EndLevel implements Disposable {
    public Stage stage;
    public Viewport viewport;

    private TextureAtlas atlas;
    private Skin skin;

    private Dialog end;

    public EndLevel(SpriteBatch sb){
        atlas = new TextureAtlas("skin/uiskin.atlas");
        skin = new Skin(Gdx.files.internal("skin/uiskin.json"), atlas);

        viewport = new FitViewport(GameTest.V_WIDTH, GameTest.V_HEIGHT, new OrthographicCamera());
        stage = new Stage(viewport, sb);



//        Table table = new Table();
//        table.top();
//        table.setFillParent(true);
//
//        Label timeLabel = new Label("TIME", skin);
//        table.add(timeLabel).expandX().padTop(50);
//        stage.addActor(table);

        //PopUp
        end = new Dialog("Pseudo", skin) {

            {
                //this.setMovable(false);
                text("Fin du niveau");
                button("Rejouer", "replay");
                button("Suivant", "next");

            }

            @Override
            protected void result(final Object object) {

                if (object.equals("replay")) {


                }

            }

        }.show(stage);
    }

    @Override
    public void dispose() {
        stage.dispose();
    }

    public void update(float dt){

    }
}
