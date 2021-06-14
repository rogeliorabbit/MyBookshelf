package com.hitglynorthz.mybookshelf.adapters;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.hitglynorthz.mybookshelf.R;
import com.hitglynorthz.mybookshelf.db.Book;
import com.hitglynorthz.mybookshelf.db.DBHelper;

import java.util.ArrayList;
import java.util.List;

public class GenresAdapter extends RecyclerView.Adapter<GenresAdapter.MyViewHolder> {
    private Context context;
    private List<String> genresList;
    private DBHelper db;

    private ClickListener clickListener;

    public interface ClickListener {
        void onItemClick(int position, View v);
    }

    public void setOnItemClickListener(ClickListener clickListener) {
        this.clickListener = clickListener;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private LinearLayout ll_genre_row;
        private TextView tv_genre;
        private RecyclerView recyclerMoreGenre;

        public MyViewHolder(View view) {
            super(view);
            if(clickListener != null) {
                view.setOnClickListener(this);
            }
            // bindeamos estos campos
            ll_genre_row = view.findViewById(R.id.ll_genre_row);
            tv_genre = view.findViewById(R.id.tv_genre);
            recyclerMoreGenre = view.findViewById(R.id.recyclerMoreGenre);
        }

        @Override
        public void onClick(View v) {
            if(clickListener != null) {
                clickListener.onItemClick(getAdapterPosition(), v);
            }
        }
    }

    public GenresAdapter(Context context, List<String> genresList) {
        this.context = context;
        this.genresList = genresList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // seleccionamos el layout en el que se mostraran los datos y el padre en el que se mostrara este
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_genre_row, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(GenresAdapter.MyViewHolder holder, int position) {
        final String genre = genresList.get(position);
        db = DBHelper.getInstance(context);

        if(TextUtils.isEmpty(genre)) {
            holder.tv_genre.setText(context.getResources().getString(R.string.stats_no_genre));
        } else {
            holder.tv_genre.setText(genre);
        }

        //
        List<Book> bookListGenre = new ArrayList<>(db.getBooksFromGenreLimit(genre));
        GenreBooksAdapter genreBooksAdapter = new GenreBooksAdapter(context, bookListGenre);
        holder.recyclerMoreGenre.setAdapter(genreBooksAdapter);
        holder.recyclerMoreGenre.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
        genreBooksAdapter.notifyDataSetChanged();
        holder.recyclerMoreGenre.setNestedScrollingEnabled(false);

        //
        holder.ll_genre_row.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("itemGenre", genre);
                bundle.putString("itemType", "genre");
                bundle.putString("fromDetails", "fromDetails");
                Navigation.findNavController(v).navigate(R.id.action_global_nav_more, bundle);
            }
        });
    }

    @Override
    public int getItemCount() {
        return genresList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

}
