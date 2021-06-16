package com.ecoplay.android.viewmodels;

import android.view.View;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.ecoplay.android.R;
import com.ecoplay.android.models.model.UserModel;
import com.ecoplay.android.models.repository.LoginRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class PasswordChangeViewModel extends ViewModel implements View.OnClickListener {
    private static final String TAG = PasswordChangeViewModel.class.getSimpleName();
    private MutableLiveData<String> newPassword;
    private MutableLiveData<String> newPasswordCheck;
    private MutableLiveData<Integer> btn;
    private String token;

    MutableLiveData<ArrayList<UserModel>> mutableLiveData;
    private LoginRepository loginRepository;

    public PasswordChangeViewModel() {
        newPassword = new MutableLiveData<>();
        newPassword.setValue("");
        newPasswordCheck = new MutableLiveData<>();
        newPasswordCheck.setValue("");
        token = "";
        btn = new MutableLiveData<>();
        mutableLiveData = new MutableLiveData<>();
    }

    public MutableLiveData<String> getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(MutableLiveData<String> newPassword) {
        this.newPassword = newPassword;
    }

    public MutableLiveData<String> getNewPasswordCheck() {
        return newPasswordCheck;
    }

    public void setNewPasswordCheck(MutableLiveData<String> newPasswordCheck) {
        this.newPasswordCheck = newPasswordCheck;
    }

    public MutableLiveData<Integer> getBtn() {
        return btn;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public MutableLiveData<ArrayList<UserModel>> getMutableLiveData() {
        return mutableLiveData;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_password_change:
                if (loginRepository == null) {
                    loginRepository = new LoginRepository(mutableLiveData);
                }
                Map<String, String> data = new HashMap<>();
                data.put("new_password1", newPassword.getValue());
                data.put("new_password2", newPasswordCheck.getValue());
                loginRepository.callPasswordChange(token, data);
                break;
            case R.id.btn_back:
                btn.setValue(view.getId());
                break;
        }
    }
}
