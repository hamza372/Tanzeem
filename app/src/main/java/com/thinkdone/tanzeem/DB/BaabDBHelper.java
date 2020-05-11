package com.thinkdone.tanzeem.DB;

import android.content.Context;

import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

public class BaabDBHelper extends SQLiteAssetHelper {
    public static int DATABASE_VERSION = 1;
    public static String DATABASE_NAME = "tanzeemdb2";

    public BaabDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
}