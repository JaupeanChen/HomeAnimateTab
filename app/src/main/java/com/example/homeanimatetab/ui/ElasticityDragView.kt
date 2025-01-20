package com.example.homeanimatetab.ui

import android.animation.ObjectAnimator
import android.animation.PointFEvaluator
import android.animation.ValueAnimator
import android.animation.ValueAnimator.AnimatorUpdateListener
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
import com.example.homeanimatetab.ui.explosion.Particle
import kotlin.math.abs
import kotlin.math.atan
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt
import kotlin.random.Random

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
    private val smallCircleR = 40f
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

    private var isExplode = false

    //大小圆圆心点距离
    private var distans = 0f

    private val path = Path()

    private var particleList: ArrayList<Particle> = ArrayList()

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
                } else {
                    //爆炸
                    prepareParticles(event.x, event.y)
                    invalidate()
                }
            }
        }
        //TODO 这里需要返回true消费事件，才能拖动大圆
        return true
    }

    private fun prepareParticles(startX: Float, startY: Float) {
        particleList.clear()
        Log.d("Explosion", Random.nextFloat().toString())
        //构造100个随机粒子
        for (i in 1..100) {
            if (i < 10) {
                Log.d("Random", Random.nextFloat().toString())
            }
            val angel = Random.nextFloat() * 2 * Math.PI
            val velocity = Random.nextFloat() * 5 + 1
            val velocityX = velocity * cos(angel)
            val velocityY = velocity * sin(angel)
            val color = Color.rgb(Random.nextInt(256), Random.nextInt(256), Random.nextInt(256))
            val particle = Particle(startX, startY, velocityX.toFloat(), velocityY.toFloat(), color)
            particleList.add(particle)
        }
        isExplode = true

        //粒子透明度动画
        val valueAnimator = ValueAnimator.ofFloat(1f, 0f)
        valueAnimator.setDuration(2000)
        valueAnimator.addUpdateListener {
            val value = it.getAnimatedValue() as Float
            for (particle in particleList) {
                particle.updateAlpha(value)
            }
        }
        valueAnimator.start()
    }

    //通过属性动画来更新bigCircleCenter属性（同时invalidate()），移动到点scopeCircleCenter位置
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

        if (inScope) {
            canvas.drawCircle(smallCircleCenter.x, smallCircleCenter.y, changeSmallCircleR, paint)
            canvas.drawCircle(bigCircleCenter.x, bigCircleCenter.y, bigCircleR, paint)
            drawBezier(canvas, changeSmallCircleR, bigCircleR)
        }

        //绘制爆炸粒子
        if (isExplode) {
            //TODO 通过从最后一个元素开始遍历，可以避免由于缩小列表大小而导致的索引错误
            // 或使用 Iterator 可以安全地在遍历过程中删除元素
            // val myList = arrayListOf("A", "B", "C", "D")
            // val iterator = myList.iterator()
            // while (iterator.hasNext()) {
            //    val item = iterator.next()
            //    if (item == "B") {
            //        iterator.remove() // 使用 Iterator 的 remove 方法
            //    }
            // }
            for (i in particleList.size - 1 downTo 0) {
                val particle = particleList[i]
                if (particle.isAlive) {
                    particle.update()
                    paint.color = particle.color
                    paint.alpha = (particle.alpha * 255).toInt()
                    canvas.drawCircle(particle.x, particle.y, 6f, paint)
                } else {
                    particleList.removeAt(i)
                }
            }

            if (particleList.isNotEmpty()) {
                invalidate()
            }
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