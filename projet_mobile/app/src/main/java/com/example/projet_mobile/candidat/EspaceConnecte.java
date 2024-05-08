package com.example.projet_mobile.candidat;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.projet_mobile.R;
import com.example.projet_mobile.adaptateur.OffreEmploiAdapter;
import com.example.projet_mobile.model.Offre_emploi;

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

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.bson.types.ObjectId;


public class EspaceConnecte  extends AppCompatActivity {
    private ListView listViewOffre;
    String nomUtilisateur ;
    String prenomUtilisateur;
    String dateNaissanceUtilisateur;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.espaceconnecte);

        // Récupérer les informations d'inscription passées depuis l'activité connexion
        Intent intent = getIntent();


        if (intent != null) {
            nomUtilisateur = intent.getStringExtra("nomUtilisateur");
            prenomUtilisateur = intent.getStringExtra("prenomUtilisateur");
            dateNaissanceUtilisateur = intent.getStringExtra("dateNaissanceUtilisateur");

        }




        listViewOffre = findViewById(R.id.listViewOffre); // Initialisation de listViewAnnonces
        Log.d("Tag", "page offre");

        // Ajouter un écouteur d'événements à la ListView
        listViewOffre.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Récupérer l'offre sélectionnée
                Offre_emploi offreEmploi = (Offre_emploi) parent.getItemAtPosition(position);

                String offreId = offreEmploi.getId();
                Log.d("EspaceConnecte", "EspaceConnecte ID de l'offre extrait avec succès: " + offreId);

                // Ouvrir le formulaire de candidature pour cette offre
                ouvrirFormulaireCandidature(offreEmploi, nomUtilisateur, prenomUtilisateur, dateNaissanceUtilisateur, offreId);
            }
        });



        Button buttonAfficherCandidatures = findViewById(R.id.buttonAfficherCandidatures);
        buttonAfficherCandidatures.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Rediriger vers une nouvelle activité ou exécuter une action pour afficher les candidatures
                Intent intent = new Intent(EspaceConnecte.this, AfficherCandidaturesActivity.class);
                startActivity(intent);
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
                    .url("https://192.168.1.27:8888/offre")
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

                    if (response.isSuccessful()) {
                        String responseData = response.body().string();
                        Log.d("TAG", "Réponse du serveur: " + responseData);

                        // Analyser les données JSON de la réponse et créer des objets Annonce
                        List<Offre_emploi> offreEmplois = parseOffreFromJSON(responseData);

                        // Mettre à jour l'interface utilisateur sur le thread UI principal
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                // Mettre à jour l'adaptateur de la ListView avec la nouvelle liste d'annonces
                                OffreEmploiAdapter offreEmploiAdapter = new OffreEmploiAdapter(EspaceConnecte.this, offreEmplois);
                                listViewOffre.setAdapter(offreEmploiAdapter);
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
    private List<Offre_emploi> parseOffreFromJSON(String jsonData) {
        List<Offre_emploi> offreEmplois = new ArrayList<>();

        try {
            JSONArray jsonArray = new JSONArray(jsonData);
            for (int i = 0; i < jsonArray.length(); i++) {
                // Extraire les données de l'objet JSON pour créer un objet Offre emploi
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String nom = jsonObject.getString("nom");
                String metier = jsonObject.getString("metier");
                String description = jsonObject.getString("Description");
                String periode = jsonObject.getString("Periode");
                int remuneration = jsonObject.getInt("Remuneration");
                String datePublicationStr = jsonObject.getString("datePublication");


                // Vérifiez si le champ ID existe dans l'objet JSON
                String offreIdStr = null;
                if (jsonObject.has("_id")) {
                    offreIdStr = jsonObject.getString("_id");
                } else {
                    // Loguez un avertissement si le champ ID est manquant
                    Log.w("TAG", "Champ ID manquant dans l'objet JSON");
                }



                // Convertir la chaîne de caractères en objet Date
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());
                Date datePublication = sdf.parse(datePublicationStr);



                // Si l'ID est null, vous pouvez choisir de ne pas ajouter cette offre à la liste
                if (offreIdStr != null) {
                    ObjectId offreId = new ObjectId(offreIdStr);
                    long timestamp = datePublication.getTime();
                    Offre_emploi offreEmploi = new Offre_emploi(nom, metier, description, periode, remuneration, new Date(timestamp));
                    offreEmplois.add(offreEmploi);
                }


                // Récupérer le timestamp de la date en millisecondes
                long timestamp = datePublication.getTime();

                // Créer un nouvel objet Annonce et l'ajouter à la liste
                Offre_emploi offreEmploi = new Offre_emploi(nom, metier, description, periode, remuneration, new Date(timestamp));
                offreEmplois.add(offreEmploi);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

        return offreEmplois;
    }



    private void ouvrirFormulaireCandidature(Offre_emploi offreEmploi, String nomUtilisateur, String prenomUtilisateur, String dateNaissanceUtilisateur, String offreId) {
        // Créer une intention pour ouvrir le formulaire de candidature
        Intent intent = new Intent(this, FormulaireCandidatureActivity.class);

        // Ajoutez les informations de l'utilisateur à l'intent
        intent.putExtra("nomUtilisateur", nomUtilisateur);
        intent.putExtra("prenomUtilisateur", prenomUtilisateur);
        intent.putExtra("dateNaissanceUtilisateur", dateNaissanceUtilisateur);
        // Passer les informations sur l'offre d'emploi à l'activité du formulaire de candidature
        intent.putExtra("offreEmploi", offreEmploi);
        intent.putExtra("offreId", offreId);

        // Démarrer l'activité du formulaire de candidature
        startActivity(intent);
    }



}
