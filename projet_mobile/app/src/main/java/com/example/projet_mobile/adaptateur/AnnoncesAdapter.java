package com.example.projet_mobile.adaptateur;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.example.projet_mobile.R;
import com.example.projet_mobile.model.Annonce;

import java.util.ArrayList;
import java.util.List;

public class AnnoncesAdapter extends ArrayAdapter<Annonce>  implements Filterable {

    private LayoutInflater inflater;
    private List<Annonce> annonces;
    private List<Annonce> annoncesFiltered;

    public AnnoncesAdapter(Context context, List<Annonce> annonces) {
        super(context, 0, annonces);
        inflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            view = inflater.inflate(R.layout.item_annonce, parent, false);
        }

        Annonce annonce = getItem(position);
        if (annonce != null) {
            TextView textViewTitre = view.findViewById(R.id.textViewTitre);
            TextView textViewDescription = view.findViewById(R.id.textViewDescription);
            TextView textViewDatePublication = view.findViewById(R.id.textViewDatePublication);
            TextView textViewLieu = view.findViewById(R.id.textViewLieu);

            textViewTitre.setText(annonce.getTitre());
            textViewDescription.setText(annonce.getDescription());
            textViewDatePublication.setText(annonce.getDatePublication());
            textViewLieu.setText(annonce.getLieu());
        }

        return view;
    }




}







