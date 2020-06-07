package com.zapatatech.santabiblia.fragments.dialogs;

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

import com.zapatatech.santabiblia.R;
import com.zapatatech.santabiblia.adapters.dialogs.DialogRecyclerView;
import com.zapatatech.santabiblia.models.Verse;

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
        getDialog().getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    @Override
    public void onPause(){
        super.onPause();
        this.dismissAllowingStateLoss();
    }

//    @Nullable
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        View rootView=inflater.inflate(R.layout.verses_dialog_fragment,container,false);
//        Log.d(TAG, "onCreateView: inside dialog");
//
//        //RECYCER
//        rv= (RecyclerView) rootView.findViewById(R.id.verses_inside_fragment_rv);
//        //ADAPTER
//        adapter = new DialogRecyclerView(this.getContext(), verseArrayList);
//        rv.setAdapter(adapter);
//        rv.setLayoutManager(new LinearLayoutManager(this.getContext()));
//
//        getDialog().setTitle(this.title);
//
//        return rootView;
//    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View rootView=inflater.inflate(R.layout.verses_dialog_fragment,null,false);
        Log.d(TAG, "onCreateDialog: inside dialog");

        //RECYCER
        rv= (RecyclerView) rootView.findViewById(R.id.verses_inside_fragment_rv);
        //ADAPTER
        adapter = new DialogRecyclerView(this.getContext(), verseArrayList);
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
