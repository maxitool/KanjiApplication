package com.example.kanjiapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.kanjiapplication.databases.DBKanjiInfoAccess;
import com.example.kanjiapplication.databases.tables.INFO;
import com.example.kanjiapplication.databases.tables.TASKS;

import java.util.List;

public class InformationActivity extends AppCompatActivity {

    private ImageButton previousButton, nextButton;
    private TextView reading, meaning;
    private char[] listKanji;
    private int pointer;
    private int maxPointer;
    private DBKanjiInfoAccess dbKanjiInfoAccess;
    private INFO info;
    private List<TASKS> tasks;

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
        previousButton.setEnabled(false);
        nextButton = findViewById(R.id.nextButton);
        if (pointer == maxPointer)
            nextButton.setEnabled(false);
        reading = findViewById(R.id.reading);
        meaning = findViewById(R.id.meaning);
        getInfoFromBD();
        reDraw();
    }

    private void reDraw() {
        reading.setText(info.READING);
        meaning.setText(info.MEANING);
    }

    private void getInfoFromBD() {
        info = dbKanjiInfoAccess.getInfoKanji(listKanji[pointer]);
        tasks = dbKanjiInfoAccess.getTasksKanji(listKanji[pointer]);
    }

    private void nextKanji() {
        if (pointer < maxPointer) {
            pointer++;
            if (pointer == maxPointer)
                nextButton.setEnabled(false);
            if (!previousButton.isEnabled())
                previousButton.setEnabled(true);
            getInfoFromBD();
            reDraw();
        }
    }
    private void previousKanji() {
        if (pointer > 0) {
            pointer--;
            if (pointer == 0)
                previousButton.setEnabled(false);
            if (!nextButton.isEnabled())
                nextButton.setEnabled(true);
            getInfoFromBD();
            reDraw();
        }
    }
}