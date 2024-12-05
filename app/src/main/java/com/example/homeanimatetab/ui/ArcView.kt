package com.example.homeanimatetab.ui

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View

class ArcView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val paint by lazy {
        Paint().apply {
            isAntiAlias = true
            color = Color.parseColor("#1296db")
            strokeWidth = 5F
            style = Paint.Style.STROKE
        }
    }

    private val centerX = 100f
    private val centerY = 100f
    private val radius = 300f

    private val path = Path()
    private val rectF =
        RectF(centerX - radius, centerY - radius, centerX + radius, centerX + radius)

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        //移到圆心点
        path.moveTo(centerX, centerY)
        path.lineTo(centerX, centerX + radius)
        //扫描的角度以X轴正向顺时针开始算
        path.arcTo(rectF, 0f, 90f, true)
        //这里需要先lineTo，再close才能把扇形面封闭起来
        path.lineTo(centerX, centerY)
        path.close()
        canvas.apply {
            save()
            drawPath(path, paint)
            restore()
        }
    }
}