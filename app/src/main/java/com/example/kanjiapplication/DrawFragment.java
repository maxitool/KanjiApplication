package com.example.kanjiapplication;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.example.kanjiapplication.imagetransformation.SearchExtremePoints;
import com.example.kanjiapplication.threads.MyThreads;
import com.example.kanjiapplication.threads.ThreadColorArray;
import com.example.kanjiapplication.threads.ThreadDraw;
import com.example.kanjiapplication.threads.ThreadExtremePoint;
import com.example.kanjiapplication.threads.ThreadString;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

public class DrawFragment extends Fragment {

    private final int COUNT_PROCESSORS = Runtime.getRuntime().availableProcessors();
    private final int MIN_COLOR = 1, MAX_COLOR = 16777215;
    private Button buttonClear;
    private ImageButton imageButtonEraser, imageButtonPencil;
    private ImageView imageViewDraw;
    private boolean isDrawable;
    private Paint paint;
    private Bitmap bitmap;
    private Canvas canvas;
    private List<MyThreads> threadsList;
    private CountDownLatch countDownLatch;
    private int height, width;
    private float[] points;
    private int[] locationViewDraw;
    private int[] globalLocation;
    private int widthBrush;
    private int countSteps;
    private float[] steps;
    private float[] pointSteps;
    private int commonCountPartitionStreams;
    private int remainder;

    static {
        System.loadLibrary("Normalization");
    }

    public native float[][] normalization(float[][] image);

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        paint = new Paint();
        paint.setColor(Color.WHITE);
        paint.setAntiAlias(true);
        points = new float[4];
        locationViewDraw = new int[2];
        steps = new float[2];
        pointSteps = new float[2];
        paint.setColor(Color.BLACK);
        View view = inflater.inflate(R.layout.fragment_draw, container, false);
        view.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View view, MotionEvent event) {
                return eventListener(view, event);
            }
        });
        isDrawable = true;
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        imageViewDraw = view.findViewById(R.id.drawView);
        buttonClear = view.findViewById(R.id.buttonClear);
        imageButtonEraser = view.findViewById(R.id.imageButtonEraser);
        imageButtonPencil = view.findViewById(R.id.imageButtonPencil);
        imageButtonPencil.setEnabled(false);
        buttonClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bitmap.eraseColor(Color.WHITE);
                imageViewDraw.setImageBitmap(bitmap);
            }
        });
        imageButtonEraser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                paint.setColor(Color.WHITE);
                widthBrush *= 2;
                imageButtonEraser.setEnabled(false);
                imageButtonPencil.setEnabled(true);
            }
        });
        imageButtonPencil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                paint.setColor(Color.BLACK);
                widthBrush /= 2;
                imageButtonEraser.setEnabled(true);
                imageButtonPencil.setEnabled(false);
            }
        });
    }

    public void setSettings(int[] globalLocation) {
        this.globalLocation = globalLocation;
        imageViewDraw.post(new Runnable() {
            @Override
            public void run() {
                height = imageViewDraw.getHeight();
                width = imageViewDraw.getWidth();
                bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
                bitmap.eraseColor(Color.WHITE);
                canvas = new Canvas(bitmap);
                imageViewDraw.getLocationOnScreen(locationViewDraw);
                if (height < width)
                    widthBrush = height / 32;
                else
                    widthBrush = width / 32;
                threadsList = new ArrayList<>();
                if (COUNT_PROCESSORS > 1)
                    for (int i = 0; i < COUNT_PROCESSORS; i++)
                        threadsList.add(new ThreadDraw(canvas, paint, widthBrush, COUNT_PROCESSORS, i));
                imageViewDraw.setImageBitmap(bitmap);
            }
        });
    }

    public String getImage() throws InterruptedException, IOException {
        //Stop draw panel and draw threads
        isDrawable = false;
        if (COUNT_PROCESSORS > 1)
            for (int i = threadsList.size() - 1; i >=0; i--) {
                threadsList.get(i).interrupt();
                threadsList.remove(i);
            }
        //Get pixels
        int[] pixels = new int[height * width];
        bitmap.getPixels(pixels, 0, width, 0, 0, width, height);
        //Search extreme points
        int[] extremePoints = new int[4];
        if (COUNT_PROCESSORS >= 4) {
            countDownLatch = new CountDownLatch(4);
            threadsList.add(new ThreadExtremePoint(countDownLatch, "left", height, width, pixels));
            threadsList.add(new ThreadExtremePoint(countDownLatch, "right", height, width, pixels));
            threadsList.add(new ThreadExtremePoint(countDownLatch, "top", height, width, pixels));
            threadsList.add(new ThreadExtremePoint(countDownLatch, "down", height, width, pixels));
            for (int i = 0; i < threadsList.size(); i++)
                threadsList.get(i).start();
            countDownLatch.await();
            for (int i = 0; i < threadsList.size(); i++)
                extremePoints[i] = threadsList.get(i).getExtremePoint();
            for (int i = threadsList.size() - 1; i >= 0; i--) {
                threadsList.get(i).interrupt();
                threadsList.remove(i);
            }
        }
        else {
            SearchExtremePoints searchExtremePoints = new SearchExtremePoints(height, width, pixels);
            extremePoints[0] = searchExtremePoints.searchExtremePoint("left");
            extremePoints[1] = searchExtremePoints.searchExtremePoint("right");
            extremePoints[2] = searchExtremePoints.searchExtremePoint("top");
            extremePoints[3] = searchExtremePoints.searchExtremePoint("down");
        }
        if (extremePoints[0] == -1) //if draw panel empty, then return empty line
            return "";
        //Get Colors array from pixels with extreme points
        int realWidth = extremePoints[1] - extremePoints[0];
        int realHeight = extremePoints[3] - extremePoints[2];
        float[][] image = new float[realHeight][realWidth];
        if (COUNT_PROCESSORS > 1 && realHeight >= COUNT_PROCESSORS) {
            commonCountPartitionStreams = realHeight / COUNT_PROCESSORS;
            remainder = realHeight - commonCountPartitionStreams * COUNT_PROCESSORS;
            countDownLatch = new CountDownLatch(COUNT_PROCESSORS);
            for (int i = 0; i < remainder; i++) {
                threadsList.add(new ThreadColorArray(countDownLatch, COUNT_PROCESSORS, i, commonCountPartitionStreams + 1, realWidth, width, extremePoints, pixels, image));
                threadsList.get(i).start();
            }
            for (int i = remainder; i < COUNT_PROCESSORS; i++) {
                threadsList.add(new ThreadColorArray(countDownLatch, COUNT_PROCESSORS, i, commonCountPartitionStreams, realWidth, width, extremePoints, pixels, image));
                threadsList.get(i).start();
            }
            countDownLatch.await();
            for (int i = threadsList.size() - 1; i >= 0; i--) {
                threadsList.get(i).interrupt();
                threadsList.remove(i);
            }
        }
        else
        {
            for (int i = 0; i < realHeight; i++)
                for (int j = 0; j < realWidth; j++)
                    image[i][j] = (float)(-pixels[(extremePoints[2] + i) * width + extremePoints[0] + j] - MIN_COLOR) / (float)MAX_COLOR;
        }
        //Normalization with native method
        float[][] normalizedImage = normalization(image);
        //Image array to string for send
        String stringImage = "";
        int lengthNormImage = (int)Math.sqrt(normalizedImage.length);
        if (COUNT_PROCESSORS > 1) {
            commonCountPartitionStreams = lengthNormImage / COUNT_PROCESSORS;
            remainder = lengthNormImage - commonCountPartitionStreams * COUNT_PROCESSORS;
            if (commonCountPartitionStreams > 0)
                countDownLatch = new CountDownLatch(COUNT_PROCESSORS);
            else
                countDownLatch = new CountDownLatch(remainder);
            for (int i = 0; i < COUNT_PROCESSORS - 1; i++) {
                threadsList.add(new ThreadString(countDownLatch, i, commonCountPartitionStreams, normalizedImage, lengthNormImage, 0));
                threadsList.get(i).start();
            }
            threadsList.add(new ThreadString(countDownLatch, threadsList.size() - 1, commonCountPartitionStreams, normalizedImage, lengthNormImage, remainder));
            threadsList.get(threadsList.size() - 1).start();
            countDownLatch.await();
            for (int i = 0; i < threadsList.size(); i++)
                stringImage += threadsList.get(i).getLine();
            for (int i = threadsList.size() - 1; i >= 0; i--) {
                threadsList.get(i).interrupt();
                threadsList.remove(i);
            }
        }
        else {
            for (int i = 0; i < lengthNormImage; i++)
                for (int j = 0; j < lengthNormImage; j++)
                    stringImage += String.format("%.2f", normalizedImage[i][j]).replace(".","");
        }
        //Resume draw panel and draw threads
        for (int i = 0; i < COUNT_PROCESSORS; i++)
            threadsList.add(new ThreadDraw(canvas, paint, widthBrush, COUNT_PROCESSORS, i));
        isDrawable = true;
        return stringImage;
    }

    private boolean eventListener(View view, MotionEvent event) {
        if (isDrawable) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    points[0] = event.getX();
                    points[1] = event.getY();
                    BitmapChangeDown();
                    return true;
                case MotionEvent.ACTION_MOVE:
                    points[2] = event.getX();
                    points[3] = event.getY();
                    BitmapChangeMove();
                    return true;
                default:
                    return false;
            }
        }
        return false;
    }

    private void BitmapChangeDown() {
        if ((int) points[0] >= (locationViewDraw[0] - globalLocation[0]) && points[1] >= (locationViewDraw[1] - globalLocation[1]) && (int) points[0] < (locationViewDraw[0] - globalLocation[0] + width) && (int) points[1] < (locationViewDraw[1] - globalLocation[1] + height)) {
            points[0] -= (float) locationViewDraw[0] - (float)globalLocation[0];
            points[1] -= (float) locationViewDraw[1] - (float)globalLocation[1];
            canvas.drawCircle(points[0], points[1], widthBrush, paint);
            imageViewDraw.setImageBitmap(bitmap);
        }
        else
        {
            points[0] = -1.0f;
            points[1] = -1.0f;
        }
    }

    private void BitmapChangeMove() {
        if ((int) points[2] >= (locationViewDraw[0] - globalLocation[0]) && points[3] >= (locationViewDraw[1] - globalLocation[1]) && (int) points[2] < (locationViewDraw[0] - globalLocation[0] + width) && (int) points[3] < (locationViewDraw[1] - globalLocation[1] + height)) {
            points[2] -= (float) locationViewDraw[0] - (float)globalLocation[0];
            points[3] -= (float) locationViewDraw[1] - (float)globalLocation[1];
            drawCircles();
            imageViewDraw.setImageBitmap(bitmap);
            points[0] = points[2];
            points[1] = points[3];
        }
        else
        {
            points[0] = points[2];
            points[1] = points[3];
        }
    }

    private void drawCircles() {
        countSteps = (int)Math.sqrt((points[0] - points[2]) * (points[0] - points[2]) + (points[1] - points[3]) * (points[1] - points[3]));
        if (countSteps > 1) {
            steps[0] = Math.abs(points[0] - points[2]) / (float) countSteps;
            steps[1] = Math.abs(points[1] - points[3]) / (float) countSteps;
            if (countSteps >= COUNT_PROCESSORS && COUNT_PROCESSORS > 1) {
                commonCountPartitionStreams = countSteps / COUNT_PROCESSORS;
                remainder = countSteps - commonCountPartitionStreams * COUNT_PROCESSORS;
                for (int i = 0; i < remainder; i++)
                    threadsList.get(i).setSettings(commonCountPartitionStreams + 1, points, steps);
                if (commonCountPartitionStreams > 0)
                    for (int i = remainder; i < COUNT_PROCESSORS; i++)
                        threadsList.get(i).setSettings(commonCountPartitionStreams, points, steps);
                for (int i = 0; i < threadsList.size(); i++)
                    threadsList.get(i).run();
            }
            else {
                for (int i = 0; i < countSteps; i++) {
                    if (points[0] == points[2])
                        pointSteps[0] = points[0];
                    else {
                        if (points[0] > points[2])
                            pointSteps[0] = points[0] - steps[0] * i;
                        else
                            pointSteps[0] = points[0] + steps[0] * i;
                    }
                    if (points[1] == points[3])
                        pointSteps[1] = points[1];
                    else {
                        if (points[1] > points[3])
                            pointSteps[1] = points[1] - steps[1] * i;
                        else
                            pointSteps[1] = points[1] + steps[1] * i;
                    }
                    canvas.drawCircle(pointSteps[0], pointSteps[1], widthBrush, paint);
                }
            }
        }
        else
            canvas.drawCircle(points[2], points[3], widthBrush, paint);
    }
}