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
import com.ldt.musicr.ui.widget.bubblepicker.physics.PhysicsEngine.scaleX
import com.ldt.musicr.ui.widget.bubblepicker.physics.PhysicsEngine.scaleY
import com.ldt.musicr.ui.widget.bubblepicker.rendering.BubbleShader.A_POSITION
import com.ldt.musicr.ui.widget.bubblepicker.rendering.BubbleShader.A_UV
import com.ldt.musicr.ui.widget.bubblepicker.rendering.BubbleShader.U_BACKGROUND
import com.ldt.musicr.ui.widget.bubblepicker.rendering.BubbleShader.fragmentShader
import com.ldt.musicr.ui.widget.bubblepicker.rendering.BubbleShader.vertexShader
import org.jbox2d.common.Vec2
import java.lang.Exception
import java.nio.FloatBuffer
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10
import kotlin.collections.ArrayList
import kotlin.math.sqrt

inline val <reified T> T.TAG: String
    get() = T::class.java.simpleName

class PickerRenderer(val glView: View) : GLTextureView.Renderer {
    override fun onSurfaceDestroyed(gl: GL10?) {
    }

    var backgroundColor: Color? = null
    var maxSelectedCount: Int? = null
        set(value) {
            PhysicsEngine.maxSelectedCount = value
            field = value
        }

    var listener: BubblePickerListener? = null
    val selectedPickerItems: List<PickerItem?>
        get() = PhysicsEngine.selectedCircleBodies.map { renderCircles.firstOrNull { circle -> circle.circleBody == it }?.pickerItem }

    private val renderCircles = ArrayList<CircleRenderItem>()
    var centerImmediately = false
        set(value) {
            field = value
            PhysicsEngine.centerImmediately = value
        }

    var adapter: Adapter? = null
        set(value) {
            field = value
            if (value != null) {
              notifyDataSetChanged()
            }
        }

    private fun removeItem(position : Int) {
        PhysicsEngine.removeCircle(position)
    }

    @Synchronized
    private  fun orderToRemoveAllItem() {
        PhysicsEngine.orderToRemoveAllCircles()
    }

    @Synchronized
    private fun addAllItems() {
        if(adapter!=null) {
            val count = (adapter as Adapter).itemCount
            if (count != 0) {
                synchronized(renderCircles) {
                    for (i in 0 until count) {
                        val picker = PickerItem()
                        adapter?.onBindItem(picker, true, i)
                        renderCircles.add(CircleRenderItem(picker,PhysicsEngine.createCircle(picker)))
                    }
                }
                renderCircles.forEachIndexed { index, circleRenderItem -> PhysicsEngine.addCircle(circleRenderItem.circleBody) }
            }
        }
    }

    private var programId = 0
    private var verticesBuffer: FloatBuffer? = null
    private var uvBuffer: FloatBuffer? = null
    private var vertices: FloatArray? = null
    private var textureVertices: FloatArray? = null
    private var textureIds: IntArray? = null

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        glClearColor(backgroundColor?.red ?: 1f, backgroundColor?.green ?: 1f,
                backgroundColor?.blue ?: 1f, backgroundColor?.alpha ?: 0f)
        enableTransparency()
    }
    var width: Float = 1f
    var height: Float = 1f

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        // set Viewport
        glViewport(0,0, width, height)

       this.width = width.toFloat()
        this.height = height.toFloat()
        PhysicsEngine.onViewPortSizeChanged(this.width, this.height, adapter?.getCircleRadiusUnit(this.width,this.height) ?: 0.125f)
        recreateTextures()
    }

    @Synchronized
    override fun onDrawFrame(gl: GL10?) {
        val iterator = renderCircles.iterator()
        while(iterator.hasNext()) {
            val item = iterator.next()
            if(item.circleBody.isDeath()) {
                iterator.remove()
                shouldRecreateTexture = true
                Log.d("PickerRenderer","remove circle "+ item.circleBody.id)
            }
        }


        PhysicsEngine.onFrameUpdated()
        if(shouldRecreateTexture) recreateTextures()
        calculateVertices()
        drawFrame()
        Log.d("PickerRenderer","current size = "+ renderCircles.size+", textureIDs size = "+ textureIds?.size)

    }

    @Synchronized
    fun deleteTexture(textureIds: IntArray, index: Int) {
        glDeleteTextures(1,textureIds,index*2)
        glDeleteTextures(1,textureIds,index*2 + 1)
    }

    @Synchronized
     private fun recreateTextures() {
        // delete all textures
        if(textureIds!=null && (textureIds as IntArray).size == renderCircles.size *2) {
            (textureIds as IntArray).forEachIndexed { index, i ->
                try {
                deleteTexture((textureIds as IntArray),index) }
                catch (e : Exception) {}
            }
        }
        textureIds = null

        // create new textures if any

        if(renderCircles.isNotEmpty()) {
            textureIds = IntArray(renderCircles.size * 2)
            recreateArrays()
        }
        shouldRecreateTexture = false
    }

    private fun recreateArrays() {
        vertices = FloatArray(renderCircles.size * 8)
        textureVertices = FloatArray(renderCircles.size * 8)
        renderCircles.forEachIndexed { i, item -> recreateTextureItem(item, i) }
        verticesBuffer = vertices?.toFloatBuffer()
        uvBuffer = textureVertices?.toFloatBuffer()
    }

    private fun recreateTextureItem(item: CircleRenderItem, index: Int) {
        initializeVertices(item, index)
        textureVertices?.passTextureVertices(index)
        item.bindTextures(textureIds ?: IntArray(0), index)
    }

    private fun calculateVertices() {
        if(renderCircles.isNotEmpty()) {
            renderCircles.forEachIndexed { i, item -> initializeVertices(item, i) }
            vertices?.forEachIndexed { i, float -> verticesBuffer?.put(i, float) }
        }
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
                circle.drawItself(programId, i, PhysicsEngine.scaleX, PhysicsEngine.scaleY)
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

    fun swipe(x: Float, y: Float) = PhysicsEngine.swipe(x.convertValue(glView.width, scaleX),
            y.convertValue(glView.height, scaleY))

    fun onTouchEnd() = PhysicsEngine.onTouchEnd()

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
            if (PhysicsEngine.onTap(item.circleBody)) {
                listener?.let {
                    if (item.circleBody.isEnhanced()) it.onBubbleDeselected(item.pickerItem, this) else it.onBubbleSelected(item.pickerItem, this)
                }
            }
        }
    }

    fun notifyItemRemoved(i: Int) {

    }

    @Synchronized
    fun notifyAllItemRemove() {
        orderToRemoveAllItem()
    }

    @Synchronized
    fun notifyItemInserted(i: Int) {

    }

    @Synchronized
    fun notifyDataSetChanged() {
        orderToRemoveAllItem()
        addAllItems()
        shouldRecreateTexture = true
    }
    private var shouldRecreateTexture : Boolean = true

    @Synchronized
    fun notifyItemChanged(i: Int) {

    }

    fun notifySelfChanged(i: Int) {

    }

}