package com.mygdx.game

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.InputAdapter
import com.badlogic.gdx.graphics.Color.BLACK
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer
import com.badlogic.gdx.math.MathUtils.cos
import com.badlogic.gdx.math.MathUtils.sin
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.*
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType.DynamicBody
import com.badlogic.gdx.utils.Array
import com.badlogic.gdx.utils.viewport.FitViewport
import com.mygdx.game.Utils.Assets
import com.mygdx.game.Utils.Constants.Companion.ENEMY
import com.mygdx.game.Utils.Constants.Companion.LOWER_ANGLE
import com.mygdx.game.Utils.Constants.Companion.MAX_DISTANCE
import com.mygdx.game.Utils.Constants.Companion.UNIT_HEIGHT
import com.mygdx.game.Utils.Constants.Companion.UNIT_WIDTH
import com.mygdx.game.Utils.Constants.Companion.UPPER_ANGLE
import com.mygdx.game.Utils.Constants.Companion.WORLD_HEIGHT
import com.mygdx.game.Utils.Constants.Companion.WORLD_WIDTH
import com.mygdx.game.Utils.TiledObjectBodyBuilder
import com.mygdx.game.Utils.Utils.angleBetweenTwoPoints
import com.mygdx.game.Utils.Utils.convertMetresToUnits
import com.mygdx.game.Utils.Utils.convertUnitsToMetres
import com.mygdx.game.Utils.Utils.distanceBetweenTwoPoints
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

    private val anchor = Vector2(convertMetresToUnits(3f), convertMetresToUnits(6f))
    private val firingPosition = anchor.cpy()
    private var distance = 0f
    private var angle = 0f

    override fun show() {
        viewport.apply()
        camera.update()
        orthogonalTiledMapRenderer.setView(camera)
        TiledObjectBodyBuilder().buildFloorAndBuildingBodies(tiledMap, world)

        Gdx.input.inputProcessor = object : InputAdapter() {
            override fun touchDragged(screenX: Int, screenY: Int, pointer: Int): Boolean {
                calculateAngleAndDistanceForBullet(screenX, screenY)

                return true
            }

            override fun touchUp(screenX: Int, screenY: Int, pointer: Int,
                                 button: Int): Boolean {
                createBullet(angle)
                firingPosition.set(anchor.cpy())
                return true
            }
        }

        world.setContactListener(NuttyContactListener())
    }


    private fun createBullet(angle: Float) = world.body {
        type = DynamicBody
        circle(radius = 0.5f) { density = 100f }
        position.set(Vector2(convertUnitsToMetres(firingPosition.x),
                convertUnitsToMetres(firingPosition.y)))
        linearVelocity.set(Math.abs(15f/*MAX_STRENGTH*/ * -cos(angle) * (distance / 100f)),
                Math.abs(15f/*MAX_STRENGTH*/ * -sin(angle) * (distance / 100f)))
    }

    private fun calculateAngleAndDistanceForBullet(screenX: Int, screenY: Int) {
        firingPosition.set(screenX.toFloat(), screenY.toFloat())
        viewport.unproject(firingPosition)
        distance = distanceBetweenTwoPoints(anchor, firingPosition)
        angle = angleBetweenTwoPoints(anchor, firingPosition)
        if (distance > MAX_DISTANCE) distance = MAX_DISTANCE
        if (angle > LOWER_ANGLE) {
            if (angle > UPPER_ANGLE) {
                angle = 0f
            } else {
                angle = LOWER_ANGLE
            }
        }
        firingPosition.set(anchor.x + distance * -cos(angle), anchor.y + distance * -sin(angle))
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
        shapeRenderer.rect(anchor.x - 5f, anchor.y - 5f, 10f, 10f)
        shapeRenderer.rect(firingPosition.x - 5f, firingPosition.y - 5f,
                10f, 10f)
        shapeRenderer.line(anchor.x, anchor.y, firingPosition.x,
                firingPosition.y)
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
                    if (Math.abs(impactVelocity.x) > 1 || Math.abs(impactVelocity.y) > 1) {
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