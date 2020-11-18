package com.example.onTime.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.TextView;

import com.example.onTime.R;
import com.example.onTime.modele.Adresse;
import com.example.onTime.modele.MRA;
import com.example.onTime.modele.MRManager;
import com.example.onTime.modele.MorningRoutine;
import com.example.onTime.modele.Tache;
import com.example.onTime.morning_routine.ItemTouchHelperTache;
import com.example.onTime.morning_routine.TacheAdapter;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;

public class EditMRFragment extends Fragment {

    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private TacheAdapter tacheAdapter;
    private MorningRoutine laMorningRoutine;
    private int positionMorningRoutine;
    private boolean isMenuShown;
    private Animation showTacheMenu;
    private Animation hideTacheMenu;

    public EditMRFragment() {
        // Required empty public constructor
    }

    public static EditMRFragment newInstance() {
        EditMRFragment fragment = new EditMRFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        this.laMorningRoutine = (MorningRoutine) getArguments().get("morning_routine");
        this.positionMorningRoutine = getArguments().getInt("position");
        sauvegarder();
        return inflater.inflate(R.layout.fragment_edit_m_r, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        this.recyclerView = view.findViewById(R.id.tache_recyclerview);
        if (this.laMorningRoutine == null)
            this.laMorningRoutine = new MorningRoutine("Premiere morning routine");


        this.layoutManager = new LinearLayoutManager(getActivity());
        this.recyclerView.setLayoutManager(this.layoutManager);

        this.tacheAdapter = new TacheAdapter(this.laMorningRoutine.getListeTaches(), this);
        this.recyclerView.setAdapter(this.tacheAdapter);

        if(this.laMorningRoutine.getListeTaches().isEmpty()){
            this.hideRecyclerView();
        }else{
            this.showRecyclerView();
        }

        // drag and drop + swipe
        ItemTouchHelperTache itemTouchHelperTache = new ItemTouchHelperTache(getActivity(), this.tacheAdapter, this);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(itemTouchHelperTache);
        itemTouchHelper.attachToRecyclerView(recyclerView);

        this.initMenu(view);

        final EditText titre = view.findViewById(R.id.titreMorningRoutine);
        titre.setText(this.laMorningRoutine.getNom());

        //Perdre le focus lorsqu'on clique sur ok lors du changement de titre
        titre.setOnEditorActionListener(new TextView.OnEditorActionListener(){
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId == EditorInfo.IME_ACTION_DONE){
                    //Clear focus here from edittext
                    titre.clearFocus();
                    EditMRFragment.this.laMorningRoutine.setNom(titre.getText().toString());
                    sauvegarder();
                }
                return false;
            }
        });

        //Enlever le clavier quand on perd le focus
        titre.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    EditMRFragment.this.laMorningRoutine.setNom(titre.getText().toString());
                    sauvegarder();
                }
            }
        });
    }

    private void hideRecyclerView(){
        View v = this.getView();

        LinearLayout emptyTaches = v.findViewById(R.id.empty_taches);

        this.recyclerView.setVisibility(View.GONE);
        emptyTaches.setVisibility(View.VISIBLE);
    }

    private void showRecyclerView(){
        View v = this.getView();

        LinearLayout emptyTaches = v.findViewById(R.id.empty_taches);

        this.recyclerView.setVisibility(View.VISIBLE);
        emptyTaches.setVisibility(View.GONE);
    }

    private void showModfierTacheDialog(){
        View v = this.getView();

        LayoutInflater factory = LayoutInflater.from(EditMRFragment.this.getContext());
        final View textEntryView = factory.inflate(R.layout.ajout_tache, null);

        TextInputLayout nomTacheLayout = textEntryView.findViewById(R.id.nomtachecreate);
        final EditText nomTache = nomTacheLayout.getEditText();
        final NumberPicker duree = textEntryView.findViewById(R.id.duree);
        duree.setMinValue(0);
        duree.setMaxValue(60);

        final MaterialAlertDialogBuilder alert = new MaterialAlertDialogBuilder(EditMRFragment.this.getContext());

        alert.setTitle("Créer une nouvelle tache :")
                .setView(textEntryView)
                .setPositiveButton("Sauvegarder",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                Tache t = new Tache(nomTache.getText().toString(),duree.getValue()*60);
                                laMorningRoutine.ajouterTache(t);
                                tacheAdapter.notifyDataSetChanged();
                                if(EditMRFragment.this.laMorningRoutine.getListeTaches().size() == 1){
                                    EditMRFragment.this.showRecyclerView();
                                }
                                EditMRFragment.this.sauvegarder();
                                EditMRFragment.this.hideMenu();
                            }
                        })
                .setNegativeButton("Annuler",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int whichButton) {
                            }
                        });
        alert.show();
    }

    private void showMenu(){
        View view = this.getView();

        FloatingActionButton mainActionButton = view.findViewById(R.id.host_action_tache);

        FloatingActionButton creerTacheActionButton = view.findViewById(R.id.creer_tache);
        TextView creerTacheText = view.findViewById(R.id.creer_tache_texte);

        FloatingActionButton choisirTacheActionButton = view.findViewById(R.id.choisir_tache);
        TextView choisirTacheText = view.findViewById(R.id.choisir_tache_texte);

        mainActionButton.startAnimation(EditMRFragment.this.showTacheMenu);

        creerTacheActionButton.show();
        creerTacheText.setVisibility(View.VISIBLE);

        choisirTacheActionButton.show();
        choisirTacheText.setVisibility(View.VISIBLE);

        EditMRFragment.this.isMenuShown = true;
    }

    private void hideMenu(){
        View view = this.getView();

        FloatingActionButton mainActionButton = view.findViewById(R.id.host_action_tache);

        FloatingActionButton creerTacheActionButton = view.findViewById(R.id.creer_tache);
        TextView creerTacheText = view.findViewById(R.id.creer_tache_texte);

        FloatingActionButton choisirTacheActionButton = view.findViewById(R.id.choisir_tache);
        TextView choisirTacheText = view.findViewById(R.id.choisir_tache_texte);

        mainActionButton.startAnimation(EditMRFragment.this.hideTacheMenu);
        //mainActionButton.shrink();

        creerTacheActionButton.hide();
        creerTacheText.setVisibility(View.INVISIBLE);

        choisirTacheActionButton.hide();
        choisirTacheText.setVisibility(View.INVISIBLE);

        EditMRFragment.this.isMenuShown = false;
    }

    private void initMenu(View view){
        //final ExtendedFloatingActionButton mainActionButton = view.findViewById(R.id.host_action_tache);
        final FloatingActionButton mainActionButton = view.findViewById(R.id.host_action_tache);

        final  FloatingActionButton creerTacheActionButton = view.findViewById(R.id.creer_tache);
        final  TextView creerTacheText = view.findViewById(R.id.creer_tache_texte);

        final  FloatingActionButton choisirTacheActionButton = view.findViewById(R.id.choisir_tache);
        final  TextView choisirTacheText = view.findViewById(R.id.choisir_tache_texte);

        this.showTacheMenu = AnimationUtils.loadAnimation(view.getContext(), R.anim.rotate_cw);
        this.hideTacheMenu = AnimationUtils.loadAnimation(view.getContext(), R.anim.rotate_acw);

        //mainActionButton.shrink();

        this.isMenuShown = false;

        mainActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!EditMRFragment.this.isMenuShown) {
                    EditMRFragment.this.showMenu();
                } else {
                    EditMRFragment.this.hideMenu();
                }
            }
        });

        creerTacheActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditMRFragment.this.showModfierTacheDialog();
            }
        });
    }

    public void sauvegarder() {
        Context context = getActivity().getApplicationContext();
        SharedPreferences sharedPreferences = context.getSharedPreferences("onTimePreferences", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String jsonMorningRoutine = gson.toJson(this.laMorningRoutine);

        if (positionMorningRoutine == sharedPreferences.getInt("CurrentMRAPosition", -3)){
            String json = sharedPreferences.getString("MRManager", "");
            MRManager mrManager = gson.fromJson(json, MRManager.class);

            Adresse a = mrManager.getListMRA().get(positionMorningRoutine).getAdresse();

            MRA mra = new MRA(this.laMorningRoutine, a);


            editor.putString("CurrentMRA", gson.toJson(mra));
        }

        editor.putString("morning_routine", jsonMorningRoutine);
        editor.putInt("position", positionMorningRoutine);
        editor.apply();
    }

    @Override
    public void onStop() {
        //this.sauvegarder();
        super.onStop();
    }

}