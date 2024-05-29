package com.example.projet_mobile.adaptateur;
import android.content.Context;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.projet_mobile.R;
import com.example.projet_mobile.model.Candidature;
import com.example.projet_mobile.model.Offre_emploi;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.List;
import java.text.SimpleDateFormat;

import javax.net.ssl.HttpsURLConnection;
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

public class OffreEmploiAdaptateurEmploye extends ArrayAdapter<Offre_emploi> {

    private LayoutInflater inflater;




    public OffreEmploiAdaptateurEmploye(Context context, List<Offre_emploi> offreEmplois) {
        super(context, 0, offreEmplois);
        inflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            view = inflater.inflate(R.layout.item_offre_emploi_employe, parent, false);
        }

        Offre_emploi offreEmploi = getItem(position);

        if (offreEmploi != null) {
            TextView textViewNom = view.findViewById(R.id.nomEntreprise);
            TextView textViewMetier = view.findViewById(R.id.metier);
            TextView textViewDescription= view.findViewById(R.id.desciption);
            TextView textViewPeriode = view.findViewById(R.id.periode);
            TextView textViewRemuneration = view.findViewById(R.id.remuneration);
            TextView textViewDatePublication = view.findViewById(R.id.datePublication);
            Button btnSupprimer = view.findViewById(R.id.btnSupprimer);


            textViewNom.setText(offreEmploi.getNom());
            textViewMetier.setText(offreEmploi.getMetier());
            textViewDescription.setText(offreEmploi.getDescription());
            textViewPeriode.setText("Periode: " + offreEmploi.getPeriode());
            Log.d("OffreEmploiAdapter", "Setting textViewRemuneration: " + String.valueOf(offreEmploi.getRemuneration()));
            textViewRemuneration.setText("Remuneration: " + String.valueOf(offreEmploi.getRemuneration()));
            textViewDatePublication.setText(new SimpleDateFormat("dd/MM/yyyy").format(offreEmploi.getDatePublication()));



           btnSupprimer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SupprimerOffre(offreEmploi);
                }
            });
        }

        return view;
    }






    private void SupprimerOffre(Offre_emploi offreEmploi) {

        // Récupérer l'ID de l'offre à supprimer
        String offreNom = offreEmploi.getNom();
        Log.d("Tag", "offre nom: " + offreNom);

        String offreMetier = offreEmploi.getMetier();
        Log.d("Tag", "offre metier: " + offreMetier);


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


            // Créer un objet JSON contenant les données de la demande
            JSONObject jsonBody = new JSONObject();
            jsonBody.put("nom", offreNom); // Ajoutez le nom de l'entreprise
            jsonBody.put("metier", offreMetier);


            // Créer une demande HTTP POST avec le corps JSON
            RequestBody body = RequestBody.create(jsonBody.toString(), MediaType.parse("application/json; charset=utf-8"));
            Request request = new Request.Builder()
                    .url("https://192.168.1.27:8888/supprimerOffre")
                    .post(body)
                    .build();


            // Exécution de la requête
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    // Gérer les échecs de requête
                }

                @Override
                public void onResponse(@NonNull okhttp3.Call call, @NonNull okhttp3.Response response) throws IOException {
                    // Vérification de la réponse
                    if (response.isSuccessful()) {
                        // Mettre à jour l'interface utilisateur sur le thread UI principal
                        new android.os.Handler(Looper.getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getContext(), "Offre supprimée avec succès", Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else {
                        // Gérer les réponses non réussies
                        Log.e("SupprimerOffre", "Échec de la suppression de l'offre: " + response.code());
                    }
                }
            });
        } catch (NoSuchAlgorithmException | KeyManagementException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }


}

