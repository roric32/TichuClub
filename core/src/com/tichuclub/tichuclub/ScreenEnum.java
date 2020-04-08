package com.tichuclub.tichuclub;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Screen;

public enum ScreenEnum {

        MAIN_MENU {
            public Screen getScreen(Object... params) {
                return new MainMenuScreen((Game) params[0]);
            }
        },
        GAME {
            public Screen getScreen(Object... params) {
                return new GameScreen((Game) params[0]);
            }
        };

        public abstract Screen getScreen(Object... params);
}
