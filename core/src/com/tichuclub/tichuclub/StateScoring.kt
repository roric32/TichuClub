package com.tichuclub.tichuclub

class StateScoring(tichu: TichuGame) : TichuState(tichu) {

    override val name = GameState.SCORING

    override fun nextState() : TichuState {
        return StateGameEnd(tichu)
    }

    override fun act() : Unit {

    }

}
