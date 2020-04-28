package com.michaelzap94.santabiblia.viewmodel;

import android.app.Application;
import android.os.AsyncTask;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.michaelzap94.santabiblia.DatabaseHelper.ContentDBHelper;
import com.michaelzap94.santabiblia.models.VersesMarked;

import java.util.ArrayList;

public class VersesMarkedViewModel extends AndroidViewModel {

    private static final String TAG = "VersesMarkedViewModel";

    private MutableLiveData<ArrayList<VersesMarked>> versesMarkedList;
    private int label_id;

    public VersesMarkedViewModel(@NonNull Application application) {
        super(application);
        this.versesMarkedList = new MutableLiveData<ArrayList<VersesMarked>>();
        //fetchData(this.book_number, this.chapter_number);
    }
    public MutableLiveData<ArrayList<VersesMarked>> getUserMutableLiveData() {
        return versesMarkedList;
    }
    //PUBLIC SO we can refresh the list for some reason from the OUTSIDE
    public void refreshVersesMarkedList(int label_id){
        fetchData(label_id);
    }

    public void fetchData(int label_id){
        this.label_id = label_id;
        loadVerses(label_id);
    }
    public void loadVerses(int label_id){new VersesMarkedViewModel.GetVersesMarked().execute(label_id);}
    private class GetVersesMarked extends AsyncTask<Integer, Void, Void> {
        //get data and populate the list
        protected Void doInBackground(Integer... args) {
            Log.d(TAG, "doInBackground: " + args[0]);
            ArrayList<VersesMarked> result = ContentDBHelper.getInstance(getApplication()).getVersesMarked(args[0]);
            Log.d(TAG, "doInBackground: result " + result.size());
            versesMarkedList.postValue(result);
            return null;
        }
    }

}
