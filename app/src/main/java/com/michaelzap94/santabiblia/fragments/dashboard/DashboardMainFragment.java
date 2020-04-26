package com.michaelzap94.santabiblia.fragments.dashboard;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.michaelzap94.santabiblia.Dashboard;
import com.michaelzap94.santabiblia.DatabaseHelper.ContentDBHelper;
import com.michaelzap94.santabiblia.R;
import com.michaelzap94.santabiblia.adapters.DashboardRecyclerViewAdapter;
import com.michaelzap94.santabiblia.adapters.VersesRecyclerViewAdapter;
import com.michaelzap94.santabiblia.fragments.ui.tabVerses.VersesFragment;
import com.michaelzap94.santabiblia.models.Label;
import com.michaelzap94.santabiblia.utilities.RecyclerItemClickListener;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class DashboardMainFragment extends Fragment {
    private static final String TAG = "DashboardMainFragment";
    private Button createNewLabel;
    private RecyclerView rvView;
    private DashboardRecyclerViewAdapter rvAdapter;


    public DashboardMainFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.dashboard_fragment, container, false);

        Log.d(TAG, "onCreateView: MAIN");
        //================================================================================================
        boolean canGoBack = getActivity().getSupportFragmentManager().getBackStackEntryCount()>0;
        Dashboard.updateCanGoBack(canGoBack, (AppCompatActivity)getActivity());
        //================================================================================================

        ArrayList<Label> arrReturned = ContentDBHelper.getInstance(getActivity()).getAllLabels();
        Log.d(TAG, "onCreateView: SIZE: " + arrReturned.size());
        rvAdapter = new DashboardRecyclerViewAdapter(getActivity(), arrReturned);

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
    }

    public static void onLabelClickedFromList(Context ctx, Label mLabel) {
        DashboardLabelFragment dashboardLabelFragment = new DashboardLabelFragment(ctx , mLabel);
        FragmentManager fragmentManager = ((AppCompatActivity) ctx).getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.dashboard_fragment, dashboardLabelFragment, "labelFragmentTag");
        fragmentTransaction.addToBackStack(null);//If you don't want later pop more than one back stack, but still want to pop one at a time
        fragmentTransaction.commit();
    }
}
