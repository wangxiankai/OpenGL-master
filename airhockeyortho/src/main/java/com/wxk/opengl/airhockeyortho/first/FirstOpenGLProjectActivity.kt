package com.wxk.opengl.airhockeyortho.first

import android.app.ActivityManager
import android.content.Context
import android.opengl.GLSurfaceView
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity


/**
 * Created on 2021/6/28 17 : 02
 * Author      : wangxiankai
 * Email       : wangkxk@foxmail.com
 * Description :
 */
class FirstOpenGLProjectActivity : AppCompatActivity() {

    private var isRendererSet: Boolean = false
    private lateinit var glSurfaceView: GLSurfaceView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        glSurfaceView = GLSurfaceView(this)

       if (checkOpenGLVersion()){
           //设置版本号
           glSurfaceView.setEGLContextClientVersion(2)

           glSurfaceView.setRenderer(AirHockeyRenderer(this))

           isRendererSet = true
       }

        setContentView(glSurfaceView)
    }

    override fun onResume() {
        super.onResume()
        if (isRendererSet){
            glSurfaceView.onResume()
        }
    }

    override fun onPause() {
        super.onPause()
        if (isRendererSet){
            glSurfaceView.onPause()
        }
    }

    private fun checkOpenGLVersion(): Boolean = (getSystemService(ACTIVITY_SERVICE) as ActivityManager)
            .deviceConfigurationInfo.reqGlEsVersion >= 0x20000

}