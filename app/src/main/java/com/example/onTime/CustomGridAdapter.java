package com.example.onTime;

import android.content.Context;
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
            convertView = layoutInflater.inflate(R.layout.grid_item_layout, null);
            holder = new ViewHolder();
            holder.moringRoutineView = convertView.findViewById(R.id.button_morning_routine);
            holder.moringRoutineView.setTag(position);
            holder.adresseView = convertView.findViewById(R.id.button_adresse);
            holder.adresseView.setTag(position);

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
                    System.out.println(adresseClicked);
                    Toast.makeText(parent.getContext(), "Selected : " + adresseClicked, Toast.LENGTH_SHORT).show();

                }
            });

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        MRA mra = this.listData.get(position);
        holder.moringRoutineView.setText(mra.getMorningRoutine().toString()); // verifer null
        Adresse a  = mra.getAdresse();
        if (a == null)
            holder.adresseView.setText("+");
        else
            holder.adresseView.setText(mra.getAdresse().toString());

        return convertView;
    }


    static class ViewHolder {
        Button moringRoutineView;
        Button adresseView;
    }

}
