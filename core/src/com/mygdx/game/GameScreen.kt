package com.mygdx.game

import com.badlogic.gdx.graphics.Color.BLACK
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer
import com.badlogic.gdx.physics.box2d.Body
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType.DynamicBody
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer
import com.badlogic.gdx.physics.box2d.World
import com.badlogic.gdx.utils.viewport.FitViewport
import com.mygdx.game.Utils.Assets
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
    private val batch = SpriteBatch()
    private val camera = OrthographicCamera()
    private val viewport = FitViewport(WORLD_WIDTH, WORLD_HEIGHT, camera)
    private val world: World = createWorld(gravity = earthGravity, allowSleep = true)
    private val debugRenderer: Box2DDebugRenderer = Box2DDebugRenderer()
    private val tiledMap = Assets.tiledMap
    private val orthogonalTiledMapRenderer = OrthogonalTiledMapRenderer(tiledMap, batch)

    override fun show() {
        viewport.apply()
        camera.update()
        orthogonalTiledMapRenderer.setView(camera)
    }


    private fun update(delta: Float) {

        world.step(delta, 6, 2)

    }

    override fun render(delta: Float) {
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

        orthogonalTiledMapRenderer.render()
    }

    override fun resize(width: Int, height: Int) {
        viewport.update(width, height, true)
    }

    override fun dispose() {
        shapeRenderer.dispose()
        orthogonalTiledMapRenderer.dispose()
        batch.dispose()
    }
}