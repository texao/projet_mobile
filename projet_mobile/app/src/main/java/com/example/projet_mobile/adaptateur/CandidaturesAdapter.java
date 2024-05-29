package com.example.projet_mobile.adaptateur;
import android.content.Context;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.projet_mobile.R;
import com.example.projet_mobile.model.Candidature;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.List;
import java.text.SimpleDateFormat;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

import androidx.annotation.NonNull;

public class CandidaturesAdapter extends ArrayAdapter<Candidature> {

    private LayoutInflater inflater;

    public CandidaturesAdapter(Context context, List<Candidature> candidatures) {
        super(context, 0, candidatures);
        inflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            view = inflater.inflate(R.layout.item_candidature, parent, false);
        }

        Candidature candidature = getItem(position);

        if (candidature != null) {
            TextView textViewNom = view.findViewById(R.id.nom);
            TextView textViewPrenom = view.findViewById(R.id.prenom);
            TextView textViewmetier = view.findViewById(R.id.metier);
            TextView textViewmdatenaissance = view.findViewById(R.id.datenaissance);
            TextView textViewnationalite = view.findViewById(R.id.nationalite);
            TextView textViewstatus = view.findViewById(R.id.status);
            TextView textViewdatCandidature = view.findViewById(R.id.datCandidature);


            textViewNom.setText(candidature.getNom());
            textViewPrenom.setText(candidature.getPrenom());
            textViewmetier.setText(candidature.getMetier());
            textViewmdatenaissance.setText(candidature.getDateNaissance());
            textViewnationalite.setText(candidature.getNationalite());
            textViewstatus.setText((candidature.getStatut()));
            textViewdatCandidature.setText(new SimpleDateFormat("dd/MM/yyyy").format(candidature.getDateCandidature()));


            Button buttonAccepter = view.findViewById(R.id.btnAccepter);
            Button buttonRefuser = view.findViewById(R.id.btnRefuser);

            // Déclarer buttonAccepter comme final pour être accessible dans la classe anonyme
            final Button finalButtonAccepter = buttonAccepter;

            buttonAccepter.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    accepterCandidature(candidature);
                }
            });

            buttonRefuser.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    refuserCandidature(candidature);
                }
            });
        }


        return view;
    }


    private void accepterCandidature(Candidature candidature) {

        // Construction de la requête pour mettre à jour le statut de la candidature
        String candidatureId = candidature.getId();
        String newStatut = "accepte";

        Log.e("Candidature", "id de la candidature: " + candidatureId);

        TrustManager[] trustAllCerts = new TrustManager[]{
                new X509TrustManager() {
                    @Override
                    public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                    }

                    @Override
                    public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                    }

                    @Override
                    public X509Certificate[] getAcceptedIssuers() {
                        return new X509Certificate[]{};
                    }
                }
        };


        try {

            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());

            HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());

            OkHttpClient client = new OkHttpClient.Builder()
                    .sslSocketFactory(sslContext.getSocketFactory(), (X509TrustManager) trustAllCerts[0])
                    .hostnameVerifier((hostname, session) -> true)
                    .build();


            RequestBody requestBody = new FormBody.Builder()
                    .add("_id", candidatureId)
                    .add("statut", newStatut)
                    .build();

            // Construction de l'URL avec l'identifiant de la candidature
            HttpUrl.Builder urlBuilder = HttpUrl.parse("https://192.168.1.27:8888/candidature/" + candidatureId + "/statut").newBuilder();

            Request request = new Request.Builder()
                    .url(urlBuilder.build().toString())
                    .put(requestBody)
                    .build();


            // Exécution de la requête
            client.newCall(request).enqueue( new Callback() {

                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {

                }

                @Override
                public void onResponse(@NonNull okhttp3.Call call, @NonNull okhttp3.Response response) throws IOException {
                    // Vérification de la réponse
                    if (response.isSuccessful()) {
                        // Mettre à jour l'interface utilisateur sur le thread UI principal
                        new android.os.Handler(Looper.getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getContext(), "Candidature acceptée avec succès", Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else {
                        // Gérer les réponses non réussies
                        Log.e("Candidature", "Échec de la mise à jour du statut: " + response.code());
                    }
                }
            });
        } catch (NoSuchAlgorithmException | KeyManagementException e) {
            e.printStackTrace();
        }


    }






    private void refuserCandidature(Candidature candidature) {


        String candidatureId = candidature.getId();
        String newStatut = "refuse";

        Log.e("Candidature", "id de la candidature: " + candidatureId);

        TrustManager[] trustAllCerts = new TrustManager[]{
                new X509TrustManager() {
                    @Override
                    public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                    }

                    @Override
                    public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                    }

                    @Override
                    public X509Certificate[] getAcceptedIssuers() {
                        return new X509Certificate[]{};
                    }
                }
        };


        try {

            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());

            HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());

            OkHttpClient client = new OkHttpClient.Builder()
                    .sslSocketFactory(sslContext.getSocketFactory(), (X509TrustManager) trustAllCerts[0])
                    .hostnameVerifier((hostname, session) -> true)
                    .build();


            RequestBody requestBody = new FormBody.Builder()
                    .add("_id", candidatureId)
                    .add("statut", newStatut)
                    .build();

            // Construction de l'URL avec l'identifiant de la candidature
            HttpUrl.Builder urlBuilder = HttpUrl.parse("https://192.168.1.27:8888/candidature/" + candidatureId + "/statut").newBuilder();

            Request request = new Request.Builder()
                    .url(urlBuilder.build().toString())
                    .put(requestBody)
                    .build();


            // Exécution de la requête
            client.newCall(request).enqueue( new Callback() {

                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {

                }

                @Override
                public void onResponse(@NonNull okhttp3.Call call, @NonNull okhttp3.Response response) throws IOException {
                    // Vérification de la réponse
                    if (response.isSuccessful()) {
                        new android.os.Handler(Looper.getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {

                                Toast.makeText(getContext(), "Candidature refuse avec succès", Toast.LENGTH_SHORT).show();



                            }
                        });
                    } else {
                        // Gérer les réponses non réussies
                        Log.e("Candidature", "Échec de la mise à jour du statut: " + response.code());
                    }
                }
            });
        } catch (NoSuchAlgorithmException | KeyManagementException e) {
            e.printStackTrace();
        }


    }





}


