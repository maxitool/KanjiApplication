package com.example.kanjiapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.TooltipCompat;
import androidx.fragment.app.FragmentTransaction;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

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
        AlertDialog alertDialog = new AlertDialog.Builder(this).setTitle("From robot").setMessage("Sending a hieroglyph to the server...").show();
        countDownLatch = new CountDownLatch(1);
        SendToServer sendToServer = new SendToServer(countDownLatch, image);
        countDownLatch.await();
        String listKanji = sendToServer.getKanjiList();
        alertDialog.hide();
        if (listKanji == "") {
            Toast.makeText(this, "No internet connection", Toast.LENGTH_LONG).show();
            return;
        }
        if (listKanji == "?") {
            Toast.makeText(this, "The drawn hieroglyph was not found", Toast.LENGTH_LONG).show();
            return;
        }
        Intent intent = new Intent(this, InformationActivity.class);
        intent.putExtra("listKanji", listKanji);
        startActivity(intent);
    }
}