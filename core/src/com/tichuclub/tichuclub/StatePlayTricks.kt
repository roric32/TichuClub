package com.tichuclub.tichuclub

class StatePlayTricks(tichu: TichuGame) : TichuState(tichu) {

    override val name = GameState.PLAY_TRICKS

    override fun nextState() : TichuState {
        return StateScoring(tichu)
    }

    override fun act() : Unit {

    }

}
