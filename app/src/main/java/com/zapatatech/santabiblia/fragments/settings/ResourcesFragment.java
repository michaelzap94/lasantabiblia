package com.zapatatech.santabiblia.fragments.settings;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.zapatatech.santabiblia.DatabaseHelper.BibleCreator;
import com.zapatatech.santabiblia.R;
import com.zapatatech.santabiblia.Settings;
import com.zapatatech.santabiblia.adapters.RecyclerView.SettingsResourcesDownloadedRVAdapter;
import com.zapatatech.santabiblia.utilities.CommonMethods;

import java.util.ArrayList;
import java.util.Arrays;

public class ResourcesFragment extends Fragment {
    private static final String TAG = "ResourcesFragment";
    private static final String MAIN_CONTENT_DB = "content.db";
    private MaterialButton seeAvailable;
    private RecyclerView rvView;
    private SettingsResourcesDownloadedRVAdapter adapter;
    private ArrayList<String> list;
    public ResourcesFragment() {
        // Required empty public constructor
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        if(CommonMethods.checkUserStatus(getActivity()) == CommonMethods.USER_ONLINE){
            MenuItem item2= menu.findItem(2);
            MenuItem item3= menu.findItem(3);
            item2.setVisible(false);
            item3.setVisible(false);
        } else {
            MenuItem item= menu.findItem(1);
            item.setVisible(false);
        }

        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        list = new ArrayList<>();
//        String[] resourcesAvailable = BibleCreator.getInstance(this.getContext()).listOfAssetsSpecific("bibles", "es");;
//        list = new ArrayList<String>(Arrays.asList(resourcesAvailable));

        //list = BibleCreator.getInstance(this.getContext()).listOfAllDBAssets();
        for (String dbName: getActivity().databaseList()) {
            if(!dbName.contains("-journal") && !dbName.equals(MAIN_CONTENT_DB)) {
                list.add(dbName);
            }
        }
        adapter = new SettingsResourcesDownloadedRVAdapter(getActivity(), list);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView: ");
        View view =  inflater.inflate(R.layout.settings_resources, container, false);
        seeAvailable = view.findViewById(R.id.resources_see_available_button);
        rvView = (RecyclerView) view.findViewById(R.id.resources_downloaded_rv);


        //rvView.setHasFixedSize(true);
        //rvView.setNestedScrollingEnabled(false);
        rvView.setAdapter(adapter);
        rvView.setLayoutManager(new LinearLayoutManager(this.getContext()));

        //============================================================================================
        setHasOptionsMenu(true);
        boolean canGoBack = getActivity().getSupportFragmentManager().getBackStackEntryCount()>0;
        Settings.updateCanGoBack(canGoBack, (Settings)getActivity(), null);
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