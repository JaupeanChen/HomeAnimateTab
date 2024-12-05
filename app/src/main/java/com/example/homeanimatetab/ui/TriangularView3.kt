package com.example.homeanimatetab.ui

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.View

class TriangularView3 @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : View(context, attrs, defStyle) {

    private val paint: Paint = Paint().apply {
        isAntiAlias = true
        color = android.graphics.Color.BLACK // 背景色：黑色
        style = Paint.Style.FILL
    }

    private val linePaint: Paint = Paint().apply {
        isAntiAlias = true
        color = android.graphics.Color.WHITE // 斜线颜色：白色
        strokeWidth = 10f // 线宽
        style = Paint.Style.STROKE
    }

    private val trianglePath: Path = Path()

    init {
        setLayerType(LAYER_TYPE_SOFTWARE, null) // 允许软件绘制
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        drawTriangle(canvas)
        drawDiagonalLines(canvas)
    }

    private fun drawTriangle(canvas: Canvas) {
        // 设置三角形的顶点
        trianglePath.reset()
        trianglePath.moveTo(width.toFloat(), 0f) // 顶点
        trianglePath.lineTo(0f, height.toFloat() / 2) // 左下角
        trianglePath.lineTo(width.toFloat(), height.toFloat()) // 右下角
        trianglePath.close() // 关闭路径

        // 填充三角形的背景
        canvas.drawPath(trianglePath, paint)
    }

    private fun drawDiagonalLines(canvas: Canvas) {
        val lineSpacing = 20 // 线间距

        for (i in 0..height step lineSpacing) {
            canvas.drawLine(
                0f, height - i.toFloat(),
                width.toFloat(), height - i.toFloat(), linePaint
            )
        }
    }
}