package com.tichuclub.tichuclub

class StateFirstTrick(tichu: TichuGame) : TichuState(tichu) {

    override fun nextState() : TichuState {
        return StatePlayTricks(tichu)
    }

    override fun act() : Unit {

        for(card in tichu.players.south.hand) {
            card.toFront()
            if(!card.isFaceUp) {
                card.flip()
            }
        }

    }

}
