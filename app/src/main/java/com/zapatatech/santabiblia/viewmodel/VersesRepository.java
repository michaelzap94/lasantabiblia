package com.zapatatech.santabiblia.viewmodel;

import androidx.lifecycle.MutableLiveData;

import com.zapatatech.santabiblia.models.Verse;

import java.util.ArrayList;

public class VersesRepository {
    private MutableLiveData<ArrayList<Verse>> versesList;
    private static VersesRepository singleton = null;

    public MutableLiveData<ArrayList<Verse>> getUserMutableLiveData() {
        if (versesList == null) {
            versesList = new MutableLiveData<>();
        }
        return versesList;
    }

    public static VersesRepository getInstance(){
        if(singleton == null){
            return new VersesRepository();
        } else {
            return singleton;
        }
    }
}
