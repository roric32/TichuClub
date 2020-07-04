package com.tichuclub.tichuclub

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.Touchable
import com.badlogic.gdx.scenes.scene2d.actions.RunnableAction
import com.badlogic.gdx.scenes.scene2d.ui.Button
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.utils.Timer

class StateFirstTrick(tichu: TichuGame) : TichuState(tichu) {

    override val name = GameState.FIRST_TRICK

    private var currentPlayer = tichu.players.getCharactersAsList().first { it.hand.filter { it.value == 1 }.count() > 0 }
    private var currentCombo: CardCombination? = null
    private var currentZIndex = 0

    private val startYValue = (tichu.WORLD_HEIGHT/2f) - (tichu.CARD_HEIGHT * .5f)
    private var yValue = startYValue
    private var trick = Trick(currentPlayer.position)

    private var playButton : TextButton
    private var passButton : TextButton
    private var humanSelectedCombo : CardCombination? = null

    private var waitingForInput = false
    lateinit var analysis: CardAnalysis

    private val passButtonListener = object: ClickListener() {

        override fun clicked(event: InputEvent, x: Float, y: Float) {
            tichu.players.south.hand.filter{it.isSelected}.map{it.toggleSelect()}
            tichu.players.south.hand.map{it.touchable = Touchable.disabled}
            pass()
            trick.state = TrickState.AI_PLAYING
            waitingForInput = false
            scheduleAct()
        }

    }

    private val playButtonListener = object: ClickListener() {

        override fun clicked(event: InputEvent, x: Float, y: Float) {
            if(humanSelectedCombo != null) {
                trick.playNext(currentPlayer, humanSelectedCombo!!)
                play(humanSelectedCombo!!)
            }
            tichu.players.south.hand.map{it.touchable = Touchable.disabled}
            waitingForInput = false
            scheduleAct()
        }

    }

    override fun nextState() : TichuState {
        return StatePlayTricks(tichu)
    }

    init {
        trick.state = TrickState.AI_PLAYING

        val whiteFont = Config.getFont(tichu.WORLD_WIDTH, 4, Color.WHITE)
        val skin = Config.getSkin()
        skin.get(TextButton.TextButtonStyle::class.java).font = whiteFont
        val buttonWidth = (tichu.FANNED_CARD_WIDTH * 4) * tichu.WIDTH_UNITS
        val buttonHeight = buttonWidth * 0.25f
        playButton = TextButton("PLAY", skin)
        playButton.setSize(buttonWidth, buttonHeight)
        playButton.name = "playButton"
        playButton.isVisible = false
        playButton.addListener(playButtonListener)

        passButton = TextButton("PASS", skin)
        passButton.setSize(buttonWidth, buttonHeight)
        passButton.name = "passButton"
        passButton.isVisible = false
        passButton.addListener(passButtonListener)

        val rootTable = Table()
        rootTable.setFillParent(true)

        val itsinyourhead = Table()
        val filler = Table()

        val buttonTable = Table()
        val width: Float = tichu.textStage.width / 3f
        val leftPad = 0.15.toFloat() * width
        buttonTable.add(playButton).width(buttonWidth).height(buttonHeight)
        buttonTable.add(passButton).width(buttonWidth).height(buttonHeight).padLeft(leftPad)

        rootTable.add(filler).height(Gdx.graphics.height - (tichu.stage.camera.project(Vector3(0f, tichu.WORLD_HEIGHT/3f, 0f)).y)).expandY()
        rootTable.row()
        rootTable.add(buttonTable).expandX()
        rootTable.row()
        rootTable.add(itsinyourhead).height(3.5f * tichu.HEIGHT_UNITS).expandY()

        tichu.textStage.addActor(rootTable)

        for (card in tichu.players.south.hand) {
            card.addListener(object : ClickListener() {

                override fun clicked(event: InputEvent, x: Float, y: Float) {
                    if (trick.state == TrickState.WAITING_FOR_HUMAN) {
                        card.toggleSelect()
                        val selectedCards = tichu.players.south.hand.filter { it.isSelected }
                        val possibleCombo = analysis.findCombination(selectedCards)
                        if (possibleCombo != null) {
                            //If this is the first card of the trick or if it matches the established type
                            if ((!trick.hasType || trick.type == possibleCombo.type) && possibleCombo.getHighestCardValue() > trick.getLastPlayedCombinationValue()) {
                                playButton.isVisible = true
                                humanSelectedCombo = possibleCombo
                            }
                        } else {
                            playButton.isVisible = false
                            humanSelectedCombo = null
                        }
                    }
                }

            })
        }
    }

    override fun act() {

        println("ACT: Start - Current Player is ${currentPlayer.name}")
        if(!currentPlayer.isHuman) {

            passButton.isVisible = false

                trick.state = TrickState.AI_PLAYING
                if (!currentPlayer.calledTichu && !currentPlayer.calledGrand && currentPlayer.hand.count() == 14) {
                    if (currentPlayer.wantsToCall(false)) {
                        currentPlayer.calledTichu = true
                        tichu.showTichuAnimation(TichuType.TICHU, currentPlayer, false)
                        tichu.eventDispatcher.dispatch(TichuEvent(TichuEvents.valueOf("TICHU_CALL_BY_${currentPlayer.name.toUpperCase()}")))
                        tichu.eventDispatcher.dispatch(TichuEvent(TichuEvents.REACT_TO_CALL_BY_PARTNER, tichu.players.getCharacterFromPosition(currentPlayer.partner)))
                    }
                }
                //AI Processes its play.
                if (trick.getNumCombinationsPlayed() == 0) {
                    currentCombo = currentPlayer.playFirst(true)
                } else {
                    currentCombo = currentPlayer.play(trick.type, trick.getLastPlayedCombinationValue(), trick.getLastPlayedCombinationCardCount())
                }

                //Are they passing?
                if (currentCombo == null) {
                    pass()
                } else {
                    //They're going to play cards.
                    trick.playNext(currentPlayer, currentCombo!!)
                    play(currentCombo!!)
                }
                scheduleAct()

        } else {
            println("I think it's the Human's turn.")
            allowHumanTurn()
        }

        println("ACT End.")

    }

    private fun scheduleAct() {
        Timer.schedule(object : Timer.Task() {
            override fun run() {
                act()
            }
        }, 2.5f)
    }

    private fun allowHumanTurn() {
        waitingForInput = true
        trick.state = TrickState.WAITING_FOR_HUMAN
        analysis = tichu.analyzer.getCombinations(tichu.players.south.hand)
        tichu.players.south.hand.map { it.touchable = Touchable.enabled }
        if (trick.getNumCombinationsPlayed() > 0) {
            passButton.isVisible = true
        }
    }

    fun play(combo: CardCombination) {

        println("${currentPlayer.name} played ${combo.cards}")
        playButton.isVisible = false
        val count = combo.cards.count()
        var totalHorizontalPixelsUsed = tichu.CARD_WIDTH
        if(combo.cards.count() > 1) {
            totalHorizontalPixelsUsed = count * tichu.FANNED_CARD_WIDTH
            totalHorizontalPixelsUsed += tichu.CARD_WIDTH
        }
        val halfScreen = tichu.WORLD_WIDTH / 2f
        var xValue = halfScreen - (totalHorizontalPixelsUsed * 0.5f)

        for(card in combo.cards.sortedBy{it.value}) {
            card.zIndex = currentZIndex
            if(!card.isFaceUp) {
                card.flip()
            }
            card.moveTo(xValue, yValue, 0.5f)
            card.toFront()
            xValue += tichu.FANNED_CARD_WIDTH
        }
        currentPlayer.removeCardsFromHand(combo)
        currentZIndex += 1
        yValue -= tichu.FANNED_CARD_WIDTH

        currentPlayer = tichu.players.getCharacterFromPosition(trick.nextPositionToPlay())
        println("${currentPlayer.name} is the next player.")

    }

    fun pass() {

        passButton.isVisible = false
        trick.pass()
        if(trick.numPasses == 3) {
            trick.declareWinner(trick.getLastPlayer())
            currentPlayer = trick.getLastPlayer()
            for(combination in trick.cards) {
                for(card in combination.second.cards) {

                    val coordinates :Pair<Float, Float> = when(currentPlayer.position) {
                        Position.NORTH -> { Pair(card.x, 50f)}
                        Position.WEST -> { Pair(-50f, card.y)}
                        Position.EAST -> { Pair(50f, card.y)}
                        Position.SOUTH -> { Pair(card.x, -50f)}
                    }

                    card.chainActions(
                            card.moveTo(coordinates.first, coordinates.second, 1f, true),
                            object: RunnableAction() {
                                override fun run() {
                                    card.remove()
                                }
                            }
                    )
                }
            }

            Timer.schedule(object : Timer.Task() {
                override fun run() {
                    trick = Trick(currentPlayer.position)
                    currentZIndex = 0
                    waitingForInput = currentPlayer.isHuman
                    yValue = startYValue
                    act()
                }
            }, 1.5f)

        } else {
            println("${currentPlayer.name} passes!")
            currentPlayer = tichu.players.getCharacterFromPosition(trick.nextPositionToPlay())
            println("${currentPlayer.name} is the next player!")
        }

    }

}
