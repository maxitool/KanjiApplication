package com.example.kanjiapplication;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.kanjiapplication.databases.tables.TASKS;

import java.util.List;

public class InfoBlockFragment extends Fragment {

    private TextView textGroup;
    private LinearLayout linearLayoutItems;
    private FragmentManager fragmentManager;
    private FrameLayout flPointer;
    private InfoItemFragment iifPointer;
    private int occupied;
    private int numberGroup;
    private List<TASKS> listTasks;

    public InfoBlockFragment(int occupied, int numberGroup, List<TASKS> listTasks) {
        this.occupied = occupied;
        this.numberGroup = numberGroup;
        this.listTasks = listTasks;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        textGroup = view.findViewById(R.id.textGroup);
        linearLayoutItems = view.findViewById(R.id.linearLayoutItems);
        if (numberGroup == 0)
            textGroup.setText(listTasks.get(0).GROUP_KANJI);
        else
            textGroup.setText(Integer.toString(numberGroup) + ". " + listTasks.get(0).GROUP_KANJI);
        fragmentManager = getChildFragmentManager();
        for (int i = 0; i < listTasks.size(); i++) {
            flPointer = new FrameLayout(this.getContext());
            flPointer.setId(occupied + i);
            flPointer.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            linearLayoutItems.addView(flPointer);
            iifPointer = new InfoItemFragment(listTasks.get(i));
            FragmentTransaction ft = fragmentManager.beginTransaction();
            ft.replace(occupied + i, iifPointer);
            ft.commit();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_info_block, container, false);
    }
}