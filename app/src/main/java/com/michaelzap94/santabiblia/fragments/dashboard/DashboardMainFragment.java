package com.michaelzap94.santabiblia.fragments.dashboard;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.michaelzap94.santabiblia.DatabaseHelper.ContentDBHelper;
import com.michaelzap94.santabiblia.R;
import com.michaelzap94.santabiblia.models.Label;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class DashboardMainFragment extends Fragment {
    private static final String TAG = "DashboardMainFragment";
    private Button createNewLabel;

    public DashboardMainFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.dashboard_fragment, container, false);

        ArrayList<Label> arrReturned = ContentDBHelper.getInstance(getActivity()).getAllLabels();
        Log.d(TAG, "onCreateView: SIZE: " + arrReturned.size());

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
        super.onViewCreated(view, savedInstanceState);
    }
}
