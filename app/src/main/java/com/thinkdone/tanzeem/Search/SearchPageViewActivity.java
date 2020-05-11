package com.thinkdone.tanzeem.Search;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.animation.Animator;
import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.thinkdone.tanzeem.Constants;
import com.thinkdone.tanzeem.DB.BaabDBHelper;
import com.thinkdone.tanzeem.DB.BookmarkDBHelper;
import com.thinkdone.tanzeem.DB.DBHelper;
import com.thinkdone.tanzeem.DB.KitabDBHelper;
import com.thinkdone.tanzeem.DB.PageDBHelper;
import com.thinkdone.tanzeem.DBFiles.KutubDBHelperFile;
import com.thinkdone.tanzeem.DataModels.PageDataModel;
import com.thinkdone.tanzeem.OnSwipeTouchListener;
import com.thinkdone.tanzeem.Pages.PageViewActivity;
import com.thinkdone.tanzeem.R;

import java.util.ArrayList;

import static android.view.View.GONE;

public class SearchPageViewActivity extends AppCompatActivity {

    PageDataModel pageDataModel;
    ArrayList<String> babListName;
    ArrayList<Integer> babListId;
    ArrayList<Integer> pageNos;
    Spinner spinner;
    Spinner pageSpinner;


    boolean babFirstFlag = true;
    boolean pageFirstFlag = true;
    ImageView fav;
    BookmarkDBHelper bookmarkDBHelper;
    boolean isSpecialPageLoad = false;
    int specialPageNo = 1;
    int specialBabPosition = -1;
    //  SeekBar seekbar;
    WebView pageWebView;
    TextView totalPagesTV;

    ArrayList<String> kitabDataModels= new ArrayList<>();
    ArrayList<String> baabsDataModels = new ArrayList<>();
    ArrayList<Integer> kitabDataModelsID= new ArrayList<>();
    ArrayList<Integer> baabsDataModelsID = new ArrayList<>();
    int[] categoriesId = {-1,11,12,13,14,15,16,17,18,19,20};

    ArrayAdapter<String> categorySpinnerAdapter;
    ArrayAdapter<String> booksSpinnerAdapter;
    ArrayAdapter<String> babSpinnerAdapter;
    Dialog dialog;

    //TODO dialgouelayouts
    ConstraintLayout dialogueLayout;
    Spinner categorySpinner;
    Spinner babSpinner;
    Spinner booksSpinner;
    TextView bookname;
    TextView babNameTv;

    int currentlySelectedBab = 0;
    int currentlySelectedPage = 0;
    CardView pageViewLayout;
    int newBabPosition = 0;

    boolean isSearch = false;
    String searchWord;
    Cursor universalCursor;
    int currentlySelectedPosition;
    int currentCursorPosition = 0;
    ImageView fontChnageBtn;
    SharedPreferences pref;
    WebSettings webSettings;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pref = getSharedPreferences("com.thinkdone.tanzeem.Pages",MODE_PRIVATE);
        setContentView(R.layout.activity_search_page_view);
        specialPageNo = getIntent().getExtras().getInt(DBHelper.PageEntry.PAGE_NO);
        if(specialPageNo != -1){
            isSpecialPageLoad = true;
            specialBabPosition = getIntent().getExtras().getInt(DBHelper.BaabEntry.ID);
        }

        pageViewLayout = findViewById(R.id.cardView);
        pageWebView = findViewById( R.id.web);
        babNameTv = findViewById(R.id.textView16);
        bookname = findViewById(R.id.textView11);
        webSettings = pageWebView.getSettings();
        if(pref.getFloat(Constants.FONT,-1) != -1) {
            webSettings.setTextZoom((int) pref.getFloat(Constants.FONT,webSettings.getDefaultFontSize()));
            bookname.setTextSize(((int) pref.getFloat(Constants.FONT,webSettings.getDefaultFontSize()) + 150)/10);
            babNameTv.setTextSize(((int) pref.getFloat(Constants.FONT,webSettings.getDefaultFontSize()) + 100)/10);
        }

        //TODO search code
        isSearch = getIntent().getExtras().getBoolean(Constants.IS_SEARCH);
        if(isSearch){
            searchWord = getIntent().getExtras().getString(Constants.SEARCH);
        }
        currentlySelectedPosition = getIntent().getExtras().getInt("position");
        currentCursorPosition = getIntent().getExtras().getInt("cursorIndex");
        totalPagesTV = findViewById(R.id.textView10);
        fetchSearhResultCursors(searchWord);



        bookmarkDBHelper = BookmarkDBHelper.getInstance(getApplicationContext());
        ImageView copy = findViewById(R.id.imageView);
        ImageView share = findViewById(R.id.imageView3);
        fav = findViewById(R.id.imageView4);
        spinner = findViewById(R.id.spinner2);
        pageSpinner = findViewById(R.id.spinner3);
        babListName = new ArrayList<>();
        babListId = new ArrayList<>();
        pageNos = new ArrayList<>();

        pageDataModel = new PageDataModel();
        pageDataModel.setBookId(getIntent().getIntExtra(Constants.BOOK,-1));
        pageDataModel.setBookName(getIntent().getExtras().getString(Constants.BOOKNAME));

        bookname.setText(getIntent().getExtras().getString(Constants.BOOKNAME));


        TextView title = findViewById(R.id.textView);
        title.setText(getIntent().getExtras().getString(Constants.BOOKNAME));

        copy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Toast.makeText(PageViewActivity.this, "Copied", Toast.LENGTH_SHORT).show();
                if(pageDataModel.getPageDetail() != null) {
                    String str = "";
                    str += pageDataModel.getPageTitleUrdu()+ "\n" +pageDataModel.getBaabName()  + "\n" + pageDataModel.getPage_no()+ "\n";
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {

                        str += Html.fromHtml(pageDataModel.getPageDetailWeb(), Html.FROM_HTML_MODE_COMPACT);
                    } else {
                        str += Html.fromHtml(pageDataModel.getPageDetailWeb());
                    }
                    ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                    ClipData clip = ClipData.newPlainText("copy", str);
                    clipboard.setPrimaryClip(clip);
                    Toast.makeText(SearchPageViewActivity.this, "Copied", Toast.LENGTH_SHORT).show();
                    //Log.d("tryCopyShare","https://kitabosunnat.com/kutub-library/" + pageDataModel.getBookNameEnglish());
                }
            }
        });

        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(pageDataModel.getPageDetail() != null) {
                    String str = "";
                    str += pageDataModel.getPageTitleUrdu()+ "\n" + pageDataModel.getBaabName() + "\n" + pageDataModel.getPage_no()+ "\n";
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
                    {
                        str += Html.fromHtml(pageDataModel.getPageDetailWeb(), Html.FROM_HTML_MODE_COMPACT);
                    } else {
                        str += Html.fromHtml(pageDataModel.getPageDetailWeb());
                    }
                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_SEND);
                    intent.putExtra(Intent.EXTRA_TEXT, str);
                    intent.setType("text/plain");
                    startActivity(intent);
                    Toast.makeText(SearchPageViewActivity.this, "Share", Toast.LENGTH_SHORT).show();
                }
            }
        });

        fav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(pageDataModel.getPageDetail() != null) {

                    if (!bookmarkDBHelper.isPageFavourite(pageDataModel.getPage_id())) {
                        //Log.d("tryFav",bookDataModel.getBookNameEnglish());
                        bookmarkDBHelper.addPage(pageDataModel.getPage_id(), pageDataModel.getPage_no(), pageDataModel.getBookName(),pageDataModel.getBookId(),pageDataModel.getPageBaabId(),pageDataModel.getBookName());
                        Toast.makeText(SearchPageViewActivity.this, "Added to favourites "+pageDataModel.getPageBaabId(), Toast.LENGTH_SHORT).show();
                        fav.setImageResource(R.drawable.ic_star_black_24dp);
                    } else {
                        fav.setImageResource(R.drawable.ic_star_border_white_24dp);
                        bookmarkDBHelper.deletePage(pageDataModel.getPage_id() );
                        Toast.makeText(SearchPageViewActivity.this, "Removed from favourites", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(SearchPageViewActivity.this, "Removed from favourites", Toast.LENGTH_SHORT).show();
                }
            }
        });

        //TODO setspinner for pageview
        if(total>0)
        {
            String[] arr = new String[total];
            for(int i = 1;i<=total;i++){
                arr[i-1] = i+"";
            }
            ArrayAdapter<String> spinerAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item,arr);
            pageSpinner.setAdapter(spinerAdapter);
            pageSpinner.setSelection(currentlySelectedPosition);

        }

       // loadBabs();
//        totalPagesTV.setText(getIntent().getExtras().getInt("pagesSize"));


//        //TODO change bab spinner code
//        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                if(babFirstFlag)
//                {
//                    babFirstFlag = false;/
//                }else{
//                    loadPage(1,babListId.get(position));
//                    setUpPageNoSpinnerData(position);
//                }
//            }

//
//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {
//
//            }
//        });
//
//
//
        //TODO change bab spinner code
        pageSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(pageFirstFlag)
                {
                    pageFirstFlag = false;
                }else{
                    currentlySelectedPosition = position;
                    universalCursors.get(currentCursorPosition).moveToPosition(currentlySelectedPosition);
                    Log.d("tryThere","pageSpinnerAdapter");
                    loadPage(universalCursors.get(currentCursorPosition).getInt(universalCursors.get(currentCursorPosition).getColumnIndexOrThrow(DBHelper.PageEntry.PAGE_NO)), universalCursors.get(0).getInt(universalCursors.get(0).getColumnIndexOrThrow(DBHelper.PageEntry.BAAB_ID)));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        ImageView leftSliderBtn = findViewById(R.id.sliderrightbtn);
        ImageView rightSliderBtn = findViewById(R.id.sliderleftbtn);
        leftSliderBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(currentlySelectedPosition-1>=0) {
                    currentlySelectedPosition--;
                    universalCursors.get(currentCursorPosition).moveToPosition(currentlySelectedPosition);
                    loadPage(universalCursors.get(currentCursorPosition).getInt(universalCursors.get(currentCursorPosition).getColumnIndexOrThrow(DBHelper.PageEntry.PAGE_NO)), universalCursors.get(0).getInt(universalCursors.get(0).getColumnIndexOrThrow(DBHelper.PageEntry.BAAB_ID)));
                }else if(currentlySelectedPosition-1<0 && currentCursorPosition-1>=0){
                    if(universalCursors.get(currentCursorPosition--).getCount()>0) {
                        currentCursorPosition--;
                        currentlySelectedPosition = universalCursors.get(currentCursorPosition).getCount()-1;
                        universalCursors.get(currentCursorPosition).moveToPosition(currentlySelectedPosition);
                        loadPage(universalCursors.get(currentCursorPosition).getInt(universalCursors.get(currentCursorPosition).getColumnIndexOrThrow(DBHelper.PageEntry.PAGE_NO)), universalCursors.get(0).getInt(universalCursors.get(0).getColumnIndexOrThrow(DBHelper.PageEntry.BAAB_ID)));
                    }
                }
                pageFirstFlag = true;
                pageSpinner.setSelection(currentlySelectedPosition);
            }
        });
        rightSliderBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(currentlySelectedPosition+1<universalCursors.get(currentCursorPosition).getCount()) {
                    currentlySelectedPosition++;
                    universalCursors.get(currentCursorPosition).moveToPosition(currentlySelectedPosition);
                    loadPage(universalCursors.get(currentCursorPosition).getInt(universalCursors.get(currentCursorPosition).getColumnIndexOrThrow(DBHelper.PageEntry.PAGE_NO)), universalCursors.get(0).getInt(universalCursors.get(0).getColumnIndexOrThrow(DBHelper.PageEntry.BAAB_ID)));
                }else if(currentlySelectedPosition+1>=universalCursors.get(currentCursorPosition).getCount() && currentCursorPosition+1<universalCursors.size()){
                    if(universalCursors.get(currentCursorPosition++).getCount()>0) {
                        currentCursorPosition++;
                        currentlySelectedPosition = 0;
                        universalCursors.get(currentCursorPosition).moveToPosition(currentlySelectedPosition);
                        loadPage(universalCursors.get(currentCursorPosition).getInt(universalCursors.get(currentCursorPosition).getColumnIndexOrThrow(DBHelper.PageEntry.PAGE_NO)), universalCursors.get(0).getInt(universalCursors.get(0).getColumnIndexOrThrow(DBHelper.PageEntry.BAAB_ID)));
                    }
                }
                pageFirstFlag = true;
                pageSpinner.setSelection(currentlySelectedPosition);
            }
        });


        //TODO Book category bab dialogue
        dialogueLayout = findViewById(R.id.dialogue_layout);
        categorySpinner  = findViewById(R.id.spinner6);
        booksSpinner  = findViewById(R.id.spinner5);
        babSpinner  = findViewById(R.id.spinner4);
        Button save = findViewById(R.id.button);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                YoYo.with(Techniques.SlideOutLeft).duration(700).onEnd(new YoYo.AnimatorCallback() {
                    @Override
                    public void call(Animator animator) {
                        dialogueLayout.setVisibility(GONE);
                    }
                }).playOn(dialogueLayout);

                changeBookBaabCategory(kitabDataModelsID.get(booksSpinner.getSelectedItemPosition()),kitabDataModels.get(booksSpinner.getSelectedItemPosition()),
                        baabsDataModelsID.get(babSpinner.getSelectedItemPosition()),baabsDataModels.get(babSpinner.getSelectedItemPosition()),babSpinner.getSelectedItemPosition());

            }
        });
//
        //TODO babs spinner
        babSpinnerAdapter = new ArrayAdapter<String>(getApplicationContext(),R.layout.spinner_item_layout,baabsDataModels);
        babSpinner.setAdapter(babSpinnerAdapter);
        babSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                newBabPosition = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
//
//
//
//
        //TODO category spinner

        booksSpinnerAdapter = new ArrayAdapter<String>(getApplicationContext(),R.layout.spinner_item_layout,kitabDataModels);
        booksSpinner.setAdapter(booksSpinnerAdapter);
        booksSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                newBabPosition = 0;
                // Toast.makeText(PageViewActivity.this, "in kutub", Toast.LENGTH_SHORT).show();
                loadBabsOfKutub(kitabDataModelsID.get(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
//
//
        //TODO category spinner

        categorySpinnerAdapter = new ArrayAdapter<String>(getApplicationContext(),R.layout.spinner_item_layout,getResources().getStringArray(R.array.categories_names));
        categorySpinner.setAdapter(categorySpinnerAdapter);
        categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                newBabPosition  = 0;
                String query = null;
                if(position == 0){
                    query = "Select * from "+ DBHelper.KitabEntry.TABLE_NAME;
                }else {
                    query = "Select * from " + DBHelper.KitabEntry.TABLE_NAME + " where " + DBHelper.KitabEntry.CATEGORY_ID + " = " + categoriesId[position];
                }
                Log.d("tryDialogue",query);
                loadBooksFromDB(query);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(Color.parseColor("#292118"));
        }
        ImageView choser = findViewById(R.id.imageView2);
        choser.setVisibility(GONE);
        choser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(dialogueLayout.getVisibility() == View.VISIBLE){
                    YoYo.with(Techniques.SlideOutLeft).duration(700).onEnd(new YoYo.AnimatorCallback() {
                        @Override
                        public void call(Animator animator) {
                            dialogueLayout.setVisibility(GONE);
                        }
                    }).playOn(dialogueLayout);

                }else{

                    int catId = fetchCategory(pageDataModel.getBookId());
                    for(int i=0;i<categoriesId.length;i++){
                        if(categoriesId[i] == catId){
                            categorySpinner.setSelection(i);
                            break;
                        }

                    }
                    for(int i=0;i<kitabDataModelsID.size();i++){
                        if(pageDataModel.getBookId() == kitabDataModelsID.get(i)){
                            booksSpinner.setSelection(i);
                            break;
                        }
                    }
                    Log.d("tryDItem",baabsDataModelsID.size()+"");
                    for(int i=0;i<baabsDataModelsID.size();i++){
                        Log.d("tryDItem",pageDataModel.getPageBaabId()+"   "+ baabsDataModelsID.get(i));
                        if(pageDataModel.getPageBaabId() == baabsDataModelsID.get(i)){
                            babSpinner.setSelection(i);
                            break;
                        }
                    }
                    YoYo.with(Techniques.SlideInLeft).duration(700).playOn(dialogueLayout);
                    //babSpinner.setSelection(currentlySelectedBab);
                    dialogueLayout.setVisibility(View.VISIBLE);


                }
            }
        });


//
        pageWebView.setOnTouchListener(new OnSwipeTouchListener(SearchPageViewActivity.this) {
            public void onSwipeTop() {
                //Toast.makeText(MyActivity.this, "top", Toast.LENGTH_SHORT).show();
            }
            public void onSwipeRight() {
                if(currentlySelectedPosition+1<universalCursors.get(currentCursorPosition).getCount()) {
                    currentlySelectedPosition++;
                    universalCursors.get(currentCursorPosition).moveToPosition(currentlySelectedPosition);
                    loadPage(universalCursors.get(currentCursorPosition).getInt(universalCursors.get(currentCursorPosition).getColumnIndexOrThrow(DBHelper.PageEntry.PAGE_NO)), universalCursors.get(0).getInt(universalCursors.get(0).getColumnIndexOrThrow(DBHelper.PageEntry.BAAB_ID)));
                }else if(currentlySelectedPosition+1>=universalCursors.get(currentCursorPosition).getCount() && currentCursorPosition+1<universalCursors.size()){
                    if(universalCursors.get(currentCursorPosition++).getCount()>0) {
                        currentCursorPosition++;
                        currentlySelectedPosition = 0;
                        universalCursors.get(currentCursorPosition).moveToPosition(currentlySelectedPosition);
                        loadPage(universalCursors.get(currentCursorPosition).getInt(universalCursors.get(currentCursorPosition).getColumnIndexOrThrow(DBHelper.PageEntry.PAGE_NO)), universalCursors.get(0).getInt(universalCursors.get(0).getColumnIndexOrThrow(DBHelper.PageEntry.BAAB_ID)));
                    }
                }

                pageFirstFlag = true;
                pageSpinner.setSelection(currentlySelectedPosition);
            }
            public void onSwipeLeft() {
                if(currentlySelectedPosition-1>=0) {
                    currentlySelectedPosition--;
                    universalCursors.get(currentCursorPosition).moveToPosition(currentlySelectedPosition);
                    loadPage(universalCursors.get(currentCursorPosition).getInt(universalCursors.get(currentCursorPosition).getColumnIndexOrThrow(DBHelper.PageEntry.PAGE_NO)), universalCursors.get(0).getInt(universalCursors.get(0).getColumnIndexOrThrow(DBHelper.PageEntry.BAAB_ID)));
                }else if(currentlySelectedPosition-1<0 && currentCursorPosition-1>=0){
                    if(universalCursors.get(currentCursorPosition--).getCount()>0) {
                        currentCursorPosition--;
                        currentlySelectedPosition = universalCursors.get(currentCursorPosition).getCount()-1;
                        universalCursors.get(currentCursorPosition).moveToPosition(currentlySelectedPosition);
                        loadPage(universalCursors.get(currentCursorPosition).getInt(universalCursors.get(currentCursorPosition).getColumnIndexOrThrow(DBHelper.PageEntry.PAGE_NO)), universalCursors.get(0).getInt(universalCursors.get(0).getColumnIndexOrThrow(DBHelper.PageEntry.BAAB_ID)));
                    }
                }
                pageFirstFlag = true;
                pageSpinner.setSelection(currentlySelectedPosition);


            }
            public void onSwipeBottom() {
                //Toast.makeText(MyActivity.this, "bottom", Toast.LENGTH_SHORT).show();
            }

        });
//

//
        //TODO table of contents click  handling
                pageWebView.setWebViewClient(new WebViewClient()
        {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url)
            {
                return true;
            }
        });
        loadPage(getIntent().getExtras().getInt(DBHelper.PageEntry.PAGE_NO),getIntent().getExtras().getInt(DBHelper.BaabEntry.ID));

    }

    public void changeBookBaabCategory(int bookId,String booKname,int baabId,String baabName,int selectedBabPosition){

        pageFirstFlag = true;
        pageDataModel.setBookId(bookId);
        pageDataModel.setBookName(booKname);
        bookname.setText(booKname);
        babNameTv.setText(baabsDataModels.get(babSpinner.getSelectedItemPosition()));
        currentlySelectedBab = babSpinner.getSelectedItemPosition();
        loadBabsFromBooks(babSpinner.getSelectedItemPosition());
        //totalPagesTV.setText(getTotalBookPages(pageDataModel.getBookId())+"");
    }


    public void loadBabsFromBooks(final int position)
    {
        new AsyncTask<Void,Void,Void>()
        {
            String query = "";
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                query = "SELECT * from "+DBHelper.BaabEntry.TABLE_NAME+" WHERE "+DBHelper.BaabEntry.BOOK_ID+" = "+pageDataModel.getBookId();
                Log.d("tryQuery",query);
                babListId.clear();
                babListName.clear();
            }
            @Override
            protected Void doInBackground(Void... voids) {
                ArrayList<String> dbList = KutubDBHelperFile.getDatabasesList();
                for(int ij=-1;ij<dbList.size();ij++) {
                    if (ij == -1) {
                        BaabDBHelper baabDBHelperr = new BaabDBHelper(getApplicationContext());
                        SQLiteDatabase sqLiteDatabase = baabDBHelperr.getReadableDatabase();
                        Cursor cursor = sqLiteDatabase.rawQuery(query, null);
                        for (int i = 0; i < cursor.getCount(); i++) {
                            cursor.moveToNext();
                            babListId.add(cursor.getInt(cursor.getColumnIndex(DBHelper.BaabEntry.ID)));
                            babListName.add(cursor.getString(cursor.getColumnIndex(DBHelper.BaabEntry.NAME)));
                        }
                        cursor.close();
                    }else{
                        KutubDBHelperFile baabDBHelperr = new KutubDBHelperFile(getApplicationContext(),dbList.get(ij));
                        SQLiteDatabase sqLiteDatabase = baabDBHelperr.getWritableDatabase();
                        Cursor cursor = sqLiteDatabase.rawQuery(query, null);
                        for (int i = 0; i < cursor.getCount(); i++) {
                            cursor.moveToNext();
                            babListId.add(cursor.getInt(cursor.getColumnIndex(DBHelper.BaabEntry.ID)));
                            babListName.add(cursor.getString(cursor.getColumnIndex(DBHelper.BaabEntry.NAME)));
                        }
                        cursor.close();
                    }
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                setUpPageNoSpinnerData(position);
                Log.d("tryThere","loadBabsFromBooks");
                loadPage(totalPages.get(0), babListId.get(position));
            }
        }.execute();
    }

    public void loadBabs()
    {
        new AsyncTask<Void,Void,Void>()
        {
            String query = "";
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                query = "SELECT * from "+DBHelper.BaabEntry.TABLE_NAME+" WHERE "+DBHelper.BaabEntry.BOOK_ID+" = "+pageDataModel.getBookId();
                Log.d("tryQuery",query);
            }
            @Override
            protected Void doInBackground(Void... voids) {
                ArrayList<String> dbList = KutubDBHelperFile.getDatabasesList();
                for(int ij=-1;ij<dbList.size();ij++) {
                    if (ij == -1) {
                        BaabDBHelper baabDBHelperr = new BaabDBHelper(getApplicationContext());
                        SQLiteDatabase sqLiteDatabase = baabDBHelperr.getReadableDatabase();
                        Cursor cursor = sqLiteDatabase.rawQuery(query, null);
                        for (int i = 0; i < cursor.getCount(); i++) {
                            cursor.moveToNext();
                            babListId.add(cursor.getInt(cursor.getColumnIndex(DBHelper.BaabEntry.ID)));
                            babListName.add(cursor.getString(cursor.getColumnIndex(DBHelper.BaabEntry.NAME)));
                        }
                        cursor.close();
                    }else{
                        KutubDBHelperFile baabDBHelperr = new KutubDBHelperFile(getApplicationContext(),dbList.get(ij));
                        SQLiteDatabase sqLiteDatabase = baabDBHelperr.getWritableDatabase();
                        Cursor cursor = sqLiteDatabase.rawQuery(query, null);
                        for (int i = 0; i < cursor.getCount(); i++) {
                            cursor.moveToNext();
                            babListId.add(cursor.getInt(cursor.getColumnIndex(DBHelper.BaabEntry.ID)));
                            babListName.add(cursor.getString(cursor.getColumnIndex(DBHelper.BaabEntry.NAME)));
                        }
                        cursor.close();
                    }
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getApplicationContext(),android.R.layout.simple_spinner_dropdown_item,babListName);
                spinner.setAdapter(arrayAdapter);

                if(!isSpecialPageLoad) {
                    setUpPageNoSpinnerData(0);
                    loadPage(totalPages.get(0), babListId.get(0));
                }else{
                    // Toast.makeText(PageViewActivity.this, "there", Toast.LENGTH_SHORT).show();
                    int specialPosition = 0;
                    for(int i =0;i<babListId.size();i++){
                        if(babListId.get(i) == specialBabPosition) {
                            Log.d("trySpecialPosition",specialBabPosition+"");
                            babSpinner.setSelection(i);
                            currentlySelectedBab = i;
                            specialPosition = i;
                            break;
                        }
                    }
                    setUpPageNoSpinnerData(specialPosition);
                    for(int i =0;i<totalPages.size();i++){
                        if(totalPages.get(i) == specialPageNo) {
                            pageSpinner.setSelection(i);
                            currentlySelectedPage = i;
                            break;
                        }
                    }
                    loadPage(specialPageNo,babListId.get(specialPosition));
                    isSpecialPageLoad = false;
                }
            }
        }.execute();
    }


    public String fetchbabName(int babId)
    {

        ArrayList<String> dbList = KutubDBHelperFile.getDatabasesList();
        String query = "";
        query = "SELECT * from "+DBHelper.BaabEntry.TABLE_NAME+" WHERE "+DBHelper.BaabEntry.ID+" = "+babId;
        Log.d("tryQuery",query);
        String res = null;
        for(int ij=-1;ij<dbList.size();ij++) {
            if (ij == -1) {
                BaabDBHelper baabDBHelperr = new BaabDBHelper(getApplicationContext());
                SQLiteDatabase sqLiteDatabase = baabDBHelperr.getReadableDatabase();
                Cursor cursor = sqLiteDatabase.rawQuery(query, null);
                if(cursor.getCount()>0){
                    cursor.moveToNext();
                    res = cursor.getString(cursor.getColumnIndex(DBHelper.BaabEntry.NAME));
                    pageDataModel.setBookId(cursor.getColumnIndex(DBHelper.BaabEntry.BOOK_ID));
                }

                cursor.close();
            }else{
                KutubDBHelperFile baabDBHelperr = new KutubDBHelperFile(getApplicationContext(),dbList.get(ij));
                SQLiteDatabase sqLiteDatabase = baabDBHelperr.getWritableDatabase();
                Cursor cursor = sqLiteDatabase.rawQuery(query, null);
                if(cursor.getCount()>0){
                    cursor.moveToNext();
                    res = cursor.getString(cursor.getColumnIndex(DBHelper.BaabEntry.NAME));
                    pageDataModel.setBookId(cursor.getColumnIndex(DBHelper.BaabEntry.BOOK_ID));
                }
                cursor.close();
            }
        }

        return res;

    }

    public String fetchKitabName(int kitabId)
    {

        ArrayList<String> dbList = KutubDBHelperFile.getDatabasesList();
        String query = "";
        query = "SELECT * from "+DBHelper.KitabEntry.TABLE_NAME+" WHERE "+DBHelper.KitabEntry.ID+" = "+kitabId;
        Log.d("tryQuery",query);
        String res = null;
        for(int ij=-1;ij<dbList.size();ij++) {
            if (ij == -1) {
                KitabDBHelper kitabEntry = new KitabDBHelper(getApplicationContext());
                SQLiteDatabase sqLiteDatabase = kitabEntry.getReadableDatabase();
                Cursor cursor = sqLiteDatabase.rawQuery(query, null);
                if(cursor.getCount()>0){
                    cursor.moveToNext();
                    res = cursor.getString(cursor.getColumnIndex(DBHelper.KitabEntry.NAME));
                }
                cursor.close();
            }else{
                KutubDBHelperFile baabDBHelperr = new KutubDBHelperFile(getApplicationContext(),dbList.get(ij));
                SQLiteDatabase sqLiteDatabase = baabDBHelperr.getWritableDatabase();
                Cursor cursor = sqLiteDatabase.rawQuery(query, null);
                if(cursor.getCount()>0){
                    cursor.moveToNext();
                    res = cursor.getString(cursor.getColumnIndex(DBHelper.KitabEntry.NAME));
                }
                cursor.close();
            }
        }
        return res;

    }



    ArrayList<Integer> totalPages;
    public void setUpPageNoSpinnerData(int pos)
    {
        pageNos.clear();
        totalPages = no_of_pages(babListId.get(pos));
        for(int i=0;i<totalPages.size();i++) {
            pageNos.add(totalPages.get(i));
        }
        ArrayAdapter<Integer> arrayAdapterPage = new ArrayAdapter<Integer>(getApplicationContext(), R.layout.spinner_item_layout, pageNos);
        pageSpinner.setAdapter(arrayAdapterPage);
    }
    public void loadPage(final int pageNo, final int baabId)
    {
        new AsyncTask<Void,Void,Void>()
        {
            String query = "";
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
              //  babNameTv.setText(babListName.get(currentlySelectedBab));
                query = "SELECT * from "+DBHelper.PageEntry.TABLE_NAME+" WHERE "+DBHelper.PageEntry.BAAB_ID+" = "+baabId+" AND "+ DBHelper.PageEntry.PAGE_NO+" = "+pageNo;
                Log.d("tryQuery",query);
            }

            @Override
            protected Void doInBackground(Void... voids) {
                ArrayList<String> dbList = KutubDBHelperFile.getDatabasesList();
                for(int ij=-1;ij<dbList.size();ij++) {
                    if (ij == -1) {

                        PageDBHelper pageDBHelper = new PageDBHelper(getApplicationContext());
                        SQLiteDatabase sqLiteDatabase = pageDBHelper.getReadableDatabase();
                        Cursor cursor = sqLiteDatabase.rawQuery(query, null);
                        for (int i = 0; i < cursor.getCount(); i++) {
                            cursor.moveToNext();
                            pageDataModel.setPage_id(cursor.getInt(cursor.getColumnIndex(DBHelper.PageEntry.ID)));
                            pageDataModel.setPage_no(cursor.getInt(cursor.getColumnIndex(DBHelper.PageEntry.PAGE_NO)));
                            pageDataModel.setPageTitleUrdu(cursor.getString(cursor.getColumnIndex(DBHelper.PageEntry.NAME_URDU)));
                            pageDataModel.setPageDetail(cursor.getString(cursor.getColumnIndex(DBHelper.PageEntry.DETAILS)));
                            pageDataModel.setPageDetailWeb(cursor.getString(cursor.getColumnIndex(DBHelper.PageEntry.DETAILS_WEB)));
                            pageDataModel.setDescUrdu(cursor.getString(cursor.getColumnIndex(DBHelper.PageEntry.DESC_URDU)));
                            Log.d("tryPageDetails", pageDataModel.getPage_id()+"   "+pageDataModel.getPageDetailWeb());
                        }
                        cursor.close();
                    }else {
                        KutubDBHelperFile baabDBHelperr = new KutubDBHelperFile(getApplicationContext(), dbList.get(ij));
                        SQLiteDatabase sqLiteDatabase = baabDBHelperr.getWritableDatabase();
                        Cursor cursor = sqLiteDatabase.rawQuery(query, null);
                        for (int i = 0; i < cursor.getCount(); i++) {
                            cursor.moveToNext();
                            pageDataModel.setPage_id(cursor.getInt(cursor.getColumnIndex(DBHelper.PageEntry.ID)));
                            pageDataModel.setPage_no(cursor.getInt(cursor.getColumnIndex(DBHelper.PageEntry.PAGE_NO)));
                            pageDataModel.setPageTitleUrdu(cursor.getString(cursor.getColumnIndex(DBHelper.PageEntry.NAME_URDU)));
                            pageDataModel.setPageDetail(cursor.getString(cursor.getColumnIndex(DBHelper.PageEntry.DETAILS)));
                            pageDataModel.setPageDetailWeb(cursor.getString(cursor.getColumnIndex(DBHelper.PageEntry.DETAILS_WEB)));
                            pageDataModel.setDescUrdu(cursor.getString(cursor.getColumnIndex(DBHelper.PageEntry.DESC_URDU)));
                            Log.d("tryPageDetails", pageDataModel.getPageDetail());
                        }
                    }
                }
                pageDataModel.setBabId(baabId);
                pageDataModel.setPageBaabId(baabId);
                fetchPageBackground(baabId);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                Log.d("tryPageID",pageDataModel.getPage_id()+"    "+bookmarkDBHelper.isPageFavourite(pageDataModel.getPage_id()));
                if(bookmarkDBHelper.isPageFavourite(pageDataModel.getPage_id())){
                    fav.setImageResource(R.drawable.ic_star_black_24dp);
                }else{
                    fav.setImageResource(R.drawable.ic_star_border_white_24dp);
                }
//                Typeface font = Typeface.createFromAsset(getAssets(), "fonts/urdu_font.ttf");
//                Typeface font2 = Typeface.createFromAsset(getAssets(), "fonts/arabic_font.ttf");
//                SpannableStringBuilder SS = null;
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//                   SS = new SpannableStringBuilder(Html.fromHtml(pageDataModel.getPageDetailWeb(), Html.FROM_HTML_MODE_COMPACT));
//                   // pageDetailTV.setText(Html.fromHtml(pageDataModel.getPageDetail(), Html.FROM_HTML_MODE_COMPACT));
//                } else {
//                    SS = new SpannableStringBuilder(Html.fromHtml(pageDataModel.getPageDetailWeb()));
//                    //pageDetailTV.setText(Html.fromHtml(pageDataModel.getPageDetail()));
//                }
//              //  SS.setSpan (new CustomTypefaceSpan("", font2), 0, 4, Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
//              //  SS.setSpan (new CustomTypefaceSpan("", font), 4, 11,Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
//                pageDetailTV.setText(SS);

                //TODO css code
                {
                    String str = pageDataModel.getPageDetailWeb();

                        str = pageDataModel.getPageDetailWeb().replaceAll(searchWord, "<span style=\"background-color:yellow;\">" + searchWord + "</span>");
                        Log.d("tryPageText",searchWord+"  "+ str);


//                    Log.d("tryPageText",searchWord+"  "+ str);
//                    if(str.contains("heading-underline-marker")) {
//                        str = pageDataModel.getPageDetailWeb().replaceAll("<span class=\"heading-underline-marker\">", "<br><span class=\"heading-underline-marker\">");
//                        str.
//                    }

                    StringBuilder data = new StringBuilder();
                    data .append("<!DOCTYPE html>\n" +
                            "<html dir=\"rtl\">\n" +
                            "<head>\n" +
                            "<link rel=\"stylesheet\" type=\"text/css\" href=\"simplecss.css\">\n" +
                            "<style>@charset \"utf-8\";\n" +
                            "/*@import url(http://fonts.googleapis.com/earlyaccess/notonastaliqurdudraft.css);*/\n" +
                            "/* CSS Document */\n" +
                            "/*@font-face {\n" +
                            "    font-family: 'Mehr Nastaliq Web';\n" +
                            "    src: url('../fonts/MehrNastaliqWeb.woff2') format('woff2'),\n" +
                            "        url('../fonts/MehrNastaliqWeb.woff') format('woff');\n" +
                            "    font-weight: normal;\n" +
                            "    font-style: normal;\n" +
                            "}*/\n" +
                            "\n" +
                            "@font-face {\n" +
                            "    font-family: Mehr Nastaliq Web;\n" +
                            "    src: url('fonts/Mehr Nastaliq Web.ttf');\n" +
                            "    font-weight: normal;\n" +
                            "    font-style: normal;\n" +
                            "}\n" +
                            "\n" +
                            "body {\n" +
                            "    padding: 0px;\n" +
                            "    margin: 0px;\n" +
                            "    font-family: 'Mehr Nastaliq Web';\n" +
                            "    font-weight: normal;\n" +
                            "    background: rgb(255, 255, 255);\n" +
                            "}\n" +
                            "\n" +
                            "img {\n" +
                            "    outline: none;\n" +
                            "    border: none;\n" +
                            "}\n" +
                            "\n" +
                            "@font-face {\n" +
                            "  font-family: noorehuda;\n" +
                            "  font-weight: normal;\n" +
                            "  src: url(\"fonts/noorehuda.ttf\");\n" +
                            "}\n" +
                            "\n" +
                            "\n" +
                            "/*@font-face\n" +
                            "{\n" +
                            "    font-family: 'noorehira','Times New Roman';\n" +
                            "    src: url('../fonts/noorehira.eot');\n" +
                            "    src: url('../fonts/noorehira.eot?#iefix') format('embedded-opentype'), url('../fonts/noorehira.woff') format('woff'), url('../fonts/noorehira.ttf') format('truetype');\n" +
                            "    font-weight: normal;\n" +
                            "    font-style: normal;\n" +
                            "}\n" +
                            "\n" +
                            "@font-face\n" +
                            "{\n" +
                            "    font-family: 'alvi','Times New Roman';\n" +
                            "    src: url('../fonts/alvi.eot');\n" +
                            "    src: url('../fonts/alvi.eot?#iefix') format('embedded-opentype'), url('../fonts/alvi.woff') format('woff'), url('../fonts/alvi.ttf') format('truetype');\n" +
                            "    font-weight: normal;\n" +
                            "    font-style: normal;\n" +
                            "}*/\n" +
                            "\n" +
                            "\n" +
                            "\n" +
                            "a {\n" +
                            "    text-decoration: none;\n" +
                            "}\n" +
                            "\n" +
                            "    a:hover {\n" +
                            "        text-decoration: none;\n" +
                            "    }\n" +
                            "\n" +
                            "border, outline {\n" +
                            "    border: none;\n" +
                            "}\n" +
                            "\n" +
                            "h1, h2, h3, h4, h5, h6, p {\n" +
                            "    padding: 0px;\n" +
                            "    margin: 0px;\n" +
                            "}\n" +
                            "\n" +
                            ".adminHeaderMenu {\n" +
                            "    /*background:#f9fcee;*/\n" +
                            "    width: 960px;\n" +
                            "    margin: 0px auto;\n" +
                            "    padding-left: 0px;\n" +
                            "    height: 40px;\n" +
                            "}\n" +
                            "\n" +
                            "#header {\n" +
                            "    width: 100%;\n" +
                            "    height: 102px;\n" +
                            "    background: #4d3921;\n" +
                            "    border-top: 2px #b0b0b0 solid;\n" +
                            "}\n" +
                            "\n" +
                            "    #header .container {\n" +
                            "        width: 1170px;\n" +
                            "        height: auto;\n" +
                            "        margin: 0 auto;\n" +
                            "    }\n" +
                            "\n" +
                            "        #header .container .input {\n" +
                            "            float: right;\n" +
                            "            width: 435px;\n" +
                            "            height: 36px;\n" +
                            "            text-align: right;\n" +
                            "            padding: 3px 9px;\n" +
                            "            font-family: 'Mehr Nastaliq Web' !important;\n" +
                            "            font-size: 16px;\n" +
                            "            direction: rtl;\n" +
                            "        }\n" +
                            "\n" +
                            ".searchArea {\n" +
                            "    width: 100%;\n" +
                            "}\n" +
                            "\n" +
                            "#header .container .button {\n" +
                            "    float: right;\n" +
                            "    width: 94px;\n" +
                            "    height: 36px;\n" +
                            "    background: #cf704d;\n" +
                            "    border: none;\n" +
                            "    cursor: pointer;\n" +
                            "    color: #fff;\n" +
                            "    text-align: center;\n" +
                            "    font-size: 16px;\n" +
                            "    font-family: 'Mehr Nastaliq Web' !important;\n" +
                            "}\n" +
                            "\n" +
                            "    #header .container .button:hover {\n" +
                            "        background: #c26948;\n" +
                            "    }\n" +
                            "\n" +
                            "#header .container #nav {\n" +
                            "    border: none;\n" +
                            "    border-radius: 4px;\n" +
                            "    width: 1125px;\n" +
                            "    height: 36px;\n" +
                            "    padding: 0px 2px;\n" +
                            "    /*float: right;*/\n" +
                            "    background: #f8f0ce;\n" +
                            "    margin: 30px 0px 0px 0px;\n" +
                            "}\n" +
                            "\n" +
                            "#header .container #logo {\n" +
                            "    position: absolute;\n" +
                            "    top: 4px;\n" +
                            "    margin: 0px 0px 0px 525px;\n" +
                            "}\n" +
                            "\n" +
                            ".logoAdmin {\n" +
                            "    position: absolute;\n" +
                            "    top: 0px;\n" +
                            "    margin: 0px 0px 0px -10px;\n" +
                            "}\n" +
                            "\n" +
                            "#header .container #nav #menu {\n" +
                            "    float: right;\n" +
                            "    width: 415px;\n" +
                            "    height: 36px;\n" +
                            "    padding: 2px 0px 0px 0px;\n" +
                            "}\n" +
                            "\n" +
                            "    #header .container #nav #menu ul {\n" +
                            "        float: left;\n" +
                            "        width: 100%;\n" +
                            "        height: auto;\n" +
                            "        padding: 0px;\n" +
                            "        margin: 0px;\n" +
                            "    }\n" +
                            "\n" +
                            "        #header .container #nav #menu ul li {\n" +
                            "            float: left;\n" +
                            "            padding: 2px 12px 9px 9px !important;\n" +
                            "            background: url(../images/menu_divider.png) no-repeat right center;\n" +
                            "            list-style: none;\n" +
                            "            margin: 0px 0px 0px 0px;\n" +
                            "            line-height: 19px;\n" +
                            "        }\n" +
                            "\n" +
                            "\n" +
                            "            #header .container #nav #menu ul li a {\n" +
                            "                color: #5E4227;\n" +
                            "                text-shadow: 2px 2px 2px #fff;\n" +
                            "                font-size: 12px;\n" +
                            "                font-weight: bold;\n" +
                            "            }\n" +
                            "\n" +
                            "            #header .container #nav #menu ul li img {\n" +
                            "                margin: 2px 0px 0px 0px;\n" +
                            "                float: left;\n" +
                            "            }\n" +
                            "\n" +
                            "#header .container #nav #search {\n" +
                            "    float: left;\n" +
                            "    width: 535px;\n" +
                            "    height: 36px;\n" +
                            "}\n" +
                            "\n" +
                            "\n" +
                            "\n" +
                            "\n" +
                            "#banner {\n" +
                            "    width: 100%;\n" +
                            "    height: 312px;\n" +
                            "    background: #e7dfba url(../images/banner_bg.jpg) no-repeat center bottom;\n" +
                            "    border-top: 1px #c8af97 solid;\n" +
                            "}\n" +
                            "\n" +
                            "    #banner .container {\n" +
                            "        width: 1170px;\n" +
                            "        height: 408px;\n" +
                            "        margin: 0 auto;\n" +
                            "    }\n" +
                            "\n" +
                            ".MainContainer {\n" +
                            "    width: 1000px;\n" +
                            "    margin: 10px auto 10px auto;\n" +
                            "}\n" +
                            "\n" +
                            ".editScreen {\n" +
                            "    display: inline-block;\n" +
                            "    width: 98%;\n" +
                            "    padding: 1%;\n" +
                            "    border-radius: 5px;\n" +
                            "    background: #fff;\n" +
                            "}\n" +
                            "\n" +
                            ".lblDiv {\n" +
                            "    width: 20%;\n" +
                            "    float: left;\n" +
                            "}\n" +
                            "\n" +
                            ".txtDiv {\n" +
                            "    width: 30%;\n" +
                            "    float: left;\n" +
                            "}\n" +
                            "\n" +
                            "#inside_banner {\n" +
                            "    width: 100%;\n" +
                            "    height: 101px;\n" +
                            "    background: #e7dfba url(../images/inside_top_banner_bg.jpg) no-repeat center top;\n" +
                            "    border-top: 1px #c8af97 solid;\n" +
                            "}\n" +
                            "\n" +
                            "    #inside_banner .container {\n" +
                            "        width: 1170px;\n" +
                            "        height: auto;\n" +
                            "        margin: 0 auto;\n" +
                            "    }\n" +
                            "\n" +
                            "        #inside_banner .container h1 {\n" +
                            "            width: 70%;\n" +
                            "            float: left;\n" +
                            "            font-size: 17px;\n" +
                            "            padding: 40px 0px 0px 0px;\n" +
                            "            text-align: center;\n" +
                            "            color: #634629;\n" +
                            "            text-shadow: 2px 2px 2px #fff;\n" +
                            "        }\n" +
                            "\n" +
                            "        #inside_banner .container .span {\n" +
                            "            font-size: 25px;\n" +
                            "        }\n" +
                            "\n" +
                            "\n" +
                            "        #inside_banner .container img {\n" +
                            "            float: left;\n" +
                            "            margin: 10px 0px 0px 0px;\n" +
                            "        }\n" +
                            "\n" +
                            "\n" +
                            "#banner .container img {\n" +
                            "    float: right;\n" +
                            "    margin: 30px 30px 0px 0px;\n" +
                            "}\n" +
                            "\n" +
                            "#banner .container #banner_search {\n" +
                            "    width: 379px;\n" +
                            "    height: 245px;\n" +
                            "    background: rgba(250, 249, 249, 0.70);\n" +
                            "    border: 0px solid #ebeaea;\n" +
                            "    border-radius: 5px;\n" +
                            "    box-shadow: 0px 0px 5px 1px #000;\n" +
                            "    float: left;\n" +
                            "    margin: 30px 0px 0px 30px;\n" +
                            "    padding: 10px 20px 20px 20px;\n" +
                            "}\n" +
                            "\n" +
                            ".ContainerDiv {\n" +
                            "    width: 359px;\n" +
                            "}\n" +
                            "\n" +
                            "#banner .container #banner_search .right {\n" +
                            "    font-size: 13px;\n" +
                            "    color: #5d4226;\n" +
                            "    margin: 5px 0px;\n" +
                            "    padding: 0px;\n" +
                            "    text-align: right;\n" +
                            "}\n" +
                            "\n" +
                            "#banner .container #banner_search .left {\n" +
                            "    font-size: 15px;\n" +
                            "    color: #5E4227;\n" +
                            "    margin: 8px 0px;\n" +
                            "    border: none;\n" +
                            "    cursor: pointer;\n" +
                            "    padding: 0px 30px 0px 0px;\n" +
                            "    font-family: 'Mehr Nastaliq Web' !important;\n" +
                            "}\n" +
                            "\n" +
                            ".drp {\n" +
                            "    width: 100%;\n" +
                            "    padding: 2px;\n" +
                            "    border: 1px solid #1d818a;\n" +
                            "    overflow: hidden;\n" +
                            "    cursor: pointer;\n" +
                            "    box-shadow: 0px 0px 2px 1px #d6d5d5 inset;\n" +
                            "    direction: rtl;\n" +
                            "    background: #f3f3f3;\n" +
                            "    font-family: 'Mehr Nastaliq Web' !important;\n" +
                            "    font-size: 13px;\n" +
                            "}\n" +
                            "\n" +
                            "    .drp option {\n" +
                            "        overflow: hidden;\n" +
                            "        padding: 2px 5px 2px 5px;\n" +
                            "        border-right: 2px solid #d0d0d0;\n" +
                            "        cursor: pointer;\n" +
                            "        text-align: right;\n" +
                            "        direction: rtl;\n" +
                            "    }\n" +
                            "\n" +
                            "        .drp option:hover {\n" +
                            "            background: #a9d099;\n" +
                            "        }\n" +
                            "\n" +
                            "#banner .container #banner_search .search {\n" +
                            "    float: left;\n" +
                            "    text-align: center;\n" +
                            "    font-size: 14px;\n" +
                            "    color: #fff;\n" +
                            "    width: 115px;\n" +
                            "    height: 39px;\n" +
                            "    background: #cf704d;\n" +
                            "    margin: 4px 0px 5px 0px;\n" +
                            "    border: none;\n" +
                            "    border-radius: 5px;\n" +
                            "    cursor: pointer;\n" +
                            "    vertical-align: middle;\n" +
                            "    /*padding-top: 5px;*/\n" +
                            "}\n" +
                            "\n" +
                            "    #banner .container #banner_search .search:hover {\n" +
                            "        background: #aa5b3f;\n" +
                            "    }\n" +
                            "\n" +
                            ".loading {\n" +
                            "    position: absolute;\n" +
                            "    top: 0px;\n" +
                            "    left: 0px;\n" +
                            "    right: 0px;\n" +
                            "    bottom: 0px;\n" +
                            "    background: #000;\n" +
                            "    opacity: 0.5;\n" +
                            "}\n" +
                            "\n" +
                            "#contentarea {\n" +
                            "    width: 100%;\n" +
                            "    height: auto;\n" +
                            "    background: #f7f5e8;\n" +
                            "    background: -webkit-linear-gradient(#f7f5e8, #fdfdfa); /* For Safari 5.1 to 6.0 */\n" +
                            "    background: -o-linear-gradient(#f7f5e8, #fdfdfa); /* For Opera 11.1 to 12.0 */\n" +
                            "    background: -moz-linear-gradient(#f7f5e8, #fdfdfa); /* For Firefox 3.6 to 15 */\n" +
                            "    background: linear-gradient(#f7f5e8, #fdfdfa); /* Standard syntax */\n" +
                            "    /*overflow: hidden;*/\n" +
                            "}\n" +
                            "\n" +
                            "    #contentarea .container {\n" +
                            "        width: 1170px;\n" +
                            "        height: auto;\n" +
                            "        margin: 0 auto;\n" +
                            "        padding: 30px 0px 30px 0px;\n" +
                            "    }\n" +
                            "\n" +
                            "        #contentarea .container #left {\n" +
                            "            float: left;\n" +
                            "            width: 700px;\n" +
                            "            height: auto;\n" +
                            "            margin: 0px 20px 0px 60px;\n" +
                            "        }\n" +
                            "\n" +
                            "            #contentarea .container #left h1 {\n" +
                            "                width: 100%;\n" +
                            "                float: left;\n" +
                            "                font-size: 20px;\n" +
                            "                text-align: right;\n" +
                            "                color: #5e4227;\n" +
                            "                direction: rtl;\n" +
                            "            }\n" +
                            "\n" +
                            "            #contentarea .container #left h2 {\n" +
                            "                width: 100%;\n" +
                            "                float: left;\n" +
                            "                font-size: 25px;\n" +
                            "                text-align: right;\n" +
                            "                color: #5e4227;\n" +
                            "                margin: 20px 0px 0px 0px;\n" +
                            "                direction: rtl;\n" +
                            "            }\n" +
                            "\n" +
                            "            #contentarea .container #left p {\n" +
                            "                width: 100%;\n" +
                            "                float: left;\n" +
                            "                font-size: 16px;\n" +
                            "                text-align: right;\n" +
                            "                line-height: 30px;\n" +
                            "                color: #5e4227;\n" +
                            "                margin: 0px 0px 5px 0px;\n" +
                            "                direction: rtl;\n" +
                            "                text-align: justify;\n" +
                            "            }\n" +
                            "\n" +
                            "\n" +
                            ".inside_h {\n" +
                            "    width: 43%;\n" +
                            "    height: auto;\n" +
                            "    float: right;\n" +
                            "}\n" +
                            "\n" +
                            ".inside_hp {\n" +
                            "    width: 14%;\n" +
                            "    height: auto;\n" +
                            "    float: right;\n" +
                            "}\n" +
                            "\n" +
                            ".inside_p {\n" +
                            "    width: 100%;\n" +
                            "    height: auto;\n" +
                            "    float: right;\n" +
                            "    padding: 5px 2% 5px 0px;\n" +
                            "    background: #edeadb;\n" +
                            "    background: -webkit-linear-gradient(#edeadb, #e5e3d3); /* For Safari 5.1 to 6.0 */\n" +
                            "    background: -o-linear-gradient(#edeadb, #e5e3d3); /* For Opera 11.1 to 12.0 */\n" +
                            "    background: -moz-linear-gradient(#edeadb, #e5e3d3); /* For Firefox 3.6 to 15 */\n" +
                            "    background: linear-gradient(#edeadb, #e5e3d3); /* Standard syntax */\n" +
                            "}\n" +
                            "\n" +
                            ".shareSection {\n" +
                            "    padding: 3px 15px;\n" +
                            "    border-radius: 5px;\n" +
                            "    margin-bottom: 10px;\n" +
                            "    background: rgba(223, 219, 193, 0.73);\n" +
                            "    float: left;\n" +
                            "    width: 100%;\n" +
                            "}\n" +
                            "\n" +
                            "    .shareSection a {\n" +
                            "        cursor: pointer;\n" +
                            "    }\n" +
                            "\n" +
                            ".page-content {\n" +
                            "    text-align: right;\n" +
                            "    font-size: 10px;\n" +
                            "    padding: 20px 0px;\n" +
                            "    direction: rtl;\n" +
                            "    display: inline-block;\n" +
                            "    background-color: white;\n" +
                            "    padding: 0px 20px;\n" +
                            "    margin-bottom: 10px;\n" +
                            "    width: 100%;\n" +
                            "}\n" +
                            "\n" +
                            ".ImagePageContent {\n" +
                            "    text-align: center;\n" +
                            "    font-size: 16px;\n" +
                            "    padding: 0px 0px;\n" +
                            "}\n" +
                            "\n" +
                            "#contentarea .container #inside_left {\n" +
                            "    float: left;\n" +
                            "    width: 700px;\n" +
                            "    height: auto;\n" +
                            "    margin: 0px 20px 0px 60px;\n" +
                            "    font-family: 'Mehr Nastaliq Web' !important;\n" +
                            "}\n" +
                            "\n" +
                            "    #contentarea .container #inside_left h1 {\n" +
                            "        width: 100%;\n" +
                            "        float: left;\n" +
                            "        font-size: 30px;\n" +
                            "        text-align: right;\n" +
                            "        color: #5e4227;\n" +
                            "        text-shadow: 2px 2px 2px #fff;\n" +
                            "    }\n" +
                            "\n" +
                            "    #contentarea .container #inside_left h5 {\n" +
                            "        width: 100%;\n" +
                            "        float: left;\n" +
                            "        font-size: 14px;\n" +
                            "        text-align: right;\n" +
                            "        color: #5e4227;\n" +
                            "        text-shadow: 2px 2px 2px #fff;\n" +
                            "    }\n" +
                            "\n" +
                            "    #contentarea .container #inside_left .span {\n" +
                            "        font-size: 16px;\n" +
                            "        text-align: right;\n" +
                            "        color: #5e4227;\n" +
                            "        margin: 0px 10px 0px 0px;\n" +
                            "    }\n" +
                            "\n" +
                            "    #contentarea .container #inside_left h4 {\n" +
                            "        width: 100%;\n" +
                            "        float: left;\n" +
                            "        font-size: 24px;\n" +
                            "        text-align: right;\n" +
                            "        color: #0C0;\n" +
                            "        text-shadow: 2px 2px 2px #fff;\n" +
                            "        margin: 15px 0px 0px 0px;\n" +
                            "    }\n" +
                            "\n" +
                            "    #contentarea .container #inside_left h2 {\n" +
                            "        width: 100%;\n" +
                            "        float: left;\n" +
                            "        font-size: 18px;\n" +
                            "        text-align: right;\n" +
                            "        color: #800000;\n" +
                            "        margin: 10px 0px 0px 0px;\n" +
                            "        line-height: 30px;\n" +
                            "        font-weight: normal;\n" +
                            "    }\n" +
                            "\n" +
                            "    #contentarea .container #inside_left p {\n" +
                            "        width: 100%;\n" +
                            "        float: left;\n" +
                            "        font-size: 16px;\n" +
                            "        text-align: right;\n" +
                            "        line-height: 20px;\n" +
                            "        color: #0000ff;\n" +
                            "        margin: 10px 0px 0px 0px;\n" +
                            "        line-height: 30px;\n" +
                            "        font-weight: normal;\n" +
                            "    }\n" +
                            "\n" +
                            "    #contentarea .container #inside_left .span {\n" +
                            "        color: #000;\n" +
                            "    }\n" +
                            "\n" +
                            "    #contentarea .container #inside_left h3 {\n" +
                            "        width: 100%;\n" +
                            "        float: left;\n" +
                            "        font-size: 14px;\n" +
                            "        text-align: right;\n" +
                            "        color: #000000;\n" +
                            "        margin: 0px 0px 0px 0px;\n" +
                            "        line-height: 30px;\n" +
                            "        font-weight: normal;\n" +
                            "    }\n" +
                            "\n" +
                            "\n" +
                            "\n" +
                            "\n" +
                            "\n" +
                            "\n" +
                            "\n" +
                            "#contentarea .container #right {\n" +
                            "    float: left;\n" +
                            "    width: 330px;\n" +
                            "    height: auto;\n" +
                            "    direction: rtl;\n" +
                            "}\n" +
                            "\n" +
                            "\n" +
                            "\n" +
                            "#contentarea .container #right .topbox {\n" +
                            "    box-shadow: 0px 0px 3px 1px #585757;\n" +
                            "    width: 100%;\n" +
                            "    height: auto;\n" +
                            "    float: left;\n" +
                            "    background: #FFF;\n" +
                            "    border: none;\n" +
                            "    border-radius: 6px;\n" +
                            "    margin: 0px 0px 15px 0px;\n" +
                            "    padding: 0px 0px 0px 0px;\n" +
                            "    direction: rtl;\n" +
                            "}\n" +
                            "\n" +
                            "#contentarea .container #right .select2c {\n" +
                            "    float: right;\n" +
                            "    text-align: right;\n" +
                            "    font-size: 15px;\n" +
                            "    color: #5E4227;\n" +
                            "    width: 300px;\n" +
                            "    height: 35px;\n" +
                            "    background: url(../images/search_bg.png) no-repeat;\n" +
                            "    margin: 5px 20px 5px 0px;\n" +
                            "    border: none;\n" +
                            "    cursor: pointer;\n" +
                            "    font-family: 'Mehr Nastaliq Web' !important;\n" +
                            "    -moz-appearance: none;\n" +
                            "    -webkit-appearance: none;\n" +
                            "    appearance: none;\n" +
                            "}\n" +
                            "#contentarea .container #right ul li {\n" +
                            "    float: right;\n" +
                            "    width: 100%;\n" +
                            "    height: auto;\n" +
                            "    line-height: 23px;\n" +
                            "    font-size: 20px;\n" +
                            "    text-align: right;\n" +
                            "    background: url(../images/dot.png) center right no-repeat;\n" +
                            "    list-style: none;\n" +
                            "    padding: 5px 15px 0px 0px;\n" +
                            "    margin: 0px;\n" +
                            "    direction: rtl;\n" +
                            "}\n" +
                            "\n" +
                            "#contentarea .container #right .select2ca {\n" +
                            "    float: right;\n" +
                            "    text-align: right;\n" +
                            "    font-size: 13px;\n" +
                            "    color: #5E4227;\n" +
                            "    width: 300px;\n" +
                            "    height: 35px;\n" +
                            "    border: 1px solid #1d818a;\n" +
                            "    box-shadow: 0px 0px 2px 1px #d6d5d5 inset;\n" +
                            "    margin: 5px 20px 5px 0px;\n" +
                            "    cursor: pointer;\n" +
                            "    padding: 0px 0px 0px 0px;\n" +
                            "    font-family: 'Mehr Nastaliq Web' !important;\n" +
                            "    direction: rtl;\n" +
                            "}\n" +
                            "#contentarea .container #right .select2ca option {\n" +
                            "    padding: 2px 5px;\n" +
                            "    border-right: 1px solid #000;\n" +
                            "}\n" +
                            "#contentarea .container #right .select2d {\n" +
                            "    /*float: left;*/\n" +
                            "    text-align: center;\n" +
                            "    font-size: 13px;\n" +
                            "    color: #5E4227;\n" +
                            "    width: 50px;\n" +
                            "    height: 32px;\n" +
                            "    border: solid 1px #ddd;\n" +
                            "    margin: -25px 0px 5px 15px;\n" +
                            "    cursor: pointer;\n" +
                            "    /*font-family: 'Mehr Nastaliq Web' !important;*/\n" +
                            "    /*-moz-appearance: none;\n" +
                            "        -webkit-appearance: none;\n" +
                            "        appearance: none;*/\n" +
                            "}\n" +
                            "\n" +
                            "#contentarea .container #right ul {\n" +
                            "    float: left;\n" +
                            "    width: 100%;\n" +
                            "    /*height: 300px;*/\n" +
                            "    /*overflow: scroll;*/\n" +
                            "    padding: 10px;\n" +
                            "    margin: 0px 0px 0px 0px;\n" +
                            "}\n" +
                            "#contentarea .container #rightS {\n" +
                            "    float: left;\n" +
                            "    width: 330px;\n" +
                            "    height: auto;\n" +
                            "    direction: rtl;\n" +
                            "}\n" +
                            "#contentarea .container #rightS .topbox {\n" +
                            "    box-shadow: 0px 0px 3px 1px #585757;\n" +
                            "    width: 100%;\n" +
                            "    height: auto;\n" +
                            "    float: left;\n" +
                            "    background: #FFF;\n" +
                            "    border: none;\n" +
                            "    border-radius: 6px;\n" +
                            "    margin: 0px 0px 15px 0px;\n" +
                            "    padding: 0px 0px 0px 0px;\n" +
                            "    direction: rtl;\n" +
                            "}\n" +
                            "\n" +
                            "#contentarea .container #rightS .select2c {\n" +
                            "    float: right;\n" +
                            "    text-align: right;\n" +
                            "    font-size: 13px;\n" +
                            "    color: #5E4227;\n" +
                            "    width: 300px;\n" +
                            "    height: 35px;\n" +
                            "    background: url(../images/search_bg.png) no-repeat;\n" +
                            "    margin: 5px 20px 5px 0px;\n" +
                            "    border: none;\n" +
                            "    cursor: pointer;\n" +
                            "    font-family: 'Mehr Nastaliq Web' !important;\n" +
                            "    -moz-appearance: none;\n" +
                            "    -webkit-appearance: none;\n" +
                            "    appearance: none;\n" +
                            "}\n" +
                            "#contentarea .container #rightS .select2ca {\n" +
                            "    float: right;\n" +
                            "    text-align: right;\n" +
                            "    font-size: 16px;\n" +
                            "    color: #5E4227;\n" +
                            "    width: 300px;\n" +
                            "    height: 35px;\n" +
                            "    border: 1px solid #1d818a;\n" +
                            "    box-shadow: 0px 0px 2px 1px #d6d5d5 inset;\n" +
                            "    margin: 5px 20px 5px 0px;\n" +
                            "    cursor: pointer;\n" +
                            "    padding: 0px 0px 0px 0px;\n" +
                            "    font-family: 'Mehr Nastaliq Web' !important;\n" +
                            "    direction: rtl;\n" +
                            "}\n" +
                            "\n" +
                            "\n" +
                            "\n" +
                            "#contentarea .container #rightS .select2ca option {\n" +
                            "    padding: 2px 5px;\n" +
                            "    border-right: 1px solid #000;\n" +
                            "}\n" +
                            "\n" +
                            "\n" +
                            "#contentarea .container #rightS .select2d {\n" +
                            "    float: left;\n" +
                            "    text-align: center;\n" +
                            "    font-size: 13px;\n" +
                            "    color: #5E4227;\n" +
                            "    width: 50px;\n" +
                            "    height: 25px;\n" +
                            "    border: solid 1px #ddd;\n" +
                            "    margin: -20px 0px 5px 15px;\n" +
                            "    cursor: pointer;\n" +
                            "    font-family: 'Mehr Nastaliq Web' !important;\n" +
                            "    -moz-appearance: none;\n" +
                            "    -webkit-appearance: none;\n" +
                            "    appearance: none;\n" +
                            "}\n" +
                            "\n" +
                            "#contentarea .container #rightS ul {\n" +
                            "    float: left;\n" +
                            "    width: 100%;\n" +
                            "    height: 300px;\n" +
                            "    overflow: scroll;\n" +
                            "    padding: 0px;\n" +
                            "    margin: 0px 0px 0px 0px;\n" +
                            "}\n" +
                            "\n" +
                            "\n" +
                            "\n" +
                            "#contentarea .container #rightS ul li {\n" +
                            "    float: right;\n" +
                            "    width: 100%;\n" +
                            "    height: auto;\n" +
                            "    line-height: 20px;\n" +
                            "    font-size: 20px;\n" +
                            "    text-align: right;\n" +
                            "    background: url(../images/dot.png) center right no-repeat;\n" +
                            "    list-style: none;\n" +
                            "    padding: 5px 15px 0px 0px;\n" +
                            "    margin: 0px;\n" +
                            "    direction: rtl;\n" +
                            "}\n" +
                            "#contentarea .container #rightS ul li:hover {\n" +
                            "    background-color: #edeadb;\n" +
                            "    font-weight: bold;\n" +
                            "}\n" +
                            "\n" +
                            "#contentarea .container #right ul li a {\n" +
                            "    font-family: 'Mehr Nastaliq Web' !important;\n" +
                            "    font-size: 15px;\n" +
                            "    color: #5E4227;\n" +
                            "}\n" +
                            "\n" +
                            "#contentarea .container #rightS ul li a {\n" +
                            "    font-family: 'Mehr Nastaliq Web' !important;\n" +
                            "    font-size: 13px;\n" +
                            "    color: #5E4227;\n" +
                            "}\n" +
                            "#contentarea .container #rightS .tab {\n" +
                            "    width: 100%;\n" +
                            "    float: left;\n" +
                            "    background: #b6a08a;\n" +
                            "    font-size: 18px;\n" +
                            "    text-align: right;\n" +
                            "    color: #FFF;\n" +
                            "    padding: 4px 10px 4px 0px;\n" +
                            "    border-radius: 5px 5px 0px 0px;\n" +
                            "    direction: rtl;\n" +
                            "}\n" +
                            "#contentarea .container #right ul li:hover {\n" +
                            "    /*background-color: #edeadb;*/\n" +
                            "    font-weight: bold;\n" +
                            "}\n" +
                            "\n" +
                            "\n" +
                            ".SelectedItem {\n" +
                            "    background-color: #edeadb;\n" +
                            "    font-weight: bold;\n" +
                            "}\n" +
                            "\n" +
                            ".searchPanel {\n" +
                            "    text-align: center;\n" +
                            "    background: #eff3ea;\n" +
                            "    padding: 0px;\n" +
                            "    margin: 0px;\n" +
                            "    border: none;\n" +
                            "    display: block;\n" +
                            "    padding: 5px;\n" +
                            "    position: relative;\n" +
                            "    top: 37px;\n" +
                            "    left: 0px;\n" +
                            "}\n" +
                            "\n" +
                            ".btnSearch {\n" +
                            "    width: 25%;\n" +
                            "    height: 45px;\n" +
                            "    background: #1d818a;\n" +
                            "    cursor: pointer;\n" +
                            "    color: #3B7425;\n" +
                            "    font-size: 16px;\n" +
                            "    font-family: 'Mehr Nastaliq Web' !important;\n" +
                            "    float: left;\n" +
                            "    border: 0px solid groove;\n" +
                            "    color: white;\n" +
                            "    box-shadow: 0px 0px 1px #1d818a;\n" +
                            "}\n" +
                            "\n" +
                            "    .btnSearch:hover {\n" +
                            "        background: #226c72;\n" +
                            "    }\n" +
                            "\n" +
                            ".btnedit {\n" +
                            "    background: #1d818a;\n" +
                            "    cursor: pointer;\n" +
                            "    color: #3B7425;\n" +
                            "    font-size: 16px;\n" +
                            "    font-family: 'Mehr Nastaliq Web' !important;\n" +
                            "    padding: 5px 20px;\n" +
                            "    border: 0px solid groove;\n" +
                            "    color: white;\n" +
                            "    box-shadow: 0px 0px 1px #1d818a;\n" +
                            "}\n" +
                            "\n" +
                            "    .btnedit:hover {\n" +
                            "        background: #226c72;\n" +
                            "    }\n" +
                            "\n" +
                            ".loginsec {\n" +
                            "    padding: 50px 200px;\n" +
                            "    height: 400px;\n" +
                            "}\n" +
                            "\n" +
                            ".btnNextSearch {\n" +
                            "    padding: 5px 10px;\n" +
                            "    background: #e5e5e5;\n" +
                            "    cursor: pointer;\n" +
                            "    color: #4d3921;\n" +
                            "    font-size: 16px;\n" +
                            "    font-family: 'Mehr Nastaliq Web' !important;\n" +
                            "    border: none;\n" +
                            "    border-radius: 3px;\n" +
                            "    font-weight: normal;\n" +
                            "    display: inline;\n" +
                            "    float: left;\n" +
                            "}\n" +
                            "\n" +
                            ".txtSearch {\n" +
                            "    font-family: 'Mehr Nastaliq Web' !important;\n" +
                            "    border: 1px solid #1d818a;\n" +
                            "    box-shadow: 0px 0px 1px #1d818a;\n" +
                            "    padding: 4px;\n" +
                            "    width: 75%;\n" +
                            "    height: 45px;\n" +
                            "    text-align: right;\n" +
                            "    float: left;\n" +
                            "}\n" +
                            "\n" +
                            ".SearchArea {\n" +
                            "    width: 100%;\n" +
                            "    padding: 2%;\n" +
                            "    text-align: center;\n" +
                            "    background: rgba(192, 192, 192, 0.12);\n" +
                            "}\n" +
                            "\n" +
                            ".txtinput {\n" +
                            "    width: 250px;\n" +
                            "    border: 1px solid #1d818a;\n" +
                            "    padding: 5px;\n" +
                            "}\n" +
                            "\n" +
                            "#editForm input[type=\"text\"] {\n" +
                            "    width: 250px;\n" +
                            "    border: 1px solid #1d818a;\n" +
                            "    padding: 5px;\n" +
                            "}\n" +
                            "\n" +
                            ".txtDetail {\n" +
                            "    width: 250px;\n" +
                            "    border: 1px solid #1d818a;\n" +
                            "    padding: 5px;\n" +
                            "}\n" +
                            "\n" +
                            "#editForm select {\n" +
                            "    width: 250px;\n" +
                            "    border: 1px solid #1d818a;\n" +
                            "    padding: 5px;\n" +
                            "}\n" +
                            "\n" +
                            "#contentarea .container #right .tab {\n" +
                            "    width: 100%;\n" +
                            "    float: left;\n" +
                            "    background: #b6a08a;\n" +
                            "    font-size: 15px;\n" +
                            "    text-align: right;\n" +
                            "    color: #FFF;\n" +
                            "    padding: 4px 0px 4px 0px;\n" +
                            "    border-radius: 5px 5px 0px 0px;\n" +
                            "    direction: rtl;\n" +
                            "}\n" +
                            "\n" +
                            "#contentarea .container #right h3 {\n" +
                            "    width: 300px;\n" +
                            "    float: left;\n" +
                            "    font-size: 12px;\n" +
                            "    text-align: right;\n" +
                            "    color: #5e4227;\n" +
                            "    margin: 10px 10px 0px 0px;\n" +
                            "}\n" +
                            "\n" +
                            "#contentarea .container #right h4 {\n" +
                            "    float: right;\n" +
                            "    font-size: 12px;\n" +
                            "    text-align: right;\n" +
                            "    color: #5e4227;\n" +
                            "    margin: 10px 0px 0px 0px;\n" +
                            "    width: 70px;\n" +
                            "}\n" +
                            "\n" +
                            "#contentarea .container #right h5 {\n" +
                            "    float: right;\n" +
                            "    font-size: 12px;\n" +
                            "    text-align: right;\n" +
                            "    color: #5e4227;\n" +
                            "    margin: 0px 0px 0px 0px;\n" +
                            "    width: 60px;\n" +
                            "}\n" +
                            "\n" +
                            "#contentarea .container #right h6 {\n" +
                            "    float: left;\n" +
                            "    font-size: 12px;\n" +
                            "    text-align: left;\n" +
                            "    color: #5e4227;\n" +
                            "    margin: 0px 0px 0px 0px;\n" +
                            "    width: 70px;\n" +
                            "}\n" +
                            "\n" +
                            ".blank {\n" +
                            "    width: 145px;\n" +
                            "    float: right;\n" +
                            "    text-align: right;\n" +
                            "    margin-right: 20px;\n" +
                            "}\n" +
                            "\n" +
                            ".blank2 {\n" +
                            "    width: 125px;\n" +
                            "    float: left;\n" +
                            "    text-align: right;\n" +
                            "}\n" +
                            "\n" +
                            ".pages {\n" +
                            "    width: 300px;\n" +
                            "    text-align: right;\n" +
                            "    background: #faf9f9;\n" +
                            "    border-radius: 6px;\n" +
                            "    height: 44px;\n" +
                            "    border: 1px solid #ebebeb;\n" +
                            "    margin: 10px 14px 10px 15px;\n" +
                            "    float: left;\n" +
                            "    padding: 4px 4px;\n" +
                            "}\n" +
                            "\n" +
                            ".pages-bottom {\n" +
                            "    width: 50%;\n" +
                            "    text-align: right;\n" +
                            "    background: #faf9f9;\n" +
                            "    border-radius: 6px;\n" +
                            "    height: 44px;\n" +
                            "    border: 1px solid #ebebeb;\n" +
                            "    /*margin: 10px 14px 10px 15px;*/\n" +
                            "    margin-bottom: 10px;\n" +
                            "    margin-left: 25%;\n" +
                            "    float: left;\n" +
                            "    padding: 4px 4px;\n" +
                            "}\n" +
                            "\n" +
                            ".pages img {\n" +
                            "    float: left;\n" +
                            "    margin: 0px 0px 0px 0px;\n" +
                            "    cursor: pointer;\n" +
                            "}\n" +
                            "\n" +
                            ".page_div {\n" +
                            "    width: 80px;\n" +
                            "    float: left;\n" +
                            "    height: auto;\n" +
                            "}\n" +
                            "\n" +
                            "\n" +
                            "#contentarea .container #right p {\n" +
                            "    width: 300px;\n" +
                            "    float: right;\n" +
                            "    font-size: 16px;\n" +
                            "    text-align: right;\n" +
                            "    color: #5e4227;\n" +
                            "    line-height: 30px;\n" +
                            "    /*font-family: 'noorehira';*/\n" +
                            "    padding: 0px 20px 0px 0px;\n" +
                            "    direction: rtl;\n" +
                            "}\n" +
                            "\n" +
                            "    #contentarea .container #right p:last-child {\n" +
                            "        margin-bottom: 10px;\n" +
                            "    }\n" +
                            "\n" +
                            "#contentarea .container #right span {\n" +
                            "    color: #4e812d;\n" +
                            "    font-size: 18px;\n" +
                            "    font-weight: bold;\n" +
                            "    background: url(../images/right_box_arrow.jpg) right top no-repeat;\n" +
                            "    padding: 0px 20px 0px 0px;\n" +
                            "    margin: 10px 10px 0px 0px;\n" +
                            "    /*float: right;*/\n" +
                            "    width: 60px !important;\n" +
                            "    /*text-align: right;*/\n" +
                            "}\n" +
                            "\n" +
                            "#contentarea .container #right p a {\n" +
                            "    color: #da3300;\n" +
                            "}\n" +
                            "\n" +
                            "\n" +
                            "\n" +
                            "\n" +
                            "\n" +
                            "#contentarea .container #right h2 {\n" +
                            "    width: 100%;\n" +
                            "    float: left;\n" +
                            "    background: #89c53e;\n" +
                            "    font-size: 18px;\n" +
                            "    text-align: right;\n" +
                            "    color: #fff;\n" +
                            "    padding: 4px 10px 4px 0px;\n" +
                            "    border-radius: 5px 5px 0px 0px;\n" +
                            "}\n" +
                            "\n" +
                            "#books {\n" +
                            "    width: 100%;\n" +
                            "    float: right;\n" +
                            "    height: auto;\n" +
                            "    margin: 10px 10px 20px 0px;\n" +
                            "    background: #ece7c3;\n" +
                            "    background: -webkit-linear-gradient(#ece7c3, #e2daa2); /* For Safari 5.1 to 6.0 */\n" +
                            "    background: -o-linear-gradient(#ece7c3, #e2daa2); /* For Opera 11.1 to 12.0 */\n" +
                            "    background: -moz-linear-gradient(#ece7c3, #e2daa2); /* For Firefox 3.6 to 15 */\n" +
                            "    background: linear-gradient(#ece7c3, #e2daa2); /* Standard syntax */\n" +
                            "    padding: 10px;\n" +
                            "    direction: ltr;\n" +
                            "}\n" +
                            "\n" +
                            "\n" +
                            "\n" +
                            "\n" +
                            "#footer {\n" +
                            "    width: 100%;\n" +
                            "    height: 218px;\n" +
                            "    background: url(../images/footer_bg.jpg) repeat-x;\n" +
                            "}\n" +
                            "\n" +
                            "    #footer .container {\n" +
                            "        width: 1170px;\n" +
                            "        height: 218px;\n" +
                            "        margin: 0 auto;\n" +
                            "        font-family: Tahoma, Geneva, sans-serif;\n" +
                            "        background: url(../images/footer_content_bg.png) no-repeat center top;\n" +
                            "    }\n" +
                            "\n" +
                            "        #footer .container .box {\n" +
                            "            width: 180px;\n" +
                            "            height: auto;\n" +
                            "            float: left;\n" +
                            "        }\n" +
                            "\n" +
                            "        #footer .container img {\n" +
                            "            float: left;\n" +
                            "            margin: 10px 10px 0px 0px;\n" +
                            "        }\n" +
                            "\n" +
                            "\n" +
                            "        #footer .container .box2 {\n" +
                            "            width: 250px;\n" +
                            "            height: auto;\n" +
                            "            float: left;\n" +
                            "        }\n" +
                            "\n" +
                            "        #footer .container .icons {\n" +
                            "            width: 100px;\n" +
                            "            height: auto;\n" +
                            "            float: left;\n" +
                            "            margin: 20px 0px 0px 0px;\n" +
                            "        }\n" +
                            "\n" +
                            "        #footer .container .icons2 {\n" +
                            "            width: 150px;\n" +
                            "            height: auto;\n" +
                            "            float: left;\n" +
                            "            margin: 30px 0px 0px 0px;\n" +
                            "        }\n" +
                            "\n" +
                            "        #footer .container h1 {\n" +
                            "            width: 100%;\n" +
                            "            height: auto;\n" +
                            "            font-size: 15px;\n" +
                            "            color: #b09478;\n" +
                            "            float: left;\n" +
                            "            margin: 20px 0px 0px 19px;\n" +
                            "        }\n" +
                            "\n" +
                            "        #footer .container p {\n" +
                            "            width: 100%;\n" +
                            "            height: auto;\n" +
                            "            font-size: 12px;\n" +
                            "            color: #fff;\n" +
                            "            float: right;\n" +
                            "            margin: 0px 0px 0px 0px;\n" +
                            "            text-align: justify;\n" +
                            "        }\n" +
                            "\n" +
                            "        #footer .container h2 {\n" +
                            "            width: 100%;\n" +
                            "            height: auto;\n" +
                            "            font-size: 12px;\n" +
                            "            color: #b09478;\n" +
                            "            float: left;\n" +
                            "            margin: 30px 0px 0px 0px;\n" +
                            "            text-align: justify;\n" +
                            "            font-weight: normal;\n" +
                            "        }\n" +
                            "\n" +
                            "        #footer .container ul {\n" +
                            "            width: 100%;\n" +
                            "            height: auto;\n" +
                            "            float: left;\n" +
                            "            padding: 0px;\n" +
                            "            margin: 5px 0px 0px 0px;\n" +
                            "        }\n" +
                            "\n" +
                            "            #footer .container ul li {\n" +
                            "                width: 100%;\n" +
                            "                height: auto;\n" +
                            "                font-size: 12px;\n" +
                            "                color: #b09478;\n" +
                            "                float: left;\n" +
                            "                margin: 5px 0px 0px 20px;\n" +
                            "                line-height: 20px;\n" +
                            "                list-style-type: none;\n" +
                            "                list-style: none;\n" +
                            "                padding: 0px;\n" +
                            "            }\n" +
                            "\n" +
                            "                #footer .container ul li a {\n" +
                            "                    color: #b09478;\n" +
                            "                }\n" +
                            "\n" +
                            "                    #footer .container ul li a:hover {\n" +
                            "                        color: #fff;\n" +
                            "                        text-decoration: underline;\n" +
                            "                    }\n" +
                            "\n" +
                            "/********************************* MARKERS  ********************************************/\n" +
                            "\n" +
                            "\n" +
                            "\n" +
                            ".quran-marker {\n" +
                            "    font-family: 'noorehuda';\n" +
                            "    font-size: 20px;\n" +
                            "    line-height: 27px;\n" +
                            "    color: Blue;\n" +
                            "}\n" +
                            "\n" +
                            ".quran-bold-marker {\n" +
                            "    font-family: 'noorehuda';\n" +
                            "    font-size: 20px;\n" +
                            "    line-height: 27px;\n" +
                            "    color: Blue;\n" +
                            "    font-weight: bold;\n" +
                            "}\n" +
                            "\n" +
                            ".quran-underline-marker {\n" +
                            "    font-size: 20px;\n" +
                            "    line-height: 27px;\n" +
                            "    color: Blue;\n" +
                            "    text-decoration: underline;\n" +
                            "}\n" +
                            "\n" +
                            "\n" +
                            ".hadees-marker {\n" +
                            "    font-family: 'noorehuda';\n" +
                            "    font-size: 20px;\n" +
                            "    line-height: 27px;\n" +
                            "    color: Green;\n" +
                            "}\n" +
                            "\n" +
                            ".hadees-underline-marker {\n" +
                            "    font-family: 'noorehuda';\n" +
                            "    font-size: 20px;\n" +
                            "    line-height: 27px;\n" +
                            "    color: Green;\n" +
                            "    text-decoration: underline;\n" +
                            "}\n" +
                            "\n" +
                            ".hadees-bold-marker {\n" +
                            "    font-family: 'noorehuda';\n" +
                            "    font-size: 20px;\n" +
                            "    line-height: 27px;\n" +
                            "    color: Green;\n" +
                            "    font-weight: bold;\n" +
                            "}\n" +
                            "\n" +
                            "\n" +
                            ".urdu-marker {\n" +
                            "    font-family: 'Mehr Nastaliq Web' !important;\n" +
                            "    font-size: 16px;\n" +
                            "    color: Black;\n" +
                            "}\n" +
                            "\n" +
                            ".urdu-underline-marker {\n" +
                            "    font-family: 'Mehr Nastaliq Web' !important;\n" +
                            "    font-size: 16px;\n" +
                            "    color: Black;\n" +
                            "    text-decoration: underline;\n" +
                            "}\n" +
                            "\n" +
                            ".urdu-bold-marker {\n" +
                            "    font-family: 'Mehr Nastaliq Web' !important;\n" +
                            "    font-size: 13px;\n" +
                            "    color: Black;\n" +
                            "    font-weight: bold;\n" +
                            "}\n" +
                            "\n" +
                            ".ref-urdu-marker {\n" +
                            "    font-family: 'Mehr Nastaliq Web' !important;\n" +
                            "    font-size: 13px;\n" +
                            "    color: Gray;\n" +
                            "    display: block;\n" +
                            "    text-align: center;\n" +
                            "}\n" +
                            "\n" +
                            ".ref-urdu {\n" +
                            "    font-family: 'Mehr Nastaliq Web' !important;\n" +
                            "    font-size: 14px;\n" +
                            "    color: Gray;\n" +
                            "    display: block;\n" +
                            "    text-align: center;\n" +
                            "}\n" +
                            "\n" +
                            ".arabic-marker {\n" +
                            "    font-family: 'noorehuda';\n" +
                            "    font-size: 20px;\n" +
                            "    line-height: 27px;\n" +
                            "    color: Maroon;\n" +
                            "}\n" +
                            "\n" +
                            ".arabic-underline-marker {\n" +
                            "    font-family: 'noorehuda';\n" +
                            "    font-size: 20px;\n" +
                            "    line-height: 27px;\n" +
                            "    color: Maroon;\n" +
                            "    text-decoration: underline;\n" +
                            "}\n" +
                            "\n" +
                            ".arabic-bold-marker {\n" +
                            "    font-family: 'noorehuda';\n" +
                            "    font-size: 20px;\n" +
                            "    line-height: 27px;\n" +
                            "    color: Maroon;\n" +
                            "    font-weight: bold;\n" +
                            "}\n" +
                            "\n" +
                            ".ref-arabic-marker {\n" +
                            "    font-family: 'noorehuda';\n" +
                            "    color: Gray;\n" +
                            "    font-size: 14px;\n" +
                            "}\n" +
                            "\n" +
                            ".ref-arabic {\n" +
                            "    font-family: 'noorehuda';\n" +
                            "    color: Gray;\n" +
                            "    font-size: 14px;\n" +
                            "}\n" +
                            "\n" +
                            ".english-marker {\n" +
                            "    font-family: Times New Roman;\n" +
                            "    color: #BB8626;\n" +
                            "    font-size: 18px;\n" +
                            "}\n" +
                            "\n" +
                            ".english-underline-marker {\n" +
                            "    font-family: Times New Roman;\n" +
                            "    color: #BB8626;\n" +
                            "    font-size: 18px;\n" +
                            "    text-decoration: underline;\n" +
                            "}\n" +
                            "\n" +
                            ".english-bold-marker {\n" +
                            "    font-family: Times New Roman;\n" +
                            "    color: #BB8626;\n" +
                            "    font-size: 18px;\n" +
                            "    font-weight: bold;\n" +
                            "}\n" +
                            "\n" +
                            "\n" +
                            ".ref-english-marker {\n" +
                            "    font-family: Times New Roman;\n" +
                            "    color: Gray;\n" +
                            "    font-size: 14px;\n" +
                            "}\n" +
                            "\n" +
                            ".ref-english {\n" +
                            "    font-family: Times New Roman;\n" +
                            "    color: Gray;\n" +
                            "    font-size: 14px;\n" +
                            "}\n" +
                            "\n" +
                            "\n" +
                            "\n" +
                            ".heading-marker {\n" +
                            "    font-family: 'Mehr Nastaliq Web' !important;\n" +
                            "    font-size: 30px;\n" +
                            "    color: navy;\n" +
                            "    text-align: center;\n" +
                            "}\n" +
                            "\n" +
                            "\n" +
                            ".heading1-marker {\n" +
                            "    font-family: 'Mehr Nastaliq Web' !important;\n" +
                            "    font-size: 24px;\n" +
                            "    color: navy;\n" +
                            "    text-align: center;\n" +
                            "}\n" +
                            "\n" +
                            ".heading-underline-marker {\n" +
                            "    font-family: 'Mehr Nastaliq Web' !important;\n" +
                            "    font-size: 24px;\n" +
                            "    color: navy;\n" +
                            "    text-align: center;\n" +
                            "    text-decoration: underline;\n" +
                            "    /*line-height: 60px;*/\n" +
                            "}\n" +
                            "\n" +
                            "\n" +
                            "\n" +
                            ".chapter-marker {\n" +
                            "    font-family: 'Mehr Nastaliq Web' !important;\n" +
                            "    font-size: 50px;\n" +
                            "    color: navy;\n" +
                            "    display: block;\n" +
                            "    text-align: center;\n" +
                            "}\n" +
                            "\n" +
                            ".poetry-marker {\n" +
                            "    font-family: 'Mehr Nastaliq Web' !important;\n" +
                            "    font-size: 20px;\n" +
                            "    color: #F20B0B;\n" +
                            "    text-align: center;\n" +
                            "}\n" +
                            "\n" +
                            ".margin-line-marker {\n" +
                            "    display: block;\n" +
                            "    text-align: center;\n" +
                            "}\n" +
                            "\n" +
                            ".ajax-loader {\n" +
                            "    position: fixed;\n" +
                            "    left: 50%;\n" +
                            "    top: 50%;\n" +
                            "    margin-left: -70px; /* -1 * image width / 2 */\n" +
                            "    margin-top: -70px; /* -1 * image height / 2 */\n" +
                            "    display: block;\n" +
                            "}\n" +
                            "\n" +
                            ".ajax-loader_Home {\n" +
                            "    position: fixed;\n" +
                            "    left: 45%;\n" +
                            "    top: 30%;\n" +
                            "    margin-left: -70px; /* -1 * image width / 2 */\n" +
                            "    margin-top: -70px; /* -1 * image height / 2 */\n" +
                            "    display: block;\n" +
                            "}\n" +
                            "\n" +
                            ".overlay {\n" +
                            "    position: fixed;\n" +
                            "    top: 0px;\n" +
                            "    left: 0px;\n" +
                            "    right: 0px;\n" +
                            "    bottom: 0px;\n" +
                            "    background: rgba(17, 16, 16, 0.08);\n" +
                            "}\n" +
                            "\n" +
                            ".overlay2 {\n" +
                            "    background: rgba(4, 4, 4, 0.34);\n" +
                            "    position: fixed;\n" +
                            "    top: 0px;\n" +
                            "    bottom: 0px;\n" +
                            "    left: 0px;\n" +
                            "    right: 0px;\n" +
                            "    z-index: 10001;\n" +
                            "}\n" +
                            "\n" +
                            ".lnkMenu {\n" +
                            "    color: white;\n" +
                            "    background: #ed7b02;\n" +
                            "    cursor: pointer;\n" +
                            "    display: inline-block;\n" +
                            "    border-radius: 50%;\n" +
                            "    height: 75px;\n" +
                            "}\n" +
                            "\n" +
                            "    .lnkMenu:hover {\n" +
                            "        background: #1d818a;\n" +
                            "    }\n" +
                            "\n" +
                            ".grid {\n" +
                            "    width: 100%;\n" +
                            "    border: 0px solid #ccaa03;\n" +
                            "    background: #fffbf8;\n" +
                            "}\n" +
                            "\n" +
                            "    .grid th {\n" +
                            "        border: 1px solid #226c72;\n" +
                            "        background: #6b745d;\n" +
                            "        color: #fafafa;\n" +
                            "        padding: 3px;\n" +
                            "    }\n" +
                            "\n" +
                            "    .grid td {\n" +
                            "        border: 1px solid #226c72;\n" +
                            "        padding: 3px;\n" +
                            "    }\n" +
                            "\n" +
                            ".alternateRow {\n" +
                            "    background: #f9f5f3;\n" +
                            "}\n" +
                            "\n" +
                            ".homePageIntroText {\n" +
                            "    font-size: 20px !Important;\n" +
                            "}\n" +
                            "\n" +
                            ".boxSec {\n" +
                            "    position: fixed;\n" +
                            "    top: -550px;\n" +
                            "    left: 30%;\n" +
                            "    right: 30%;\n" +
                            "    background-color: #fff;\n" +
                            "    color: #7F7F7F;\n" +
                            "    padding: 20px;\n" +
                            "    border: 1px solid #3C3C3C;\n" +
                            "    border-radius: 5px;\n" +
                            "    -webkit-border-radius: 5px;\n" +
                            "    -moz-border-radius: 5px;\n" +
                            "    -moz-box-shadow: 0px 0px 10px #000;\n" +
                            "    -webkit-box-shadow: 0px 0px 10px #000;\n" +
                            "    box-shadow: 0px 0px 10px #000;\n" +
                            "    z-index: 10005;\n" +
                            "}\n" +
                            "\n" +
                            "    .boxSec h1 {\n" +
                            "        border-radius: 5px 0px;\n" +
                            "        border-bottom: 1px dashed #7F7F7F;\n" +
                            "        margin: -20px -20px 0px -20px;\n" +
                            "        padding: 10px;\n" +
                            "        background-color: #ece7c3;\n" +
                            "        color: #5A5A5A;\n" +
                            "        font-size: 22px;\n" +
                            "        font-weight: bold;\n" +
                            "        font-family: Arial, Helvetica, sans-serif;\n" +
                            "    }\n" +
                            "\n" +
                            "    .boxSec p {\n" +
                            "        font-size: 16px;\n" +
                            "        padding: 20px 10px;\n" +
                            "        height: 100px;\n" +
                            "        color: black;\n" +
                            "        font-family: Arial;\n" +
                            "    }\n" +
                            "\n" +
                            ".boxclose {\n" +
                            "    float: right;\n" +
                            "    width: 26px;\n" +
                            "    height: 26px;\n" +
                            "    background: transparent url(../images/cancel.png) repeat top left;\n" +
                            "    margin-top: -39px;\n" +
                            "    margin-right: -43px;\n" +
                            "    cursor: pointer;\n" +
                            "    z-index: 100060;\n" +
                            "}\n" +
                            "\n" +
                            ".prev {\n" +
                            "    position: fixed;\n" +
                            "    top: 200px;\n" +
                            "    left: 400px;\n" +
                            "    opacity: .1;\n" +
                            "    filter: alpha(opacity=40);\n" +
                            "}\n" +
                            "\n" +
                            ".next {\n" +
                            "    position: fixed;\n" +
                            "    top: 245px;\n" +
                            "    left: 145px;\n" +
                            "    opacity: .1;\n" +
                            "    filter: alpha(opacity=40);\n" +
                            "}\n" +
                            "\n" +
                            ".bookfrontpage {\n" +
                            "    min-height: 550px;\n" +
                            "    max-height: 550px;\n" +
                            "    width: 400px;\n" +
                            "    /*background-color: #BE2134;*/\n" +
                            "    text-align: center;\n" +
                            "    font-size: 16px;\n" +
                            "    padding: 0px 0px;\n" +
                            "    margin-left: 130px;\n" +
                            "    margin-right: 130px;\n" +
                            "}\n" +
                            "\n" +
                            ".booktitle1 {\n" +
                            "    float: left;\n" +
                            "    width: 100%;\n" +
                            "    min-height: 193px;\n" +
                            "    max-height: 193px;\n" +
                            "}\n" +
                            "\n" +
                            ".b-maroon {\n" +
                            "    background-color: #BE2134;\n" +
                            "}\n" +
                            "\n" +
                            ".b-offwhite {\n" +
                            "    background-color: #D1CFD0;\n" +
                            "}\n" +
                            "\n" +
                            ".b-green {\n" +
                            "    background-color: #21402A;\n" +
                            "}\n" +
                            "\n" +
                            ".c-maroon {\n" +
                            "    color: #BE2134;\n" +
                            "}\n" +
                            "\n" +
                            ".c-offwhite {\n" +
                            "    color: #D1CFD0;\n" +
                            "}\n" +
                            "\n" +
                            ".c-green {\n" +
                            "    color: #21402A;\n" +
                            "}\n" +
                            "\n" +
                            "\n" +
                            ".seprator {\n" +
                            "    float: left;\n" +
                            "    width: 100%;\n" +
                            "    min-height: 10px;\n" +
                            "    max-height: 10px;\n" +
                            "}\n" +
                            "\n" +
                            ".bookAuthor1 {\n" +
                            "    float: left;\n" +
                            "    width: 100%;\n" +
                            "    min-height: 153px;\n" +
                            "    max-height: 153px;\n" +
                            "    padding-top: 40px;\n" +
                            "}\n" +
                            "\n" +
                            ".bookPublisher1 {\n" +
                            "    float: left;\n" +
                            "    width: 100%;\n" +
                            "    min-height: 193px;\n" +
                            "    max-height: 193px;\n" +
                            "    padding-top: 25px;\n" +
                            "}\n" +
                            "\n" +
                            ".spanstyle {\n" +
                            "    font-size: 24px;\n" +
                            "    font-family: 'Mehr Nastaliq Web' !important;\n" +
                            "}\n" +
                            "\n" +
                            ".booktitle2 {\n" +
                            "    float: left;\n" +
                            "    width: 100%;\n" +
                            "    min-height: 165px;\n" +
                            "    max-height: 165px;\n" +
                            "}\n" +
                            "\n" +
                            ".bookAuthor2 {\n" +
                            "    float: left;\n" +
                            "    width: 100%;\n" +
                            "    min-height: 220px;\n" +
                            "    max-height: 220px;\n" +
                            "}\n" +
                            "\n" +
                            ".bookPublisher2 {\n" +
                            "    float: left;\n" +
                            "    width: 100%;\n" +
                            "    min-height: 165px;\n" +
                            "    max-height: 165px;\n" +
                            "}\n" +
                            "\n" +
                            ".contentpage {\n" +
                            "    min-height: 800px;\n" +
                            "    max-height: 1000px;\n" +
                            "    width: 400px;\n" +
                            "    font-size: 16px;\n" +
                            "    padding: 0px 0px;\n" +
                            "    /*text-align: center;*/\n" +
                            "    font-size: 16px;\n" +
                            "    padding: 0px 0px;\n" +
                            "    margin-left: 130px;\n" +
                            "    margin-right: 130px;\n" +
                            "}\n" +
                            "\n" +
                            ".contentpageHeading {\n" +
                            "    min-height: 50px;\n" +
                            "    max-height: 50px;\n" +
                            "    margin-right: 155px;\n" +
                            "    /*float:right;*/\n" +
                            "}\n" +
                            "\n" +
                            ".main-content {\n" +
                            "    float: right;\n" +
                            "    width: 360px;\n" +
                            "    clear: both;\n" +
                            "    direction: rtl;\n" +
                            "    /*padding-right:5px;*/\n" +
                            "    margin-top: 0px;\n" +
                            "    margin-right: 10px;\n" +
                            "}\n" +
                            "\n" +
                            "    .main-content:hover, .sub-content:hover {\n" +
                            "        background-color: #E9E6D7;\n" +
                            "        cursor: pointer;\n" +
                            "    }\n" +
                            "\n" +
                            ".sub-content {\n" +
                            "    float: right;\n" +
                            "    width: 350px;\n" +
                            "    clear: both;\n" +
                            "    direction: rtl;\n" +
                            "    /*padding-right:5px;*/\n" +
                            "    margin-right: 20px;\n" +
                            "    margin-top: 5px;\n" +
                            "}\n" +
                            "\n" +
                            ".span-content {\n" +
                            "    font-size: 14px;\n" +
                            "    font-family: 'Mehr Nastaliq Web' !important;\n" +
                            "    color: black;\n" +
                            "}\n" +
                            "/*New Color/Background Classes*/\n" +
                            ".b-green {\n" +
                            "    background-color: #027236;\n" +
                            "}\n" +
                            "\n" +
                            ".c-green {\n" +
                            "    color: #027236;\n" +
                            "}\n" +
                            "\n" +
                            ".b-aqua {\n" +
                            "    background-color: #00A1CD;\n" +
                            "}\n" +
                            "\n" +
                            ".c-aqua {\n" +
                            "    color: #00A1CD;\n" +
                            "}\n" +
                            "\n" +
                            ".b-brown {\n" +
                            "    background-color: #6A3C2C;\n" +
                            "}\n" +
                            "\n" +
                            ".c-brown {\n" +
                            "    color: #6A3C2C;\n" +
                            "}\n" +
                            "\n" +
                            ".b-yellow {\n" +
                            "    background-color: #7C7817;\n" +
                            "}\n" +
                            "\n" +
                            ".c-yellow {\n" +
                            "    color: #7B7725;\n" +
                            "}\n" +
                            "\n" +
                            ".b-grey {\n" +
                            "    background-color: #696768;\n" +
                            "}\n" +
                            "\n" +
                            ".c-grey {\n" +
                            "    color: #696768;\n" +
                            "}\n" +
                            "\n" +
                            ".b-purple {\n" +
                            "    background-color: #864694;\n" +
                            "}\n" +
                            "\n" +
                            ".c-purple {\n" +
                            "    color: #864694;\n" +
                            "}\n" +
                            "\n" +
                            ".b-maroon {\n" +
                            "    background-color: #AB192B;\n" +
                            "}\n" +
                            "\n" +
                            ".c-maroon {\n" +
                            "    color: #AB192B;\n" +
                            "}\n" +
                            "\n" +
                            ".b-olive {\n" +
                            "    background-color: #94B461;\n" +
                            "}\n" +
                            "\n" +
                            ".c-olive {\n" +
                            "    color: #94B461;\n" +
                            "}\n" +
                            "\n" +
                            ".b-website {\n" +
                            "    background-color: #F7F5E9;\n" +
                            "}\n" +
                            "\n" +
                            ".selectedbox {\n" +
                            "    float: right;\n" +
                            "    text-align: right;\n" +
                            "    width: 100%;\n" +
                            "}\n" +
                            "\n" +
                            "#contentarea .container #inside_left_search {\n" +
                            "    float: left;\n" +
                            "    width: 700px;\n" +
                            "    height: auto;\n" +
                            "    margin: 0px 20px 0px 60px;\n" +
                            "    font-family: 'Mehr Nastaliq Web' !important;\n" +
                            "}\n" +
                            "\n" +
                            "    #contentarea .container #inside_left_search h1 {\n" +
                            "        width: 100%;\n" +
                            "        float: left;\n" +
                            "        font-size: 30px;\n" +
                            "        text-align: right;\n" +
                            "        color: #5e4227;\n" +
                            "        text-shadow: 2px 2px 2px #fff;\n" +
                            "    }\n" +
                            "\n" +
                            "    #contentarea .container #inside_left_search h5 {\n" +
                            "        width: 100%;\n" +
                            "        float: left;\n" +
                            "        font-size: 14px;\n" +
                            "        text-align: right;\n" +
                            "        color: #5e4227;\n" +
                            "        text-shadow: 2px 2px 2px #fff;\n" +
                            "    }\n" +
                            "\n" +
                            "    #contentarea .container #inside_left_search .span {\n" +
                            "        font-size: 16px;\n" +
                            "        text-align: right;\n" +
                            "        color: #5e4227;\n" +
                            "        margin: 0px 10px 0px 0px;\n" +
                            "    }\n" +
                            "\n" +
                            "    #contentarea .container #inside_left_search h4 {\n" +
                            "        width: 100%;\n" +
                            "        float: left;\n" +
                            "        font-size: 24px;\n" +
                            "        text-align: right;\n" +
                            "        color: #0C0;\n" +
                            "        text-shadow: 2px 2px 2px #fff;\n" +
                            "        margin: 15px 0px 0px 0px;\n" +
                            "    }\n" +
                            "\n" +
                            "    #contentarea .container #inside_left_search h2 {\n" +
                            "        width: 100%;\n" +
                            "        float: left;\n" +
                            "        font-size: 18px;\n" +
                            "        text-align: right;\n" +
                            "        color: #800000;\n" +
                            "        margin: 10px 0px 0px 0px;\n" +
                            "        line-height: 30px;\n" +
                            "        font-weight: normal;\n" +
                            "    }\n" +
                            "\n" +
                            "    #contentarea .container #inside_left_search p {\n" +
                            "        width: 100%;\n" +
                            "        float: left;\n" +
                            "        font-size: 16px;\n" +
                            "        text-align: right;\n" +
                            "        line-height: 20px;\n" +
                            "        color: #0000ff;\n" +
                            "        margin: 10px 0px 0px 0px;\n" +
                            "        line-height: 30px;\n" +
                            "        font-weight: normal;\n" +
                            "    }\n" +
                            "\n" +
                            "    #contentarea .container #inside_left_search .span {\n" +
                            "        color: #000;\n" +
                            "    }\n" +
                            "\n" +
                            "    #contentarea .container #inside_left_search h3 {\n" +
                            "        width: 100%;\n" +
                            "        float: left;\n" +
                            "        font-size: 14px;\n" +
                            "        text-align: right;\n" +
                            "        color: #000000;\n" +
                            "        margin: 0px 0px 0px 0px;\n" +
                            "        line-height: 30px;\n" +
                            "        font-weight: normal;\n" +
                            "    }\n" +
                            "\n" +
                            "    #contentarea .container #rightS_search {\n" +
                            "    float: left;\n" +
                            "    width: 330px;\n" +
                            "    height: auto;\n" +
                            "    direction: rtl;\n" +
                            "}\n" +
                            "\n" +
                            "#contentarea .container #rightS_search .topbox {\n" +
                            "    box-shadow: 0px 0px 3px 1px #585757;\n" +
                            "    width: 100%;\n" +
                            "    height: auto;\n" +
                            "    float: left;\n" +
                            "    background: #FFF;\n" +
                            "    border: none;\n" +
                            "    border-radius: 6px;\n" +
                            "    margin: 0px 0px 15px 0px;\n" +
                            "    padding: 0px 0px 0px 0px;\n" +
                            "    direction: rtl;\n" +
                            "}\n" +
                            "\n" +
                            "#contentarea .container #rightS_search .select2c {\n" +
                            "    float: right;\n" +
                            "    text-align: right;\n" +
                            "    font-size: 13px;\n" +
                            "    color: #5E4227;\n" +
                            "    width: 300px;\n" +
                            "    height: 35px;\n" +
                            "    background: url(../images/search_bg.png) no-repeat;\n" +
                            "    margin: 5px 20px 5px 0px;\n" +
                            "    border: none;\n" +
                            "    cursor: pointer;\n" +
                            "    font-family: 'Mehr Nastaliq Web' !important;\n" +
                            "    -moz-appearance: none;\n" +
                            "    -webkit-appearance: none;\n" +
                            "    appearance: none;\n" +
                            "}\n" +
                            "#contentarea .container #rightS_search .select2ca {\n" +
                            "    float: right;\n" +
                            "    text-align: right;\n" +
                            "    font-size: 16px;\n" +
                            "    color: #5E4227;\n" +
                            "    width: 300px;\n" +
                            "    height: 35px;\n" +
                            "    border: 1px solid #1d818a;\n" +
                            "    box-shadow: 0px 0px 2px 1px #d6d5d5 inset;\n" +
                            "    margin: 5px 20px 5px 0px;\n" +
                            "    cursor: pointer;\n" +
                            "    padding: 0px 0px 0px 0px;\n" +
                            "    font-family: 'Mehr Nastaliq Web' !important;\n" +
                            "    direction: rtl;\n" +
                            "}\n" +
                            "\n" +
                            "\n" +
                            "\n" +
                            "#contentarea .container #rightS_search .select2ca option {\n" +
                            "    padding: 2px 5px;\n" +
                            "    border-right: 1px solid #000;\n" +
                            "}\n" +
                            "\n" +
                            "\n" +
                            "#contentarea .container #rightS_search .select2d {\n" +
                            "    float: left;\n" +
                            "    text-align: center;\n" +
                            "    font-size: 13px;\n" +
                            "    color: #5E4227;\n" +
                            "    width: 50px;\n" +
                            "    height: 25px;\n" +
                            "    border: solid 1px #ddd;\n" +
                            "    margin: -20px 0px 5px 15px;\n" +
                            "    cursor: pointer;\n" +
                            "    font-family: 'Mehr Nastaliq Web' !important;\n" +
                            "    -moz-appearance: none;\n" +
                            "    -webkit-appearance: none;\n" +
                            "    appearance: none;\n" +
                            "}\n" +
                            "\n" +
                            "#contentarea .container #rightS_search ul {\n" +
                            "    float: left;\n" +
                            "    width: 100%;\n" +
                            "    height: 300px;\n" +
                            "    overflow: scroll;\n" +
                            "    padding: 0px;\n" +
                            "    margin: 0px 0px 0px 0px;\n" +
                            "}\n" +
                            "\n" +
                            "\n" +
                            "\n" +
                            "#contentarea .container #rightS_search ul li {\n" +
                            "    float: right;\n" +
                            "    width: 100%;\n" +
                            "    height: auto;\n" +
                            "    line-height: 20px;\n" +
                            "    font-size: 20px;\n" +
                            "    text-align: right;\n" +
                            "    background: url(../images/dot.png) center right no-repeat;\n" +
                            "    list-style: none;\n" +
                            "    padding: 5px 15px 0px 0px;\n" +
                            "    margin: 0px;\n" +
                            "    direction: rtl;\n" +
                            "}\n" +
                            "#contentarea .container #rightS_search ul li:hover {\n" +
                            "    background-color: #edeadb;\n" +
                            "    font-weight: bold;\n" +
                            "}\n" +
                            "\n" +
                            "#contentarea .container #right ul li a {\n" +
                            "    font-family: 'Mehr Nastaliq Web' !important;\n" +
                            "    font-size: 15px;\n" +
                            "    color: #5E4227;\n" +
                            "}\n" +
                            "\n" +
                            "#contentarea .container #rightS_search ul li a {\n" +
                            "    font-family: 'Mehr Nastaliq Web' !important;\n" +
                            "    font-size: 13px;\n" +
                            "    color: #5E4227;\n" +
                            "}\n" +
                            "#contentarea .container #rightS_search .tab {\n" +
                            "    width: 100%;\n" +
                            "    float: left;\n" +
                            "    background: #b6a08a;\n" +
                            "    font-size: 18px;\n" +
                            "    text-align: right;\n" +
                            "    color: #FFF;\n" +
                            "    padding: 4px 10px 4px 0px;\n" +
                            "    border-radius: 5px 5px 0px 0px;\n" +
                            "    direction: rtl;\n" +
                            "}\n" +
                            "#contentarea .container #right ul li:hover {\n" +
                            "    /*background-color: #edeadb;*/\n" +
                            "    font-weight: bold;\n" +
                            "}\n" +
                            ".searchboxcontainer {\n" +
                            "        width: 1170px;\n" +
                            "        height: auto;\n" +
                            "        margin: 0 auto;\n" +
                            "        padding: 30px 0px 30px 0px;\n" +
                            "    }\n" +
                            ".searchboxcontainer .txtSearch{\n" +
                            "    float:left;\n" +
                            "    display:inline-block;\n" +
                            "}\n" +
                            ".searchboxcontainer .btnSearch{\n" +
                            "    float:right;\n" +
                            "    display:inline-block;\n" +
                            "}\n" +
                            ".txtSearchNew {\n" +
                            "    font-family: 'Mehr Nastaliq Web' !important;\n" +
                            "    border: 1px solid #1d818a;\n" +
                            "    box-shadow: 0px 0px 1px #1d818a;\n" +
                            "    padding: 4px;\n" +
                            "    width: 750px;\n" +
                            "    height: 45px;\n" +
                            "    text-align: right;\n" +
                            "    margin-left:10px;\n" +
                            "    margin-right:0px;\n" +
                            "    float:right;\n" +
                            "}" +
                            "#heading-underline-marker:before { content: '\\00000A'; }"+

                            "\n</style>\n" +
                            "</head>\n" +
                            "<body style = \"padding:10px 10px 10px  10px\"  " +
                            ">\n<meta name=\"viewport\" content=\"width=device-width, initial-scale=1\">" +
                            str+
                            "</body>\n" +
                            "</html>");
                    // data.append("<span class=\"urdu-marker\"> جو یہاں نہیں ہیں۔<br/>\tیہاں یہ بات مزید سمجھ لیجیے کہ یہ صرف اُس وقت کی دنیا والوں کا معاملہ نہیں تھا۔ اب تو دائمی رسالتِ محمدی کا دَور ہے (علیٰ صاحبہا الصلوٰۃ والسلام)۔ اب تاقیامِ قیامت بنی نوعِ انسان کے لیے شہادت علی الناس کی ذمہ داری کون ادا کرے گا؟ یہ ذمہ داری اُمت محمد کی ہے (علیٰ صاحبہا الصلوٰۃ والسلام)۔ اگر اُمت یہ فریضہ ادا نہیں کرتی تو جان لیجیے کہ دنیا کی گمراہی کا وبال اُس کے سر آئے گا۔ آپ یہ سمجھتے ہیں کہ رسول اللہﷺ کا اُمتی ہونے سے آپ کو اﷲ تعالیٰ کے ہاں کوئی کریڈٹ مل جائے گا! میں آپ کو دوسرا رُخ دکھا رہا ہوں۔ یہ تو اتنی بڑی ذمہ داری ہے کہ اگر آپ اسے ادا نہیں کرتے تو دنیا کی ضلالت اور گمراہی کا وبال بھی آپ کے ذمہ آئے گا۔ بنی نوع انسان عدالت اُخروی میں یہ عذر پیش کرنے میں بڑی حد تک حق بجانب ہوں گے کہ اے اﷲ!ان کے پاس تیری آخری اور مکمل کتاب تھی‘ان کے پاس تیرا دین تھا‘یہ تیری شریعت کے علمبردار تھے‘یہ تیرے آخری نبی اور رسول کے اُمتی تھے‘انہوں نے اس دین کو نہ ہم تک پہنچایا اور نہ خود اس پر عمل کیا۔ یہ تیری آخری کتاب اور آخری نبیؐ‘ کی تعلیمات پر خزانے کے سانپ بن کر بیٹھے رہے۔<br/>\tمیں آپ کی نصح و خیر خواہی کا حق ادا نہیں کرپاؤں گا اگر میں آپ کو متنبہ نہ کردوں کہ اگر آپ کا طرز عمل یہ ہوگا تو آخرت میں لینے کے دینے پڑجائیں گے۔ خدارا مجھے بتائیے کہ اﷲ ربّ العزت کی عدالت میں جب ہم سے یہ سوال ہوگا کہ تم ہمارے آخری نبی و رسول جناب محمدﷺ کے اُمتی تھے‘تمہارے پاس ہمارا دین تھا‘تم حاملِ قرآن تھے‘ہم نے چینیوں اور امریکیوں کو اپنا دین نہیں دیا تھابلکہ اس کا وارث تم کو بنایا تھا اور یہ تمہاری ذمہ داری لگائی تھی کہ ان تک ہمارا دین پہنچاؤ‘ ہم نے محمدﷺ کو روسیوں میں مبعوث نہیں کیا تھا‘ان تک پہنچانا تمہارے ذمے تھا‘تو ہمارے پاس کیا جواب ہوگا؟ دوسروں تک دین نہ پہنچانے کی ذمہ داری ہم پر ہوگی یا نہیں؟ تو یہ ہے دوسری <br/>ذمہ داری جسے میں نے چار اصطلاحات کے حوالے سے بیان کیا ہے۔ اور ہمارا حال یہ  </span>");

                    pageWebView.loadDataWithBaseURL("file:///android_asset/", data .toString(), "text/html", "utf-8", null);
                }

//                for(int i=0;i<booksAllPages.size();i++) {
//                    if(pageDataModel.getPage_no() == booksAllPages.get(i)) {
//                        //seekbar.setTag("system");
//                        //seekbar.setProgress(i);
//                        break;
//                    }
//                }

//                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getApplicationContext(),android.R.layout.simple_spinner_dropdown_item,babListName);
//                spinner.setAdapter(arrayAdapter);
//                loadPage(0);
                babNameTv.setText(pageDataModel.getBaabName());
                bookname.setText(pageDataModel.getBookName());
            }
        }.execute();
        fontChnageBtn = findViewById(R.id.imageView33);
        fontChnageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFontDialogue();
            }
        });
    }

    public ArrayList<Integer> no_of_pages(int baabId)
    {
        ArrayList<Integer> list = new ArrayList<>();
        ArrayList<String> dbList = KutubDBHelperFile.getDatabasesList();
        for(int ij=-1;ij<dbList.size();ij++) {
            if (ij == -1) {
                PageDBHelper pageDBHelper = new PageDBHelper(getApplicationContext());
                SQLiteDatabase sqLiteDatabase = pageDBHelper.getReadableDatabase();
                Cursor cursor = sqLiteDatabase.rawQuery("SELECT * from " + DBHelper.PageEntry.TABLE_NAME + " WHERE " + DBHelper.PageEntry.BAAB_ID + " = " + baabId + " order by " + DBHelper.PageEntry.PAGE_NO, null);
                for (int i = 0; i < cursor.getCount(); i++) {
                    cursor.moveToNext();
                    list.add(cursor.getInt(cursor.getColumnIndexOrThrow(DBHelper.PageEntry.PAGE_NO)));
                }
                cursor.close();
            }else{
                KutubDBHelperFile pageDBHelper = new KutubDBHelperFile(getApplicationContext(),dbList.get(ij));
                SQLiteDatabase sqLiteDatabase = pageDBHelper.getWritableDatabase();
                Cursor cursor = sqLiteDatabase.rawQuery("SELECT * from " + DBHelper.PageEntry.TABLE_NAME + " WHERE " + DBHelper.PageEntry.BAAB_ID + " = " + baabId + " order by " + DBHelper.PageEntry.PAGE_NO, null);
                for (int i = 0; i < cursor.getCount(); i++) {
                    cursor.moveToNext();
                    list.add(cursor.getInt(cursor.getColumnIndexOrThrow(DBHelper.PageEntry.PAGE_NO)));
                }
                cursor.close();
            }
        }
        return list;
    }

    public int no_of_pages_in_bab(int baabId)
    {
        ArrayList<Integer> list = new ArrayList<>();
        PageDBHelper pageDBHelper =new PageDBHelper(getApplicationContext());
        SQLiteDatabase sqLiteDatabase = pageDBHelper.getReadableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT * from "+DBHelper.PageEntry.TABLE_NAME+" WHERE "+DBHelper.PageEntry.BAAB_ID+" = "+baabId+" order by "+DBHelper.PageEntry.PAGE_NO,null);
        return cursor.getCount();
    }

    int total = 0;
    public int no_of_pages_in_bab_full(int baabId,int position)
    {
        int returnValue = 0;
        ArrayList<String> dbList = KutubDBHelperFile.getDatabasesList();
        Log.d("tryDB",dbList.size()+"");
        for(int ij=-1;ij<dbList.size();ij++) {
            if (ij == -1) {
                PageDBHelper pageDBHelper = new PageDBHelper(getApplicationContext());
                SQLiteDatabase sqLiteDatabase = pageDBHelper.getReadableDatabase();
                int[] arr = new int[2];
                arr[0] = booksAllPages.size();
                Cursor cursor = sqLiteDatabase.rawQuery("SELECT " + DBHelper.PageEntry.PAGE_NO + " from " + DBHelper.PageEntry.TABLE_NAME + " WHERE " + DBHelper.PageEntry.BAAB_ID + " = " + baabId + " order by " + DBHelper.PageEntry.PAGE_NO, null);
                for (int i = 0; i < cursor.getCount(); i++) {
                    cursor.moveToNext();
                    booksAllPages.add(cursor.getInt(cursor.getColumnIndex(DBHelper.PageEntry.PAGE_NO)));
                }
                arr[1] = booksAllPages.size() - 1;
                Log.d("tryEachBefore",babLimit.size()+"");
                babLimit.add(arr);
                Log.d("tryEachAfter",babLimit.size()+"");
                if(cursor.getCount() != 0) {
                    returnValue = cursor.getCount();
                }
                cursor.close();
            }else{
                KutubDBHelperFile pageDBHelper = new KutubDBHelperFile(getApplicationContext(),dbList.get(ij));
                SQLiteDatabase sqLiteDatabase = pageDBHelper.getWritableDatabase();
                int[] arr = new int[2];
                arr[0] = booksAllPages.size();
                Cursor cursor = sqLiteDatabase.rawQuery("SELECT " + DBHelper.PageEntry.PAGE_NO + " from " + DBHelper.PageEntry.TABLE_NAME + " WHERE " + DBHelper.PageEntry.BAAB_ID + " = " + baabId + " order by " + DBHelper.PageEntry.PAGE_NO, null);
                for (int i = 0; i < cursor.getCount(); i++) {
                    cursor.moveToNext();
                    booksAllPages.add(cursor.getInt(cursor.getColumnIndex(DBHelper.PageEntry.PAGE_NO)));
                }
                arr[1] = booksAllPages.size() - 1;
                babLimit.add(arr);
                if(cursor.getCount() != 0) {
                    returnValue = cursor.getCount();
                }
                cursor.close();
            }
        }
        return returnValue;
    }

    ArrayList<Integer> booksAllPages;
    ArrayList<int[] > babLimit;
    public int getTotalBookPages(final int bookId)
    {
        total = 0;
        booksAllPages = new ArrayList<>();
        babLimit = new ArrayList<>();
        int returnValue = 0;
        ArrayList<String> dbList = KutubDBHelperFile.getDatabasesList();
        for(int ij=-1;ij<dbList.size();ij++) {
            if (ij == -1) {
                BaabDBHelper baabDBHelper = new BaabDBHelper(SearchPageViewActivity.this);
                SQLiteDatabase sqLiteDatabase = baabDBHelper.getReadableDatabase();
                Cursor cursor = sqLiteDatabase.rawQuery("SELECT * from " + DBHelper.BaabEntry.TABLE_NAME + " WHERE " + DBHelper.BaabEntry.BOOK_ID + " = " + bookId, null);
                Log.d("tryPages", cursor.getCount() + " SELECT * from " + DBHelper.BaabEntry.TABLE_NAME + " WHERE " + DBHelper.BaabEntry.BOOK_ID + " = " + bookId);
                for (int i = 0; i < cursor.getCount(); i++) {
                    cursor.moveToNext();
                    total += no_of_pages_in_bab_full(cursor.getInt(cursor.getColumnIndexOrThrow(DBHelper.BaabEntry.ID)), i);
                    Log.d("tryBablimit", total + "     " + babLimit.size());
                    Log.d("tryPages", total + " ");
                }
                Log.d("tryTotalPages", total + " ");
                cursor.close();
            } else {
                KutubDBHelperFile baabDBHelper = new KutubDBHelperFile(SearchPageViewActivity.this,dbList.get(ij));
                SQLiteDatabase sqLiteDatabase = baabDBHelper.getWritableDatabase();
                Cursor cursor = sqLiteDatabase.rawQuery("SELECT * from " + DBHelper.BaabEntry.TABLE_NAME + " WHERE " + DBHelper.BaabEntry.BOOK_ID + " = " + bookId, null);
                Log.d("tryPages", cursor.getCount() + " SELECT * from " + DBHelper.BaabEntry.TABLE_NAME + " WHERE " + DBHelper.BaabEntry.BOOK_ID + " = " + bookId);
                for (int i = 0; i < cursor.getCount(); i++) {
                    cursor.moveToNext();
                    total += no_of_pages_in_bab_full(cursor.getInt(cursor.getColumnIndexOrThrow(DBHelper.BaabEntry.ID)), i);

                }
                Log.d("tryTotalPages", total + " ");
                cursor.close();
            }
        }

        return total;
    }
    public void backBtn(View v)
    {
        onBackPressed();
    }

    Handler handler;
    public void loadBooksFromDB(final String query2)
    {
        new AsyncTask<Void,Void,Void>()
        {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                handler = new Handler();
                kitabDataModels = new ArrayList<>();
                kitabDataModelsID = new ArrayList<>();
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                booksSpinner.setAdapter(booksSpinnerAdapter);
                booksSpinnerAdapter.clear();
                booksSpinnerAdapter.addAll(kitabDataModels);
                booksSpinnerAdapter.notifyDataSetChanged();
                for(int i=0;i<kitabDataModelsID.size();i++){
                    Log.d("tryLog",pageDataModel.getBookId()+"   "+kitabDataModelsID.get(i));
                    if(pageDataModel.getBookId() == kitabDataModelsID.get(i)){
                        booksSpinner.setSelection(i);
                        break;
                    }
                }
            }

            @Override
            protected Void doInBackground(Void... voids) {

                ArrayList<String> dbList = KutubDBHelperFile.getDatabasesList();
                Log.d("tryDialogue","before loop");
                for(int ij=-1;ij<dbList.size();ij++) {
                    if(ij == -1) {
                        KitabDBHelper kitabDBHelper = new KitabDBHelper(getApplicationContext());
                        SQLiteDatabase sqLiteDatabase = kitabDBHelper.getReadableDatabase();
                        Cursor cursor = sqLiteDatabase.rawQuery(query2, null);
                        for (int i = 0; i < cursor.getCount(); i++) {
                            cursor.moveToNext();
                            if(cursor.getInt(cursor.getColumnIndex(DBHelper.KitabEntry.IS_ACTIVE)) == 1) {
                                Log.d("tryDialogue", "insideloop " + i);
                                kitabDataModelsID.add(cursor.getInt(cursor.getColumnIndex(DBHelper.KitabEntry.ID)));
                                kitabDataModels.add(cursor.getString(cursor.getColumnIndex(DBHelper.KitabEntry.NAME_URDU)));
                                // Log.d("tryCat", "val = " + kitabDataModel.getBookName());
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        booksSpinnerAdapter.notifyDataSetChanged();
                                    }
                                });
                            }
                        }
                        cursor.close();
                    }else{
                        KutubDBHelperFile kutubDBHelperFile = new KutubDBHelperFile(getApplicationContext(),dbList.get(ij));
                        SQLiteDatabase sqLiteDatabase = kutubDBHelperFile.getWritableDatabase();
                        Cursor cursor = sqLiteDatabase.rawQuery(query2, null);
                        for (int i = 0; i < cursor.getCount(); i++) {
                            cursor.moveToNext();
                            kitabDataModelsID.add(cursor.getInt(cursor.getColumnIndex(DBHelper.KitabEntry.ID)));
                            kitabDataModels.add(cursor.getString(cursor.getColumnIndex(DBHelper.KitabEntry.NAME_URDU)));
                            //Log.d("tryCat", "val = " + kitabDataModel.getBookName());
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    booksSpinnerAdapter.notifyDataSetChanged();
                                    //  dialog.invalidateOptionsMenu();
                                }
                            });
                        }
                        cursor.close();

                    }
                }
                return null;
            }

        }.execute();

    }

    public void loadBabsOfKutub(final int kitabId)
    {
        new AsyncTask<Void,Void,Void>()
        {
            String query = "";
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                handler = new Handler();
                baabsDataModels = new ArrayList<>();
                baabsDataModelsID = new ArrayList<>();
                query = "SELECT * from "+DBHelper.BaabEntry.TABLE_NAME+" WHERE "+DBHelper.BaabEntry.BOOK_ID+" = "+kitabId;
                Log.d("tryQuery",query);
            }
            @Override
            protected Void doInBackground(Void... voids) {
                ArrayList<String> dbList = KutubDBHelperFile.getDatabasesList();
                for(int ij=-1;ij<dbList.size();ij++) {
                    if (ij == -1) {
                        BaabDBHelper baabDBHelperr = new BaabDBHelper(getApplicationContext());
                        SQLiteDatabase sqLiteDatabase = baabDBHelperr.getReadableDatabase();
                        Cursor cursor = sqLiteDatabase.rawQuery(query, null);
                        for (int i = 0; i < cursor.getCount(); i++) {
                            cursor.moveToNext();
                            baabsDataModelsID.add(cursor.getInt(cursor.getColumnIndex(DBHelper.BaabEntry.ID)));
                            baabsDataModels.add(cursor.getString(cursor.getColumnIndex(DBHelper.BaabEntry.NAME)));
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    babSpinnerAdapter.notifyDataSetChanged();
                                }
                            });
                        }
                        cursor.close();
                    }else{
                        KutubDBHelperFile baabDBHelperr = new KutubDBHelperFile(getApplicationContext(),dbList.get(ij));
                        SQLiteDatabase sqLiteDatabase = baabDBHelperr.getWritableDatabase();
                        Cursor cursor = sqLiteDatabase.rawQuery(query, null);
                        for (int i = 0; i < cursor.getCount(); i++) {
                            cursor.moveToNext();
                            baabsDataModelsID.add(cursor.getInt(cursor.getColumnIndex(DBHelper.BaabEntry.ID)));
                            baabsDataModels.add(cursor.getString(cursor.getColumnIndex(DBHelper.BaabEntry.NAME)));
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    babSpinnerAdapter.notifyDataSetChanged();
                                }
                            });
                        }
                        cursor.close();
                    }
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                babSpinnerAdapter.clear();
                babSpinnerAdapter.addAll(baabsDataModels);
                babSpinnerAdapter.notifyDataSetChanged();
                Log.d("tryDItem",baabsDataModelsID.size()+"");
                for(int i=0;i<baabsDataModelsID.size();i++){
                    Log.d("tryDItem",pageDataModel.getPageBaabId()+"   "+ baabsDataModelsID.get(i));
                    if(pageDataModel.getPageBaabId() == baabsDataModelsID.get(i)){
                        babSpinner.setSelection(i);
                        break;
                    }
                }
                //  babSpinner.setSelection(currentlySelectedBab);
                // babSpinner.setAdapter(babSpinnerAdapter);

            }
        }.execute();
    }
    public int fetchCategory(int kitabId){
        int categoryId = -1;
        String query2 = "SELECT * FROM "+DBHelper.KitabEntry.TABLE_NAME+" WHERE "+DBHelper.KitabEntry.ID+"="+kitabId;
        ArrayList<String> dbList = KutubDBHelperFile.getDatabasesList();
        for(int ij=-1;ij<dbList.size();ij++) {
            if(ij == -1) {
                KitabDBHelper kitabDBHelper = new KitabDBHelper(getApplicationContext());
                SQLiteDatabase sqLiteDatabase = kitabDBHelper.getReadableDatabase();
                Cursor cursor = sqLiteDatabase.rawQuery(query2, null);
                for (int i = 0; i < cursor.getCount(); i++) {
                    cursor.moveToNext();
                    categoryId = cursor.getInt(cursor.getColumnIndex(DBHelper.KitabEntry.CATEGORY_ID));
                }
                cursor.close();
            }else{
                KutubDBHelperFile kutubDBHelperFile = new KutubDBHelperFile(getApplicationContext(),dbList.get(ij));
                SQLiteDatabase sqLiteDatabase = kutubDBHelperFile.getWritableDatabase();
                Cursor cursor = sqLiteDatabase.rawQuery(query2, null);
                for (int i = 0; i < cursor.getCount(); i++) {
                    cursor.moveToNext();
                    categoryId = cursor.getInt(cursor.getColumnIndex(DBHelper.KitabEntry.CATEGORY_ID));
                }
                cursor.close();

            }
        }
        return  categoryId;
    }

    @Override
    public void onBackPressed() {
        if(dialogueLayout.getVisibility() == View.VISIBLE){
            dialogueLayout.setVisibility(GONE);
        }else{
            super.onBackPressed();
        }
    }

    ArrayList<Cursor> universalCursors;
    public void fetchSearhResultCursors(final String searchWord)
    {
        universalCursors = new ArrayList<>();
        String query = ("SELECT * FROM " + DBHelper.PageEntry.TABLE_NAME + " where " + DBHelper.PageEntry.DETAILS_WEB+" LIKE '%"+searchWord+"%'");

        ArrayList<String> dbList = KutubDBHelperFile.getDatabasesList();
        int totalResultCount = 0;
        int flag = 0;
        for(int ij=-1;ij<dbList.size();ij++) {
            if (ij == -1) {
                PageDBHelper pageDBHelper = new PageDBHelper(getApplicationContext());
                SQLiteDatabase sqLiteDatabase = pageDBHelper.getReadableDatabase();
                Cursor ctemp = sqLiteDatabase.rawQuery(query, null);
                totalResultCount+=ctemp.getCount();
                universalCursors.add(ctemp);
            }else {
                KutubDBHelperFile pageDBHelper = new KutubDBHelperFile(getApplicationContext(),dbList.get(ij));
                SQLiteDatabase sqLiteDatabase = pageDBHelper.getWritableDatabase();
                Cursor ctemp = sqLiteDatabase.rawQuery(query, null);
                totalResultCount+=ctemp.getCount();
                universalCursors.add(ctemp);

            }

        }
        totalPagesTV.setText(totalResultCount+"");
        total = totalResultCount;

    }

    public void fetchPageBackground(int babiD){
        PageDBHelper pageDBHelper = new PageDBHelper(getApplicationContext());
        SQLiteDatabase sqLiteDatabase = pageDBHelper.getReadableDatabase();
        Cursor bcursor = sqLiteDatabase.rawQuery("select * from "+DBHelper.BaabEntry.TABLE_NAME+" where "+DBHelper.BaabEntry.ID+" = "+babiD, null);
        bcursor.moveToNext();
        pageDataModel.setBaabName(bcursor.getString(bcursor.getColumnIndexOrThrow(DBHelper.BaabEntry.NAME)));
        pageDataModel.setBookId(bcursor.getInt(bcursor.getColumnIndexOrThrow(DBHelper.BaabEntry.BOOK_ID)));
        bcursor.close();

        KitabDBHelper kitabDBHelper = new KitabDBHelper(getApplicationContext());
        SQLiteDatabase sqLiteDatabase2 = kitabDBHelper.getReadableDatabase();
        Cursor kcursor = sqLiteDatabase2.rawQuery("select * from "+DBHelper.KitabEntry.TABLE_NAME+" where "+DBHelper.KitabEntry.ID+" = "+pageDataModel.getBookId(), null);
        kcursor.moveToNext();
        pageDataModel.setBookName(kcursor.getString(kcursor.getColumnIndexOrThrow(DBHelper.KitabEntry.NAME_URDU)));
        kcursor.close();
    }


    public void showFontDialogue()
    {
        final Dialog dialog = new Dialog(SearchPageViewActivity.this);
        dialog.setContentView(R.layout.font_slider_dialogue);;
        final SeekBar seekBar = dialog.findViewById(R.id.seekBar3);

        seekBar.setMax(170);
        //Log.d("tryFont",pageWebView.getTextSize()+"");
        webSettings = pageWebView.getSettings();
        webSettings.setDefaultFontSize(10);
        if(pref.getFloat(Constants.FONT,-1) != -1) {
            seekBar.setProgress((int) pref.getFloat(Constants.FONT,webSettings.getDefaultFontSize())-50);
        }else{
            seekBar.setProgress((int) webSettings.getDefaultFontSize());
        }



        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                float newfontsize = seekBar.getProgress()+50;
                pref.edit().putFloat(Constants.FONT,newfontsize).apply();
                //   webSettings.setDefaultFontSize((int) newfontsize);
                webSettings.setTextZoom((int) newfontsize);
                bookname.setTextSize((newfontsize + 150)/10);
                babNameTv.setTextSize((newfontsize + 100)/10);

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        dialog.show();
    }
}
