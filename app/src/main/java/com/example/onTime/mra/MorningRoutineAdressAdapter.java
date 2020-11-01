package com.example.onTime.mra;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.onTime.R;
import com.example.onTime.modele.MRA;
import com.example.onTime.modele.MorningRoutine;
import com.example.onTime.morning_routine.MorningRoutineActivity;


import java.util.List;

public class MorningRoutineAdressAdapter extends RecyclerView.Adapter<MorningRoutineAdressAdapter.MoringRoutineAdressViewHolder> {

    private List<MRA> listMRA;
    private Context context;

    public MorningRoutineAdressAdapter(List<MRA> listMRA, Context context) {
        this.context = context;
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
        Intent intent = new Intent(view.getContext(), MorningRoutineActivity.class);

        intent.putExtra("morning_routine", morningRoutine);
        intent.putExtra("position", position);
        ((Activity) context).startActivityForResult(intent, 1);
    }


}
