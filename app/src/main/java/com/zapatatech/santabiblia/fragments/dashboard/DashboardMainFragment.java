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
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.zapatatech.santabiblia.Dashboard;
import com.zapatatech.santabiblia.R;
import com.zapatatech.santabiblia.adapters.RecyclerView.DashboardRecyclerViewAdapter;
import com.zapatatech.santabiblia.models.Label;
import com.zapatatech.santabiblia.viewmodel.VersesMarkedViewModel;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class DashboardMainFragment extends Fragment {
    private static final String TAG = "DashboardMainFragment";
    private Button createNewLabel;
    private RecyclerView rvView;
    private DashboardRecyclerViewAdapter rvAdapter;
    private VersesMarkedViewModel viewModel;
    private ArrayList<Label> allLabels = new ArrayList<>();

    public DashboardMainFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(getActivity()).get(VersesMarkedViewModel.class);
        viewModel.getAllLabels();
        rvAdapter = new DashboardRecyclerViewAdapter(getActivity(), allLabels);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.dashboard_fragment, container, false);
        //================================================================================================
        boolean canGoBack = getActivity().getSupportFragmentManager().getBackStackEntryCount()>0;
        Dashboard.updateCanGoBack(canGoBack, (AppCompatActivity)getActivity());
        //================================================================================================
        createNewLabel = view.findViewById(R.id.dash_new_button);
        createNewLabel.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                DashboardCreatorFragment dashboardCreatorFragment = new DashboardCreatorFragment();
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.dashboard_fragment, dashboardCreatorFragment, "mainFragmentTag");
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        //////////////////////////////////////////////
        this.rvView = (RecyclerView) view.findViewById(R.id.dash_recycler_view);
        rvView.setLayoutManager(new LinearLayoutManager(this.getContext()));
//        rvView.addOnItemTouchListener(new RecyclerItemClickListener(getActivity(), rvView, VersesFragment.this));
        rvView.setAdapter(rvAdapter);//attach the RecyclerView adapter to the RecyclerView View
        /////////////////////////////////////////
        observerViewModel();
        Log.d(TAG, "onViewCreated: ");
    }

    private void observerViewModel() {
        viewModel.getAllLabelsLiveData().observe(getActivity(), (labels) -> {
            Log.d(TAG, "observerViewModel: LABEL GOT DATA" + labels.size() + "in label: Mem");
            //WHEN data is created  pass data and set it in the updateVersesMarkedViewPager VIEW
            rvAdapter.refreshData(labels);
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getActivity() instanceof Dashboard) {
            ((Dashboard) getActivity()).getSupportActionBar().setTitle(R.string.dashboard);
        }
        viewModel.getAllLabels();
        Log.d(TAG, "onResume: ");
    }

    public static void onLabelClickedFromList(Context ctx, Label mLabel) {
        DashboardLabelFragment dashboardLabelFragment = DashboardLabelFragment.newInstance(mLabel);
        FragmentManager fragmentManager = ((AppCompatActivity) ctx).getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.dashboard_fragment, dashboardLabelFragment, "labelFragmentTag");
        fragmentTransaction.addToBackStack(null);//If you don't want later pop more than one back stack, but still want to pop one at a time
        fragmentTransaction.commit();
    }
}
