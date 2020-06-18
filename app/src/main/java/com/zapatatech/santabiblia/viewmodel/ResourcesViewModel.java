package com.zapatatech.santabiblia.viewmodel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.zapatatech.santabiblia.interfaces.retrofit.RetrofitRESTendpointsService;
import com.zapatatech.santabiblia.retrofit.Pojos.POJOResource;
import com.zapatatech.santabiblia.retrofit.RetrofitServiceGenerator;

import java.util.List;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

public class ResourcesViewModel extends ViewModel {
    public static String RESOURCE_TYPE_BIBLES = "bibles";
    public static String RESOURCE_TYPE_EXTRA = "extra";
    //MutableLiveData is an observable where we set values
    public MutableLiveData<List<POJOResource>> resources = new MutableLiveData<List<POJOResource>>();
    public MutableLiveData<Boolean> resourceLoadError = new MutableLiveData<Boolean>();
    public MutableLiveData<Boolean> loading = new MutableLiveData<Boolean>();

    public RetrofitRESTendpointsService resourcesService = RetrofitServiceGenerator.createServiceRx(RetrofitRESTendpointsService.class, null);
    //Dispose of anything inside it
    private CompositeDisposable disposable = new CompositeDisposable();

    public ResourcesViewModel() {
        super();
    }

    //ViewModel receives a call from the view to refresh the information/data
    public void refreshBibles() {
        fetchResources(RESOURCE_TYPE_BIBLES);
    }
    //ViewModel receives a call from the view to refresh the information/data
    public void refreshExtra() {
        fetchResources(RESOURCE_TYPE_EXTRA);
    }

    //Called when app is destroyed, here we need to handle if Screen is rotated.
    @Override
    protected void onCleared() {
        super.onCleared();
        //RxJava: disposed of all processed added with disposable.add();
        disposable.clear();
    }

    private void fetchResources(String type) {
        loading.setValue(true);
        //RxJava: Wrap the API call in a disposable so we can Safely dispose of it, if app is destroyed.
        Single<List<POJOResource>> singleCall;
        if(type == RESOURCE_TYPE_EXTRA ) {
            singleCall = resourcesService.getResourcesExtra();
        } else {
            singleCall = resourcesService.getResourcesByType(RESOURCE_TYPE_BIBLES);
        }

        disposable.add(
                singleCall
                        .subscribeOn(Schedulers.newThread())//enables communication on new Thread background
                        .observeOn(AndroidSchedulers.mainThread())//handle response on UI Thread
                        .subscribeWith(new DisposableSingleObserver<List<POJOResource>>() {//new DisposableSingleObserve so we can call it inside disposable.add()

                            @Override
                            public void onSuccess(List<POJOResource> resourceModels) {
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
