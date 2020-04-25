package com.tichuclub.tichuclub

import com.tichuclub.tichuclub.TichuEvent as TichuEvent
import com.tichuclub.tichuclub.TichuGame as TichuGame

interface TichuEventListener {
    val event: TichuEvent
    val game: TichuGame
    fun respond()
}