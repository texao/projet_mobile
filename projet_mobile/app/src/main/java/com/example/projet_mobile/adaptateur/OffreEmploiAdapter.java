package com.example.projet_mobile.adaptateur;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.projet_mobile.R;
import com.example.projet_mobile.model.Offre_emploi;

import java.util.List;
import java.text.SimpleDateFormat;

public class OffreEmploiAdapter extends ArrayAdapter<Offre_emploi> {

    private LayoutInflater inflater;

    public OffreEmploiAdapter(Context context, List<Offre_emploi> offreEmplois) {
        super(context, 0, offreEmplois);
        inflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            view = inflater.inflate(R.layout.item_offre_emploi, parent, false);
        }

        Offre_emploi offreEmploi = getItem(position);

        if (offreEmploi != null) {
            TextView textViewNom = view.findViewById(R.id.nomEntreprise);
            TextView textViewMetier = view.findViewById(R.id.metier);
            TextView textViewDescription= view.findViewById(R.id.desciption);
            TextView textViewPeriode = view.findViewById(R.id.periode);
            TextView textViewRemuneration = view.findViewById(R.id.remuneration);
            TextView textViewDatePublication = view.findViewById(R.id.datePublication);



            textViewNom.setText(offreEmploi.getNom());
            textViewMetier.setText(offreEmploi.getMetier());
            textViewDescription.setText(offreEmploi.getDescription());
            textViewPeriode.setText("Periode: " + offreEmploi.getPeriode());
            Log.d("OffreEmploiAdapter", "Setting textViewRemuneration: " + String.valueOf(offreEmploi.getRemuneration()));
            textViewRemuneration.setText("Remuneration: " + String.valueOf(offreEmploi.getRemuneration()));
            textViewDatePublication.setText(new SimpleDateFormat("dd/MM/yyyy").format(offreEmploi.getDatePublication()));

        }

        return view;
    }
}

