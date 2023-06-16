package com.example.kanjiapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.example.kanjiapplication.databases.tables.TASKS;
import java.util.Objects;

public class TaskActivity extends AppCompatActivity {

    public enum TaskResult {
        Correct,
        Incorrect,
        Unknown
    }

    private TaskResult _result = TaskResult.Unknown;
    private TASKS _task;
    private String _answer;
    private TextView _tvWord, _tvFurigana, _tvRomanji, _tvTranslation;
    private Button _bCheck, _bNext, _bBack;
    private FrameLayout _frameLayoutTask;
    private DrawFragment _drawFragment;
    private char[] _listKanji;

    public void setTask (TASKS task) {
        _task = task;

        _tvWord.setText(_task.WORD);
        _tvFurigana.setText(_task.FURIGANA);
        _tvRomanji.setText(_task.ROMAJI);
        _tvTranslation.setText(_task.TRANSLATION);
    }

    public TaskResult getResult () {
        return _result;
    }

    public void setAnswer (String answer) {
        _answer = answer;
    }

    public TASKS getTask () {
        return _task;
    }

    public String getAnswer () {
        return _answer;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task);
        Intent intent = getIntent();
        String listKanji = (String)intent.getSerializableExtra("listKanji");
        _listKanji = listKanji.toCharArray();
        _tvWord = findViewById(R.id.textViewWord);
        _tvFurigana = findViewById(R.id.textViewFurigana);
        _tvRomanji = findViewById(R.id.textViewRomanji);
        _tvTranslation = findViewById(R.id.textViewTranslation);
        _frameLayoutTask = findViewById(R.id.frameLayoutTask);
        _drawFragment = new DrawFragment();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frameLayoutTask, _drawFragment);
        fragmentTransaction.commit();

        _bCheck = findViewById(R.id.buttonCheck);
        _bCheck.setOnClickListener(view -> { // TODO: On check button clicked listener
            String printed = "";

            try {
                // TODO: Отправка и получение файлов с сервера
                _result = Objects.equals(printed, _answer) ? TaskResult.Correct : TaskResult.Incorrect;
                _bNext.setEnabled(true);
            }
            catch (Exception e) {
                _bNext.setEnabled(false);
            }
        });

        _bNext = findViewById(R.id.buttonNext);
        _bNext.setOnClickListener(view -> { // TODO: On next button clicked listener

        });

        _bBack = findViewById(R.id.buttonBack);
        _bBack.setOnClickListener(view -> { // TODO: On back button clicked listener

        });
    }
}