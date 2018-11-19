package com.mygdx.game

import com.badlogic.gdx.Screen
import com.badlogic.gdx.physics.box2d.Box2D
import com.mygdx.game.Utils.Assets
import ktx.app.KtxGame

class NuttyGame : KtxGame<Screen>() {

    override fun create() {

        Box2D.init()

        Assets.loadAssets()

        addScreen(LoadingScreen(this))
        addScreen(GameScreen())

        setScreen<LoadingScreen>()
    }


    override fun dispose() {
        Assets.assetManager.dispose()
    }
}
