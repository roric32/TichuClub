package com.tichuclub.tichuclub

import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.scenes.scene2d.Action
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.Touchable
import com.badlogic.gdx.scenes.scene2d.actions.DelayAction
import com.badlogic.gdx.scenes.scene2d.actions.MoveToAction
import com.badlogic.gdx.scenes.scene2d.actions.RunnableAction
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction
import javax.swing.plaf.basic.BasicInternalFrameTitlePane

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

    var lastZIndex: Int = this.zIndex
    var lastCoordinates = Pair(0f, 0f)
    var nextCoordinates = Pair(0f, 0f)
    var isFaceUp : Boolean = false
    var isSelected : Boolean = false
    var passPosition: Position? = null

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
                togglePassSelect()
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

    fun togglePassSelect() {
        if(passPosition == null) {

            //Set the current card coordinates here in case we need to send the card back here after moving it.
            this.lastCoordinates = Pair(this.x, this.y)

            var targetPosition: Position? = null
            //Priority goes W, N, E.
            for(position in Position.values().filter{ it !== Position.SOUTH}) {
                if(!tichu.players.south.pendingPassCards.containsKey(position)) {
                    targetPosition = position
                    break
                }
            }

            if(targetPosition !== null) {
                passPosition = targetPosition
                tichu.players.south.pendingPassCards[targetPosition] = this
                lastZIndex = this.zIndex

                val passState = tichu.state as StatePass
                val moveAction = moveTo(passState.slotPositions[targetPosition]!!.first, passState.slotPositions[targetPosition]!!.second, 0.3f, true)
                val afterMove = (object : RunnableAction() {
                    override fun run() {
                        if(tichu.players.south.pendingPassCards.count() == 3) {
                            val state = tichu.state as StatePass
                            state.togglePassButton()
                        }
                    }
                })
                chainActions(moveAction, afterMove)
            }

        } else {
            val passState = tichu.state as StatePass
            passState.togglePassButton(false)
            moveTo(lastCoordinates.first, lastCoordinates.second, 0.3f)
            this.zIndex = lastZIndex
            tichu.players.south.pendingPassCards.remove(passPosition!!)
            passPosition = null
        }
    }

    open fun moveTo(x: Float, y: Float, duration: Float) {
        val moveAction = MoveToAction()
        moveAction.setPosition(x, y)
        moveAction.duration = duration
        this.addAction(moveAction)
    }

    open fun moveTo(x: Float, y: Float, duration: Float, returnAction: Boolean = true) : MoveToAction {
        val moveAction = MoveToAction()
        moveAction.setPosition(x, y)
        moveAction.duration = duration
        return moveAction
    }

    open fun moveWithDelayAfterward(x: Float, y: Float, moveDuration: Float, delay: Float) {
        val moveAction = moveTo(x, y, moveDuration, true)
        val delayAction = DelayAction(delay)
        chainActions(delayAction, moveAction)
    }

    open fun chainActions(vararg actions: Action) {
        val sequence = SequenceAction()
        for(action in actions) {
            sequence.addAction(action)
        }
        this.addAction(sequence)
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

