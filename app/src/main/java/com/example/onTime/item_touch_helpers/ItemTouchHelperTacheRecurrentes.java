package com.example.onTime.item_touch_helpers;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.example.onTime.R;
import com.example.onTime.adapters.TacheAdapter;
import com.example.onTime.adapters.TachesRecurrentesAdapter;
import com.example.onTime.fragments.EditMRFragment;
import com.example.onTime.modele.Tache;
import com.google.android.material.snackbar.Snackbar;

import java.util.Collections;


/**
 * Drag & drop + swipe sur une Tache récurrente
 */
public class ItemTouchHelperTacheRecurrentes extends ItemTouchHelper.SimpleCallback {


    private TachesRecurrentesAdapter tachesRecurrentesAdapter;
    private int positionSuppr;
    private Tache tacheSuppr;
    private final Drawable icon;
    final ColorDrawable background = new ColorDrawable(Color.parseColor("#CA4242"));


    public ItemTouchHelperTacheRecurrentes(Context context, TachesRecurrentesAdapter tachesRecurrentesAdapter) {
        super(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT);
        this.tachesRecurrentesAdapter = tachesRecurrentesAdapter;
        icon = ContextCompat.getDrawable(context, R.drawable.ic_delete_24px);
    }

    @Override
    public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
        int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
        int swipeFlags = ItemTouchHelper.START | ItemTouchHelper.END;
        return makeMovementFlags(dragFlags, swipeFlags);
    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        Collections.swap(tachesRecurrentesAdapter.getList(), viewHolder.getAdapterPosition(), target.getAdapterPosition());
        tachesRecurrentesAdapter.notifyItemMoved(viewHolder.getAdapterPosition(), target.getAdapterPosition());
        return true;
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        int position = viewHolder.getAdapterPosition();
        this.positionSuppr = position;
        this.tacheSuppr = tachesRecurrentesAdapter.getList().get(position);
        tachesRecurrentesAdapter.getList().remove(position);
        tachesRecurrentesAdapter.notifyItemRemoved(position);
        showUndoSnackbar(viewHolder);
    }

    private void showUndoSnackbar(RecyclerView.ViewHolder viewHolder) {
        Snackbar snackbar = Snackbar.make(viewHolder.itemView, "Suppression de " + tacheSuppr.getNom(), Snackbar.LENGTH_SHORT);
        snackbar.setAction("Annuler", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tachesRecurrentesAdapter.getList().add(positionSuppr, tacheSuppr);
                tachesRecurrentesAdapter.notifyItemInserted(positionSuppr);
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
                }else{
                    iconLeft = itemView.getLeft() + (int) dX - icon.getIntrinsicWidth();
                    iconRight = itemView.getLeft() + (int) dX;
                }
                icon.setBounds(iconLeft, iconTop, iconRight, iconBottom);
                background.setBounds(itemView.getLeft(), itemView.getTop(), itemView.getLeft() + ((int) dX), itemView.getBottom());
            }else{ // Swiping to the left
                if(dX * -1 > icon.getIntrinsicWidth()) {
                    iconLeft = itemView.getRight() - icon.getIntrinsicWidth();
                    iconRight = itemView.getRight();
                }else{
                    iconLeft = itemView.getRight() + (int) dX; // ajouter iconMaring si necessaire
                    iconRight = itemView.getRight() + (int) dX + icon.getIntrinsicWidth();
                }
                icon.setBounds(iconLeft, iconTop, iconRight, iconBottom);
                background.setBounds(itemView.getRight() + (int) dX, itemView.getTop(), itemView.getRight(), itemView.getBottom());
            }
            background.draw(c);
            icon.draw(c);
        }
    }
}



