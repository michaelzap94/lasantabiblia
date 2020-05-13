package com.michaelzap94.santabiblia.viewmodel;

import android.app.Application;
import android.os.AsyncTask;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.michaelzap94.santabiblia.DatabaseHelper.BibleDBHelper;
import com.michaelzap94.santabiblia.DatabaseHelper.ContentDBHelper;
import com.michaelzap94.santabiblia.models.Label;
import com.michaelzap94.santabiblia.models.Verse;

import java.util.ArrayList;
import java.util.List;

public class VersesViewModel extends AndroidViewModel {

    private static final String TAG = "VersesViewModel";

    private MutableLiveData<ArrayList<Verse>> versesList;
    private int book_number;
    private int chapter_number;

//    public VersesViewModel(@NonNull Application application, int... args) {
//        super(application);
//        this.versesList = new MutableLiveData<ArrayList<Verse>>();
//        this.book_number = args[0];
//        this.chapter_number = args[0];
//        fetchData(this.book_number, this.chapter_number);
//    }
    public VersesViewModel(@NonNull Application application) {
        super(application);
        this.versesList = new MutableLiveData<ArrayList<Verse>>();
        //fetchData(this.book_number, this.chapter_number);
    }
    public MutableLiveData<ArrayList<Verse>> getUserMutableLiveData() {
        return versesList;
    }
    //PUBLIC SO we can refresh the list for some reason from the OUTSIDE
    public void refreshVersesList(int book_number, int chapter_number){
        fetchData(book_number, chapter_number);
    }

    public void fetchData(int book_number, int chapter_number){
        this.book_number = book_number;
        this.chapter_number = chapter_number;
        loadVerses(book_number, chapter_number);
    }
    private void loadVerses(int book_number, int chapter_number){new VersesViewModel.GetVerses().execute(book_number, chapter_number);}
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
    public void markVerses(Label mLabel, int book_number, int chapter_number, String note, List<Integer> selectedItems){new VersesViewModel.MarkVerses().execute(mLabel, book_number, chapter_number, note, selectedItems);}
    private class MarkVerses extends AsyncTask<Object, Void, Void> {
        //get data and populate the list
        protected Void doInBackground(Object... args) {
            Log.d(TAG, "MarkVerses doInBackground: " + args[0] + " " + args[1]);
            boolean success = ContentDBHelper.getInstance(getApplication()).insertSelectedItemsBulkTransaction((Label) args[0], (int) args[1], (int) args[2], (String) args[3], (List<Integer>) args[4]);
            Log.d(TAG, "MarkVerses doInBackground: result " + success);
            if(success){
                loadVerses(book_number, chapter_number);
            } else {
                Log.d(TAG, "MarkVerses Not all elements could be inserted");
            }
            return null;
        }
    }

}
