package com.zapatatech.santabiblia.fragments.settings;

import android.app.Activity;
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
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import com.zapatatech.santabiblia.R;
import com.zapatatech.santabiblia.adapters.RecyclerView.ResourcesRVA;
import com.zapatatech.santabiblia.utilities.CommonMethods;
import com.zapatatech.santabiblia.viewmodel.ResourcesViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ResourcesAvailableBiblesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ResourcesAvailableBiblesFragment extends Fragment {
    private static final String TAG = "ResourcesAvailableBible";
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private TextView listError;
    private RecyclerView resourcesListRV;
    private ProgressBar loadingView;
    private SwipeRefreshLayout refreshLayout;
    private ResourcesViewModel viewModel;
    //Instantiate the RecyclerViewAdapter, passing an empty list initially.
    // this data will not be shown until you setAdapter to the RecyclerView view in the Layout
    private ResourcesRVA adapter = new ResourcesRVA(new ArrayList<>());

    public ResourcesAvailableBiblesFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ResourcesAvailableBiblesFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ResourcesAvailableBiblesFragment newInstance(String param1, String param2) {
        ResourcesAvailableBiblesFragment fragment = new ResourcesAvailableBiblesFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        //get viewmodel class and properties, pass this context so LifeCycles are handled by ViewModel,
        // in case the Activity is destroyed and recreated(screen roation)
        //ViewModel will help us show the exact same data, and resume the application from when the user left last time.
        viewModel =  new ViewModelProvider(this).get(ResourcesViewModel.class);
        viewModel.refreshBibles();//refresh -> load data
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_resources_available_bibles, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        listError = view.findViewById(R.id.resources_available_error);
        resourcesListRV = view.findViewById(R.id.resources_available_rv);
        loadingView = view.findViewById(R.id.resources_available_loading_view);
        refreshLayout = view.findViewById(R.id.resources_available_swipeRefreshLayout);
        //////////////////////////////////////////////
        resourcesListRV.setLayoutManager(new LinearLayoutManager(getActivity()));
        resourcesListRV.setItemAnimator(null);
        //attach the RecyclerView adapter to the RecyclerView View
        resourcesListRV.setAdapter(adapter);
        /////////////////////////////////////////
        /////////////////////////////////////////
        // Set the listener to be notified when a refresh is triggered via the SWIPE UP gesture.
        //SwipeRefreshLayout refreshLayout
        refreshLayout.setOnRefreshListener(() -> {
            viewModel.refreshBibles();
            refreshLayout.setRefreshing(false);
        });
        ///////////////////////////////////////
        observerViewModel();
        registerWorkManagerListenerTagAll(getActivity());
    }

    private void observerViewModel() {
        // access this variables we created in the ListViewModel and subscribe to Observable
//        public MutableLiveData<List<ResourceModel>> resources = new MutableLiveData<List<ResourceModel>>();
//        public MutableLiveData<Boolean> resourceLoadError = new MutableLiveData<Boolean>();
//        public MutableLiveData<Boolean> loading = new MutableLiveData<Boolean>();

        // HERE the observer (MutableLiveData<List<ResourceModel>> resources) is subscribing(using observe)
        // to an Observable( the code in ...observe(this,()->{});).
        // when we call some function to update data(viewModel.refresh() or any method in the ViewModel Class)
        // The ViewModel, will receive this action(method call) and will trigger the data retrieval(sync/async)
        // and will attach the Result of the operation to the observer: LiveData container (MutableLiveData<List<ResourceModel>> resources)
        // When the data is attached to the container, the result will come back to the Observable in the Callback argument.
        // We can now update the RecyclerView.
        viewModel.resources.observe(getActivity(), (resourceModels) -> {
            if(resourceModels != null) {
                resourcesListRV.setVisibility(View.VISIBLE);
                adapter.updateResources(resourceModels);
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
                    resourcesListRV.setVisibility(View.GONE);
                }
            }
        });
    }

    public void registerWorkManagerListenerTagAll(Activity mActivity){
        WorkManager.getInstance(mActivity).getWorkInfosByTagLiveData(CommonMethods.DOWNLOAD_RESOURCE_TAG)
                .observe((LifecycleOwner) mActivity, new Observer<List<WorkInfo>>() {
                    @Override
                    public void onChanged(@Nullable List<WorkInfo> workInfoList) {
                        if (workInfoList.size() > 0) {
                            for (WorkInfo workInfo: workInfoList) {
                                Log.d(TAG, "startWorkManagerDownloadResource: workInfo.toString(): " + workInfo.toString());
                                Log.d(TAG, "startWorkManagerDownloadResource: workInfo.getState(): " + workInfo.getState());
                                if (workInfo != null && workInfo.getState() == WorkInfo.State.RUNNING) {
                                    //SENT while WM is processing so we can update our own progress bar
                                    String fileNameProcessing = workInfo.getProgress().getString("fileNameProcessing");
                                    int progress = workInfo.getProgress().getInt("progress", -1);
//                                    Log.d(TAG, "startWorkManagerDownloadResource: STILL RUNNING AFTER: " + fileNameProcessing);
//                                    Log.d(TAG, "startWorkManagerDownloadResource: STILL RUNNING AFTER: " + progress);
                                    if(fileNameProcessing!=null){
                                        adapter.updateResourceStateProgressByName(fileNameProcessing, progress);
                                    } else {
                                        //fileName dit not start processing or is not processing
                                    }

                                } else if (workInfo != null && workInfo.getState()  == WorkInfo.State.SUCCEEDED) {
                                    Log.d(TAG, "startWorkManagerDownloadResource: SUCCEEDED " + workInfo.toString());
                                    Log.d(TAG, "startWorkManagerDownloadResource: SUCCEEDED isFinished " + workInfo.getState().isFinished());
                                    boolean myResult = workInfo.getOutputData().getBoolean("downloadComplete", false);
                                    String fileName = workInfo.getOutputData().getString("fileName");
                                    Log.d(TAG, "startWorkManagerDownloadResource: SUCCEEDED myResult: "+myResult);
                                    Log.d(TAG, "startWorkManagerDownloadResource: SUCCEEDED fileName: "+fileName);
                                    if(myResult && fileName != null){
                                        //SUCCESS
                                        adapter.updateResourceStateByName(fileName, "completed");
                                    }
                                    //clears unfinished tasks
                                    //WorkManager.getInstance(MainActivityRT.this).cancelAllWorkByTag("downloadResourceUWID");
                                    //You can call the method pruneWork() on your WorkManager to clear the List<WorkStatus> && List<WorkInfo>
                                    WorkManager.getInstance(mActivity).pruneWork();//kill the workmanager we started before this
                                } else if (workInfo != null && workInfo.getState()  == WorkInfo.State.CANCELLED) {
                                    Log.d(TAG, "startWorkManagerDownloadResource: cancelled " + workInfo.toString());
                                    Log.d(TAG, "startWorkManagerDownloadResource: cancelled isFinished " + workInfo.getState().isFinished());
                                    //boolean myResult = workInfo.getOutputData().getBoolean("downloadComplete", false);
                                    String fileName = workInfo.getProgress().getString("fileNameProcessing");
                                    //Log.d(TAG, "startWorkManagerDownloadResource: cancelled myResult: "+myResult);
                                    Log.d(TAG, "startWorkManagerDownloadResource: cancelled fileName: "+fileName);
                                    if(fileName != null){
                                        //SUCCESS
                                        adapter.updateResourceStateByName(fileName, "cancelled");
                                        //clears unfinished tasks
                                        WorkManager.getInstance(mActivity).cancelUniqueWork(fileName);
                                    }
                                    //You can call the method pruneWork() on your WorkManager to clear the List<WorkStatus> && List<WorkInfo>
                                    WorkManager.getInstance(mActivity).pruneWork();//kill all finished tasks
                                }  else if (workInfo != null && workInfo.getState()  == WorkInfo.State.FAILED) {
                                    Log.d(TAG, "startWorkManagerDownloadResource: FAILED " + workInfo.toString());
                                    Log.d(TAG, "startWorkManagerDownloadResource: FAILED isFinished " + workInfo.getState().isFinished());
                                    boolean myResult = workInfo.getOutputData().getBoolean("downloadComplete", false);
                                    String fileName = workInfo.getOutputData().getString("fileName");
                                    //Log.d(TAG, "startWorkManagerDownloadResource: cancelled myResult: "+myResult);
                                    Log.d(TAG, "startWorkManagerDownloadResource: FAILED fileName: "+fileName);
                                    if(fileName != null){
                                        //FAILED
                                        adapter.updateResourceStateByName(fileName, "failed");
                                    } else {
                                        //kill the workmanager we started before this
                                        WorkManager.getInstance(mActivity).pruneWork();
                                    }
                                }  else if (workInfo != null && workInfo.getState().isFinished()) {
                                    Log.d(TAG, "startWorkManagerDownloadResource: completed " + workInfo.toString());
                                    boolean myResult = workInfo.getOutputData().getBoolean("downloadComplete", false);
                                    String fileName = workInfo.getOutputData().getString("fileName");
                                    Log.d(TAG, "startWorkManagerDownloadResource: completed myResult: "+myResult);
                                    Log.d(TAG, "startWorkManagerDownloadResource: completed fileName: "+fileName);
                                    if(myResult){
                                        //SUCCESS
                                        adapter.updateResourceStateByName(fileName, "completed");
                                    }
                                    //clears unfinished tasks
                                    //WorkManager.getInstance(MainActivityRT.this).cancelAllWorkByTag("downloadResourceUWID");
                                    //You can call the method pruneWork() on your WorkManager to clear the List<WorkStatus> && List<WorkInfo>
                                    WorkManager.getInstance(mActivity).pruneWork();//kill the workmanager we started before this
                                }
                            }
                        }
                    }
                });
    }
}
