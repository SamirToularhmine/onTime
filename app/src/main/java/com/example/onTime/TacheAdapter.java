package com.example.onTime;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.onTime.modele.Tache;


import java.util.List;

public class TacheAdapter extends RecyclerView.Adapter<TacheAdapter.TacheViewHolder> {
    private List<Tache> listTache;



    public static class TacheViewHolder extends RecyclerView.ViewHolder {
        TextView nomTache;
        TextView duree;

        public TacheViewHolder(View itemView) {
            super(itemView);
            nomTache = itemView.findViewById(R.id.nomtache);
            duree = itemView.findViewById(R.id.dureetache);
        }
    }

        public TacheAdapter(List<Tache> listTache) {
        this.listTache = listTache;
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
                Tache tacheClicked = listTache.get(holder.getAdapterPosition());
                Toast.makeText(v.getContext(), "Selected : " + tacheClicked.getNom(), Toast.LENGTH_SHORT).show();
            }
        });

    }



    @Override
    public int getItemCount() {
        return listTache.size();
    }

    public List<Tache> getListTache() {
        return listTache;
    }

}
