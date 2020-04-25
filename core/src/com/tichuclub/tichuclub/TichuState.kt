package com.tichuclub.tichuclub

abstract class TichuState(val tichu: TichuGame) {

    abstract fun act() : Unit

    abstract fun nextState() : TichuState

}