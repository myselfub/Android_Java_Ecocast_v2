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

public class PasswordResetViewModel extends ViewModel implements View.OnClickListener {
    private static final String TAG = PasswordResetViewModel.class.getSimpleName();
    private MutableLiveData<String> email;
    private MutableLiveData<Integer> btn;

    MutableLiveData<ArrayList<UserModel>> mutableLiveData;
    private LoginRepository loginRepository;

    public PasswordResetViewModel() {
        email = new MutableLiveData<>();
        email.setValue("");
        btn = new MutableLiveData<>();
        mutableLiveData = new MutableLiveData<>();
    }

    public MutableLiveData<String> getEmail() {
        return email;
    }

    public void setEmail(MutableLiveData<String> email) {
        this.email = email;
    }

    public MutableLiveData<Integer> getBtn() {
        return btn;
    }

    public MutableLiveData<ArrayList<UserModel>> getMutableLiveData() {
        return mutableLiveData;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_password_reset:
                if (loginRepository == null) {
                    loginRepository = new LoginRepository(mutableLiveData);
                }
                Map<String, String> data = new HashMap<>();
                data.put("email", email.getValue());
                loginRepository.callPasswordReset(data);
                break;
            case R.id.btn_back:
                btn.setValue(view.getId());
                break;
        }
    }
}
