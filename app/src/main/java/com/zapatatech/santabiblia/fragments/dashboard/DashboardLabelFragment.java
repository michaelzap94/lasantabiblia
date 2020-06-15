package com.zapatatech.santabiblia.fragments.dashboard;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.zapatatech.santabiblia.Dashboard;
import com.zapatatech.santabiblia.R;
import com.zapatatech.santabiblia.adapters.RecyclerView.VersesMarkedRecyclerViewAdapter;
import com.zapatatech.santabiblia.models.Label;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.zapatatech.santabiblia.models.VersesMarked;
import com.zapatatech.santabiblia.viewmodel.VersesMarkedViewModel;

import java.util.ArrayList;


public class DashboardLabelFragment extends Fragment {
    private static final String TAG = "DashboardLabelFragment";
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

    public static DashboardLabelFragment newInstance(Label mLabel) {
        Bundle args = new Bundle();
        args.putParcelable("mLabel", mLabel);
        DashboardLabelFragment fragment = new DashboardLabelFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.ctx = getActivity();
        this.mLabel = (Label) getArguments().getParcelable("mLabel");
        if(this.mLabel.getPermanent() != 1){
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
        Log.d(TAG, "onCreateView: DashboardLabelFragment");

        View view =  inflater.inflate(R.layout.dashboard_label_fragment, container, false);
        //============================================================================================
        boolean canGoBack = ((AppCompatActivity) this.ctx).getSupportFragmentManager().getBackStackEntryCount()>0;
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

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.dash_label_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.d(TAG, "onOptionsItemSelected: " + item.getItemId());
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.dash_label_menu_edit:
                goToEdit();
                return true;
            case R.id.dash_label_menu_delete:
                deleteConfimation();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void deleteConfimation(){
        new MaterialAlertDialogBuilder(ctx)
                .setTitle("Do you want to delete this label?")
                .setMessage("You will not be able to recover this label or its contents.")
                .setNegativeButton("Cancel", (dialog, which) -> {
                    dialog.dismiss();
                })
                .setPositiveButton("Delete", (dialog, which) -> {
                    viewModel.deleteLabel(mLabel.getId());
                    ((AppCompatActivity) ctx).getSupportFragmentManager().popBackStack();
                })
                .show();
    }

    public void goToEdit(){
        DashboardCreatorFragment dashboardCreatorFragment = new DashboardCreatorFragment(mLabel.getName(), mLabel.getColor(), mLabel.getId());
        FragmentManager fragmentManager = ((AppCompatActivity) ctx).getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.dashboard_fragment, dashboardCreatorFragment, "mainFragmentTagEdit");
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }
}