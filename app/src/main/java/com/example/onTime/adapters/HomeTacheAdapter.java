package com.example.onTime.adapters;

import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.onTime.R;
import com.example.onTime.modele.MRT;
import com.example.onTime.modele.Tache;
import com.example.onTime.modele.TacheHeureDebut;
import com.example.onTime.modele.Toolbox;

import java.util.List;
import java.util.Map;

public class HomeTacheAdapter extends RecyclerView.Adapter<HomeTacheAdapter.TacheViewHolder> {
    private List<TacheHeureDebut> listeTachesHeuresDebut;
    private Button boutonGoogleMaps;
    private MRT mrt;

    public HomeTacheAdapter(List<TacheHeureDebut> liste, MRT mrt) {
        this.listeTachesHeuresDebut = liste;
        this.mrt = mrt;
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

    @Override
    public int getItemViewType(int position) {
        return (position == listeTachesHeuresDebut.size()) ? 1 : 0; // 1=FOOTER, 0=TACHE
    }

    @NonNull
    @Override
    public TacheViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == 0) { // TACHE
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.tache_item_layout, parent, false);
            return new TacheViewHolder(view);
        }else{ // FOOTER
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.home_mr_item_gmaps_button_layout, parent, false);
            this.boutonGoogleMaps = view.findViewById(R.id.bouton_gmaps);
            if(HomeTacheAdapter.this.mrt.getTrajet() == null){
                HomeTacheAdapter.this.boutonGoogleMaps.setText(R.string.aucun_trajet);
                HomeTacheAdapter.this.boutonGoogleMaps.setClickable(false);
            }
            return new TacheViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull final TacheViewHolder holder, int position) {
        if (position < this.getItemCount() - 1) {
            Tache tache = this.listeTachesHeuresDebut.get(position).getTache();
            holder.nomTache.setText(tache.getNom());
            holder.duree.setText(Toolbox.secodesToMin(tache.getDuree()));

            long heureDebutDepuisMinuit = Toolbox.getHeureFromEpoch(this.listeTachesHeuresDebut.get(position).getHeureDebut());
            String heures = String.valueOf(Toolbox.getHourFromSecondes(heureDebutDepuisMinuit));
            String minutes = String.valueOf(Toolbox.getMinutesFromSecondes(heureDebutDepuisMinuit));
            if (minutes.length() == 1) {
                minutes = "0" + minutes;
            }
            String affichageHeure = heures+"H"+minutes;
            holder.heureDebut.setText(affichageHeure);
        }else{
            this.boutonGoogleMaps.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Google maps veut des + à la place des espaces dans la destination
                    String destination = HomeTacheAdapter.this.mrt.getTrajet().getAdresseArrivee().replaceAll(" ", "+");
                    Uri gmmIntentUri = Uri.parse("google.navigation:q=" + destination);
                    Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                    mapIntent.setPackage("com.google.android.apps.maps");
                    v.getContext().startActivity(mapIntent);
                }
            });
        }
    }

    public void setMrt(MRT mrt) {
        this.mrt = mrt;
    }

    public void setListeTachesHeuresDebut(List<TacheHeureDebut> listeTachesHeuresDebut) {
        this.listeTachesHeuresDebut = listeTachesHeuresDebut;
    }

    @Override
    public int getItemCount() {
        return listeTachesHeuresDebut.size() + 1; // + 1 car on ajoute le footer
    }

}
