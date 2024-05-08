package com.example.projet_mobile.candidat;

import androidx.room.Dao;
import androidx.room.Insert;

import com.example.projet_mobile.candidat.Utilisateur;

@Dao
public interface UtilisateurDao {
    @Insert
    void inserer(Utilisateur utilisateur);
}