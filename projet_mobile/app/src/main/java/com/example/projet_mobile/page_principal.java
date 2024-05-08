package com.example.projet_mobile;

import android.content.Intent;
import android.nfc.Tag;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;

public class page_principal extends AppCompatActivity {

    private static final String TAG = "PagePrincipal";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.page_principal);

        Log.d(TAG, "Message de débogage dans page principal");


        // Récupérer une référence du bouton "Passer"
        Button passerButton = findViewById(R.id.buttonPasser);

        // Définir un écouteur d'événements pour le bouton "Passer"
        passerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Lorsque le bouton est cliqué, démarrer une nouvelle activité (page d'accueil)
                Intent intent = new Intent(page_principal.this, Page_Accueil.class);
                startActivity(intent);
            }
        });
    }

    }

