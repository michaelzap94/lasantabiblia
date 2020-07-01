package com.zapatatech.santabiblia.fragments.dashboard;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.zapatatech.santabiblia.Dashboard;
import com.zapatatech.santabiblia.R;
import com.zapatatech.santabiblia.adapters.RecyclerView.DashboardNotesRVA;
import com.zapatatech.santabiblia.fragments.dialogs.NoteDialog;
import com.zapatatech.santabiblia.models.Label;
import com.zapatatech.santabiblia.retrofit.Pojos.POJONote;
import com.zapatatech.santabiblia.viewmodel.NotesViewModel;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DashboardNotesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DashboardNotesFragment extends Fragment {

    private static final String TAG = "DashboardNotesFragment";
    private RecyclerView notesRV;
    private ArrayList<POJONote> notes = new ArrayList<>();
    private DashboardNotesRVA noteAdapter;
    private FloatingActionButton addNote;
    private NotesViewModel viewModel;


    private static final String M_LABEL = "mLabel";
    private Label mLabel;

    public DashboardNotesFragment() {
        // Required empty public constructor
    }

    public static DashboardNotesFragment newInstance(Label mLabel) {
        DashboardNotesFragment fragment = new DashboardNotesFragment();
        Bundle args = new Bundle();
        args.putParcelable(M_LABEL, mLabel);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: ");
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mLabel = getArguments().getParcelable(M_LABEL);
        }

        noteAdapter = new DashboardNotesRVA(mLabel, notes);
        viewModel = new ViewModelProvider(getActivity()).get(NotesViewModel.class);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onActivityCreated: ");
        super.onActivityCreated(savedInstanceState);
        observerViewModel();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_dashboard_notes, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        //super.onViewCreated(view, savedInstanceState);
        addNote = view.findViewById(R.id.notes_add_button);
        addNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mLabel != null) {
                    goToInsertNotes();
                }
            }
        });
        //--------------------------------------------------------------------------

        notesRV = view.findViewById(R.id.notes_recycler_view);
        notesRV.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        notesRV.setAdapter(noteAdapter);

        //--------------------------------------------------------------------------
//        observerViewModel();
    }

    public void goToInsertNotes(){

        FragmentManager fragmentManager = ((AppCompatActivity) getActivity()).getSupportFragmentManager();
        NoteDialog newFragment = NoteDialog.newInstance(mLabel, null, "new");
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        // For a little polish, specify a transition animation
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        // To make it fullscreen, use the 'content' root view as the container
        // for the fragment, which is always the root view for the activity
        transaction.add(R.id.dashboard_fragment, newFragment, "modifyNoteFragmentTag")
                .addToBackStack(null).commit();
    }


    private void observerViewModel() {
        viewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
            if(isLoading != null) {
//                loadingView.setVisibility(isLoading ? View.VISIBLE : View.GONE);
                Log.d(TAG, "observerViewModel: " + isLoading);
                if(isLoading) {
                } else {
                }
            }
        });
        viewModel.getNotesLiveData().observe(getViewLifecycleOwner(), (_notes) -> {
            Log.d(TAG, "observerViewModel: NOTES GOT DATA" + _notes.size() + "in label: " + this.mLabel.getName());
            noteAdapter.refreshData(_notes);
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getActivity() instanceof Dashboard) {
            ((Dashboard) getActivity()).getSupportActionBar().setTitle(this.mLabel.getName());
            viewModel.fetchNotes(this.mLabel.getId());
        }
    }
    
}