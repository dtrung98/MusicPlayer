package com.ldt.musicr.ui.widget.bubblepicker.physics

import android.util.Log
import com.ldt.musicr.ui.widget.bubblepicker.model.PickerItem
import org.jbox2d.common.Vec2
import org.jbox2d.dynamics.World

object PhysicsEngine {
    // World that handles everything
    private var world = World(Vec2(0f,0f),false)

    // All Circle Shapes
    private val circleBodies : ArrayList<CircleBody> = ArrayList()

    // Borders
    private val lineBorders : ArrayList<Border> = ArrayList()

    private var gravityPoint = Vec2(0f,0f)
    private var isOnTouch = false

    private var normalGravityValue = 6f
    private var enhanceGravityValue = 55f

    private val currentGravityValue: Float
    get() = if(isOnTouch) enhanceGravityValue else normalGravityValue

    private var isWorldInit  = false
    private fun initWorld() {
        isWorldInit = true
        createBorders()
    }

    public val floorYValue = 0.5f

    private fun createBorders() {
       lineBorders.add(
                Border(world, Vec2(0f, floorYValue /scaleY), Border.HORIZONTAL))

        lineBorders.add(
                Border(world, Vec2(0f, -floorYValue / scaleY), Border.HORIZONTAL))
    }

    private var scaleX = 1f;
    private var scaleY = 1f;

    public fun onViewPortSizeChanged(width : Float, height : Float) {

        scaleX = if(width < height) height/width else 1f
        scaleY = if(width < height) 1f else width/height

        if(!isWorldInit)
            initWorld()

        // update all items
        // update borders
        // update circle bodies
    }

    public fun onViewPortUpdated() {
        recordValue()

    }

    /**
     * Call to create new circle shape
     */
    private fun onAddPickerItem(pickerItem : PickerItem) : CircleBody {

    }

    /**
     *  Call to create multiple circle shapes
     */
    private fun onAddPickerItems() : List<CircleBody> {

    }

    private fun onRemovePickerItem(position : Int) {

    }

    private fun onRemoveItems(itemPos : List<Int>) {

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
}