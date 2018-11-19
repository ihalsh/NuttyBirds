package com.mygdx.game

import com.badlogic.gdx.graphics.Color.BLACK
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.Body
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType.DynamicBody
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer
import com.badlogic.gdx.physics.box2d.World
import com.badlogic.gdx.utils.viewport.FitViewport
import com.mygdx.game.Utils.Constants.Companion.UNITS_PER_METER
import com.mygdx.game.Utils.Constants.Companion.WORLD_HEIGHT
import com.mygdx.game.Utils.Constants.Companion.WORLD_WIDTH
import ktx.app.KtxScreen
import ktx.app.clearScreen
import ktx.box2d.body
import ktx.box2d.createWorld
import ktx.box2d.earthGravity
import ktx.graphics.use

class GameScreen : KtxScreen {

    private val shapeRenderer = ShapeRenderer()
    private val viewport = FitViewport(WORLD_WIDTH, WORLD_HEIGHT)
    private val batch = SpriteBatch()

    private val world: World = createWorld(gravity = earthGravity, allowSleep = true)
    private val debugRenderer: Box2DDebugRenderer = Box2DDebugRenderer()
    // Building body from scratch
    private val body: Body = world.body {
        type = DynamicBody
        position.set(100f, 200f)
        box(160 / UNITS_PER_METER, 160 / UNITS_PER_METER)
        { density = 40f }
    }

    private fun update(delta: Float) {

        world.step(delta, 6, 2)
        body.isAwake = true

    }

    override fun render(delta: Float) {
        viewport.apply()
        clearScreen(BLACK.r, BLACK.g, BLACK.b)
        update(delta)
        draw()
        drawDebug()
    }

    private fun draw() {
        batch.projectionMatrix = viewport.camera.combined
        batch.use { }
    }

    private fun drawDebug() {
        shapeRenderer.projectionMatrix = viewport.camera.combined
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line)
        shapeRenderer.end()

        debugRenderer.render(world, viewport.camera.combined)
    }

    override fun resize(width: Int, height: Int) {
        viewport.update(width, height, true)
    }

    override fun dispose() {
        shapeRenderer.dispose()
    }
}