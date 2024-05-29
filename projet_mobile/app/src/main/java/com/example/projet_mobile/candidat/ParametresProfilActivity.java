package com.example.projet_mobile.candidat;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.projet_mobile.R;

public class ParametresProfilActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parametres_profil);

        // Récupérer les informations de l'utilisateur depuis l'intent
        Intent intent = getIntent();
        String nomUtilisateur = intent.getStringExtra("nomUtilisateur");
        String prenomUtilisateur = intent.getStringExtra("prenomUtilisateur");
        String datenaissance = intent.getStringExtra("emailUtilisateur");

        // Afficher les informations de l'utilisateur
        TextView textViewNom = findViewById(R.id.textViewNom);
        TextView textViewPrenom = findViewById(R.id.textViewPrenom);
        TextView textViewdatenaissance = findViewById(R.id.datenaissance);

        textViewNom.setText("Nom : " + nomUtilisateur);
        textViewPrenom.setText("Prénom : " + prenomUtilisateur);
        textViewdatenaissance.setText("date naissance : " + datenaissance);


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
    }
}
