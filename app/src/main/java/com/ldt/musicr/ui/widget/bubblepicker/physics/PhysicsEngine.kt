package com.ldt.musicr.ui.widget.bubblepicker.physics

import android.util.Log
import com.ldt.musicr.ui.widget.bubblepicker.model.PickerItem
import org.jbox2d.common.Vec2
import org.jbox2d.dynamics.World
import java.util.*
import kotlin.collections.ArrayList

object PhysicsEngine {
    // World that handles everything
    private var world = World(Vec2(0f,0f),false)
    val step = 0.0005f // for a loop
    val forceDelta : Float
    get() {
        return step*200f*60f/ deltaInSecond
    }

    val selectedCircleBodies : List<CircleBody>
    get() = circleBodies.filter { it.state==CircleBody.STATE_ENHANCED || it.state == CircleBody.STATE_MOTION_ENHANCE }
    var maxSelectedCount: Int? = null

    // All Circle Shapes
    private val circleBodies : ArrayList<CircleBody> = ArrayList()

    // Removing Circle Shapes

    // Borders
    private val lineBorders : ArrayList<Border> = ArrayList()

    var gravityPoint = Vec2(0f,0f)
    private var isOnTouch = false

    var normalGravityValue = 6f
    var touchGravityValue = 55f
    var enhanceGravityValue = normalGravityValue *1.3f

    private val currentGravityValue: Float
    get() = if(isOnTouch) touchGravityValue else normalGravityValue

    var centerImmediately = false
    private val startX
        get() = if (centerImmediately) 0.5f else 2.2f

    private var isWorldInit  = false
    private fun initWorld() {
        isWorldInit = true
        updateBorders()
    }

    public val floorYValue = 0.5f

    private fun updateBorders() {
        if(lineBorders.isEmpty()) {
            lineBorders.add(
                    Border(world, Vec2(0f, floorYValue / scaleY), Border.HORIZONTAL))

            lineBorders.add(
                    Border(world, Vec2(0f, -floorYValue / scaleY), Border.HORIZONTAL))
        } else {
            lineBorders[0].apply {
                position.y = floorYValue / scaleY;
                updatePosition()
            }

            lineBorders[1].apply {
                position.y = - floorYValue / scaleY
                updatePosition()
            }
        }
    }

    private fun updateCircleBodies() {
        synchronized(circleBodies) {
            circleBodies.forEach { circleBody ->
                circleBody.updateUnitValue(currentUnitValue)
            }
        }
    }

    private var scaleX = 1f
    private var scaleY = 1f

    private var currentUnitValue = 0f
    val isUnitAvailable : Boolean
    get() {
        return currentUnitValue >0f
    }

    public fun interpolate(start: Float, end: Float, f: Float) = start + f * (end - start)

    public fun onViewPortSizeChanged(width : Float, height : Float, radiusUnitValue  : Float) {

        scaleX = if(width < height) height/width else 1f
        scaleY = if(width < height) 1f else width/height

        currentUnitValue = radiusUnitValue
        // update all items ???
        // update borders
        if(isUnitAvailable) {
            updateBorders()
            // update circle bodies
            updateCircleBodies()
        }
    }

    public fun onFrameUpdated() : Boolean {
        recordValue()
        if(isUnitAvailable) {

            synchronized(circleBodies) {
                circleBodies.forEach {
                    it.step(deltaInSecond)
                }
            }

            world.step(deltaInSecond, 11, 11)
            return true
        }
        return false
    }
    private val rnd = Random()

    /**
     * Call to create new circle shape
     */
    public fun createCircle(pickerItem : PickerItem) : CircleBody {
        val x = if (rnd.nextBoolean()) -PhysicsEngine.startX else PhysicsEngine.startX
        val y = if (rnd.nextBoolean()) -0.5f / scaleY else 0.5f / scaleY
        return CircleBody(world, Vec2(x,y), currentUnitValue,pickerItem.radiusUnit)
    }

    public fun addCircle(circleBody: CircleBody) {
        circleBodies.add(circleBody)
    }

    /**
     *  Call to create multiple circle shapes
     */
    public fun addCircles(circles : List<CircleBody>) {
        circleBodies.addAll(circles)
    }

    public fun removeCircle(position : Int) {
        if(!circleBodies[position].isDeath())
        circleBodies[position].runMotion(CircleBody.STATE_MOTION_HIDE, CircleBody.STATE_DEATH)
    }

    public fun removeCircles(itemPos : List<CircleBody>) {
        itemPos.forEach {
            if(!it.isDeath())
            it.runMotion(CircleBody.STATE_MOTION_HIDE,CircleBody.STATE_DEATH)
        }
    }

    public fun removeAllCircles() {
        circleBodies.forEach{
            if(!it.isDeath())
            it.runMotion(CircleBody.STATE_MOTION_HIDE, CircleBody.STATE_DEATH)
        }
    }

    public fun selfRemoveCircle(circle : CircleBody) {
        circleBodies.remove(circle)
    }

    private var firstTime = true
    private var startTime: Long = 0
    private var deltaInMilli: Long = 0
    private var deltaInSecond: Float = 0f
    private var totalRunningTime: Long = 0
    private var frames: Long = 0
    private val FRAME_INTERVAL = 1000f / 60

    private fun recordValue() {
        if(firstTime) {
            frames = 0
            deltaInMilli = 0
            totalRunningTime = 0
            startTime = System.currentTimeMillis()
            firstTime = false
        } else {
            val current = System.currentTimeMillis()
            deltaInMilli = current - startTime - totalRunningTime

            totalRunningTime += deltaInMilli
            frames++
        }
        deltaInSecond = deltaInMilli/1000f
        Log.i("Engine","delta = "+ deltaInMilli)
    }
}