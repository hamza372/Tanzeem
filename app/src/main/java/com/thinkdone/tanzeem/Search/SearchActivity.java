package com.thinkdone.tanzeem.Search;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.thinkdone.tanzeem.Constants;
import com.thinkdone.tanzeem.R;

public class SearchActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        final EditText searchbar = findViewById(R.id.editTextse);
        ImageView search  = findViewById(R.id.imageView10);
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SearchActivity.this, PageSearchActivity.class);
                intent.putExtra(Constants.SEARCH,searchbar.getText().toString());
                startActivity(intent);
            }
        });
    }
}
