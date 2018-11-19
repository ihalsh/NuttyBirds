package com.mygdx.game

import com.badlogic.gdx.Screen
import com.mygdx.game.Utils.Assets
import ktx.app.KtxGame

class NuttyGame : KtxGame<Screen>() {

    override fun create() {

        addScreen(LoadingScreen(this))
        addScreen(GameScreen(this))

        setScreen<LoadingScreen>()
    }


    override fun dispose() {
        Assets.assetManager.dispose()
    }
}
