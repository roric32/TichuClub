package com.tichuclub.tichuclub

import java.util.Timer
import kotlin.math.roundToInt


class StateGrandCheck(tichu: TichuGame) : TichuState(tichu) {

    override fun nextState() : TichuState {
        return StatePass(tichu)
    }

    override fun act() {
        var x = 0
        var y = 0

        val playerHand = tichu.players.getCharacterFromPosition(Position.SOUTH).hand

        for (card in playerHand.sortedBy{it.value}) {
            card.flip()
        }

    }

}
