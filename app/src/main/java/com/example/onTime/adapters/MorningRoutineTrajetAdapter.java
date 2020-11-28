package com.example.onTime.adapters;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.onTime.R;
import com.example.onTime.modele.Trajet;
import com.example.onTime.modele.MRT;
import com.example.onTime.modele.MorningRoutine;


import java.util.List;

/**
 * Adapater de la MRT
 */
public class MorningRoutineTrajetAdapter extends RecyclerView.Adapter<MorningRoutineTrajetAdapter.MoringRoutineAdressViewHolder> {

    private List<MRT> listMRT;

    public MorningRoutineTrajetAdapter(List<MRT> listMRT) {
        this.listMRT = listMRT;
    }

    public static class MoringRoutineAdressViewHolder extends RecyclerView.ViewHolder {
        TextView moringRoutineView;
        TextView trajetView;

        public MoringRoutineAdressViewHolder(View itemView) {
            super(itemView);
            moringRoutineView = itemView.findViewById(R.id.button_morning_routine);
            trajetView = itemView.findViewById(R.id.button_trajet);
        }

    }

    @NonNull
    @Override
    public MoringRoutineAdressViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.mra_item_layout, parent, false);
        return new MorningRoutineTrajetAdapter.MoringRoutineAdressViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MoringRoutineAdressViewHolder holder, int position) {
        MRT MRT = listMRT.get(position);
        String nomAdresse;
        holder.moringRoutineView.setText(MRT.getMorningRoutine().getNom());

        if (MRT.getTrajet() == null) {
            nomAdresse = "+";
        } else {
            nomAdresse = MRT.getTrajet().getNom();
        }

        holder.trajetView.setText(nomAdresse);
        holder.trajetView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Trajet trajet = listMRT.get(holder.getAdapterPosition()).getTrajet();
                modifierTrajet(holder.itemView, trajet, holder.getAdapterPosition());
            }

        });
        holder.moringRoutineView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                MorningRoutine morningRoutine = listMRT.get(holder.getAdapterPosition()).getMorningRoutine();
                modifierMorningRoutine(holder.itemView, morningRoutine, holder.getAdapterPosition());
            }
        });
    }

    @Override
    public int getItemCount() {
        return listMRT.size();
    }

    public List<MRT> getList() {
        return listMRT;
    }

    /**
     * Modification d'une mornging routine
     *
     * @param view           est la vue du frament
     * @param morningRoutine est la morning routine a modifier
     * @param position       est la position de la morning routine
     */
    public void modifierMorningRoutine(View view, MorningRoutine morningRoutine, int position) {
        Bundle bundle = new Bundle();
        bundle.putParcelable("morning_routine", morningRoutine);
        bundle.putInt("position", position);

        AppCompatActivity activity = (AppCompatActivity) view.getContext();

        NavHostFragment navHostFragment = (NavHostFragment) activity.getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        NavController navController = navHostFragment.getNavController();
        navController.navigate(R.id.editMRFragment, bundle);
    }

    /**
     * Modifie le trajet d'une MRT
     *
     * @param view     est la vue du fragment
     * @param trajet   est le trajet a modifier
     * @param position est la position du trajet
     */
    public void modifierTrajet(View view, Trajet trajet, int position) {
        Bundle bundle = new Bundle();
        bundle.putParcelable("trajet", trajet);
        bundle.putInt("position", position);

        AppCompatActivity activity = (AppCompatActivity) view.getContext();

        NavHostFragment navHostFragment = (NavHostFragment) activity.getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        NavController navController = navHostFragment.getNavController();
        navController.navigate(R.id.listAFragment, bundle);
    }
}
