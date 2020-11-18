package com.example.onTime.mra;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.onTime.R;
import com.example.onTime.fragments.HomeFragment;
import com.example.onTime.modele.MRA;
import com.example.onTime.modele.MorningRoutine;
import com.google.gson.Gson;

import java.util.List;

public class HomeMorningRoutineAdressAdapter extends ArrayAdapter<MRA> {

    private List<MRA> listMRA;
    private HomeFragment homeFragment;
    private Context context;


    public HomeMorningRoutineAdressAdapter(@NonNull Context context, List<MRA> listMRA, HomeFragment homeFragment) {
        super(context, 0, listMRA);
        this.context = context;
        this.listMRA = listMRA;
        this.homeFragment = homeFragment;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        MRA mra = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.home_mr_item_layout, parent, false);
        }

        TextView mr = convertView.findViewById(R.id.home_morning_routine);
        mr.setTag(position);



        mr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = (Integer)v.getTag();
                MRA mra = getItem(position);
                //Log.d("CLICK", "onClick: "+ mra.getMorningRoutine().getNom());
                HomeMorningRoutineAdressAdapter.this.homeFragment.changerCurrentMr(mra);
                SharedPreferences sharedPreferences = context.getSharedPreferences("onTimePreferences", Context.MODE_PRIVATE);
                Gson gson = new Gson();
                String jsonMRA = gson.toJson(mra);
                sharedPreferences.edit()
                        .putString("CurrentMRA", jsonMRA)
                        .putInt("CurrentMRAPosition", position)
                        .apply();

            }
        });

        if (mra != null) {
            if (mra.getMorningRoutine() != null)
                mr.setText(mra.getMorningRoutine().getNom());
            else
                mr.setText("pas défini");
        }

        return convertView;
    }




}
