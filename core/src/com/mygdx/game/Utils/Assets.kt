package com.mygdx.game.Utils

import com.badlogic.gdx.assets.AssetDescriptor
import com.badlogic.gdx.assets.AssetErrorListener
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.maps.tiled.TiledMap
import com.badlogic.gdx.maps.tiled.TmxMapLoader
import com.badlogic.gdx.utils.Disposable
import com.badlogic.gdx.utils.Logger
import com.mygdx.game.Utils.Constants.Companion.MAP_FILE_NAME
import ktx.log.info

object Assets : Disposable, AssetErrorListener {

    val assetManager: AssetManager = AssetManager().apply {
        setErrorListener(Assets)
        setLoader(TiledMap::class.java, TmxMapLoader(InternalFileHandleResolver()))
    }

    private val logger = ktx.log.logger<Assets>()
    lateinit var tiledMap: TiledMap
    lateinit var obstacleVertical: Texture
    lateinit var obstacleHorizontal: Texture
    lateinit var bird: Texture
    lateinit var slingshot: Texture
    lateinit var squirrel: Texture
    lateinit var acorn: Texture

    fun loadAssets() {
        with(assetManager) {
            logger.level = Logger.INFO
            load(MAP_FILE_NAME, TiledMap::class.java)
            load("obstacleVertical.png",Texture::class.java)
            load("obstacleHorizontal.png",Texture::class.java)
            load("bird.png",Texture::class.java)
            load("slingshot.png",Texture::class.java)
            load("squirrel.png",Texture::class.java)
            load("acorn.png",Texture::class.java)
            finishLoading()
            tiledMap = get(MAP_FILE_NAME)
            obstacleVertical = get("obstacleVertical.png")
            obstacleHorizontal = get("obstacleHorizontal.png")
            bird = get("bird.png")
            slingshot = get("slingshot.png")
            squirrel = get("squirrel.png")
            acorn = get("acorn.png")
        }
    }

    override fun dispose() {
        assetManager.dispose()
        info { "Assets disposed...Ok" }
    }

    override fun error(asset: AssetDescriptor<*>, throwable: Throwable) {
        logger.error(throwable) { "Couldn't load asset: ${asset.fileName}" }
    }
}