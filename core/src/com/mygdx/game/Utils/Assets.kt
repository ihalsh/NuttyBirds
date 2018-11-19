package com.mygdx.game.Utils

import com.badlogic.gdx.assets.AssetDescriptor
import com.badlogic.gdx.assets.AssetErrorListener
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.utils.Disposable
import ktx.log.info

object Assets : Disposable, AssetErrorListener {

    val assetManager: AssetManager = AssetManager().apply { setErrorListener(Assets) }

    private val logger = ktx.log.logger<Assets>()

    override fun dispose() {
        assetManager.dispose()
        info { "Assets disposed...Ok" }
    }

    override fun error(asset: AssetDescriptor<*>, throwable: Throwable) {
        logger.error(throwable) { "Couldn't load asset: ${asset.fileName}" }
    }
}