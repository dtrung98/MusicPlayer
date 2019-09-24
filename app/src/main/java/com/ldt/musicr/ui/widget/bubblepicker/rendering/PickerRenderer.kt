package com.ldt.musicr.ui.widget.bubblepicker.rendering

import android.opengl.GLES20
import android.opengl.GLES20.*
import android.util.Log
import android.view.View
import com.ldt.musicr.ui.widget.GLTextureView
import com.ldt.musicr.ui.widget.bubblepicker.*
import com.ldt.musicr.ui.widget.bubblepicker.model.Color
import com.ldt.musicr.ui.widget.bubblepicker.model.PickerItem
import com.ldt.musicr.ui.widget.bubblepicker.physics.PhysicsEngine
import com.ldt.musicr.ui.widget.bubblepicker.rendering.BubbleShader.A_POSITION
import com.ldt.musicr.ui.widget.bubblepicker.rendering.BubbleShader.A_UV
import com.ldt.musicr.ui.widget.bubblepicker.rendering.BubbleShader.U_BACKGROUND
import com.ldt.musicr.ui.widget.bubblepicker.rendering.BubbleShader.fragmentShader
import com.ldt.musicr.ui.widget.bubblepicker.rendering.BubbleShader.vertexShader
import org.jbox2d.common.Vec2
import java.nio.FloatBuffer
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10
import kotlin.collections.ArrayList
import kotlin.math.sqrt

inline val <reified T> T.TAG: String
    get() = T::class.java.simpleName

public class PickerRenderer(val glView: View) : GLTextureView.Renderer {
    override fun onSurfaceDestroyed(gl: GL10?) {
    }

    var backgroundColor: Color? = null
    var maxSelectedCount: Int? = null
        set(value) {
            PhysicsEngine.maxSelectedCount = value
            field = value
        }

    var listener: BubblePickerListener? = null
    var pickerItems: ArrayList<PickerItem> = ArrayList()
    val selectedPickerItemItems: List<PickerItem?>
        get() = PhysicsEngine.selectedCircleBodies.map { renderCircles.firstOrNull { circle -> circle.circleBody == it }?.pickerItem }
    var centerImmediately = false
        set(value) {
            field = value
            PhysicsEngine.centerImmediately = value
        }

    var adapter: Adapter? = null
        set(value) {
            field = value
            if (value != null) {
              updateAllItems()
            }
        }

    var decorator: Decorator? = null
        set(value) {
            field = value
            if (value != null) {
                updateAllItems()
            }
        }
    private fun removeItem(position : Int) {
        PhysicsEngine.removeCircle(position)
    }
    private fun removeAllItems() {
        PhysicsEngine.removeAllCircles()
    }

    private fun updateAllItems() {
        Log.d(TAG,"prepare to clear")
        clear()
        Log.d(TAG,"cleared")
        synchronized(pickerItems) {
            pickerItems.clear()
            Log.d(TAG, "cleared items")
            if (adapter != null) {
                val count = (adapter as Adapter).itemCount;
                if (count != 0) {
                    for (item in 0 until count) {
                        val pick = PickerItem()
                        (adapter as Adapter).onBindItem(pick, true, item)
                        Log.d(TAG, "adding item $item")
                        pickerItems.add(pick)
                        Log.d(TAG, "added")
                    }
                }
            }
        }
        Log.d(TAG,"prepare to initialize")
        initialize()
    }

    private var programId = 0
    private var verticesBuffer: FloatBuffer? = null
    private var uvBuffer: FloatBuffer? = null
    private var vertices: FloatArray? = null
    private var textureVertices: FloatArray? = null
    private var textureIds: IntArray? = null

    private val scaleX: Float
        // if scaleX is h/w if w < h (this means scaleX > 1), else 1
        get() = if (glView.width < glView.height) glView.height.toFloat() / glView.width.toFloat() else 1f

        // if scaleY is w/h if h > w, else 1
    private val scaleY: Float
        get() = if (glView.width < glView.height) 1f else glView.width.toFloat() / glView.height.toFloat()
    private val renderCircles = ArrayList<CircleRenderItem>()

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        glClearColor(backgroundColor?.red ?: 1f, backgroundColor?.green ?: 1f,
                backgroundColor?.blue ?: 1f, backgroundColor?.alpha ?: 0f)
        enableTransparency()
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        // set Viewport
        glViewport(0,0, width, height)

        val w = width.toFloat()
        val h = height.toFloat()
        PhysicsEngine.onViewPortSizeChanged(w, h, decorator?.getCircleRadiusUnit(w,h) ?: 0f)

    }

    private fun recreate() {
        synchronized(renderCircles) {

        }
    }

    override fun onDrawFrame(gl: GL10?) {
        calculateVertices()
        PhysicsEngine.onFrameUpdated()
        drawFrame()
    }

    private fun initialize() {

        PhysicsEngine.centerImmediately = centerImmediately
        synchronized(renderCircles) {
            val list = Engine.build(pickerItems.size, scaleX, scaleY)
            list.forEachIndexed { index, item ->
                renderCircles.add(CircleRenderItem(pickerItems[index], item))
            }
        }

        if(pickerItems.isNotEmpty()) {
            pickerItems.forEach { if (it.isSelected) Engine.onTap(renderCircles.first { circle -> circle.pickerItem == it }) }
            if (textureIds == null) textureIds = IntArray(renderCircles.size * 2)
            initializeArrays()
        }
    }

    private fun initializeArrays() {
        vertices = FloatArray(renderCircles.size * 8)
        textureVertices = FloatArray(renderCircles.size * 8)
        renderCircles.forEachIndexed { i, item -> initializeItem(item, i) }
        verticesBuffer = vertices?.toFloatBuffer()
        uvBuffer = textureVertices?.toFloatBuffer()
    }

    private fun initializeItem(item: CircleRenderItem, index: Int) {
        initializeVertices(item, index)
        textureVertices?.passTextureVertices(index)
        item.bindTextures(textureIds ?: IntArray(0), index)
    }

    private fun calculateVertices() {
        renderCircles.forEachIndexed { i, item -> initializeVertices(item, i) }
        vertices?.forEachIndexed { i, float -> verticesBuffer?.put(i, float) }
    }

    private fun initializeVertices(body: CircleRenderItem, index: Int) {
        val radius = body.radius
        val radiusX = radius * scaleX
        val radiusY = radius * scaleY

        body.initialPosition.apply {
            vertices?.put(8 * index, floatArrayOf(x - radiusX, y + radiusY, x - radiusX, y - radiusY,
                    x + radiusX, y + radiusY, x + radiusX, y - radiusY))
        }
    }

    private fun drawFrame() {

        glClear(GL_COLOR_BUFFER_BIT)
        glUniform4f(glGetUniformLocation(programId, U_BACKGROUND), 1f, 1f, 1f, 0f)
        verticesBuffer?.passToShader(programId, A_POSITION)
        uvBuffer?.passToShader(programId, A_UV)

        synchronized(renderCircles) {
            renderCircles.forEachIndexed { i, circle ->
                circle.drawItself(programId, i, scaleX, scaleY)
            }
        }
    }

    private fun enableTransparency() {
        glEnable(GLES20.GL_BLEND)
        glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA)
        attachShaders()
    }

    private fun attachShaders() {
        programId = createProgram(createShader(GL_VERTEX_SHADER, vertexShader), createShader(GL_FRAGMENT_SHADER, fragmentShader))
        glUseProgram(programId)
    }

    private fun createProgram(vertexShader: Int, fragmentShader: Int) = glCreateProgram().apply {
        glAttachShader(this, vertexShader)
        glAttachShader(this, fragmentShader)
        glLinkProgram(this)
    }

    private fun createShader(type: Int, shader: String) = GLES20.glCreateShader(type).apply {
        glShaderSource(this, shader)
        glCompileShader(this)
    }

    fun swipe(x: Float, y: Float) = Engine.swipe(x.convertValue(glView.width, scaleX),
            y.convertValue(glView.height, scaleY))

    fun release() = Engine.release()

    private fun getItemPos(position: Vec2) = position.let {
        val x = it.x.convertPoint(glView.width, scaleX)
        val y = it.y.convertPoint(glView.height, scaleY)
        renderCircles.indexOfFirst { sqrt(((x - it.x).sqr() + (y - it.y).sqr()).toDouble()) <= it.radius }
       // circles.find { Math.sqrt(((x - it.x).sqr() + (y - it.y).sqr()).toDouble()) <= it.radius }
    }

    private fun getItem(position: Vec2) = position.let {
        val x = it.x.convertPoint(glView.width, scaleX)
        val y = it.y.convertPoint(glView.height, scaleY)
         renderCircles.find { Math.sqrt(((x - it.x).sqr() + (y - it.y).sqr()).toDouble()) <= it.radius }
    }

    fun onTap(x: Float, y: Float) = getItemPos(Vec2(x, glView.height - y)).apply {
        if (this >= 0) {
            val item = renderCircles[this]
            if (Engine.onTap(item)) {
                listener?.let {
                    if (item.circleBody.increased) it.onBubbleDeselected(item.pickerItem, this) else it.onBubbleSelected(item.pickerItem, this)
                }
            }
        }
    }

    fun notifyItemRemoved(i: Int) {

    }

    fun notifyAllItemRemove() {

    }

    fun notifyItemInserted(i: Int) {

    }

    fun notifyDataSetChanged() {
        removeAllItems()
    }

    fun notifyItemChanged(i: Int) {

    }

    fun notifySelfChanged(i: Int) {

    }

}