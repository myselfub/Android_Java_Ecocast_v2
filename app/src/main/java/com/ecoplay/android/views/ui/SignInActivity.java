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
import com.ecoplay.android.databinding.ActivitySigninBinding;
import com.ecoplay.android.models.model.UserModel;
import com.ecoplay.android.models.repository.LocalRepository;
import com.ecoplay.android.viewmodels.SignInViewModel;

import java.util.ArrayList;
import java.util.Map;

public class SignInActivity extends AppCompatActivity {
    private static final String TAG = SignInActivity.class.getSimpleName();
    private ViewModelProvider.AndroidViewModelFactory viewModelFactory;
    private SignInViewModel signInViewModel;
    private ActivitySigninBinding activitySigninBinding;
    private LocalRepository localRepository;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);
        localRepository = new LocalRepository(this);
        context = this;
        if (viewModelFactory == null) {
            viewModelFactory = ViewModelProvider.AndroidViewModelFactory.getInstance(getApplication());
        }

        signInViewModel = new ViewModelProvider(this, viewModelFactory).get(SignInViewModel.class);
        activitySigninBinding = DataBindingUtil.setContentView(SignInActivity.this, R.layout.activity_signin);
        activitySigninBinding.setLifecycleOwner(this);
        activitySigninBinding.setSignInViewModel(signInViewModel);
        signInViewModel.getMutableLiveData().observe(this, signInViewModelObserver);
        signInViewModel.getBtn().observe(this, btnClickObserver);
        Intent intent = getIntent();
        if (intent.getStringExtra("email") != null) {
            signInViewModel.setStringEmail(intent.getStringExtra("email"));
            Toast.makeText(this, "Ecocast 기존 회원입니다.", Toast.LENGTH_SHORT).show();
        }
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

    private Observer<ArrayList<UserModel>> signInViewModelObserver = new Observer<ArrayList<UserModel>>() {
        @Override
        public void onChanged(@Nullable ArrayList<UserModel> arrayList) {
            if (arrayList != null && !arrayList.isEmpty()) {
                UserModel userModel = arrayList.get(0);
                if (userModel.getNon_field_errors() != null) {
                    Toast.makeText(SignInActivity.this, userModel.getNon_field_errors(), Toast.LENGTH_SHORT).show();
                } else if (userModel.getDetail() != null) {
                    Toast.makeText(SignInActivity.this, userModel.getDetail(), Toast.LENGTH_SHORT).show();
                } else if (userModel.getUsername() != null) {
                    Toast.makeText(SignInActivity.this, userModel.getUsername(), Toast.LENGTH_SHORT).show();
                    activitySigninBinding.etSignInEmail.requestFocus();
                } else if (userModel.getEmail() != null) {
                    Toast.makeText(SignInActivity.this, userModel.getEmail(), Toast.LENGTH_SHORT).show();
                    activitySigninBinding.etSignInEmail.requestFocus();
                } else if (userModel.getPassword1() != null) {
                    Toast.makeText(SignInActivity.this, userModel.getPassword1(), Toast.LENGTH_SHORT).show();
                    activitySigninBinding.etSignInPassword.requestFocus();
                } else if (userModel.getPassword2() != null) {
                    Toast.makeText(SignInActivity.this, userModel.getPassword2(), Toast.LENGTH_SHORT).show();
                    activitySigninBinding.etSignInPasswordCheck.requestFocus();
                } else {
                    Map<String, String> user = userModel.getUser();
                    String accessToken = userModel.getAccess_token();
                    String email = user.get("email");
                    String name = "";
                    if (user.get("last_name") != null) {
                        name = user.get("last_name");
                    }
                    if (user.get("first_name") != null) {
                        name += user.get("first_name");
                    }
                    localRepository.setLoginInfo(accessToken, email, name);
                    Intent intent = new Intent(context, MainActivity.class);
                    startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                    finish();
                }
            }
        }
    };
}