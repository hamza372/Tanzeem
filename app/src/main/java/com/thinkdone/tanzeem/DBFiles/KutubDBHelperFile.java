package com.thinkdone.tanzeem.DBFiles;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;

public class KutubDBHelperFile extends SQLiteOpenHelper {

    private String DATABASE_PATH = null;
    private static final int DATABASE_VERSION = 1;
    private static final String PACKAGE_NAME = "com.my.package";
    private SQLiteDatabase db;

    public KutubDBHelperFile(Context context,String databasename) {
        super(context, databasename, null, DATABASE_VERSION);
        DATABASE_PATH = getDatabaseFolder()+databasename;
        db = getWritableDatabase();
//        Log.d("tryDB","files "+db.isOpen());
       // Toast.makeText(context, "good in DB", Toast.LENGTH_SHORT).show();
    }

    public static String getDatabaseFolder() {
        return Environment.getExternalStorageDirectory().getAbsolutePath()+"/Android/data/TanzeemDB/";//+PACKAGE_NAME+"/databases/";
    }

    @Override
    public synchronized SQLiteDatabase getWritableDatabase() {
        try {
            if (db != null) {
                if (db.isOpen()) {
                    return db;
                }
            }
            return SQLiteDatabase.openDatabase(DATABASE_PATH, null, SQLiteDatabase.OPEN_READWRITE | SQLiteDatabase.NO_LOCALIZED_COLLATORS);
        }
        catch (Exception e) {
            return null;
        }
    }

    @Override
    public synchronized void close() {
        if (db != null) {
            db.close();
            db = null;
        }
        super.close();
    }

    @Override
    public void onCreate(SQLiteDatabase db) { }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) { }

    public void copyDataFr()
    {
        // Baab
    }

    public static ArrayList<String> getDatabasesList()
    {
        ArrayList<String> filesNames = new ArrayList<>();
        File directory = new File(getDatabaseFolder());
        if(!directory.exists()) {

                directory.mkdir();

        }
        File[] files = directory.listFiles();
        if(files != null) {
            Log.d("Files", "Size: " + files.length);
            for (int i = 0; i < files.length; i++) {
                Log.d("Files", "FileName:" + files[i].getName());
                filesNames.add(files[i].getName());
            }
        }
        return filesNames;
    }
}

