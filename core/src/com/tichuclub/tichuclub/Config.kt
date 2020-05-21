package com.tichuclub.tichuclub

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import kotlin.math.roundToInt

class Config {
    companion object {
        const val skinFile = "uiskin.json"
        fun getSkin() : Skin {
            val assetManager = AssetManager()
            assetManager.load(skinFile, Skin::class.java)
            assetManager.finishLoading()
            return assetManager.get(skinFile)
        }

        fun getFont(width: Int, size: Int, color: Color, font: String = "truetypefont/Brewers Bold Lhf.ttf") : BitmapFont {
            val ffgenerator = FreeTypeFontGenerator(Gdx.files.internal(font))
            val parameter = FreeTypeFontGenerator.FreeTypeFontParameter()
            val fontWidth = Gdx.graphics.width/width
            parameter.size = fontWidth/size
            parameter.color = color
            val generatedFont = ffgenerator.generateFont(parameter)
            ffgenerator.dispose()
            return generatedFont
        }

        fun getFont(width: Int, color: Color, borderWidth: Float, borderColor: Color) : BitmapFont {
            val ffgenerator = FreeTypeFontGenerator(Gdx.files.internal("truetypefont/Brewers Bold Lhf.ttf"))
            val parameter = FreeTypeFontGenerator.FreeTypeFontParameter()
            val fontWidth = Gdx.graphics.width/width
            parameter.size = fontWidth/2
            parameter.color = color
            parameter.borderColor = borderColor
            parameter.borderWidth = borderWidth
            val generatedFont = ffgenerator.generateFont(parameter)
            ffgenerator.dispose()
            return generatedFont
        }

        fun getCharacterSprites(character: Character) : Map<Expression, Sprite> {

            val characterMap = HashMap<Expression, Sprite>()

            //TODO: Actual portrait art. For now "Zach" always just refers to the generic silhouette image.
            val expressionsMap = TextureAtlas(Gdx.files.internal("expressions.atlas"))
            for(expression: Expression in Expression.values()) {
                characterMap[expression] = expressionsMap.createSprite("Zach")
            }

            return characterMap

        }
    }
}