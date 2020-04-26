package com.tichuclub.tichuclub

import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.scenes.scene2d.ui.Skin

class Config {
    companion object {
        const val skinFile = "uiskin.json"
        fun getSkin() : Skin {
            val assetManager = AssetManager()
            assetManager.load(skinFile, Skin::class.java)
            assetManager.finishLoading()
            return assetManager.get(skinFile)
        }
    }
}