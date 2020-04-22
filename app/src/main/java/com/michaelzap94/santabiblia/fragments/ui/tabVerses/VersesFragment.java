package com.michaelzap94.santabiblia.fragments.ui.tabVerses;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.michaelzap94.santabiblia.BaseActivityTopDrawer;
import com.michaelzap94.santabiblia.R;
import com.michaelzap94.santabiblia.adapters.VersesRecyclerViewAdapter;
import com.michaelzap94.santabiblia.models.Verse;
import com.michaelzap94.santabiblia.viewmodel.VersesViewModel;

import java.util.ArrayList;

public class VersesFragment extends Fragment {
    private static final String TAG = "VersesFragment";
    public static final String TAB_VERSES_NUMBER = "tab_verses_number";
    View root;
    private int book_id;
    private int chapter_number;
    private int verse_number;

    private ArrayList<Verse> list = new ArrayList();
    private RecyclerView rvView;
    private VersesViewModel viewModel;
    //Instantiate the RecyclerViewAdapter, passing an empty list initially.
    // this data will not be shown until you setAdapter to the RecyclerView view in the Layout
    private VersesRecyclerViewAdapter rvAdapter;

    public static VersesFragment newInstance(int book, int chapter, int verse) {
        Log.d(TAG, "VersesFragment: newInstance" + + book + " " + chapter);
        VersesFragment f = new VersesFragment();
        Bundle args = new Bundle();
        args.putInt("book", book);
        args.putInt("chapter", chapter);
        args.putInt("verse", verse);
        f.setArguments(args);
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.book_id = getArguments().getInt("book");
        this.chapter_number = getArguments().getInt("chapter");
        this.verse_number = getArguments().getInt("verse");
        Log.d(TAG, "onCreate: VersesFragment " + chapter_number);
        rvAdapter = new VersesRecyclerViewAdapter(getActivity(), new ArrayList<>());

        //get viewmodel class and properties, pass this context so LifeCycles are handled by ViewModel,
        // in case the Activity is destroyed and recreated(screen roation)
        //ViewModel will help us show the exact same data, and resume the application from when the user left last time.
        viewModel = new ViewModelProvider(this).get(VersesViewModel.class);
        //Use when we need to reload data
        viewModel.fetchData(this.book_id, this.chapter_number);//refresh -> load data
        //viewModel.getUserMutableLiveData().observe(context, verseListUpdateObserver);
        //observerViewModel();
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView: VersesFragment " + this.chapter_number);

        root = inflater.inflate(R.layout.verses_fragment, container, false);
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onViewCreated: VersesFragment " + this.chapter_number);
        //////////////////////////////////////////////
        this.rvView = (RecyclerView) root.findViewById(R.id.verses_list_view);
        rvView.setLayoutManager(new LinearLayoutManager(this.getContext()));
        rvView.setAdapter(rvAdapter);//attach the RecyclerView adapter to the RecyclerView View
        /////////////////////////////////////////
        observerViewModel();

    }

    @Override
    public void onResume() {
        super.onResume();
        if (getActivity() instanceof BaseActivityTopDrawer) {
            ((BaseActivityTopDrawer) getActivity()).getSupportActionBar().setSubtitle("Capitulo "+this.chapter_number);
        }
    }

    private void observerViewModel() {
        viewModel.getUserMutableLiveData().observe(getViewLifecycleOwner(), (verseArrayList) -> {
            Log.d(TAG, "observerViewModel: VersesFragment GOT DATA" + verseArrayList.size() + "in fragment: " + this.chapter_number);
            //WHEN data is created  pass data and set it in the recyclerview VIEW
            rvAdapter.updateVersesRecyclerView(verseArrayList);

            if (VersesFragment.this.verse_number != 0) {
                VersesFragment.this.rvView.scrollToPosition(VersesFragment.this.verse_number - 1);
            }
        });
    }
}
