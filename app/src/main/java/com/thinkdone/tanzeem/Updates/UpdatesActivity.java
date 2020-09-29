package com.thinkdone.tanzeem.Updates;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.thinkdone.tanzeem.BookMarks.BookMarksActivity;
import com.thinkdone.tanzeem.Categories.CategoriesActivity;
import com.thinkdone.tanzeem.DBFiles.KutubDBHelperFile;
import com.thinkdone.tanzeem.DataModels.KitabDataModel;
import com.thinkdone.tanzeem.Database;
import com.thinkdone.tanzeem.Drawer.About_us;
import com.thinkdone.tanzeem.Kutub.KutubListActivity;
import com.thinkdone.tanzeem.MainActivity;
import com.thinkdone.tanzeem.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;


public class UpdatesActivity extends AppCompatActivity {

    RecyclerView kutubRecycler;
    BookDownloadAdapter bookDownloadAdapter;
    //https://api.myjson.com/bins/hdjgy

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
    private static final float END_SCALE = 0.7f;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nav_activity_updates);

        kutubRecycler = findViewById(R.id.recycler);
        GridLayoutManager linearLayoutManager = new GridLayoutManager(getApplicationContext(),3);
        kutubRecycler.setLayoutManager(linearLayoutManager);

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.bottomNavigationView);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        Menu menu = navigation.getMenu();
        MenuItem menuItem = menu.getItem(0);
        menuItem.setChecked(true);

        bookDownloadAdapter = new BookDownloadAdapter(getApplicationContext(),UpdatesActivity.this);
        kutubRecycler.setAdapter(bookDownloadAdapter);
        //KitabDataModel kitabDataModel = new KitabDataModel();
        //kitabDataModel.setBookName("قرآن حکیم اور ہمار ی زندگی");
        //kitabDataModel.setBookImage("http://tanzeemdigitallibrary.com//BookImages/infardi_nijat_or_quran_ka_laiha_amal.jpg");
        //kitabDataModel.setDownloadLink("https://cdn.fbsbx.com/v/t59.2708-21/72985947_2583352771753623_4527044878881783808_n.db/FirstBook.db?_nc_cat=100&_nc_oc=AQlbNLWlrVbCa0Rb38y6jYKQ1HyAUAWW76lOItpKknRLVceKxTN9lbWVRnUZtjIHr-Q&_nc_ht=cdn.fbsbx.com&oh=ee92297538de829b507e38e3aade0bf3&oe=5DD4BF82&dl=1&fbclid=IwAR1Lyi-ZobWG9h8xeyw61R0c1njqXbOMZ5dpPd3whPuxjFQ1-WjPI62_3M4");
        //bookDownloadAdapter.addKitab(kitabDataModel);
        bookDownloadAdapter.notifyDataSetChanged();

        Log.d("tryDownloadFile",Database.getDatabaseFolder()+Database.DATABASE_NAME+".db");
        TextView downloadDB = findViewById(R.id.textView8);
        downloadDB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
//                    Toast.makeText(getApplicationContext(), "Sd card not found", Toast.LENGTH_LONG).show();
//                    finish();
//                }
                Log.d("tryDB","my DB Background");
//                mDatabaseOpenTask = new DatabaseOpenTask();
//                mDatabaseOpenTask.execute(new Context[] { UpdatesActivity.this });

//                mDatabaseOpenTask = new DatabaseOpenTask();
//                mDatabaseOpenTask.execute(new Context[] { UpdatesActivity.this });
                mDatabaseDownloadTask = new DatabaseDownloadTask();
                mDatabaseDownloadTask.execute(new Context[]{UpdatesActivity.this});
            }
        });

        handler = new Handler();
        loadData();

        //TODO navigation drawer
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                int id = menuItem.getItemId();

                if (id == R.id.homed) {
                    Intent intent = new Intent(UpdatesActivity.this, MainActivity.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
                } else if (id == R.id.aboutd) {
                    Intent intent = null;
                    intent = new Intent(UpdatesActivity.this, About_us.class);
                    intent.putExtra("title","کچھ ہمارے بارے میں");
                    intent.putExtra("type", "about");
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
                } else if (id == R.id.booksd) {
                    Intent intent = new Intent(UpdatesActivity.this, KutubListActivity.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
                } else if (id == R.id.tawund) {
                    Intent intent = null;
                    intent = new Intent(UpdatesActivity.this, About_us.class);
                    intent.putExtra("title","تعاون");
                    intent.putExtra("type", "tawun");
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
                } else if (id == R.id.rabtad) {
                    Intent intent = null;
                    intent = new Intent(UpdatesActivity.this, About_us.class);
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
        final ImageView layout = findViewById(R.id.imageView);
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

        final ImageView layoutd = findViewById(R.id.imageView);
        layoutd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawer.openDrawer(GravityCompat.START);
            }
        });
    }

    HttpURLConnection urlConnection = null;
    BufferedReader reader = null;
    // Will contain the raw JSON response as a string.
    String forecastJsonStr = null;
    Handler handler;
    ArrayList<String> existingFiles;
   public void loadData()
   {
       new AsyncTask<Void,Void,Void>()
       {
           @Override
           protected void onPreExecute() {
               super.onPreExecute();
               existingFiles = KutubDBHelperFile.getDatabasesList();
           }
//+"https://api.myjson.com/bins/16t77e"
           @Override
           protected Void doInBackground(Void... voids) {
               try {
                   URL url = new URL("https://api.myjson.com/bins/azn92");
                   urlConnection = (HttpURLConnection) url.openConnection();
                   urlConnection.setRequestMethod("GET");
                   urlConnection.connect();
                   int lengthOfFile = urlConnection.getContentLength();
                   // Read the input stream into a String
                   InputStream inputStream = urlConnection.getInputStream();
                   StringBuffer buffer = new StringBuffer();
                   if (inputStream == null) {

                   }
                   reader = new BufferedReader(new InputStreamReader(inputStream));

                   String line;
                   while ((line = reader.readLine()) != null) {
                       buffer.append(line + "\n");
                   }

                   if (buffer.length() == 0) {

                   }
                   forecastJsonStr = buffer.toString();
                   Log.e("Json1", forecastJsonStr);


                   //TODO string
                   JSONArray jsonArray = new JSONObject(forecastJsonStr).getJSONArray("books");
                   for (int i = 0; i < jsonArray.length(); i++) {
                       JSONObject jsonObject = jsonArray.getJSONObject(i);
                       final KitabDataModel kitabDataModel = new KitabDataModel();
                       kitabDataModel.setBookImage(jsonObject.getString("image"));
                       kitabDataModel.setBookName(jsonObject.getString("name"));
                       kitabDataModel.setFileName(jsonObject.getString("file_name"));
                       kitabDataModel.setDownloadLink(jsonObject.getString("link"));
                       Log.d("File",jsonObject.getString("file_name")+" "+existingFiles.contains(kitabDataModel.getBookName()));
                       handler.post(new Runnable() {
                           @Override
                           public void run() {
                               bookDownloadAdapter.addKitab(kitabDataModel);
                               bookDownloadAdapter.notifyDataSetChanged();

                           }
                       });
                   }


               } catch (IOException e) {
                   Log.e("PlaceholderFragment", "Error ", e);
               } catch (JSONException e) {
                   e.printStackTrace();
               } finally {
                   if (urlConnection != null) {
                       urlConnection.disconnect();
                   }
                   if (reader != null) {
                       try {
                           reader.close();
                       } catch (final IOException e) {
                           Log.e("PlaceholderFragment", "Error closing stream", e);
                       }
                   }
               }
               return null;
           }
           @Override
           protected void onPostExecute(Void aVoid) {
               super.onPostExecute(aVoid);
           }

       }.execute();


   }
    private static final String SD_CARD_FOLDER = "MyApp";
    private static final String DB_DOWNLOAD_PATH = "https://img.raymond.cc/blog/wp-content/uploads/2011/02/rapidgator_download.png";//"https://www.dropbox.com/s/49yvcios1e7dn55/2018-09-24%2020.10.17.jpg?dl=0";//"https://www.dropbox.com/s/7na222lb2l8ldf7/UrduFatwa.db?dl=0";
    private Database mDB = null;
    private DatabaseDownloadTask mDatabaseDownloadTask = null;
    private DatabaseOpenTask mDatabaseOpenTask = null;
    ProgressDialog mProgressDialog;

    private class DatabaseDownloadTask extends AsyncTask<Context, Integer, Boolean> {

        @Override
        protected void onPreExecute() {
            mProgressDialog = new ProgressDialog(UpdatesActivity.this);
            mProgressDialog.setTitle("Starting download");
            mProgressDialog.setMessage("It will take some time");
            mProgressDialog.setIndeterminate(false);
            mProgressDialog.setMax(100);
            mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            mProgressDialog.setCancelable(false);
            mProgressDialog.show();
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }

        @Override
        protected Boolean doInBackground(Context... params) {
            InputStream input = null;
            OutputStream output = null;
            HttpURLConnection connection = null;
            try {
                URL url = new URL(DB_DOWNLOAD_PATH);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                // expect HTTP 200 OK, so we don't mistakenly save error report
                // instead of the file
                if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                    /*return "Server returned HTTP " + connection.getResponseCode()
                            + " " + connection.getResponseMessage();*/
                }

                // this will be useful to display download percentage
                // might be -1: server did not report the length
                int fileLength = connection.getContentLength();

                // download the file
                input = connection.getInputStream();
                output = new FileOutputStream(Database.getDatabaseFolder()+"file_name2.png");

                byte data[] = new byte[4096];
                long total = 0;
                int count;
                while ((count = input.read(data)) != -1) {
                    // allow canceling with back button
                    if (isCancelled()) {
                        input.close();
                        return null;
                    }
                    total += count;
                    // publishing the progress....
                    if (fileLength > 0) // only if total length is known
                        publishProgress((int) (total * 100 / fileLength));
                    output.write(data, 0, count);

                }
                if (output != null)
                    output.close();
                if (input != null)
                    input.close();
                return true;
            } catch (Exception e) {
                return false;
            } finally {
                try {
                    if (output != null)
                        output.close();
                    if (input != null)
                        input.close();
                } catch (IOException ignored) {
                }

                if (connection != null)
                    connection.disconnect();
            }

        }

        protected void onProgressUpdate(Integer... values) {
            if (mProgressDialog != null) {
                if (mProgressDialog.isShowing()) {
                    mProgressDialog.setProgress(values[0]);
                }
            }
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if (mProgressDialog != null) {
                mProgressDialog.dismiss();
                mProgressDialog = null;
            }
            if (result.equals(Boolean.TRUE)) {
                Toast.makeText(UpdatesActivity.this, "Opening", Toast.LENGTH_LONG).show();
//                mDatabaseOpenTask = new DatabaseOpenTask();
//                mDatabaseOpenTask.execute(new Context[] { UpdatesActivity.this });
            }
            else {
                Toast.makeText(getApplicationContext(), "Download Failed", Toast.LENGTH_LONG).show();
                finish();
            }
        }

    }

    private class DatabaseOpenTask extends AsyncTask<Context, Void, Database> {

        @Override
        protected Database doInBackground(Context ... ctx) {
            try {
                Log.d("tryDB","in do In Background");
//                String externalBaseDir = Environment.getExternalStorageDirectory().getAbsolutePath();
//                // DELETE OLD DATABASE ANFANG
//                File oldFolder = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/"+SD_CARD_FOLDER);
//                File oldFile = new File(oldFolder, "dictionary.db");
//                if (oldFile.exists()) {
//                    oldFile.delete();
//                }
//                if (oldFolder.exists()) {
//                    oldFolder.delete();
//                }
                // DELETE OLD DATABASE ENDE
                Toast.makeText(UpdatesActivity.this, "UrduFatwa.db", Toast.LENGTH_SHORT).show();
                File newDB = new File(Database.getDatabaseFolder()+"UrduFatwa.db");
                Log.d("tryDB","is DB exisits "+newDB.exists());
                if (newDB.exists()) {
                    return new Database(ctx[0]);
                }
                else {
                    return null;
                }
            }
            catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(UpdatesActivity.this, "exception", Toast.LENGTH_SHORT).show();
                return null;
            }
        }

        @Override
        protected void onPreExecute() {
            Toast.makeText(UpdatesActivity.this, "in ptre", Toast.LENGTH_SHORT).show();
            Log.d("tryDB","in pre");
            mProgressDialog = ProgressDialog.show(UpdatesActivity.this, "please_wait", "Loading the database! This may take some time ...", true);
        }

        @Override
        protected void onPostExecute(Database newDB) {
            Toast.makeText(UpdatesActivity.this, "in post"+ newDB, Toast.LENGTH_SHORT).show();
            if (mProgressDialog != null) {
                mProgressDialog.dismiss();
                mProgressDialog = null;
            }
            if (newDB == null) {
                mDB = null;
                AlertDialog.Builder downloadDatabase = new AlertDialog.Builder(UpdatesActivity.this);
                downloadDatabase.setTitle("download Database");
                downloadDatabase.setCancelable(false);
                downloadDatabase.setMessage("want To Download Database Now");
                downloadDatabase.setPositiveButton("download", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        mDatabaseDownloadTask = new DatabaseDownloadTask();
                        mDatabaseDownloadTask.execute();
                    }
                });
                downloadDatabase.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        finish();
                    }
                });
                downloadDatabase.show();
            }
            else {
                mDB = newDB;
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mDatabaseDownloadTask != null) {
            if (mDatabaseDownloadTask.getStatus() != AsyncTask.Status.FINISHED) {
                mDatabaseDownloadTask.cancel(true);
            }
        }
        if (mDatabaseOpenTask != null) {
            if (mDatabaseOpenTask.getStatus() != AsyncTask.Status.FINISHED) {
                mDatabaseOpenTask.cancel(true);
            }
        }
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
            mProgressDialog = null;
        }
        if (mDB != null) {
            mDB.close();
            mDB = null;
        }
    }
}
