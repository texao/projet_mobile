package com.example.projet_mobile.candidat;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.projet_mobile.R;
import com.example.projet_mobile.employe.inscription_employe;

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



public class inscription extends AppCompatActivity {


    private RelativeLayout layoutInterim;
    private RelativeLayout layoutEmploye;
    private CheckBox checkBoxRechercheInterim;
    private CheckBox checkBoxEmploye;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.inscription_interim);

        Log.d("Tag", "page connexion ");


        // Récupérer les références des champs de saisie
        EditText editTextNom = findViewById(R.id.editTextNom);
        EditText editTextPrenom = findViewById(R.id.editTextPrenom);
        EditText editTextEmail = findViewById(R.id.editTextEmail);
        EditText editTextDateNaissance = findViewById(R.id.editTextDateNaissance);



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

        Button bouton_creer_compte = findViewById(R.id.button_creer_compte);
        bouton_creer_compte.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Récupérer les valeurs des champs de saisie
                String nom = editTextNom.getText().toString();
                String prenom = editTextPrenom.getText().toString();
                String email = editTextEmail.getText().toString();
                String dateNaissance = editTextDateNaissance.getText().toString();

                // Enregistrer l'utilisateur dans la base de données
                enregistrerUtilisateur(nom, prenom, email, dateNaissance);

                // Rediriger vers l'activité d'accueil avec la page de connexion
                Intent intent = new Intent(inscription.this, connexion.class);
                intent.putExtra("nomUtilisateur", nom);
                intent.putExtra("prenomUtilisateur", prenom);
                intent.putExtra("dateNaissanceUtilisateur", dateNaissance);


                Log.d("TAG", "page inscription, nom: " + nom);
                startActivity(intent);
            }
        });



        Button bouton_employe = findViewById(R.id.buttonEmploye);
        bouton_employe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Rediriger vers l'activité d'accueil avec les offres d'emploi
                Intent intent = new Intent(inscription.this, inscription_employe.class);
                startActivity(intent);
            }
        });


    }




    private void enregistrerUtilisateur(String nom, String prenom, String email, String dateNaissance) {


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
                jsonObject.put("nom", nom);
                jsonObject.put("prenom", prenom);
                jsonObject.put("email", email);
                jsonObject.put("type", "Candidat");
                jsonObject.put("dateNaissance", dateNaissance);
            } catch (JSONException e) {
                e.printStackTrace();
            }


            // Créer une requête HTTP POST vers le serveur
            String url = "https://192.168.1.27:8888/inscription";
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
                    Log.e("Inscription", "Erreur lors de l'enregistrement de l'utilisateur : " + e.getMessage());
                    afficherMessage("Erreur lors de l'enregistrement. Veuillez réessayer.");

                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (response.isSuccessful()) {
                        afficherMessage("Utilisateur enregistré avec succès !");
                        // L'utilisateur a été enregistré avec succès
                        Log.d("Inscription", "Utilisateur enregistré avec succès !");
                        // Rediriger vers l'activité d'accueil avec les offres d'emploi
                        Intent intent = new Intent(inscription.this, connexion.class);
                        startActivity(intent);
                    } else {
                        // Gérer les réponses non réussies
                        Log.e("Inscription", "Erreur lors de l'enregistrement de l'utilisateur : " + response.code());
                        afficherMessage("Erreur lors de l'enregistrement. Veuillez réessayer.");
                    }
                }
            });
        } catch (NoSuchAlgorithmException | KeyManagementException e) {
            e.printStackTrace();
        }
    }


    // Afficher un message à l'utilisateur à l'aide de Toast
    private void afficherMessage(String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(inscription.this, message, Toast.LENGTH_SHORT).show();
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



