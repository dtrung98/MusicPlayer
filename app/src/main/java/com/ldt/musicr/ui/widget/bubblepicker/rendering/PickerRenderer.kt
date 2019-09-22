package com.ldt.musicr.ui.widget.bubblepicker.rendering

import android.opengl.GLES20
import android.opengl.GLES20.*
import android.util.Log
import android.view.View
import com.ldt.musicr.ui.widget.GLTextureView
import com.ldt.musicr.ui.widget.bubblepicker.*
import com.ldt.musicr.ui.widget.bubblepicker.model.Color
import com.ldt.musicr.ui.widget.bubblepicker.model.PickerItem
import com.ldt.musicr.ui.widget.bubblepicker.physics.Engine
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

/**
 * Created by irinagalata on 1/19/17.
 */
inline val <reified T> T.TAG: String
    get() = T::class.java.simpleName

class TexturePickerRenderer(val glView: View) : GLTextureView.Renderer {
    override fun onSurfaceDestroyed(gl: GL10?) {
    }

    var backgroundColor: Color? = null
    var maxSelectedCount: Int? = null
        set(value) {
            Engine.maxSelectedCount = value
            field = value
        }
    var bubbleSize = 50
        set(value) {
            Engine.radius = value
            field = value
        }
    var listener: BubblePickerListener? = null
    var items: ArrayList<PickerItem> = ArrayList()
    val selectedItems: List<PickerItem?>
        get() = Engine.selectedBodies.map { circles.firstOrNull { circle -> circle.circleBody == it }?.pickerItem }
    var centerImmediately = false
        set(value) {
            field = value
            Engine.centerImmediately = value
        }

    var adapter: Adapter? = null
        set(value) {
            field = value
            if (value != null) {
              updateAllItems()
            }
        }

    private fun updateAllItems() {
        Log.d(TAG,"prepare to clear")
        clear()
        Log.d(TAG,"cleared")
        synchronized(items) {
            items.clear()
            Log.d(TAG, "cleared items")
            if (adapter != null) {
                val count = (adapter as Adapter).itemCount;
                if (count != 0) {
                    for (item in 0 until count) {
                        val pick = PickerItem()
                        (adapter as Adapter).onBindItem(pick, true, item)
                        Log.d(TAG, "adding item $item")
                        items.add(pick)
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
        get() = if (glView.width < glView.height) glView.height.toFloat() / glView.width.toFloat() else 1f
    private val scaleY: Float
        get() = if (glView.width < glView.height) 1f else glView.width.toFloat() / glView.height.toFloat()
    private val circles = ArrayList<Item>()

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        glClearColor(backgroundColor?.red ?: 1f, backgroundColor?.green ?: 1f,
                backgroundColor?.blue ?: 1f, backgroundColor?.alpha ?: 0f)
        enableTransparency()
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        glViewport(0, 0, width, height)
        clear()
        initialize()
    }

    override fun onDrawFrame(gl: GL10?) {
        calculateVertices()
        Engine.move()
        drawFrame()
    }

    private fun initialize() {

        Engine.centerImmediately = centerImmediately
        synchronized(circles) {
            Engine.build(items.size, scaleX, scaleY).forEachIndexed { index, body ->
                circles.add(Item(items[index], body))
            }
        }
        if(!items.isEmpty()) {
            items.forEach { if (it.isSelected) Engine.resize(circles.first { circle -> circle.pickerItem == it }) }
            if (textureIds == null) textureIds = IntArray(circles.size * 2)
            initializeArrays()
        }
    }

    private fun initializeArrays() {
        vertices = FloatArray(circles.size * 8)
        textureVertices = FloatArray(circles.size * 8)
        circles.forEachIndexed { i, item -> initializeItem(item, i) }
        verticesBuffer = vertices?.toFloatBuffer()
        uvBuffer = textureVertices?.toFloatBuffer()
    }

    private fun initializeItem(item: Item, index: Int) {
        initializeVertices(item, index)
        textureVertices?.passTextureVertices(index)
        item.bindTextures(textureIds ?: IntArray(0), index)
    }

    private fun calculateVertices() {
        circles.forEachIndexed { i, item -> initializeVertices(item, i) }
        vertices?.forEachIndexed { i, float -> verticesBuffer?.put(i, float) }
    }

    private fun initializeVertices(body: Item, index: Int) {
        val radius = body.radius
        val radiusX = radius * scaleX
        val radiusY = radius * scaleY

        body.initialPosition.apply {
            vertices?.put(8 * index, floatArrayOf(x - radiusX, y + radiusY, x - radiusX, y - radiusY,
                    x + radiusX, y + radiusY, x + radiusX, y - radiusY))
        }
    }

    private var tick : Long  = System.currentTimeMillis()

    private fun drawFrame() {

        glClear(GL_COLOR_BUFFER_BIT)
        glUniform4f(glGetUniformLocation(programId, U_BACKGROUND), 1f, 1f, 1f, 0f)
        verticesBuffer?.passToShader(programId, A_POSITION)
        uvBuffer?.passToShader(programId, A_UV)

        synchronized(circles) {
            circles.forEachIndexed { i, circle ->
                circle.drawItself(programId, i, scaleX, scaleY)
            }
        }
        val current = System.currentTimeMillis()
        val fps =  (current - tick+0f)
        //Log.d(TAG, "fps: $fps")
        tick = current
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
        circles.indexOfFirst { Math.sqrt(((x - it.x).sqr() + (y - it.y).sqr()).toDouble()) <= it.radius }
       // circles.find { Math.sqrt(((x - it.x).sqr() + (y - it.y).sqr()).toDouble()) <= it.radius }
    }

    private fun getItem(position: Vec2) = position.let {
        val x = it.x.convertPoint(glView.width, scaleX)
        val y = it.y.convertPoint(glView.height, scaleY)
         circles.find { Math.sqrt(((x - it.x).sqr() + (y - it.y).sqr()).toDouble()) <= it.radius }
    }

    fun onTap(x: Float, y: Float) = getItemPos(Vec2(x, glView.height - y)).apply {
        if (this >= 0) {
            val item = circles[this]
            if (Engine.resize(item)) {
                listener?.let {
                    if (item.circleBody.increased) it.onBubbleDeselected(item.pickerItem, this) else it.onBubbleSelected(item.pickerItem, this)
                }
            }
        }
    }

    private fun clear() {

        synchronized(circles) {
            circles.clear()
        }

        synchronized(Engine) {
            Engine.clear()
        }
    }

    fun notifyItemRemoved(i: Int) {

    }

    fun notifyItemInserted(i: Int) {

    }

    fun notifyDataSetChanged() {
        updateAllItems()
    }

    fun notifyItemChanged(i: Int) {

    }

    fun notifySelfChanged(i: Int) {

    }

}