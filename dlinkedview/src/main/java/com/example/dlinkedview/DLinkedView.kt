package com.example.dlinkedview

/**
 * Created by anweshmishra on 27/05/18.
 */

import android.app.Activity
import android.content.Context
import android.view.*
import android.graphics.*

val D_NODES = 5

class DLinkedView(ctx : Context) : View(ctx) {

    private val paint : Paint = Paint(Paint.ANTI_ALIAS_FLAG)

    private val renderer : Renderer = Renderer(this)

    override fun onDraw(canvas : Canvas) {
        renderer.render(canvas, paint)
    }

    override fun onTouchEvent(event : MotionEvent) : Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                renderer.handleTap()
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

    data class DLinked(var i : Int) {

        var curr : DNode = DNode(0)

        var dir : Int = 1

        fun draw(canvas : Canvas, paint : Paint) {
            curr.draw(canvas, paint)
        }

        fun update(stopcb : (Float) -> Unit) {
            curr.update {scale ->
                curr = curr.getNext(dir) {
                    dir *= -1
                }
                stopcb(scale)
            }
        }

        fun startUpdating(startcb : () -> Unit) {
            curr.startUpdating(startcb)
        }
    }

    data class Renderer (var view : DLinkedView) {

        private val animator : Animator = Animator(view)

        private val dLinked : DLinked = DLinked(0)

        fun render(canvas : Canvas, paint : Paint) {
            canvas.drawColor(Color.parseColor("#e53935"))
            dLinked.draw(canvas, paint)
            animator.animate {
                dLinked.update {
                    animator.stop()
                }
            }
        }

        fun handleTap() {
            dLinked.startUpdating {
                animator.start()
            }
        }
    }

    companion object {
        fun create(activity : Activity) : DLinkedView  {
            val view : DLinkedView = DLinkedView(activity)
            activity.setContentView(view)
            return view
        }
    }
}