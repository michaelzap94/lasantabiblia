package com.michaelzap94.santabiblia.viewmodel;

import android.app.Application;
import android.os.AsyncTask;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.michaelzap94.santabiblia.DatabaseHelper.BibleDBHelper;
import com.michaelzap94.santabiblia.SearchSpecific;
import com.michaelzap94.santabiblia.models.SearchResult;

import java.util.ArrayList;

public class SearchResultsViewModel extends AndroidViewModel {

        private static final String TAG = "SearchResultsViewModel";

        private MutableLiveData<ArrayList<SearchResult>> searchResultsList;

        public SearchResultsViewModel(@NonNull Application application) {
            super(application);
            this.searchResultsList = new MutableLiveData<ArrayList<SearchResult>>();
            //fetchData(this.book_number, this.chapter_number);
        }
        public MutableLiveData<ArrayList<SearchResult>> getUserMutableLiveData() {
            return searchResultsList;
        }
        //PUBLIC SO we can refresh the list for some reason from the OUTSIDE
        public void refreshsearchResultsList(String title, String type){
            fetchData(title, type);
        }

        public void fetchData(String title, String type){
            loadVerses(title, type);
        }
        public void loadVerses(String title, String type){new SearchResultsViewModel.GetVersesMarked().execute(title, type);}
        private class GetVersesMarked extends AsyncTask<String, Void, Void> {
            //get data and populate the list
            protected Void doInBackground(String... args) {
                Log.d(TAG, "doInBackground: " + args[0]);
                ArrayList<SearchResult> results = BibleDBHelper.getInstance(getApplication()).searchInConcordanceOrDictionary(args[0], args[1]);
                Log.d(TAG, "doInBackground: result " + results.size());
                searchResultsList.postValue(results);
                return null;
            }
        }

    
}