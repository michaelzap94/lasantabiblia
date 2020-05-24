package com.michaelzap94.santabiblia.fragments.settings;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.michaelzap94.santabiblia.R;
import com.michaelzap94.santabiblia.Settings;

import java.util.ArrayList;
import java.util.List;

public class ResourcesAvailableFragment extends Fragment {

//        private Toolbar toolbar;
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ((Settings) getActivity()).getmCollapsingToolbarLayout().setTitle("Available to Download");
        View view =  inflater.inflate(R.layout.settings_resources_available, container, false);
        viewPager = (ViewPager) view.findViewById(R.id.viewpager);
        tabLayout = (TabLayout) view.findViewById(R.id.tabs);



        //============================================================================================
        boolean canGoBack = getActivity().getSupportFragmentManager().getBackStackEntryCount()>0;
        Settings.updateCanGoBack(canGoBack, (Settings)getActivity());
        //============================================================================================
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        setupViewPager(viewPager);
        tabLayout.setupWithViewPager(viewPager);
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

