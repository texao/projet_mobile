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

import com.example.projet_mobile.R;

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

public class GererOffresActivity extends AppCompatActivity {

    private EditText nom;
    private EditText metier;
    private EditText description;
    private EditText periode;

    private EditText remuneration;
    private String nomEntreprise;

    private String emailEntreprise;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gerer_offres);



        Intent intent = getIntent();
        // Extraire le nom et l'email de l'intent
         nomEntreprise = intent.getStringExtra("nomEntreprise");
         emailEntreprise = intent.getStringExtra("emailEntreprise");



        // Récupérer les références des champs de saisie
        nom = findViewById(R.id.editTextNom);
        metier = findViewById(R.id.editTextMetier);
        description = findViewById(R.id.editTextDescription);
        periode = findViewById(R.id.editTextPeriode);
        remuneration = findViewById(R.id.editTextRemuneration);




        ImageView imageViewBack = findViewById(R.id.imageViewBack);

        // Ajouter un écouteur de clic à l'ImageView
        imageViewBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Gérer le clic pour retourner à l'activité précédente (connexion)
                onBackPressed();
            }
        });



        Button boutonconnexion = findViewById(R.id.buttonEnvoyerOffre);
        boutonconnexion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Récupérer les valeurs des champs de saisie
                String nomCompany = nom.getText().toString();
                String metierCompany = metier.getText().toString();
                String descriptionCompany = description.getText().toString();
                String periodeCompany = periode.getText().toString();
                int remunerationCompany = Integer.parseInt(remuneration.getText().toString());


                DepotOffre(nomCompany, metierCompany, descriptionCompany, periodeCompany, remunerationCompany);


            }
        });






        Button buttonConsulterOffres = findViewById(R.id.buttonConsulterOffres);
        buttonConsulterOffres.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Rediriger vers l'activité de consultation des offres
                Intent intent = new Intent(GererOffresActivity.this, ConsulterOffresActivity.class);
                intent.putExtra("nomEntreprise", nomEntreprise);
                startActivity(intent);
            }
        });


    }



    private void DepotOffre(String nom, String metier, String description, String periode, int remuneration) {


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
                jsonObject.put("metier", metier);
                jsonObject.put("description", description);
                jsonObject.put("periode", periode);
                jsonObject.put("remuneration", remuneration);
                jsonObject.put("nomEntreprise", nomEntreprise);
                jsonObject.put("emailEntreprise", emailEntreprise);

            } catch (JSONException e) {
                e.printStackTrace();
            }


            // Créer une requête HTTP POST vers le serveur
            String url = "https://192.168.1.27:8888/depotOffre";
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
                    Log.e("Inscription", "Erreur lors de l'enregistrement de l'offre : " + e.getMessage());
                    afficherMessage("Erreur lors de l'enregistrement. Veuillez réessayer.");

                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (response.isSuccessful()) {
                        afficherMessage("Utilisateur enregistré avec succès !");

                        afficherMessage("Offre d'emploi enregistrée avec succès !");
                        Log.d("DepotOffre", "Offre d'emploi enregistrée avec succès !");

                    } else {
                        // Gérer les réponses non réussies
                        Log.e("DepotOffre", "Erreur lors de l'enregistrement de l'offre : " + response.code());
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
                Toast.makeText(GererOffresActivity.this, message, Toast.LENGTH_SHORT).show();
            }
        });
    }





}




