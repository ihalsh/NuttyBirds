package com.mygdx.game

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.utils.viewport.FitViewport
import com.mygdx.game.Utils.Assets.assetManager
import com.mygdx.game.Utils.Constants
import com.mygdx.game.Utils.Constants.Companion.PROGRESS_BAR_HEIGHT
import com.mygdx.game.Utils.Constants.Companion.PROGRESS_BAR_WIDTH
import com.mygdx.game.Utils.Constants.Companion.WORLD_HEIGHT
import com.mygdx.game.Utils.Constants.Companion.WORLD_WIDTH
import ktx.app.KtxScreen
import ktx.app.clearScreen

class LoadingScreen(private val game: NuttyGame) : KtxScreen {

    private var shapeRenderer = ShapeRenderer()
    private var viewport = FitViewport(Constants.WORLD_WIDTH, Constants.WORLD_HEIGHT)
    private var progress = 0f

    override fun resize(width: Int, height: Int) {
        viewport.update(width, height, true)
    }

    override fun render(delta: Float) {
        viewport.apply()
        clearScreen(Color.BLACK.r, Color.BLACK.g, Color.BLACK.b)
        update()
        draw()
    }

    override fun dispose() {
        shapeRenderer.dispose()
    }

    private fun update() {
        if (assetManager.update()) {
            game.setScreen<GameScreen>()
        } else {
            progress = assetManager.progress
        }
    }

    private fun draw() {
        shapeRenderer.projectionMatrix = viewport.camera.combined
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled)
        shapeRenderer.color = Color.WHITE
        shapeRenderer.rect((WORLD_WIDTH - PROGRESS_BAR_WIDTH) / 2,
                WORLD_HEIGHT / 2 - PROGRESS_BAR_HEIGHT / 2,
                progress * PROGRESS_BAR_WIDTH,
                PROGRESS_BAR_HEIGHT)
        shapeRenderer.end()
    }
}