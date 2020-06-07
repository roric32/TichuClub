package com.tichuclub.tichuclub

import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.InputListener

class CardInputListener : InputListener() {

    override fun touchDown(event: InputEvent, x: Float, y: Float, pointer: Int, button: Int): Boolean {
        super.touchDown(event, x, y, pointer, button)
        val touchedCard = event.listenerActor as Card
        touchedCard.touchAction()
        return true
    }

}