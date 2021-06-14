package com.hitglynorthz.mybookshelf.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.res.ResourcesCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.hitglynorthz.mybookshelf.MainActivity;
import com.hitglynorthz.mybookshelf.R;
import com.hitglynorthz.mybookshelf.db.Book;
import com.hitglynorthz.mybookshelf.db.DBHelper;
import com.hitglynorthz.mybookshelf.db.Shelf;
import com.hitglynorthz.mybookshelf.db.Shelves;
import com.hitglynorthz.mybookshelf.ui.bottomlevels.Fragment_Shelves;
import com.hitglynorthz.mybookshelf.ui.details.Fragment_More;
import com.hitglynorthz.mybookshelf.ui.toplevels.HomeFragment;
import com.hitglynorthz.mybookshelf.utils.Utils;

import java.util.ArrayList;
import java.util.List;

import static androidx.preference.PreferenceManager.getDefaultSharedPreferences;

public class ShelvesAdapter extends RecyclerView.Adapter<ShelvesAdapter.MyViewHolder> implements Filterable {

    private Context context;
    private List<Shelves> shelvesList;
    private List<Shelves> filterShelvesList;
    private DBHelper db;
    private SharedPreferences prefSettings;
    private ClickListener clickListener;

    private Fragment_Shelves fragment_shelves;

    public interface ClickListener {
        void onItemClick(int position, View v);
    }

    public void setOnItemClickListener(ClickListener clickListener) {
        this.clickListener = clickListener;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        // inicializamos los campos del layout en el que se mostraran los datos
        private MaterialCardView cv_shelves_row;
        private TextView tv_shelves, tv_nbooks;
        private RecyclerView rv_shelf;

        public MyViewHolder(View view) {
            super(view);
            if (clickListener != null) {
                view.setOnClickListener(this);
            }
            // bindeamos estos campos
            cv_shelves_row = view.findViewById(R.id.cv_shelves_row);
            tv_shelves = view.findViewById(R.id.tv_shelves);
            tv_nbooks = view.findViewById(R.id.tv_nbooks);
            rv_shelf = view.findViewById(R.id.rv_shelf);
        }

        @Override
        public void onClick(View v) {
            if (clickListener != null) {
                clickListener.onItemClick(getAdapterPosition(), v);
            }
        }
    }

    public ShelvesAdapter(Context context, List<Shelves> shelvesList, Fragment_Shelves fragment_shelves) {
        this.context = context;
        this.shelvesList = shelvesList;
        this.fragment_shelves = fragment_shelves;
        filterShelvesList = new ArrayList<>(shelvesList);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // seleccionamos el layout en el que se mostraran los datos y el padre en el que se mostrara este
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_shelves_row, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ShelvesAdapter.MyViewHolder holder, final int position) {
        holder.setIsRecyclable(false); // hacemos que no se regeneren los items al usar el searchview
        final Shelves shelves = shelvesList.get(position);
        db = DBHelper.getInstance(context);
        prefSettings = getDefaultSharedPreferences(context);

        int nbooks = db.getCountBooksFromShelvesById(shelves.getId_shelves());
        // lista con los estantes por estanteria
        List<Shelf> shelfList = new ArrayList<>(db.getShelfInShelvesById(shelves.getId_shelves()));
        // cargamos el nombre y el numero de libros que hay en cada estanteria
        holder.tv_shelves.setText(shelves.getShelves_name());
        holder.tv_nbooks.setText(String.format(context.getResources().getString(R.string.adapter_nbooks), nbooks));
        // inicializamos el recyclerview en el que se mostraran los estantes de cada estanteria
        // el layoutmanager se cambia segun los settings
        if(prefSettings.getBoolean("shelves_select", false)) {
            holder.rv_shelf.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
        } else {
            holder.rv_shelf.setLayoutManager(new GridLayoutManager(context, 2, GridLayoutManager.VERTICAL, false));
        }
        ShelfsAdapter shelfsAdapter = new ShelfsAdapter(context, shelfList);
        holder.rv_shelf.setAdapter(shelfsAdapter);
        shelfsAdapter.notifyDataSetChanged();
        // para que funcione el scroll vertical al pulsar en el recyclerview horizontal
        holder.rv_shelf.setNestedScrollingEnabled(false);
        // onClickListener para las estanterias
        holder.cv_shelves_row.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(db.getCountBooksFromShelvesById(shelves.getId_shelves()) > 0) {
                    clickListener.onItemClick(position, v);
                    Bundle bundle = new Bundle();
                    bundle.putInt("itemId", shelves.getId_shelves());
                    bundle.putString("itemType", "shelves");
                    ((MainActivity)context).getMainController().navigate(R.id.action_global_nav_more, bundle);
                } else {
                    showDeleteShelves(shelves, position);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return shelvesList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public Filter getFilter() {
        return shelvesFilter;
    }

    // filtro para el searchview
    private Filter shelvesFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<Shelves> filteredShelves = new ArrayList<>();
            if(constraint == null || constraint.length() == 0) {
                filteredShelves.addAll(filterShelvesList);
            } else {
                String filterPattern = Utils.removeAccents(constraint.toString().toLowerCase().trim());
                for(Shelves shelves : filterShelvesList) {
                    if(Utils.removeAccents(shelves.getShelves_name().toLowerCase()).contains(filterPattern)) {
                        filteredShelves.add(shelves);
                    }
                }
            }
            FilterResults results = new FilterResults();
            results.values = filteredShelves;

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            shelvesList.clear();
            shelvesList.addAll((List) results.values);
            notifyDataSetChanged();
        }
    };

    // dialogo borrar
    private void showDeleteShelves(final Shelves shelves, final int pos) {
        new MaterialAlertDialogBuilder(context, R.style.MaterialDialog)
                .setTitle(context.getString(R.string.dialog_delete_shelves_title))
                .setMessage(String.format(context.getString(R.string.dialog_delete_shelves_text), shelves.getShelves_name()))
                .setPositiveButton(context.getString(R.string.book_details_dialog_delete_positive), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteShelves(shelves, pos);
                        dialog.cancel();
                    }
                })
                .setNegativeButton(R.string.book_details_dialog_delete_negative, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                })
                .show();
    }

    // borrar
    private void deleteShelves(Shelves shelves, int pos) {
        db.deleteShelfByShelves(shelves.getId_shelves());
        db.deleteShelves(shelves.getId_shelves());
        // actualizamos la lista y mostramos aviso de borrado
        shelvesList.remove(pos);
        notifyItemRemoved(pos);
        notifyItemRangeChanged(pos, shelvesList.size());
        notifyDataSetChanged();
        HomeFragment.getInstance().setupUpdateHome(context.getString(R.string.dialog_delete_shelves_deleted));
        fragment_shelves.toggleEmptyBooksShelves(shelvesList);
    }

}
