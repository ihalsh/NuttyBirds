package com.mygdx.game.Utils

import com.badlogic.gdx.math.MathUtils

class Constants {

    companion object {
        //General
        const val WORLD_WIDTH = 960f
        const val WORLD_HEIGHT = 544f
        const val UNITS_PER_METER = 16f
        const val PROGRESS_BAR_WIDTH = 100f
        const val PROGRESS_BAR_HEIGHT = 25f
        const val MAP_FILE_NAME = "nuttybirds.tmx"
        const val ENEMY = "enemy"
        const val FLOOR = "floor"
        const val HORIZONTAL = "h"
        const val VERTICAL = "v"
        const val ACORN = "a"
        const val UNIT_WIDTH = WORLD_WIDTH / (UNITS_PER_METER * 2)
        const val UNIT_HEIGHT = WORLD_HEIGHT / (UNITS_PER_METER * 2)

        const val MAX_STRENGTH = 15f
        const val MAX_DISTANCE = 100f
        const val UPPER_ANGLE = 3 * MathUtils.PI / 2f
        const val LOWER_ANGLE = MathUtils.PI / 2f

        //TiledObjectBodyBuilder
        const val PIXELS_PER_TILE = 32f
        const val HALF = 0.5f
        const val PHYSICS_BUILDINGS_LAYER = "Physics_Buildings"
        const val FLOOR_LAYER = "Physics_Floor"
        const val PHYSICS_BIRDS_LAYER = "Physics_Birds"

        //Acorn Pool
        const val ACORN_COUNT = 3
    }
}