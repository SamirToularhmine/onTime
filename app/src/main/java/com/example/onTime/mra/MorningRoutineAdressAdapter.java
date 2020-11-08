package com.example.onTime.mra;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.widget.RecyclerView;

import com.example.onTime.R;
import com.example.onTime.fragments.EditMRFragment;
import com.example.onTime.modele.MRA;
import com.example.onTime.modele.MorningRoutine;
import com.example.onTime.morning_routine.MorningRoutineActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;


import java.util.List;

public class MorningRoutineAdressAdapter extends RecyclerView.Adapter<MorningRoutineAdressAdapter.MoringRoutineAdressViewHolder> {

    private List<MRA> listMRA;
    private Fragment fragment;

    public MorningRoutineAdressAdapter(List<MRA> listMRA, Fragment fragment) {
        this.fragment = fragment;
        this.listMRA = listMRA;
    }

    public static class MoringRoutineAdressViewHolder extends RecyclerView.ViewHolder {
        TextView moringRoutineView;
        TextView adresseView;

        public MoringRoutineAdressViewHolder(View itemView){
            super(itemView);
            moringRoutineView = itemView.findViewById(R.id.button_morning_routine);
            adresseView = itemView.findViewById(R.id.button_adresse);
        }

    }

    @NonNull
    @Override
    public MoringRoutineAdressViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.mra_item_layout, parent, false);
        return new MorningRoutineAdressAdapter.MoringRoutineAdressViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MoringRoutineAdressViewHolder holder, int position) {
        MRA mra = listMRA.get(position);
        String nomAdresse;
        holder.moringRoutineView.setText(mra.getMorningRoutine().getNom());
        if (mra.getAdresse() == null)
            nomAdresse = "+";
        else
            nomAdresse = mra.getAdresse().getNom();
        holder.adresseView.setText(String.valueOf(nomAdresse));

        holder.moringRoutineView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                MorningRoutine morningRoutine = listMRA.get(holder.getAdapterPosition()).getMorningRoutine();
                modifierMorningRoutine(holder.itemView, morningRoutine, holder.getAdapterPosition());
                //Toast.makeText(v.getContext(), "Selected : " + morningRoutine.getNom(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public int getItemCount() {
        return listMRA.size();
    }

    public List<MRA> getList() {
        return listMRA;
    }

    public void modifierMorningRoutine(View view, MorningRoutine morningRoutine, int position) {
        Bundle bundle = new Bundle();
        bundle.putParcelable("morning_routine", morningRoutine);
        bundle.putInt("position", position);

        AppCompatActivity activity = (AppCompatActivity) view.getContext();

        NavHostFragment navHostFragment = (NavHostFragment) activity.getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        NavController navController = navHostFragment.getNavController();
        navController.navigate(R.id.editMRFragment, bundle);
    }
}
