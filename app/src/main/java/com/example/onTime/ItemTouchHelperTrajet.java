package com.example.onTime;

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
import com.example.onTime.TrajetAdapter;
import com.example.onTime.modele.MRT;
import com.example.onTime.modele.Trajet;
import com.example.onTime.mrt.MorningRoutineAdressAdapter;
import com.google.android.material.snackbar.Snackbar;

import java.util.Collections;

public class ItemTouchHelperTrajet extends ItemTouchHelper.SimpleCallback {


    private TrajetAdapter trajetAdapter;
    private int positionSuppr;
    private Trajet trajet;
    private final Drawable icon;
    final ColorDrawable background = new ColorDrawable(Color.parseColor("#CA4242"));

    public ItemTouchHelperTrajet(Context context, TrajetAdapter trajetAdapter) {
        super(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT);
        this.trajetAdapter = trajetAdapter;
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
        Collections.swap(trajetAdapter.getList(), viewHolder.getAdapterPosition(), target.getAdapterPosition());
        trajetAdapter.notifyItemMoved(viewHolder.getAdapterPosition(), target.getAdapterPosition());
        return true;
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        int position = viewHolder.getAdapterPosition();
        this.positionSuppr = position;
        this.trajet = trajetAdapter.getList().get(position);
        trajetAdapter.getList().remove(position);
        trajetAdapter.notifyItemRemoved(position);
        showUndoSnackbar(viewHolder);
    }

    private void showUndoSnackbar(RecyclerView.ViewHolder viewHolder) {
        Snackbar snackbar = Snackbar.make(viewHolder.itemView, "Suppression de " + trajet.getNom(), Snackbar.LENGTH_SHORT);
        snackbar.setAction("Annuler", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                trajetAdapter.getList().add(positionSuppr, trajet);
                trajetAdapter.notifyItemInserted(positionSuppr);
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