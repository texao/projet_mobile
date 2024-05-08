package com.example.projet_mobile.employe;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.projet_mobile.Page_Accueil;
import com.example.projet_mobile.R;
import com.example.projet_mobile.candidat.connexion;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

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

public class inscription_employe extends AppCompatActivity {


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.inscription_employe);


        // Récupérer les références des champs de saisie
        EditText editTextEntreprise = findViewById(R.id.nomEntreprise);
        EditText editTextMail = findViewById(R.id.adresseMail);
        EditText editnumTelephone = findViewById(R.id.numTelephone);
        EditText editTextAdresse = findViewById(R.id.adresse);
        EditText editTexLien = findViewById(R.id.lienPublic);


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


        Button bouton_creer_compte = findViewById(R.id.button_employe);
        bouton_creer_compte.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Récupérer les valeurs des champs de saisie
                String NomEntreprise = editTextEntreprise.getText().toString();
                String Mail = editTextMail.getText().toString();
                String NumTelephone = editnumTelephone.getText().toString();
                String Adresse = editTextAdresse.getText().toString();
                String LienPublic = editTexLien.getText().toString();

                // Enregistrer l'utilisateur dans la base de données (à implémenter)
                enregistrerUtilisateur(NomEntreprise, Mail, NumTelephone, Adresse, LienPublic);

                // Rediriger vers l'activité d'accueil avec les offres d'emploi
                Intent intent = new Intent(inscription_employe.this, connexion.class);
                startActivity(intent);
            }
        });

    }



    private void enregistrerUtilisateur(String NomEntreprise, String email, String NumTelephone, String Adresse, String lienPublic ) {


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

        // Initialiser le contexte SSL avec le gestionnaire de confiance
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
                jsonObject.put("nomEntreprise", NomEntreprise);
                jsonObject.put("email", email);
                jsonObject.put("type", "Employe");
                jsonObject.put("numtelephone", NumTelephone);
                jsonObject.put("Adresse", Adresse);
                jsonObject.put("lienPublic", lienPublic);

            } catch (JSONException e) {
                e.printStackTrace();
            }


            // Créer une requête HTTP POST vers le serveur
            String url = "https://192.168.1.27:8888/inscription/employe";
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
                    Log.e("Inscription", "Erreur lors de l'enregistrement de l'employe : " + e.getMessage());
                    afficherMessage("Erreur lors de l'enregistrement. Veuillez réessayer.");

                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {

                    if (response.isSuccessful()) {
                        afficherMessage("Employe enregistré avec succès !");
                        // L'utilisateur a été enregistré avec succès
                        Log.d("Inscription", "Employe enregistré avec succès !");
                        // Rediriger vers l'activité d'accueil avec les offres d'emploi
                        Intent intent = new Intent(inscription_employe.this, Page_Accueil.class);
                        startActivity(intent);
                    } else {
                        // Gérer les réponses non réussies
                        Log.e("Inscription", "Erreur lors de l'enregistrement de l'employe : " + response.code());
                        afficherMessage("Erreur lors de l'enregistrement. Veuillez réessayer.");
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
                Toast.makeText(inscription_employe.this, message, Toast.LENGTH_SHORT).show();
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


}



