package com.zapatatech.santabiblia.fragments.dialogs;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.zapatatech.santabiblia.R;
import com.zapatatech.santabiblia.adapters.dialogs.DialogVersesMarkedInOneVerseRV;
import com.zapatatech.santabiblia.models.Label;
import com.zapatatech.santabiblia.models.Verse;
import com.zapatatech.santabiblia.utilities.BookHelper;
import com.zapatatech.santabiblia.viewmodel.VersesMarkedViewModel;

import java.util.ArrayList;

public class VersesMarkedInOneVerse extends DialogFragment {
    private static final String TAG = "VersesMarkedInOneVerse";
    private ArrayList<Label> listOfLabels;
    private RecyclerView rv;
    private DialogVersesMarkedInOneVerseRV adapter;
    private String title;

    private VersesMarkedViewModel viewModel;

    public VersesMarkedInOneVerse(Verse verse){
        this.listOfLabels = verse.getListOfLabels();
        this.title = BookHelper.getInstance().getBook(verse.getBookNumber()).getName() + " " + verse.getChapterNumber();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //ADAPTER
        adapter = new DialogVersesMarkedInOneVerseRV(this.getContext(), null, null);
        viewModel = new ViewModelProvider(getActivity()).get(VersesMarkedViewModel.class);
        viewModel.getVersesMarkedByUUID(listOfLabels);//refresh -> load data
        observerViewModel();
    }

    private void observerViewModel() {
        viewModel.getVersesMarkedListByUUIDLiveData().observe(getActivity(), (versesMarkedArrayList) -> {
            Log.d(TAG, "observerViewModel: LABEL GOT DATA DIALOG One verse: " + versesMarkedArrayList.size());
            //WHEN data is created  pass data and set it in the recyclerview VIEW
            adapter.updateVersesMarkedRecyclerView(versesMarkedArrayList);
        });
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View rootView=inflater.inflate(R.layout.verses_dialog_fragment,null,false);
        Log.d(TAG, "onCreateDialog: inside dialog");

        //RECYCER
        rv= (RecyclerView) rootView.findViewById(R.id.verses_inside_fragment_rv);
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

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        adapter.setDialog(getDialog());
        return super.onCreateView(inflater, container, savedInstanceState);
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
}

