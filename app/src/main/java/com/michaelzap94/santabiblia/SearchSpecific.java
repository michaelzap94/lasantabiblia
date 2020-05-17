package com.michaelzap94.santabiblia;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.michaelzap94.santabiblia.DatabaseHelper.BibleDBHelper;
import com.michaelzap94.santabiblia.adapters.SearchResultsRecyclerView;
import com.michaelzap94.santabiblia.adapters.VersesRecyclerViewAdapter;
import com.michaelzap94.santabiblia.models.Book;
import com.michaelzap94.santabiblia.models.SearchResult;
import com.michaelzap94.santabiblia.utilities.BookHelper;
import com.michaelzap94.santabiblia.viewmodel.SearchResultsViewModel;

import java.util.ArrayList;

public class SearchSpecific extends AppCompatActivity {
    private static final String TAG = "SearchSpecific";
    private int _id;
    private String title;
    private String type;
    private SearchView searchView;
    private TextView resultsCounter;
    ///////////////////////////////////////////////////////////
    private ProgressBar loadingView;
    private ArrayList<SearchResult> results = new ArrayList();
    private RecyclerView rvView;
    private SearchResultsViewModel viewModel;
    //Instantiate the RecyclerViewAdapter, passing an empty list initially.
    // this data will not be shown until you setAdapter to the RecyclerView view in the Layout
    private SearchResultsRecyclerView rvAdapter;
    //==========================================================

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
                case R.id.search_card_text:
                    title = getResources().getString(R.string.search_card_text_title);
                    type = "bible";
                    break;
                case R.id.search_card_label:
                    title = getResources().getString(R.string.search_card_label_title);
                    type = "notes";
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
        loadingView = (ProgressBar) findViewById(R.id.search_results_spinner);
        //=========================================================================================
        // get viewmodel class and properties, pass this context so LifeCycles are handled by ViewModel,
        // in case the Activity is destroyed and recreated(screen roation)
        //ViewModel will help us show the exact same data, and resume the application from when the user left last time.
        viewModel = new ViewModelProvider(this).get(SearchResultsViewModel.class);

        //=========================================================================================
        this.rvView = (RecyclerView) findViewById(R.id.search_results_recyclerview);
        rvView.setLayoutManager(new LinearLayoutManager(this));
        rvAdapter = new SearchResultsRecyclerView(new ArrayList<>());
        rvView.setAdapter(rvAdapter);//attach the RecyclerView adapter to the RecyclerView View
        //=========================================================================================
        searchView.clearFocus();
        // perform set on query text listener event
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                //ArrayList<SearchResult> results = BibleDBHelper.getInstance(SearchSpecific.this).searchInConcordanceOrDictionary(query, type);
                //Use when we need to reload data
                viewModel.fetchData(query, type);//load data
                searchView.clearFocus();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // do something when text changes
                return false;
            }
        });

        ///////////////////////////////////////
        observerViewModel();
    }

    private void observerViewModel() {
        viewModel.getUserMutableLiveData().observe(this, (results) -> {
            Log.d(TAG, "observerViewModel: results: " + results.size());
            //WHEN data is created  pass data and set it in the recyclerview VIEW
            resultsCounter.setText("Results: " + results.size());
            rvAdapter.refreshSearchResults(results);
            rvView.setVisibility(View.VISIBLE);
        });

        viewModel.loading.observe(this, isLoading -> {
            if(isLoading != null) {
                loadingView.setVisibility(isLoading ? View.VISIBLE : View.GONE);
                if(isLoading) {
                    resultsCounter.setText("Loading...");
                    rvView.setVisibility(View.GONE);
                }
            }
        });
    }
}
