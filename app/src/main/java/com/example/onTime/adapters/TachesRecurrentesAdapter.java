package com.example.onTime.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.onTime.R;
import com.example.onTime.fragments.EditMRFragment;
import com.example.onTime.modele.MRManager;
import com.example.onTime.modele.Tache;
import com.example.onTime.modele.Trajet;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;

import java.util.List;

public class TachesRecurrentesAdapter extends RecyclerView.Adapter<TachesRecurrentesAdapter.TachesRecurrentesViewHolder> {
    private List<Tache> listeTaches;


    public TachesRecurrentesAdapter(List<Tache> listeTaches) {
        this.listeTaches = listeTaches;

    }

    public List<Tache> getList() {
        return this.listeTaches;
    }

    public static class TachesRecurrentesViewHolder extends RecyclerView.ViewHolder {
        TextView nomTache, dureeTache;
        Button boutonModifier, boutonSelectionner;

        public TachesRecurrentesViewHolder(View itemView) {
            super(itemView);
            nomTache = itemView.findViewById(R.id.nom_tache);
            dureeTache = itemView.findViewById(R.id.duree_tache);
            boutonModifier = itemView.findViewById(R.id.boutonmodifiertrajet);
            boutonSelectionner = itemView.findViewById(R.id.boutonselectionnertrajet);
        }
    }

    @NonNull
    @Override
    public TachesRecurrentesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.tache_recurrente_item_layout, parent, false);
        return new TachesRecurrentesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final TachesRecurrentesViewHolder holder, int position) {

        Tache tache = this.listeTaches.get(position);
        holder.nomTache.setText(tache.getNom());
        holder.dureeTache.setText(String.valueOf(tache.getDuree()));

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                modifierTache(v, holder);
            }
        });
    }


    public void modifierTache(View view, final TachesRecurrentesViewHolder holder) {
        final int position = holder.getAdapterPosition();

        LayoutInflater factory = LayoutInflater.from(view.getContext());
        final View textEntryView = factory.inflate(R.layout.ajout_tache, null);
        final Tache tacheClicked = this.listeTaches.get(position);

        TextInputLayout nomTacheLayout = textEntryView.findViewById(R.id.nomtachecreate);
        final EditText nomTache = nomTacheLayout.getEditText();
        final NumberPicker duree = textEntryView.findViewById(R.id.duree);
        nomTache.setText(tacheClicked.getNom());

        duree.setMinValue(0);
        duree.setMaxValue(60);
        duree.setValue((int) tacheClicked.getDuree() / 60);

        final android.app.AlertDialog.Builder alert = new android.app.AlertDialog.Builder(view.getContext());

        alert.setTitle("Modifier la tache récurrente :")
                .setView(textEntryView)
                .setPositiveButton("Sauvegarder",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                tacheClicked.setNom(nomTache.getText().toString());
                                tacheClicked.setDuree(duree.getValue() * 60);
                                TachesRecurrentesAdapter.this.notifyItemChanged(position);
                            }
                        })
                .setNegativeButton("Annuler",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int whichButton) {
                            }
                        });
        alert.show();

/*
         AppCompatActivity activity = (AppCompatActivity) view.getContext();

        NavHostFragment navHostFragment = (NavHostFragment) activity.getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        NavController navController = navHostFragment.getNavController();
        navController.navigate(R.id.editrec, bundle);

*/
    }

    @Override
    public int getItemCount() {
        return this.listeTaches.size();
    }

}
