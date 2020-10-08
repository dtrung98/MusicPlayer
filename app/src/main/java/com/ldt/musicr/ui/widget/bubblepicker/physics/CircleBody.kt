package com.ldt.musicr.ui.widget.bubblepicker.physics

import android.util.Log
import android.view.animation.Interpolator
import android.view.animation.OvershootInterpolator
import com.ldt.musicr.ui.widget.bubblepicker.sqr
import org.jbox2d.collision.shapes.CircleShape
import org.jbox2d.common.Vec2
import org.jbox2d.dynamics.*

/**
 * Created by irinagalata on 1/26/17.
 */
class CircleBody(val id: Int, val world: World, var position: Vec2, var sizePerUnit: Float, var sizeUnit : Float) {


    companion object {
        var count = 1
        const val STATE_JUST_CREATE = 0
        const val STATE_MOTION_APPEAR = 1
        const val STATE_NO_MOTION = 2
        const val STATE_MOTION_HIDE = 3
        const val STATE_DEATH = 4
        const val STATE_MOTION_ENHANCE = 5
        const val STATE_ENHANCED = 6
        const val STATE_MOTION_ENHANCE_REVERSE = 7

        fun stateName(st: Int) : String {
           return when(st) {
                STATE_JUST_CREATE -> "Just Create"
               STATE_MOTION_APPEAR -> "Motion Appear"
               STATE_NO_MOTION -> "No Motion"
               STATE_MOTION_HIDE -> "Motion Hide"
               STATE_DEATH -> "Death"
               STATE_MOTION_ENHANCE -> "Motion Enhance"
               STATE_ENHANCED -> "Enhanced"
               STATE_MOTION_ENHANCE_REVERSE -> "Motion Enhance Reverse"
               else -> "Invalid State"
            }
        }

        const val DURATION_APPEAR = 0.65f
        const val DURATION_HIDE = 1.65f
        const val DURATION_ENHANCE = 0.35f
        const val ENHANCE_VALUE = 1.25f
        val nextId : Int
         get() = count++

    }

    var state : Int = STATE_JUST_CREATE
    var nextState : Int = STATE_MOTION_APPEAR
    var motionRunningTime = 0f
    var isBusy = false

    val density: Float
    get() {
        return 0.5f//PhysicsEngine.interpolate(0.8f,0.2f, currentRadius/2f)
    }

    fun runMotion(which : Int, endState : Int) : Boolean {
        if(!isBusy) {
            state = which
            this.nextState = endState
            motionRunningTime = 0f
            isBusy = true
            return true
        }
        return false
    }

    fun runMotion(which : Int, endState : Int, interpolator: Interpolator) : Boolean {
        if(!isBusy) {
            state = which
            this.nextState = endState
            motionRunningTime = 0f
            this.interpolator = interpolator
            isBusy = true
            return true
        }
        return false
    }
    fun isDeath() : Boolean {
        return state == STATE_DEATH
    }

    fun isEnhanced() : Boolean {
        return state == STATE_ENHANCED || state == STATE_MOTION_ENHANCE
    }

    private fun endMotion() {
        motionRunningTime = 0f
        state = nextState
        isBusy = false
    }

    private var interpolator : Interpolator = OvershootInterpolator()

    fun step(deltaInSecond : Float) {
        motionRunningTime +=deltaInSecond
        when (state) {
            STATE_JUST_CREATE -> {
            endMotion()
                runMotion(STATE_MOTION_APPEAR, STATE_NO_MOTION)
            }

            STATE_MOTION_APPEAR -> {
                var animatedValue = motionRunningTime/ DURATION_APPEAR
                if(animatedValue>1f) animatedValue = 1f
                currentRatio = PhysicsEngine.interpolate(0f,1f,interpolator.getInterpolation(animatedValue))
                updateSize()
                if(animatedValue==1f)
                    endMotion()
            }

            STATE_NO_MOTION -> {

            }

            STATE_MOTION_HIDE -> {
                var animatedValue = motionRunningTime/ DURATION_HIDE
                if(animatedValue>1f) animatedValue = 1f
                currentRatio = PhysicsEngine.interpolate(1f,0f,interpolator.getInterpolation(animatedValue))
                updateSize()
                if(animatedValue==1f) endMotion()
            }

            STATE_DEATH -> {

            }

            STATE_MOTION_ENHANCE -> {
                var animatedValue = motionRunningTime/ DURATION_ENHANCE
                if(animatedValue>1f) animatedValue = 1f
                currentRatio = PhysicsEngine.interpolate(1f,ENHANCE_VALUE,interpolator.getInterpolation(animatedValue))
                updateSize()
                if(animatedValue ==1f) endMotion()
            }

            STATE_MOTION_ENHANCE_REVERSE -> {
                var animatedValue = motionRunningTime/ DURATION_APPEAR
                if(animatedValue>1f) animatedValue = 1f
                currentRatio= PhysicsEngine.interpolate( ENHANCE_VALUE,1f,interpolator.getInterpolation(animatedValue))
                updateSize()
                if(animatedValue==1f) endMotion()
            }
        }

        applyGravityEffect(deltaInSecond)
        Log.d("CircleBody","Circle "+id+" with sizeUnit = "+sizeUnit+", radius = "+ currentRadius+", state = "+ stateName(state)+", pos = ("+physicalBody.position.x+", "+physicalBody.position.y+")")
    }

    private fun applyGravityEffect(deltaInSecond: Float) {
       // isVisible = PhysicsEngine.centerImmediately.not()
        val direction = PhysicsEngine.gravityPoint.sub(physicalBody.position)
        val distance = direction.length()
        if (distance > deltaInSecond* 200f) {
            var gravity = if (state== STATE_ENHANCED) PhysicsEngine.enhanceGravityValue else PhysicsEngine.currentGravityValue
          //  gravity = (gravity /PhysicsEngine.step) * deltaInSecond
            physicalBody.applyForce(direction.mul(gravity / distance.sqr()), physicalBody.position)
        }
    }

    lateinit var physicalBody: Body

    var isVisible = true

    private val marginSizeUnit = 0.1f

    private val marginSize : Float
    get() {
        return marginSizeUnit * sizePerUnit
    }
    private val damping = 25f
    private val shape: CircleShape
        get() = CircleShape().apply {
            m_radius = currentRadius + marginSize
            m_p.setZero()
        }

    init {
        while (true) {
            if (world.isLocked.not()) {
                initializeBody()
                break
            }
        }
    }

    private fun initializeBody() {

        val fixture = FixtureDef().apply {
            this.shape = this@CircleBody.shape
            this.density = this@CircleBody.density
        }

        val bodyDef = BodyDef().apply {
            type = BodyType.DYNAMIC
            this.position = this@CircleBody.position
        }


        physicalBody = world.createBody(bodyDef)

        physicalBody.apply {
            createFixture(fixture)
            linearDamping = damping
        }
    }

    fun enhance() {
        runMotion(STATE_MOTION_ENHANCE, STATE_ENHANCED)
    }

    fun reverseEnhance() {
        runMotion(STATE_MOTION_ENHANCE_REVERSE, STATE_NO_MOTION)
    }

    var currentRatio : Float = 1f

/*    val normalRadius : Float
        get() =  sizePerUnit * sizeUnit*/
    val currentRadius : Float
        get()= sizePerUnit * sizeUnit* currentRatio

    fun updateSizePerUnitValue(value :Float) {
        sizePerUnit = value
        updateSize()
    }

    private fun updateSize() {
        if(currentRatio<0f) currentRatio = 0f
        physicalBody.fixtureList?.shape?.m_radius = currentRadius + marginSize
        physicalBody.fixtureList?.density = density
    }

    private fun clear() {
        currentRatio = 1f
        updateSize()
        state = STATE_NO_MOTION
        isBusy = false
    }

    fun destroy() {
        PhysicsEngine.destroyBody(physicalBody)
    }
}