package com.mygdx.game.Utils

import com.badlogic.gdx.maps.objects.EllipseMapObject
import com.badlogic.gdx.maps.objects.RectangleMapObject
import com.badlogic.gdx.maps.tiled.TiledMap
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType.DynamicBody
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType.StaticBody
import com.badlogic.gdx.physics.box2d.CircleShape
import com.badlogic.gdx.physics.box2d.PolygonShape
import com.badlogic.gdx.physics.box2d.World
import com.mygdx.game.Utils.Constants.Companion.ENEMY
import com.mygdx.game.Utils.Constants.Companion.FLOOR
import com.mygdx.game.Utils.Constants.Companion.FLOOR_LAYER
import com.mygdx.game.Utils.Constants.Companion.HALF
import com.mygdx.game.Utils.Constants.Companion.HORIZONTAL
import com.mygdx.game.Utils.Constants.Companion.PHYSICS_BIRDS_LAYER
import com.mygdx.game.Utils.Constants.Companion.PHYSICS_BUILDINGS_LAYER
import com.mygdx.game.Utils.Constants.Companion.PIXELS_PER_TILE
import com.mygdx.game.Utils.Constants.Companion.VERTICAL
import ktx.box2d.body

class TiledObjectBodyBuilder {

    //Draw the floor
    fun buildFloorBodies(tiledMap: TiledMap, world: World) {

        val floorObjects = tiledMap.layers.get(FLOOR_LAYER).objects

        for (mapObjects in floorObjects) {
            val rectangleMapObject = mapObjects as RectangleMapObject
            val rectangle = getRectangle(rectangleMapObject)
            world.body {
                type = StaticBody
                fixture(rectangle) { density = 1f }
                userData = FLOOR
            }.setTransform((getTransformForRectangle(rectangleMapObject.rectangle)), 0f)
        }
    }

    //Draw building
    fun buildBuildingBodies(tiledMap: TiledMap, world: World) {

        val buildingObjects = tiledMap.layers.get(PHYSICS_BUILDINGS_LAYER).objects

        for (mapObjects in buildingObjects) {
            val rectangleMapObject = mapObjects as RectangleMapObject
            val rectangle = getRectangle(rectangleMapObject)
            world.body {
                type = DynamicBody
                fixture(rectangle) { density = 1f }
                userData = if (rectangleMapObject.rectangle.width > rectangleMapObject.rectangle.height)
                    HORIZONTAL else VERTICAL
            }.setTransform((getTransformForRectangle(rectangleMapObject.rectangle)), 0f)
            rectangle.dispose()
        }
    }

    //Draw birds
    fun buildBirdBodies(tiledMap: TiledMap, world: World) {

        val birdObjects = tiledMap.layers.get(PHYSICS_BIRDS_LAYER).objects

        for (birdObject in birdObjects) {
            val circle = getCircle(birdObject as EllipseMapObject)
            val ellipse = birdObject.ellipse
            world.body {
                type = DynamicBody
                fixture(circle) {
                    density = 1f
                    userData = ENEMY
                }
                userData = ENEMY
            }.setTransform(Vector2((ellipse.x + ellipse.width * HALF) / PIXELS_PER_TILE,
                    (ellipse.y + ellipse.height * HALF) / PIXELS_PER_TILE), 0f)
            circle.dispose()
        }
    }

    private fun getRectangle(rectangleObject: RectangleMapObject): PolygonShape {
        val rectangle = rectangleObject.rectangle
        val polygon = PolygonShape()
        polygon.setAsBox(
                rectangle.width * HALF / PIXELS_PER_TILE,
                rectangle.height * HALF / PIXELS_PER_TILE)
        return polygon
    }

    private fun getCircle(ellipseObject: EllipseMapObject): CircleShape {
        val ellipse = ellipseObject.ellipse
        val circleShape = CircleShape()
        circleShape.radius = ellipse.width * HALF / PIXELS_PER_TILE
        return circleShape
    }

    private fun getTransformForRectangle(rectangle: Rectangle): Vector2 =
            Vector2((rectangle.x + rectangle.width * HALF) / PIXELS_PER_TILE,
                    (rectangle.y + rectangle.height * HALF) / PIXELS_PER_TILE)
}