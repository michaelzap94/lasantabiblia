package com.michaelzap94.santabiblia.fragments.settings;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.preference.PreferenceFragmentCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.michaelzap94.santabiblia.DatabaseHelper.BibleCreator;
import com.michaelzap94.santabiblia.R;
import com.michaelzap94.santabiblia.Settings;
import com.michaelzap94.santabiblia.adapters.RecyclerView.SettingsResourcesDownloadedRVAdapter;

import java.util.ArrayList;
import java.util.Arrays;

public class ResourcesFragment extends Fragment {
    private static final String TAG = "ResourcesFragment";
    private MaterialButton seeAvailable;
    private RecyclerView rvView;
    private SettingsResourcesDownloadedRVAdapter adapter;
    private ArrayList<String> list;
    public ResourcesFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //list = new ArrayList<>();
        String[] resourcesAvailable = BibleCreator.getInstance(this.getContext()).listOfDBAssets();
        list = new ArrayList<String>(Arrays.asList(resourcesAvailable));
        adapter = new SettingsResourcesDownloadedRVAdapter(getActivity(), list);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView: ");
        ((Settings) getActivity()).getmCollapsingToolbarLayout().setTitle("Manage Resources");
        View view =  inflater.inflate(R.layout.settings_resources, container, false);
        seeAvailable = view.findViewById(R.id.resources_see_available_button);
        rvView = (RecyclerView) view.findViewById(R.id.resources_downloaded_rv);


        //rvView.setHasFixedSize(true);
        //rvView.setNestedScrollingEnabled(false);
        rvView.setAdapter(adapter);
        rvView.setLayoutManager(new LinearLayoutManager(this.getContext()));

        //============================================================================================
        boolean canGoBack = getActivity().getSupportFragmentManager().getBackStackEntryCount()>0;
        Settings.updateCanGoBack(canGoBack, (Settings)getActivity());
        //============================================================================================
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onViewCreated: ");
        seeAvailable.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {

                ResourcesAvailableFragment fragment = new ResourcesAvailableFragment();
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.activity_settings_fragment_item, fragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });

        //observerViewModelRepository();


    }

}