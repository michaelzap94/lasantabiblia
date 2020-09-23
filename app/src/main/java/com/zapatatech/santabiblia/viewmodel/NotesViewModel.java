package com.zapatatech.santabiblia.viewmodel;

import android.app.Application;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.zapatatech.santabiblia.retrofit.Pojos.POJONote;

import java.util.ArrayList;
import java.util.List;

public class NotesViewModel extends AndroidViewModel {
    private static final String TAG = "NotesViewModel";
    NotesRepository repository;

    public NotesViewModel(@NonNull Application application) {
        super(application);
        repository = NotesRepository.getInstance();
    }

    //specific to each context
    private MutableLiveData<Boolean> isLoading = new MutableLiveData<>();
    public MutableLiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    //------------------------------------------------------------------------
    public LiveData<ArrayList<POJONote>> getNotesLiveData() {
        return repository.getMutableNotesLiveData();
    }
    public void fetchNotes(String label_id){
        getIsLoading().setValue(true);//start loading - will stop when we finish getting the results
        repository.getNotes(getApplication(), getIsLoading(), label_id);
    }
    //------------------------------------------------------------------------
    public void insertNotes(String label_id, String title, String content){
//        getIsLoading().setValue(true);//start loading - will stop when we finish getting the results
        repository.insertNote(getApplication(), label_id, title, content);
    }
    //------------------------------------------------------------------------
    public void editNotes(POJONote mNote){
//        getIsLoading().setValue(true);//start loading - will stop when we finish getting the results
        repository.editNote(getApplication(), mNote);
    }
    //------------------------------------------------------------------------
    @Override
    protected void onCleared() {
        super.onCleared();
    }
}
