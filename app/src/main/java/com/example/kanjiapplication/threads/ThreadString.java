package com.example.kanjiapplication.threads;

import java.util.concurrent.CountDownLatch;

public class ThreadString extends MyThreads{

    private String line;
    private CountDownLatch countDownLatch;
    private int numberThread;
    private int countRows;
    private float[][] image;
    private int length;
    private int remainder;

    public ThreadString(CountDownLatch countDownLatch, int numberThread, int countRows, float[][] image, int length, int remainder) {
        this.countDownLatch = countDownLatch;
        this.numberThread = numberThread;
        this.countRows = countRows;
        this.image = image;
        this.length = length;
        this.remainder = remainder;
        line = "";
    }

    @Override
    public String getLine() { return line; }

    @Override
    public void run() {
        super.run();
        for (int i = 0; i < countRows + remainder; i++)
            for (int j = 0; j < length; j++)
                line += String.format("%.2f", image[countRows * numberThread + i][j]).replace(".","");
        try {
            countDownLatch.countDown();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
