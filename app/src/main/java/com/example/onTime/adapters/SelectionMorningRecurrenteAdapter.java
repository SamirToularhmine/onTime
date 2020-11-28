package com.example.onTime.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.onTime.R;
import com.example.onTime.fragments.EditMRFragment;
import com.example.onTime.modele.Tache;
import com.example.onTime.modele.Toolbox;

import java.util.List;

public class SelectionMorningRecurrenteAdapter extends ArrayAdapter<Tache> {

    private List<Tache> listTache;
    private EditMRFragment editMRFragment;

    public SelectionMorningRecurrenteAdapter(@NonNull Context context, List<Tache> listTache, EditMRFragment editMRFragment) {
        super(context, 0, listTache);
        this.listTache = listTache;
        this.editMRFragment = editMRFragment;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Tache tache = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.tache_reccurente_item, parent, false);
        }

        TextView titreTache = convertView.findViewById(R.id.titreTache);
        titreTache.setTag(position);

        if (tache != null) {
            String affichage = tache.getNom() + " | " + Toolbox.getMinutesFromSecondes(tache.getDuree()) + " m";
            titreTache.setText(affichage);
        }else{
            titreTache.setText(R.string.tache_pas_definie);
        }

        return convertView;
    }

    public Tache getTache(int which) {
        return this.listTache.get(which);
    }

    public void choisirTache(int position) {
        Tache tache = getItem(position);
        SelectionMorningRecurrenteAdapter.this.editMRFragment.ajouterTache(tache);
    }
}
