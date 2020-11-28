package com.example.onTime.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.appcompat.app.AppCompatActivity;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.TextView;

import com.example.onTime.R;
import com.example.onTime.adapters.SelectionMorningRecurrenteAdapter;
import com.example.onTime.modele.MorningRoutine;
import com.example.onTime.modele.Tache;
import com.example.onTime.item_touch_helpers.ItemTouchHelperTache;
import com.example.onTime.adapters.TacheAdapter;
import com.example.onTime.modele.Toolbox;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Fragment qui gère une Morning routie avec toutes ses tâches
 */
public class EditMRFragment extends Fragment {

    private RecyclerView recyclerView; // est la recycler view des tâches
    private RecyclerView.LayoutManager layoutManager;
    private TacheAdapter tacheAdapter; //  gestion de la popup des tâches
    private MorningRoutine laMorningRoutine; // morning routine du framgnet
    private int positionMorningRoutine; // la position de la morning routine dans le MRmanager
    private boolean isMenuShown;
    private Animation showTacheMenu;
    private Animation hideTacheMenu;

    public EditMRFragment() {
        // Required empty public constructor
    }

    public static EditMRFragment newInstance() {
        return new EditMRFragment();
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

        return inflater.inflate(R.layout.fragment_edit_m_r, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        this.recyclerView = view.findViewById(R.id.tache_recyclerview);
        if (this.laMorningRoutine == null)
            this.laMorningRoutine = new MorningRoutine("");


        this.layoutManager = new LinearLayoutManager(getActivity());
        this.recyclerView.setLayoutManager(this.layoutManager);

        this.tacheAdapter = new TacheAdapter(this.laMorningRoutine.getListeTaches());
        this.recyclerView.setAdapter(this.tacheAdapter);

        if (this.laMorningRoutine.getListeTaches().isEmpty()) {
            this.hideRecyclerView();
        } else {
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
        titre.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    titre.clearFocus();
                    EditMRFragment.this.laMorningRoutine.setNom(titre.getText().toString());
                    titre.setCursorVisible(false);
                }

                return false;
            }
        });

        //Enlever le clavier quand on perd le focus
        titre.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    titre.clearFocus();
                    titre.setCursorVisible(false);
                    EditMRFragment.this.laMorningRoutine.setNom(titre.getText().toString());
                }else{
                    titre.setCursorVisible(true);
                }
            }
        });

        Button retour = view.findViewById(R.id.boutton_retour);

        retour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditMRFragment.this.laMorningRoutine.setNom(titre.getText().toString());
                Toolbox.hideSoftKeyboard(v);
                sauvegarder();
                AppCompatActivity activity = (AppCompatActivity) v.getContext();
                activity.getSupportFragmentManager().popBackStack();
            }
        });

        View blur = view.findViewById(R.id.shadowView);
        blur.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditMRFragment.this.hideMenu();
            }
        });
    }

    private void hideRecyclerView() {
        View v = this.getView();
        LinearLayout emptyTaches = v.findViewById(R.id.empty_taches);
        this.recyclerView.setVisibility(View.GONE);
        emptyTaches.setVisibility(View.VISIBLE);
    }

    private void showRecyclerView() {
        View v = this.getView();
        LinearLayout emptyTaches = v.findViewById(R.id.empty_taches);
        this.recyclerView.setVisibility(View.VISIBLE);
        emptyTaches.setVisibility(View.GONE);
    }


    /**
     * Affiche le dialogue pour modifier une tâche
     */
    private void showModfierTacheDialog() {
        LayoutInflater factory = LayoutInflater.from(EditMRFragment.this.getContext());
        final View textEntryView = factory.inflate(R.layout.ajout_tache, null);

        TextInputLayout nomTacheLayout = textEntryView.findViewById(R.id.nomtachecreate);
        final EditText nomTache = nomTacheLayout.getEditText();
        final NumberPicker duree = textEntryView.findViewById(R.id.duree);
        duree.setMinValue(0);
        duree.setMaxValue(60);

        final MaterialAlertDialogBuilder alert = new MaterialAlertDialogBuilder(EditMRFragment.this.getContext());

        alert.setTitle(R.string.creer_nouvelle_tache)
                .setView(textEntryView)
                .setPositiveButton(R.string.sauvegarder,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                Tache t = new Tache(nomTache.getText().toString(), duree.getValue() * 60);
                                ajouterTache(t);
                                EditMRFragment.this.hideMenu();
                            }
                        })
                .setNegativeButton(R.string.annuler,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int whichButton) {
                            }
                        });
        alert.show();
    }

    /**
     * Affiche le menu afin de charger une tâche récurrente ou d'en créer une nouvelle
     */
    private void showMenu() {
        View view = this.getView();
        View blur = view.findViewById(R.id.shadowView);

        FloatingActionButton mainActionButton = view.findViewById(R.id.host_action_tache);

        FloatingActionButton creerTacheActionButton = view.findViewById(R.id.creer_tache);
        CardView creerTacheText = view.findViewById(R.id.creer_tache_texte);

        FloatingActionButton choisirTacheActionButton = view.findViewById(R.id.choisir_tache);
        CardView choisirTacheText = view.findViewById(R.id.choisir_tache_texte);

        mainActionButton.startAnimation(EditMRFragment.this.showTacheMenu);

        blur.setVisibility(View.VISIBLE);
        blur.getBackground().setAlpha(170);
        creerTacheActionButton.show();
        creerTacheText.setVisibility(View.VISIBLE);

        choisirTacheActionButton.show();
        choisirTacheText.setVisibility(View.VISIBLE);

        EditMRFragment.this.isMenuShown = true;
    }

    /**
     * Cache le  menu qui s'ouvre après un plus
     */
    private void hideMenu() {
        View view = this.getView();
        View blur = view.findViewById(R.id.shadowView);

        FloatingActionButton mainActionButton = view.findViewById(R.id.host_action_tache);

        FloatingActionButton creerTacheActionButton = view.findViewById(R.id.creer_tache);
        CardView creerTacheText = view.findViewById(R.id.creer_tache_texte);

        FloatingActionButton choisirTacheActionButton = view.findViewById(R.id.choisir_tache);
        CardView choisirTacheText = view.findViewById(R.id.choisir_tache_texte);

        mainActionButton.startAnimation(EditMRFragment.this.hideTacheMenu);
        //mainActionButton.shrink();

        blur.setVisibility(View.INVISIBLE);
        blur.getBackground().setAlpha(0);
        creerTacheActionButton.hide();
        creerTacheText.setVisibility(View.INVISIBLE);

        choisirTacheActionButton.hide();
        choisirTacheText.setVisibility(View.INVISIBLE);

        EditMRFragment.this.isMenuShown = false;
    }

    /**
     * Iniialise le menu a afficher
     * @param view est la vue dans laquelle le menu sera affiché
     */
    private void initMenu(View view) {
        final FloatingActionButton mainActionButton = view.findViewById(R.id.host_action_tache);
        final FloatingActionButton creerTacheActionButton = view.findViewById(R.id.creer_tache);
        final FloatingActionButton choisir_tacheActionButton = view.findViewById(R.id.choisir_tache);

        this.showTacheMenu = AnimationUtils.loadAnimation(view.getContext(), R.anim.rotate_cw);
        this.hideTacheMenu = AnimationUtils.loadAnimation(view.getContext(), R.anim.rotate_acw);
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
                EditMRFragment.this.hideMenu();
                EditMRFragment.this.showModfierTacheDialog();
            }
        });

        final AlertDialog alertDialog = initialiserAlerte();

        choisir_tacheActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditMRFragment.this.hideMenu();
                alertDialog.show();
            }
        });


    }

    /**
     * Sauvegarde dans les shared pref la morning_routine ainsi que la position de celle-ci
     * dans la liste des MRT du MRManager
     */
    public void sauvegarder() {
        Context context = getActivity().getApplicationContext();
        SharedPreferences sharedPreferences = context.getSharedPreferences("onTimePreferences", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String jsonMorningRoutine = gson.toJson(this.laMorningRoutine);

        editor.putString("morning_routine", jsonMorningRoutine);
        editor.putInt("position", positionMorningRoutine);
        editor.apply();
    }

    /**
     * Méthoed qui permet d'ajouter une tâche à la morning routine
     * @param tache est la tâche à ajouter
     */
    public void ajouterTache(Tache tache) {
        laMorningRoutine.ajouterTache(tache);
        tacheAdapter.notifyItemInserted(laMorningRoutine.getListeTaches().size() - 1);

        if (EditMRFragment.this.laMorningRoutine.getListeTaches().size() == 1) {
            EditMRFragment.this.showRecyclerView();
        }
    }

    /**
     * Méthode qui récup_re les taches récurentes dans les sharedPref
     * @return la liste des tâches récurrentes
     */
    private List<Tache> recuperTachesRec(){
        Context context = this.getActivity().getApplicationContext();
        SharedPreferences sharedPreferences = context.getSharedPreferences("onTimePreferences", Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString("listeTachesRec", "");
        List<Tache> tachesRec = new ArrayList<>();

        if (!json.equals("")) {
            Type type = new TypeToken<List<Tache>>() {
            }.getType();
            tachesRec = gson.fromJson(json, type);
        }

        return tachesRec;
    }

    /**
     * Prépare le dialogue pour choisir une tâche récurrente
     * @return l'alertDialog créé
     */
    private AlertDialog initialiserAlerte(){
        final MaterialAlertDialogBuilder alert = new MaterialAlertDialogBuilder(EditMRFragment.this.getContext());
        final SelectionMorningRecurrenteAdapter adapter = new SelectionMorningRecurrenteAdapter(EditMRFragment.this.getContext(), recuperTachesRec(), EditMRFragment.this);

        alert.setTitle(R.string.choisir_tache_rec)

                .setAdapter(adapter, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        adapter.choisirTache(which);
                        dialog.dismiss();
                    }

                });

        return alert.create();
    }
}