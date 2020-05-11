package com.thinkdone.tanzeem;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.Layout;
import android.text.SpannableString;
import android.text.style.AlignmentSpan;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;

import com.thinkdone.tanzeem.BookMarks.BookMarksActivity;
import com.thinkdone.tanzeem.Categories.CategoriesActivity;
import com.thinkdone.tanzeem.Drawer.About_us;
import com.thinkdone.tanzeem.Kutub.KutubListActivity;
import com.thinkdone.tanzeem.Search.PageSearchActivity;
import com.thinkdone.tanzeem.Updates.UpdatesActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity {

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
                case R.id.download:
                    Intent intent = new Intent(activity, UpdatesActivity.class);
                    startActivity(intent);
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
        setContentView(R.layout.nav_activity_main);


//        postponeEnterTransition();
//        final View sharedview = findViewById(R.id.imageView21);
//        sharedview.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
//            @Override
//            public boolean onPreDraw() {
//                // Tell the framework to start.
//                sharedview.getViewTreeObserver().removeOnPreDrawListener(this);
//                MainActivity.this.startPostponedEnterTransition();
//                return true;
//            }
//        });


        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.bottomNavigationView);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        Menu menu = navigation.getMenu();
        MenuItem menuItem = menu.getItem(4);
        menuItem.setChecked(true);

        //TODO navigation drawer
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                int id = menuItem.getItemId();

                if (id == R.id.homed) {
                    Intent intent = new Intent(MainActivity.this, MainActivity.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
                } else if (id == R.id.aboutd) {
                    Intent intent = null;
                    intent = new Intent(MainActivity.this, About_us.class);
                    intent.putExtra("title","کچھ ہمارے بارے میں");
                    intent.putExtra("type", "about");
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
                } else if (id == R.id.booksd) {
                    Intent intent = new Intent(MainActivity.this, KutubListActivity.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
                } else if (id == R.id.tawund) {
                    Intent intent = null;
                    intent = new Intent(MainActivity.this, About_us.class);
                    intent.putExtra("title","تعاون");
                    intent.putExtra("type", "tawun");
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
                } else if (id == R.id.rabtad) {
                    Intent intent = null;
                    intent = new Intent(MainActivity.this, About_us.class);
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
        final ImageView layout = findViewById(R.id.imageView19);
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
        drawer.setStatusBarBackgroundColor(Color.BLACK);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(Color.parseColor("#292118"));
        }

//        //TODO Kutub Activity
//        CardView kutubCard = findViewById(R.id.card3);
//        kutubCard.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startActivity(new Intent(activity, KutubListActivity.class));
//                overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
//            }
//        });
//
//        //TODO important Kutub Activity
//        CardView important = findViewById(R.id.card3);
//        important.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startActivity(new Intent(activity, KutubListActivity.class));
//                overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
//            }
//        });
//
//        //TODO updates Activity
//        CardView updates = findViewById(R.id.card5);
//        updates.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startActivity(new Intent(activity, UpdatesActivity.class));
//                overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
//            }
//        });
//
//        //TODO topic Activity
//        CardView topicCard = findViewById(R.id.card6);
//        topicCard.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startActivity(new Intent(activity, CategoriesActivity.class));
//                overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
//            }
//        });


        final EditText searchbar = findViewById(R.id.editText2);
        final ImageView search  = findViewById(R.id.imageView41);
        search.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onClick(View v) {
                ActivityOptionsCompat activityOptionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(MainActivity.this,search,"sh");

                // Intent intent = new Intent(MainActivity.this, SearchActivity.class);
               // intent.putExtra(Constants.SEARCH,searchbar.getText().toString());
               // startActivity(intent);
                if (!searchbar.getText().toString().equals("")) {
                    Intent intent = new Intent(MainActivity.this, PageSearchActivity.class);
                    intent.putExtra(Constants.SEARCH, searchbar.getText().toString());
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        startActivity(intent);
                    }
                    overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
                }
            }
        });

        ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE},1);

    }


}
