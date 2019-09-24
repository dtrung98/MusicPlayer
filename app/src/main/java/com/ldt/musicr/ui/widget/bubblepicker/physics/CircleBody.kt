package com.ldt.musicr.ui.widget.bubblepicker.physics

import android.view.animation.Interpolator
import android.view.animation.LinearInterpolator
import com.ldt.musicr.ui.widget.bubblepicker.sqr
import org.jbox2d.collision.shapes.CircleShape
import org.jbox2d.common.Vec2
import org.jbox2d.dynamics.*

/**
 * Created by irinagalata on 1/26/17.
 */
class CircleBody(val world: World, var position: Vec2, var sizePerUnit: Float, var unitValue : Float) {
    companion object {
        const val STATE_JUST_CREATE = 0
        const val STATE_MOTION_APPEAR = 1
        const val STATE_NO_MOTION = 2
        const val STATE_MOTION_HIDE = 3
        const val STATE_DEATH = 4
        const val STATE_MOTION_ENHANCE = 5
        const val STATE_ENHANCED = 6
        const val STATE_MOTION_ENHANCE_REVERSE = 7

        const val DURATION_APPEAR = 0.65f
        const val DURATION_HIDE = 0.65f
        const val ENHANCE_VALUE = 1.25f
    }

    var state : Int = STATE_MOTION_APPEAR
    var nextState : Int = STATE_JUST_CREATE
    var motionRunningTime = 0f
    var isBusy = false

    public val density: Float
    get() {
        return PhysicsEngine.interpolate(0.8f,0.2f, unitValue/2f)
    }

    public fun runMotion(which : Int, endState : Int) : Boolean {
        if(!isBusy) {
            state = which
            this.nextState = endState
            motionRunningTime = 0f
            return true
        }
        return false
    }

    public fun runMotion(which : Int, endState : Int, interpolator: Interpolator) : Boolean {
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
    public fun isDeath() : Boolean {
        return state == STATE_DEATH
    }

    private fun endMotion() {
        motionRunningTime = 0f
        state = nextState
        isBusy = false
        if(isDeath()) PhysicsEngine.selfRemoveCircle(this)
    }

    private var interpolator : Interpolator = LinearInterpolator()

    public fun step(deltaInSecond : Float) {
        motionRunningTime +=deltaInSecond
        when (state) {
            STATE_JUST_CREATE -> {
            endMotion()
            }

            STATE_MOTION_APPEAR -> {
                val animatedValue = interpolator.getInterpolation(motionRunningTime/ DURATION_APPEAR)
                currentRadius = PhysicsEngine.interpolate(0f,normalRadius,animatedValue)
                updateRadius()
            }

            STATE_NO_MOTION -> {

            }

            STATE_MOTION_HIDE -> {
                val animatedValue = interpolator.getInterpolation(motionRunningTime/ DURATION_APPEAR)
                currentRadius = PhysicsEngine.interpolate(normalRadius,0f,animatedValue)
                updateRadius()
            }

            STATE_DEATH -> {

            }

            STATE_MOTION_ENHANCE -> {
                val animatedValue = interpolator.getInterpolation(motionRunningTime/ DURATION_APPEAR)
                currentRadius = PhysicsEngine.interpolate(normalRadius,normalRadius* ENHANCE_VALUE,animatedValue)
                updateRadius()
            }

            STATE_MOTION_ENHANCE_REVERSE -> {
                val animatedValue = interpolator.getInterpolation(motionRunningTime/ DURATION_APPEAR)
                currentRadius = PhysicsEngine.interpolate(normalRadius* ENHANCE_VALUE,normalRadius,animatedValue)
                updateRadius()
            }
        }

        applyGravityEffect()
    }

    private fun applyGravityEffect() {        //isVisible = centerImmediately.not()
        val direction = PhysicsEngine.gravityPoint.sub(position)
        val distance = direction.length()
        val gravity = if (state== STATE_ENHANCED) PhysicsEngine.enhanceGravityValue else PhysicsEngine.normalGravityValue
        if (distance > PhysicsEngine.forceDelta) {
            physicalBody.applyForce(direction.mul(gravity / distance.sqr()), position)
        }
    }

    lateinit var physicalBody: Body

    var isVisible = true

    private val marginSizeUnit = 0.1f

    private val marginSize : Float
    get() {
        return marginSizeUnit * unitValue
    }
    private val damping = 25f
    private val shape: CircleShape
        get() = CircleShape().apply {
            m_radius = currentRadius + marginSizeUnit
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

    var normalRadius : Float = sizePerUnit * unitValue
    var currentRadius : Float = normalRadius

    public fun updateUnitValue(value :Float) {
        currentRadius = currentRadius/unitValue*value
        normalRadius = sizePerUnit * value
        unitValue = value;
        updateRadius()
    }

    public fun updateRadius(value : Float) {
        currentRadius = value
        updateRadius()
    }
    private fun updateRadius() {
        physicalBody.fixtureList?.shape?.m_radius = currentRadius + marginSize
        physicalBody.fixtureList?.density = density
    }

    private fun clear() {
        updateRadius(normalRadius)
        state = STATE_NO_MOTION
        isBusy = false
    }

}