package com.mygdx.game.Utils

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.physics.box2d.Body
import com.mygdx.game.Utils.Assets.assetManager
import com.mygdx.game.Utils.Assets.bird
import com.mygdx.game.Utils.Assets.obstacleHorizontal
import com.mygdx.game.Utils.Assets.obstacleVertical
import com.mygdx.game.Utils.Constants.Companion.ENEMY
import com.mygdx.game.Utils.Constants.Companion.HORIZONTAL
import com.mygdx.game.Utils.Constants.Companion.VERTICAL

object SpriteGenerator {

    fun generateSpriteForBody(body: Body): Sprite? {
        return when {
            HORIZONTAL == body.userData -> createSprite(obstacleHorizontal)
            VERTICAL == body.userData -> createSprite(obstacleVertical)
            ENEMY == body.userData -> createSprite(bird)
            else -> null
        }
    }
}

private fun createSprite(texture: Texture): Sprite {
    val sprite = Sprite(texture)
    sprite.setOrigin(sprite.width / 2, sprite.height / 2)
    return sprite
}
