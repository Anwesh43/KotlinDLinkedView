package com.example.dlinkedview

/**
 * Created by anweshmishra on 27/05/18.
 */

import android.content.Context
import android.view.*
import android.graphics.*

val D_NODES = 5

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

    data class State (var prevScale : Float = 0f, var dir : Float = 0f, var j : Int = 0) {

        val scales : Array<Float> = arrayOf(0f, 0f, 0f)

        fun update(stopcb : (Float) -> Unit) {
            scales[j] += dir * 0.1f
            if (Math.abs(scales[j] - prevScale) > 1) {
                scales[j] = prevScale + dir
                if (Math.abs(scales[j] - prevScale) > 1) {
                    j -= dir.toInt()
                    dir = 0f
                    prevScale = scales[j]
                    stopcb(prevScale)
                }
            }
        }

        fun startUpdating(startcb : () -> Unit) {
            if (dir == 0f) {
                dir = 1 - 2 * prevScale
                startcb()
            }
        }
    }

    data class Animator(var view : DLinkedView, var animated : Boolean = false) {

        fun animate(updatecb : () -> Unit) {
            if (animated) {
                updatecb()
                try {
                    Thread.sleep(50)
                    view.invalidate()
                } catch(ex : Exception) {

                }
            }
        }

        fun start() {
            if (!animated) {
                animated = true
                view.postInvalidate()
            }
        }

        fun stop() {
            if (animated) {
                animated = false
            }
        }
    }

    data class DNode(var i : Int = 0, val state : State = State()) {

        var prev : DNode? = null

        var next : DNode? = null

        init {
            this.addNeighbor()
        }

        fun addNeighbor() {
            if (i < D_NODES - 1) {
                next = DNode(i +1)
                next?.prev = this
            }
        }

        fun draw(canvas : Canvas, paint : Paint) {
            val w : Float = canvas.width.toFloat()
            val h : Float = canvas.height.toFloat()
            val gap : Float = h / D_NODES
            paint.strokeWidth = Math.min(w, h)/45
            paint.strokeCap = Paint.Cap.ROUND
            paint.color = Color.parseColor("#4CAF50")
            canvas.save()
            canvas.translate(w/2, gap * i)
            canvas.save()
            canvas.translate(0f, gap * state.scales[2])
            canvas.drawLine(0f, 0f, 0f, gap, paint)
            canvas.restore()
            canvas.drawArc(RectF(-gap/2, 0f, gap/2, gap), -90f + 180f * state.scales[1], 180f * state.scales[0] * (1 - state.scales[1]), false, paint)
            canvas.restore()
        }

        fun update(stopcb : (Float) -> Unit) {
            state.update(stopcb)
        }

        fun startUpdating(startcb : () -> Unit) {
            state.startUpdating(startcb)
        }

        fun getNext(dir : Int, cb : () -> Unit) : DNode {
            var curr : DNode? = this.prev
            if (dir == 1) {
                curr = next
            }
            if (curr != null) {
                return curr
            }
            cb()
            return this
        }
    }
}