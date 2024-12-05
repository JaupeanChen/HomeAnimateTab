package com.example.homeanimatetab.ui

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlin.math.sqrt

class CustomBottomNavigationView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : BottomNavigationView(context, attrs, defStyleAttr) {

    //中间大圆到x轴距离
    private var distance = 60f

    //两边小圆半径
    private val radiusCorner = 30

    //中间大圆半径
    private val radiusCentral: Float
        get() = radiusCorner + 2 * distance.toFloat()

    private val circleCenter: Pair<Float, Float>
        get() = (width.toFloat() / 2) to -distance.toFloat()

    private var path = Path()

    private val paint by lazy {
        Paint().apply {
            isAntiAlias = true
            color = Color.parseColor("#1296db")
            strokeWidth = 5F
            style = Paint.Style.STROKE
        }
    }

    fun updateDistance(distance: Float) {
        this.distance = 50 - distance
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val leftCenter = (circleCenter.first - sqrt(3f) * (radiusCorner + distance)) to radiusCorner
        val rightCenter =
            (circleCenter.first + sqrt(3f) * (radiusCorner + distance)) to radiusCorner
        path.apply {
            //将当前绘图的起点移动到指定的 (x, y) 坐标
            moveTo(0f, 0f)
            if (distance >= -10f) {
                //从当前点绘制一条直线到指定的 (x, y) 坐标
                lineTo(leftCenter.first, 0f)
                //绘制一个弧线，并将路径的当前点移动到弧线的终点。该方法使用指定的椭圆矩形来定义圆弧的范围，
                //并通过 startAngle 和 sweepAngle 确定弧的起始角度和扫过的角度.
                arcTo(
                    leftCenter.first - radiusCorner,
                    0f,
                    leftCenter.first + radiusCorner,
                    2 * radiusCorner.toFloat(),
                    -90f,
                    60f,
                    true
                )
                arcTo(
                    circleCenter.first - radiusCentral,
                    circleCenter.second - radiusCentral,
                    circleCenter.first + radiusCentral,
                    circleCenter.second + radiusCentral,
                    150f,
                    -120f,
                    true
                )
                arcTo(
                    rightCenter.first - radiusCorner,
                    0f,
                    rightCenter.first + radiusCorner,
                    2 * radiusCorner.toFloat(),
                    -150f,
                    60f,
                    true
                )
                lineTo(width.toFloat(), 0f)
            } else {
                lineTo(width.toFloat(), 0f)
            }
        }
        canvas.apply {
            save()
            drawPath(path, paint)
            restore()
        }
    }
}