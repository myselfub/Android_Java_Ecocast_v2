package com.ecoplay.android.viewmodels;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.ecoplay.android.models.model.VentilationTimeModel;
import com.ecoplay.android.models.repository.VentilationTimeRepository;

import java.util.ArrayList;
import java.util.Map;

public class VentilationTimeViewModel extends ViewModel {
    private static final String TAG = VentilationTimeViewModel.class.getSimpleName();
    private MutableLiveData<ArrayList<VentilationTimeModel>> mutableLiveData;
    private VentilationTimeRepository ventilationTimeRepository;

    private VentilationTimeViewModel() {
    }

    public VentilationTimeViewModel(String url, Map<String, String> queryMap) {
        if (mutableLiveData == null) {
            mutableLiveData = new MutableLiveData<>();
        }
        if (ventilationTimeRepository == null) {
            ventilationTimeRepository = new VentilationTimeRepository(mutableLiveData, url);
        }
        ventilationTimeRepository.callVentilationTime(queryMap);
    }

    public MutableLiveData<ArrayList<VentilationTimeModel>> getMutableLiveData() {
        return mutableLiveData;
    }

    public void setMutableLiveData(ArrayList<VentilationTimeModel> arrayList) {
        mutableLiveData.setValue(arrayList);
    }
}