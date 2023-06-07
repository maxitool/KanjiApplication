package com.example.kanjiapplication.threads;

import android.graphics.Canvas;
import android.graphics.Paint;

public class ThreadDraw extends MyThreads {

    private Canvas canvas;
    private Paint paint;
    private int widthBrush;
    private float[] points;
    private float[] steps;
    private float[] pointSteps;
    private int countProcessors;
    private int countDraw;
    private int numberThread;

    public ThreadDraw(Canvas canvas, Paint paint, int widthBrush, int countProcessors, int numberThread) {
        this.canvas = canvas;
        this.paint = paint;
        this.widthBrush = widthBrush;
        this.countProcessors = countProcessors;
        this.numberThread = numberThread;
        pointSteps = new float[2];
    }

    @Override
    public void setSettings(int countDraw, float[] points, float[] steps) {
        this.countDraw = countDraw;
        this.points = points;
        this.steps = steps;
    }

    @Override
    public void run() {
        super.run();
        for (int i = 0; i < countDraw; i++) {
            if (points[0] == points[2])
                pointSteps[0] = points[0];
            else {
                if (points[0] > points[2])
                    pointSteps[0] = points[0] - steps[0] * (i * countProcessors + numberThread);
                else
                    pointSteps[0] = points[0] + steps[0] * (i * countProcessors + numberThread);
            }
            if (points[1] == points[3])
                pointSteps[1] = points[1];
            else {
                if (points[1] > points[3])
                    pointSteps[1] = points[1] - steps[1] * (i * countProcessors + numberThread);
                else
                    pointSteps[1] = points[1] + steps[1] * (i * countProcessors + numberThread);
            }
            canvas.drawCircle(pointSteps[0], pointSteps[1], widthBrush, paint);
        }
    }
}
