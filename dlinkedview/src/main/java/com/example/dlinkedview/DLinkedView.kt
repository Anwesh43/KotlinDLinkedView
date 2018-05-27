package com.example.dlinkedview

/**
 * Created by anweshmishra on 27/05/18.
 */

import android.content.Context
import android.view.*
import android.graphics.*

class DLinkedView(ctx : Context) : View(ctx) {

    private val paint : Paint = Paint(Paint.ANTI_ALIAS_FLAG)

    override fun onDraw(canvas : Canvas) {

    }

    override fun onTouchEvent(event : MotionEvent) : Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {

            }
        }
        return true
    }

}