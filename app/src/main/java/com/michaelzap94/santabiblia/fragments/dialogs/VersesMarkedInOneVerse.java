package com.michaelzap94.santabiblia.fragments.dialogs;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.michaelzap94.santabiblia.R;
import com.michaelzap94.santabiblia.adapters.dialogs.DialogVersesMarkedInOneVerseRV;
import com.michaelzap94.santabiblia.models.Label;
import com.michaelzap94.santabiblia.models.Verse;
import com.michaelzap94.santabiblia.utilities.BookHelper;

import java.util.ArrayList;

public class VersesMarkedInOneVerse extends DialogFragment {
    private static final String TAG = "VersesMarkedInOneVerse";
    private ArrayList<Label> listOfLabels;
    private RecyclerView rv;
    private DialogVersesMarkedInOneVerseRV adapter;
    private String title;

    public VersesMarkedInOneVerse(Verse verse){
        this.listOfLabels = verse.getListOfLabels();
        this.title = BookHelper.getBook(verse.getBookNumber()).getName() + " " + verse.getChapterNumber();
    }

    @Override
    public void onStart() {
        super.onStart();
        getDialog().getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    @Override
    public void onPause(){
        super.onPause();
        this.dismissAllowingStateLoss();
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View rootView=inflater.inflate(R.layout.verses_dialog_fragment,null,false);
        Log.d(TAG, "onCreateDialog: inside dialog");

        //RECYCER
        rv= (RecyclerView) rootView.findViewById(R.id.verses_inside_fragment_rv);
        //ADAPTER
        adapter = new DialogVersesMarkedInOneVerseRV(this.getContext(), listOfLabels);
        rv.setAdapter(adapter);
        rv.setLayoutManager(new LinearLayoutManager(this.getContext()));

        return new AlertDialog.Builder(getActivity())
                .setTitle(this.title)
                .setView(rootView)
                .setNegativeButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                dialog.dismiss();
                            }
                        }
                )
                .create();
    }

}

