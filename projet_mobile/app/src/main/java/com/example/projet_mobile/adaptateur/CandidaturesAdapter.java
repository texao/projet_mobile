package com.example.projet_mobile.adaptateur;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.projet_mobile.R;
import com.example.projet_mobile.model.Candidature;

import java.util.List;
import java.text.SimpleDateFormat;

public class CandidaturesAdapter extends ArrayAdapter<Candidature> {

    private LayoutInflater inflater;

    public CandidaturesAdapter(Context context, List<Candidature> candidatures) {
        super(context, 0, candidatures);
        inflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            view = inflater.inflate(R.layout.item_candidature, parent, false);
        }

        Candidature candidature = getItem(position);

        if (candidature != null) {
            TextView textViewNom = view.findViewById(R.id.nom);
            TextView textViewPrenom = view.findViewById(R.id.prenom);
            TextView textViewmetier = view.findViewById(R.id.metier);
            TextView textViewmdatenaissance = view.findViewById(R.id.datenaissance);
            TextView textViewnationalite = view.findViewById(R.id.nationalite);
            TextView textViewstatus= view.findViewById(R.id.status);
            TextView textViewdatCandidature= view.findViewById(R.id.datCandidature);


            textViewNom.setText(candidature.getNom());
            textViewPrenom.setText(candidature.getPrenom());
            textViewmetier.setText(candidature.getMetier());
            textViewmdatenaissance.setText(candidature.getDateNaissance());
            textViewnationalite.setText(candidature.getNationalite());
            textViewstatus.setText((candidature.getStatut()));
            textViewdatCandidature.setText(new SimpleDateFormat("dd/MM/yyyy").format(candidature.getDateCandidature()));

        }

        return view;
    }
}



