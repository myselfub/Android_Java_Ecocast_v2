package com.ecoplay.android.models.network;

import android.content.ContentValues;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

public class HttpConnection {
    private static final String TAG = HttpConnection.class.getSimpleName();
    private final String SERVER_URL = "http://10.0.2.2:8000";

    public String request(String url_, String method, ContentValues params) {
        String Tag = this.TAG + " request()";
        HttpURLConnection httpURLConnection = null;
        StringBuffer stringBuffer = new StringBuffer();
        if (params == null) {
            stringBuffer.append("");
        } else {
            boolean isAnd = false;
            String key;
            String value;
            for (Map.Entry<String, Object> parameter : params.valueSet()) {
                key = parameter.getKey();
                value = parameter.getValue().toString();

                if (isAnd) {
                    stringBuffer.append("&");
                }
                stringBuffer.append(key).append("=").append(value);

                if (!isAnd) {
                    if (params.size() >= 2)
                        isAnd = true;
                }
            }
        }

        try {
            URL url = new URL(SERVER_URL + url_);
            httpURLConnection = (HttpURLConnection) url.openConnection();

            if (method.toLowerCase().equals("post")) {
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setRequestProperty("Accept-Charset", "UTF-8");
                httpURLConnection.setRequestProperty("Context-Type", "application/json;charset=UTF-8");
            } else {
                httpURLConnection.setRequestMethod("GET");
            }

            String strParams = stringBuffer.toString();
            OutputStream os = httpURLConnection.getOutputStream();
            os.write(strParams.getBytes("UTF-8"));
            os.flush();
            os.close();

            if (httpURLConnection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                return null;
            }
            BufferedReader br = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream(), "UTF-8"));

            String line;
            String page = "";
            while ((line = br.readLine()) != null) {
                page += line;
            }
            return page;
        } catch (MalformedURLException murlE) {
            murlE.printStackTrace();
            Log.e("TAG", Tag);
        } catch (IOException ioE) {
            ioE.printStackTrace();
            Log.e("TAG", Tag);
        } finally {
            if (httpURLConnection != null) {
                httpURLConnection.disconnect();
            }
        }
        return null;
    }
}
