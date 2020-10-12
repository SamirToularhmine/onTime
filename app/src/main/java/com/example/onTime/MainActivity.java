package com.example.onTime;


import android.app.ListActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.onTime.modele.MRA;
import com.example.onTime.modele.MRManager;
import com.example.onTime.modele.MorningRoutine;
import com.example.onTime.modele.Toolbox;


public class MainActivity extends ListActivity {

    private MRManager mrManager;

    private TextView text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mrManager = new MRManager();
        MorningRoutine m = new MorningRoutine("test1", null);
        MorningRoutine m2 = new MorningRoutine("test2", null);
        MRA mra1 = new MRA(m);
        MRA mra2 = new MRA(m2);
        mrManager.ajouterMRA(mra1);
        mrManager.ajouterMRA(mra2);

        text = findViewById(R.id.mainText);


        // initiate the listadapter
        ArrayAdapter<MRA> myAdapter = new ArrayAdapter <>(this,
                R.layout.row_layout, R.id.listText, mrManager.getListMRA());

        // assign the list adapter
        setListAdapter(myAdapter);

        int heure = Toolbox.getHourFromSecondes(this.mrManager.getHeureArrivee());
        int minute = Toolbox.getMinutesFromSecondes(this.mrManager.getHeureArrivee());
        text.setText(Toolbox.formaterHeure(heure, minute));

    }

    // when an item of the list is clicked
    @Override
    protected void onListItemClick(ListView list, View view, int position, long id) {
        super.onListItemClick(list, view, position, id);

        //String selectedItem = (String) getListView().getItemAtPosition(position);
        //MRA selectedItem =  (MRA) getListAdapter().getItem(position);

        //text.setText("You clicked " + selectedItem.getMorningRoutine().getNom() + " at position " + position);

    }
}