package com.example.kanjiapplication.databases;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.kanjiapplication.databases.tables.INFO;
import com.example.kanjiapplication.databases.tables.TASKS;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DBKanjiInfoAccess {

    private DBKanjiInfoHelper dbHelper;
    private SQLiteDatabase sqLiteDatabase;
    private Cursor cursor;

    public DBKanjiInfoAccess(Context context) {
        dbHelper = new DBKanjiInfoHelper(context);
        try {
            dbHelper.createDataBase();
        } catch (IOException e) {
            e.printStackTrace();
        }
        sqLiteDatabase = dbHelper.getReadableDatabase();
    }

    public INFO getInfoKanji(char kanji) {
        INFO info = new INFO();
        cursor = sqLiteDatabase.rawQuery("SELECT * FROM INFO WHERE KANJI = '" + kanji + "'", null);
        if (cursor.moveToFirst())
            while (!cursor.isAfterLast()) {
                info.KANJI = cursor.getString(0);
                info.READING = cursor.getString(1);
                info.MEANING = cursor.getString(2);
                cursor.moveToNext();
            }
        cursor.close();
        return info;
    }

    public List<TASKS> getTasksKanji(char kanji) {
        List<TASKS> tasks = new ArrayList<>();
        int iterator = 0;
        cursor = sqLiteDatabase.rawQuery("SELECT * FROM TASKS WHERE WORD LIKE '%" + kanji + "%'", null);
        if (cursor.moveToFirst())
            while (!cursor.isAfterLast()) {
                tasks.add(new TASKS());
                tasks.get(iterator).WORD = cursor.getString(0);
                tasks.get(iterator).FURIGANA = cursor.getString(1);
                tasks.get(iterator).ROMAJI = cursor.getString(2);
                tasks.get(iterator).TRANSLATION = cursor.getString(3);
                cursor.moveToNext();
                iterator++;
            }
        cursor.close();
        return tasks;
    }
}
