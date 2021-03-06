package com.example.onTime.adapters;

import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.onTime.R;
import com.example.onTime.modele.Tache;
import com.example.onTime.modele.Toolbox;
import com.google.android.material.textfield.TextInputLayout;


import java.util.List;

/**
 * Adapter pour la liste des tâches récurrentes
 */
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

        public TachesRecurrentesViewHolder(View itemView) {
            super(itemView);
            nomTache = itemView.findViewById(R.id.nom_tache);
            dureeTache = itemView.findViewById(R.id.duree_tache);
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
        String affichage = Toolbox.getMinutesFromSecondes(tache.getDuree()) + " m";
        holder.dureeTache.setText(affichage);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                modifierTacheReccurente(v, holder);
            }
        });
    }

    /**
     * Modification d'uneTacheReccurent
     *
     * @param view   est la vue actuelle
     * @param holder est le recycler holder
     */
    public void modifierTacheReccurente(View view, final TachesRecurrentesViewHolder holder) {
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

        alert.setTitle(R.string.modifer_tache_rec)
                .setView(textEntryView)
                .setPositiveButton(R.string.sauvegarder,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                tacheClicked.setNom(nomTache.getText().toString());
                                tacheClicked.setDuree(duree.getValue() * 60);
                                TachesRecurrentesAdapter.this.notifyItemChanged(position);
                            }
                        })
                .setNegativeButton(R.string.annuler,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int whichButton) {
                            }
                        });
        alert.show();
    }

    @Override
    public int getItemCount() {
        return this.listeTaches.size();
    }
}
