package com.zapatatech.santabiblia.fragments.dashboard;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.zapatatech.santabiblia.Dashboard;
import com.zapatatech.santabiblia.R;
import com.zapatatech.santabiblia.adapters.RecyclerView.VersesMarkedRecyclerViewAdapter;
import com.zapatatech.santabiblia.models.Label;
import com.zapatatech.santabiblia.models.VersesMarked;
import com.zapatatech.santabiblia.viewmodel.VersesMarkedViewModel;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DashboardBibleFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DashboardBibleFragment extends Fragment {
    private static final String TAG = "DashboardBibleFragment";
    private Label mLabel;
    private Context ctx;

    ///////////////////////////////////////////////////////////
    private ArrayList<VersesMarked> list = new ArrayList();
    private RecyclerView rvView;
    private VersesMarkedViewModel viewModel;
    //Instantiate the RecyclerViewAdapter, passing an empty list initially.
    // this data will not be shown until you setAdapter to the RecyclerView view in the Layout
    private VersesMarkedRecyclerViewAdapter rvAdapter;
    //==========================================================

    public static DashboardBibleFragment newInstance(Label mLabel) {
        Bundle args = new Bundle();
        args.putParcelable("mLabel", mLabel);
        DashboardBibleFragment fragment = new DashboardBibleFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.ctx = getActivity();
        this.mLabel = (Label) getArguments().getParcelable("mLabel");
        if (this.mLabel.getPermanent() != 1) {
            setHasOptionsMenu(true);
        }
        rvAdapter = new VersesMarkedRecyclerViewAdapter(this.ctx, new ArrayList<>());
        //get viewmodel class and properties, pass this context so LifeCycles are handled by ViewModel,
        // in case the Activity is destroyed and recreated(screen roation)
        //ViewModel will help us show the exact same data, and resume the application from when the user left last time.
        viewModel = new ViewModelProvider(getActivity()).get(VersesMarkedViewModel.class);
        //viewModel.getUserMutableLiveData().observe(context, verseListUpdateObserver);
        //observerViewModel();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView: DashboardBibleFragment");

        View view = inflater.inflate(R.layout.dashboard_label_fragment, container, false);
        //============================================================================================
        boolean canGoBack = ((AppCompatActivity) this.ctx).getSupportFragmentManager().getBackStackEntryCount() > 0;
        Dashboard.updateCanGoBack(canGoBack, (AppCompatActivity) this.ctx);
        //============================================================================================
//        rvAdapter = new VersesMarkedRecyclerViewAdapter(getActivity(), arrReturned);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        ////////////////////////////////////////////
        this.rvView = (RecyclerView) view.findViewById(R.id.verses_marked_recycler_view);
        rvView.setLayoutManager(new LinearLayoutManager(this.getContext()));
//        rvView.addOnItemTouchListener(new RecyclerItemClickListener(getActivity(), rvView, VersesFragment.this));
        rvView.setAdapter(rvAdapter);//attach the RecyclerView adapter to the RecyclerView View
        ///////////////////////////////////////
        observerViewModel();
    }

    private void observerViewModel() {
        viewModel.getUserMutableLiveData().observe(getViewLifecycleOwner(), (versesMarkedArrayList) -> {
            Log.d(TAG, "observerViewModel: LABEL GOT DATA" + versesMarkedArrayList.size() + "in label: " + this.mLabel.getName());
            //WHEN data is created  pass data and set it in the recyclerview VIEW
            rvAdapter.updateVersesMarkedRecyclerView(versesMarkedArrayList);
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getActivity() instanceof Dashboard) {
            ((Dashboard) getActivity()).getSupportActionBar().setTitle(this.mLabel.getName());
            //Use when we need to reload data
            viewModel.fetchData(this.mLabel.getId());//refresh -> load data
        }
    }

}