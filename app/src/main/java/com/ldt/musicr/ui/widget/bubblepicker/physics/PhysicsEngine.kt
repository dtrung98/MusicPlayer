package com.ldt.musicr.ui.widget.bubblepicker.physics

import android.util.Log
import com.ldt.musicr.ui.widget.bubblepicker.model.PickerItem
import org.jbox2d.common.Vec2
import org.jbox2d.dynamics.Body
import org.jbox2d.dynamics.World
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.abs

object PhysicsEngine {
    // World that handles everything
    private var world = World(Vec2(0f,0f),false)
    val step = 0.0005f // for a loop
    val forceDelta : Float
    get() {
        return step*200f*60f/ deltaInSecond
    }

    var selectedCircleBodies : ArrayList<CircleBody> = ArrayList()
    var maxSelectedCount: Int? = null

    // All Circle Shapes
    private val circleBodies : ArrayList<CircleBody> = ArrayList()

    // Removing Circle Shapes

    // Borders
    private val lineBorders : ArrayList<Border> = ArrayList()

    var gravityPoint = Vec2(0f,0f)
    private var isOnTouch = false

    var normalGravityValue = 0.1f
    var touchGravityValue = 0.3f

    val enhanceGravityValue :Float
            get() = currentGravityValue*1.3f

    val currentGravityValue: Float
    get() = if(isOnTouch) touchGravityValue else normalGravityValue

    var centerImmediately = false
    private val startX
        get() = if (centerImmediately) 0.5f else 2.2f

    private var isWorldInit  = false
    private fun initWorld() {
        isWorldInit = true
        updateBorders()
    }

    val floorYValue = 0.5f

    private fun updateBorders() {
        if(lineBorders.isEmpty()) {
            lineBorders.add(
                    Border(world, Vec2(0f, floorYValue / scaleY), Border.HORIZONTAL))

            lineBorders.add(
                    Border(world, Vec2(0f, -floorYValue / scaleY), Border.HORIZONTAL))
        } else {
            lineBorders[0].apply {
                position.y = floorYValue / scaleY
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
                circleBody.updateSizePerUnitValue(currentUnitValue)
            }
        }
    }

    internal var scaleX = 1f
    internal var scaleY = 1f

    private var currentUnitValue = 0f
    val isUnitAvailable : Boolean
    get() {
        return currentUnitValue >0f
    }

    fun interpolate(start: Float, end: Float, f: Float) = start + f * (end - start)

    fun onRadiusUnitChanged(value : Float) {
        currentUnitValue = value
        // update borders
        if(isUnitAvailable) {
            updateBorders()
            // update circle bodies
            updateCircleBodies()
        }
    }

    fun onViewPortSizeChanged(width : Float, height : Float, radiusUnitValue  : Float) {

        scaleX = if(width < height) height/width else 1f
        scaleY = if(width < height) 1f else width/height

        onRadiusUnitChanged(radiusUnitValue)
    }

    @Synchronized
    fun onFrameUpdated() : Boolean {
        recordValue()

        if (isUnitAvailable && circleBodies.isNotEmpty()) {

            val iterator = circleBodies.iterator()
            while(iterator.hasNext()) {
                val item = iterator.next()
                if(item.isDeath()) {
                    item.destroy()
                    iterator.remove()
                    Log.d("PhysicEngine","remove circle "+ item.id)
                }
            }

            circleBodies.forEach {
                it.step(deltaInSecond)
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
    fun createCircle(pickerItem : PickerItem) : CircleBody {
        val x = if (rnd.nextBoolean()) -PhysicsEngine.startX else PhysicsEngine.startX
        val y = if (rnd.nextBoolean()) - 0.5f / scaleY else 0.5f / scaleY
        return CircleBody(CircleBody.nextId,world, Vec2(x,y), currentUnitValue,pickerItem.radiusUnit)
    }

    fun addCircle(circleBody: CircleBody) {
        circleBodies.add(circleBody)
    }

    /**
     *  Call to create multiple circle shapes
     */
    fun addCircles(circles : List<CircleBody>) {
        circleBodies.addAll(circles)
    }

    fun removeCircle(position : Int) {
        if(!circleBodies[position].isDeath())
        circleBodies[position].runMotion(CircleBody.STATE_MOTION_HIDE, CircleBody.STATE_DEATH)
    }

    fun removeCircles(itemPos : List<CircleBody>) {
        itemPos.forEach {
            if(!it.isDeath())
            it.runMotion(CircleBody.STATE_MOTION_HIDE,CircleBody.STATE_DEATH)
        }
    }

    fun orderToRemoveAllCircles() {
        circleBodies.forEach{
            if(!it.isBusy&&!it.isDeath())
            it.runMotion(CircleBody.STATE_MOTION_HIDE, CircleBody.STATE_DEATH)
        }
    }

    fun destroyBody(body : Body) {
        world.destroyBody(body)
    }

    fun onTap(item: CircleBody): Boolean {
        if(item.isBusy) return false
        if(!selectedCircleBodies.contains(item)) {
            item.runMotion(CircleBody.STATE_MOTION_ENHANCE,CircleBody.STATE_ENHANCED)
            selectedCircleBodies.add(item)
        } else {
            item.runMotion(CircleBody.STATE_MOTION_ENHANCE_REVERSE, CircleBody.STATE_NO_MOTION)
            selectedCircleBodies.remove(item)
        }
        return true
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
        Log.i("Engine","delta = "+ deltaInMilli+", circle size = "+ circleBodies.size)
    }

    fun swipe(x: Float, y: Float) {
        if (Math.abs(gravityPoint.x) < 2) gravityPoint.x += -x
        if (Math.abs(gravityPoint.y) < 0.5f / scaleY) gravityPoint.y += y
       // increasedGravity = standardIncreasedGravity * Math.abs(x * 13) * Math.abs(y * 13)
       // touchGravityValue = 650f * abs(x * 13) * abs(y * 13)
        isOnTouch = true
    }

    fun onTouchEnd() {
        gravityPoint.setZero()
        isOnTouch = false
    }

    fun deleteDeathCircle(circleBody: CircleBody) {
        if(circleBodies.remove(circleBody)) {
            circleBody.destroy()
            Log.d("PhysicEngine","delete circle id "+ circleBody.id)
        } else Log.d("PhysicEngine","not existed circle")
        Log.d("PhysicEngine","current bodies size = "+ circleBodies.size)
    }
}