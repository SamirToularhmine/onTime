package com.example.onTime;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.example.onTime.modele.Tache;
import com.google.android.material.snackbar.Snackbar;

import java.util.Collections;

public class ItemTouchHelperTache extends ItemTouchHelper.SimpleCallback {


    private TacheAdapter tacheAdapter;
    private int positionSuppr;
    private Tache tacheSuppr;
    private final Drawable icon;
    final ColorDrawable background = new ColorDrawable(Color.RED);


    public ItemTouchHelperTache(Context context, TacheAdapter tacheAdapter) {
        super(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT);
        this.tacheAdapter = tacheAdapter;
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
        Collections.swap(tacheAdapter.getListTache(), viewHolder.getAdapterPosition(), target.getAdapterPosition());
        tacheAdapter.notifyItemMoved(viewHolder.getAdapterPosition(), target.getAdapterPosition());
        return true;
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        int position = viewHolder.getAdapterPosition();
        this.positionSuppr = position;
        this.tacheSuppr = tacheAdapter.getListTache().get(position);
        tacheAdapter.getListTache().remove(position);
        tacheAdapter.notifyItemRemoved(position);
        showUndoSnackbar(viewHolder);
    }

    private void showUndoSnackbar(RecyclerView.ViewHolder viewHolder) {
        Snackbar snackbar = Snackbar.make(viewHolder.itemView, "Suppression de " + tacheSuppr.getNom(), Snackbar.LENGTH_SHORT);
        snackbar.setAction("Annuler", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tacheAdapter.getListTache().add(positionSuppr, tacheSuppr);
                tacheAdapter.notifyItemInserted(positionSuppr);
            }
        });
        snackbar.show();
    }

    @Override
    public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
            View itemView = viewHolder.itemView;

            int iconMargin = (itemView.getHeight() - icon.getIntrinsicHeight()) / 2;
            int iconTop = itemView.getTop() + (itemView.getHeight() - icon.getIntrinsicHeight()) / 2;
            int iconBottom = iconTop + icon.getIntrinsicHeight();

            if (dX > 0) { // Swiping to the right
                int iconLeft = itemView.getLeft() + (int) dX - icon.getIntrinsicWidth();
                int iconRight = itemView.getLeft() + (int) dX;
                icon.setBounds(iconLeft, iconTop, iconRight, iconBottom);

                background.setBounds(itemView.getLeft(), itemView.getTop(), itemView.getLeft() + ((int) dX), itemView.getBottom());
            } else if (dX < 0) { // Swiping to the left
                int iconLeft = itemView.getRight() + (int) dX ; // ajouter inconMaring si necessaire
                int iconRight = itemView.getRight() + (int) dX + icon.getIntrinsicWidth();
                icon.setBounds(iconLeft, iconTop, iconRight, iconBottom);

                background.setBounds(itemView.getRight() + (int) dX , itemView.getTop(), itemView.getRight(), itemView.getBottom());

            }

            background.draw(c);
            icon.draw(c);
        }
    }
}

