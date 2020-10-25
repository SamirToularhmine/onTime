package com.example.onTime;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.onTime.modele.Adresse;
import com.example.onTime.modele.MRA;
import com.example.onTime.modele.MRManager;
import com.example.onTime.modele.MorningRoutine;
import com.example.onTime.modele.Toolbox;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity {

    private ListView listView;
    private MRManager mrManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        List<MRA> mras = createMRA(18);
        this.mrManager = new MRManager(39600, mras);

        this.listView = findViewById(R.id.morningRoutinesList);
        this.listView.setAdapter(new CustomGridAdapter(this, mras));

        TextView heureReveil = findViewById(R.id.heureReveil);
        if(heureReveil != null){
            heureReveil.setText(Toolbox.formaterHeure(Toolbox.getHourFromSecondes(this.mrManager.getHeureArrivee()), Toolbox.getMinutesFromSecondes(this.mrManager.getHeureArrivee())));
        }
    }

    private List<MRA> createMRA(int longeur){
        List<MRA> mra = new ArrayList<>();

        for (int i = 0; i < longeur; i++) {
            mra.add(new MRA(new MorningRoutine("Morning Routine" + i), new Adresse("adresse" + i, "depart" + i, "arrivee" + i)));
        }

        return mra;
    }

    public void createMorningRoutine(View view) {
        Intent intent = new Intent(this, MorningRoutineActivity.class);
        startActivity(intent);

    }
}