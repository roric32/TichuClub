package com.tichuclub.tichuclub

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.utils.Timer
import com.badlogic.gdx.utils.Timer.Task
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory


class StateGrandCheck(tichu: TichuGame) : TichuState(tichu) {

    override val name = GameState.GRAND_CHECK

    private val debug = false
    private val BANNER_DELAY : Float = 3.7f

    override fun nextState() : TichuState {
        return StatePass(tichu)
    }

    override fun act() {

        val playerHand = tichu.players.getCharacterFromPosition(Position.SOUTH).hand

        for (card in playerHand) {
            card.flip()
        }

        val font = Config.getFont(tichu.WORLD_WIDTH, Color.BLACK, 1f, Color.BROWN)
        val skin = Config.getSkin()

        skin.get(Label.LabelStyle::class.java).font = font
        val callGrandImage = Label("Call Grand Tichu?", skin)

        callGrandImage.x = Gdx.graphics.width/2f - (callGrandImage.width/2f)
        callGrandImage.y = tichu.stage.camera.project(Vector3(0f, tichu.WORLD_HEIGHT/3f, 0f)).y

        tichu.textStage.addActor(callGrandImage)

        val rootTable = Table()
        rootTable.setFillParent(true)

        val width: Float = tichu.textStage.width / 3f
        val height: Float = width / 4f

        val leftPad = 0.15.toFloat() * width

        val whiteFont = Config.getFont(tichu.WORLD_WIDTH, 4, Color.WHITE)
        skin.get(TextButtonStyle::class.java).font = whiteFont

        val buttonTextFile = Gdx.files.internal("buttonText.json").readString()

        val moshi = Moshi.Builder()
                .add(KotlinJsonAdapterFactory())
                .build()

        val jsonAdapter : JsonAdapter<GrandButtons> = moshi.adapter(GrandButtons::class.java)
        val possibleTexts : GrandButtons = jsonAdapter.fromJson(buttonTextFile)!!

        var handTotal = 0
        tichu.players.south.hand.map{handTotal += it.value}

        val confidence : Confidence

        when(handTotal) {
            in 15..32 -> {
                confidence = Confidence.LOW
            }
            in 33..65 -> {
                confidence = Confidence.NORMAL
            }
            in 66..113 -> {
                confidence = Confidence.HIGH
            }
            else -> {
                confidence = Confidence.NORMAL
            }
        }

        val yesText = possibleTexts.buttons.filter{it.confidence.name == confidence.toString()}.filter{it.type == "YES"}.random().text
        val noText = possibleTexts.buttons.filter{it.confidence.name == confidence.toString()}.filter{it.type == "NO"}.random().text

        val yesButton = TextButton(yesText, skin)
        val noButton = TextButton(noText, skin)

        yesButton.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent, x: Float, y: Float) {
                //Hide our buttons.
                callGrandImage.remove()
                noButton.remove()
                yesButton.remove()

                tichu.showTichuAnimation(TichuType.GRAND_TICHU, tichu.players.south)

                //Let the opponents react to the player's decision to call Grand.
                tichu.players.south.calledGrand = true

                Timer.schedule(object : Task() {
                    override fun run() {
                        tichu.eventDispatcher.dispatch(TichuEvent(TichuEvents.GRAND_TICHU_CALL_BY_PLAYER))
                        processAIGrandCalls()

                        var duration = tichu.players.numPlayersThatCalledGrand(true) * BANNER_DELAY
                        if(duration == 0f) duration = 1f

                        Timer.schedule(object : Task() {
                            override fun run() {
                                tichu.state = nextState()
                                tichu.state.act()
                            }
                        }, duration)
                    }
                }, BANNER_DELAY)


            }
        })

        noButton.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent, x: Float, y: Float) {
                //Hide our buttons.
                callGrandImage.remove()
                noButton.remove()
                yesButton.remove()

                tichu.players.south.calledGrand = false
                processAIGrandCalls()

                var duration = tichu.players.numPlayersThatCalledGrand(true) * BANNER_DELAY

                //Just to make it feel a little more natural.
                if(duration == 0f) duration = 1f

                Timer.schedule(object : Task() {
                    override fun run() {
                        tichu.state = nextState()
                        tichu.state.act()
                    }
                }, duration)

            }
        })

        val itsinyourhead = Table()
        val filler = Table()

        val buttonTable = Table()
        val buttonWidth = (tichu.FANNED_CARD_WIDTH * 6) * tichu.WIDTH_UNITS
        val buttonHeight = buttonWidth * 0.25f
        buttonTable.add(yesButton).width(buttonWidth).height(buttonHeight)
        buttonTable.add(noButton).width(buttonWidth).height(buttonHeight).padLeft(leftPad)

        rootTable.add(filler).height(Gdx.graphics.height - callGrandImage.y).expandY()
        rootTable.row()
        rootTable.add(buttonTable).expandX()
        rootTable.row()
        rootTable.add(itsinyourhead).height(3.5f * tichu.HEIGHT_UNITS).expandY()
        rootTable.debug = debug

        tichu.textStage.addActor(rootTable)
        Gdx.input.inputProcessor = tichu.textStage

    }

    fun processAIGrandCalls() {

        val playersWhoWantToCallGrand = tichu.getAIGrandCalls()

        for ((index, player) in playersWhoWantToCallGrand.withIndex()) {

            //They'll play nicely and won't call grand over each other.
            if (!tichu.players.getCharacterFromPosition(player.partner).calledGrand) {

                player.calledGrand = true
                val charName: String = player.name

                val duration = index * BANNER_DELAY

                Timer.schedule(object : Task() {
                    override fun run() {
                        val useDoubleSound = (tichu.players.numPlayersThatCalledGrand() > 1)
                        tichu.showTichuAnimation(TichuType.GRAND_TICHU, player, useDoubleSound)
                    }
                }, duration)

                val newEvent = TichuEvents.valueOf("GRAND_TICHU_CALL_BY_${charName.toUpperCase()}")
                tichu.eventDispatcher.dispatch(TichuEvent(newEvent))
            }
        }

    }

    }
