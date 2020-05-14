package com.michaelzap94.santabiblia;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.michaelzap94.santabiblia.DatabaseHelper.BibleDBHelper;
import com.michaelzap94.santabiblia.adapters.SearchResultsRecyclerView;
import com.michaelzap94.santabiblia.adapters.VersesRecyclerViewAdapter;
import com.michaelzap94.santabiblia.models.Book;
import com.michaelzap94.santabiblia.utilities.BookHelper;

import java.util.ArrayList;

public class SearchSpecific extends AppCompatActivity {
    private static final String TAG = "SearchSpecific";
    private int _id;
    private String title;
    private String type;
    private SearchView searchView;
    private TextView resultsCounter;
    private RecyclerView rvView;
    private SearchResultsRecyclerView rvAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_specific);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            this._id = extras.getInt("id");
            switch (this._id){
                case R.id.search_card_conc:
                    title = getResources().getString(R.string.search_card_conc_title);
                    type = "conc";
                    break;
                case R.id.search_card_dict:
                    title = getResources().getString(R.string.search_card_dict_title);
                    type = "dict";
                    break;
                case R.id.search_card_label: title = getResources().getString(R.string.search_card_label_title);
                    break;
                case R.id.search_card_text: title = getResources().getString(R.string.search_card_text_title);
                    break;
                default: title = "No card found";
                    break;
            }
        }
        ActionBar ab = getSupportActionBar();
        ab.setTitle(title);
        ab.setDisplayHomeAsUpEnabled(true);
        searchView = (SearchView) findViewById(R.id.search_bar);
        resultsCounter = (TextView) findViewById(R.id.search_results_counter);

        //=========================================================================================
        this.rvView = (RecyclerView) findViewById(R.id.search_results_recyclerview);
        rvView.setLayoutManager(new LinearLayoutManager(this));
        rvAdapter = new SearchResultsRecyclerView(new ArrayList<String[]>());
        rvView.setAdapter(rvAdapter);//attach the RecyclerView adapter to the RecyclerView View
        //=========================================================================================
        searchView.clearFocus();
        // perform set on query text listener event
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                ArrayList<String[]> results = BibleDBHelper.getInstance(SearchSpecific.this).searchInConcordanceOrDictionary(query, type);
                resultsCounter.setText("Results: " + results.size());
                rvAdapter.refreshSearchResults(results);
                searchView.clearFocus();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // do something when text changes
                return false;
            }
        });

    }
}
