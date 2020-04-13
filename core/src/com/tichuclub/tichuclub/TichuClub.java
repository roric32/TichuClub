package com.tichuclub.tichuclub;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;

public class TichuClub extends Game {

	private boolean gameStarted = false;

	@Override
	public void create () {
		ScreenOverlord.initialize(this);
	    this.setScreen(ScreenOverlord.get(ScreenEnum.MAIN_MENU));
	}

	public boolean isGameStarted() {
		return this.gameStarted;
	}

	public void setGameStarted(boolean value) {
		this.gameStarted = value;
	}

}
