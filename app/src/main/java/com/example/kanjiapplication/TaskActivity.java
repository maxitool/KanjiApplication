package com.example.kanjiapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import java.util.Locale;

public class TaskActivity extends AppCompatActivity {

    private int _taskNumber = -228;
    private String _symbols = "Please, input the symbols";
    private String _rightAnswer = "Please, input the right answer";
    private TextView _taskNumberTextView;
    private TextView _symbolsTextView;

    public void set_taskNumber (int taskNumber) {
        _taskNumber = taskNumber;
    }

    public void set_symbols (String symbols) {
        _symbols = symbols;
    }

    public void set_rightAnswer (String rightAnswer) {
        _rightAnswer = rightAnswer;
    }

    public int get_taskNumber () {
        return  _taskNumber;
    }

    public String get_symbols () {
        return _symbols;
    }

    public String get_rightAnswer () {
        return _rightAnswer;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task);

        //_taskNumberTextView = findViewById(R.id.taskNumberTextView);
        //_taskNumberTextView.setEnabled(false);
        //_taskNumberTextView.setText(String.format(Locale.US,"Task: %d", _taskNumber));

        //_symbolsTextView = findViewById(R.id.symbolsTextView);
        //_symbolsTextView.setEnabled(false);
        //_symbolsTextView.setText(_symbols);
    }

    public void check (View view) {
        // TODO: check is drawing the right answer
    }

    public void back (View view) {

    }

    public void Next (View view) {

    }
}