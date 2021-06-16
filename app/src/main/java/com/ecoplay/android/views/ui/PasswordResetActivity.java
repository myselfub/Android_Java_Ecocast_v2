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
import com.ecoplay.android.databinding.ActivityPasswordResetBinding;
import com.ecoplay.android.models.model.UserModel;
import com.ecoplay.android.viewmodels.PasswordResetViewModel;

import java.util.ArrayList;

public class PasswordResetActivity extends AppCompatActivity {
    private static final String TAG = PasswordResetActivity.class.getSimpleName();
    private ViewModelProvider.AndroidViewModelFactory viewModelFactory;
    private PasswordResetViewModel passwordResetViewModel;
    private ActivityPasswordResetBinding activityPasswordResetBinding;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_reset);
        context = this;
        if (viewModelFactory == null) {
            viewModelFactory = ViewModelProvider.AndroidViewModelFactory.getInstance(getApplication());
        }

        passwordResetViewModel = new ViewModelProvider(this, viewModelFactory).get(PasswordResetViewModel.class);
        activityPasswordResetBinding = DataBindingUtil.setContentView(PasswordResetActivity.this, R.layout.activity_password_reset);
        activityPasswordResetBinding.setLifecycleOwner(this);
        activityPasswordResetBinding.setPasswordResetViewModel(passwordResetViewModel);
        passwordResetViewModel.getMutableLiveData().observe(this, passwordResetViewModelObserver);
        passwordResetViewModel.getBtn().observe(this, btnClickObserver);
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

    private Observer<ArrayList<UserModel>> passwordResetViewModelObserver = new Observer<ArrayList<UserModel>>() {
        @Override
        public void onChanged(@Nullable ArrayList<UserModel> arrayList) {
            if (arrayList != null && !arrayList.isEmpty()) {
                UserModel userModel = arrayList.get(0);
                if (userModel.getNon_field_errors() != null) {
                    Toast.makeText(PasswordResetActivity.this, userModel.getNon_field_errors(), Toast.LENGTH_SHORT).show();
                } else if (userModel.getDetail() != null) {
                    Toast.makeText(PasswordResetActivity.this, userModel.getDetail(), Toast.LENGTH_SHORT).show();
                    activityPasswordResetBinding.etPasswordResetEmail.requestFocus();
                } else {
                    Intent intent = new Intent(context, LoginActivity.class);
                    intent.putExtra("message", userModel.getEmail());
                    startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                    finish();
                }
            }
        }
    };
}