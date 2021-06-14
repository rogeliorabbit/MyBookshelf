package com.hitglynorthz.mybookshelf.adapters;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.hitglynorthz.mybookshelf.MainActivity;
import com.hitglynorthz.mybookshelf.R;
import com.hitglynorthz.mybookshelf.db.Book;
import com.hitglynorthz.mybookshelf.db.DBHelper;
import com.hitglynorthz.mybookshelf.db.Publisher;
import com.hitglynorthz.mybookshelf.utils.Utils;

import java.util.ArrayList;
import java.util.List;

public class PublishersAdapter extends RecyclerView.Adapter<PublishersAdapter.MyViewHolder> implements Filterable {

    private Context context;
    private List<Publisher> publisherList;
    private List<Publisher> filterPublisherList;
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
        private MaterialCardView cv_publisher_row;
        private TextView tv_icon, tv_publisher, tv_nbooks, tv_nauthors;

        public MyViewHolder(View view) {
            super(view);
            if (clickListener != null) {
                view.setOnClickListener(this);
            }
            // bindeamos estos campos
            cv_publisher_row = view.findViewById(R.id.cv_publisher_row);
            tv_icon = view.findViewById(R.id.tv_icon);
            tv_publisher = view.findViewById(R.id.tv_publisher);
            tv_nbooks = view.findViewById(R.id.tv_nbooks);
            tv_nauthors = view.findViewById(R.id.tv_nauthors);
        }

        @Override
        public void onClick(View v) {
            if (clickListener != null) {
                clickListener.onItemClick(getAdapterPosition(), v);
            }
        }
    }

    public PublishersAdapter(Context context, List<Publisher> publisherList) {
        this.context = context;
        this.publisherList = publisherList;
        filterPublisherList = new ArrayList<>(publisherList);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // seleccionamos el layout en el que se mostraran los datos y el padre en el que se mostrara este
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_publisher_row, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(PublishersAdapter.MyViewHolder holder, final int position) {
        final Publisher publisher = publisherList.get(position);
        db = DBHelper.getInstance(context);
        // pillamos la primera letra de la editorial
        String first = String.valueOf(publisher.getPublisher_name().charAt(0));
        int nbooks = db.getCountBooksFromPublisherById(publisher.getId_publisher());
        int nauthors = db.getCountAuthorPublisherId(publisher.getId_publisher());

        holder.tv_icon.setText(first.toUpperCase());
        holder.tv_publisher.setText(publisher.getPublisher_name());
        holder.tv_nbooks.setText(String.format(context.getResources().getString(R.string.adapter_nbooks), nbooks));
        holder.tv_nauthors.setText(String.format(context.getResources().getString(R.string.adapter_nauthors), nauthors));

        holder.cv_publisher_row.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickListener.onItemClick(position, v);
                Bundle bundle = new Bundle();
                bundle.putInt("itemId", publisher.getId_publisher());
                bundle.putString("itemType", "publisher");
                //Navigation.findNavController(v).navigate(R.id.action_global_nav_more, bundle);
                ((MainActivity)context).getMainController().navigate(R.id.action_global_nav_more, bundle);
            }
        });
    }

    @Override
    public int getItemCount() {
        return publisherList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public Filter getFilter() {
        return publisherFilter;
    }

    // filtro para el searchview
    private Filter publisherFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<Publisher> filteredPublisher = new ArrayList<>();
            if(constraint == null || constraint.length() == 0) {
                filteredPublisher.addAll(filterPublisherList);
            } else {
                String filterPattern = Utils.removeAccents(constraint.toString().toLowerCase().trim());
                for(Publisher publisher : filterPublisherList) {
                    if(Utils.removeAccents(publisher.getPublisher_name().toLowerCase()).contains(filterPattern)) {
                        filteredPublisher.add(publisher);
                    }
                }
            }
            FilterResults results = new FilterResults();
            results.values = filteredPublisher;

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            publisherList.clear();
            publisherList.addAll((List) results.values);
            notifyDataSetChanged();
        }
    };

}
