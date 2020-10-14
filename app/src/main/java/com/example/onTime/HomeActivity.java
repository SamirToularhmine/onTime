package com.example.onTime;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.List;

public class HomeActivity extends AppCompatActivity {

    private static final String[] listeMR = new String[]{
            "Oranges",
            "Tomates",
            "Raisin",
            "Pain",
            "Banane",
            "Kiwi",
            "Pates",
            "Raviolis",
            "Fraises",
            "Glace",
            "Pizza",
            "Yaourts",
            "Riz",
            "Haricots"
    };

    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        this.listView = (ListView) findViewById(R.id.morningRoutinesList);

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, listeMR);

        listView.setAdapter(arrayAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String itemValue = (String) listView.getItemAtPosition(i);
            }
        });
    }
}