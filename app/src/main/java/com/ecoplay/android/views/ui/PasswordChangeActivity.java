package com.ecoplay.android.views.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.ecoplay.android.R;
import com.ecoplay.android.databinding.ActivityPasswordChangeBinding;
import com.ecoplay.android.models.model.UserModel;
import com.ecoplay.android.models.repository.LocalRepository;
import com.ecoplay.android.viewmodels.PasswordChangeViewModel;

import java.util.ArrayList;

public class PasswordChangeActivity extends AppCompatActivity {
    private static final String TAG = PasswordChangeActivity.class.getSimpleName();
    private ViewModelProvider.AndroidViewModelFactory viewModelFactory;
    private PasswordChangeViewModel passwordChangeViewModel;
    private ActivityPasswordChangeBinding activityPasswordChangeBinding;
    private LocalRepository localRepository;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_change);
        context = this;
        if (viewModelFactory == null) {
            viewModelFactory = ViewModelProvider.AndroidViewModelFactory.getInstance(getApplication());
        }

        passwordChangeViewModel = new ViewModelProvider(this, viewModelFactory).get(PasswordChangeViewModel.class);
        localRepository = new LocalRepository(this);
        String token = localRepository.getLoginInfo().get("token");
        passwordChangeViewModel.setToken(token);
        activityPasswordChangeBinding = DataBindingUtil.setContentView(PasswordChangeActivity.this, R.layout.activity_password_change);
        activityPasswordChangeBinding.setLifecycleOwner(this);
        activityPasswordChangeBinding.setPasswordChangeViewModel(passwordChangeViewModel);
        passwordChangeViewModel.getMutableLiveData().observe(this, passwordChangeViewModelObserver);
        passwordChangeViewModel.getBtn().observe(this, btnClickObserver);
    }

    private Observer<Integer> btnClickObserver = integer -> {
        if (integer != null) {
            if (integer == R.id.btn_back) {
                Intent intent = new Intent(this, LoginActivity.class);
                startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                finish();
            }
        }
    };

    private Observer<ArrayList<UserModel>> passwordChangeViewModelObserver = new Observer<ArrayList<UserModel>>() {
        @Override
        public void onChanged(@Nullable ArrayList<UserModel> arrayList) {
            if (arrayList != null && !arrayList.isEmpty()) {
                UserModel userModel = arrayList.get(0);
                if (userModel.getNon_field_errors() != null) {
                    Toast.makeText(PasswordChangeActivity.this, userModel.getNon_field_errors(), Toast.LENGTH_SHORT).show();
                } else if (userModel.getNew_password1() != null) {
                    Toast.makeText(PasswordChangeActivity.this, userModel.getNew_password1(), Toast.LENGTH_SHORT).show();
                    activityPasswordChangeBinding.etPasswordChangeNewPassword.requestFocus();
                } else if (userModel.getNew_password2() != null) {
                    Toast.makeText(PasswordChangeActivity.this, userModel.getNew_password2(), Toast.LENGTH_SHORT).show();
                    activityPasswordChangeBinding.etPasswordChangeNewPasswordCheck.requestFocus();
                } else if (userModel.getDetail() != null) {
                    String detail = userModel.getDetail();
                    if (detail.toLowerCase().startsWith("new")) {
                        Intent intent = new Intent(context, MainActivity.class);
                        startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                        finish();
                    } else {
                        Toast.makeText(PasswordChangeActivity.this, detail, Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }
    };
}