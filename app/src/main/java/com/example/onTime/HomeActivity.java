package com.example.onTime;

import androidx.appcompat.app.AppCompatActivity;

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

        List<MRA> mras = getListData();
        this.mrManager = new MRManager(39600, mras);

        this.listView = findViewById(R.id.morningRoutinesList);
        this.listView.setAdapter(new CustomGridAdapter(this, mras));

        TextView heureReveil = findViewById(R.id.heureReveil);
        if(heureReveil != null){
            heureReveil.setText(Toolbox.formaterHeure(Toolbox.getHourFromSecondes(this.mrManager.getHeureArrivee()), Toolbox.getMinutesFromSecondes(this.mrManager.getHeureArrivee())));
        }
    }

    private List<MRA> getListData() {
        MorningRoutine mr1 = new MorningRoutine("mr1");
        MorningRoutine mr2 = new MorningRoutine("mr2");
        MorningRoutine mr3 = new MorningRoutine("m3");
        Adresse a1 = new Adresse("adresse1", "d1","a1");
        Adresse a2 = new Adresse("adresse2", "d2","a2");
        Adresse a3 = new Adresse("adresse3", "d3","a3");

        MRA mra1 = new MRA(mr1, null);
        MRA mra2 = new MRA(mr2, a2);
        MRA mra3 = new MRA(mr3, a3);

        List<MRA> mras = new ArrayList<>();

        mras.add(mra1);
        mras.add(mra2);
        mras.add(mra3);

        return mras;
    }
}