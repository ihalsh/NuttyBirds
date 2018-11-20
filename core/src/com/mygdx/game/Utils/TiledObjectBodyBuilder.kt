package com.mygdx.game.Utils

import com.badlogic.gdx.maps.objects.EllipseMapObject
import com.badlogic.gdx.maps.objects.RectangleMapObject
import com.badlogic.gdx.maps.tiled.TiledMap
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType.DynamicBody
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType.StaticBody
import com.badlogic.gdx.physics.box2d.CircleShape
import com.badlogic.gdx.physics.box2d.PolygonShape
import com.badlogic.gdx.physics.box2d.World
import com.mygdx.game.Utils.Constants.Companion.ENEMY
import com.mygdx.game.Utils.Constants.Companion.FLOOR_LAYER
import com.mygdx.game.Utils.Constants.Companion.HALF
import com.mygdx.game.Utils.Constants.Companion.PHYSICS_BIRDS_LAYER
import com.mygdx.game.Utils.Constants.Companion.PHYSICS_BUILDINGS_LAYER
import com.mygdx.game.Utils.Constants.Companion.PIXELS_PER_TILE
import ktx.box2d.body
import ktx.log.info

class TiledObjectBodyBuilder {

    fun buildFloorAndBuildingBodies(tiledMap: TiledMap, world: World) {

        //Draw the floor
        val floorObject = tiledMap.layers.get(FLOOR_LAYER).objects
        val rectangle = getRectangle(floorObject[0] as RectangleMapObject)
        world.body {
            type = StaticBody
            fixture(rectangle) { density = 1f }
        }
        rectangle.dispose()

        //Draw building
        val objects = tiledMap.layers.get(PHYSICS_BUILDINGS_LAYER).objects
        for (mapObject in objects) {
            val rectangle = getRectangle(mapObject as RectangleMapObject)
            world.body {
                type = DynamicBody
                fixture(rectangle) { density = 40f }
            }
            rectangle.dispose()
        }
        //Draw birds
        val birdObjects = tiledMap.layers.get(PHYSICS_BIRDS_LAYER).objects
        for (birdObject in birdObjects) {
            val circle = getCircle(birdObject as EllipseMapObject)
            world.body {
                type = DynamicBody
                fixture(circle) {
                    density = 10f
                    userData = ENEMY
                }
            }
            circle.dispose()
        }
    }

    private fun getRectangle(rectangleObject: RectangleMapObject): PolygonShape {
        val rectangle = rectangleObject.rectangle
        val polygon = PolygonShape()
        val size = Vector2(
                (rectangle.x + rectangle.width * HALF) / PIXELS_PER_TILE,
                (rectangle.y + rectangle.height * HALF) / PIXELS_PER_TILE
        )
        polygon.setAsBox(
                rectangle.width * HALF / PIXELS_PER_TILE,
                rectangle.height * HALF / PIXELS_PER_TILE,
                size,
                0.0f)
        return polygon
    }

    private fun getCircle(ellipseObject: EllipseMapObject): CircleShape {
        val ellipse = ellipseObject.ellipse
        val circleShape = CircleShape()
        circleShape.radius = ellipse.width * HALF / PIXELS_PER_TILE
        circleShape.position = Vector2(
                (ellipse.x + ellipse.width * HALF) / PIXELS_PER_TILE,
                (ellipse.y + ellipse.height * HALF) / PIXELS_PER_TILE
        )
        return circleShape
    }
}