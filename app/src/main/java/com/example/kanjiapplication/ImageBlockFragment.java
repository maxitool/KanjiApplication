package com.example.kanjiapplication;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.kanjiapplication.databases.DBKanjiInfoAccess;
import com.example.kanjiapplication.databases.tables.GROUPS;

public class ImageBlockFragment extends Fragment {

    private static final int COUNT_IMAGES = 4;
    private ImageView[] images;
    private TextView textNameGroup;
    private RelativeLayout relativeLayouImages;
    private GROUPS groups;

    public ImageBlockFragment(GROUPS groups) {
        this.groups = groups;
        images = new ImageView[COUNT_IMAGES];
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        textNameGroup = view.findViewById(R.id.textNameGroup);
        textNameGroup.setText(groups.NAME);
        DBKanjiInfoAccess dbKanjiInfoAccess = new DBKanjiInfoAccess(view.getContext());
        char[] listKanji = groups.LIST_KANJI.toCharArray();
        int[] ids = dbKanjiInfoAccess.getIDsKanji(listKanji, COUNT_IMAGES);
        //set resources of images
        images[0] = view.findViewById(R.id.imageKanji1);
        images[1] = view.findViewById(R.id.imageKanji2);
        images[2] = view.findViewById(R.id.imageKanji3);
        images[3] = view.findViewById(R.id.imageKanji4);
        for (int i = 0; i < COUNT_IMAGES; i++)
            images[i].setImageResource(this.getResources().getIdentifier('i' + Integer.toString(ids[i]), "drawable", getActivity().getPackageName()));
        relativeLayouImages = view.findViewById(R.id.relativeLayouImages);
        relativeLayouImages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), TaskActivity.class);
                intent.putExtra("listKanji", groups.LIST_KANJI);
                startActivity(intent);
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_image_block, container, false);
    }
}