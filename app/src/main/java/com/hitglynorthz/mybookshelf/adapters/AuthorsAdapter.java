package com.hitglynorthz.mybookshelf.adapters;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.hitglynorthz.mybookshelf.MainActivity;
import com.hitglynorthz.mybookshelf.R;
import com.hitglynorthz.mybookshelf.db.Author;
import com.hitglynorthz.mybookshelf.db.Book;
import com.hitglynorthz.mybookshelf.db.DBHelper;
import com.hitglynorthz.mybookshelf.utils.Utils;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class AuthorsAdapter extends RecyclerView.Adapter<AuthorsAdapter.MyViewHolder> implements Filterable {

    private Context context;
    private List<Author> authorList;
    private List<Author> filterAuthorList;
    private DBHelper db;
    private ClickListener clickListener;

    public interface ClickListener {
        void onItemClick(int position, View v);
    }

    public void setOnItemClickListener(ClickListener clickListener) {
        this.clickListener = clickListener;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        // inicializamos los campos del layout en el que se mostraran los datos
        private MaterialCardView cv_author_row;
        private TextView tv_icon, tv_author, tv_nbooks;

        public MyViewHolder(View view) {
            super(view);
            if (clickListener != null) {
                view.setOnClickListener(this);
            }
            // bindeamos estos campos
            cv_author_row = view.findViewById(R.id.cv_author_row);
            tv_icon = view.findViewById(R.id.tv_icon);
            tv_author = view.findViewById(R.id.tv_author);
            tv_nbooks = view.findViewById(R.id.tv_nbooks);
        }

        @Override
        public void onClick(View v) {
            if (clickListener != null) {
                clickListener.onItemClick(getAdapterPosition(), v);
            }
        }
    }

    public AuthorsAdapter(Context context, List<Author> authorList) {
        this.context = context;
        this.authorList = authorList;
        filterAuthorList = new ArrayList<>(authorList);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // seleccionamos el layout en el que se mostraran los datos y el padre en el que se mostrara este
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_author_row, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(AuthorsAdapter.MyViewHolder holder, final int position) {
        final Author author = authorList.get(position);
        db = DBHelper.getInstance(context);
        // pillamos la primera letra del nombre del autor
        String first = String.valueOf(author.getAuthor_name().charAt(0));
        int nbooks = db.getCountBooksFromAuthorById(author.getId_author());

        holder.tv_icon.setText(first.toUpperCase());
        holder.tv_author.setText(author.getAuthor_name());
        holder.tv_nbooks.setText(String.format(context.getResources().getString(R.string.adapter_nbooks), nbooks));

        holder.cv_author_row.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickListener.onItemClick(position, v);
                Bundle bundle = new Bundle();
                bundle.putInt("itemId", author.getId_author());
                bundle.putString("itemType", "author");
                //Navigation.findNavController(v).navigate(R.id.action_global_nav_more, bundle);
                ((MainActivity)context).getMainController().navigate(R.id.action_global_nav_more, bundle);
            }
        });

    }

    @Override
    public int getItemCount() {
        return authorList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public Filter getFilter() {
        return authorFilter;
    }

    private Filter authorFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<Author> filteredAuthor = new ArrayList<>();
            if(constraint == null || constraint.length() == 0 ) {
                filteredAuthor.addAll(filterAuthorList);
            } else {
                String filterPattern = Utils.removeAccents(constraint.toString().toLowerCase().trim());
                for(Author author : filterAuthorList) {
                    if(Utils.removeAccents(author.getAuthor_name().toLowerCase()).contains(filterPattern)) {
                        filteredAuthor.add(author);
                    }
                }
            }
            FilterResults results = new FilterResults();
            results.values = filteredAuthor;

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            authorList.clear();
            authorList.addAll((List) results.values);
            notifyDataSetChanged();
        }
    };

}
