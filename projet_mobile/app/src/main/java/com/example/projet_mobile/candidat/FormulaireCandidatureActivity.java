package com.example.projet_mobile.candidat;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.projet_mobile.R;
import com.example.projet_mobile.model.Offre_emploi;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.Response;

public class FormulaireCandidatureActivity extends AppCompatActivity {

    private Offre_emploi offreEmploi;
    private EditText editTextNom, editTextPrenom, editTextDateNaissance, editTextNationalite, editTextCV, editTextLettreMotivation;

    String nomUtilisateur;
    String prenomUtilisateur;
    String dateNaissanceUtilisateur;
    private String offreId;

    private String metier;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_formulaire_candidature);


        // Récupérer les données de l'offre d'emploi de l'intent
        Intent intent = getIntent();

        if (intent != null) {
            nomUtilisateur = intent.getStringExtra("nomUtilisateur");
            prenomUtilisateur = intent.getStringExtra("prenomUtilisateur");
            dateNaissanceUtilisateur = intent.getStringExtra("dateNaissanceUtilisateur");
            offreEmploi = (Offre_emploi) intent.getSerializableExtra("offreEmploi");
        }

        if (intent != null && intent.hasExtra("offreEmploi")) {
            offreEmploi = (Offre_emploi) intent.getSerializableExtra("offreEmploi");
            offreId = offreEmploi.getId();
            metier = offreEmploi.getMetier();// Récupérer l'ID de l'offre
            Log.d("FormulaireCandidature", "FormulaireCandidature ID de l'offre extrait avec succès: " + offreId);
        } else {
            Log.e("FormulaireCandidature", "FormulaireCandidature Aucune offre d'emploi fournie dans l'intent");
            // Gérer l'absence de données d'offre d'emploi
            finish(); // Fermer l'activité si aucune offre d'emploi n'est fournie
        }



        // Initialiser les variables de classe
        editTextNom = findViewById(R.id.editTextNom);
        editTextPrenom = findViewById(R.id.editTextPrenom);
        editTextDateNaissance = findViewById(R.id.editTextDateNaissance);
        editTextNationalite = findViewById(R.id.editTextNationalite);
        editTextCV = findViewById(R.id.editTextCV);




        EditText editTextNom = findViewById(R.id.editTextNom);
        if (nomUtilisateur != null) {
            editTextNom.setText(nomUtilisateur);
        }
        Log.d("Tag", "page candidature , nom: " + nomUtilisateur);
        EditText editTextPrenom = findViewById(R.id.editTextPrenom);
        if (prenomUtilisateur != null) {
            editTextPrenom.setText(prenomUtilisateur);
        }

        EditText editTextDateNaissance = findViewById(R.id.editTextDateNaissance);
        if (dateNaissanceUtilisateur != null) {
            editTextDateNaissance.setText(dateNaissanceUtilisateur);
        }



        Button bouton_creer_candidature = findViewById(R.id.buttonEnvoyerCandidature);
        bouton_creer_candidature.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Récupérer les valeurs des champs de saisie
                String nom = editTextNom.getText().toString();
                String prenom = editTextPrenom.getText().toString();
                String dateNaissance = editTextDateNaissance.getText().toString();
                String nationalite = editTextNationalite.getText().toString();
                String  cv = editTextCV.getText().toString();

                // Enregistrer l'utilisateur dans la base de données (à implémenter)
                enregistrerCandidature(nom, prenom, dateNaissance, nationalite, cv);

                // Rediriger vers l'activité d'accueil avec les offres d'emploi
                Intent intent = new Intent(FormulaireCandidatureActivity.this, EspaceConnecte.class);
                startActivity(intent);
            }
        });




    }




    private void enregistrerCandidature(String nom, String prenom, String dateNaissance, String nationalite, String cv) {

        // Créer un gestionnaire de confiance qui ne valide pas les certificats
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


            // Créer un objet JSON contenant les données de l'utilisateur
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("nom", nom);
                jsonObject.put("prenom", prenom);
                jsonObject.put("dateNaissance", dateNaissance);
                jsonObject.put("nationalite", nationalite);
                jsonObject.put("cv", cv);
                jsonObject.put("Statut", "En attente");
                jsonObject.put("dateCandidature", getCurrentDateTime());
                jsonObject.put("offreId", offreId);
                jsonObject.put("metier", metier);

            } catch (JSONException e) {
                e.printStackTrace();
            }


            // Créer une requête HTTP POST vers le serveur
            String url = "https://192.168.1.27:8888/candidature";
            RequestBody body = RequestBody.create(jsonObject.toString(), MediaType.get("application/json"));
            okhttp3.Request request = new okhttp3.Request.Builder()
                    .url(url)
                    .post(body)
                    .build();

            // Exécuter la requête de manière asynchrone

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    // Gérer les erreurs de requête
                    e.printStackTrace();
                    Log.e("Inscription", "Erreur enregistrement candidature : " + e.getMessage());
                    afficherMessage("Erreur enregistrement candidature. Veuillez réessayer.");

                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {

                    if (response.isSuccessful()) {
                        afficherMessage("candidature enregistré avec succès !");
                        // L'utilisateur a été enregistré avec succès
                        Log.d("Inscription", "Employe enregistré avec succès !");
                        // Rediriger vers l'activité d'accueil avec les offres d'emploi
                        Intent intent = new Intent(FormulaireCandidatureActivity.this, EspaceConnecte.class);
                        startActivity(intent);
                    } else {
                        // Gérer les réponses non réussies
                        Log.e("Inscription", "Erreur enregistrement candidature : " + response.code());
                        afficherMessage("Erreur enregistrement candidature. Veuillez réessayer.");
                    }
                }
            });
        } catch (NoSuchAlgorithmException | KeyManagementException e) {
            e.printStackTrace();
        }
    }


    private void afficherMessage(String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(FormulaireCandidatureActivity.this, message, Toast.LENGTH_SHORT).show();
            }
        });
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




    // Méthode pour obtenir la date et l'heure actuelles dans un format adapté
    private String getCurrentDateTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
        Date currentDate = new Date();
        return dateFormat.format(currentDate);
    }



}



