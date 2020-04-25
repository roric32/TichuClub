package com.tichuclub.tichuclub

class StateGameEnd(tichu: TichuGame) : TichuState(tichu) {

    override fun nextState() : TichuState {
        return StateGameStart(tichu)
    }

    override fun act() : Unit {

    }

}
