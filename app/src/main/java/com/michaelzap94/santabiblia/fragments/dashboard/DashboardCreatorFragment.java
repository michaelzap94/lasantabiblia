package com.michaelzap94.santabiblia.fragments.dashboard;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.michaelzap94.santabiblia.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class DashboardCreatorFragment extends Fragment {

    public DashboardCreatorFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.dashboard_creator_fragment, container, false);
    }
}
