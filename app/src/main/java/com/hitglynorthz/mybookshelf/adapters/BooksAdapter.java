package com.hitglynorthz.mybookshelf.adapters;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ShareCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentContainer;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.NavController;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.FragmentNavigator;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.chip.Chip;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.hitglynorthz.mybookshelf.MainActivity;
import com.hitglynorthz.mybookshelf.R;
import com.hitglynorthz.mybookshelf.db.Book;
import com.hitglynorthz.mybookshelf.db.BookBean;
import com.hitglynorthz.mybookshelf.db.DBHelper;
import com.hitglynorthz.mybookshelf.ui.bottomlevels.Fragment_Books;
import com.hitglynorthz.mybookshelf.ui.toplevels.HomeFragment;
import com.hitglynorthz.mybookshelf.utils.Utils;

import java.util.ArrayList;
import java.util.List;

import static androidx.core.content.ContextCompat.getSystemService;
import static androidx.preference.PreferenceManager.getDefaultSharedPreferences;

public class BooksAdapter extends RecyclerView.Adapter<BooksAdapter.MyViewHolder> implements Filterable {

    private Context context;
    private List<BookBean> bookList;
    private List<BookBean> filterBookList;
    private DBHelper db;
    private SharedPreferences prefSettings;
    private ClickListener clickListener;

    private Fragment_Books fragment_books;

    public interface ClickListener {
        void onItemClick(int position, View v);
    }

    public void setOnItemClickListener(ClickListener clickListener) {
        this.clickListener = clickListener;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        // inicializamos los campos del layout en el que se mostraran los datos
        private MaterialCardView cv_book_row;
        private ImageView iv_cover, iv_read_status, iv_fav;
        private TextView tv_title, tv_author, tv_publisher, tv_rating;
        private Chip chip_shelves, chip_shelf;
        private RelativeLayout rl_info;

        public MyViewHolder(View view) {
            super(view);
            if (clickListener != null) {
                view.setOnClickListener(this);
            }
            // bindeamos estos campos
            cv_book_row = view.findViewById(R.id.cv_book_row);
            iv_cover = view.findViewById(R.id.iv_cover);
            tv_title = view.findViewById(R.id.tv_title);
            tv_author = view.findViewById(R.id.tv_author);
            tv_publisher = view.findViewById(R.id.tv_publisher);
            tv_rating = view.findViewById(R.id.tv_rating);
            iv_read_status = view.findViewById(R.id.iv_read_status);
            iv_fav = view.findViewById(R.id.iv_fav);
            chip_shelves = view.findViewById(R.id.chip_shelves);
            chip_shelf = view.findViewById(R.id.chip_shelf);
            rl_info = view.findViewById(R.id.rl_info);
        }

        @Override
        public void onClick(View v) {
            if (clickListener != null) {
                clickListener.onItemClick(getAdapterPosition(), v);
            }
        }
    }

    public BooksAdapter(Context context, List<BookBean> bookList, Fragment_Books fragment_books) {
        this.context = context;
        this.bookList = bookList;
        this.fragment_books = fragment_books;
        filterBookList = new ArrayList<>(bookList);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        prefSettings = getDefaultSharedPreferences(context);
        // seleccionamos el layout en el que se mostraran los datos y el padre en el que se mostrara este
        View itemView;
        if(prefSettings.getBoolean("listype_select", false)) {
            itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_book_grid_row, parent, false);
        } else {
            itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_book_row, parent, false);
        }

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        // asignamos los datos recuperados a los campos del layout
        final BookBean book = bookList.get(position);
        db = DBHelper.getInstance(context);
        prefSettings = getDefaultSharedPreferences(context);

        // mostra u ocultar info segun settings si no es gridlayout
        if(!prefSettings.getBoolean("listype_select", false)) {
            if (prefSettings.getBoolean("book_select", false)) {
                holder.rl_info.setVisibility(View.GONE);
                holder.iv_cover.getLayoutParams().width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 65, context.getResources().getDisplayMetrics());
                holder.iv_cover.getLayoutParams().height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 90, context.getResources().getDisplayMetrics());
                holder.iv_cover.requestLayout();
            } else {
                holder.rl_info.setVisibility(View.VISIBLE);
            }
        }

        // poner datos
        holder.tv_title.setText(book.getTitle());
        holder.tv_author.setText(book.getAuthorName());
        holder.tv_publisher.setText(book.getPublisherName());

        // read_status
        switch (book.getRead_status()) {
            case 0:
                holder.iv_read_status.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_book_status_unread_black));
                break;
            case 1:
                holder.iv_read_status.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_book_status_reading_black));
                break;
            case 2:
                holder.iv_read_status.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_book_status_read_black));
                break;
        }

        // fav
        if(book.getFav() == 1) {
            holder.iv_fav.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_favorite_white));
        } else {
            holder.iv_fav.setVisibility(View.GONE);
        }

        // rating
        if(book.getRating() > 0) {
            holder.tv_rating.setText(String.valueOf(book.getRating()));
        } else {
            holder.tv_rating.setVisibility(View.GONE);
        }

        // cover
        if(!TextUtils.isEmpty(book.getCover())) {
            // si hay datos almacenados se muestra la cover
            Glide.with(context)
                    .load(book.getCover())
                    .centerCrop()
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .error(R.drawable.ic_book_white)
                    .into(holder.iv_cover);
        } else {
            // si no hay datos almacenados se muestra una imágen por defecto
            Glide.with(context)
                    .load(R.drawable.ic_book_white)
                    .fitCenter()
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .into(holder.iv_cover);
        }

        // shelves & shelf si no es gridlayout
        if(!prefSettings.getBoolean("listype_select", false)) {
            if (!prefSettings.getBoolean("book_select", false)) {
                holder.chip_shelves.setText(book.getShelvesName());
                holder.chip_shelf.setText(book.getShelfName());
            }
        }

        // transitionName unica para cada elemento del recyclerview
        holder.cv_book_row.setTransitionName(context.getResources().getString(R.string.transition_row) + book.getId_book());
        holder.iv_cover.setTransitionName(context.getResources().getString(R.string.transition_cover) + book.getId_book());
        holder.tv_title.setTransitionName(context.getResources().getString(R.string.transition_title) + book.getId_book());
        holder.tv_author.setTransitionName(context.getResources().getString(R.string.transition_author) + book.getId_book());
        holder.tv_publisher.setTransitionName(context.getResources().getString(R.string.transition_publisher) + book.getId_book());

        // accion onClick en item del RecyclerView
        holder.cv_book_row.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickListener.onItemClick(position, v);
                Bundle bundle = new Bundle();
                bundle.putInt("book_id", book.getId_book());
                bundle.putString("itemTransitionName", holder.cv_book_row.getTransitionName());
                FragmentNavigator.Extras extras = new FragmentNavigator.Extras.Builder()
                        .addSharedElement(holder.cv_book_row, context.getResources().getString(R.string.transition_row) + book.getId_book())
                        .addSharedElement(holder.iv_cover, context.getResources().getString(R.string.transition_cover) + book.getId_book())
                        .addSharedElement(holder.tv_title, context.getResources().getString(R.string.transition_title) + book.getId_book())
                        .addSharedElement(holder.tv_author, context.getResources().getString(R.string.transition_author) + book.getId_book())
                        .addSharedElement(holder.tv_publisher, context.getResources().getString(R.string.transition_publisher) + book.getId_book())
                        .addSharedElement(holder.cv_book_row, holder.cv_book_row.getTransitionName())
                        .build();
                ((MainActivity)context).getMainController().navigate(R.id.action_global_nav_details, bundle, null, extras);
            }
        });

        // accion onLongClick en item del RecyclerView
        holder.cv_book_row.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                holder.cv_book_row.setChecked(!holder.cv_book_row.isChecked());
                showBottomSheet(context, holder, book, position);
                return true;
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

    @Override
    public Filter getFilter() {
        return bookFilter;
    }

    // filtro para el SearchView
    private Filter bookFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<BookBean> filteredBook = new ArrayList<>();
            if(constraint == null || constraint.length() == 0) {
                filteredBook.addAll(filterBookList);
            } else {
                String filterPattern = Utils.removeAccents(constraint.toString().toLowerCase().trim());
                for (BookBean book : filterBookList) {
                    if(Utils.removeAccents(book.getTitle().toLowerCase()).contains(filterPattern)) {
                        filteredBook.add(book);
                    } else if(Utils.removeAccents(book.getAuthorName().toLowerCase()).contains(filterPattern)) {
                        filteredBook.add(book);
                    } else if(Utils.removeAccents(book.getPublisherName().toLowerCase()).contains(filterPattern)) {
                        filteredBook.add(book);
                    }
                }
            }
            FilterResults results = new FilterResults();
            results.values = filteredBook;

            return  results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            bookList.clear();
            bookList.addAll((List) results.values);
            notifyDataSetChanged();
        }
    };

    // bottom sheet
    private void showBottomSheet(final Context context, final MyViewHolder holder, final BookBean book, final int position) {
        final BottomSheetDialog mBottomSheetDialog = new BottomSheetDialog(context);
        View sheetView = LayoutInflater.from(context).inflate(R.layout.bottom_sheet_book_row, null);
        mBottomSheetDialog.setContentView(sheetView);
        mBottomSheetDialog.show();
        mBottomSheetDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                holder.cv_book_row.setChecked(!holder.cv_book_row.isChecked());
            }
        });
        //
        ImageView iv_bottom_sheet_book = sheetView.findViewById(R.id.iv_bottom_sheet_book);
        Glide.with(context)
                .load(book.getCover())
                .centerCrop()
                .error(R.drawable.ic_broken_image_white)
                .placeholder(R.drawable.ic_book_white)
                .into(iv_bottom_sheet_book);
        TextView tv_sheet_title = sheetView.findViewById(R.id.tv_sheet_title);
        tv_sheet_title.setText(book.getTitle());
        TextView tv_sheet_author = sheetView.findViewById(R.id.tv_sheet_author);
        tv_sheet_author.setText(db.getAuthorNameById(book.getAuthor()));
        TextView tv_sheet_publisher = sheetView.findViewById(R.id.tv_sheet_publisher);
        tv_sheet_publisher.setText(db.getPublisherNameById(book.getPublisher()));
        //
        LinearLayout share = sheetView.findViewById(R.id.ll_bottom_sheet_share);
        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBottomSheetDialog.dismiss();
                share(context, book);
            }
        });
        //
        LinearLayout delete = sheetView.findViewById(R.id.ll_bottom_sheet_delete);
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBottomSheetDialog.dismiss();
                deleteBookDialog(context, book, position);
            }
        });
    }

    // compartir
    private void share(Context context, BookBean book) {
        String shareBodyText;
        if(book.getIsbn() != null && !book.getIsbn().isEmpty() && !book.getIsbn().equals("0")) {
            shareBodyText = String.format(context.getString(R.string.book_details_share_text_isbn), book.getTitle(), book.getAuthorName(), book.getPublisherName(), book.getIsbn(), book.getWeb_link());
        } else {
            shareBodyText = String.format(context.getString(R.string.book_details_share_text), book.getTitle(), book.getAuthorName(), book.getPublisherName(), book.getWeb_link());
        }
        String mimeType = "text/plain";
        ShareCompat.IntentBuilder
                .from((Activity)context)
                .setType(mimeType)
                .setChooserTitle(R.string.book_details_share_title)
                .setText(shareBodyText)
                .startChooser();
    }

    // dialogo borrar libro
    private void deleteBookDialog(final Context context, final BookBean book, final int pos) {
        new MaterialAlertDialogBuilder(context, R.style.MaterialDialog)
                .setTitle(context.getString(R.string.book_details_dialog_delete_title))
                .setMessage(String.format(context.getString(R.string.book_details_dialog_delete_text), book.getTitle()))
                .setPositiveButton(context.getString(R.string.book_details_dialog_delete_positive), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteBook(context, book, pos);
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

    // borrar libro
    private void deleteBook(Context context, BookBean book, int pos) {
        Utils.deleteCover(book.getCover());
        db.deleteBook(book.getId_book());
        // check Author
        if(db.getCountBooksFromAuthorById(book.getAuthor()) == 0) {
            db.deleteAuthor(book.getAuthor());
        }
        // se borran las editoriales asociadas al autor si no tienen mas autores
        List<Integer> publishers = db.getPublisherIdByAuthorId(book.getAuthor());
        for(int i = 0; i < publishers.size(); i++) {
            if(db.getCountAuthorPublisherId(publishers.get(i)) == 0) {
                db.deletePublisher(publishers.get(i));
            }
        }
        // check Publisher
        if(db.getCountBooksFromPublisherById(book.getPublisher()) == 0) {
            db.deletePublisher(book.getPublisher());
        }
        // se borran los autores asociados a la editorial si no tienen mas editoriales
        List<Integer> authors = db.getAuthorIdbyPublisherId(book.getPublisher());
        for(int i = 0; i < authors.size(); i++) {
            if(db.getCountPublishersByAuthorId(authors.get(i)) == 0) {
                db.deleteAuthor(authors.get(i));
            }
        }
        // check Shelves
        if(db.getCountBooksFromShelvesById(book.getShelves()) == 0) {
            db.deleteShelves(book.getShelves());
        }
        // check Shelf
        if(db.getCountBooksFromShelfById(book.getShelf()) == 0) {
            db.deleteShelf(book.getShelf());
        }
        if(db.getAllAuthors().size() == 0) {
            db.deleteAll();
        }
        bookList.remove(pos);
        notifyItemRemoved(pos);
        notifyItemRangeChanged(pos, bookList.size());
        // mostrar snackbar en HomeFragment, actualizar vista si no quedan libros
        // y actualizar el drawer
        HomeFragment.getInstance().setupUpdateHome(context.getString(R.string.book_details_dialog_delete_deleted));
        fragment_books.toggleEmptyBooks(bookList);
        ((MainActivity)context).setupStatsItem();
    }

}
