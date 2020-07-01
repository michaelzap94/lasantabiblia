package com.zapatatech.santabiblia.fragments.dashboard;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.zapatatech.santabiblia.AddNote;
import com.zapatatech.santabiblia.R;
import com.zapatatech.santabiblia.adapters.RecyclerView.DashboardNotesRVA;
import com.zapatatech.santabiblia.models.Label;
import com.zapatatech.santabiblia.models.Note;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DashboardNotesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DashboardNotesFragment extends Fragment {

    private static final String TAG = "DashboardNotesFragment";
    private RecyclerView notesRV;
    private ArrayList<Note> notes = new ArrayList<>();
    private DashboardNotesRVA noteAdapter;
    private FloatingActionButton addNote;

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
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mLabel = getArguments().getParcelable(M_LABEL);
        }

        noteAdapter = new DashboardNotesRVA(notes);
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
                    Intent i = new Intent(getActivity(), AddNote.class);
                    i.putExtra(M_LABEL, mLabel);
                    getActivity().startActivity(i);
                }
            }
        });
        //--------------------------------------------------------------------------

        notesRV = view.findViewById(R.id.notes_recycler_view);
        notesRV.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        notesRV.setAdapter(noteAdapter);

        //--------------------------------------------------------------------------
        observerViewModel();
    }

    private void observerViewModel() {
        ArrayList<Note> notes = new ArrayList<>();
        notes.add(new Note("id", "test title 0", "This is the content"));
        notes.add(new Note("id", "test title 1", "Lorem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry's standard dummy text ever since the 1500s, when an unknown printer took a galley of type and scrambled it to make a type specimen book. It has survived not only five centuries, but also the leap into electronic typesetting, remaining essentially unchanged. It was popularised in the 1960s with the release of Letraset sheets containing Lorem Ipsum passages, and more recently with desktop publishing software like Aldus PageMaker including versions of Lorem Ipsum."));
        notes.add(new Note("id", "test title 2", "Lorem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry's standard dummy text ever since the 1500s, when an unknown printer took a galley of type and scrambled it to make a type specimen book. It has survived not only five centuries"));
        notes.add(new Note("id", "test title 3", "Lorem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry's standard dummy text ever since the 1500s, when an unknown printer took a galley of type and scrambled it to make a type specimen book. It has survived not only five centuries"));
        notes.add(new Note("id", "test title 4", "Lorem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry's standard dummy text ever since the 1500s, when an unknown printer took a galley of type and scrambled it to make a type specimen book. It has survived not only five centuries"));
        notes.add(new Note("id", "test title 5", "Lorem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry's standard dummy text ever since the 1500s, when an unknown printer took a galley of type and scrambled it to make a type specimen book. It has survived not only five centuries"));
        notes.add(new Note("id", "test title 6", "This is the content"));

        noteAdapter.refreshData(notes);
    }
    
}