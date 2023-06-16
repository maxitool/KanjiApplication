package com.example.kanjiapplication.databases;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.kanjiapplication.databases.tables.GROUPS;
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
                info.ID = Integer.parseInt(cursor.getString(0));
                info.KANJI = cursor.getString(1);
                info.READING = cursor.getString(2);
                info.MEANING = cursor.getString(3);
                cursor.moveToNext();
            }
        cursor.close();
        return info;
    }

    public int[] getIDsKanji(char[] listKanji, int countKanji) {
        int count;
        if (listKanji.length < countKanji)
            count = listKanji.length;
        else
            count = countKanji;
        int[] ids = new int[count];
        for (int i = 0; i < count; i++) {
            cursor = sqLiteDatabase.rawQuery("SELECT ID FROM INFO WHERE KANJI = '" + listKanji[i] + "'", null);
            if (cursor.moveToFirst())
                ids[i] = Integer.parseInt(cursor.getString(0));
        }
        return ids;
    }

    public List<TASKS> getTasksKanji(char kanji) {
        List<TASKS> tasks = new ArrayList<>();
        int iterator = 0;
        cursor = sqLiteDatabase.rawQuery("SELECT * FROM TASKS WHERE WORD LIKE '%" + kanji + "%' ORDER BY GROUP_KANJI", null);
        if (cursor.moveToFirst())
            while (!cursor.isAfterLast()) {
                tasks.add(new TASKS());
                tasks.get(iterator).WORD = cursor.getString(0);
                tasks.get(iterator).FURIGANA = cursor.getString(1);
                tasks.get(iterator).ROMAJI = cursor.getString(2);
                tasks.get(iterator).TRANSLATION = cursor.getString(3);
                tasks.get(iterator).GROUP_KANJI = cursor.getString(4);
                cursor.moveToNext();
                iterator++;
            }
        cursor.close();
        return tasks;
    }

    public List<GROUPS> getGroups(String level) {
        List<GROUPS> groups = new ArrayList<>();
        int iterator = 0;
        cursor = sqLiteDatabase.rawQuery("SELECT * FROM MAIN_GROUPS WHERE LEVEL = " + level, null);
        if (cursor.moveToFirst())
            while (!cursor.isAfterLast()) {
                groups.add(new GROUPS());
                groups.get(iterator).ID = Integer.parseInt(cursor.getString(0));
                groups.get(iterator).NAME = cursor.getString(2);
                groups.get(iterator).LIST_KANJI = cursor.getString(3);
                cursor.moveToNext();
                iterator++;
            }
        cursor.close();
        return groups;
    }
}
