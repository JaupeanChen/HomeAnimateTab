package com.example.homeanimatetab.ui.explosion;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateInterpolator;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ExplosionView extends View {

    private List<Particle> particles = new ArrayList<>();
    private Paint paint = new Paint();
    private Random random = new Random();
    private ValueAnimator alphaAnimator;

    public ExplosionView(Context context) {
        super(context);
        init();
    }

    public ExplosionView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        paint.setAntiAlias(true);
    }

    // 启动爆炸效果
    public void startExplosion(float startX, float startY) {
        particles.clear();

        for (int i = 0; i < 100; i++) {  // 生成100个粒子
            // 随机方向和速度
            float angle = (float) (random.nextFloat() * 2 * Math.PI);
            float velocity = random.nextFloat() * 5 + 2; // 速度范围 2-7
            float velocityX = (float) (velocity * Math.cos(angle));
            float velocityY = (float) (velocity * Math.sin(angle));

            // 随机颜色
            int color = Color.rgb(random.nextInt(256), random.nextInt(256), random.nextInt(256));

            // 创建一个新的粒子
            Particle particle = new Particle(startX, startY, velocityX, velocityY, color);
            particles.add(particle);
        }

        startAlphaAnimation();

        // 刷新视图，开始绘制动画
        invalidate();
    }

    private void startAlphaAnimation() {
        alphaAnimator = ValueAnimator.ofFloat(1f, 0f);
        alphaAnimator.setDuration(2000);
        alphaAnimator.addUpdateListener(animation -> {
            float alphaValue = (Float) animation.getAnimatedValue();
            for (Particle particle : particles) {
                particle.updateAlpha(alphaValue);
            }
            invalidate();
        });
        alphaAnimator.start();
    }

    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        super.onDraw(canvas);

        // 更新并绘制粒子
        for (int i = particles.size() - 1; i >= 0; i--) {
            Particle particle = particles.get(i);
            if (particle.isAlive()) {
                particle.update();
                // 绘制粒子
                paint.setColor(particle.color);
//                Log.d("onDraw", "alpha: " + (int)particle.alpha);
                paint.setAlpha((int) (particle.alpha * 255)); // 透明度取值范围 [0, 255]
                canvas.drawCircle(particle.x, particle.y, 6, paint);  // 绘制小圆形粒子
            } else {
                particles.remove(i); // 移除生命周期已结束的粒子
            }
        }

        // 如果还有粒子存在，继续重绘
        if (!particles.isEmpty()) {
            invalidate();
        }
    }
}

