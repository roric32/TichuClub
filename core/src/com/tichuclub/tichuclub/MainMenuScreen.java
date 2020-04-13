package com.tichuclub.tichuclub;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TiledDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class MainMenuScreen implements Screen {

    private Stage stage;
    private String skinFile = "uiskin.json";
    private String backgroundAsset = "redback.png";
    private String middleBackgroundAsset = "whiteback.png";
    private String dragon = "dragon.png";
    private String bars = "bars.png";
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
        FreeTypeFontGenerator ffgenerator = new FreeTypeFontGenerator(Gdx.files.internal("truetypefont/Brewers Bold Lhf.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 40;
        parameter.color = Color.WHITE;
        BitmapFont font = ffgenerator.generateFont(parameter);
        ffgenerator.dispose();

        Texture rootBackground = new Texture(Gdx.files.internal(backgroundAsset));
        TiledDrawable tiler = new TiledDrawable(new TextureRegion(rootBackground, 0, 0, 250, 250));

        rootTable.setBackground(tiler);

        //Declare middle Table
        Texture middleBackground = new Texture(Gdx.files.internal(middleBackgroundAsset));
        TiledDrawable middleTile = new TiledDrawable(new TextureRegion(middleBackground, 0, 0, 250, 250));

        Table middleTable = new Table();
        skin.get(TextButton.TextButtonStyle.class).font = font;
        TextButton playTichu = new TextButton("New Game", skin);

        playTichu.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                ((Game)Gdx.app.getApplicationListener()).setScreen(ScreenOverlord.get(ScreenEnum.GAME));
            }
        });

        //TODO: continueGame logic
        TextButton continueGame = new TextButton("Continue", skin);

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

        float width = stage.getWidth()/2.25f;
        float height = stage.getHeight()/10;
        float leftPad = (float) 0.15 * width;
        float rightPad = (float) 0.15 * width;
        float bottomPad = (float) 0.45 * height;

        Image dragonImage = new Image(new Texture(dragon));
        dragonImage.setScaling(Scaling.fit);

        Image logo = new Image(new Texture("logo.png"));
        logo.setScaling(Scaling.fit);
        middleTable.add(logo).width(width).padLeft(leftPad).padRight(rightPad).padBottom(bottomPad);
        middleTable.row();
        middleTable.add(playTichu).width(width).height(height).padLeft(leftPad).padRight(rightPad).padBottom(bottomPad);
        middleTable.row();

        if(!((TichuClub) this.game).isGameStarted()) {
            continueGame.setDisabled(true);
            continueGame.setTouchable(Touchable.disabled);
            continueGame.getLabel().setColor(Color.DARK_GRAY);
        }

        middleTable.add(continueGame).width(width).height(height).padLeft(leftPad).padRight(rightPad).padBottom(bottomPad);
        middleTable.row();

        middleTable.add(quit).width(width).height(height).padLeft(leftPad).padRight(rightPad).padBottom(bottomPad);

        Image topBar = new Image(new Texture(bars));

        Image bottomBar = new Image(new Texture(bars));

        rootTable.add(topBar).colspan(2).expandX().fillX();
        rootTable.row();
        rootTable.add(dragonImage).expandY().fillY().padRight(20).padTop(20).padBottom(20);
        rootTable.add(middleTable).align(Align.center).expandY().fillY().padTop(20).padBottom(20);
        rootTable.row();
        rootTable.add(bottomBar).colspan(2).expandX().fillX();

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
        stage.dispose();
    }
}
