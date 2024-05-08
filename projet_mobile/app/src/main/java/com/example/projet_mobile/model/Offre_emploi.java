package com.example.projet_mobile.model;

import java.io.Serializable;
import java.util.Date;

public class Offre_emploi implements Serializable {

    private String id;
    private String nom;
    private String metier;
    private String description;
    private String periode;
    private int remuneration;
    private Date datePublication;



public Offre_emploi(String nom, String metier, String description, String periode, int remuneration, Date datePublication) {
        this.nom = nom;
        this.metier = metier;
        this.description = description;
        this.periode = periode;
        this.remuneration = remuneration;
        this.datePublication = datePublication;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }




    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }


    public String getMetier() {
        return metier;
    }


    public void setMetier(String metier) {
        this.metier = metier;
    }


    public String getDescription() {
        return description;
    }


    public void setDescription(String description) {
        this.description = description;
    }


    public String getPeriode() {
        return periode;
    }

    public void setPeriode(String periode) {
        this.periode = periode;
    }


    public int getRemuneration() {
        return remuneration;
    }


    public void setRemuneration(int remuneration) {
        this.remuneration = remuneration;
    }


    public Date getDatePublication() {
        return datePublication;
    }


    public void setDatePublication(Date datePublication) {
        this.datePublication = datePublication;
    }



    @Override
    public String toString() {
        return "Offre_emploi{" +
                "id='" + id + '\'' +
                ", nom='" + nom + '\'' +
                ", metier='" + metier + '\'' +
                ", description='" + description + '\'' +
                ", periode='" + periode + '\'' +
                ", remuneration=" + remuneration +
                ", datePublication=" + datePublication +
                '}';
    }

}

