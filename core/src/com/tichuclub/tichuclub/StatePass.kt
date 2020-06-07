package com.tichuclub.tichuclub

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.utils.Timer

class StatePass(tichu: TichuGame) : TichuState(tichu) {

    var waitingForInput : Boolean = false

    override val name = GameState.PASS

    override fun nextState() : TichuState {
        return StateFirstTrick(tichu)
    }

    override fun act() {

        if(!waitingForInput) {

            tichu.deck.deal()

            val duration = 1f

            Timer.schedule(object : Timer.Task() {
                override fun run() {
                    for (card in tichu.players.south.hand) {
                        card.toFront()
                        if (!card.isFaceUp) {
                            card.flip()
                        }
                    }

                    for (card in tichu.players.south.hand) {
                        card.addListener(CardInputListener())
                    }


                }
            }, duration)

            Gdx.input.inputProcessor = tichu.stage
            waitingForInput = true

        }
    }

}
