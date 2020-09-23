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
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.zapatatech.santabiblia.Dashboard;
import com.zapatatech.santabiblia.R;
import com.zapatatech.santabiblia.Settings;
import com.zapatatech.santabiblia.adapters.RecyclerView.VersesMarkedRecyclerViewAdapter;
import com.zapatatech.santabiblia.fragments.settings.ResourcesAvailableBiblesFragment;
import com.zapatatech.santabiblia.fragments.settings.ResourcesAvailableExtrasFragment;
import com.zapatatech.santabiblia.models.Label;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.zapatatech.santabiblia.models.VersesMarked;
import com.zapatatech.santabiblia.utilities.CommonMethods;
import com.zapatatech.santabiblia.viewmodel.VersesMarkedViewModel;

import java.util.ArrayList;
import java.util.List;


public class DashboardLabelFragment extends Fragment {
    private static final String TAG = "DashboardLabelFragment";
    private Label mLabel;
    private Context ctx;
    private static final String CAN_GO_BACK = "CAN_GO_BACK";
    private static final String TITLE = "Resources Available";
    boolean canGoBack;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private DashboardLabelFragment.ViewPagerAdapter viewPagerAdapter;
    private VersesMarkedViewModel viewModel;

    public DashboardLabelFragment() {
        // Required empty public constructor
    }

    public static DashboardLabelFragment newInstance(Label mLabel) {
        Bundle args = new Bundle();
        args.putParcelable("mLabel", mLabel);
        DashboardLabelFragment fragment = new DashboardLabelFragment();
        fragment.setArguments(args);
        return fragment;
    }

//    @Override
//    public void onPrepareOptionsMenu(Menu menu) {
//        if(CommonMethods.checkUserStatus(getActivity()) == CommonMethods.USER_ONLINE){
//            MenuItem item2= menu.findItem(2);
//            MenuItem item3= menu.findItem(3);
//            item2.setVisible(false);
//            item3.setVisible(false);
//        } else {
//            MenuItem item= menu.findItem(1);
//            item.setVisible(false);
//        }
//
//        super.onPrepareOptionsMenu(menu);
//    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.ctx = getActivity();
        this.mLabel = (Label) getArguments().getParcelable("mLabel");
        if (this.mLabel.getPermanent() != 1) {
            setHasOptionsMenu(true);
        }
        if (savedInstanceState != null) {
            canGoBack = savedInstanceState.getBoolean(CAN_GO_BACK, false);
        } else {
            canGoBack = getActivity().getSupportFragmentManager().getBackStackEntryCount()>0;
        }

        viewModel = new ViewModelProvider(getActivity()).get(VersesMarkedViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.settings_resources_available, container, false);
        viewPager = (ViewPager) view.findViewById(R.id.viewpager);
        tabLayout = (TabLayout) view.findViewById(R.id.tabs);

        //============================================================================================
        Dashboard.updateCanGoBack(canGoBack, (Dashboard)getActivity());
        //============================================================================================
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        setupViewPager(viewPager);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(CAN_GO_BACK, canGoBack);
    }

    //============================================================================================
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
    //============================================================================================


    public void deleteConfimation() {
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

    public void goToEdit() {
        DashboardCreatorFragment dashboardCreatorFragment = new DashboardCreatorFragment(mLabel.getName(), mLabel.getColor(), mLabel.getId());
        FragmentManager fragmentManager = ((AppCompatActivity) ctx).getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.dashboard_fragment, dashboardCreatorFragment, "mainFragmentTagEdit");
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    //============================================================================================

    private void setupViewPager(ViewPager viewPager) {
        viewPagerAdapter = new DashboardLabelFragment.ViewPagerAdapter(getChildFragmentManager());
        viewPagerAdapter.addFragment(DashboardBibleFragment.newInstance(mLabel), "BIBLE");
        viewPagerAdapter.addFragment(DashboardNotesFragment.newInstance(mLabel), "NOTES");
        viewPager.setAdapter(viewPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);
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
