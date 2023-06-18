package com.example.kanjiapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.TooltipCompat;
import androidx.fragment.app.FragmentTransaction;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.navigation.NavigationView;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

public class MainActivity extends AppCompatActivity {

    private DrawFragment drawFragment;
    private FrameLayout frameLayout;
    private CountDownLatch countDownLatch;
    private NavigationView navigationMenu;
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //getSupportActionBar().setIcon(R.drawable.ic_baseline_view_headline_24);
        //getSupportActionBar().setDisplayShowHomeEnabled(true);

        navigationMenu = findViewById(R.id.navigationMenu);
        navigationMenu.setNavigationItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.menuLevel1:
                    intent = new Intent(MainActivity.this, ChooseBlockActivity.class);
                    intent.putExtra("level", "Уровень 1");
                    startActivity(intent);
                    return true;
                case R.id.menuLevel2:
                    intent = new Intent(MainActivity.this, ChooseBlockActivity.class);
                    intent.putExtra("level", "Уровень 2");
                    startActivity(intent);
                    return true;
                case R.id.menuLevel3:
                    intent = new Intent(MainActivity.this, ChooseBlockActivity.class);
                    intent.putExtra("level", "Уровень 3");
                    startActivity(intent);
                    return true;
                case R.id.menuOwnGroups:
                    intent = new Intent(MainActivity.this, ChooseBlockActivity.class);
                    intent.putExtra("level", "Own");
                    startActivity(intent);
                    return true;
            }
            return false;
        });
        drawFragment = new DrawFragment();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frameLayout, drawFragment);
        fragmentTransaction.commit();
        frameLayout = findViewById(R.id.frameLayout);
        frameLayout.post(() -> {
            int[] globalLocation = new int[2];
            frameLayout.getLocationOnScreen(globalLocation);
            drawFragment.setSettings(globalLocation);
        });
    }

    public void searchKanji(View view) throws InterruptedException, IOException {
        String image = drawFragment.getImage();
        if (image== "") {
            Toast.makeText(this, "Paint panel is empty", Toast.LENGTH_LONG).show();
            return;
        }
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
        intent = new Intent(this, InformationActivity.class);
        intent.putExtra("listKanji", listKanji);
        startActivity(intent);
    }
}