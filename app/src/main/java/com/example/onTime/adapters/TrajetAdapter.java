package com.example.onTime.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.onTime.R;
import com.example.onTime.modele.MRManager;
import com.example.onTime.modele.Trajet;
import com.google.gson.Gson;

import java.util.List;

public class TrajetAdapter extends RecyclerView.Adapter<TrajetAdapter.TacheViewHolder> {
    private List<Trajet> listTrajet;
    private int positionTrajet, positionMRT;
    private boolean onlyShowList;

    public TrajetAdapter(List<Trajet> listTrajet, int positionMRT, boolean onlyShowList) {
        this.listTrajet = listTrajet;
        this.positionMRT = positionMRT;
        this.onlyShowList = onlyShowList;
    }

    public List<Trajet> getList() {
        return this.listTrajet;
    }

    public static class TacheViewHolder extends RecyclerView.ViewHolder {
        TextView nomTrajet, adresseDepart, adresseArrivee;
        Button boutonModifier, boutonSelectionner;

        public TacheViewHolder(View itemView) {
            super(itemView);
            adresseDepart = itemView.findViewById(R.id.adressedepart);
            adresseArrivee = itemView.findViewById(R.id.adressearrivee);
            boutonModifier = itemView.findViewById(R.id.boutonmodifiertrajet);
            boutonSelectionner = itemView.findViewById(R.id.boutonselectionnertrajet);
        }
    }

    @NonNull
    @Override
    public TacheViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.trajet_item_layout, parent, false);
        return new TrajetAdapter.TacheViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final TacheViewHolder holder, int position) {
        this.positionTrajet = position;
        Trajet trajet = listTrajet.get(this.positionTrajet);
        //holder.nomTrajet.setText(trajet.getNom());
        holder.adresseDepart.setText(trajet.getAdresseDepart());
        holder.adresseArrivee.setText(trajet.getAdresseArrivee());
        if (onlyShowList) {
            holder.boutonSelectionner.setVisibility(View.GONE);
        }


        // l'utilisateur veut modifier le trajet
        holder.boutonModifier.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                modifierTrajet(holder.itemView, holder);
            }
        });


        // l'utilisateur veut choisir ce trajet pour la morning routine
        holder.boutonSelectionner.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                selectionnerTrajet(holder.itemView, holder);
            }
        });

    }


    public void modifierTrajet(View view, final TacheViewHolder holder) {
        int position = holder.getAdapterPosition();
        Trajet trajet = listTrajet.get(position);

        Bundle bundle = new Bundle();
        bundle.putParcelable("trajet", trajet);
        bundle.putInt("position", position);

        AppCompatActivity activity = (AppCompatActivity) view.getContext();

        NavHostFragment navHostFragment = (NavHostFragment) activity.getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        NavController navController = navHostFragment.getNavController();
        navController.navigate(R.id.editTFragment, bundle);
    }

    public void selectionnerTrajet(View view, final TacheViewHolder holder) {
        Context context = view.getContext().getApplicationContext();
        SharedPreferences sharedPreferences = context.getSharedPreferences("onTimePreferences", Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString("MRManager", "");
        MRManager mrManager = gson.fromJson(json, MRManager.class);

        mrManager.getListMRT().get(this.positionMRT).setTrajet(this.listTrajet.get(holder.getAdapterPosition()));

        SharedPreferences.Editor editor = sharedPreferences.edit();
        gson = new Gson();
        json = gson.toJson(mrManager);
        editor.putString("MRManager", json);
        editor.apply();

        AppCompatActivity activity = (AppCompatActivity) view.getContext();

        NavHostFragment navHostFragment = (NavHostFragment) activity.getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        NavController navController = navHostFragment.getNavController();
        navController.navigate(R.id.listMRFragment);
    }


    @Override
    public int getItemCount() {
        return listTrajet.size();
    }

}
