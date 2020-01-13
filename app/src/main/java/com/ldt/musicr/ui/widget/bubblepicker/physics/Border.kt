package com.ldt.musicr.ui.widget.bubblepicker.physics

import org.jbox2d.collision.shapes.PolygonShape
import org.jbox2d.common.Vec2
import org.jbox2d.dynamics.*

class Border(world: World, var position: Vec2, var direction: Int) {
    companion object {
        const val HORIZONTAL: Int = 0
        const val VERTICAL: Int = 1
    }

    var itemBody: Body
    private val shape: PolygonShape = PolygonShape()
    private val fixture: FixtureDef = FixtureDef()
    private val bodyDef: BodyDef = BodyDef()

    init {
        update()
        itemBody = world.createBody(bodyDef).apply { createFixture(fixture) }
    }

    fun updatePosition() {
        shape.apply {
            if (direction == HORIZONTAL) {

                setAsEdge(Vec2(-100f, position.y), Vec2(100f, position.y))
            } else {
                setAsEdge(Vec2(position.x, -100f), Vec2(position.x, 100f))
            }
        }
    }

    fun updateFixture() {
        fixture.apply {
            this.shape = this@Border.shape
            density = 50f
        }
    }

    fun updateBodyDef() {
        bodyDef.apply {
            type = BodyType.STATIC
            this.position = this@Border.position
        }
    }

    fun update() {
        updatePosition()
        updateFixture()
        updateBodyDef()
    }

}