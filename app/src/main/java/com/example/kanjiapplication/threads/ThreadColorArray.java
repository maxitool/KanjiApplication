package com.example.kanjiapplication.threads;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;

public class ThreadColorArray extends MyThreads {

    private final int MIN_COLOR = 1, MAX_COLOR = 16777215;
    private CountDownLatch countDownLatch;
    private int[] pixels;
    private float[][] image;
    private int[] extremePoints; //0 - left, 1 - right, 2 - top, 3 - down
    private int realWidth, width;
    private int countProcessors;
    private int numberThread;
    private int countRows;

    public ThreadColorArray(CountDownLatch countDownLatch, int countProcessors, int numberThread, int countRows, int realWidth, int width,
                            int[] extremePoints, int[] pixels, float[][] image) {
        this.countDownLatch = countDownLatch;
        this.countProcessors = countProcessors;
        this.numberThread = numberThread;
        this.countRows = countRows;
        this.realWidth = realWidth;
        this.width = width;
        this.extremePoints = extremePoints;
        this.pixels = pixels;
        this.image = image;
    }

    @Override
    public void run() {
        super.run();
        for (int i = 0; i < countRows; i++)
            for (int j = 0; j < realWidth; j++)
                image[i * countProcessors + numberThread][j] =
                        (float)(-pixels[(extremePoints[2] + i * countProcessors + numberThread) * width + extremePoints[0] + j] - MIN_COLOR) / (float)MAX_COLOR;
        try {
            countDownLatch.countDown();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
