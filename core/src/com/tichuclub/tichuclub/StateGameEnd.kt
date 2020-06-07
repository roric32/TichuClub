package com.tichuclub.tichuclub

class StateGameEnd(tichu: TichuGame) : TichuState(tichu) {

    override val name = GameState.GAME_END

    override fun nextState() : TichuState {
        return StateGameStart(tichu)
    }

    override fun act() : Unit {

    }

}
