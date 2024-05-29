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


public class Employeur extends AppCompatActivity {

    private EditText nom;
    private EditText mail;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.employeur);


        Intent intent = getIntent();
        // Extraire le nom et l'email de l'intent
        String nom = intent.getStringExtra("nom");
        String email = intent.getStringExtra("email");



        // Références des boutons
        Button buttonGererOffres = findViewById(R.id.buttonGererOffres);
        Button buttonCandidatureNonTraitees = findViewById(R.id.buttonCandidatureNonTraitees);
        Button buttonCandidatureAcceptees = findViewById(R.id.buttonCandidatureAcceptees);

        // Gérer le clic sur le bouton Gérer Offres
        buttonGererOffres.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Employeur.this, GererOffresActivity.class);
                intent.putExtra("nomEntreprise", nom);
                intent.putExtra("emailEntreprise", email);
                startActivity(intent);
            }
        });

        // Gérer le clic sur le bouton Candidature non traitées
        buttonCandidatureNonTraitees.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Employeur.this, CandidatureNonTraiteesActivity.class);
                intent.putExtra("nomEntreprise", nom);
                intent.putExtra("emailEntreprise", email);
                startActivity(intent);
            }
        });

        // Gérer le clic sur le bouton Candidature acceptées
        buttonCandidatureAcceptees.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Employeur.this, CandidatureAccepteesActivity.class);
                intent.putExtra("nomEntreprise", nom);
                startActivity(intent);
            }
        });
    }



}



