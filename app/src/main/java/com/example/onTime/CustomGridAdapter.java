package com.example.onTime;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.onTime.modele.Adresse;
import com.example.onTime.modele.MRA;
import com.example.onTime.modele.MorningRoutine;

import java.util.List;

public class CustomGridAdapter extends BaseAdapter {

    private List<MRA> listData;
    private LayoutInflater layoutInflater;
    private Context context;

    public CustomGridAdapter(Context aContext, List<MRA> listData) {
        this.context = aContext;
        this.listData = listData;
        layoutInflater = LayoutInflater.from(aContext);
    }

    @Override
    public int getCount() {
        return listData.size();
    }



    @Override
    public Object getItem(int position) {
        return listData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, final ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.grid_item_layout, parent, false);
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

}
