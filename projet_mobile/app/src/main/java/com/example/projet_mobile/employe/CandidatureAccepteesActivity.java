package com.example.projet_mobile.employe;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.projet_mobile.R;
import com.example.projet_mobile.adaptateur.CandidatureAccpeteEmployeurAdaptateur;
import com.example.projet_mobile.model.Candidature;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.function.Function;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class CandidatureAccepteesActivity extends AppCompatActivity {
    private ListView listViewcandidature;


    // Déclaration de l'adaptateur
    private CandidatureAccpeteEmployeurAdaptateur candidaturesAdapter;

    // Liste des candidsaut saut@gmail;catures
    private List<Candidature> listeCandidatures = new ArrayList<>();

    private String nomEntreprise;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.candidature_accepte_employeur);


        // Récupérer le nom de l'entreprise depuis l'Intent
        nomEntreprise = getIntent().getStringExtra("nomEntreprise");


        // Initialisation de l'adaptateur
        candidaturesAdapter = new CandidatureAccpeteEmployeurAdaptateur(this, listeCandidatures);
        candidaturesAdapter.setOnContactClickListener(new Function<Candidature, Void>() {
            @Override
            public Void apply(Candidature candidature) {
                getEmailForUser(candidature);
                return null;
            }
        });



        listViewcandidature = findViewById(R.id.listViewcandidature); // Initialisation de listViewAnnonces
        listViewcandidature.setAdapter(candidaturesAdapter);



        // Récupérer la référence de l'ImageView pour le bouton de retour
        ImageView imageViewBack = findViewById(R.id.imageViewBack);

        // Ajouter un écouteur de clic à l'ImageView
        imageViewBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Gérer le clic pour retourner à l'activité précédente
                onBackPressed();
            }
        });


        getOffreFromServer();


    }


    public void getOffreFromServer() {


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
            sslContext.init(null, trustAllCerts, new SecureRandom());

            OkHttpClient client = new OkHttpClient.Builder()
                    .sslSocketFactory(sslContext.getSocketFactory(), (X509TrustManager) trustAllCerts[0])
                    .hostnameVerifier((hostname, session) -> true)
                    .build();


            JSONObject jsonBody = new JSONObject();
            jsonBody.put("nomEntreprise", nomEntreprise);



            RequestBody body = RequestBody.create(jsonBody.toString(), MediaType.parse("application/json; charset=utf-8"));
            Request request = new Request.Builder()
                    .url("https://192.168.1.27:8888/affichecandidatureEmployeur_accepte")
                    .post(body)
                    .build();




            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    // Gérer les erreurs de requête
                    e.printStackTrace();
                    Log.e("TAG", "Erreur lors de la requête vers le serveur: " + e.getMessage());
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    // Assurez-vous que la réponse est réussie
                    if (response.isSuccessful()) {
                        String responseData = response.body().string();
                        Log.d("TAG", "Réponse du serveur: " + responseData);

                        // Analyser les données JSON de la réponse et créer des objets Annonce
                        List<Candidature> candidatures = parseOffreFromJSON(responseData);

                        // Mettre à jour l'interface utilisateur sur le thread UI principal
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                candidaturesAdapter.clear();
                                candidaturesAdapter.addAll(candidatures);
                                candidaturesAdapter.notifyDataSetChanged();
                            }


                        });
                    } else {
                        // Gérer les réponses non réussies
                        Log.d("TAG", "Réponse non réussie: " + response.code());
                    }
                }
            });
        } catch (NoSuchAlgorithmException | KeyManagementException | JSONException e) {
            e.printStackTrace();
        }


}

    // Méthode pour analyser les données JSON et créer des objets Annonce
    private List<Candidature> parseOffreFromJSON(String jsonData) {
        List<Candidature> candidatures = new ArrayList<>();

        try {
            JSONArray jsonArray = new JSONArray(jsonData);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                // Extraire les données de l'objet JSON pour créer un objet Offre emploi
                String id = jsonObject.getString("_id");
                String nom = jsonObject.getString("nom");
                String prenom = jsonObject.getString("prenom");
                String dateNaissanceStr = jsonObject.getString("dateNaissance");
                String nationalite = jsonObject.getString("nationalite");
                String statut = jsonObject.getString("Statut");
                String datePublicationStr = jsonObject.getString("dateCandidature");
                String metier = jsonObject.getString("metier");


                // Convertir la chaîne de caractères en objet Date
                SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
                Date datePublication = sdf1.parse(datePublicationStr);

                // Récupérer le timestamp de la date en millisecondes
                long timestamp1 = datePublication.getTime();

                // Créer un nouvel objet Annonce et l'ajouter à la liste
                Candidature candidature = new Candidature(id, nom, prenom, dateNaissanceStr, nationalite, statut, new Date(timestamp1), metier);
                candidatures.add(candidature);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

        // Mettre à jour la liste listeCandidatures avec les nouvelles candidatures
        listeCandidatures.clear(); // Vider la liste actuelle
        listeCandidatures.addAll(candidatures); // Ajouter toutes les nouvelles candidatures


        return candidatures;
    }




    private void getEmailForUser(Candidature candidature) {

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
            sslContext.init(null, trustAllCerts, new SecureRandom());

            OkHttpClient client = new OkHttpClient.Builder()
                    .sslSocketFactory(sslContext.getSocketFactory(), (X509TrustManager) trustAllCerts[0])
                    .hostnameVerifier((hostname, session) -> true)
                    .build();


            // Créer le corps de la requête JSON
            JSONObject jsonBody = new JSONObject();


            // Créer la requête HTTP
            RequestBody body = RequestBody.create(jsonBody.toString(), MediaType.parse("application/json; charset=utf-8"));
            Request request = new Request.Builder()
                    .url("https://192.168.1.27:8888/get_user_email")
                    .post(body)
                    .build();


            // Envoi de la requête asynchrone
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    // Gérer les erreurs de requête
                    e.printStackTrace();
                    Log.e("TAG", "Erreur lors de la requête pour récupérer l'email de l'utilisateur: " + e.getMessage());
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    // Assurez-vous que la réponse est réussie
                    if (response.isSuccessful()) {
                        String responseData = response.body().string();
                        Log.d("TAG", "Réponse du serveur pour récupérer l'email de l'utilisateur: " + responseData);

                        // Extraire l'e-mail de la réponse JSON
                        try {
                            JSONArray jsonResponse = new JSONArray(responseData);
                            if (jsonResponse.length() > 0) {
                                String email = jsonResponse.getString(0);  // Récupère le premier e-mail

                                // Appeler la méthode pour contacter l'utilisateur par e-mail
                                contactUserByEmail(email, candidature);
                            } else {
                                Log.e("TAG", "Aucun e-mail trouvé dans la réponse");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else {
                        // Gérer les réponses non réussies
                        Log.d("TAG", "Réponse non réussie pour récupérer l'email de l'utilisateur: " + response.code());
                    }
                }
            });
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        } catch (KeyManagementException e) {
            throw new RuntimeException(e);
        }
    }

        // Méthode pour contacter l'utilisateur par e-mail
    private void contactUserByEmail(String email, Candidature candidature) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), "Contactez l'utilisateur par e-mail: " + email, Toast.LENGTH_SHORT).show();
            }
        });
    }



}
