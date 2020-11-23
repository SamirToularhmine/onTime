package com.example.onTime.mrt;

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
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.onTime.R;
import com.example.onTime.fragments.HomeFragment;
import com.example.onTime.modele.MRT;
import com.example.onTime.modele.MorningRoutine;
import com.google.gson.Gson;

import java.util.List;

public class HomeMorningRoutineAdressAdapter extends ArrayAdapter<MRT> {

    private List<MRT> listMRT;
    private HomeFragment homeFragment;
    private Context context;

    public HomeMorningRoutineAdressAdapter(@NonNull Context context, List<MRT> listMRT, HomeFragment homeFragment) {
        super(context, 0, listMRT);
        this.context = context;
        this.listMRT = listMRT;
        this.homeFragment = homeFragment;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        MRT mrt = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.home_mr_item_layout, parent, false);
        }

        TextView mr = convertView.findViewById(R.id.home_morning_routine);
        mr.setTag(position);


        if (mrt != null) {
            if (mrt.getMorningRoutine() != null)
                mr.setText(mrt.getMorningRoutine().getNom());
            else
                mr.setText("pas défini");
        }

        return convertView;
    }


    public MRT getMrt(int which) {
        return this.listMRT.get(which);
    }

    public void choisirMRT(int position) {
        MRT mrt = getItem(position);
        //Log.d("CLICK", "onClick: "+ mra.getMorningRoutine().getNom());
        HomeMorningRoutineAdressAdapter.this.homeFragment.changerCurrentMr(mrt);
        int idCurrentMRA = mrt.getId();

        SharedPreferences sharedPreferences = context.getSharedPreferences("onTimePreferences", Context.MODE_PRIVATE);
        sharedPreferences.edit()
                .putInt("current_id_MRA", idCurrentMRA)
                .apply();
    }
}

