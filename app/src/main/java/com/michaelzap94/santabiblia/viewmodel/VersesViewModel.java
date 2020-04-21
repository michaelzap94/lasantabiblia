package com.michaelzap94.santabiblia.viewmodel;

import android.app.Application;
import android.os.AsyncTask;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.michaelzap94.santabiblia.DatabaseHelper.BibleDBHelper;
import com.michaelzap94.santabiblia.models.Verse;

import java.util.ArrayList;

public class VersesViewModel extends AndroidViewModel {

    private static final String TAG = "VersesViewModel";

    private MutableLiveData<ArrayList<Verse>> versesList;
    private int book_id;
    private int chapter_number;

//    public VersesViewModel(@NonNull Application application, int... args) {
//        super(application);
//        this.versesList = new MutableLiveData<ArrayList<Verse>>();
//        this.book_id = args[0];
//        this.chapter_number = args[0];
//        fetchData(this.book_id, this.chapter_number);
//    }
    public VersesViewModel(@NonNull Application application) {
        super(application);
        this.versesList = new MutableLiveData<ArrayList<Verse>>();
        //fetchData(this.book_id, this.chapter_number);
    }
    public MutableLiveData<ArrayList<Verse>> getUserMutableLiveData() {
        return versesList;
    }
    //PUBLIC SO we can refresh the list for some reason from the OUTSIDE
    public void refreshVersesList(int book_id, int chapter_number){
        fetchData(book_id, chapter_number);
    }

    public void fetchData(int book_id, int chapter_number){
        this.book_id = book_id;
        this.chapter_number = chapter_number;
        loadVerses(book_id, chapter_number);
    }
    public void loadVerses(int book_id, int chapter_number){new VersesViewModel.GetVerses().execute(book_id, chapter_number);}
    private class GetVerses extends AsyncTask<Integer, Void, Void> {
        //get data and populate the list
        protected Void doInBackground(Integer... args) {
            Log.d(TAG, "doInBackground: " + args[0] + " " + args[1]);
            ArrayList<Verse> result = BibleDBHelper.getInstance(getApplication()).getVerses(args[0], args[1]);
            Log.d(TAG, "doInBackground: result " + result.size());
            versesList.postValue(result);
            return null;
        }
    }

}
