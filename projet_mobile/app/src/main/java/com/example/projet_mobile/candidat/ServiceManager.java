package com.example.projet_mobile.candidat;

import android.util.Log;

import java.io.IOException;

import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class ServiceManager {

    private static final String SERVER_URL = "http://mongodb://localhost:27017:8888";
    private static final String TAG = "ServerManager";

    private final OkHttpClient client = new OkHttpClient();

    public void authenticateUser(String email, String password, Callback callback) {
        RequestBody body = new FormBody.Builder()
                .add("email", email)
                .add("motDePasse", password)
                .build();

        Request request = new Request.Builder()
                .url(SERVER_URL + "/connexion")
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(okhttp3.Call call, IOException e) {
                Log.e(TAG, "Erreur de connexion : " + e.getMessage());
                callback.onFailure(call, e);
            }

            @Override
            public void onResponse(okhttp3.Call call, okhttp3.Response response) throws IOException {
                callback.onResponse(call, response);
            }
        });
    }

}
