    package com.example.projet_mobile.model;

    public class Annonce {


        private String titre;
        private String description;
        private String datePublication;
        private String lieu;
        private String utilisateurId;

        public Annonce(String titre, String description, String datePublication, String lieu, String utilisateurId) {
            this.titre = titre;
            this.description = description;
            this.datePublication = datePublication;
            this.lieu = lieu;
            this.utilisateurId = utilisateurId;
        }

        public String getTitre() {
            return titre;
        }

        public void setTitre(String titre) {
            this.titre = titre;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getDatePublication() {
            return datePublication;
        }

        public void setDatePublication(String datePublication) {
            this.datePublication = datePublication;
        }

        public String getLieu() {
            return lieu;
        }

        public void setLieu(String lieu) {
            this.lieu = lieu;
        }

        public String getUtilisateurId() {
            return utilisateurId;
        }

        public void setUtilisateurId(String utilisateurId) {
            this.utilisateurId = utilisateurId;
        }

        @Override
        public String toString() {
            return "Annonce{" +
                    "titre='" + titre + '\'' +
                    ", description='" + description + '\'' +
                    ", datePublication='" + datePublication + '\'' +
                    ", lieu='" + lieu + '\'' +
                    ", utilisateurId='" + utilisateurId + '\'' +
                    '}';
        }


    }



