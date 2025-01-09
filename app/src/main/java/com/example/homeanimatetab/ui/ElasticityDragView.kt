package com.example.homeanimatetab.ui

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PointF
import android.graphics.RectF
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import kotlin.math.abs
import kotlin.math.pow
import kotlin.math.sqrt

class ElasticityDragView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    //范围圆圆心
    private val scopeCircleCenter: PointF

    //范围圆半径
    private val scopeCircleR = 350f

    //小圆圆心
    private val smallCircleCenter: PointF

    //小圆半径
    private val smallCircleR = 60f

    //大圆圆心
    private val bigCircleCenter: PointF

    //大圆半径
    private val bigCircleR = 100f

    private val paint = Paint().apply {
        color = Color.LTGRAY
        isAntiAlias = true
    }

    private var isDragging = false


    init {
        val screenWidth = resources.displayMetrics.widthPixels
        val screenHeight = resources.displayMetrics.heightPixels
        scopeCircleCenter = PointF(screenWidth / 2f, screenHeight / 2f - 150)
        smallCircleCenter = PointF(screenWidth / 2f, screenHeight / 2f - 150)
        bigCircleCenter = PointF(screenWidth / 2f + 350, screenHeight / 2f - 150)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        val action = event?.action
        when (action) {
            MotionEvent.ACTION_DOWN -> {
                Log.d("ElasticityDragView", "ACTION_DOWN")
                //判断是否在大圆内
                isDragging = isTouchBigCircle(event)
                if (isDragging) {
                    Log.d("ElasticityDragView", "ACTION_DOWN on target")
                }
            }

            MotionEvent.ACTION_MOVE -> {
                if (isDragging && inScopeCircle(event)) {
                    Log.d("ElasticityDragView", "isDragging ACTION_MOVE")
                    bigCircleCenter.x = event.x
                    bigCircleCenter.y = event.y
                    invalidate()
                }
            }

            MotionEvent.ACTION_UP -> {
                Log.d("ElasticityDragView", "ACTION_UP")
                isDragging = false
            }
        }
        //TODO 这里需要返回true消费事件，才能拖动大圆
        return true
    }

    private fun isTouchBigCircle(event: MotionEvent?): Boolean {
        if (event == null) return false
        return (event.x >= bigCircleCenter.x - bigCircleR) && (event.x <= bigCircleCenter.x + bigCircleR)
                && (event.y >= bigCircleCenter.y - bigCircleR) && (event.y <= bigCircleCenter.y + bigCircleR)
    }

    private fun inScopeCircle(event: MotionEvent?): Boolean {
        if (event == null) return false
        return sqrt(((abs(event.x - scopeCircleCenter.x)).pow(2) + abs(event.y - scopeCircleCenter.y).pow(2)).toDouble()) <= scopeCircleR
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        paint.color = Color.LTGRAY
        canvas.drawCircle(scopeCircleCenter.x, scopeCircleCenter.y, scopeCircleR, paint)
        paint.color = Color.BLUE
        canvas.drawCircle(smallCircleCenter.x, smallCircleCenter.y, smallCircleR, paint)
        canvas.drawCircle(bigCircleCenter.x, bigCircleCenter.y, bigCircleR, paint)
    }
}