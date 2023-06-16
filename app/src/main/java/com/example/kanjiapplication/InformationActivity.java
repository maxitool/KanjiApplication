package com.example.kanjiapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.example.kanjiapplication.databases.DBKanjiInfoAccess;
import com.example.kanjiapplication.databases.tables.INFO;
import com.example.kanjiapplication.databases.tables.TASKS;

import org.w3c.dom.Text;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class InformationActivity extends AppCompatActivity {

    private ImageButton previousButton, nextButton;
    private ImageView imageKanji;
    private LinearLayout linearLayoutInfo;
    private TextView reading, meaning;
    private char[] listKanji;
    private int pointer;
    private int maxPointer;
    private DBKanjiInfoAccess dbKanjiInfoAccess;
    private INFO info;
    private List<TASKS> listTasks;
    private FragmentManager fragmentManager;
    private char[] currentGroup;
    private List<TASKS> blockTasks;
    private FrameLayout flPointer;
    private InfoBlockFragment ibfPointer;
    private boolean isFirstGroup;
    private int numberGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_information);
        Intent intent = getIntent();
        String _listKanji = (String)intent.getSerializableExtra("listKanji");
        pointer = 0;
        maxPointer = _listKanji.length() - 1;
        listKanji = _listKanji.toCharArray();
        dbKanjiInfoAccess = new DBKanjiInfoAccess(this);
        previousButton = findViewById(R.id.previousButton);
        previousButton.setVisibility(View.GONE);
        nextButton = findViewById(R.id.nextButton);
        if (pointer == maxPointer)
            nextButton.setVisibility(View.GONE);
        reading = findViewById(R.id.reading);
        meaning = findViewById(R.id.meaning);
        imageKanji = findViewById(R.id.imageKanji);
        linearLayoutInfo = findViewById(R.id.linearLayoutInfo);
        fragmentManager = getSupportFragmentManager();
        getInfoFromBD();
        reDraw();
    }

    private void reDraw() {
        linearLayoutInfo.removeAllViews();
        imageKanji.setImageResource(this.getResources().getIdentifier("i1", "kanji_images", this.getPackageName()));
        reading.setText(info.READING);
        meaning.setText(info.MEANING);
        numberGroup = 0;
        for (int i = 0; i < listTasks.size(); i++) {
            if (i == 0) {
                isFirstGroup = true;
                currentGroup = listTasks.get(i).GROUP_KANJI.toCharArray();
                blockTasks = new ArrayList<>();
            }
            if (!checkGroupsNames(listTasks.get(i).GROUP_KANJI.toCharArray(), currentGroup)) {
                addBlock(i);
                isFirstGroup = false;
                currentGroup = listTasks.get(i).GROUP_KANJI.toCharArray();
                blockTasks = new ArrayList<>();
            }
            blockTasks.add(listTasks.get(i));
        }
        addBlock(listTasks.size() - 1);
    }

    private void addBlock(int id) {
        if (isFirstGroup)
            blockTasks.get(0).GROUP_KANJI = "";
        flPointer = new FrameLayout(this);
        flPointer.setId(id);
        flPointer.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        linearLayoutInfo.addView(flPointer);
        ibfPointer = new InfoBlockFragment(listKanji.length, numberGroup, blockTasks);
        FragmentTransaction fm = fragmentManager.beginTransaction();
        fm.replace(id, ibfPointer);
        fm.commit();
        numberGroup++;
    }

    private boolean checkGroupsNames(char[] group1, char[] group2) {
        if (group1.length != group1.length)
            return false;
        for (int i = 0; i < group1.length; i++)
            if (group1[i] != group2[i])
                return false;
        return true;
    }

    private void getInfoFromBD() {
        info = dbKanjiInfoAccess.getInfoKanji(listKanji[pointer]);
        listTasks = dbKanjiInfoAccess.getTasksKanji(listKanji[pointer]);
    }

    private void nextKanji(View view) {
        if (pointer < maxPointer) {
            pointer++;
            if (pointer == maxPointer)
                nextButton.setVisibility(View.GONE);
            if (!previousButton.isEnabled())
                previousButton.setVisibility(View.VISIBLE);
            getInfoFromBD();
            reDraw();
        }
    }
    private void previousKanji(View view) {
        if (pointer > 0) {
            pointer--;
            if (pointer == 0)
                previousButton.setVisibility(View.GONE);
            if (!nextButton.isEnabled())
                nextButton.setVisibility(View.VISIBLE);
            getInfoFromBD();
            reDraw();
        }
    }
}