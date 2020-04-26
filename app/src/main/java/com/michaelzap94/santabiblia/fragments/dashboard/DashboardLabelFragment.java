package com.michaelzap94.santabiblia.fragments.dashboard;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.michaelzap94.santabiblia.Dashboard;
import com.michaelzap94.santabiblia.DatabaseHelper.ContentDBHelper;
import com.michaelzap94.santabiblia.R;
import com.michaelzap94.santabiblia.adapters.DashboardRecyclerViewAdapter;
import com.michaelzap94.santabiblia.models.Label;

import java.util.ArrayList;

public class DashboardLabelFragment extends Fragment {
    private static final String TAG = "DashboardLabelFragment";

    private RecyclerView rvView;
    private DashboardRecyclerViewAdapter rvAdapter;

    public DashboardLabelFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.dashboard_label_fragment, container, false);
        //============================================================================================
        boolean canGoBack = getActivity().getSupportFragmentManager().getBackStackEntryCount()>0;
        Dashboard.updateCanGoBack(canGoBack, (AppCompatActivity)getActivity());
        //============================================================================================
//        ArrayList<Label> arrReturned = ContentDBHelper.getInstance(getActivity()).getAllLabels();
//        Log.d(TAG, "onCreateView: SIZE: " + arrReturned.size());
//        rvAdapter = new DashboardRecyclerViewAdapter(getActivity(), arrReturned);


        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        //////////////////////////////////////////////
//        this.rvView = (RecyclerView) view.findViewById(R.id.dash_recycler_view);
//        rvView.setLayoutManager(new LinearLayoutManager(this.getContext()));
////        rvView.addOnItemTouchListener(new RecyclerItemClickListener(getActivity(), rvView, VersesFragment.this));
//        rvView.setAdapter(rvAdapter);//attach the RecyclerView adapter to the RecyclerView View
        /////////////////////////////////////////
    }
}
