package com.michaelzap94.santabiblia;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;

import com.michaelzap94.santabiblia.models.Book;
import com.michaelzap94.santabiblia.utilities.BookHelper;

public class SearchSpecific extends AppCompatActivity {

    private int _id;
    private String title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_specific);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            this._id = extras.getInt("id");
            switch (this._id){
                case R.id.search_card_conc: title = getResources().getString(R.string.search_card_conc_title);
                    break;
                case R.id.search_card_dict: title = getResources().getString(R.string.search_card_dict_title);
                    break;
                case R.id.search_card_label: title = getResources().getString(R.string.search_card_label_title);
                    break;
                case R.id.search_card_text: title = getResources().getString(R.string.search_card_text_title);
                    break;
                default: title = "No card found";
                    break;
            }
        }
        // my_child_toolbar is defined in the layout file
//        Toolbar myChildToolbar = (Toolbar) findViewById(R.id.my_child_toolbar);
//        setSupportActionBar(myChildToolbar);
        // Get a support ActionBar corresponding to this toolbar
        ActionBar ab = getSupportActionBar();
        ab.setTitle(title);
        // Enable the Up button
        ab.setDisplayHomeAsUpEnabled(true);
    }
}
