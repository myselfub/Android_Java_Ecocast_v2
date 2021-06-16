package com.ecoplay.android.util;

import android.app.Activity;
import android.os.Looper;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.ecoplay.android.R;
import com.ecoplay.android.models.model.UserModel;
import com.ecoplay.android.models.repository.LoginRepository;
import com.nhn.android.naverlogin.OAuthLogin;
import com.nhn.android.naverlogin.OAuthLoginHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class NaverLogin {
    private static final String TAG = NaverLogin.class.getSimpleName();
    private final String naverApiURL = "https://openapi.naver.com/v1/nid/me";
    private OAuthLogin oAuthLogin;
    private Activity activity;
    private MutableLiveData<ArrayList<UserModel>> mutableLiveData;
    private LoginRepository loginRepository;

    private NaverLogin() {
    }

    public NaverLogin(Activity activity, MutableLiveData<ArrayList<UserModel>> mutableLiveData) {
        this.activity = activity;
        oAuthLogin = OAuthLogin.getInstance();
        oAuthLogin.logout(activity);
        String clientId = activity.getString(R.string.naver_oauth_client_id);
        String clientSecret = activity.getString(R.string.naver_oauth_client_secret);
        String clientName = activity.getString(R.string.naver_oauth_client_name);
        oAuthLogin.init(this.activity, clientId, clientSecret, clientName);
        this.mutableLiveData = mutableLiveData;
    }

    private OAuthLoginHandler oAuthLoginHandler = new OAuthLoginHandler(Looper.getMainLooper()) {
        @Override
        public void run(boolean b) {
            if (b) {
                loginRepository = new LoginRepository(mutableLiveData);
                String accessToken = oAuthLogin.getAccessToken(activity);
                UserModel userModel = new UserModel();
                userModel.setAccess_token(accessToken);
                loginRepository.callNaverLogin(userModel);
                Log.d(TAG + " token", accessToken);
                String refreshToekn = oAuthLogin.getRefreshToken(activity);
                getProfile(accessToken);
            } else {
                Log.e(TAG, oAuthLogin.getLastErrorCode(activity) + ", " + oAuthLogin.getLastErrorDesc(activity));
            }
        }
    };

    public OAuthLoginHandler getOAuthLoginHandler() {
        return oAuthLoginHandler;
    }

    private void getProfile(String token) {
        Map<String, String> requestHeaders = new HashMap<>();
        String header = "Bearer " + token;
        requestHeaders.put("Authorization", header);
        Thread thread = new Thread() {
            @Override
            public void run() {
                String responseBody = get(naverApiURL, requestHeaders);
                try {
                    JSONObject jObject = new JSONObject(responseBody);
                    JSONObject json = (JSONObject) jObject.get("response");
                    String email = (String) json.get("email");
                    String name = (String) json.get("name");
                    Log.d(TAG + " Info", email + " / " + name);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
        thread.start();
    }

    private String get(String apiUrl, Map<String, String> requestHeaders) {
        HttpURLConnection con = connect(apiUrl);
        try {
            con.setRequestMethod("GET");
            for (Map.Entry<String, String> header : requestHeaders.entrySet()) {
                con.setRequestProperty(header.getKey(), header.getValue());
            }

            int responseCode = con.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) { // 정상 호출
                return readBody(con.getInputStream());
            } else { // 에러 발생
                return readBody(con.getErrorStream());
            }
        } catch (IOException e) {
            throw new RuntimeException("API 요청과 응답 실패", e);
        } finally {
            con.disconnect();
        }
    }


    private HttpURLConnection connect(String apiUrl) {
        try {
            URL url = new URL(apiUrl);
            return (HttpURLConnection) url.openConnection();
        } catch (MalformedURLException e) {
            throw new RuntimeException("API URL이 잘못되었습니다. : " + apiUrl, e);
        } catch (IOException e) {
            throw new RuntimeException("연결이 실패했습니다. : " + apiUrl, e);
        }
    }

    private String readBody(InputStream body) {
        InputStreamReader streamReader = new InputStreamReader(body);

        try (BufferedReader lineReader = new BufferedReader(streamReader)) {
            StringBuilder responseBody = new StringBuilder();

            String line;
            while ((line = lineReader.readLine()) != null) {
                responseBody.append(line);
            }

            return responseBody.toString();
        } catch (IOException e) {
            throw new RuntimeException("API 응답을 읽는데 실패했습니다.", e);
        }
    }

}
