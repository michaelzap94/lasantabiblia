package com.zapatatech.santabiblia.fragments.settings;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.zapatatech.santabiblia.R;
import com.zapatatech.santabiblia.adapters.RecyclerView.ResourcesRVA;
import com.zapatatech.santabiblia.viewmodel.ResourcesViewModel;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ResourcesAvailableExtrasFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ResourcesAvailableExtrasFragment extends Fragment {
    private static final String TAG = "ResourcesAvailableExtra";
    private TextView listError;
    private TextView listNoResources;
    private RecyclerView resourcesList;
    private ProgressBar loadingView;
    private SwipeRefreshLayout refreshLayout;
    private ResourcesViewModel viewModel;
    //Instantiate the RecyclerViewAdapter, passing an empty list initially.
    // this data will not be shown until you setAdapter to the RecyclerView view in the Layout
    private ResourcesRVA adapter = new ResourcesRVA(new ArrayList<>());

    public ResourcesAvailableExtrasFragment() {
        // Required empty public constructor
    }

    public static ResourcesAvailableExtrasFragment newInstance() {
        return new ResourcesAvailableExtrasFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //get viewmodel class and properties, pass this context so LifeCycles are handled by ViewModel,
        // in case the Activity is destroyed and recreated(screen roation)
        //ViewModel will help us show the exact same data, and resume the application from when the user left last time.
        viewModel =  new ViewModelProvider(this).get(ResourcesViewModel.class);
        viewModel.refreshExtra();//refresh -> load data
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_resources_available_extras, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        listError = view.findViewById(R.id.resources_available_error);
        listNoResources = view.findViewById(R.id.no_resources_available);
        resourcesList = view.findViewById(R.id.resources_available_rv);
        loadingView = view.findViewById(R.id.resources_available_loading_view);
        refreshLayout = view.findViewById(R.id.resources_available_swipeRefreshLayout);
        //////////////////////////////////////////////
        resourcesList.setLayoutManager(new LinearLayoutManager(getActivity()));
        //attach the RecyclerView adapter to the RecyclerView View
        resourcesList.setAdapter(adapter);
        /////////////////////////////////////////
        /////////////////////////////////////////
        // Set the listener to be notified when a refresh is triggered via the SWIPE UP gesture.
        //SwipeRefreshLayout refreshLayout
        refreshLayout.setOnRefreshListener(() -> {
            viewModel.refreshExtra();
            refreshLayout.setRefreshing(false);
        });
        ///////////////////////////////////////
        observerViewModel();
    }


    private void observerViewModel() {
        viewModel.resources.observe(getActivity(), (resourceModels) -> {
            if(resourceModels != null) {
                if(resourceModels.size() > 0){
                    listNoResources.setVisibility(View.GONE);
                    resourcesList.setVisibility(View.VISIBLE);
                    adapter.updateResources(resourceModels);
                } else {
                    resourcesList.setVisibility(View.GONE);
                    listNoResources.setVisibility(View.VISIBLE);
                }
            }
        });
        viewModel.resourceLoadError.observe(getActivity(), isError -> {
            if(isError != null) {
                listError.setVisibility(isError ? View.VISIBLE : View.GONE);
            }
        });
        viewModel.loading.observe(getActivity(), isLoading -> {
            if(isLoading != null) {
                loadingView.setVisibility(isLoading ? View.VISIBLE : View.GONE);
                if(isLoading) {
                    listError.setVisibility(View.GONE);
                    resourcesList.setVisibility(View.GONE);
                }
            }
        });
    }
}
