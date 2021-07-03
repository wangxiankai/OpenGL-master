package com.wxk.opengl.l.first

import android.content.Context
import android.opengl.GLES10
import android.opengl.GLES20.*
import android.opengl.GLSurfaceView
import com.wxk.opengl.l.R
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

/**
 * Created on 2021/6/28 17 : 41
 * Author      : wangxiankai
 * Email       : wangkxk@foxmail.com
 * Description :
 */
class AirHockeyRenderer(private val context: Context) : GLSurfaceView.Renderer {

    private var programId: Int = 0
    private var vertexData: FloatBuffer
    private val POSITION_COMPONENT_COUNT: Int = 2
    private val BYTES_PER_FLOAT = 8

    //顶点坐标数据
    private var tableVertices = floatArrayOf(
            0f, 0f,
            0f, 14f,
            9f, 14f,
            9f, 0f
    )

    //顶点坐标数据
    private val tableVerticesWithTriangles = floatArrayOf(
            //第一个三角形
            -0.5f, -0.5f,
            0.5f, 0.5f,
            -0.5f, 0.5f,
            //第二个三角形
            -0.5f, -0.5f,
            0.5f, -0.5f,
            0.5f, 0.5f,
            //线条 1
            -0.5f, 0f,
            0.5f, 0f,
            //木槌坐标
            0f, -0.25f,
            0f, 0.25f
    )

    private val vertexShaderSource: String = TextResourceReader.readTextFileFromRes(context, R.raw.simple_vertex_shader)
    private val fragmentShaderSource: String = TextResourceReader.readTextFileFromRes(context, R.raw.simple_fragment_shader)

    init {
        //将顶点坐标通过缓冲区bf装载
        vertexData = ByteBuffer.allocateDirect(tableVerticesWithTriangles.size * BYTES_PER_FLOAT)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer()
        vertexData.put(tableVerticesWithTriangles)
    }

    //定义与片元着色器源代码对应的变量
    private val U_COLOR = "u_Color"
    private var uColorLocation: Int = 0
    //定义与顶点着色器源代码对应的变量
    private val A_POSITION = "a_Position"
    private var aPositionLocation: Int = 0

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        //清空颜色
        glClearColor(0.0f, 0.0f, 0.0f, 0.0f)
        //顶点着色器编译源代码
        val vertexShaderId = ShaderHelper.compileVertexShader(vertexShaderSource)
        //片元着色器编译源代码
        val fragmentShaderId = ShaderHelper.compileFragmentShader(fragmentShaderSource)
        //链接着色器，通过程序对象链接
        programId = ShaderHelper.linkProgram(vertexShaderId, fragmentShaderId)
        //检验程序对象
        ShaderHelper.validateProgram(programId)
        //通知openGL在屏幕绘画需要采用以上定义的程序对象
        glUseProgram(programId)

        //获取片元着色器的源代码uniform定义的"u_Color"变量，转化为java/kotlin语言获取到并且通过变量表示出来
        uColorLocation = glGetUniformLocation(programId, U_COLOR)
        //获取顶点着色器的源代码attribute定义的"a_Position"变量，转化为java/kotlin语言获取到并且通过变量表示出来
        aPositionLocation = glGetAttribLocation(programId, A_POSITION)

        //关联属性与顶点着色器数据的数组，一系列点坐标，通过buffer缓冲区对象装载
        vertexData.position(0) //将缓冲区内部指针指向起始位置，表示从头部开始读取数据
        glVertexAttribPointer(aPositionLocation, POSITION_COMPONENT_COUNT, GL_FLOAT,
                false, 0, vertexData)
        //通知顶点着色器源代码顶点数据已经初始化定义好，这样着色器就知道顶点坐标数据了
        glEnableVertexAttribArray(aPositionLocation)
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        //设置窗口大小
        glViewport(0, 0, width, height)
    }

    override fun onDrawFrame(gl: GL10?) {
        //清空所有颜色
        GLES10.glClear(GL_COLOR_BUFFER_BIT)
        //更新片元着色器源代码中"u_Color"变量值, "4f"代表有四个分量值，对应：RGBA
        glUniform4f(uColorLocation, 1.0f, 1.0f, 1.0f, 1.0f)
        //绘画三角图像，定义需要绘制图像类型以及顶点坐标的读取范围(顶点数组的范围, 从设置的0起始位置开始包含6组坐标)
        glDrawArrays(GL_TRIANGLES, 0, 6)
        //绘制分割线
        glUniform4f(uColorLocation, 1.0f, 0.0f, 0.0f, 1.0f) //确定线的颜色
        glDrawArrays(GL_LINES, 6, 2) //(顶点数组的范围, 从设置的6起始位置开始包含2组坐标)
        //绘制木槌点一
        glUniform4f(uColorLocation, 0.0f, 0.0f, 1.0f, 1.0f) //确定颜色
        glDrawArrays(GL_POINTS, 8, 1)//(顶点数组的范围, 从设置的8起始位置开始包含1组坐标)
        //绘制木槌点二
        glUniform4f(uColorLocation, 1.0f, 0.0f, 0.0f, 1.0f) //确定颜色
        glDrawArrays(GL_POINTS, 9, 1)//(顶点数组的范围, 从设置的9起始位置开始包含1组坐标)
    }
}