package com.tichuclub.tichuclub

import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.Touchable
import com.badlogic.gdx.scenes.scene2d.actions.MoveToAction

enum class Suit {
    SWORDS, STARS, EMERALDS, PAGODAS, NONE;
    fun getAbbreviation(suit: Suit) : String {
        var abbreviation = ""
        when(suit) {
            SWORDS -> { abbreviation = "W" }
            EMERALDS -> { abbreviation = "E" }
            STARS -> { abbreviation = "S" }
            PAGODAS -> { abbreviation = "P" }
            NONE -> {}
        }
        return abbreviation
    }
}

abstract class Card(open var suit: Suit, open var value: Int, open var frontImage: Sprite, open var backImage: Sprite, open val tichu: TichuGame) : Actor() {

    abstract var currentSide : Sprite

    var isFaceUp : Boolean = false
    var isSelected : Boolean = false

    private val SLIDE_AMOUNT : Float = 1f

    fun flip() {

        if(currentSide == backImage) {
            frontImage.setSize(currentSide.width, currentSide.height)
            frontImage.setPosition(currentSide.x, currentSide.y)
            currentSide = frontImage
            isFaceUp = true
        } else {
            backImage.setSize(currentSide.width, currentSide.height)
            backImage.setPosition(currentSide.x, currentSide.y)
            currentSide = backImage
            isFaceUp = false
        }

    }

    fun touchAction() {

        when(tichu.state.name) {
            GameState.GAME_START -> {}
            GameState.ROUND_START -> {}
            GameState.GRAND_CHECK -> {}
            GameState.PASS -> {
                toggleSelect()
            }
            GameState.FIRST_TRICK -> {
            }
            GameState.PLAY_TRICKS -> {}
            GameState.SCORING -> {}
            GameState.GAME_END -> {}
        }

    }

    fun select() {
        val moveAction = MoveToAction()
        moveAction.x = currentSide.x
        moveAction.y = currentSide.y + SLIDE_AMOUNT
        moveAction.duration = 0.1f
        this.addAction(moveAction)
        this.isSelected = true
    }

    fun deselect() {
        val moveAction = MoveToAction()
        moveAction.x = currentSide.x
        moveAction.y = currentSide.y - SLIDE_AMOUNT
        moveAction.duration = 0.1f
        this.addAction(moveAction)
        this.isSelected = false
    }

    fun toggleSelect() {
        if(isSelected) deselect() else select()
    }

}

class NumericCard(override var suit: Suit, override var value: Int, override var frontImage: Sprite, override var backImage: Sprite, override val tichu: TichuGame) : Card(suit, value, frontImage, backImage, tichu) {

    override var currentSide : Sprite = backImage

    init {
        setBounds(currentSide.x,currentSide.y,currentSide.width,currentSide.height)
        touchable = Touchable.enabled
        this.name = suit.getAbbreviation(suit) + value.toString()
    }

    override fun draw(batch: Batch?, parentAlpha: Float) {
        if(batch !== null) {
            batch.draw(currentSide, currentSide.x, currentSide.y, currentSide.originX, currentSide.originY, currentSide.width, currentSide.height, currentSide.scaleX, currentSide.scaleY, currentSide.rotation)
        }
    }

    override fun setRotation(degrees: Float) {
        currentSide.rotation = degrees
        super.setRotation(degrees)
    }

    override fun rotateBy(amountInDegrees: Float) {
        currentSide.rotation += amountInDegrees
    }

    override fun setSize(width: Float, height: Float) {
        currentSide.setSize(width, height)
        super.setSize(width, height)
    }

    override fun setPosition(x: Float, y: Float) {
        currentSide.setPosition(x, y)
        super.setPosition(x, y)
    }

    override fun positionChanged() {
        super.positionChanged()
        currentSide.setPosition(x, y)
    }

    override fun toString() : String {

        val stringValue : String = when(value) {
            11 -> "Jack"
            12 -> "Queen"
            13 -> "King"
            14 -> "Ace"
            else -> value.toString()
        }

        return "$stringValue of $suit"
    }

}

class SpecialCard(override var suit: Suit, override var value: Int, override var frontImage: Sprite, override var backImage: Sprite, override val tichu: TichuGame, var specialName: String) : Card(suit, value, frontImage, backImage, tichu) {

    override var currentSide : Sprite = backImage

    init {
        setBounds(currentSide.x,currentSide.y,currentSide.width,currentSide.height)
        touchable = Touchable.enabled
        this.name = specialName
    }

    override fun draw(batch: Batch?, parentAlpha: Float) {
        //super.draw(batch, parentAlpha)
        if(batch !== null) {
            batch.draw(currentSide, currentSide.x, currentSide.y, currentSide.originX, currentSide.originY, currentSide.width, currentSide.height, currentSide.scaleX, currentSide.scaleY, currentSide.rotation)
            //currentSide.draw(batch, parentAlpha)
        }
    }

    override fun setRotation(degrees: Float) {
        currentSide.rotation = degrees
        super.setRotation(degrees)
    }

    override fun rotateBy(amountInDegrees: Float) {
        currentSide.rotation += amountInDegrees
    }

    override fun setSize(width: Float, height: Float) {
        currentSide.setSize(width, height)
        super.setSize(width, height)
    }

    override fun setPosition(x: Float, y: Float) {
        currentSide.setPosition(x, y)
        super.setPosition(x, y)
    }

    override fun positionChanged() {
        super.positionChanged()
        currentSide.setPosition(x, y)
    }

    override fun toString() : String {
        return "The $specialName"
    }

}

