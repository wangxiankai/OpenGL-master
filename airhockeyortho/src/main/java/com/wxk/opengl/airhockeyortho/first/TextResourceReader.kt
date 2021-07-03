package com.wxk.opengl.airhockeyortho.first

import android.content.Context
import android.content.res.Resources.NotFoundException
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader

/**
 * Created on 2021/6/28 17 : 27
 * Author      : wangxiankai
 * Email       : wangkxk@foxmail.com
 * Description :
 */
object TextResourceReader {

    fun readTextFileFromRes(context: Context, resourceId: Int): String {
        val sb = StringBuilder()
        try {
            val inputStream = context.resources.openRawResource(resourceId)
            val inputStreamReader = InputStreamReader(inputStream)
            val bufferedReader = BufferedReader(inputStreamReader)
            var nextLine: String? = null
            while (bufferedReader.readLine().also { nextLine = it } != null) {
                sb.append(nextLine).append('\n')
            }
        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: NotFoundException) {
            e.printStackTrace()
        }
        return sb.toString()
    }
}