package com.tichuclub.tichuclub

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.actions.MoveToAction

class StateRoundStart(tichu: TichuGame) : TichuState(tichu) {

    override fun nextState() : TichuState {
        return StateGrandCheck(tichu)
    }

    override fun act() : Unit {

        tichu.deck.readyDeck(tichu)

        tichu.shuffleSound.setOnCompletionListener {
            tichu.deck.deal(tichu)
            tichu.state = nextState()
        }
        tichu.shuffleSound.play()
    }

}
