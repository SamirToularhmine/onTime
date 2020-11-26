package com.example.onTime.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.onTime.R;
import com.example.onTime.modele.Tache;
import com.example.onTime.modele.TacheHeureDebut;
import com.example.onTime.modele.Toolbox;

import java.util.List;
import java.util.Map;

public class HomeTacheAdapter extends RecyclerView.Adapter<HomeTacheAdapter.TacheViewHolder> {
    private List<TacheHeureDebut> listeTachesHeuresDebut;

    public HomeTacheAdapter(List<TacheHeureDebut> listeTachesHeuresDebut) {
        this.listeTachesHeuresDebut = listeTachesHeuresDebut;
    }

    public List<TacheHeureDebut> getList() {
        return this.listeTachesHeuresDebut;
    }

    public static class TacheViewHolder extends RecyclerView.ViewHolder {
        TextView nomTache;
        TextView duree;
        TextView heureDebut;

        public TacheViewHolder(View itemView) {
            super(itemView);
            nomTache = itemView.findViewById(R.id.nomtache);
            duree = itemView.findViewById(R.id.dureetache);
            heureDebut = itemView.findViewById(R.id.labelHeureDebutTache);
        }
    }

    @NonNull
    @Override
    public TacheViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.tache_item_layout, parent, false);
        return new TacheViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final TacheViewHolder holder, int position) {
        Tache tache = this.listeTachesHeuresDebut.get(position).getTache();
        holder.nomTache.setText(tache.getNom());
        holder.duree.setText(Toolbox.secondesToMinSecString(tache.getDuree()));

        long heureDebutDepuisMinuit = Toolbox.getHeureFromEpoch(this.listeTachesHeuresDebut.get(position).getHeureDebut());
        String heures = String.valueOf(Toolbox.getHourFromSecondes(heureDebutDepuisMinuit));
        String minutes = String.valueOf(Toolbox.getMinutesFromSecondes(heureDebutDepuisMinuit));
        if (minutes.length() == 1) {
            minutes = "0" + minutes;
        }
        String affichageHeure = heures+"H"+minutes;
        holder.heureDebut.setText(affichageHeure);
    }

    public void setListeTachesHeuresDebut(List<TacheHeureDebut> listeTachesHeuresDebut) {
        this.listeTachesHeuresDebut = listeTachesHeuresDebut;
    }

    //    public void updateHeuresDebut(List<long> listeHeuresDebut) {
//        this.
//    }

    @Override
    public int getItemCount() {
        return listeTachesHeuresDebut.size();
    }



}
