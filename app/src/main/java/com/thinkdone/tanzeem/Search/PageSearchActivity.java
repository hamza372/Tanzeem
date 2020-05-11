package com.thinkdone.tanzeem.Search;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Layout;
import android.text.SpannableString;
import android.text.style.AlignmentSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.thinkdone.tanzeem.BookMarks.BookMarksActivity;
import com.thinkdone.tanzeem.Categories.CategoriesActivity;
import com.thinkdone.tanzeem.Constants;
import com.thinkdone.tanzeem.DB.BaabDBHelper;
import com.thinkdone.tanzeem.DB.DBHelper;
import com.thinkdone.tanzeem.DB.KitabDBHelper;
import com.thinkdone.tanzeem.DB.PageDBHelper;
import com.thinkdone.tanzeem.DBFiles.KutubDBHelperFile;
import com.thinkdone.tanzeem.DataModels.KitabDataModel;
import com.thinkdone.tanzeem.DataModels.PageDataModel;
import com.thinkdone.tanzeem.Drawer.About_us;
import com.thinkdone.tanzeem.Kutub.KutubListActivity;
import com.thinkdone.tanzeem.MainActivity;
import com.thinkdone.tanzeem.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.HashMap;

import static android.view.View.VISIBLE;

public class PageSearchActivity extends AppCompatActivity {

    String searchWord;
    RecyclerView recyclerView;
    HashMap<Integer,Integer> hashMap;
    ProgressBar pbar;
    TextView resultCount;

    Activity activity = this;
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {

            switch ( item.getItemId()) {
                case R.id.fist_page:
                    startActivity(new Intent(activity, MainActivity.class));
                    overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
                    break;
                case R.id.kutub:
                    startActivity(new Intent(activity, KutubListActivity.class));
                    overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
                    break;
                case R.id.bookmark:
                    startActivity(new Intent(activity, BookMarksActivity.class));
                    overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
                    break;
                case R.id.topic:
                    startActivity(new Intent(activity, CategoriesActivity.class));
                    overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
                    break;

            }
            return  true;
        };
    };
    //ListView searchListView;
    int currentItems;
    int totalItems;
    int scrollOutItems;
    int itemsOnPage = 15;
    Boolean isScroll = false;
    private static final float END_SCALE = 0.7f;
    LinearLayoutManager linearLayoutManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nav_activity_search_page_list);
        pbar = findViewById(R.id.progressBar);
        hashMap = new HashMap<>();

        TextView title = findViewById(R.id.title);
        resultCount = findViewById(R.id.textView11);
        searchWord = getIntent().getExtras().getString(Constants.SEARCH);
        title.setText(searchWord);

        recyclerView = findViewById(R.id.bmrecycler);
        linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        searchPages(searchWord);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if(newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL){
                    isScroll = true;
                }
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                currentItems = linearLayoutManager.getChildCount();
                totalItems = linearLayoutManager.getItemCount();
                scrollOutItems = linearLayoutManager.findFirstVisibleItemPosition();

                if(isScroll && (currentItems + scrollOutItems == totalItems)){
                    isScroll = false;
                    pbar.setVisibility(VISIBLE);
                    if(totalItems<cursor.getCount()) {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                if(cursor.getCount() - totalItems<15){
                                    itemsOnPage = cursor.getCount() - totalItems;
                                }
                                for (int i = 0; i < itemsOnPage; i++) {
                                    final PageDataModel pageDataModel = new PageDataModel();
                                    cursor.moveToNext();
                                    pageDataModel.setPage_id(cursor.getInt(cursor.getColumnIndex(DBHelper.PageEntry.ID)));
                                    pageDataModel.setPage_no(cursor.getInt(cursor.getColumnIndex(DBHelper.PageEntry.PAGE_NO)));
                                    pageDataModel.setPageBaabId(cursor.getInt(cursor.getColumnIndex(DBHelper.PageEntry.BAAB_ID)));
                                    pageDataModel.setBookId(getBookId(pageDataModel.getPageBaabId()));
                                    pageDataModel.setBookName(getBookNameFromBaabId(pageDataModel.getBookId()));
                                    pageDataModel.setCursorIndex(0);
                                    handler.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            bookMarkPageAdapter.addPage(pageDataModel);
                                            bookMarkPageAdapter.notifyDataSetChanged();
                                            pbar.setVisibility(View.GONE);
                                        }
                                    });
                                }
                            }
                        }).start();
                    }else{
                        pbar.setVisibility(View.GONE);
                    }
                }
            }
        });

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.bottomNavigationView);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        Menu menu = navigation.getMenu();
        MenuItem menuItem = menu.getItem(3);
        menuItem.setChecked(true);

       // searchListView = (ListView) findViewById(R.id.listsearch);

        //TODO navigation drawer
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                int id = menuItem.getItemId();

                if (id == R.id.homed) {
                    Intent intent = new Intent(PageSearchActivity.this, MainActivity.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
                } else if (id == R.id.aboutd) {
                    Intent intent = null;
                    intent = new Intent(PageSearchActivity.this, About_us.class);
                    intent.putExtra("title","کچھ ہمارے بارے میں");
                    intent.putExtra("type", "about");
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
                } else if (id == R.id.booksd) {
                    Intent intent = new Intent(PageSearchActivity.this, KutubListActivity.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
                } else if (id == R.id.tawund) {
                    Intent intent = null;
                    intent = new Intent(PageSearchActivity.this, About_us.class);
                    intent.putExtra("title","تعاون");
                    intent.putExtra("type", "tawun");
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
                } else if (id == R.id.rabtad) {
                    Intent intent = null;
                    intent = new Intent(PageSearchActivity.this, About_us.class);
                    intent.putExtra("title","رابطہ");
                    intent.putExtra("type", "rabta");
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
                    // Intent intent = new Intent(MainActivity.this, AboutApp.class);
//                    startActivity(intent);
//                    overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
                }
                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                drawer.closeDrawer(GravityCompat.START);
                return true;
            }
        });

        final DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        final ImageView layout = findViewById(R.id.imageView7);
        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawer.openDrawer(GravityCompat.START);
            }
        });
        //TODO header close button code
        View headerV = navigationView.inflateHeaderView(R.layout.header_drawer);
        ImageView close = headerV.findViewById(R.id.imageView25);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawer.closeDrawer(GravityCompat.START);
            }
        });

        final ConstraintLayout contentView = findViewById(R.id.mainll);
        drawer.setScrimColor(Color.TRANSPARENT);
        drawer.addDrawerListener(new DrawerLayout.SimpleDrawerListener() {
                                     @Override
                                     public void onDrawerSlide(View drawerView, float slideOffset) {
                                         //labelView.setVisibility(slideOffset > 0 ? View.VISIBLE : View.GONE);
                                         // Scale the View based on current slide offset
                                         final float diffScaledOffset = slideOffset * (1 - END_SCALE);
                                         final float offsetScale = 1 - diffScaledOffset;
                                         contentView.setScaleX(offsetScale);
                                         contentView.setScaleY(offsetScale);

                                         // Translate the View, accounting for the scaled width
                                         final float xOffset = drawerView.getWidth() * slideOffset;
                                         final float xOffsetDiff = contentView.getWidth() * diffScaledOffset / 2;
                                         final float xTranslation = xOffset - xOffsetDiff;
                                         contentView.setTranslationX(xTranslation);
                                     }

                                     @Override
                                     public void onDrawerClosed(View drawerView) {
                                         //labelView.setVisibility(View.GONE);
                                     }
                                 }
        );
        //TODO make menu item appear in center
        Menu menud = navigationView.getMenu();
        for(int i = 0; i < menud.size(); i++) {
            MenuItem item = menud.getItem(i);
            if(item.getTitle() != null) {
                SpannableString s = new SpannableString(item.getTitle());
                s.setSpan(new AlignmentSpan.Standard(Layout.Alignment.ALIGN_CENTER), 0, s.length(), 0);
                item.setTitle(s);
            }
        }
        drawer.setStatusBarBackgroundColor(Color.parseColor("#292118"));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(Color.parseColor("#292118"));
        }

        final ImageView layoutd = findViewById(R.id.imageView7);
        layoutd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawer.openDrawer(GravityCompat.START);
            }
        });
    }

    Handler handler;
    PageSearchListAdapter bookMarkPageAdapter;

    Cursor cursor;
    public void searchPages(final String searchWord)
    {
        new AsyncTask<Void,Void,Void>()
        {
            String query = "";
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                handler = new Handler();
                pbar.setVisibility(VISIBLE);
                bookMarkPageAdapter = new PageSearchListAdapter(getApplicationContext(),searchWord,PageSearchActivity.this);

                recyclerView.setAdapter(bookMarkPageAdapter);
                query = ("SELECT * FROM " + DBHelper.PageEntry.TABLE_NAME + " where " + DBHelper.PageEntry.DETAILS_WEB+" LIKE '%"+searchWord+"%'");
            }

            int totalResultCount;
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            protected Void doInBackground(Void... voids) {
                ArrayList<String> dbList = KutubDBHelperFile.getDatabasesList();
                totalResultCount = 0;
                int flag = 0;
                for(int ij=-1;ij<dbList.size();ij++) {
                    if (ij == -1) {
                        PageDBHelper pageDBHelper = new PageDBHelper(getApplicationContext());
                        SQLiteDatabase sqLiteDatabase = pageDBHelper.getReadableDatabase();
                        cursor = sqLiteDatabase.rawQuery(query, null);

                        totalResultCount += cursor.getCount();
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                resultCount.setText("نتائج: " +totalResultCount);
                            }
                        });

                        int firstShowItem = 30;
                        if(cursor.getCount()<30){
                            firstShowItem = cursor.getCount();
                        }
                        for(int i = 0; i < firstShowItem; i++) {
                            final PageDataModel pageDataModel = new PageDataModel();
                            cursor.moveToNext();
                            pageDataModel.setPage_id(cursor.getInt(cursor.getColumnIndex(DBHelper.PageEntry.ID)));
                            pageDataModel.setPage_no(cursor.getInt(cursor.getColumnIndex(DBHelper.PageEntry.PAGE_NO)));
                            pageDataModel.setPageBaabId(cursor.getInt(cursor.getColumnIndex(DBHelper.PageEntry.BAAB_ID)));
                            pageDataModel.setBookId(getBookId(pageDataModel.getPageBaabId()));
                            pageDataModel.setBookName(getBookNameFromBaabId(pageDataModel.getBookId()));
                            pageDataModel.setCursorIndex(0);
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    bookMarkPageAdapter.addPage(pageDataModel);
                                    bookMarkPageAdapter.notifyDataSetChanged();
                                    pbar.setVisibility(View.INVISIBLE);
                                }
                            });
                        }
                       // cursor.close();
                    }
//                    else {
//                        KutubDBHelperFile pageDBHelper = new KutubDBHelperFile(getApplicationContext(),dbList.get(ij));
//                        SQLiteDatabase sqLiteDatabase = pageDBHelper.getWritableDatabase();
//                        Cursor cursor = sqLiteDatabase.rawQuery(query, null);
//                        totalResultCount+=cursor.getCount();
//                        handler.post(new Runnable() {
//                            @Override
//                            public void run() {
//                                resultCount.setText("نتائج: " +totalResultCount);
//                            }
//                        });
//                        Log.d("trySearch", cursor.getCount() + "");
//                        for (int i = 0; i < cursor.getCount(); i++) {
//                            final PageDataModel pageDataModel = new PageDataModel();
//                            cursor.moveToNext();
//                            pageDataModel.setPage_id(cursor.getInt(cursor.getColumnIndex(DBHelper.PageEntry.ID)));
//                            pageDataModel.setPage_no(cursor.getInt(cursor.getColumnIndex(DBHelper.PageEntry.PAGE_NO)));
//                            pageDataModel.setPageBaabId(cursor.getInt(cursor.getColumnIndex(DBHelper.PageEntry.BAAB_ID)));
//                            pageDataModel.setCursorIndex(ij+1);
//                            pageDataModel.setBookId(getBookId(pageDataModel.getPageBaabId()));
//                            pageDataModel.setBookName(getBookNameFromBaabId(pageDataModel.getBookId()));
//*                            handler.post(new Runnable() {
//                                @Override
//                                public void run() {
//                                    bookMarkPageAdapter.addPage(pageDataModel);
//                                    bookMarkPageAdapter.notifyDataSetChanged();
//                                }
//                            });
//
//                        }
//                        cursor.close();
//                    }

//                    handler.post(new Runnable() {
//                        @Override
//                        public void run() {
//                            if(totalResultCount<0){
//                                pbar.setVisibility(View.GONE);
//                            }
//                            resultCount.setText("نتائج: " +
//                                    ""+totalResultCount);
//                        }
//                    });
                }
                return null;
            }


            @Override
            protected void onPostExecute(Void aVoid){

                    pbar.setVisibility(View.GONE);

                super.onPostExecute(aVoid);
            }
        }.execute();
    }


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
                BaabDBHelper baabDBHelper = new BaabDBHelper(getApplicationContext());
                SQLiteDatabase sqlLiteDatabase = baabDBHelper.getReadableDatabase();
                Cursor cursor = sqlLiteDatabase.rawQuery("SELECT "+DBHelper.BaabEntry.BOOK_ID+" from "+DBHelper.BaabEntry.TABLE_NAME+" where "+DBHelper.BaabEntry.ID+" = "+baabId,null);
                Log.d("tryFea","SELECT "+DBHelper.BaabEntry.BOOK_ID+" from "+DBHelper.BaabEntry.TABLE_NAME+" where "+DBHelper.BaabEntry.ID+" = "+baabId);
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
                KutubDBHelperFile kutubDBHelperFile = new KutubDBHelperFile(getApplicationContext(), dbList.get(ij));
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
                KitabDBHelper kitabDBHelper = new KitabDBHelper(getApplicationContext());
                SQLiteDatabase sqLiteDatabase = kitabDBHelper.getReadableDatabase();
                Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM "+DBHelper.KitabEntry.TABLE_NAME+" where "+DBHelper.KitabEntry.ID+" = "+bookId, null);
                for (int i = 0; i < cursor.getCount(); i++) {
                    cursor.moveToNext();
                    kitabDataModel.setBookName(cursor.getString(cursor.getColumnIndex(DBHelper.KitabEntry.NAME_URDU)));
                }
                cursor.close();
            } else {
                KutubDBHelperFile kutubDBHelperFile = new KutubDBHelperFile(getApplicationContext(), dbList.get(ij));
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

    public void backBtn(View v)
    {
        onBackPressed();
    }
}
