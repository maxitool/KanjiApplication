package com.example.kanjiapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.example.kanjiapplication.databases.DBKanjiInfoAccess;
import com.example.kanjiapplication.databases.tables.GROUPS;

import java.util.List;

public class ChooseBlockActivity extends AppCompatActivity {

    private static final int COUNT_GROUPS_IN_ROW = 3;
    private LinearLayout linearLayoutBlocks;
    private DBKanjiInfoAccess dbKanjiInfoAccess;
    private LinearLayout llPointer;
    private FrameLayout flPointer;
    private ImageBlockFragment imageBlockFragment;
    private String level;
    private List<GROUPS> listGroups;
    private FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_block);
        Intent intent = getIntent();
        level = (String)intent.getSerializableExtra("level");
        fragmentManager = getSupportFragmentManager();
        linearLayoutBlocks = findViewById(R.id.linearLayoutBlocks);
        dbKanjiInfoAccess = new DBKanjiInfoAccess(this);
        listGroups = dbKanjiInfoAccess.getGroups(level);
        setImagesFragments();
        if (level.equals("Own")) {
            Button button = new Button(this);
            button.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            button.setText("Добавить");
            linearLayoutBlocks.addView(button);
        }
    }

    private void setImagesFragments() {
        int countRows = (int)Math.ceil((double)listGroups.size() / (double)COUNT_GROUPS_IN_ROW);
        int countGroups = listGroups.size();
        int count;
        for (int i = 0; i < countRows; i++) {
            llPointer = new LinearLayout(this);
            llPointer.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.VERTICAL));
            linearLayoutBlocks.addView(llPointer);
            if (countGroups > COUNT_GROUPS_IN_ROW)
                count = COUNT_GROUPS_IN_ROW;
            else
                count = countGroups;
            for (int j = 0; j < count; j++) {
                flPointer = new FrameLayout(this);
                flPointer.setId(i * COUNT_GROUPS_IN_ROW + j + 1);
                flPointer.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                llPointer.addView(flPointer);
                imageBlockFragment = new ImageBlockFragment(listGroups.get(i * COUNT_GROUPS_IN_ROW + j));
                FragmentTransaction fm = fragmentManager.beginTransaction();
                fm.replace(i * COUNT_GROUPS_IN_ROW + j + 1, imageBlockFragment);
                fm.commit();
            }
            countGroups  -= COUNT_GROUPS_IN_ROW;
        }
    }
}