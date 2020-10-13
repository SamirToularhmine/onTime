package com.example.onTime;


import android.app.Activity;
import android.app.ListActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.onTime.modele.Adresse;
import com.example.onTime.modele.MRA;
import com.example.onTime.modele.MRManager;
import com.example.onTime.modele.MorningRoutine;
import com.example.onTime.modele.Toolbox;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    private GridView gridView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.gridView = (GridView) findViewById(R.id.gridView);

        //
        MorningRoutine mr1 = new MorningRoutine("mr1");
        MorningRoutine mr2 = new MorningRoutine("mr2");
        MorningRoutine mr3 = new MorningRoutine("m3");
        Adresse a1 = new Adresse("adresse1", "d1","a1");
        Adresse a2 = new Adresse("adresse2", "d2","a2");
        Adresse a3 = new Adresse("adresse3", "d3","a3");

        final MRA mra1 = new MRA(mr1, a1);
        MRA mra2 = new MRA(mr2, a2);
        MRA mra3 = new MRA(mr3, a3);

        List<MRA> mras = new ArrayList<>();

        mras.add(mra1);
        mras.add(mra2);
        mras.add(mra3);


        // android.R.layout.simple_list_item_1 is a constant predefined layout of Android.
        // used to create a GridView with simple GridItem (Only one TextView).

        ArrayAdapter<MRA> arrayAdapter
                = new ArrayAdapter<MRA>(this, android.R.layout.simple_list_item_1, mras);


        gridView.setAdapter(arrayAdapter);

        // When the user clicks on the GridItem
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> a, View v, int position, long id) {
                Object o = gridView.getItemAtPosition(position);
                MRA mra = (MRA) o;
                Toast.makeText(MainActivity.this, "Selected :" + " " + mra.toString(),
                        Toast.LENGTH_LONG).show();
            }
        });
    }

}
