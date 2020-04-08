package com.tichuclub.tichuclub;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TiledDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.graphics.GL20;

public class MainMenuScreen implements Screen {

    private SpriteBatch batch;
    private Texture texture;
    private OrthographicCamera camera;
    private Stage stage;
    private String skinFile = "uiskin.json";
    private String backgroundAsset = "redback.png";
    private String middleBackgroundAsset = "blueback.png";
    private Game game;

    public MainMenuScreen(Game game) {
        this.game = game;
        stage = new Stage(new ScreenViewport());

        AssetManager assetManager = new AssetManager();
        assetManager.load(skinFile, Skin.class);
        assetManager.finishLoading();

        //Declare root Table
        Table rootTable = new Table();
        rootTable.setFillParent(true);

        Skin skin = assetManager.get(skinFile);
        rootTable.setSkin(skin);
        Texture rootBackground = new Texture(Gdx.files.internal(backgroundAsset));
        TiledDrawable tiler = new TiledDrawable(new TextureRegion(rootBackground, 0, 0, 250, 250));

        rootTable.setBackground(tiler);
        rootTable.setDebug(true);

        //Declare middle Table
        Texture middleBackground = new Texture(Gdx.files.internal(middleBackgroundAsset));
        TiledDrawable middleTile = new TiledDrawable(new TextureRegion(middleBackground, 0, 0, 250, 250));

        Table middleTable = new Table();
        middleTable.setBackground(middleTile);
        TextButton playTichu = new TextButton("New Game", skin);

        playTichu.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                ((Game)Gdx.app.getApplicationListener()).setScreen(ScreenOverlord.get(ScreenEnum.GAME));
            }
        });

        //TODO: continueGame logic
        TextButton continueGame = new TextButton("Continue", skin);

        boolean gameExists = false;

        continueGame.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                ((Game)Gdx.app.getApplicationListener()).setScreen(ScreenOverlord.get(ScreenEnum.GAME));
            }
        });

        TextButton quit = new TextButton("Quit", skin);
        quit.addListener( new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.exit();
            }
        } );

        middleTable.add(playTichu).width(400).height(100).padLeft(100).padRight(100).padBottom(25);
        middleTable.row();
        if(gameExists) {
            middleTable.add(continueGame).width(400).height(100).padLeft(100).padRight(100).padBottom(25);
            middleTable.row();
        }
        middleTable.add(quit).width(400).height(100).padLeft(100).padRight(100).padBottom(25);

        rootTable.add(middleTable).align(Align.center).expand().center().fillY();

        stage.addActor(rootTable);

        Gdx.input.setInputProcessor(stage);


    }

    public void show() {

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
        batch.dispose();
        stage.dispose();
        texture.dispose();
    }
}
