package com.wxk.opengl.l.first

import android.opengl.GLES10.glClear
import android.opengl.GLES20.glViewport
import android.opengl.GLES20.glClearColor
import android.opengl.GLSurfaceView
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class FirstOpenGLProjectRenderer : GLSurfaceView.Renderer {

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        //清空颜色
        glClearColor(1.0f, 0.0f, 0.0f, 0.0f)
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        //设置窗口大小
        glViewport(0, 0, width, height)
    }

    override fun onDrawFrame(gl: GL10?) {
        //清空所有颜色
        glClear(GL10.GL_COLOR_BUFFER_BIT)
    }

}
