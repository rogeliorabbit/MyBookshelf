package com.hitglynorthz.mybookshelf.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.hitglynorthz.mybookshelf.R;
import com.hitglynorthz.mybookshelf.db.DBHelper;
import com.hitglynorthz.mybookshelf.db.Wishlist;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class WishlistAdapter extends RecyclerView.Adapter<WishlistAdapter.MyViewHolder> {

    private Context context;
    private List<Wishlist> wishlistList;
    private DBHelper db;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        // inicializamos los campos del layout en el que se mostraran los datos
        private MaterialCardView cv_wishlist_row;
        private TextView tv_title, tv_author, tv_publisher, tv_isbn;

        public MyViewHolder(View view) {
            super(view);
            // bindeamos estos campos
            cv_wishlist_row = view.findViewById(R.id.cv_wislist_row);
            tv_title = view.findViewById(R.id.tv_title);
            tv_author = view.findViewById(R.id.tv_author);
            tv_publisher = view.findViewById(R.id.tv_publisher);
            tv_isbn = view.findViewById(R.id.tv_isbn);
        }
    }

    public WishlistAdapter(Context context, List<Wishlist> wishlistList) {
        this.context = context;
        this.wishlistList = wishlistList;
    }

    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_wishlist_row, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        final Wishlist wishlist = wishlistList.get(position);
        db = DBHelper.getInstance(context);

        // datos
        holder.tv_title.setText(wishlist.getTitle());
        holder.tv_author.setText(wishlist.getAuthor());
        holder.tv_publisher.setText(wishlist.getPublisher());
        holder.tv_isbn.setText(wishlist.getIsbn());

        // onClick
        holder.cv_wishlist_row.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.cv_wishlist_row.setChecked(!holder.cv_wishlist_row.isChecked());
                showMenuDialog(holder, wishlist, position, v);
            }
        });

        // onLongClick
        holder.cv_wishlist_row.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                holder.cv_wishlist_row.setChecked(!holder.cv_wishlist_row.isChecked());
                deleteDialog(holder, wishlist, position);
                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return wishlistList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    // dialogo principal
    private void showMenuDialog(final MyViewHolder holder, final Wishlist wishlist, final int pos, final View view) {
        MaterialAlertDialogBuilder selectAdd = new MaterialAlertDialogBuilder(context, R.style.MaterialDialog);
        CharSequence[] choices = {context.getString(R.string.wishlist_dialog_menu_op1), context.getString(R.string.wishlist_dialog_menu_op2), context.getString(R.string.wishlist_dialog_menu_op3)};
        selectAdd.setItems(choices, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch(which) {
                    case 0:
                        Bundle bundleAdd = new Bundle();
                        bundleAdd.putInt("wishlist_id", wishlist.getId_wishlist());
                        bundleAdd.putString("add_from_wishlist", "add_from_wishlist");
                        Navigation.findNavController(view).navigate(R.id.action_global_nav_add_book, bundleAdd, null, null);
                        dialog.dismiss();
                        break;
                    case 1:
                        Bundle bundleEdit = new Bundle();
                        bundleEdit.putInt("wishlist_id", wishlist.getId_wishlist());
                        bundleEdit.putString("edit_wishlist", "edit");
                        Navigation.findNavController(view).navigate(R.id.action_global_nav_add_wishlist, bundleEdit, null, null);
                        dialog.dismiss();
                        break;
                    case 2:
                        deleteDialog(holder, wishlist, pos);
                        dialog.dismiss();
                        break;
                }
            }
        });
        selectAdd.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                holder.cv_wishlist_row.setChecked(!holder.cv_wishlist_row.isChecked());
            }
        });
        selectAdd.show();
    }

    // dialogo borrar
    private void deleteDialog(final MyViewHolder holder, final Wishlist wishlist, final int pos) {
        new MaterialAlertDialogBuilder(context, R.style.MaterialDialog)
                .setTitle(context.getString(R.string.wishlist_dialog_delete_title))
                .setMessage(context.getString(R.string.wishlist_dialog_delete_text))
                .setPositiveButton(context.getString(R.string.wishlist_dialog_delete_positive), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteWishlist(wishlist, pos);
                        dialog.cancel();
                    }
                })
                .setNegativeButton(R.string.wishlist_dialog_delete_negative, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                })
                .setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        holder.cv_wishlist_row.setChecked(!holder.cv_wishlist_row.isChecked());
                    }
                })
                .show();
    }

    // borrar
    private void deleteWishlist(Wishlist wishlist, int pos) {
        db.deleteWishlist(wishlist.getId_wishlist());
        wishlistList.remove(pos);
        notifyItemRemoved(pos);
        notifyItemRangeChanged(pos, wishlistList.size());
    }

}
