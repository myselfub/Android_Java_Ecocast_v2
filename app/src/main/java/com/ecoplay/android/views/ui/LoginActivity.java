package com.ecoplay.android.views.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.ecoplay.android.R;
import com.ecoplay.android.databinding.ActivityLoginBinding;
import com.ecoplay.android.models.model.UserModel;
import com.ecoplay.android.models.repository.LocalRepository;
import com.ecoplay.android.util.FacebookLogin;
import com.ecoplay.android.util.GoogleLogin;
import com.ecoplay.android.util.NaverLogin;
import com.ecoplay.android.viewmodels.LoginViewModel;
import com.facebook.CallbackManager;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.Task;
import com.nhn.android.naverlogin.ui.view.OAuthLoginButton;

import java.util.ArrayList;
import java.util.Map;

public class LoginActivity extends AppCompatActivity implements LifecycleOwner {
    private static final String TAG = LoginActivity.class.getSimpleName();
    private static final int GOOGLE_SIGN_IN = 9001;
    private static final int FACEBOOK_SIGN_IN = 64206;
    private GoogleLogin googleLogin;
    private FacebookLogin facebookLogin;
    private CallbackManager callbackManager;
    private NaverLogin naverLogin;
    private OAuthLoginButton oAuthLoginButton;
    private ViewModelProvider.AndroidViewModelFactory viewModelFactory;
    private LoginViewModel loginViewModel;
    private ActivityLoginBinding activityLoginBinding;
    private Context context;
    private LocalRepository localRepository;
    private MutableLiveData<ArrayList<UserModel>> mutableLiveData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("ActionBar");
        Intent intent = getIntent();
        if (intent.getStringExtra("message") != null) {
            Toast.makeText(this, intent.getStringExtra("message"), Toast.LENGTH_SHORT).show();
        }
        context = this;
        mutableLiveData = new MutableLiveData<>();
        localRepository = new LocalRepository(this);

        SharedPreferences sharedPreferences = this.getSharedPreferences("com.google.android.gms.signin", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.commit();
        sharedPreferences = this.getSharedPreferences("com.facebook.login.AuthorizationClient.WebViewAuthHandler.TOKEN_STORE_KEY", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.clear();
        editor.commit();
        sharedPreferences = this.getSharedPreferences("com.facebook.internal.preferences.APP_GATEKEEPERS", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.clear();
        editor.commit();
        sharedPreferences = this.getSharedPreferences("com.facebook.internal.preferences.APP_SETTINGS", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.clear();
        editor.commit();
        sharedPreferences = this.getSharedPreferences("NaverOAuthLoginPreferenceData", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.clear();
        editor.commit();

        googleLogin = new GoogleLogin(this, mutableLiveData);
//        SignInButton signInButton = findViewById(R.id.btn_google_login);
//        signInButton.setSize(SignInButton.SIZE_STANDARD);

        facebookLogin = new FacebookLogin(mutableLiveData);

        naverLogin = new NaverLogin(this, mutableLiveData);
        oAuthLoginButton = findViewById(R.id.btn_naver_login);
        oAuthLoginButton.setOAuthLoginHandler(naverLogin.getOAuthLoginHandler());

        if (viewModelFactory == null) {
            viewModelFactory = ViewModelProvider.AndroidViewModelFactory.getInstance(getApplication());
        }

        loginViewModel = new ViewModelProvider(this, viewModelFactory).get(LoginViewModel.class);
        loginViewModel.setMutableLiveData(mutableLiveData);
        activityLoginBinding = DataBindingUtil.setContentView(LoginActivity.this, R.layout.activity_login);
        activityLoginBinding.setLifecycleOwner(this);
        activityLoginBinding.setLoginViewModel(loginViewModel);
        mutableLiveData.observe(this, loginViewModelObserver);
        loginViewModel.getBtn().observe(this, btnClickObserver);
    }

    @Override
    protected void onStart() {
        super.onStart();
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        googleLogin.googleUpdateUI(account);
//        mGoogleSignInClient.silentSignIn()
//                .addOnCompleteListener(
//                        this,
//                        new OnCompleteListener<GoogleSignInAccount>() {
//                            @Override
//                            public void onComplete(@NonNull Task<GoogleSignInAccount> task) {
//                                handleSignInResult(task);
//                            }
//                        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == GOOGLE_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            googleLogin.googleHandleSignInResult(task);
        } else if (requestCode == FACEBOOK_SIGN_IN) {
            callbackManager.onActivityResult(requestCode, resultCode, data);
        } else {
            callbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void googleSignIn() {
        startActivityForResult(googleLogin.getGoogleSignIntent(), GOOGLE_SIGN_IN);
    }

    private Observer<Integer> btnClickObserver = integer -> {
        if (integer != null) {
            if (integer == R.id.btn_google_login) {
                googleSignIn();
                loginViewModel.getBtn().setValue(0);
            } else if (integer == R.id.btn_facebook_login) {
                callbackManager = CallbackManager.Factory.create();
                LoginManager loginManager = LoginManager.getInstance();
//                loginManager.logInWithReadPermissions(this, Arrays.asList("public_profile", "email"));
                loginManager.registerCallback(callbackManager, facebookLogin);
                loginViewModel.getBtn().setValue(0);
            } else if (integer == R.id.btn_naver_login) {
                oAuthLoginButton.callOnClick();
                loginViewModel.getBtn().setValue(0);
            } else if (integer == R.id.tv_sign_in) {
                Intent intent = new Intent(this, SignInActivity.class);
                startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                finish();
            } else if (integer == R.id.tv_forget_pw) {
                Intent intent = new Intent(this, PasswordResetActivity.class);
                startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                finish();
            }
        }
    };

    private Observer<ArrayList<UserModel>> loginViewModelObserver = new Observer<ArrayList<UserModel>>() {
        @Override
        public void onChanged(@Nullable ArrayList<UserModel> arrayList) {
            if (arrayList != null && !arrayList.isEmpty()) {
                UserModel userModel = arrayList.get(0);
                if (userModel.getNon_field_errors() != null) {
                    Toast.makeText(LoginActivity.this, userModel.getNon_field_errors(), Toast.LENGTH_SHORT).show();
                } else if (userModel.getDetail() != null) {
                    Toast.makeText(LoginActivity.this, userModel.getDetail(), Toast.LENGTH_SHORT).show();
                } else if (userModel.getEmail() != null) {
                    Toast.makeText(LoginActivity.this, userModel.getEmail(), Toast.LENGTH_SHORT).show();
                    activityLoginBinding.etLoginEmail.requestFocus();
                } else if (userModel.getPassword() != null) {
                    Toast.makeText(LoginActivity.this, userModel.getPassword(), Toast.LENGTH_SHORT).show();
                    activityLoginBinding.etLoginPassword.requestFocus();
                } else if (userModel.isLegacy()) {
                    Map<String, String> user = userModel.getUser();
                    String email = user.get("email");
                    String name = user.get("name");
                    Intent intent = new Intent(context, SignInActivity.class);
                    intent.putExtra("email", email);
                    intent.putExtra("name", name);
                    startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                    finish();
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