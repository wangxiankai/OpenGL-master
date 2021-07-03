package com.wxk.opengl.l.first

import android.opengl.GLES20.*
import android.util.Log
import javax.microedition.khronos.opengles.GL10


/**
 * Created on 2021/6/28 18 : 16
 * Author      : wangxiankai
 * Email       : wangkxk@foxmail.com
 * Description :
 */
object ShaderHelper {

    fun compileVertexShader(shaderCode: String): Int = compileShader(GL_VERTEX_SHADER, shaderCode)

    fun compileFragmentShader(shaderCode: String): Int = compileShader(GL_FRAGMENT_SHADER, shaderCode)

    private fun compileShader(type: Int, shaderCode: String): Int {
        //创建一个着色器对象
        val shaderObjectId = glCreateShader(type)
        if (shaderObjectId == 0){
            Log.e("LogUtils", "无法创建一个着色器")
            return 0
        }
        //把着色器源代码上传到着色器对象里
        glShaderSource(shaderObjectId, shaderCode)
        //编译着色器
        glCompileShader(shaderObjectId)
        //取出编译状态
        val compileStatus = IntArray(1)
        glGetShaderiv(shaderObjectId, GL_COMPILE_STATUS, compileStatus, 0)
        //获取着色器编译日志
        Log.e("LogUtils", "着色器编译 ： \n" + shaderCode + "\n : " + glGetShaderInfoLog(shaderObjectId))
        //验证状态并且返回着色器对象id
        if (compileStatus[0] == 0){
            glDeleteShader(shaderObjectId)
            Log.e("LogUtils", "编译着色器失败")
            return 0
        }
        return shaderObjectId
    }

    /**
     * 链接程序对象，附上着色器
     */
    fun linkProgram(vertexShaderId: Int, fragmentShaderId: Int): Int{
        //创建程序对象
        val programObjectId = glCreateProgram()
        if (programObjectId == 0){
            Log.e("LogUtils","创建程序对象失败")
            return 0
        }
        //附上着色器
        glAttachShader(programObjectId, vertexShaderId)
        glAttachShader(programObjectId, fragmentShaderId)
        //把着色器链接起来
        glLinkProgram(programObjectId)
        //取出链接状态，检测链接结果
        val linkStatus = IntArray(1)
        glGetProgramiv(programObjectId, GL_LINK_STATUS, linkStatus, 0)
        //获取着色器链接日志
        Log.e("LogUtils", "着色器链接 : " + glGetProgramInfoLog(programObjectId))
        //验证链接状态并且返回程序对象id
        if (linkStatus[0] == 0){
            glDeleteProgram(programObjectId)
            Log.e("LogUtils", "链接着色器失败")
            return 0
        }
        return programObjectId
    }

    /**
     * 检验程序对象
     */
    fun validateProgram(programObjectId: Int): Boolean{
        glValidateProgram(programObjectId)
        //取出链接状态，检测链接结果
        val validateStatus = IntArray(1)
        glGetProgramiv(programObjectId, GL_VALIDATE_STATUS, validateStatus, 0)
        //获取着色器链接日志
        Log.e("LogUtils", "检验程序对象: ${validateStatus[0]} \nLog： " + glGetProgramInfoLog(programObjectId))
        //验证链接状态并且返回程序对象id
        return validateStatus[0] != 0
    }

}