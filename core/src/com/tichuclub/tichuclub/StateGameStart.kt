package com.tichuclub.tichuclub

class StateGameStart(tichu: TichuGame) : TichuState(tichu) {

    override val name = GameState.GAME_START

    override fun nextState() : TichuState {
        return StateRoundStart(tichu)
    }

    override fun act() : Unit {
        tichu.state = nextState()
        tichu.state.act()
    }

}