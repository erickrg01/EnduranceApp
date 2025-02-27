package com.example.enduranceacademyapp.fragments;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

public class CircularProgressView extends View {
    private Paint backgroundPaint;
    private Paint progressPaint;
    private Paint textPaint;
    private float progress = 0;
    private String label = "";
    private int color;



    public CircularProgressView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public void setTypeface(Typeface typeface) {
        if (textPaint != null) {
            textPaint.setTypeface(typeface); // Set the custom typeface
            invalidate(); // Redraw the view to apply the changes
        }
    }

    private void init() {
        backgroundPaint = new Paint();
        backgroundPaint.setColor(0xFFE0E0E0); // Color de fondo (gris)
        backgroundPaint.setStyle(Paint.Style.STROKE);
        backgroundPaint.setStrokeWidth(15);
        backgroundPaint.setAntiAlias(true);

        progressPaint = new Paint();
        progressPaint.setStyle(Paint.Style.STROKE);
        progressPaint.setStrokeWidth(15);
        progressPaint.setAntiAlias(true);

        textPaint = new Paint();
        textPaint.setTextSize(40);
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setColor(0xFF000000); // Negro
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int width = getWidth();
        int height = getHeight();
        int size = Math.min(width, height);
        float radius = size / 2f - 20;

        // Dibujar fondo circular
        canvas.drawCircle(width / 2f, height / 2f, radius, backgroundPaint);

        // Dibujar progreso en arco
        RectF rect = new RectF(width / 2f - radius, height / 2f - radius, width / 2f + radius, height / 2f + radius);
        canvas.drawArc(rect, -90, progress * 360, false, progressPaint);

        // Dibujar texto (valor y etiqueta)
        canvas.drawText(label, width / 2f, height / 2f + 15, textPaint);
    }

    public void setProgress(float progress, String label, int color) {
        this.progress = progress;
        this.label = label;
        this.color = color;
        progressPaint.setColor(color);
        invalidate();
    }
}
