package com.tichuclub.tichuclub

class StateFirstTrick(tichu: TichuGame) : TichuState(tichu) {

    override val name = GameState.FIRST_TRICK

    override fun nextState() : TichuState {
        return StatePlayTricks(tichu)
    }

    override fun act() : Unit {


    }

}
