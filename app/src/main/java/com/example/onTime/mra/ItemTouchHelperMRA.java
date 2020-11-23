package com.example.onTime.mra;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.example.onTime.R;
import com.example.onTime.fragments.ListMRFragment;
import com.example.onTime.modele.MRA;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;

import java.util.Collections;

public class ItemTouchHelperMRA extends ItemTouchHelper.SimpleCallback {


    private MorningRoutineAdressAdapter morningRoutineAdressAdapter;
    private int positionSuppr, idCurrentMRA;
    private MRA supprMRA;
    private final Drawable icon;
    private ListMRFragment listMRFragment;
    private Context context ;
    private boolean wasCurrent;
    final ColorDrawable background = new ColorDrawable(Color.parseColor("#CA4242"));

    public ItemTouchHelperMRA(Context context, MorningRoutineAdressAdapter morningRoutineAdressAdapter, ListMRFragment listMRFragment) {
        super(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT);
        this.morningRoutineAdressAdapter = morningRoutineAdressAdapter;
        icon = ContextCompat.getDrawable(context, R.drawable.ic_delete_24px);
        this.listMRFragment = listMRFragment;
        this.context = context;
        this.wasCurrent = false;
    }

    @Override
    public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
        int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
        int swipeFlags = ItemTouchHelper.START | ItemTouchHelper.END;
        return makeMovementFlags(dragFlags, swipeFlags);
    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        Collections.swap(morningRoutineAdressAdapter.getList(), viewHolder.getAdapterPosition(), target.getAdapterPosition());
        morningRoutineAdressAdapter.notifyItemMoved(viewHolder.getAdapterPosition(), target.getAdapterPosition());
        this.listMRFragment.sauvegarder();
        return true;
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        this.wasCurrent = false;
        int position = viewHolder.getAdapterPosition();
        this.positionSuppr = position;
        this.supprMRA = morningRoutineAdressAdapter.getList().get(position);
        morningRoutineAdressAdapter.getList().remove(position);
        morningRoutineAdressAdapter.notifyItemRemoved(position);
        this.listMRFragment.sauvegarder();
        SharedPreferences sharedPreferences = context.getSharedPreferences("onTimePreferences", Context.MODE_PRIVATE);
        int idCurrentMRA = sharedPreferences.getInt("current_id_MRA", -2);
        if (this.supprMRA.getId() == idCurrentMRA){
            wasCurrent = true;
            this.idCurrentMRA = idCurrentMRA;
            sharedPreferences.edit()
                    .remove("current_id_MRA") // changer le current position lors d'un swipe d'un élément
                    .apply();
        }
        showUndoSnackbar(viewHolder);
    }

    private void showUndoSnackbar(RecyclerView.ViewHolder viewHolder) {
        Snackbar snackbar = Snackbar.make(viewHolder.itemView, "Suppression de " + supprMRA.getMorningRoutine().getNom(), Snackbar.LENGTH_SHORT);
        snackbar.setAction("Annuler", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                morningRoutineAdressAdapter.getList().add(positionSuppr, supprMRA);
                morningRoutineAdressAdapter.notifyItemInserted(positionSuppr);
                ItemTouchHelperMRA.this.listMRFragment.sauvegarder();
                if (wasCurrent){
                    SharedPreferences sharedPreferences = context.getSharedPreferences("onTimePreferences", Context.MODE_PRIVATE);
                    sharedPreferences.edit()
                            .putInt("current_id_MRA", supprMRA.getId())
                            .apply();
                }
            }
        });
        snackbar.show();
    }


    @Override
    public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
            View itemView = viewHolder.itemView;

            int iconTop = itemView.getTop() + (itemView.getHeight() - icon.getIntrinsicHeight()) / 2;
            int iconBottom = iconTop + icon.getIntrinsicHeight();

            int iconLeft, iconRight;

            if (dX > 0) { // Swiping to the right
                if (dX > icon.getIntrinsicWidth()) {
                    iconLeft = itemView.getLeft();
                    iconRight = itemView.getLeft() + icon.getIntrinsicWidth();
                } else {
                    iconLeft = itemView.getLeft() + (int) dX - icon.getIntrinsicWidth();
                    iconRight = itemView.getLeft() + (int) dX;
                }


                icon.setBounds(iconLeft, iconTop, iconRight, iconBottom);

                background.setBounds(itemView.getLeft(), itemView.getTop() + 25, itemView.getLeft() + ((int) dX), itemView.getBottom() - 25);
            } else { // Swiping to the left
                if (dX * -1 > icon.getIntrinsicWidth()) {
                    iconLeft = itemView.getRight() - icon.getIntrinsicWidth();
                    iconRight = itemView.getRight();
                } else {
                    iconLeft = itemView.getRight() + (int) dX; // ajouter iconMaring si necessaire
                    iconRight = itemView.getRight() + (int) dX + icon.getIntrinsicWidth();
                }
                icon.setBounds(iconLeft, iconTop, iconRight, iconBottom);



                background.setBounds(itemView.getRight() + (int) dX, itemView.getTop() + 25, itemView.getRight(), itemView.getBottom() - 25);

            }

            background.draw(c);
            icon.draw(c);
        }


    }
}



