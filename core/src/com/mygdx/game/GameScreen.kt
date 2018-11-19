package com.mygdx.game

import com.badlogic.gdx.graphics.Color.BLACK
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer
import com.badlogic.gdx.physics.box2d.World
import com.badlogic.gdx.utils.viewport.FitViewport
import com.mygdx.game.Utils.Assets
import com.mygdx.game.Utils.Constants.Companion.UNIT_HEIGHT
import com.mygdx.game.Utils.Constants.Companion.UNIT_WIDTH
import com.mygdx.game.Utils.Constants.Companion.WORLD_HEIGHT
import com.mygdx.game.Utils.Constants.Companion.WORLD_WIDTH
import com.mygdx.game.Utils.TiledObjectBodyBuilder
import ktx.app.KtxScreen
import ktx.app.clearScreen
import ktx.box2d.createWorld
import ktx.box2d.earthGravity
import ktx.graphics.use

class GameScreen : KtxScreen {

    private val shapeRenderer = ShapeRenderer()
    private val batch = SpriteBatch()
    private val camera = OrthographicCamera()
    private val box2dCam = OrthographicCamera(UNIT_WIDTH, UNIT_HEIGHT)
    private val viewport = FitViewport(WORLD_WIDTH, WORLD_HEIGHT, camera)
    private val world: World = createWorld(gravity = earthGravity, allowSleep = true)
    private val debugRenderer: Box2DDebugRenderer = Box2DDebugRenderer()
    private val tiledMap = Assets.tiledMap
    private val orthogonalTiledMapRenderer = OrthogonalTiledMapRenderer(tiledMap, batch)

    override fun show() {
        viewport.apply()
        camera.update()
        orthogonalTiledMapRenderer.setView(camera)
        TiledObjectBodyBuilder.buildFloorAndBuildingBodies(tiledMap, world)
    }


    private fun update(delta: Float) {
        world.step(delta, 6, 2)
        box2dCam.position.set(UNIT_WIDTH / 2, UNIT_HEIGHT / 2, 0f)
        box2dCam.update()
    }

    override fun render(delta: Float) {
        clearScreen(BLACK.r, BLACK.g, BLACK.b)
        update(delta)
        draw()
        drawDebug()
    }

    private fun draw() {
        batch.projectionMatrix = camera.combined
        batch.use { }
        orthogonalTiledMapRenderer.render()
    }

    private fun drawDebug() {
        shapeRenderer.projectionMatrix = camera.combined
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line)
        shapeRenderer.end()
        debugRenderer.render(world, box2dCam.combined)
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