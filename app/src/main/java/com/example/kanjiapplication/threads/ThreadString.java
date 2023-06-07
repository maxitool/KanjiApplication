package com.example.kanjiapplication.threads;

import java.util.concurrent.CountDownLatch;

public class ThreadString extends MyThreads{

    private String line;
    private CountDownLatch countDownLatch;
    private int countProcessors;
    private int numberThread;
    private int countRows;
    private float[][] image;
    private int length;

    public ThreadString(CountDownLatch countDownLatch, int countProcessors, int numberThread, int countRows, float[][] image, int length) {
        this.countDownLatch = countDownLatch;
        this.countProcessors = countProcessors;
        this.numberThread = numberThread;
        this.countRows = countRows;
        this.image = image;
        this.length = length;
        line = "";
    }

    @Override
    public String getLine() { return line; }

    @Override
    public void run() {
        super.run();
        for (int i = 0; i < countRows; i++)
            for (int j = 0; j < length; j++)
                line += String.format("%.2f", image[i * countProcessors + numberThread][j]).replace(".","");
        try {
            countDownLatch.countDown();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
