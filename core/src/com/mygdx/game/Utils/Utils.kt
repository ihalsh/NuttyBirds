package com.mygdx.game.Utils

import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector2
import com.mygdx.game.Utils.Constants.Companion.UNITS_PER_METER
import ktx.log.info

object Utils {

    fun convertUnitsToMetres(pixels: Float): Float = pixels / (UNITS_PER_METER * 2)

    fun convertMetresToUnits(metres: Float): Float = metres * (UNITS_PER_METER * 2)

    fun angleBetweenTwoPoints(anchor: Vector2, firingPosition: Vector2): Float {
        var angle = MathUtils.atan2(anchor.y - firingPosition.y,
                anchor.x - firingPosition.x)
        angle %= 2 * MathUtils.PI
        if (angle < 0) angle += 2 * MathUtils.PI2
        return angle
    }

    fun distanceBetweenTwoPoints(anchor: Vector2, firingPosition: Vector2): Float =
            Math.sqrt(((anchor.x - firingPosition.x) * (anchor.x - firingPosition.x) +
                    (anchor.y - firingPosition.y) * (anchor.y - firingPosition.y)).toDouble())
                    .toFloat()

}