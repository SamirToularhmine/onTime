package com.example.onTime;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.onTime.modele.Adresse;
import com.example.onTime.modele.MRA;
import com.example.onTime.modele.MorningRoutine;
import com.example.onTime.modele.Tache;
import com.example.onTime.modele.Toolbox;

import java.util.List;

public class TacheAdapter extends BaseAdapter {
    private List<Tache> listData;
    private LayoutInflater layoutInflater;
    private Context context;

    public TacheAdapter(Context aContext, List<Tache> listTache) {
        this.context = aContext;
        this.listData = listTache;
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
        TacheAdapter.ViewHolder holder;
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.tache_item_layout, parent, false);
            holder = new TacheAdapter.ViewHolder();
            convertView.setTag(holder);
        } else {
            holder = (TacheAdapter.ViewHolder) convertView.getTag();
        }

        holder.nomTache = convertView.findViewById(R.id.button_nomTache);
        holder.nomTache.setTag(position);



        Tache tache = this.listData.get(position);
        String heure = Toolbox.formaterHeure(Toolbox.getHourFromSecondes(tache.getDuree()), Toolbox.getMinutesFromSecondes(tache.getDuree()));
        String afficher = tache.getNom() + " " + heure;
        holder.nomTache.setText(afficher);


        return convertView;
    }



    static class ViewHolder {
        TextView nomTache;

    }
}
