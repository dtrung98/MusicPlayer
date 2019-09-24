package com.ldt.musicr.ui.widget.bubblepicker.physics

import android.util.Log
import com.ldt.musicr.ui.widget.bubblepicker.rendering.CircleRenderItem
import com.ldt.musicr.ui.widget.bubblepicker.rendering.PickerRenderer
import com.ldt.musicr.ui.widget.bubblepicker.sqr
import org.jbox2d.common.Vec2
import org.jbox2d.dynamics.World
import java.util.*

/**
 * Created by irinagalata on 1/26/17.
 */
object Engine {

    val selectedBodies: List<CircleBody>
        get() = bodies.filter { it.increased || it.toBeIncreased || it.isIncreasing }
    var maxSelectedCount: Int? = null
    var radius = 50
        set(value) {
            field = value
            bubbleRadius = interpolate(0.01f, 0.5f, value / 100f)
            gravity = interpolate(1f, 40f, value / 100f)
            standardIncreasedGravity = interpolate(500f, 800f, value / 100f)
        }
    var centerImmediately = false
    private var standardIncreasedGravity = interpolate(500f, 800f, 0.5f)
    private var bubbleRadius = 0.17f // 0.166666 = 1/60

    private val world = World(Vec2(0f, 0f), false)
    private val step = 0.0005f // = 5ms for a loop
    private val bodies: ArrayList<CircleBody> = ArrayList()
    private var borders: ArrayList<Border> = ArrayList()
    private val resizeStep = 0.005f
    private var scaleX = 0f
    private var scaleY = 0f
    private var touch = false
    private var gravity = 6f
    private var increasedGravity = 55f
    private var gravityCenter = Vec2(0f, 0f)
    private val currentGravity: Float
        get() = if (touch) increasedGravity else gravity
    private val toBeResized = ArrayList<CircleRenderItem>()
    private val startX
        get() = if (centerImmediately) 0.5f else 2.2f
    private var stepsCount = 0

    fun build(bodiesCount: Int, scaleX: Float, scaleY: Float): List<CircleBody> {
        val density = interpolate(0.8f, 0.2f, radius / 100f)
        val rnd = Random()
        for (i in 0..bodiesCount - 1) {
            val x = if (rnd.nextBoolean()) -startX else startX
            val y = if (rnd.nextBoolean()) -0.5f / scaleY else 0.5f / scaleY
            var randomSize =  rnd.nextInt(100)/100f
            val size = 1f//bubbleRadius * interpolate(0.5f,1.5f,randomSize)
            bodies.add(CircleBody(world, Vec2(x, y), size* scaleX, (size * scaleX) * 1.3f, density))
        }

        this.scaleX = scaleX
        this.scaleY = scaleY
        createBorders()

        return bodies
    }

    private var firstTime = true
    private var mStartTime: Long = 0
    private var mDeltaInMilli: Long = 0
    private var mDeltaInSecond: Float = 0f
    private var mTotalRunningTime: Long = 0
    private var mFrames: Long = 0
    private val FRAME_INTERVAL = 1000f / 60

    private fun recordValue() {
        if(firstTime) {
            mFrames = 0
            mDeltaInMilli = 0
            mTotalRunningTime = 0
            mStartTime = System.currentTimeMillis()
            firstTime = false
        } else {
            val current = System.currentTimeMillis()
            mDeltaInMilli = current - mStartTime - mTotalRunningTime

            mTotalRunningTime += mDeltaInMilli
            mFrames++
        }
        mDeltaInSecond = mDeltaInMilli/1000f
        Log.i("Engine","delta = "+ mDeltaInMilli)
    }

    fun move() {
        recordValue()
        toBeResized.forEach { it.circleBody.resize(resizeStep) }
        // if centerImmediately, everything renders 7x faster than normal
        // why and how ???
        mDeltaInSecond = step
        world.step(if (centerImmediately) mDeltaInSecond*7 else mDeltaInSecond, 11, 11)
        bodies.forEach { move(it) }
        toBeResized.removeAll(toBeResized.filter { it.circleBody.finished })
        stepsCount++
        if (stepsCount >= 10) {
            centerImmediately = false
        }
    }

    fun swipe(x: Float, y: Float) {
        if (Math.abs(gravityCenter.x) < 2) gravityCenter.x += -x
        if (Math.abs(gravityCenter.y) < 0.5f / scaleY) gravityCenter.y += y
        increasedGravity = standardIncreasedGravity * Math.abs(x * 13) * Math.abs(y * 13)
        touch = true
    }

    fun release() {
        gravityCenter.setZero()
        touch = false
        increasedGravity = standardIncreasedGravity
    }

    fun clear() {
        borders.forEach { world.destroyBody(it.itemBody) }
        bodies.forEach { world.destroyBody(it.physicalBody) }
        borders.clear()
        bodies.clear()
    }

    fun onTap(item: CircleRenderItem): Boolean {
        if (selectedBodies.size >= maxSelectedCount ?: bodies.size && !item.circleBody.increased) return false

        if (item.circleBody.isBusy) return false

        toBeResized.add(item)

        return true
    }

    private fun createBorders() {
        borders = arrayListOf(
                Border(world, Vec2(0f, 2.5f / scaleY), Border.HORIZONTAL),
                Border(world, Vec2(0f, -2.5f / scaleY), Border.HORIZONTAL)
        )
    }

    private fun move(body: CircleBody) {
        body.physicalBody.apply {
            body.isVisible = centerImmediately.not()
            val direction = gravityCenter.sub(position)
            val distance = direction.length()
            val gravity = if (body.increased) 1.3f * currentGravity else currentGravity
            if (distance > step * 200) {
                applyForce(direction.mul(gravity / distance.sqr()), position)
            }
        }
    }

    private fun interpolate(start: Float, end: Float, f: Float) = start + f * (end - start)

}