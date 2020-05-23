package com.tichuclub.tichuclub

class StatePass(tichu: TichuGame) : TichuState(tichu) {

    override fun nextState() : TichuState {
        return StateFirstTrick(tichu)
    }

    override fun act() : Unit {
        tichu.deck.deal(tichu)
        tichu.state = nextState()
    }

}
