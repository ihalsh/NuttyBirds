package com.mygdx.game.Entities

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.utils.Pool
import com.mygdx.game.Utils.Assets.assetManager

class Acorn(private val sprite: Sprite = Sprite(assetManager.get("acorn.png",
        Texture::class.java))) : Pool.Poolable {

    val width: Float
        get() = sprite.width
    val height: Float
        get() = sprite.height

    init {
        sprite.setOrigin(sprite.width / 2, sprite.height / 2)
    }

    fun setPosition(x: Float, y: Float) = sprite.setPosition(x, y)

    fun setRotation(degrees: Float) {
        sprite.rotation = degrees
    }

    fun draw(batch: Batch) = sprite.draw(batch)

    override fun reset() {
        sprite.setPosition(0f, 0f)
        sprite.rotation = 0f
    }
}