package com.mygdx.game

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.InputAdapter
import com.badlogic.gdx.graphics.Color.BLACK
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer
import com.badlogic.gdx.physics.box2d.*
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType.DynamicBody
import com.badlogic.gdx.utils.Array
import com.badlogic.gdx.utils.viewport.FitViewport
import com.mygdx.game.Utils.Assets
import com.mygdx.game.Utils.Constants.Companion.ENEMY
import com.mygdx.game.Utils.Constants.Companion.UNIT_HEIGHT
import com.mygdx.game.Utils.Constants.Companion.UNIT_WIDTH
import com.mygdx.game.Utils.Constants.Companion.WORLD_HEIGHT
import com.mygdx.game.Utils.Constants.Companion.WORLD_WIDTH
import com.mygdx.game.Utils.TiledObjectBodyBuilder
import ktx.app.KtxScreen
import ktx.app.clearScreen
import ktx.box2d.body
import ktx.box2d.createWorld
import ktx.box2d.earthGravity
import ktx.graphics.use
import ktx.log.info

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
    private val toRemove = Array<Body>()

    override fun show() {
        viewport.apply()
        camera.update()
        orthogonalTiledMapRenderer.setView(camera)
        TiledObjectBodyBuilder().buildFloorAndBuildingBodies(tiledMap, world)

        Gdx.input.inputProcessor = object : InputAdapter() {
            override fun touchDown(screenX: Int, screenY: Int, pointer: Int,
                                   button: Int): Boolean {
                createBullet()
                return true
            }
        }
        world.setContactListener(NuttyContactListener())
    }


    private fun createBullet() = world.body {
        type = DynamicBody
        circle(radius = 0.5f) { density = 100f }
        position.set(3f, 6f)
        linearVelocity.set(10f, 6f)
    }

    private fun update(delta: Float) {
        clearDeadBodies()
        world.step(delta, 6, 2)
        box2dCam.position.set(UNIT_WIDTH / 2, UNIT_HEIGHT / 2, 0f)
        box2dCam.update()
    }

    private fun clearDeadBodies() {
        for (body in toRemove) world.destroyBody(body)
        toRemove.clear()
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
        world.dispose()
    }

    inner class NuttyContactListener : ContactListener {

        override fun beginContact(contact: Contact) {
            if (contact.isTouching) {
                val attacker = contact.fixtureA
                val defender = contact.fixtureB
                val worldManifold = contact.worldManifold
                if (ENEMY == defender.userData) {
                    val vel1 =
                            attacker.body.getLinearVelocityFromWorldPoint(worldManifold.points[0])
                    val vel2 =
                            defender.body.getLinearVelocityFromWorldPoint(worldManifold.points[0])
                    val impactVelocity = vel1.sub(vel2)
                    if (Math.abs(impactVelocity.x) > 1 || Math.abs(impactVelocity.y) > 1){
                        info { "${defender.userData} dead!" }
                        toRemove.add(defender.body)
                    }
                }
            }
        }

        override fun endContact(contact: Contact) {

        }
        override fun preSolve(contact: Contact, oldManifold: Manifold) {

        }
        override fun postSolve(contact: Contact, impulse: ContactImpulse) {
         }
    }
}