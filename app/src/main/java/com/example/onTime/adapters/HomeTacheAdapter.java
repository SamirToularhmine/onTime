package com.example.onTime.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.onTime.R;
import com.example.onTime.modele.Tache;
import com.example.onTime.modele.Toolbox;

import java.util.List;

public class HomeTacheAdapter extends RecyclerView.Adapter<HomeTacheAdapter.TacheViewHolder> {
    private List<Tache> listTache;

    public HomeTacheAdapter(List<Tache> listTache) {
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
        holder.duree.setText(Toolbox.secondesToMinSecString(tache.getDuree()));
    }

    @Override
    public int getItemCount() {
        return listTache.size();
    }



}
