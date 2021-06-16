package com.ecoplay.android.notification;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.ecoplay.android.models.model.JsonResultModel;
import com.ecoplay.android.models.model.UserModel;
import com.ecoplay.android.models.network.RetrofitRestAPI;
import com.ecoplay.android.models.network.RetrofitRestAPIService;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class NotificationRepository {
    private static final String TAG = NotificationRepository.class.getSimpleName();

    private RetrofitRestAPI retrofitRestAPI;
    private CompositeDisposable compositeDisposable;
//    private MutableLiveData<ArrayList<UserModel>> mutableLiveData;

    public static void clear(Context context) {
        // TODO
        FirebaseMessaging.getInstance().setAutoInitEnabled(true);
    }

    public static void saveDeviceTokenToServer(final String token) {
        NotificationRepository repo = new NotificationRepository();
        repo.callSaveDeviceTokenToServer(token);
    }

    public NotificationRepository() {
        retrofitRestAPI = RetrofitRestAPIService.getInstance().getRetrofitRestAPI("http://10.0.2.2:8000");
//        this.mutableLiveData = mutableLiveData;
    }

//    public NotificationRepository(MutableLiveData<ArrayList<UserModel>> mutableLiveData, String url) {
//        retrofitRestAPI = RetrofitRestAPIService.getInstance().getRetrofitRestAPI(url);
//        this.mutableLiveData = mutableLiveData;
//    }

    public void callSaveDeviceTokenToServer(String token) {
        if (compositeDisposable == null || compositeDisposable.isDisposed()) {
//            disposable.clear();
            compositeDisposable = new CompositeDisposable();
        }

        Map<String, String> data = new HashMap<>();
        data.put("token", token);
        compositeDisposable.add(retrofitRestAPI.saveDeviceTokenToServer(data)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(result -> onNext(result),
                        e -> onError(e), () -> onComplete()));
    }

    private void onNext(JsonResultModel result) {
        Log.d(TAG, result.getCode());

    }

    private void onError(Throwable t) {
        t.printStackTrace();
    }

    private void onComplete() {
        if (compositeDisposable != null && !compositeDisposable.isDisposed()) {
            compositeDisposable.dispose();
        }
    }

}