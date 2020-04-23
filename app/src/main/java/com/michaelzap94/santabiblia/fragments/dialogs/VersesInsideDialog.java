package com.michaelzap94.santabiblia.fragments.dialogs;

import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.michaelzap94.santabiblia.Bible;
import com.michaelzap94.santabiblia.R;
import com.michaelzap94.santabiblia.dialogs.DialogRecyclerView;
import com.michaelzap94.santabiblia.models.Verse;

import java.util.ArrayList;

public class VersesInsideDialog extends DialogFragment {
    private static final String TAG = "VersesInsideDialog";
    ArrayList<Verse> verseArrayList = new ArrayList<>();
    RecyclerView rv;
    DialogRecyclerView adapter;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView=inflater.inflate(R.layout.verses_dialog_fragment,container,false);
        Log.d(TAG, "onCreateView: inside dialog");

        Spanned textSpanned = Html.fromHtml("<pb/>Y dijo Dios: Sea la luz; <f>[1†]</f> y fue la luz.");
        verseArrayList.add(new Verse(230, 1, 1, textSpanned, 0));
        verseArrayList.add(new Verse(230, 1, 1, textSpanned, 0));
        verseArrayList.add(new Verse(230, 1, 1, textSpanned, 0));
        verseArrayList.add(new Verse(230, 1, 1, textSpanned, 0));
        verseArrayList.add(new Verse(230, 1, 1, textSpanned, 0));
        verseArrayList.add(new Verse(230, 1, 1, textSpanned, 0));
        verseArrayList.add(new Verse(230, 1, 1, textSpanned, 0));
        verseArrayList.add(new Verse(230, 1, 1, textSpanned, 0));
        verseArrayList.add(new Verse(230, 1, 1, textSpanned, 0));
        verseArrayList.add(new Verse(230, 1, 1, textSpanned, 0));
        verseArrayList.add(new Verse(230, 1, 1, textSpanned, 0));
        verseArrayList.add(new Verse(230, 1, 1, textSpanned, 0));
        verseArrayList.add(new Verse(230, 1, 1, textSpanned, 0));
        verseArrayList.add(new Verse(230, 1, 1, textSpanned, 0));
        verseArrayList.add(new Verse(230, 1, 1, textSpanned, 0));

        //RECYCER
        rv= (RecyclerView) rootView.findViewById(R.id.verses_inside_fragment_rv);


        //ADAPTER
        adapter = new DialogRecyclerView(this.getContext(), verseArrayList);
        rv.setAdapter(adapter);
        rv.setLayoutManager(new LinearLayoutManager(this.getContext()));

        this.getDialog().setTitle("TV Shows");

        return rootView;
    }

}
