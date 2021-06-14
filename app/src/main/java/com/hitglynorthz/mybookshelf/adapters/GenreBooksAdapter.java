package com.hitglynorthz.mybookshelf.adapters;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.hitglynorthz.mybookshelf.R;
import com.hitglynorthz.mybookshelf.db.Book;

import java.util.List;

public class GenreBooksAdapter extends RecyclerView.Adapter<GenreBooksAdapter.MyViewHolder> {

    private Context context;
    private List<Book> bookList;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        // inicializamos los campos del layout en el que se mostraran los datos
        LinearLayout ll_more_from;
        ImageView iv_cover;
        TextView tv_title;

        public MyViewHolder(View view) {
            super(view);
            // bindeamos estos campos
            ll_more_from = view.findViewById(R.id.ll_more_from);
            iv_cover = view.findViewById(R.id.iv_cover);
            tv_title = view.findViewById(R.id.tv_title);
        }
    }

    public GenreBooksAdapter(Context context, List<Book> bookList) {
        this.context = context;
        this.bookList = bookList;
    }

    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_book_more_from_row, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        final Book book = bookList.get(position);

        // cover
        Glide.with(context)
                .load(book.getCover())
                .centerCrop()
                .transition(DrawableTransitionOptions.withCrossFade())
                .apply(RequestOptions.placeholderOf(R.drawable.ic_book_white))
                .into(holder.iv_cover);

        // book title
        holder.tv_title.setText(book.getTitle());

        // onClick
        holder.ll_more_from.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavOptions navOptions = new NavOptions.Builder()
                        .setEnterAnim(android.R.anim.fade_in)
                        .setExitAnim(android.R.anim.fade_out)
                        .setPopEnterAnim(android.R.anim.fade_in)
                        .setPopExitAnim(android.R.anim.fade_out)
                        .build();
                Bundle bundle = new Bundle();
                bundle.putInt("book_id", book.getId_book());
                Navigation.findNavController(v).navigate(R.id.action_global_nav_details, bundle, navOptions, null);
            }
        });
    }

    @Override
    public int getItemCount() {
        return bookList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

}
