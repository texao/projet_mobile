    package com.example.projet_mobile;

    import android.content.Intent;
    import android.os.Bundle;
    import android.util.Log;
    import android.view.View;
    import android.widget.Button;
    import android.widget.ListView;
    import android.widget.SearchView;

    import androidx.appcompat.app.AppCompatActivity;

    import com.example.projet_mobile.adaptateur.AnnoncesAdapter;
    import com.example.projet_mobile.candidat.connexion;
    import com.example.projet_mobile.model.Annonce;

    import org.json.JSONArray;
    import org.json.JSONException;
    import org.json.JSONObject;

    import java.io.IOException;
    import java.security.KeyManagementException;
    import java.security.NoSuchAlgorithmException;
    import java.security.SecureRandom;
    import java.security.cert.CertificateException;
    import java.security.cert.X509Certificate;
    import java.util.ArrayList;
    import java.util.List;

    import javax.net.ssl.SSLContext;
    import javax.net.ssl.TrustManager;
    import javax.net.ssl.X509TrustManager;

    import okhttp3.Call;
    import okhttp3.Callback;
    import okhttp3.OkHttpClient;
    import okhttp3.Request;
    import okhttp3.Response;

    public class Page_Accueil  extends AppCompatActivity {
        private ListView listViewAnnonces;
        private AnnoncesAdapter annoncesAdapter;
        private SearchView searchView;
        private List<Annonce> listeannonces = new ArrayList<>();


        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.accueil);

            listViewAnnonces = findViewById(R.id.listViewAnnonces); // Initialisation de listViewAnnonces
            Log.d("Tag", "page acueil");
            searchView = findViewById(R.id.searchView);

            if (searchView == null) {
                Log.e("Page_Accueil", "SearchView not found in layout!");
            } else {
                Log.d("Page_Accueil", "SearchView initialized successfully.");
            }




            // Récupérer les annonces à partir du serveur
            getAnnoncesFromServer();

            Button bouton_connexion = findViewById(R.id.buttonConnexion);
            bouton_connexion.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Lorsque le bouton de connexion est cliqué, démarrer l'activité de connexion
                    Intent intent = new Intent(Page_Accueil.this, connexion.class);
                    startActivity(intent);
                }
            });


        }



        public void getAnnoncesFromServer() {


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
                        .url("https://192.168.1.27:8888/annonces")
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
                        // s'assurer que la réponse est réussie
                        if (response.isSuccessful()) {
                            String responseData = response.body().string();
                            Log.d("TAG", "Réponse du serveur: " + responseData);

                            List<Annonce> annonces = parseAnnoncesFromJSON(responseData);

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    annoncesAdapter = new AnnoncesAdapter(Page_Accueil.this, annonces);
                                    listViewAnnonces.setAdapter(annoncesAdapter);

                                    if (searchView == null) {
                                        Log.e("Page_Accueil", "SearchView is still null in runOnUiThread!");
                                    } else {
                                        Log.d("Page_Accueil", "SearchView is ready to be used.");

                                    }

                                    // Mettre en place le filtre de recherche maintenant que l'adaptateur est initialisé
                                    searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                                        @Override
                                        public boolean onQueryTextSubmit(String query) {
                                            return false;
                                        }

                                        @Override
                                        public boolean onQueryTextChange(String newText) {
                                            if (annoncesAdapter != null) {
                                                filterAnnonces(newText);

                                            }
                                            return false;
                                        }
                                    });


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
        private List<Annonce> parseAnnoncesFromJSON(String jsonData) {
            List<Annonce> annonces = new ArrayList<>();

            try {
                JSONArray jsonArray = new JSONArray(jsonData);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    // Extraire les données de l'objet JSON pour créer un objet Annonce
                    String titre = jsonObject.getString("Titre");
                    String description = jsonObject.getString("Description");
                    String datePublication = jsonObject.getString("Date de publication");
                    String lieu = jsonObject.getString("Lieu");
                    String utilisateurId = jsonObject.getString("Utilisateur ID");

                    // Créer un nouvel objet Annonce et l'ajouter à la liste
                    Annonce annonce = new Annonce(titre, description, datePublication, lieu, utilisateurId);
                    annonces.add(annonce);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            // Mettre à jour la liste listeCandidatures avec les nouvelles candidatures
            listeannonces.clear(); // Vider la liste actuelle
            listeannonces.addAll(annonces); // Ajouter toutes les nouvelles candidatures



            return annonces;
        }





        private void filterAnnonces(String titre) {
            // Créer une liste pour stocker les annonces filtrées
            List<Annonce> filteredList = new ArrayList<>();

            if (titre.isEmpty()) {
                // Si le champ de recherche est vide, afficher toutes les annonces
                filteredList.addAll(listeannonces);
            } else {
                // Parcourir toutes les annonces
                for (Annonce annonce : listeannonces) {
                    if (annonce.getTitre().toLowerCase().contains(titre.toLowerCase())) {
                        // Vérifier si le métier de l'annonce contient le critère de recherche
                        filteredList.add(annonce);
                    }
                }
            }
        //   Créer un nouvel adaptateur avec les annonces filtrées
            AnnoncesAdapter filteredAdapter = new AnnoncesAdapter(Page_Accueil.this, filteredList);
            listViewAnnonces.setAdapter(filteredAdapter);
        }



    }
