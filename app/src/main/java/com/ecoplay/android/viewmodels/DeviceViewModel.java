package com.ecoplay.android.viewmodels;

import androidx.databinding.ObservableArrayList;
import androidx.lifecycle.ViewModel;

import com.ecoplay.android.models.model.DeviceModel;

public class DeviceViewModel extends ViewModel {
    private static final String TAG = DeviceViewModel.class.getSimpleName();
    private ObservableArrayList<DeviceModel> observableArrayList;

    public DeviceViewModel() {
        if (observableArrayList == null) {
            observableArrayList = new ObservableArrayList<>();
        }
    }

    public void setObservableArrayList(String name, String macAddress) {
        DeviceModel deviceModel = new DeviceModel();
        if (name == null || name.trim().equals("")) {
            name = "unknown";
        }
        deviceModel.setName(name);
        deviceModel.setMacAddress(macAddress);
        observableArrayList.add(deviceModel);
    }

    public ObservableArrayList<DeviceModel> getObservableArrayList() {
        return observableArrayList;
    }
}