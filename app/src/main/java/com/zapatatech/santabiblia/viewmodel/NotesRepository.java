package com.zapatatech.santabiblia.viewmodel;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.zapatatech.santabiblia.DatabaseHelper.BibleDBHelper;
import com.zapatatech.santabiblia.DatabaseHelper.ContentDBHelper;
import com.zapatatech.santabiblia.adapters.RecyclerView.DashboardNotesRVA;
import com.zapatatech.santabiblia.models.Label;
import com.zapatatech.santabiblia.models.Verse;
import com.zapatatech.santabiblia.retrofit.Pojos.POJONote;

import java.util.ArrayList;
import java.util.List;

public class NotesRepository {
    private static final String TAG = "NotesRepository";
    private MutableLiveData<ArrayList<POJONote>> notesList;
    private static NotesRepository singleton = null;
    public static NotesRepository getInstance(){
        if(singleton == null){
            return new NotesRepository();
        } else {
            return singleton;
        }
    }

    public MutableLiveData<ArrayList<POJONote>> getMutableNotesLiveData() {
        if (notesList == null) {
            notesList = new MutableLiveData<>();
        }
        return notesList;
    }

    public void getNotes(Context context, MutableLiveData<Boolean> isLoading, String label_id){
        //retrieve them from local db
        new NotesRepository.GetNotesLocal(context, isLoading).execute(label_id);
    }
    private class GetNotesLocal extends AsyncTask<String, Void, Void> {
        private Context context;
        MutableLiveData<Boolean> isLoading;
        private GetNotesLocal(Context context, MutableLiveData<Boolean> isLoading) {
            this.context = context;
            this.isLoading = isLoading;
        }
        protected Void doInBackground(String... args) {
            Log.d(TAG, "GetNotesLocal doInBackground: " + args[0]);
            ArrayList<POJONote> result = ContentDBHelper.getInstance(context).getNotes(args[0]);
            Log.d(TAG, "GetNotesLocal doInBackground: result " + result.size());
            getMutableNotesLiveData().postValue(result);
            if(isLoading != null) {
                isLoading.postValue(false);
            }
            return null;
        }
    }

    public void insertNote(Context context, String label_id, String title, String content){
        //insert it to local db
        new NotesRepository.InsertNoteLocal(context).execute(label_id, title, content);
    }
    private class InsertNoteLocal extends AsyncTask<String, Void, Void> {
        private Context context;
        private InsertNoteLocal(Context context) {
            this.context = context;
        }
        protected Void doInBackground(String... args) {
            Log.d(TAG, "InsertNoteLocal doInBackground: " + args[0]);
            boolean success = ContentDBHelper.getInstance(context).insertNote(args[0], args[1], args[2]);
            if(success) {
                getNotes(context, null, args[0]);
            } else {
                Toast.makeText(context, "Note could not be added.", Toast.LENGTH_SHORT).show();
            }
            return null;
        }
    }

    public void editNote(Context context, POJONote note){
        //insert it to local db
        new NotesRepository.EditNoteLocal(context).execute(note);
    }
    private class EditNoteLocal extends AsyncTask<POJONote, Void, Void> {
        private Context context;
        private EditNoteLocal(Context context) {
            this.context = context;
        }
        protected Void doInBackground(POJONote... args) {
            Log.d(TAG, "EditNoteLocal doInBackground: " + args[0].getLabel_id());
            boolean success = ContentDBHelper.getInstance(context).editNote(args[0]);
            if(success) {
                getNotes(context, null, args[0].getLabel_id());
            } else {
                Toast.makeText(context, "Note could not be edited.", Toast.LENGTH_SHORT).show();
            }
            return null;
        }
    }

    public void deleteNote(Context context, DashboardNotesRVA adapter, int position, String label_id, String note_id){
        //insert it to local db
        new NotesRepository.DeleteNoteLocal(context, adapter, position).execute(label_id, note_id);
    }
    private class DeleteNoteLocal extends AsyncTask<String, Void, Boolean> {
        private Context context;
        private DashboardNotesRVA adapter;
        private int position;
        private DeleteNoteLocal(Context context, DashboardNotesRVA adapter, int position) {
            this.context = context;
            this.adapter = adapter;
            this.position = position;
        }
        protected Boolean doInBackground(String... args) {
            Log.d(TAG, "DeleteNoteLocal doInBackground: " + args[0] + " at " + this.position);
            return ContentDBHelper.getInstance(context).deleteOneNote(args[0], args[1]);
        }

        @Override
        protected void onPostExecute(Boolean success) {
            if(success) {
                adapter.removeItem(position);
            } else {
                Toast.makeText(context, "Note could not be deleted.", Toast.LENGTH_SHORT).show();
            }
        }
    }

}
