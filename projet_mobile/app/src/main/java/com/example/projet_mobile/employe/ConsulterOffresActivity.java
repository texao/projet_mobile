package com.example.projet_mobile.employe;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projet_mobile.R;
import com.example.projet_mobile.adaptateur.OffreEmploiAdaptateurEmploye;
import com.example.projet_mobile.adaptateur.OffreEmploiAdapter;
import com.example.projet_mobile.model.Offre_emploi;

import org.bson.types.ObjectId;
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
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ConsulterOffresActivity extends AppCompatActivity {
    private ListView listViewOffre;
    private String nomEntreprise;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.consulte_offre);

        // Récupérer le nom de l'entreprise depuis l'Intent
        nomEntreprise = getIntent().getStringExtra("nomEntreprise");

        Log.d("ConsulterOffresActivity", "nom entreprise " + nomEntreprise);

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



        listViewOffre = findViewById(R.id.listViewOffre);
        Log.d("Tag", "page offre");

        // Ajouter un écouteur d'événements à la ListView
        listViewOffre.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Récupérer l'offre sélectionnée
                Offre_emploi offreEmploi = (Offre_emploi) parent.getItemAtPosition(position);


                String offreId = offreEmploi.getId();
                Log.d("ConsulterOffresActivity", "ConsulterOffresActivity ID de l'offre extrait avec succès: " + offreId);


            }
        });

        getOffreFromServer(nomEntreprise);


    }





    public void getOffreFromServer(String nomEntreprise) {




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



            // Créer un objet JSON contenant les données de la demande
            JSONObject jsonBody = new JSONObject();
            jsonBody.put("nomEntreprise", nomEntreprise); // Ajoutez le nom de l'entreprise


            // Créer une demande HTTP POST avec le corps JSON
            RequestBody body = RequestBody.create(jsonBody.toString(), MediaType.parse("application/json; charset=utf-8"));
            Request request = new Request.Builder()
                    .url("https://192.168.1.27:8888/offre_employe")
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
                                OffreEmploiAdaptateurEmploye offreEmploiAdaptateurEmploye = new OffreEmploiAdaptateurEmploye(ConsulterOffresActivity.this, offreEmplois);
                                listViewOffre.setAdapter(offreEmploiAdaptateurEmploye);
                            }
                        });
                    } else {
                        Log.d("TAG", "Réponse non réussie: " + response.code());
                    }
                }
            });
        } catch (NoSuchAlgorithmException | KeyManagementException e) {
            e.printStackTrace();

        } catch (JSONException e) {
            throw new RuntimeException(e);
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

}

