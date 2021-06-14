package com.hitglynorthz.mybookshelf.ui.details;

import android.animation.Animator;
import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.appcompat.view.menu.MenuPopupHelper;
import androidx.appcompat.widget.PopupMenu;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ShareCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.view.ViewCompat;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.palette.graphics.Palette;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.transition.Transition;
import androidx.transition.TransitionInflater;
import androidx.transition.TransitionSet;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.transition.MaterialContainerTransform;
import com.hitglynorthz.mybookshelf.MainActivity;
import com.hitglynorthz.mybookshelf.R;
import com.hitglynorthz.mybookshelf.adapters.AuthorBooksAdapter;
import com.hitglynorthz.mybookshelf.adapters.GenreBooksAdapter;
import com.hitglynorthz.mybookshelf.adapters.PublisherBooksAdapter;
import com.hitglynorthz.mybookshelf.db.Book;
import com.hitglynorthz.mybookshelf.db.DBHelper;
import com.hitglynorthz.mybookshelf.utils.Utils;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.CLIPBOARD_SERVICE;

public class Fragment_Details extends Fragment implements View.OnClickListener, View.OnLongClickListener {
    private View rootView;
    private Context context;

    private Toolbar toolbar;
    private AppBarLayout appBarLayout;
    private BottomAppBar bottomAppBar;
    private FloatingActionButton details_fab;
    private NestedScrollView nestedDetails;
    private LinearLayout cv_book_row;

    private NavController mainController;

    private int id_book;
    private Book book;
    private String authorName, publisherName;

    private ImageView iv_cover;
    private TextView tv_title, tv_author, tv_publisher, tv_genre,tv_shelves, tv_shelf, tv_descp;
    private TextView tv_more_author, tv_more_publisher, tv_more_genre, tv_pages;
    private LinearLayout ll_descp, ll_rating, ll_more_author, ll_more_publisher, ll_more_genre;
    private ImageView iv_book_fav, iv_book_read_status, iv_book_note;
    private RatingBar ratingBar;

    private RecyclerView recyclerMoreAuthor, recyclerMorePublisher, recyclerMoreGenre;
    private AuthorBooksAdapter authorBooksAdapter;
    private PublisherBooksAdapter publisherBooksAdapter;
    private GenreBooksAdapter genreBooksAdapter;
    private List<Book> bookListAuthor = new ArrayList<>();
    private List<Book> bookListPublisher = new ArrayList<>();
    private List<Book> bookListGenre = new ArrayList<>();

    private DBHelper db;

    String sharedViewId = "";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        //setSharedElementReturnTransition(TransitionInflater.from(getActivity()).inflateTransition(android.R.transition.move));
        //setExitTransition(TransitionInflater.from(getActivity()).inflateTransition(android.R.transition.no_transition));
        //setSharedElementEnterTransition(TransitionInflater.from(getActivity()).inflateTransition(android.R.transition.move));
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        if (getArguments() != null) {
            sharedViewId = getArguments().getString("itemTransitionName");
        }

        setSharedElementEnterTransition(new MaterialContainerTransform());
        //postponeEnterTransition();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView =  inflater.inflate(R.layout.fragment_details, container, false);
        context = getContext();
        db = DBHelper.getInstance(context);

        initViews();

        // recuperamos los datos que se envian al fragment
        if (getArguments() != null) {
            id_book = getArguments().getInt("book_id");
            if(db.getBook(id_book) == null) {
                Navigation.findNavController(getActivity(), R.id.nav_host_fragment).popBackStack();
            } else {
                book = db.getBook(id_book);
                setBookDetails();
            }
        }

        setupLayout();
        handleBottomAppBar();

        // transitionName unica
        nestedDetails.setTransitionName(getString(R.string.transition_row) + id_book);
        iv_cover.setTransitionName(getString(R.string.transition_cover) + id_book);
        tv_title.setTransitionName(getString(R.string.transition_title) + id_book);
        tv_author.setTransitionName(getString(R.string.transition_author) + id_book);
        tv_publisher.setTransitionName(getString(R.string.transition_publisher) + id_book);

        // se usa para mostrar snackbar al editar un libro
        mainController = ((MainActivity)context).getMainController();
        MutableLiveData<String> liveData = mainController.getCurrentBackStackEntry()
                .getSavedStateHandle()
                .getLiveData("update_details");
        liveData.observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                Snackbar.make(getActivity().getWindow().getDecorView().findViewById(android.R.id.content), s, Snackbar.LENGTH_SHORT).setAnchorView(details_fab).show();
                mainController.getCurrentBackStackEntry().getSavedStateHandle().remove("update_details");
            }
        });

        details_fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setFav();
            }
        });

        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        ViewCompat.setTransitionName(rootView, sharedViewId);
        super.onViewCreated(view, savedInstanceState);
    }

    //
    private void initViews() {
        toolbar = this.getActivity().findViewById(R.id.toolbar);
        toolbar.setTitle("");
        appBarLayout = this.getActivity().findViewById(R.id.appBarLayout);
        details_fab = rootView.findViewById(R.id.details_fab);
        bottomAppBar = rootView.findViewById(R.id.bottomAppBar);
        bottomAppBar.inflateMenu(R.menu.menu_book_bottom);
        nestedDetails = rootView.findViewById(R.id.nestedDetails);

        iv_cover = rootView.findViewById(R.id.iv_cover);

        tv_title = rootView.findViewById(R.id.tv_title);
        tv_author = rootView.findViewById(R.id.tv_author);
        tv_publisher = rootView.findViewById(R.id.tv_publisher);
        tv_genre = rootView.findViewById(R.id.tv_genre);

        iv_book_fav = rootView.findViewById(R.id.iv_book_fav);
        iv_book_read_status = rootView.findViewById(R.id.iv_book_read_status);
        iv_book_note = rootView.findViewById(R.id.iv_book_note);
        tv_pages = rootView.findViewById(R.id.tv_pages);

        tv_shelves = rootView.findViewById(R.id.tv_shelves);
        tv_shelves.setOnClickListener(this);
        tv_shelves.setOnLongClickListener(this);
        tv_shelf = rootView.findViewById(R.id.tv_shelf);
        tv_shelf.setOnClickListener(this);
        tv_shelf.setOnLongClickListener(this);

        tv_descp = rootView.findViewById(R.id.tv_descp);

        ratingBar = rootView.findViewById(R.id.ratingBar);

        tv_more_author = rootView.findViewById(R.id.tv_more_author);
        tv_more_publisher = rootView.findViewById(R.id.tv_more_publisher);
        tv_more_genre = rootView.findViewById(R.id.tv_more_genre);

        ll_descp = rootView.findViewById(R.id.ll_descp);
        ll_descp.setOnClickListener(this);
        ll_rating = rootView.findViewById(R.id.ll_rating);
        ll_rating.setOnClickListener(this);

        ll_more_author = rootView.findViewById(R.id.ll_more_author);
        ll_more_publisher = rootView.findViewById(R.id.ll_more_publisher);
        ll_more_genre = rootView.findViewById(R.id.ll_more_genre);
    }

    //
    private void setupLayout() {
        //nestedscrollview
        nestedDetails.setOnScrollChangeListener(new View.OnScrollChangeListener() {
            @Override
            public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if(scrollY > 85) {
                    toolbar.setTitle(book.getTitle());
                } else {
                    toolbar.setTitle(" ");
                }
            }
        });
        // fab & rating
        checkFav();
        ratingBar.setRating(book.getRating());
    }

    //
    private void checkFav() {
        int fav = db.checkIfBookFav(id_book);
        switch (fav) {
            case 0:
                details_fab.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_favorite_border_white));
                iv_book_fav.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_favorite_border_white));
                iv_book_fav.setColorFilter(ContextCompat.getColor(context, R.color.colorSecondaryVariant));
                break;
            case 1:
                details_fab.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_favorite_white));
                iv_book_fav.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_favorite_white));
                iv_book_fav.setColorFilter(ContextCompat.getColor(context, R.color.colorPink));
                break;
        }
    }

    //
    private void setFav() {
        int fav = db.checkIfBookFav(id_book);
        switch (fav) {
            case 0:
                db.updateBookFav(1, id_book);
                Snackbar.make(getActivity().getWindow().getDecorView().findViewById(android.R.id.content), getString(R.string.book_details_book_fav_text), Snackbar.LENGTH_SHORT).setAnchorView(details_fab).show();
                break;
            case 1:
                db.updateBookFav(0, id_book);
                Snackbar.make(getActivity().getWindow().getDecorView().findViewById(android.R.id.content), getString(R.string.book_details_book_unfav_text), Snackbar.LENGTH_SHORT).setAnchorView(details_fab).show();
                break;
        }
        checkFav();
    }

    //
    private void setBookDetails() {
        authorName = db.getAuthorNameById(book.getAuthor());
        publisherName = db.getPublisherNameById(book.getPublisher());

        // cover
        Glide.with(context)
                .asBitmap()
                .load(book.getCover())
                .centerCrop()
                .error(R.drawable.ic_book_white)
                .listener(new RequestListener<Bitmap>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {
                        if (resource != null) {
                            setCustomColors(Palette.from(resource).generate());
                        }
                        return false;
                    }
                })
                .into(iv_cover);

        tv_title.setText(book.getTitle());
        tv_author.setText(authorName);
        if(String.valueOf(book.getYear()).length() == 4) {
            tv_publisher.setText(publisherName + " - " + book.getYear());
        } else {
            tv_publisher.setText(publisherName);
        }
        if(!TextUtils.isEmpty(book.getGenre())) {
            tv_genre.setVisibility(View.VISIBLE);
            tv_genre.setText(book.getGenre());
        }

        tv_shelves.setText(db.getShelvesNameById(book.getShelves()));
        tv_shelf.setText(db.getShelfNameById(book.getShelf()));

        if(book.getDescp().length() > 0) {
            tv_descp.setText(book.getDescp());
        } else {
            tv_descp.setVisibility(View.GONE);
        }

        tv_pages.setText(String.format(getString(R.string.book_details_pages_text), book.getPages()));

        // recycler more from Author
        if(db.getBooksFromAuthorByIdExcept(book.getAuthor(), book.getId_book()).size() > 0) {
            ll_more_author.setVisibility(View.VISIBLE);
            tv_more_author.setText(String.format(getString(R.string.book_details_more_author), authorName));
            ll_more_author.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle bundle = new Bundle();
                    bundle.putInt("itemId", book.getAuthor());
                    bundle.putString("itemType", "author");
                    bundle.putString("fromDetails", "fromDetails");
                    Navigation.findNavController(v).navigate(R.id.action_global_nav_more, bundle);
                }
            });
            //
            recyclerMoreAuthor = rootView.findViewById(R.id.recyclerMoreAuthor);
            recyclerMoreAuthor.setNestedScrollingEnabled(false);
            bookListAuthor.clear();
            bookListAuthor.addAll(db.getBooksFromAuthorByIdExcept(book.getAuthor(), book.getId_book()));
            authorBooksAdapter = new AuthorBooksAdapter(context, bookListAuthor);
            recyclerMoreAuthor.setAdapter(authorBooksAdapter);
            recyclerMoreAuthor.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
        }
        // recycler more from Publisher
        if(db.getBooksFromPublisherByIdExcept(book.getPublisher(), book.getId_book()).size() > 0) {
            ll_more_publisher.setVisibility(View.VISIBLE);
            tv_more_publisher.setText(String.format(getString(R.string.book_details_more_publisher), publisherName));
            ll_more_publisher.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle bundle = new Bundle();
                    bundle.putInt("itemId", book.getPublisher());
                    bundle.putString("itemType", "publisher");
                    bundle.putString("fromDetails", "fromDetails");
                    Navigation.findNavController(v).navigate(R.id.action_global_nav_more, bundle);
                }
            });
            //
            recyclerMorePublisher = rootView.findViewById(R.id.recyclerMorePublisher);
            recyclerMorePublisher.setNestedScrollingEnabled(false);
            bookListPublisher.clear();
            bookListPublisher.addAll(db.getBooksFromPublisherByIdExcept(book.getPublisher(), book.getId_book()));
            publisherBooksAdapter = new PublisherBooksAdapter(context, bookListPublisher);
            recyclerMorePublisher.setAdapter(publisherBooksAdapter);
            recyclerMorePublisher.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
        }
        // recycler more from Genre
        if(db.getBooksFromGenreExcept(book.getGenre(), book.getId_book()).size() > 0) {
            ll_more_genre.setVisibility(View.VISIBLE);
            tv_more_genre.setText(String.format(getString(R.string.book_details_more_genre), book.getGenre()));
            ll_more_genre.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle bundle = new Bundle();
                    bundle.putString("itemGenre", book.getGenre());
                    bundle.putString("itemType", "genre");
                    bundle.putString("fromDetails", "fromDetails");
                    Navigation.findNavController(v).navigate(R.id.action_global_nav_more, bundle);
                }
            });
            //
            recyclerMoreGenre = rootView.findViewById(R.id.recyclerMoreGenre);
            recyclerMoreGenre.setNestedScrollingEnabled(false);
            bookListGenre.clear();
            bookListGenre.addAll(db.getBooksFromGenreExcept(book.getGenre(), book.getId_book()));
            genreBooksAdapter = new GenreBooksAdapter(context, bookListGenre);
            recyclerMoreGenre.setAdapter(genreBooksAdapter);
            recyclerMoreGenre.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_shelves:
                Bundle bundleShelves = new Bundle();
                bundleShelves.putInt("itemId", book.getShelves());
                bundleShelves.putString("itemType", "shelves");
                Navigation.findNavController(v).navigate(R.id.action_global_nav_more, bundleShelves);
                break;
            case R.id.tv_shelf:
                Bundle bundleShelf = new Bundle();
                bundleShelf.putInt("itemId", book.getShelf());
                bundleShelf.putString("itemType", "shelf");
                Navigation.findNavController(v).navigate(R.id.action_global_nav_more, bundleShelf);
                break;
            case R.id.ll_descp:
                showMore();
                break;
            case R.id.ll_rating:
                showRatingDialog();
                break;
        }
    }

    //
    @Override
    public boolean onLongClick(View v) {
        switch (v.getId()) {
            case R.id.tv_shelves:
                Snackbar.make(v, getString(R.string.book_details_shelves_text), Snackbar.LENGTH_SHORT).setAnchorView(details_fab).show();
                break;
            case R.id.tv_shelf:
                Snackbar.make(v, getString(R.string.book_details_shelf_text), Snackbar.LENGTH_SHORT).setAnchorView(details_fab).show();
                break;
        }
        return true;
    }

    //
    private void showMore() {
        BottomSheetDialog mBottomSheetDialog = new BottomSheetDialog(context);
        View sheetView = LayoutInflater.from(context).inflate(R.layout.bottom_sheet_book_details_more, null);
        mBottomSheetDialog.setContentView(sheetView);
        mBottomSheetDialog.show();
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
        tv_sheet_author.setText(authorName);
        TextView tv_sheet_publisher = sheetView.findViewById(R.id.tv_sheet_publisher);
        tv_sheet_publisher.setText(publisherName);
        LinearLayout ll_bs_descp = sheetView.findViewById(R.id.ll_bs_descp);
        TextView tv_details_descp = sheetView.findViewById(R.id.tv_details_descp);
        if(book.getDescp() != null && (!TextUtils.equals(book.getDescp() ,"null")) && (!TextUtils.isEmpty(book.getDescp()))) {
            ll_bs_descp.setVisibility(View.VISIBLE);
            tv_details_descp.setText(book.getDescp());
        }
        LinearLayout ll_bs_isbn = sheetView.findViewById(R.id.ll_bs_isbn);
        TextView tv_details_isbn = sheetView.findViewById(R.id.tv_details_isbn);
        if(book.getIsbn() != null && (!TextUtils.equals(book.getIsbn(), "null")) && (!TextUtils.isEmpty(book.getIsbn()))) {
            ll_bs_isbn.setVisibility(View.VISIBLE);
            tv_details_isbn.setText(book.getIsbn());
            registerForContextMenu(tv_details_isbn);
        }
        LinearLayout ll_bs_link = sheetView.findViewById(R.id.ll_bs_link);
        TextView tv_details_web_link = sheetView.findViewById(R.id.tv_details_web_link);
        if(book.getWeb_link() != null && (!TextUtils.equals(book.getWeb_link(), "null")) && (!TextUtils.isEmpty(book.getWeb_link()))) {
            ll_bs_link.setVisibility(View.VISIBLE);
            tv_details_web_link.setMovementMethod(LinkMovementMethod.getInstance());
            tv_details_web_link.setText(book.getTitle() + ": " + book.getWeb_link());
        }
    }

    //
    private void showRatingDialog() {
        MaterialAlertDialogBuilder ratingDialog = new MaterialAlertDialogBuilder(context, R.style.MaterialDialog);
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.dialog_book_rating, null);
        final RatingBar dialog_ratingBar = view.findViewById(R.id.dialog_ratingBar);
        dialog_ratingBar.setRating(db.checkBookRating(id_book));
        ratingDialog.setView(view);
        ratingDialog.setTitle(getString(R.string.book_details_dialog_rating_text));
        ratingDialog.setPositiveButton(getString(R.string.book_details_dialog_rating_positive), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                int rating = (int) dialog_ratingBar.getRating();
                db.updateBookRating(rating, id_book);
                ratingBar.setRating(rating);
                Snackbar.make(getActivity().getWindow().getDecorView().findViewById(android.R.id.content), getString(R.string.book_details_dialog_rating_updated), Snackbar.LENGTH_SHORT).setAnchorView(details_fab).show();
                dialog.cancel();
            }
        });
        ratingDialog.setNegativeButton(R.string.book_details_dialog_rating_negative, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        ratingDialog.show();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_book_top, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_book_edit:
                Bundle bundle = new Bundle();
                bundle.putInt("id_book", book.getId_book());
                Navigation.findNavController(getActivity().getWindow().getDecorView().findViewById(R.id.nav_host_fragment)).navigate(R.id.action_global_nav_edit_book, bundle);
                break;
            case R.id.action_book_delete:
                deleteBookDialog();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    //
    private void deleteBookDialog() {
        new MaterialAlertDialogBuilder(context, R.style.MaterialDialog)
                .setTitle(getString(R.string.book_details_dialog_delete_title))
                .setMessage(String.format(getString(R.string.book_details_dialog_delete_text), book.getTitle()))
                .setPositiveButton(getString(R.string.book_details_dialog_delete_positive), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Utils.deleteCover(book.getCover());
                        db.deleteBook(id_book);
                        // check Author
                        if(db.getCountBooksFromAuthorById(book.getAuthor()) == 0) {
                            db.deleteAuthor(book.getAuthor());
                        }
                        // check Publisher
                        if(db.getCountBooksFromPublisherById(book.getPublisher()) == 0) {
                            db.deletePublisher(book.getPublisher());
                        }
                        // check Shelves
                        if(db.getCountBooksFromShelvesById(book.getShelves()) == 0) {
                            db.deleteShelves(book.getShelves());
                        }
                        // check Shelf
                        if(db.getCountBooksFromShelfById(book.getShelf()) == 0) {
                            db.deleteShelf(book.getShelf());
                        }
                        NavController navController = ((MainActivity)context).getMainController();
                        navController.getPreviousBackStackEntry().getSavedStateHandle().set("update_home", getString(R.string.book_details_dialog_delete_deleted));
                        navController.popBackStack();
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

    //
    private void handleBottomAppBar() {
        bottomAppBar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int id = item.getItemId();
                switch (id) {
                    case R.id.action_share:
                        shareBook();
                        break;
                    case R.id.action_note:
                        showNoteDialog();
                        break;
                    case R.id.action_read:
                        View itemReadStatus = getActivity().findViewById(R.id.action_read);
                        showPopupMenuReadStatus(itemReadStatus);
                        break;
                }
                return false;
            }
        });
        setReadStatusIcon();
        setNoteIcons();
    }

    //
    private void shareBook() {
        String shareBodyText;
        if(book.getIsbn() != null && !book.getIsbn().isEmpty() && !book.getIsbn().equals("0")) {
            shareBodyText = String.format(getString(R.string.book_details_share_text_isbn), book.getTitle(), authorName, publisherName, book.getIsbn(), book.getWeb_link());
        } else {
            shareBodyText = String.format(getString(R.string.book_details_share_text), book.getTitle(), authorName, publisherName, book.getWeb_link());
        }
        String mimeType = "text/plain";
        ShareCompat.IntentBuilder
                .from(getActivity())
                .setType(mimeType)
                .setChooserTitle(R.string.book_details_share_title)
                .setText(shareBodyText)
                .startChooser();
    }

    //
    private void showNoteDialog() {
        MaterialAlertDialogBuilder noteDialog = new MaterialAlertDialogBuilder(context, R.style.NoteDialog);
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.dialog_book_note, null);
        final EditText et_note = view.findViewById(R.id.et_note);
        String note = db.getBookNote(id_book);
        if(note != null && (!TextUtils.equals(note ,"null")) && (!TextUtils.isEmpty(note))) {
            et_note.setText(note);
        }
        noteDialog.setView(view);
        noteDialog.setPositiveButton(getString(R.string.book_details_dialog_note_positive), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                db.updateBookNote(et_note.getText().toString(), id_book);
                setNoteIcons();
                Snackbar.make(getActivity().getWindow().getDecorView().findViewById(android.R.id.content), getString(R.string.book_details_dialog_note_saved), Snackbar.LENGTH_SHORT).setAnchorView(details_fab).show();
                dialog.cancel();
            }
        });
        noteDialog.setNegativeButton(R.string.book_details_dialog_note_negative, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        noteDialog.show();
    }

    //
    private void setNoteIcons() {
        String note = db.getBookNote(id_book);
        if(note != null && (!TextUtils.equals(note ,"null")) && (!TextUtils.isEmpty(note))) {
            bottomAppBar.getMenu().getItem(2).setIcon(ResourcesCompat.getDrawable(context.getResources(), R.drawable.ic_notebook_filled_white, null));
            iv_book_note.setImageDrawable(ResourcesCompat.getDrawable(context.getResources(), R.drawable.ic_notebook_filled_black, null));
        } else {
            bottomAppBar.getMenu().getItem(2).setIcon(ResourcesCompat.getDrawable(context.getResources(), R.drawable.ic_notebook_outline_white, null));
            iv_book_note.setImageDrawable(ResourcesCompat.getDrawable(context.getResources(), R.drawable.ic_notebook_outline_black, null));
        }
    }

    //
    @SuppressLint("RestrictedApi")
    private void showPopupMenuReadStatus(View view) {
        PopupMenu popupMenu = new PopupMenu(context, view);
        popupMenu.inflate(R.menu.menu_popup_read_status);
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_popup_read_status_unread:
                        setReadStatus(0);
                        Snackbar.make(getActivity().getWindow().getDecorView().findViewById(android.R.id.content), getString(R.string.action_popup_read_status_unread), Snackbar.LENGTH_SHORT).setAnchorView(details_fab).show();
                        return true;
                    case R.id.action_popup_read_status_reading:
                        setReadStatus(1);
                        Snackbar.make(getActivity().getWindow().getDecorView().findViewById(android.R.id.content), getString(R.string.action_popup_read_status_reading), Snackbar.LENGTH_SHORT).setAnchorView(details_fab).show();
                        return true;
                    case R.id.action_popup_read_status_readed:
                        setReadStatus(2);
                        Snackbar.make(getActivity().getWindow().getDecorView().findViewById(android.R.id.content), getString(R.string.action_popup_read_status_readed), Snackbar.LENGTH_SHORT).setAnchorView(details_fab).show();
                        return true;
                    default:
                        return false;
                }
            }
        });
        MenuPopupHelper menuHelper = new MenuPopupHelper(context, (MenuBuilder) popupMenu.getMenu(), view);
        menuHelper.setForceShowIcon(true);
        menuHelper.show();
    }

    //
    private void setReadStatus(int rs) {
        db.updateBookReadStatus(rs, id_book);
        setReadStatusIcon();
    }

    //
    private void setReadStatusIcon() {
        int rs = db.checkBookReadStatus(id_book);
        switch (rs) {
            case 0:
                bottomAppBar.getMenu().getItem(1).setIcon(ResourcesCompat.getDrawable(context.getResources(), R.drawable.ic_book_status_unread_white, null));
                iv_book_read_status.setImageDrawable(ResourcesCompat.getDrawable(context.getResources(), R.drawable.ic_book_status_unread_black, null));
                break;
            case 1:
                bottomAppBar.getMenu().getItem(1).setIcon(ResourcesCompat.getDrawable(context.getResources(), R.drawable.ic_book_status_reading_white, null));
                iv_book_read_status.setImageDrawable(ResourcesCompat.getDrawable(context.getResources(), R.drawable.ic_book_status_reading_black, null));
                break;
            case 2:
                bottomAppBar.getMenu().getItem(1).setIcon(ResourcesCompat.getDrawable(context.getResources(), R.drawable.ic_book_status_read_white, null));
                iv_book_read_status.setImageDrawable(ResourcesCompat.getDrawable(context.getResources(), R.drawable.ic_book_status_read_black, null));
                break;
        }
    }

    //
    private void setCustomColors(Palette pCustom) {
        Palette.Swatch swatchColor = Utils.getPalette(pCustom, "mutedSwatch");
        Palette.Swatch swatchColorB = Utils.getPalette(pCustom, "vibrantSwatch");
        if(swatchColor != null) {
            bottomAppBar.setBackgroundTint(ColorStateList.valueOf(Utils.manipulateColor(swatchColor.getRgb(), 0.62f)));
            iv_cover.setBackgroundColor(Utils.manipulateColor(swatchColor.getRgb(), 0.62f));
        } else if(swatchColorB != null) {
            bottomAppBar.setBackgroundTint(ColorStateList.valueOf(Utils.manipulateColor(swatchColorB.getRgb(), 0.62f)));
            iv_cover.setBackgroundColor(Utils.manipulateColor(swatchColorB.getRgb(), 0.62f));
        }
    }

    //
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        menu.add(0, v.getId(),0, getString(R.string.book_details_contextmenu_copy));
        menu.setHeaderTitle(getString(R.string.book_details_contextmenu_header));
        TextView textView = (TextView) v;
        ClipboardManager manager = (ClipboardManager) context.getSystemService(CLIPBOARD_SERVICE);
        ClipData clipData = ClipData.newPlainText("text", textView.getText());
        manager.setPrimaryClip(clipData);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        details_fab.hide();
        bottomAppBar.setVisibility(View.GONE);
    }
}
