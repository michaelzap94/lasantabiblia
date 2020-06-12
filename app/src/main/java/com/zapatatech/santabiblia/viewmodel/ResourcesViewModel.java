package com.zapatatech.santabiblia.viewmodel;

import android.app.ProgressDialog;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.zapatatech.santabiblia.interfaces.retrofit.RetrofitAuthService;
import com.zapatatech.santabiblia.interfaces.retrofit.RetrofitRESTendpointsService;
import com.zapatatech.santabiblia.models.AuthInfo;
import com.zapatatech.santabiblia.models.Resource;
import com.zapatatech.santabiblia.utilities.RetrofitServiceGenerator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Call;
import retrofit2.Callback;

public class ResourcesViewModel extends ViewModel {
    //MutableLiveData is an observable where we set values
    public MutableLiveData<List<Resource>> resources = new MutableLiveData<List<Resource>>();
    public MutableLiveData<Boolean> resourceLoadError = new MutableLiveData<Boolean>();
    public MutableLiveData<Boolean> loading = new MutableLiveData<Boolean>();

    public RetrofitRESTendpointsService resourcesService = RetrofitServiceGenerator.createServiceRx(RetrofitRESTendpointsService.class, null);
    //Dispose of anything inside it
    private CompositeDisposable disposable = new CompositeDisposable();

    public ResourcesViewModel() {
        super();
    }

    //ViewModel receives a call from the view to refresh the information/data
    public void refresh() {
        fetchResources();
    }

    //Called when app is destroyed, here we need to handle if Screen is rotated.
    @Override
    protected void onCleared() {
        super.onCleared();
        //RxJava: disposed of all processed added with disposable.add();
        disposable.clear();
    }

    private void fetchResources() {
        loading.setValue(true);
        //RxJava: Wrap the API call in a disposable so we can Safely dispose of it, if app is destroyed.
        //Single<List<Resource>> singleCall = resourcesService.getResourcesAll();

        disposable.add(
                resourcesService.getResourcesAll()
                        .subscribeOn(Schedulers.newThread())//enables communication on new Thread background
                        .observeOn(AndroidSchedulers.mainThread())//handle response on UI Thread
                        .subscribeWith(new DisposableSingleObserver<List<Resource>>() {//new DisposableSingleObserve so we can call it inside disposable.add()

                            @Override
                            public void onSuccess(List<Resource> resourceModels) {
                                //set the resourceModels DATA to the MutableLiveData(Object that creates values)
                                // so that this is returned in the callback in the MainActivity and refreshed the RecyclerView
                                resources.setValue(resourceModels);
                                resourceLoadError.setValue(false);
                                loading.setValue(false);
                            }

                            @Override
                            public void onError(Throwable e) {
                                resourceLoadError.setValue(true);
                                loading.setValue(false);
                                e.printStackTrace();
                            }
                        })
        );
    }
}
