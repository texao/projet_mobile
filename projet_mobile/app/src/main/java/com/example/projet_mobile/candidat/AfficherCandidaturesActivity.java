package com.example.projet_mobile.candidat;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.projet_mobile.R;
import com.example.projet_mobile.adaptateur.CandidaturesAdapter;
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
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class AfficherCandidaturesActivity  extends AppCompatActivity {
    private ListView listViewcandidature;
    String nomUtilisateur;
    String prenomUtilisateur;
    String dateNaissanceUtilisateur;

    private EditText editTextMetier;

    private EditText editTextDate;
    private Date dateFiltree;


    // Déclaration de l'adaptateur
    private CandidaturesAdapter candidaturesAdapter;

    // Liste des candidatures
    private List<Candidature> listeCandidatures = new ArrayList<>();


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.listecandidature);

        // Initialisation de l'adaptateur
        candidaturesAdapter = new CandidaturesAdapter(this, listeCandidatures);


        // Récupérer les informations d'inscription passées depuis l'activité connexion
        Intent intent = getIntent();


        if (intent != null) {
            nomUtilisateur = intent.getStringExtra("nomUtilisateur");
            prenomUtilisateur = intent.getStringExtra("prenomUtilisateur");
            dateNaissanceUtilisateur = intent.getStringExtra("dateNaissanceUtilisateur");

        }


        listViewcandidature = findViewById(R.id.listViewcandidature); // Initialisation de listViewAnnonces


        listViewcandidature.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Récupérer l'offre sélectionnée
                Candidature candidature = (Candidature) parent.getItemAtPosition(position);
            }
        });




        editTextMetier = findViewById(R.id.editTextMetier);



        // Associer un écouteur de texte au champ texte
        editTextMetier.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Ne rien faire avant que le texte ne change
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Log.d("AfficherCandidatures", "onTextChanged: Texte du champ de texte changé");
                // Filtrer les candidatures à chaque changement de texte
                filtrerParMetier();
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Ne rien faire après que le texte a changé
            }
        });




        // Récupérer la référence de l'ImageView pour le bouton de retour
        ImageView imageViewBack = findViewById(R.id.imageViewBack);

        // Ajouter un écouteur de clic à l'ImageView
        imageViewBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Gérer le clic pour retourner à l'activité précédente (connexion)
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


            Request request = new Request.Builder()
                    .url("https://192.168.1.27:8888/affichecandidature")
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
                                // Mettre à jour l'adaptateur de la ListView avec la nouvelle liste d'annonces
                                CandidaturesAdapter Candidature = new CandidaturesAdapter(AfficherCandidaturesActivity.this, candidatures);
                                listViewcandidature.setAdapter(Candidature);
                            }

                        });
                    } else {
                        // Gérer les réponses non réussies
                        Log.d("TAG", "Réponse non réussie: " + response.code());
                    }
                }
            });
        } catch (NoSuchAlgorithmException | KeyManagementException e) {
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
                Candidature candidature = new Candidature(id,nom, prenom, dateNaissanceStr, nationalite, statut, new Date(timestamp1), metier);
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


    // Méthode pour filtrer les candidatures en fonction des critères spécifiés
    private void filtrerParMetier() {
        // Récupérer le texte entré dans le champ de recherche
        String critereMetier = editTextMetier.getText().toString().trim().toLowerCase();
        Log.d("Filtrage", "Critère de recherche : " + critereMetier);

        // Créer une liste pour stocker les candidatures filtrées
        List<Candidature> candidaturesFiltrees = new ArrayList<>();

        // Parcourir toutes les candidatures
        for (Candidature candidature : listeCandidatures) {
            String metier = candidature.getMetier().toLowerCase();
            Log.d("Filtrage", "Métier actuel : " + metier);

            // Vérifier si le métier contient le critère de recherche
            if (metier.contains(critereMetier)) {
                Log.d("Filtrage", "Ajout de la candidature : " + candidature);
                candidaturesFiltrees.add(candidature);
            }
        }

        // Créer un nouvel adaptateur avec les candidatures filtrées
        CandidaturesAdapter adapter = new CandidaturesAdapter(this, candidaturesFiltrees);

        // Mettre à jour la ListView avec le nouvel adaptateur
        listViewcandidature.setAdapter(adapter);

    }





    // Gérer le clic sur le bouton de retour en arrière dans l'actionBar
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // Retourner à l'activité précédente (connexion)
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }




}