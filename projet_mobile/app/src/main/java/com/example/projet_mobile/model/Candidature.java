package com.example.projet_mobile.model;


import java.io.Serializable;
import java.util.Date;

public class Candidature implements Serializable {


    private String nom;
    private String prenom;
    private String dateNaissance;
    private String nationalite;
    private String statut;
    private Date dateCandidature;

    private String metier;



    public Candidature(String nom, String prenom, String dateNaissance, String nationalite, String statut, Date dateCandidature, String metier) {
        this.nom = nom;
        this.prenom = prenom;
        this.dateNaissance = dateNaissance;
        this.nationalite = nationalite;
        this.statut = statut;
        this.dateCandidature = dateCandidature;
        this.metier = metier;
    }


    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }


    public String getPrenom() {
        return prenom;
    }


    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }


    public String getDateNaissance() {
        return dateNaissance;
    }


    public void dateNaissance(String dateNaissance) {
        this.dateNaissance = dateNaissance;
    }


    public String getNationalite() {
        return nationalite;
    }

    public void setNationalite(String nationalite) {
        this.nationalite = nationalite;
    }


    public String getStatut() {
        return statut;
    }


    public void setStatut(String statut) {
        this.statut = statut;
    }


    public Date getDateCandidature() {
        return dateCandidature;
    }


    public void setDateCandidature(Date dateCandidature) {
        this.dateCandidature = dateCandidature;
    }

    public String getMetier() {
        return metier;
    }

    public void setMetier(String nom) {
        this.metier = metier;
    }


    @Override
    public String toString() {
        return "Candidature{" +
                "nom='" + nom + '\'' +
                ", prenom='" + prenom + '\'' +
                ", metier='" + metier + '\'' +
                ", dateNaissance='" + dateNaissance + '\'' +
                ", nationalite='" + nationalite + '\'' +
                ", statut='" + statut + '\'' +
                ", dateCandidature=" + dateCandidature +
                '}';
    }




}