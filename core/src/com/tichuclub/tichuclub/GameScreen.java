package com.tichuclub.tichuclub;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.TiledDrawable;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.FillViewport;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.ScalingViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.util.ArrayList;

import static com.badlogic.gdx.Application.ApplicationType.Android;

public class GameScreen implements Screen {

    private boolean debug = true;
    private int WORLD_HEIGHT = 16;
    private float ppu = Gdx.graphics.getHeight() / (float) WORLD_HEIGHT;
    private int WORLD_WIDTH = Gdx.graphics.getWidth() / (int) ppu;
    private Game game;
    private TichuGame tichu;
    private Stage stage;
    private String skinFile = "uiskin.json";
    private String backgroundAsset = "woodback.png";
    private String silhouetteImage;
    private String cardAtlasFile;
    private TextureAtlas atlas;
    private OrthographicCamera camera;
    private TiledDrawable background;
    private Viewport viewport;
    private OrthographicCamera backgroundCamera;
    private ScreenViewport backgroundViewport;
    private Stage backgroundStage;


    public GameScreen(Game game) {
        this.game = game;

        //Set up the resolution and stuff.
        boolean isAndroid = (Gdx.app.getType() == Android);
        silhouetteImage = (isAndroid) ? "silhouette-small.png" : "silhouette.png";
        cardAtlasFile = (isAndroid) ? "cards/smallcards.atlas" : "cards/smallcards.atlas";
        camera = new OrthographicCamera(WORLD_WIDTH, WORLD_HEIGHT);
        viewport = new FitViewport(WORLD_WIDTH, WORLD_HEIGHT, camera);
        stage = new Stage(viewport);

        backgroundCamera = new OrthographicCamera();
        backgroundViewport = new ScreenViewport(backgroundCamera);
        backgroundStage = new Stage(backgroundViewport);

        AssetManager assetManager = new AssetManager();
        assetManager.load(skinFile, Skin.class);
        assetManager.finishLoading();
        Texture woodBackground = new Texture(Gdx.files.internal(backgroundAsset));
        background = new TiledDrawable(new TextureRegion(woodBackground, 0, 0, 600, 392));

        //Declare root Table
        Table rootTable = new Table();
        rootTable.setFillParent(true);

        Skin skin = assetManager.get(skinFile);
        rootTable.setSkin(skin);

        rootTable.setBackground(background);

        rootTable.setName("rootTable");
        backgroundStage.addActor(rootTable);

        Gdx.input.setCatchBackKey(true);
        Gdx.input.setInputProcessor(stage);

        this.atlas = new TextureAtlas(Gdx.files.internal(cardAtlasFile));

        tichu = new TichuGame(WORLD_WIDTH, WORLD_HEIGHT, this.stage, atlas);

        //TODO: Move this player setup to the new game screen.
        PlayerOverlord players = new PlayerOverlord();

        players.addPlayer(Position.NORTH, new Zach("Zach", tichu));
        players.addPlayer(Position.EAST, new Nate("Nate", tichu));
        players.addPlayer(Position.SOUTH, new Player("Brandon", tichu));
        players.addPlayer(Position.WEST, new Thong("Thong", tichu));

        tichu.setUp(players);
        tichu.play();

    }

    public void show() {
        Gdx.input.setInputProcessor(this.stage);
    }

    public void render(float delta) {
        Gdx.gl20.glClearColor(0, 0, 0, 1);
        Gdx.gl20.glClear(GL20.GL_COLOR_BUFFER_BIT);
        backgroundStage.getViewport().apply();
        backgroundStage.act();
        backgroundStage.draw();
        stage.getViewport().apply();
        stage.act(Gdx.graphics.getDeltaTime());
        stage.draw();
        if (Gdx.input.isKeyPressed(Input.Keys.BACK)) {
            this.hide();
            game.setScreen(ScreenOverlord.get(ScreenEnum.MAIN_MENU));
        }
    }

    public void resize(int width, int height) {
        backgroundStage.getViewport().update(width, height, true);
        backgroundStage.getBatch().setProjectionMatrix(backgroundCamera.combined);
        stage.getViewport().update(width, height, true);
        stage.getBatch().setProjectionMatrix(camera.combined);
    }


    public void pause() {

    }

    public void resume() {

    }

    public void hide() {
    }

    public void dispose() {
        this.stage.dispose();
    }
}
