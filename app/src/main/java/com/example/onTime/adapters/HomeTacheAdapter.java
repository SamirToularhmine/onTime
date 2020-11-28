package com.example.onTime.adapters;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.onTime.R;
import com.example.onTime.modele.MRT;
import com.example.onTime.modele.Tache;
import com.example.onTime.modele.TacheHeureDebut;
import com.example.onTime.modele.Toolbox;
import com.google.android.material.button.MaterialButton;

import java.net.URL;
import java.util.List;

/**
 * Adapater pour les tâches dans le fragment home
 */
public class HomeTacheAdapter extends RecyclerView.Adapter<HomeTacheAdapter.TacheViewHolder> {
    private List<TacheHeureDebut> listeTachesHeuresDebut;
    private MaterialButton boutonGoogleMaps;
    private MRT mrt;
    private SharedPreferences sharedPreferences;

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
        if (viewType == 0) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.tache_item_layout, parent, false);
            return new TacheViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.home_mr_item_gmaps_button_layout, parent, false);
            this.boutonGoogleMaps = view.findViewById(R.id.bouton_gmaps);

            // affichage du bon icône en fonction du mode de déplacement choix dans les paramètres
            // possible uniquement dans la version Lollipop et supérieur
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                this.sharedPreferences = view.getContext().getApplicationContext().getSharedPreferences("onTimePreferences", Context.MODE_PRIVATE);
                int ridingMethod = sharedPreferences.getInt("ridingMethod", 0);
                switch (ridingMethod) {

                    case 1: // Vélo
                        HomeTacheAdapter.this.boutonGoogleMaps.setIcon(view.getContext().getDrawable(R.drawable.ic_baseline_directions_bike_24));
                        break;

                    case 2: // A pied
                        HomeTacheAdapter.this.boutonGoogleMaps.setIcon(view.getContext().getDrawable(R.drawable.ic_baseline_directions_walk_24));
                        break;

                    default: // 0, Voiture, équivaut également à la valeur par défaut de la récupération dans les sharedPreferences
                        HomeTacheAdapter.this.boutonGoogleMaps.setIcon(view.getContext().getDrawable(R.drawable.ic_baseline_directions_car_24));
                }
            }


            if (HomeTacheAdapter.this.mrt.getTrajet() == null) {
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

            String affichageHeure = heures + "H" + minutes;
            holder.heureDebut.setText(affichageHeure);
        } else {
            this.boutonGoogleMaps.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String destination =
                            HomeTacheAdapter.this.mrt.getTrajet().getCoordDestination().latitude
                            + ","
                            + HomeTacheAdapter.this.mrt.getTrajet().getCoordDestination().longitude;

                    int ridingMethod = HomeTacheAdapter.this.sharedPreferences.getInt("ridingMethod", 0);
                    String travelMode;
                    switch (ridingMethod) {
                        case 1:
                            travelMode = "b";
                            break;

                        case 2:
                            travelMode = "w";
                            break;

                        default:
                            travelMode = "d";
                    }

                    Uri gmmIntentUri = Uri.parse("google.navigation:q=" + destination + "&mode=" + travelMode);
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
        if (listeTachesHeuresDebut != null)
            return listeTachesHeuresDebut.size() + 1; // + 1 car on ajoute le footer
        return 0;
    }

}
