package com.example.homeanimatetab.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.NonNull;

/**
 * sample
 */
public class ArcShapeView extends View {

    private Paint paint;
    private Path path;
    private RectF rectF;
    // 计算扇形的起始点和路径
    float centerX = 400; // 圆心X坐标
    float centerY = 400; // 圆心Y坐标
    float radius = 300; // 半径
    float startAngle = 0; // 起始角度，以度为单位
    float sweepAngle = 270; // 扫描的角度

    public ArcShapeView(Context context) {
        super(context);
        init();
    }

    public ArcShapeView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        paint = new Paint();
        paint.setColor(Color.BLUE); // 设置扇形颜色
        paint.setStyle(Paint.Style.FILL); // 填充扇形
        path = new Path();
        rectF = new RectF(centerX - radius, centerY - radius,
                centerX + radius, centerY + radius);
    }

    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        super.onDraw(canvas);

        // moveTo 移动到弧线起点
        path.moveTo(centerX, centerY);

        // arcTo 绘制扇形弧线
//        RectF oval = rectF;
        path.arcTo(rectF, startAngle, sweepAngle);

        // 关闭路径
        path.close();

        // 删除背景的白色
        canvas.drawColor(Color.WHITE);

        // 绘制扇形
        canvas.drawPath(path, paint);
    }
}
