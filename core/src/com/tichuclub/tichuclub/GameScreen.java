package com.tichuclub.tichuclub;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TiledDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import java.util.ArrayList;
import java.util.List;

import static com.badlogic.gdx.Application.ApplicationType.Android;

public class GameScreen implements Screen {

    private boolean debug = false;
    private Game game;
    private Stage stage;
    private String skinFile = "uiskin.json";
    private String backgroundAsset = "woodback.png";
    private String silhouetteImage;

    public GameScreen(Game game) {
        //debug = true;
        this.game = game;
        silhouetteImage = (Gdx.app.getType() == Android) ? "silhouette-mobile.png" : "silhouette-small.png";
    }

    public void show() {
        stage = new Stage(new ScreenViewport());

        AssetManager assetManager = new AssetManager();
        assetManager.load(skinFile, Skin.class);
        assetManager.finishLoading();

        //Declare root Table
        Table rootTable = new Table();
        rootTable.setFillParent(true);

        Skin skin = assetManager.get(skinFile);
        rootTable.setSkin(skin);

        Texture woodBackground = new Texture(Gdx.files.internal(backgroundAsset));
        TiledDrawable woodTile = new TiledDrawable(new TextureRegion(woodBackground, 0, 0, 600, 392));

        rootTable.setBackground(woodTile);
        rootTable.setDebug(debug);

        //Top table
        rootTable.row();

        Table topTable = new Table();
        Image topPlayer = new Image(new Texture(Gdx.files.internal(silhouetteImage)));
        topPlayer.setScaling(Scaling.fit);
        topTable.add(topPlayer);

        topTable.row();
        Label topLabel = new Label("NORTH", skin);
        topTable.add(topLabel);
        topTable.top();
        rootTable.add(topTable).colspan(3).minHeight(Gdx.graphics.getHeight()/4);

        //Left table
        rootTable.row();

        Table leftTable = new Table();

        Image leftPlayer = new Image(new Texture(Gdx.files.internal(silhouetteImage)));
        leftPlayer.setScaling(Scaling.fit);
        leftTable.add(leftPlayer);
        leftTable.row();
        Label leftLabel = new Label("WEST", skin);
        leftTable.add(leftLabel);
        rootTable.add(leftTable).expand().fillX();

        //Center Area
        Table centerTable = new Table();
        rootTable.add(centerTable).minWidth(Gdx.graphics.getWidth()/3).expand().fillX();


        //Right Table
        Table rightTable = new Table();

        Image rightPlayer = new Image(new Texture(Gdx.files.internal(silhouetteImage)));
        rightTable.add(rightPlayer);
        rightTable.row();
        Label rightLabel = new Label("EAST", skin);
        rightTable.add(rightLabel);
        rootTable.add(rightTable).expand().fillX();

        //South
        rootTable.row();

        Table southTable = new Table();

        Label southLabel = new Label("SOUTH", skin);
        southTable.add(southLabel);
        rootTable.add(southTable).colspan(3).minHeight(Gdx.graphics.getHeight()/3);

        stage.addActor(rootTable);

        Gdx.input.setInputProcessor(stage);


    }

    public void render(float delta) {
        stage.act(Gdx.graphics.getDeltaTime());
        stage.draw();
    }

    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }


    public void pause() {

    }

    public void resume() {

    }

    public void hide() {

    }

    public void dispose() {
    }
}
