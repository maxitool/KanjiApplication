package com.example.kanjiapplication;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.kanjiapplication.databases.tables.TASKS;

public class InfoItemFragment extends Fragment {

    private TASKS tasks;
    private TextView textFurigana, textWord, textRomaji, textTranslation;

    public InfoItemFragment(TASKS tasks) {
        this.tasks = tasks;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        textFurigana = view.findViewById(R.id.textFurigana);
        textRomaji = view.findViewById(R.id.textRomaji);
        textWord = view.findViewById(R.id.textWord);
        textTranslation = view.findViewById(R.id.textTranslation);
        textFurigana.setText(tasks.FURIGANA.replaceAll("\\s+",""));
        textRomaji.setText(tasks.ROMAJI);
        textWord.setText(tasks.WORD);
        textTranslation.setText(tasks.TRANSLATION);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_info_item, container, false);
    }
}