package com.example.kanjiapplication.threads;

import com.example.kanjiapplication.imagetransformation.SearchExtremePoints;

import java.util.concurrent.CountDownLatch;

public class ThreadExtremePoint extends  MyThreads{

    private CountDownLatch countDownLatch;
    private SearchExtremePoints searchExtremePoints;
    private String direction;
    private int extremePoint;

    public ThreadExtremePoint(CountDownLatch countDownLatch, String direction, int height, int width, int[] pixels) {
        this.countDownLatch = countDownLatch;
        this.direction = direction;
        searchExtremePoints = new SearchExtremePoints(height, width, pixels);
        extremePoint = -1;
    }

    @Override
    public int getExtremePoint() { return extremePoint; }

    @Override
    public void run() {
        super.run();
        extremePoint = searchExtremePoints.searchExtremePoint(direction);
        try {
            countDownLatch.countDown();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
