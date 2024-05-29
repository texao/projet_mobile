package com.example.projet_mobile.employe;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.projet_mobile.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class connexion_employe extends AppCompatActivity {

    private EditText nom;
    private EditText mail;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.connexion_employe);

        Log.d("Tag", "page connexion ");


        // Récupérer les références des champs de saisie
        nom = findViewById(R.id.editTextNom);
        mail = findViewById(R.id.editTextEmail);

        Log.d("Tag", "page connexion");


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



        Button bouton_connexion = findViewById(R.id.buttonConnexion);
        bouton_connexion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(connexion_employe.this, Employeur.class);
                startActivity(intent);


            }
        });


        Button boutonconnexion = findViewById(R.id.buttonConnexion);
        boutonconnexion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Récupérer les valeurs des champs de saisie
                String name = nom.getText().toString();
                String email = mail.getText().toString();

                // Envoyer les informations de connexion au serveur pour vérification
                authentification_utilisateur(name, email);
            }
        });
    }



    public void authentification_utilisateur(String nom, String email) {


        try {
            // Configuration SSL
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, trustAllCerts, new SecureRandom());


            OkHttpClient client = new OkHttpClient.Builder()
                    .sslSocketFactory(sslContext.getSocketFactory(), (X509TrustManager) trustAllCerts[0])
                    .hostnameVerifier((hostname, session) -> true)
                    .build();



            // Construire le corps de la requête avec les données d'authentification
            RequestBody requestBody = new FormBody.Builder()
                    .add("nom", nom)
                    .add("email", email)
                    .build();

            String url = "https://192.168.1.27:8888/connexion/employe";

            // Créer la requête HTTP POST
            Request request = new Request.Builder()
                    .url(url)
                    .post(requestBody)
                    .build();


            // Exécuter la requête de manière asynchrone
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    // Gérer les erreurs de requête
                    e.printStackTrace();
                    Log.e("connexion", "Erreur lors de l'authentification onFailure : " + e.getMessage());
                    afficherMessage("Erreur lors de l'authentification. Veuillez réessayer.");
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    // Vérifier si la réponse est réussie
                    if (response.isSuccessful()) {
                        // Récupérer la réponse du serveur
                        String responseData = response.body().string();
                        try {
                            // Convertir la réponse en JSON
                            JSONObject json = new JSONObject(responseData);

                            // Vérifier si l'authentification a réussi
                            boolean authentificationReussie = json.getBoolean("success");
                            if (authentificationReussie) {
                                // Authentification réussie, rediriger vers la page d'accueil
                                Intent intent = new Intent(connexion_employe.this, Employeur.class);
                                intent.putExtra("nom", nom);
                                intent.putExtra("email", email);
                                startActivity(intent);
                            } else {
                                // Authentification échouée, afficher un message d'erreur
                                afficherMessage("Nom ou email incorrect.");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else {
                        Log.e("connexion", "Erreur lors de l'authentification onResponses : " + response.code());
                        afficherMessage("Erreur lors de l'authentification. Veuillez réessayer.");
                    }
                }
            });
        } catch (NoSuchAlgorithmException | KeyManagementException e) {
            e.printStackTrace();
        }
    }

    // Afficher un message à l'utilisateur à l'aide de Toast
    private void afficherMessage (String message){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(connexion_employe.this, message, Toast.LENGTH_SHORT).show();
            }
        });
    }


    // Array pour la confiance de tous les certificats
    private final TrustManager[] trustAllCerts = new TrustManager[]{
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



}

