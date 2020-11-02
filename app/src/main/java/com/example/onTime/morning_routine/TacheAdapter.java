package com.example.onTime.morning_routine;

import android.app.AlertDialog;
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


import java.util.List;

public class TacheAdapter extends RecyclerView.Adapter<TacheAdapter.TacheViewHolder> {
    private List<Tache> listTache;

    public TacheAdapter(List<Tache> listTache) {
        this.listTache = listTache;
    }

    public List<Tache> getList() {
        return this.listTache;
    }

    public static class TacheViewHolder extends RecyclerView.ViewHolder {
        TextView nomTache;
        TextView duree;

        public TacheViewHolder(View itemView) {
            super(itemView);
            nomTache = itemView.findViewById(R.id.nomtache);
            duree = itemView.findViewById(R.id.dureetache);
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
        Tache tache = listTache.get(position);
        holder.nomTache.setText(tache.getNom());
        holder.duree.setText(String.valueOf(tache.getDuree()));


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //Tache tacheClicked = listTache.get(holder.getAdapterPosition());
                modifierTache(holder.itemView, holder);
                //Toast.makeText(v.getContext(), "Selected : " + tacheClicked.getNom(), Toast.LENGTH_SHORT).show();
            }
        });

    }


    public void modifierTache(View view, final TacheViewHolder holder) {
        LayoutInflater factory = LayoutInflater.from(view.getContext());
        final View textEntryView = factory.inflate(R.layout.ajout_tache, null);
        final int position = holder.getAdapterPosition();
        final Tache tacheClicked = listTache.get(position);


        final EditText nomTache = textEntryView.findViewById(R.id.nomtachecreate);
        final NumberPicker duree = textEntryView.findViewById(R.id.duree);
        nomTache.setText(tacheClicked.getNom());

        duree.setMinValue(0);
        duree.setMaxValue(60);
        duree.setValue((int) tacheClicked.getDuree() / 60);


        final AlertDialog.Builder alert = new AlertDialog.Builder(view.getContext());

        alert.setTitle("Enter the Text:")
                .setView(textEntryView)
                .setPositiveButton("Save",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                tacheClicked.setNom(nomTache.getText().toString());
                                tacheClicked.setDuree(duree.getValue() * 60);
                                TacheAdapter.this.notifyItemChanged(position);
                            }
                        })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int whichButton) {
                            }
                        });
        alert.show();
    }


    @Override
    public int getItemCount() {
        return listTache.size();
    }

}