package com.zapatatech.santabiblia.fragments.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.zapatatech.santabiblia.Bible;
import com.zapatatech.santabiblia.Dashboard;
import com.zapatatech.santabiblia.R;
import com.zapatatech.santabiblia.adapters.RecyclerView.VersesMarkedEditRecyclerViewAdapter;
import com.zapatatech.santabiblia.models.Label;
import com.zapatatech.santabiblia.models.VersesMarked;
import com.zapatatech.santabiblia.retrofit.Pojos.POJONote;
import com.zapatatech.santabiblia.utilities.BookHelper;
import com.zapatatech.santabiblia.utilities.SwipeToDelete;
import com.zapatatech.santabiblia.viewmodel.NotesRepository;
import com.zapatatech.santabiblia.viewmodel.NotesViewModel;
import com.zapatatech.santabiblia.viewmodel.VersesMarkedViewModel;

import java.util.ArrayList;
import java.util.Map;

public class NoteDialog extends DialogFragment {

    private static final String TAG = "NoteDialog";
    private static final String M_LABEL = "mLabel";
    private static final String ACTION_TYPE = "ACTION_TYPE";
    private static final String M_NOTE = "mNote";
    private Label mLabel;
    private POJONote mNote;
    private String actionType;

    private Toolbar toolbar;
    private Context context;
    private TextInputLayout noteTitle;
    private TextInputLayout noteContent;

    private NotesViewModel viewModel;


    public static NoteDialog newInstance(Label mLabel, POJONote note, String actionType) {
        Bundle args = new Bundle();
        args.putParcelable(M_LABEL, mLabel);
        args.putString(ACTION_TYPE, actionType);
        if(note != null) {
            args.putParcelable(M_NOTE, note);
        }
        NoteDialog fragment = new NoteDialog();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity();
        mLabel = (Label) getArguments().getParcelable(M_LABEL);
        actionType = (String) getArguments().getString(ACTION_TYPE);
        mNote = getArguments().getParcelable(M_NOTE);
        setHasOptionsMenu(true);
        viewModel = new ViewModelProvider(getActivity()).get(NotesViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.note_dialog, container, false);
        //toolbar = rootView.findViewById(R.id.toolbar);
        noteTitle = rootView.findViewById(R.id.addNoteTitle);
        noteContent = rootView.findViewById(R.id.addNoteContent);

        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        switch (actionType) {
            case "new": initNew();
                break;
            case "edit": initEdit();
                break;
            case "view": initView();
                break;
            default: break;
        }
    }

    private void initNew(){
        if (getActivity() instanceof Dashboard) {
            ((Dashboard) getActivity()).getSupportActionBar().setSubtitle("Inserting Note");
        }
    }

    private void initEdit(){
        if (getActivity() instanceof Dashboard) {
            ((Dashboard) getActivity()).getSupportActionBar().setSubtitle("Editing Note");
        }
        if (mNote != null) {
            noteTitle.getEditText().setText(mNote.getTitle());
            noteContent.getEditText().setText(mNote.getContent());
        }
    }

    private void initView(){
        if (getActivity() instanceof Dashboard) {
            ((Dashboard) getActivity()).getSupportActionBar().setSubtitle("Viewing Note");
        }
        if (mNote != null) {
            noteTitle.getEditText().setText(mNote.getTitle());
            noteContent.getEditText().setText(mNote.getContent());
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    @Override
    public void onDestroyView() {
        ((Dashboard) getActivity()).getSupportActionBar().setSubtitle(null);
        super.onDestroyView();
    }

    //============================================================================================
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        switch (actionType) {
            case "new":
            case "edit": inflater.inflate(R.menu.menu_save, menu);
                break;
            case "view": inflater.inflate(R.menu.menu_save, menu);
                break;
            default: break;
        }

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
//            case R.id.dash_label_menu_edit:
//                //goToEdit();
//                return true;
//            case R.id.dash_label_menu_delete:
//                //deleteConfimation();
//                return true;
            case R.id.action_save:
                saveNote();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    //============================================================================================
    private void saveNote(){
        boolean error = false;
        if (noteTitle.getEditText().getText().toString().trim().equalsIgnoreCase("")) {
            noteTitle.getEditText().setError("This field can not be blank");
            error = true;
        }
        if (noteContent.getEditText().getText().toString().trim().equalsIgnoreCase("")) {
            noteContent.getEditText().setError("This field can not be blank");
            error = true;
        }
        if(!error){
            String titleValue = noteTitle.getEditText().getText().toString();
            String contentValue = noteContent.getEditText().getText().toString();
            if(mNote != null){
                //if something changed, update, otherwise do not
                if(!titleValue.equals(mNote.getTitle()) || !contentValue.equals(mNote.getContent())) {
                    mNote.setTitle(titleValue);
                    mNote.setContent(contentValue);
//                    NotesRepository.getInstance().editNote(getActivity(), mNote);
                    viewModel.editNotes(mNote);
                }
            } else {
                //insert new
                //NotesRepository.getInstance().insertNote(getActivity().getApplication(), mLabel.getId(), titleValue, contentValue);
                viewModel.insertNotes(mLabel.getId(), titleValue, contentValue);
            }
            getActivity().onBackPressed();
        }
    }

}
