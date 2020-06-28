package com.tichuclub.tichuclub

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.Touchable
import com.badlogic.gdx.scenes.scene2d.actions.RunnableAction
import com.badlogic.gdx.scenes.scene2d.ui.Button
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.utils.Timer

class StatePass(tichu: TichuGame) : TichuState(tichu) {

    var waitingForInput : Boolean = false
    var slotPositions = HashMap<Position, Pair<Float, Float>>()
    lateinit var leftImage : Image
    lateinit var middleImage : Image
    lateinit var rightImage : Image
    lateinit var passButton : Button

    override val name = GameState.PASS

    override fun nextState() : TichuState {
        return StateFirstTrick(tichu)
    }

    override fun act() {

        if(!waitingForInput) {

            tichu.deck.deal()

            val duration = 1f

            Timer.schedule(object : Timer.Task() {
                override fun run() {
                    for ((index, card) in tichu.players.south.hand.withIndex()) {
                        card.toFront()
                        if (!card.isFaceUp) {
                            card.flip()
                        }
                    }

                    val image = Texture(Gdx.files.internal("border.png"))
                    leftImage = Image(image)
                    middleImage = Image(image)
                    rightImage = Image(image)

                    val slots = listOf(leftImage, middleImage, rightImage)
                    for(slot in slots) {
                        slot.setSize(tichu.CARD_WIDTH, tichu.CARD_HEIGHT)
                    }

                    //Position three empty "slot" images relative to the position of the 2nd, 6th (and a bit), and 11th cards.
                    val leftCardX = tichu.players.south.hand[1].x
                    val rightCardX = tichu.players.south.hand[12].x
                    val middleCardX = tichu.players.south.hand[6].x + .3f

                    slotPositions[Position.NORTH] = Pair(middleCardX, 5f)
                    slotPositions[Position.EAST] = Pair(rightCardX, 4f)
                    slotPositions[Position.WEST] = Pair(leftCardX, 4f)

                    leftImage.setPosition(leftCardX, 4f)
                    middleImage.setPosition(middleCardX, 5f)
                    rightImage.setPosition(rightCardX, 4f)
                    tichu.stage.addActor(leftImage)
                    tichu.stage.addActor(middleImage)
                    tichu.stage.addActor(rightImage)

                    leftImage.toBack()
                    middleImage.toBack()
                    rightImage.toBack()

                    for (card in tichu.players.south.hand) {
                        card.addListener(CardInputListener())
                    }

                    //Create the "Pass" button. It will be hidden at first until all three slots are filled.
                    val whiteFont = Config.getFont(tichu.WORLD_WIDTH, 4, Color.WHITE)
                    val skin = Config.getSkin()
                    skin.get(TextButton.TextButtonStyle::class.java).font = whiteFont
                    val buttonWidth = (tichu.FANNED_CARD_WIDTH * 4) * tichu.WIDTH_UNITS
                    val buttonHeight = buttonWidth * 0.25f
                    passButton = TextButton("PASS", skin)
                    passButton.setSize(buttonWidth, buttonHeight)
                    passButton.setPosition(Gdx.graphics.width/2f - passButton.width/2f, Gdx.graphics.height/2f)
                    passButton.name = "passButton"
                    passButton.isVisible = false

                    passButton.addListener(object: ClickListener() {
                        override fun clicked(event: InputEvent, x: Float, y: Float) {
                            togglePassButton(false)
                            passCards()
                        }
                    })

                    tichu.textStage.addActor(passButton)
                }
            }, duration)

            waitingForInput = true

        }

    }

    fun togglePassButton() {
        val button = tichu.textStage.actors.find { it.name == "passButton" }
        if(button != null) {
            button.isVisible = !button.isVisible
        }
    }

    fun togglePassButton(newValue: Boolean) {
        val button = tichu.textStage.actors.find { it.name == "passButton" }
        if(button != null) {
            button.isVisible = newValue
        }
    }

    fun passCards() {
        tichu.players.getAICharacters().map{it.evaluatePass(it.hand)}
        for(character in tichu.players.getCharactersAsList()) {
            for(card in character.pendingPassCards) {
                val x = tichu.players.getCharacterFromPosition(card.key).pendingPassCards.get(character.position)!!.x
                val y = tichu.players.getCharacterFromPosition(card.key).pendingPassCards.get(character.position)!!.y
                card.value.nextCoordinates = Pair(x, y)
                character.hand.remove(card.value)
                tichu.players.getCharacterFromPosition(card.key).passedCards.add(card.value)
            }
        }

        for(character in tichu.players.getCharactersAsList()) {
            character.pendingPassCards.clear()
            for(card in character.passedCards) {
                val moveAction = card.moveTo(card.nextCoordinates.first, card.nextCoordinates.second, 1.5f, true)
                val flipAction = (object: RunnableAction() {
                    override fun run() {
                        if(!card.isFaceUp) {
                            card.flip()
                        }
                    }
                })
                card.chainActions(moveAction, flipAction)
            }
        }

    }

}
