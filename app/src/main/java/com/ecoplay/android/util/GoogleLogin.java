package com.ecoplay.android.util;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.ecoplay.android.R;
import com.ecoplay.android.models.model.UserModel;
import com.ecoplay.android.models.repository.LoginRepository;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;

public class GoogleLogin {
    private static final String TAG = GoogleLogin.class.getSimpleName();
    private Activity activity;
    private GoogleSignInClient googleSignInClient;
    private MutableLiveData<ArrayList<UserModel>> mutableLiveData;
    private LoginRepository loginRepository;

    private GoogleLogin() {
    }

    public GoogleLogin(Activity activity, MutableLiveData<ArrayList<UserModel>> mutableLiveData) {
        this.activity = activity;
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestServerAuthCode(this.activity.getString(R.string.google_client_id))
                .requestIdToken(this.activity.getString(R.string.google_client_id))
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(this.activity, gso);
        this.mutableLiveData = mutableLiveData;
    }

    public Intent getGoogleSignIntent() {
        Intent signIntent = googleSignInClient.getSignInIntent();
        return signIntent;
    }

    private void signOut() {
        googleSignInClient.signOut()
                .addOnCompleteListener(this.activity, task -> googleUpdateUI(null));
    }

    private void revokeAccess() {
        googleSignInClient.revokeAccess()
                .addOnCompleteListener(this.activity, task -> googleUpdateUI(null));
    }

    public void googleHandleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            // Signed in successfully, show authenticated UI.
            googleUpdateUI(account);
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w(TAG, "signInResult:failed code=" + e.getStatusCode());
            e.printStackTrace();
            googleUpdateUI(null);
        }
    }

    public void googleUpdateUI(GoogleSignInAccount googleSignInAccount) {
//        GoogleSignInAccount googleSignInAccount = GoogleSignIn.getLastSignedInAccount(this);
        if (googleSignInAccount != null) {
            String tokenId = googleSignInAccount.getIdToken();
            String authCode = googleSignInAccount.getServerAuthCode();
            String personName = googleSignInAccount.getDisplayName();
            String personGivenName = googleSignInAccount.getGivenName();
            String personFamilyName = googleSignInAccount.getFamilyName();
            String personEmail = googleSignInAccount.getEmail();
            String personId = googleSignInAccount.getId();
            Uri personPhoto = googleSignInAccount.getPhotoUrl();
            if (loginRepository == null) {
                loginRepository = new LoginRepository(mutableLiveData);
            }
            UserModel userModel = new UserModel();
            userModel.setAccess_token(tokenId);
            loginRepository.callGoogleLogin(userModel);
            Log.d(TAG + " token", tokenId);
            Log.d(TAG + " authCode", authCode);
        }
    }
}
