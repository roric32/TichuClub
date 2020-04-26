package com.tichuclub.tichuclub

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.scenes.scene2d.ui.Label
import kotlin.math.roundToInt


class StateGrandCheck(tichu: TichuGame) : TichuState(tichu) {

    override fun nextState() : TichuState {
        return StatePass(tichu)
    }

    override fun act() {

        val playerHand = tichu.players.getCharacterFromPosition(Position.SOUTH).hand

        for (card in playerHand) {
            card.flip()
        }

        val ffgenerator = FreeTypeFontGenerator(Gdx.files.internal("truetypefont/Brewers Bold Lhf.ttf"))
        val parameter = FreeTypeFontParameter()

        val fontWidth = (Gdx.graphics.width/tichu.WORLD_WIDTH)
        parameter.size = fontWidth/2
        parameter.color = Color.BLACK
        parameter.borderColor = Color.BROWN
        parameter.borderWidth = 1f
        val font = ffgenerator.generateFont(parameter)
        val skin = Config.getSkin()

        ffgenerator.dispose()

        skin.get(Label.LabelStyle::class.java).font = font
        val callGrandImage = Label("Call Grand Tichu?", skin)

        callGrandImage.x = Gdx.graphics.width/2f - (callGrandImage.width/2f)
        callGrandImage.y = tichu.stage.camera.project(Vector3(0f, tichu.WORLD_HEIGHT/3f, 0f)).y

        tichu.textStage.addActor(callGrandImage)

    }

}
