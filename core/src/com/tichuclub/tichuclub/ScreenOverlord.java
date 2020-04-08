package com.tichuclub.tichuclub;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Screen;

public class ScreenOverlord {

    static Game game = null;
    static Screen loginScreen;
    static Screen gameScreen;


    public static void initialize(Game game) {
        ScreenOverlord.game = game;
        loginScreen = new MainMenuScreen(game);
        gameScreen = new GameScreen(game);
    }

    public static Screen get(ScreenEnum screen) {
        return screen.getScreen(game);
    }

}
