package com.example.onTime.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.onTime.R;
import com.example.onTime.modele.MRManager;
import com.example.onTime.modele.Toolbox;

public class NextAlarmHeaderFragment extends Fragment {

    private MRManager mrManager;

    public NextAlarmHeaderFragment() {
        // Required empty public constructor
    }

    public static NextAlarmHeaderFragment newInstance() {
        NextAlarmHeaderFragment fragment = new NextAlarmHeaderFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        this.mrManager = (MRManager) getArguments().get("mr_manager");
        return inflater.inflate(R.layout.next_alarm_header_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

      TextView heureReveil = view.findViewById(R.id.heureReveil);
        if(heureReveil != null){
            heureReveil.setText(Toolbox.formaterHeure(Toolbox.getHourFromSecondes(this.mrManager.getHeureArrivee()), Toolbox.getMinutesFromSecondes(this.mrManager.getHeureArrivee())));
        }
    }
}