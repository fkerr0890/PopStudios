package com.example.popstudios;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;

import java.util.Random;

public class Bubble extends View {
    private float radius;
    private Paint paint;
    private int color;

    public Bubble(Context context, float radius, int color) {
        super(context);
        this.radius = radius;
        this.color = color;
        setupPaint();
    }

    private void setupPaint() {
        paint = new Paint();
        paint.setColor(color);
        paint.setAntiAlias(true);
        paint.setStrokeWidth(5);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStyle(Paint.Style.FILL);
    }

    public static int getRandomColor() {
        Random rnd = new Random();
        return Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
    }

    public double getRadius() {
        return radius;
    }

    public void setRadius(float radius) {
        this.radius = radius;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawCircle(radius,radius,radius,paint);
        super.onDraw(canvas);
    }
}
