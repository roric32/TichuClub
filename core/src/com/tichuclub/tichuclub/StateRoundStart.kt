package com.tichuclub.tichuclub

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.actions.*
import com.badlogic.gdx.scenes.scene2d.ui.Image

class StateRoundStart(tichu: TichuGame) : TichuState(tichu) {

    override fun nextState() : TichuState {
        return StateGrandCheck(tichu)
    }

    override fun act() : Unit {

        tichu.deck.readyDeck(tichu)

        val topCard: Card = tichu.deck.get().last()
        topCard.currentSide.setOriginCenter()
        val rotateAction = Actions.rotateBy(360f*3, 0.9f)
        topCard.addAction(SequenceAction(DelayAction(0.7f), rotateAction, rotateAction))

        tichu.shuffleSound.setOnCompletionListener {
            tichu.deck.deal(tichu)
            tichu.state = nextState()
        }
        tichu.shuffleSound.play()

    }

}
