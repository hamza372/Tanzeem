package com.thinkdone.tanzeem.DB;

import android.content.Context;

import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

public class PageDBHelper extends SQLiteAssetHelper {
    public static int DATABASE_VERSION = 1;
    public static String DATABASE_NAME = "tanzeemdb2";

    public PageDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
}
