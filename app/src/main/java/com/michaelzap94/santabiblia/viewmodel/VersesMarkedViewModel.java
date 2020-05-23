package com.michaelzap94.santabiblia.viewmodel;

import android.app.Application;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.michaelzap94.santabiblia.DatabaseHelper.ContentDBHelper;
import com.michaelzap94.santabiblia.adapters.RecyclerView.VersesLearnedRecyclerView;
import com.michaelzap94.santabiblia.models.Label;
import com.michaelzap94.santabiblia.models.VersesMarked;

import java.util.ArrayList;
import java.util.List;

public class VersesMarkedViewModel extends AndroidViewModel {

    private static final String TAG = "VersesMarkedViewModel";

    private MutableLiveData<ArrayList<VersesMarked>> versesMarkedList;
    private MutableLiveData<ArrayList<VersesMarked>> versesMarkedListByUUID;
    private MutableLiveData<ArrayList<VersesMarked>> versesMarkedListLearned;
    private MutableLiveData<ArrayList<VersesMarked>> versesMarkedListNotLearned;
    private int label_id;

    public VersesMarkedViewModel(@NonNull Application application) {
        super(application);
        this.versesMarkedList = new MutableLiveData<ArrayList<VersesMarked>>();
        //fetchData(this.book_number, this.chapter_number);
    }
    public MutableLiveData<ArrayList<VersesMarked>> getUserMutableLiveData() {
        return versesMarkedList;
    }
    public MutableLiveData<ArrayList<VersesMarked>> getVersesMarkedListByUUIDLiveData() {
        return versesMarkedListByUUID;
    }
    public MutableLiveData<ArrayList<VersesMarked>> getVersesMarkedListLearned() {
        return versesMarkedListLearned;
    }
    public MutableLiveData<ArrayList<VersesMarked>> getVersesMarkedListNotLearned() {
        return versesMarkedListNotLearned;
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
            Log.d(TAG, "GetVersesMarked doInBackground: " + args[0]);
            ArrayList<VersesMarked> result = ContentDBHelper.getInstance(getApplication()).getVersesMarked(args[0], null, -1);
            Log.d(TAG, "GetVersesMarked doInBackground: result " + result.size());
            versesMarkedList.postValue(result);
            return null;
        }
    }
    //===============================================================================================
    public void updateVersesMarked(String uuid, Label mLabel, int book_number, int chapter_number, String note, List<Integer> selectedItems){new VersesMarkedViewModel.UpdateVersesMarked().execute(uuid, mLabel, book_number, chapter_number, note, selectedItems);}
    private class UpdateVersesMarked extends AsyncTask<Object, Void, Void> {
        //get data and populate the list
        protected Void doInBackground(Object... args) {
            Log.d(TAG, "UpdateVersesMarked doInBackground: " + args[0]);
            boolean success = ContentDBHelper.getInstance(getApplication()).updateSelectedItemsBulkTransaction((String) args[0], (Label) args[1], (int) args[2], (int) args[3], (String) args[4], (List<Integer>) args[5]);
            Log.d(TAG, "UpdateVersesMarked doInBackground: result " + success);
            if(success){
                fetchData(((Label) args[1]).getId());
            } else {
                Log.d(TAG, "MarkVerses Not all elements could be inserted");
            }
            return null;
        }
    }
    //===============================================================================================
    public void getVersesMarkedByUUID(ArrayList<Label> listOfLabels){
        if(versesMarkedListByUUID == null){
            this.versesMarkedListByUUID = new MutableLiveData<ArrayList<VersesMarked>>();
        }
        new VersesMarkedViewModel.VersesMarkedByUUID().execute(listOfLabels);
    }
    private class VersesMarkedByUUID extends AsyncTask<ArrayList<Label>, Void, Void> {
        //get data and populate the list
        protected Void doInBackground(ArrayList<Label>... args) {
            Log.d(TAG, "GetVersesMarked doInBackground: " + args[0]);
            ArrayList<VersesMarked> result = ContentDBHelper.getInstance(getApplication()).getVersesMarkedByUUID(args[0]);
            Log.d(TAG, "GetVersesMarked doInBackground: result " + result.size());
            versesMarkedListByUUID.postValue(result);
            return null;
        }
    }
    //===============================================================================================
    public void getVersesLearned(int learned){
        if(learned == 0){
            if(versesMarkedListNotLearned == null){
                this.versesMarkedListNotLearned = new MutableLiveData<>();
            }
        } else {
            if(versesMarkedListLearned == null){
                this.versesMarkedListLearned = new MutableLiveData<>();
            }
        }
        new VersesMarkedViewModel.GetVersesMarkedLearned().execute(learned);
    }
    private class GetVersesMarkedLearned extends AsyncTask<Integer, Void, Void> {
        //get data and populate the list
        protected Void doInBackground(Integer... args) {
            Log.d(TAG, "GetVersesMarkedLearned doInBackground: " + args[0]);
            ArrayList<VersesMarked> result = ContentDBHelper.getInstance(getApplication()).getVersesMarkedLearned(args[0]);
            Log.d(TAG, "GetVersesMarkedLearned doInBackground: result " + result.size());
            if(args[0] == 0){
                versesMarkedListNotLearned.postValue(result);
            } else {
                versesMarkedListLearned.postValue(result);
            }
            return null;
        }
    }
    //===============================================================================================
//    public void removeFromLearned(String uuid, int position){new VersesMarkedViewModel.RemoveVersesLearned(position).execute(uuid);}
//    private class RemoveVersesLearned extends AsyncTask<String, Void, Boolean> {
//        private int position;
//        private RemoveVersesLearned(int position) {
//            this.position = position;
//        }
//        protected Boolean doInBackground(String... args) {
//            Log.d(TAG, "doInBackground: " + args[0]);
//            return ContentDBHelper.getInstance(getApplication()).editVersesLearned(args[0], 0);
//        }
//        @Override
//        protected void onPostExecute(Boolean success) {
//            super.onPostExecute(success);
//            if(success){
//                versesMarkedArrayList.remove(this.position);
//                notifyItemRemoved(this.position);
//
//            } else {
//                Toast.makeText(VersesLearnedRecyclerView.this.ctx, "This item could not be deleted", Toast.LENGTH_SHORT).show();
//            }
//        }
//    }
}
