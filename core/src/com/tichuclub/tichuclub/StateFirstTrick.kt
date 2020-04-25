package com.tichuclub.tichuclub

class StateFirstTrick(tichu: TichuGame) : TichuState(tichu) {

    override fun nextState() : TichuState {
        return StatePlayTricks(tichu)
    }

    override fun act() : Unit {

    }

}
