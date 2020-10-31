package com.example.onTime.mra;

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


import java.util.List;

public class MorningRoutineAdressAdapter extends RecyclerView.Adapter<MorningRoutineAdressAdapter.MoringRoutineAdressViewHolder> {

    private List<MRA> listMRA;

    public MorningRoutineAdressAdapter(List<MRA> listMRA) {
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
        holder.moringRoutineView.setText(mra.getMorningRoutine().getNom());
        holder.adresseView.setText(String.valueOf(mra.getAdresse().getNom()));

        holder.moringRoutineView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                MorningRoutine morningRoutine = listMRA.get(holder.getAdapterPosition()).getMorningRoutine();
                Toast.makeText(v.getContext(), "Selected : " + morningRoutine.getNom(), Toast.LENGTH_SHORT).show();
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




    /*
    public View getView(int position, View convertView, final ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.mra_item_layout, parent, false);
            holder = new ViewHolder();
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.moringRoutineView = convertView.findViewById(R.id.button_morning_routine);
        holder.moringRoutineView.setTag(position);
        holder.adresseView = convertView.findViewById(R.id.button_adresse);
        holder.adresseView.setTag(position);
        holder.deleteMR = convertView.findViewById(R.id.button_delete);
        holder.deleteMR.setTag(position);

        holder.moringRoutineView.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                int position=(Integer)v.getTag();
                MorningRoutine morningRoutineClicked = listData.get(position).getMorningRoutine();
                Toast.makeText(parent.getContext(), "Selected : " + morningRoutineClicked, Toast.LENGTH_SHORT).show();
            }
        });

        holder.adresseView.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                int position=(Integer)v.getTag();
                Adresse adresseClicked = listData.get(position).getAdresse();
                Toast.makeText(parent.getContext(), "Selected :" + adresseClicked, Toast.LENGTH_SHORT).show();
            }
        });

        holder.deleteMR.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                int position=(Integer)v.getTag();
                MRA morningRoutineClicked = listData.get(position);
                AlertDialog alertDialog =  AskOption(morningRoutineClicked);
                alertDialog.show();
            }
        });

        MRA mra = this.listData.get(position);
        holder.moringRoutineView.setText(mra.getMorningRoutine().toString()); // verifer null
        Adresse a  = mra.getAdresse();
        if (a == null)
            holder.adresseView.setText("+");
        else
            holder.adresseView.setText(mra.getAdresse().toString());

        return convertView;
    }

    private AlertDialog AskOption(final MRA morningRoutineClicked) {

        return new AlertDialog.Builder(context)
                // set message, title, and icon
                .setTitle("Delete")
                .setMessage("Do you want to Delete")
                .setIcon(R.drawable.ic_delete_24px)

                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {
                        listData.remove(morningRoutineClicked);
                        notifyDataSetChanged();
                        dialog.dismiss();
                        Toast.makeText(context, "Removed : " + morningRoutineClicked, Toast.LENGTH_SHORT).show();
                    }

                })
                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        dialog.dismiss();

                    }
                })
                .create();
    }


    static class ViewHolder {
        TextView moringRoutineView;
        TextView adresseView;
        TextView deleteMR;
    }

     */

}
