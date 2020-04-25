package com.tichuclub.tichuclub;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Screen;

public class ScreenOverlord {

    static Game game = null;

    public static void initialize(Game game) {
        ScreenOverlord.game = game;
    }

    public static Screen get(ScreenEnum screen) {
        return screen.getScreen(game);
    }

}
