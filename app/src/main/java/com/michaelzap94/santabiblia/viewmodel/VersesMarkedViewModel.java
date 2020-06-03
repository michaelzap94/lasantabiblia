package com.michaelzap94.santabiblia.viewmodel;

import android.app.Application;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.LongDef;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.michaelzap94.santabiblia.Bible;
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
    private MutableLiveData<ArrayList<Label>> allLabels;
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
    public MutableLiveData<ArrayList<Label>> getAllLabelsLiveData() {
        return allLabels;
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
    public void updateVersesMarked(Bible ctx, String uuid, Label mLabel, int book_number, int chapter_number, String note, List<Integer> selectedItems){new VersesMarkedViewModel.UpdateVersesMarked(ctx).execute(uuid, mLabel, book_number, chapter_number, note, selectedItems);}
    private class UpdateVersesMarked extends AsyncTask<Object, Void, Void> {
        Bible ctx;
        UpdateVersesMarked(Bible ctx){
            this.ctx = ctx;
        }
        //get data and populate the list
        protected Void doInBackground(Object... args) {
            Log.d(TAG, "UpdateVersesMarked doInBackground: " + args[0]);
            boolean success = ContentDBHelper.getInstance(getApplication()).updateSelectedItemsBulkTransaction((String) args[0], (Label) args[1], (int) args[2], (int) args[3], (String) args[4], (List<Integer>) args[5]);
            Log.d(TAG, "UpdateVersesMarked doInBackground: result " + success);
            if(success){
                if(ctx == null){//viewmodel from Dashboard
                    fetchData(((Label) args[1]).getId());
                } else {//viewmodel from bible
                    ((Bible) ctx).onVersesMarkedEditedFromDialog((int) args[2], (int) args[3]);
                }
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
    public void getAllLabels(){
        if(allLabels == null){
            this.allLabels = new MutableLiveData<ArrayList<Label>>();
        }
        new VersesMarkedViewModel.AllLabelsAsync().execute();
    }
    private class AllLabelsAsync extends AsyncTask<Void, Void, Void> {
        //get data and populate the list
        protected Void doInBackground(Void... args) {
            Log.d(TAG, "GetVersesMarked doInBackground: ");
            ArrayList<Label> result = ContentDBHelper.getInstance(getApplication()).getAllLabels();
            Log.d(TAG, "GetVersesMarked doInBackground: result " + result.size());
            allLabels.postValue(result);
            return null;
        }
    }
    //===============================================================================================
    public void updateOrCreateLabel(String nameValue, String colorValue, int idValue){
        new VersesMarkedViewModel.UpdateOrCreateLabelAsync().execute(nameValue, colorValue, idValue);
    }
    private class UpdateOrCreateLabelAsync extends AsyncTask<Object, Void, Void> {
        //get data and populate the list
        protected Void doInBackground(Object... args) {
            int idValue = (int) args[2];
            boolean insertSuccess;
            if(idValue != -1){
                insertSuccess = ContentDBHelper.getInstance(getApplication()).editLabel((String) args[0], (String) args[1], idValue);
            } else {
                insertSuccess = ContentDBHelper.getInstance(getApplication()).createLabel((String) args[0], (String) args[1]);
            }
            if(insertSuccess){
                getAllLabels();
            }
            return null;
        }
    }
    //===============================================================================================
    public void deleteLabel(int label_id){
        new VersesMarkedViewModel.DeleteLabelAsync().execute(label_id);
    }
    private class DeleteLabelAsync extends AsyncTask<Integer, Void, Void> {
        //get data and populate the list
        protected Void doInBackground(Integer... args) {
            int idValue = args[0];
            boolean insertSuccess = ContentDBHelper.getInstance(getApplication()).deleteOneLabel(idValue);
            if(insertSuccess){
                getAllLabels();
            } else {
                Log.d(TAG, "doInBackground: label could not be deleted");
            }
            return null;
        }
    }
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
