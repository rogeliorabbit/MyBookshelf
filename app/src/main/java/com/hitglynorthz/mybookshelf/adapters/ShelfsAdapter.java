package com.hitglynorthz.mybookshelf.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.core.content.res.ResourcesCompat;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.chip.Chip;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.hitglynorthz.mybookshelf.MainActivity;
import com.hitglynorthz.mybookshelf.R;
import com.hitglynorthz.mybookshelf.db.DBHelper;
import com.hitglynorthz.mybookshelf.db.Shelf;
import com.hitglynorthz.mybookshelf.db.Shelves;

import java.util.List;

public class ShelfsAdapter extends RecyclerView.Adapter<ShelfsAdapter.MyViewHolder> {

    private Context context;
    private List<Shelf> shelfList;
    private DBHelper db;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        // inicializamos los campos del layout en el que se mostraran los datos
        private Chip chipOnly;

        public MyViewHolder(View view) {
            super(view);
            chipOnly = view.findViewById(R.id.chipOnly);
        }
    }

    public ShelfsAdapter(Context context, List<Shelf> shelfList) {
        this.context = context;
        this.shelfList = shelfList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // seleccionamos el layout en el que se mostraran los datos y el padre en el que se mostrara este
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chip_row, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ShelfsAdapter.MyViewHolder holder, int position) {
        holder.setIsRecyclable(false); // hacemos que no se regeneren los items al usar el searchview
        final Shelf shelf = shelfList.get(position);
        db = DBHelper.getInstance(context);
        // cargamos el nombre de cada estante
        holder.chipOnly.setText(shelf.getShelf_name());
        holder.chipOnly.setChipIcon(ResourcesCompat.getDrawable(context.getResources(), R.drawable.ic_shelf_white, null));
        // onClickListener para cada estante
        holder.chipOnly.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(db.getCountBooksFromShelfById(shelf.getId_shelf()) > 0) {
                    Bundle bundle = new Bundle();
                    bundle.putInt("itemId", shelf.getId_shelf());
                    bundle.putString("itemType", "shelf");
                    bundle.putInt("itemShelvesId", shelf.getId_shelves());
                    //Navigation.findNavController(v).navigate(R.id.action_global_nav_more, bundle);
                    ((MainActivity)context).getMainController().navigate(R.id.action_global_nav_more, bundle);
                } else {
                    FloatingActionButton fab = ((MainActivity) context).findViewById(R.id.main_fab);
                    Snackbar.make(v, context.getResources().getString(R.string.recycler_no_books_in_shelf), Snackbar.LENGTH_SHORT).setAnchorView(fab).show();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return shelfList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

}
