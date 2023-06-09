package com.example.kanjiapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.widget.FrameLayout;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

public class MainActivity extends AppCompatActivity {

    private DrawFragment drawFragment;
    private FrameLayout frameLayout;
    private CountDownLatch countDownLatch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        drawFragment = new DrawFragment();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frameLayout, drawFragment);
        fragmentTransaction.commit();
        frameLayout = findViewById(R.id.frameLayout);
        frameLayout.post(new Runnable() {
            @Override
            public void run() {
                int[] globalLocation = new int[2];
                frameLayout.getLocationOnScreen(globalLocation);
                drawFragment.setSettings(globalLocation);
            }
        });
    }

    public void searchKanji(View view) throws InterruptedException, IOException {
        String image = drawFragment.getImage();
        countDownLatch = new CountDownLatch(1);
        SendToServer sendToServer = new SendToServer(countDownLatch, image);
        countDownLatch.await();
        String kanjiList = sendToServer.getKanjiList();
        int a = 0;
    }
}