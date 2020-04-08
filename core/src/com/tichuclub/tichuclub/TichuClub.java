package com.tichuclub.tichuclub;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;

public class TichuClub extends Game {

    private ScreenOverlord screenOverlord;

	@Override
	public void create () {
	    this.setScreen(ScreenOverlord.get(ScreenEnum.MAIN_MENU));
	}

}
