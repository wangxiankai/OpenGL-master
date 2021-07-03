package com.wxk.opengl.airhockeyortho.first

import android.content.Context
import android.opengl.GLES10
import android.opengl.GLES20.*
import android.opengl.GLSurfaceView
import android.opengl.Matrix.orthoM
import android.util.Log
import com.wxk.opengl.airhockeyortho.R
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
    private val BYTES_PER_FLOAT = 4

    //顶点坐标数据
    private var tableVertices = floatArrayOf(
        0f, 0f,
        0f, 14f,
        9f, 14f,
        9f, 0f
    )

    //顶点坐标数据
    private val tableVerticesWithTriangles = floatArrayOf(
        //扇形三角形(以一个点为中心点， 向四周连接)
        //三角形有abc、 acd、 ade、 aef (f点与b点重合，可以认为是一个点)
        //每个坐标点后面添加三个分量代表颜色值：r、g、b
        //坐标颜色点规则： x,y,r,g,b
        0f, 0f, 1f, 1f, 1f,        //a点
        -0.5f, -0.8f, 0.7f, 0.7f, 0.7f,  //b点
        0.5f, -0.8f, 0.7f, 0.7f, 0.7f,  //c点
        0.5f, 0.8f, 0.7f, 0.7f, 0.7f,  //d点
        -0.5f, 0.8f, 0.7f, 0.7f, 0.7f,  //e点
        -0.5f, -0.8f, 0.7f, 0.7f, 0.7f,  //f点
        //线条 1
        -0.5f, 0f, 1f, 0f, 0f,
        0.5f, 0f, 1f, 0f, 0f,
        //木槌坐标
        0f, -0.25f, 0f, 0f, 1f,
        0f, 0.25f, 1f, 0f, 0f,
    )

    private val vertexShaderSource: String =
        TextResourceReader.readTextFileFromRes(context, R.raw.simple_vertex_shader)
    private val fragmentShaderSource: String =
        TextResourceReader.readTextFileFromRes(context, R.raw.simple_fragment_shader)

    init {
        //将顶点坐标通过缓冲区bf装载
        vertexData = ByteBuffer.allocateDirect(tableVerticesWithTriangles.size * BYTES_PER_FLOAT)
            .order(ByteOrder.nativeOrder())
            .asFloatBuffer()
        vertexData.put(tableVerticesWithTriangles)
    }

    //定义与片元着色器源代码对应的变量
    //定义着色器源代码‘varying’渐变色属性

    //定义与顶点着色器源代码对应的变量
    private val A_POSITION = "a_Position"
    private var aPositionLocation: Int = 0
    private val A_COLOR = "a_Color"
    private var aColorLocation: Int = 0
    private val COLOR_COMPONENT_COUNT = 3

    //跨距，由于顶点坐标中加入了颜色向量，所以一个坐标点对应五个向量，向量单位是float是占用4个字节
    private val STRIDE = (POSITION_COMPONENT_COUNT + COLOR_COMPONENT_COUNT) * BYTES_PER_FLOAT

    //定义顶点着色器源代码的矩阵变量
    private val U_MATRIX = "u_Matrix"
    private val projectionMatrix = FloatArray(16) //存储矩阵数组
    private var uMatrixLocation: Int = 0


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
        aColorLocation = glGetAttribLocation(programId, A_COLOR)
        //获取顶点着色器的源代码attribute定义的"a_Position"变量，转化为java/kotlin语言获取到并且通过变量表示出来
        aPositionLocation = glGetAttribLocation(programId, A_POSITION)

        //关联属性与顶点着色器数据的顶点位置数组，一系列点坐标，通过buffer缓冲区对象装载
        vertexData.position(0) //将缓冲区内部指针指向起始位置，表示从头部开始读取数据
        glVertexAttribPointer(
            aPositionLocation, POSITION_COMPONENT_COUNT, GL_FLOAT,
            false, STRIDE, vertexData
        )
        //通知顶点着色器源代码顶点数据已经初始化定义好，这样着色器就知道顶点坐标数据了
        glEnableVertexAttribArray(aPositionLocation)

        //关联属性与顶点着色器数据的顶点颜色数组，一系列点坐标，通过buffer缓冲区对象装载
        vertexData.position(POSITION_COMPONENT_COUNT)
        glVertexAttribPointer(
            aColorLocation,
            COLOR_COMPONENT_COUNT,
            GL_FLOAT,
            false,
            STRIDE,
            vertexData
        )
        //通知顶点着色器源代码顶点数据已经初始化定义好，这样着色器就知道顶点颜色数据了
        glEnableVertexAttribArray(aColorLocation)

        //获取顶点着色器的源代码uniform定义的"u_Matrix"变量，转化为java/kotlin语言获取到并且通过变量表示出来
        uMatrixLocation = glGetUniformLocation(programId, U_MATRIX)
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        //设置窗口大小
        glViewport(0, 0, width, height)
        //矩阵变换
        val aspectRatio =
            if (width > height) width.toFloat() / height.toFloat()
            else height.toFloat() / width.toFloat()
        if (width > height){
            orthoM(
                projectionMatrix, 0, -aspectRatio, aspectRatio,
                -1f, 1f, -1f, 1f
            )
        } else {
            orthoM(
                projectionMatrix, 0, -1f, 1f,
                -aspectRatio, aspectRatio, -1f, 1f
            )
        }

        Log.d("LogUtils", "矩阵 ： ")
        for (i in 1..16) {
            if (i / 4 == 0){
                println("\n")
            }
            println("${projectionMatrix[i - 1]}, ")
        }
    }

    override fun onDrawFrame(gl: GL10?) {
        //清空所有颜色
        GLES10.glClear(GL_COLOR_BUFFER_BIT)
        //顶点着色器源代码矩阵赋值
        glUniformMatrix4fv(uMatrixLocation, 1, false, projectionMatrix, 0)

        //绘画扇形三角图像，定义需要绘制图像类型以及顶点坐标的读取范围(顶点数组的范围, 从设置的0起始位置开始包含6组坐标)
        glDrawArrays(GL_TRIANGLE_FAN, 0, 6)
        //绘制分割线
        glDrawArrays(GL_LINES, 6, 2) //(顶点数组的范围, 从设置的6起始位置开始包含2组坐标)
        //绘制木槌点一
        glDrawArrays(GL_POINTS, 8, 1)//(顶点数组的范围, 从设置的8起始位置开始包含1组坐标)
        //绘制木槌点二
        glDrawArrays(GL_POINTS, 9, 1)//(顶点数组的范围, 从设置的9起始位置开始包含1组坐标)
    }
}