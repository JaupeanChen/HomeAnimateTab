package com.example.homeanimatetab.ui.explosion;

public class Particle {
    // 粒子的位置
    public float x, y;
    // 粒子的速度（单位为像素/秒）
    public float velocityX, velocityY;
    // 粒子的生命周期
    public long startTime;
    // 粒子的颜色
    public int color;
    // 粒子透明度
    public float alpha = 1f;

    public Particle(float x, float y, float velocityX, float velocityY, int color) {
        this.x = x;
        this.y = y;
        this.velocityX = velocityX;
        this.velocityY = velocityY;
        this.startTime = System.currentTimeMillis();
        this.color = color;
    }

    // 更新粒子位置
    public void update() {
        long elapsed = System.currentTimeMillis() - startTime;
        x += velocityX * elapsed / 1000.0f;
        y += velocityY * elapsed / 1000.0f;
    }

    public void updateAlpha(Float alpha) {
        this.alpha = alpha;
    }

    // 判断粒子是否还活着
    public boolean isAlive() {
        long elapsed = System.currentTimeMillis() - startTime;
        return elapsed < 2000; // 粒子活1秒
    }
}

