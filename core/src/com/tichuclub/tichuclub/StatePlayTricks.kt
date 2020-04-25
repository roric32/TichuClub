package com.tichuclub.tichuclub

class StatePlayTricks(tichu: TichuGame) : TichuState(tichu) {

    override fun nextState() : TichuState {
        return StateScoring(tichu)
    }

    override fun act() : Unit {

    }

}
