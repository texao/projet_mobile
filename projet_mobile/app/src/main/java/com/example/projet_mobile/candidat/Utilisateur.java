package com.example.projet_mobile.candidat;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Utilisateur {
    @PrimaryKey(autoGenerate = true)
    public int id;

    public String nom;
    public String prenom;
    public String email;
    public String type;
    public String dateNaissance;
}