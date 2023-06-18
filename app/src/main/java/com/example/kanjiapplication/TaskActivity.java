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

    private TaskResult result = TaskResult.Unknown;
    private TASKS task;
    private String answer;
    private TextView tvWord, tvFurigana, tvRomanji, tvTranslation;
    private Button bCheck, bNext, bBack;
    private FrameLayout frameLayoutTask;
    private DrawFragment drawFragment;
    private char[] listKanji;
    private FrameLayout frameLayout;

    public void setTask (TASKS task) {
        task = task;

        tvWord.setText(task.WORD);
        tvFurigana.setText(task.FURIGANA);
        tvRomanji.setText(task.ROMAJI);
        //tvTranslation.setText(task.TRANSLATION);
    }

    public TaskResult getResult () {
        return result;
    }

    public void setAnswer (String answer) {
        this.answer = tvTranslation.getText().toString();
    }

    public TASKS getTask () {
        return task;
    }

    public String getAnswer () {
        return answer;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task);
        Intent intent = getIntent();
        String _listKanji = (String)intent.getSerializableExtra("listKanji");
        listKanji = _listKanji.toCharArray();
        tvWord = findViewById(R.id.textViewWord);
        tvFurigana = findViewById(R.id.textViewFurigana);
        tvRomanji = findViewById(R.id.textViewRomanji);
        tvTranslation = findViewById(R.id.textViewTranslation);
        frameLayoutTask = findViewById(R.id.frameLayoutTask);
        drawFragment = new DrawFragment();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frameLayoutTask, drawFragment);
        fragmentTransaction.commit();

        bCheck = findViewById(R.id.buttonCheck);
        bCheck.setOnClickListener(view -> { // TODO: On check button clicked listener
            String printed = "";

            try {
                // TODO: Отправка и получение файлов с сервера
                result = Objects.equals(printed, answer) ? TaskResult.Correct : TaskResult.Incorrect;
                bNext.setEnabled(true);
            }
            catch (Exception e) {
                bNext.setEnabled(false);
            }
        });

        bNext = findViewById(R.id.buttonNext);
        bNext.setOnClickListener(view -> { // TODO: On next button clicked listener

        });

        bBack = findViewById(R.id.buttonBack);
        bBack.setOnClickListener(view -> { // TODO: On back button clicked listener

        });

        frameLayout = findViewById(R.id.frameLayoutTask);
        frameLayout.post(() -> {
            int[] globalLocation = new int[2];
            frameLayout.getLocationOnScreen(globalLocation);
            drawFragment.setSettings(globalLocation);
        });
    }
}