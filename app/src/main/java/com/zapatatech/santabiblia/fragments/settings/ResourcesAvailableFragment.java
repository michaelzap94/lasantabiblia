package com.zapatatech.santabiblia.fragments.settings;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.zapatatech.santabiblia.DatabaseHelper.BibleCreator;
import com.zapatatech.santabiblia.R;
import com.zapatatech.santabiblia.Settings;
import com.zapatatech.santabiblia.adapters.RecyclerView.SettingsResourcesDownloadedRVAdapter;
import com.zapatatech.santabiblia.utilities.CommonMethods;

import java.util.ArrayList;
import java.util.List;

public class ResourcesAvailableFragment extends Fragment {

    private static final String CAN_GO_BACK = "CAN_GO_BACK";
    private static final String TITLE = "Resources Available";
    boolean canGoBack;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private ViewPagerAdapter viewPagerAdapter;

//        @Override
//        public void onCreate(Bundle savedInstanceState) {
//            super.onCreate(savedInstanceState);
//
////            toolbar = (Toolbar) findViewById(R.id.toolbar);
////            setSupportActionBar(toolbar);
////            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//
//        }

    public ResourcesAvailableFragment() {
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
        if (savedInstanceState != null) {
            canGoBack = savedInstanceState.getBoolean(CAN_GO_BACK, false);
        } else {
            canGoBack = getActivity().getSupportFragmentManager().getBackStackEntryCount()>0;
        }
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.settings_resources_available, container, false);
        viewPager = (ViewPager) view.findViewById(R.id.viewpager);
        tabLayout = (TabLayout) view.findViewById(R.id.tabs);



        //============================================================================================
        Settings.updateCanGoBack(canGoBack, (Settings)getActivity(), TITLE);
        //============================================================================================
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        setupViewPager(viewPager);
        tabLayout.setupWithViewPager(viewPager);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(CAN_GO_BACK, canGoBack);
    }

    private void setupViewPager(ViewPager viewPager) {
        viewPagerAdapter = new ViewPagerAdapter(getChildFragmentManager());
        viewPagerAdapter.addFragment(new ResourcesAvailableBiblesFragment(), "BIBLES");
        viewPagerAdapter.addFragment(new ResourcesAvailableExtrasFragment(), "EXTRAS");
        viewPager.setAdapter(viewPagerAdapter);
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }
}

