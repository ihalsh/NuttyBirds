package com.mygdx.game

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.InputAdapter
import com.badlogic.gdx.graphics.Color.BLACK
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer
import com.badlogic.gdx.math.MathUtils.*
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.*
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType.DynamicBody
import com.badlogic.gdx.utils.Array
import com.badlogic.gdx.utils.ObjectMap
import com.badlogic.gdx.utils.OrderedMap
import com.badlogic.gdx.utils.Pools
import com.badlogic.gdx.utils.viewport.FitViewport
import com.mygdx.game.Entities.Acorn
import com.mygdx.game.Utils.Assets
import com.mygdx.game.Utils.Assets.acorn
import com.mygdx.game.Utils.Constants.Companion.ACORN
import com.mygdx.game.Utils.Constants.Companion.ACORN_COUNT
import com.mygdx.game.Utils.Constants.Companion.ENEMY
import com.mygdx.game.Utils.Constants.Companion.LOWER_ANGLE
import com.mygdx.game.Utils.Constants.Companion.MAX_DISTANCE
import com.mygdx.game.Utils.Constants.Companion.UNIT_HEIGHT
import com.mygdx.game.Utils.Constants.Companion.UNIT_WIDTH
import com.mygdx.game.Utils.Constants.Companion.UPPER_ANGLE
import com.mygdx.game.Utils.Constants.Companion.WORLD_HEIGHT
import com.mygdx.game.Utils.Constants.Companion.WORLD_WIDTH
import com.mygdx.game.Utils.SpriteGenerator
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

    private val anchor = Vector2(convertMetresToUnits(6.125f), convertMetresToUnits(5.75f))
    private val firingPosition = anchor.cpy()
    private var distance = 0f
    private var angle = 0f

    private val sprites = ObjectMap<Body, Sprite>()
    private val slingshot = Sprite(Assets.slingshot).apply { setPosition(170f, 64f) }
    private val squirrel = Sprite(Assets.squirrel).apply { setPosition(32f, 64f) }
    private val staticAcorn = Sprite(Assets.acorn)

    private val acorns = OrderedMap<Body, Acorn>()
    private var acornPool = Pools.get<Acorn>(Acorn::class.java)

    override fun show() {
        viewport.apply()
        camera.update()
        orthogonalTiledMapRenderer.setView(camera)

        with(TiledObjectBodyBuilder()) {
            buildFloorBodies(tiledMap, world)
            buildBirdBodies(tiledMap, world)
            buildBuildingBodies(tiledMap, world)
        }

        val bodies = Array<Body>()
        world.getBodies(bodies)
        for (body in bodies) {
            val sprite = SpriteGenerator.generateSpriteForBody(body)
            if (sprite != null) {
                sprites.put(body, sprite)
            }
        }

        Gdx.input.inputProcessor = object : InputAdapter() {
            override fun touchDragged(screenX: Int, screenY: Int, pointer: Int): Boolean {
                calculateAngleAndDistanceForAcorn(screenX, screenY)
                return true
            }

            override fun touchUp(screenX: Int, screenY: Int, pointer: Int,
                                 button: Int): Boolean {
                createAcorn(angle)
                firingPosition.set(anchor.cpy())
                return true
            }
        }

        world.setContactListener(NuttyContactListener())
    }


    private fun createAcorn(angle: Float) {
        val acorn = world.body {
            type = DynamicBody
            circle(radius = 0.5f) { density = 2f }
            userData = ACORN
            linearVelocity.set(Math.abs(20f/*MAX_STRENGTH*/ * -cos(angle) * (distance / 100f)),
                    Math.abs(20f/*MAX_STRENGTH*/ * -sin(angle) * (distance / 100f)))
        }.apply { setTransform(Vector2(convertUnitsToMetres(firingPosition.x),
                convertUnitsToMetres(firingPosition.y)), 0f) }
        checkLimitAndRemoveAcornIfNecessary()
        acorns.put(acorn, acornPool.obtain())
    }

    private fun checkLimitAndRemoveAcornIfNecessary() {
        if (acorns.size == ACORN_COUNT) {
            val body = acorns.keys().iterator().next()
            toRemove.add(body)
            val acorn = acorns.remove(body)
            acornPool.free(acorn)
        }
    }

    private fun calculateAngleAndDistanceForAcorn(screenX: Int, screenY: Int) {
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
        updateSpritePositions()
        updateAcornPositions()
    }

    private fun updateAcornPositions() {

        for (acornEntry in acorns) {
            acornEntry.value.setPosition(convertMetresToUnits(acornEntry.key.position.x) -
                    acornEntry.value.width / 2f,
                    convertMetresToUnits(acornEntry.key.position.y) - acornEntry.value.height / 2f)
            acornEntry.value.setRotation(radiansToDegrees * acornEntry.key.angle)
        }
    }

    private fun updateSpritePositions() {
        for (body in sprites.keys()) {
            val sprite = sprites.get(body)
            sprite.setPosition(
                    convertMetresToUnits(body.position.x) - sprite.width / 2f,
                    convertMetresToUnits(body.position.y) - sprite.height / 2f)
            sprite.rotation = radiansToDegrees * body.angle
        }
        staticAcorn.setPosition(firingPosition.x - staticAcorn.width / 2f, firingPosition.y - staticAcorn.height / 2f)
    }

    private fun clearDeadBodies() {
        for (body in toRemove) {
            sprites.remove(body)
            world.destroyBody(body)
        }
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
        orthogonalTiledMapRenderer.render()
        batch.use {
            for (sprite in sprites.values()) sprite.draw(it)
            squirrel.draw(it)
            staticAcorn.draw(it)
            slingshot.draw(it)
            for (acorn in acorns.values()) acorn.draw(it)
        }
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
//        debugRenderer.render(world, box2dCam.combined)
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