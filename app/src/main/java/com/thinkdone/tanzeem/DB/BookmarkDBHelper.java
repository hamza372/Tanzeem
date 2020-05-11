package com.thinkdone.tanzeem.DB;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class BookmarkDBHelper extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "MyDB";


    public BookmarkDBHelper(Context context) {
        super(context, DATABASE_NAME,null, DATABASE_VERSION);
    }

    static BookmarkDBHelper sInstance;
    public static synchronized BookmarkDBHelper getInstance(Context context) {
        // Use the application context, which will ensure that you
        // don't accidentally leak an Activity's context.
        // See this article for more information: http://bit.ly/6LRzfx
        if (sInstance == null) {
            sInstance = new BookmarkDBHelper(context.getApplicationContext());
        }
        return sInstance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String dbstring = "CREATE TABLE IF NOT EXISTS "+
                DBHelper.BookmarkEntry.TABLE_NAME +"("+
                DBHelper.PageEntry.ID+" INTEGER  PRIMARY KEY,"+ DBHelper.PageEntry.PAGE_NO+" INTEGER,"+ DBHelper.KitabEntry.NAME_URDU+" TEXT,"+DBHelper.BookmarkEntry.KITAB_ID+" INTEGER,"+DBHelper.BaabEntry.Baab_ID+" INTEGER)";
        Log.d("tryString",dbstring);
        db.execSQL(dbstring);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void addPage(int Id, int no, String name,int bookId,int babPosition,String bookName) {
        Log.d("tryFav",Id+"  "+no+"  "+name);
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(DBHelper.PageEntry.ID, Id);
        values.put(DBHelper.PageEntry.PAGE_NO, no);
        values.put(DBHelper.KitabEntry.NAME_URDU, name);
        values.put(DBHelper.BaabEntry.Baab_ID,babPosition);
        values.put(DBHelper.KitabEntry.NAME,bookName);
        //values.put(DBHelper.FavouriteEntry.ALIAS, alias);

       // Log.d("tryFav",alias+"");
        // Inserting Row
       // long i = db.insert(DBHelper.BookmarkEntry.TABLE_NAME, null, values);

        db.execSQL("INSERT INTO "+DBHelper.BookmarkEntry.TABLE_NAME+" VALUES("+Id+","+no+",'"+name+"',"+bookId+","+babPosition+") ");
        //Log.d("tryFatwa",i+"");
        //2nd argument is String containing nullColumnHack
        db.close(); // Closing database connection
    }

    // Deleting single contact
    public void deletePage(int page_id) {
        Log.d("tryFav",page_id+"  ");
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(DBHelper.BookmarkEntry.TABLE_NAME, DBHelper.PageEntry.ID + " = ?",
                new String[] { String.valueOf(page_id) });
        db.close();
    }

    // code to get the single contact
    public boolean isPageFavourite(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(DBHelper.BookmarkEntry.TABLE_NAME, new String[] {DBHelper.PageEntry.ID,
                }, DBHelper.PageEntry.ID + "=?",
                new String[] { String.valueOf(id) }, null, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();

        }else{
            return false;
        }

        Log.d("tryFatwa",cursor.getCount()+"");
        if(cursor.getCount()>0)
        {
            Log.d("tryFatwa",cursor.getInt(cursor.getColumnIndex(DBHelper.PageEntry.ID))+"");
            return  true;
        }

        return  false;
    }
}
