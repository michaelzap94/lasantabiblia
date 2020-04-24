package com.michaelzap94.santabiblia.fragments.dialogs;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.michaelzap94.santabiblia.R;
import com.michaelzap94.santabiblia.adapters.dialogs.DialogRecyclerView;
import com.michaelzap94.santabiblia.models.Verse;

import java.util.ArrayList;
import java.util.HashMap;

public class VersesInsideDialog extends DialogFragment {
    private static final String TAG = "VersesInsideDialog";
    HashMap<String, ArrayList<Verse>> verseArrayList;
    RecyclerView rv;
    DialogRecyclerView adapter;
    String title;

    public VersesInsideDialog(String title, HashMap<String, ArrayList<Verse>> verseArrayList){
        this.verseArrayList = verseArrayList;
        this.title = title;
    }

    @Override
    public void onStart() {
        super.onStart();
        getDialog().getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView=inflater.inflate(R.layout.verses_dialog_fragment,container,false);
        Log.d(TAG, "onCreateView: inside dialog");

        //RECYCER
        rv= (RecyclerView) rootView.findViewById(R.id.verses_inside_fragment_rv);
        //ADAPTER
        adapter = new DialogRecyclerView(this.getContext(), verseArrayList);
        rv.setAdapter(adapter);
        rv.setLayoutManager(new LinearLayoutManager(this.getContext()));

        getDialog().setTitle(this.title);

        return rootView;
    }

}
