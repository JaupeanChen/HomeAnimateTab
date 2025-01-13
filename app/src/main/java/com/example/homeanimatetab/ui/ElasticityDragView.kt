package com.example.homeanimatetab.ui

import android.animation.ObjectAnimator
import android.animation.PointFEvaluator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PointF
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.animation.OvershootInterpolator
import androidx.core.graphics.minus
import kotlin.math.abs
import kotlin.math.atan
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

/**
 * 仿qq可拖拽消息气泡
 * 参考：https://mp.weixin.qq.com/s/UIyF8-83315RfXFW4PAWYQ
 */
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
    private var changeSmallCircleR = 60f

    //大圆圆心
    //TODO 由于用到回弹动画ObjectAnimator，所以不能为private
    var bigCircleCenter = PointF(0f, 0f)
        set(value) {
            field = value
            invalidate()
        }

    //大圆半径
    private val bigCircleR = 40f

    private val paint = Paint().apply {
        color = Color.LTGRAY
        isAntiAlias = true
    }

    private var isDragging = false

    private var inScope = true

    //大小圆圆心点距离
    private var distans = 0f

    private val path = Path()

    init {
        val screenWidth = resources.displayMetrics.widthPixels
        val screenHeight = resources.displayMetrics.heightPixels
        scopeCircleCenter = PointF(screenWidth / 2f, screenHeight / 2f - 150)
        smallCircleCenter = PointF(screenWidth / 2f, screenHeight / 2f - 150)
//        bigCircleCenter = PointF(screenWidth / 2f, screenHeight / 2f - 150)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        val screenWidth = resources.displayMetrics.widthPixels
        val screenHeight = resources.displayMetrics.heightPixels
        bigCircleCenter.x = screenWidth / 2f
        bigCircleCenter.y = screenHeight / 2f - 150
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
                inScope = inScopeCircle(event)
                if (isDragging) {
                    Log.d("ElasticityDragView", "isDragging ACTION_MOVE")
                    bigCircleCenter.x = event.x
                    bigCircleCenter.y = event.y
                    invalidate()
                }
            }

            MotionEvent.ACTION_UP -> {
                Log.d("ElasticityDragView", "ACTION_UP")
                isDragging = false
                if (inScope) {
                    //回弹
                    animatorBack()
                }
            }
        }
        //TODO 这里需要返回true消费事件，才能拖动大圆
        return true
    }

    private fun animatorBack() {
        ObjectAnimator.ofObject(
            this,
            "bigCircleCenter",
            PointFEvaluator(),
            scopeCircleCenter
        ).apply {
            duration = 400
            interpolator = OvershootInterpolator(3f)
        }.start()
    }

    private fun isTouchBigCircle(event: MotionEvent?): Boolean {
        if (event == null) return false
        return (event.x >= bigCircleCenter.x - bigCircleR) && (event.x <= bigCircleCenter.x + bigCircleR)
                && (event.y >= bigCircleCenter.y - bigCircleR) && (event.y <= bigCircleCenter.y + bigCircleR)
    }

    private fun inScopeCircle(event: MotionEvent?): Boolean {
        if (event == null) return false
        distans = sqrt(
            ((abs(event.x - scopeCircleCenter.x)).pow(2) + abs(event.y - scopeCircleCenter.y).pow(
                2
            )).toDouble()
        ).toFloat()
        var ratio = distans / scopeCircleR
        Log.d("ratio", "ratio = $ratio")
        if (ratio > 0.618) {
            ratio = 0.618f
        }
        changeSmallCircleR = smallCircleR * (1 - ratio)
        return distans <= scopeCircleR
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        paint.color = Color.LTGRAY
        canvas.drawCircle(scopeCircleCenter.x, scopeCircleCenter.y, scopeCircleR, paint)
        paint.color = Color.BLUE
        canvas.drawCircle(
            smallCircleCenter.x,
            smallCircleCenter.y,
            changeSmallCircleR,
            paint
        )
        if (inScope) {
            canvas.drawCircle(bigCircleCenter.x, bigCircleCenter.y, bigCircleR, paint)
            drawBezier(canvas, changeSmallCircleR, bigCircleR)
        }
    }

    private fun drawBezier(canvas: Canvas, smallRadius: Float, bigRadius: Float) {
        val current = bigCircleCenter - smallCircleCenter
        val tempY = current.y.toDouble()
        val tempX = current.x.toDouble()
        val BDF = atan(tempY / tempX)
        val p1X = smallCircleCenter.x + smallRadius * sin(BDF)
        val p1Y = smallCircleCenter.y - smallRadius * cos(BDF)
        val p2X = bigCircleCenter.x + bigRadius * sin(BDF)
        val p2Y = bigCircleCenter.y - bigRadius * cos(BDF)
        val p3X = smallCircleCenter.x - smallRadius * sin(BDF)
        val p3Y = smallCircleCenter.y + smallRadius * cos(BDF)
        val p4X = bigCircleCenter.x - bigRadius * sin(BDF)
        val p4Y = bigCircleCenter.y + bigRadius * cos(BDF)
        // 控制点
        val controlPointX =
            current.x / 2 + smallCircleCenter.x
        val controlPointY = current.y / 2 + smallCircleCenter.y
        //创建路径
//        val path = Path()
        //移动到p1位置
        path.moveTo(p1X.toFloat(), p1Y.toFloat())
        // 绘制贝塞尔曲线，连接到p2
        path.quadTo(controlPointX, controlPointY, p2X.toFloat(), p2Y.toFloat())
        // 连接到p4
        path.lineTo(p4X.toFloat(), p4Y.toFloat())
        // 绘制贝塞尔曲线，连接到p3
        path.quadTo(controlPointX, controlPointY, p3X.toFloat(), p3Y.toFloat())
        path.close()
        canvas.drawPath(path, paint)
        path.reset()
    }

}