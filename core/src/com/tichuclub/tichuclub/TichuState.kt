package com.tichuclub.tichuclub

enum class GameState {
    GAME_START,
    ROUND_START,
    GRAND_CHECK,
    PASS,
    FIRST_TRICK,
    PLAY_TRICKS,
    SCORING,
    GAME_END
}

abstract class TichuState(val tichu: TichuGame) {

    abstract val name: GameState

    abstract fun act() : Unit

    abstract fun nextState() : TichuState

}