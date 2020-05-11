package com.thinkdone.tanzeem.Search;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import androidx.cardview.widget.CardView;

import com.thinkdone.tanzeem.Constants;
import com.thinkdone.tanzeem.DB.BaabDBHelper;
import com.thinkdone.tanzeem.DB.DBHelper;
import com.thinkdone.tanzeem.DB.KitabDBHelper;
import com.thinkdone.tanzeem.DBFiles.KutubDBHelperFile;
import com.thinkdone.tanzeem.DataModels.KitabDataModel;
import com.thinkdone.tanzeem.DataModels.PageDataModel;
import com.thinkdone.tanzeem.Pages.PageViewActivity;
import com.thinkdone.tanzeem.R;

import java.util.ArrayList;
import java.util.HashMap;

public class SearchCursorAdapter extends CursorAdapter {
    Context context;
    String searchWord;
    public SearchCursorAdapter(Context context, Cursor cursor,String searchWord) {
    //    super();
        super(context, cursor, 0);
        this.searchWord = searchWord;
        this.context = context;
        hashMap = new HashMap<>();
    }

    // The newView method is used to inflate a new view and return it,
    // you don't bind any data to the view at this point.
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.card_for_page_list, parent, false);
    }

    // The bindView method is used to bind all data to a given view
    // such as setting the text on a TextView.
    PageDataModel pageDataModel;
    @Override
    public void bindView(View view, final Context context, Cursor cursor) {
        // Find fields to populate in inflated template
        pageDataModel = new PageDataModel();
        CardView layout;
        TextView bookName;
        TextView pageNo;
        layout = view.findViewById(R.id.bmcard);
        bookName = view.findViewById(R.id.nametvcat);
        pageNo = view.findViewById(R.id.textView4);

        pageDataModel.setPage_id(cursor.getInt(cursor.getColumnIndex(DBHelper.PageEntry.ID)));
        pageDataModel.setPage_no(cursor.getInt(cursor.getColumnIndex(DBHelper.PageEntry.PAGE_NO)));
        pageDataModel.setPageBaabId(cursor.getInt(cursor.getColumnIndex(DBHelper.PageEntry.BAAB_ID)));
        pageDataModel.setBookId(getBookId(pageDataModel.getPageBaabId()));
        pageDataModel.setBookName(getBookNameFromBaabId(pageDataModel.getBookId()));

        pageNo.setText("صفحہ نمبر: " +pageDataModel.getPage_no());
        bookName.setText(getBookNameFromBaabId(pageDataModel.getBookId()));
        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, PageViewActivity.class);
                intent.putExtra(Constants.BOOK,pageDataModel.getBookId());
                intent.putExtra(Constants.BOOKNAME,pageDataModel.getBookName());
                intent.putExtra(DBHelper.PageEntry.PAGE_NO,pageDataModel.getPage_no());
                intent.putExtra(DBHelper.BaabEntry.ID,pageDataModel.getPageBaabId());
                intent.putExtra(Constants.IS_SEARCH,true);
                intent.putExtra(Constants.SEARCH,searchWord);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        });

    }

    HashMap<Integer,Integer> hashMap;
    public int getBookId(int baabId)
    {
        ArrayList<String> dbList = KutubDBHelperFile.getDatabasesList();
        KitabDataModel kitabDataModel = new KitabDataModel();
        int idReturn = -1;
        for(int ij=-1;ij<dbList.size();ij++) {
            if (ij == -1) {
                if(hashMap.containsKey(baabId))
                {
                    return  hashMap.get(baabId);
                }
                BaabDBHelper baabDBHelper = new BaabDBHelper(context);
                SQLiteDatabase sqlLiteDatabase = baabDBHelper.getReadableDatabase();
                Cursor cursor = sqlLiteDatabase.rawQuery("SELECT "+DBHelper.BaabEntry.BOOK_ID+" from "+DBHelper.BaabEntry.TABLE_NAME+" where "+DBHelper.BaabEntry.ID+" = "+baabId,null);
                cursor.moveToNext();
                int bookId =   cursor.getInt(cursor.getColumnIndex(DBHelper.BaabEntry.BOOK_ID));
                hashMap.put(baabId,bookId);
                idReturn = bookId;
            }
            else
            {
                if(hashMap.containsKey(baabId))
                {
                    return hashMap.get(baabId);
                }
                KutubDBHelperFile kutubDBHelperFile = new KutubDBHelperFile(context, dbList.get(ij));
                SQLiteDatabase sqLiteDatabase = kutubDBHelperFile.getWritableDatabase();
                Cursor cursor = sqLiteDatabase.rawQuery("SELECT "+DBHelper.BaabEntry.BOOK_ID+" from "+DBHelper.BaabEntry.TABLE_NAME+" where "+DBHelper.BaabEntry.ID+" = "+baabId,null);
                cursor.moveToNext();
                int bookId =   cursor.getInt(cursor.getColumnIndex(DBHelper.BaabEntry.BOOK_ID));
                hashMap.put(baabId,bookId);
                idReturn = bookId;
            }
        }
        return idReturn;
    }

    public String getBookNameFromBaabId(int bookId)
    {
        ArrayList<String> dbList = KutubDBHelperFile.getDatabasesList();
        KitabDataModel kitabDataModel = new KitabDataModel();
        for(int ij=-1;ij<dbList.size();ij++) {
            if (ij == -1) {
                KitabDBHelper kitabDBHelper = new KitabDBHelper(context);
                SQLiteDatabase sqLiteDatabase = kitabDBHelper.getReadableDatabase();
                Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM "+DBHelper.KitabEntry.TABLE_NAME+" where "+DBHelper.KitabEntry.ID+" = "+bookId, null);
                for (int i = 0; i < cursor.getCount(); i++) {
                    cursor.moveToNext();
                    kitabDataModel.setBookName(cursor.getString(cursor.getColumnIndex(DBHelper.KitabEntry.NAME_URDU)));
                }
                cursor.close();
            } else {
                KutubDBHelperFile kutubDBHelperFile = new KutubDBHelperFile(context, dbList.get(ij));
                SQLiteDatabase sqLiteDatabase = kutubDBHelperFile.getWritableDatabase();
                Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM "+DBHelper.KitabEntry.TABLE_NAME+" where "+DBHelper.KitabEntry.ID+" = "+bookId, null);
                for (int i = 0; i < cursor.getCount(); i++) {
                    cursor.moveToNext();
                    kitabDataModel.setBookName(cursor.getString(cursor.getColumnIndex(DBHelper.KitabEntry.NAME_URDU)));
                }
                cursor.close();

            }
        }
        return kitabDataModel.getBookName();
    }
}
