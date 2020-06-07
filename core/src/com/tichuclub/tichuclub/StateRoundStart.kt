package com.tichuclub.tichuclub

import com.badlogic.gdx.scenes.scene2d.actions.*

class StateRoundStart(tichu: TichuGame) : TichuState(tichu) {

    override val name = GameState.ROUND_START

    override fun nextState() : TichuState {
        return StateGrandCheck(tichu)
    }

    override fun act() : Unit {

        tichu.deck.readyDeck()

        val topCard: Card = tichu.deck.get().last()
        topCard.currentSide.setOriginCenter()
        val rotateAction = Actions.rotateBy(360f*3, 0.9f)
        topCard.addAction(SequenceAction(DelayAction(0.7f), rotateAction, rotateAction))

        tichu.shuffleSound.setOnCompletionListener {
            tichu.deck.dealEight()
            tichu.state = nextState()
        }
        tichu.shuffleSound.play()

    }

}
