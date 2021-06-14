package com.hitglynorthz.mybookshelf.ui.details;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.hitglynorthz.mybookshelf.MainActivity;
import com.hitglynorthz.mybookshelf.R;
import com.hitglynorthz.mybookshelf.adapters.BooksAdapter;
import com.hitglynorthz.mybookshelf.adapters.MoreAdapter;
import com.hitglynorthz.mybookshelf.db.BookBean;
import com.hitglynorthz.mybookshelf.db.DBHelper;
import com.hitglynorthz.mybookshelf.db.Shelf;

import java.util.ArrayList;
import java.util.List;

import static androidx.preference.PreferenceManager.getDefaultSharedPreferences;

public class Fragment_More extends Fragment {
    private View rootView;
    private Context context;

    private AppBarLayout appBarLayout;
    private Toolbar toolbar;

    private int itemId, itemShelvesId;
    private String itemType, itemGenre, itemName;

    private MoreAdapter moreAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private List<BookBean> bookList = new ArrayList<>();
    private RecyclerView recyclerView;
    private MaterialCardView cv_info_no_books;

    private final String KEY_RECYCLER_STATE = "recycler_state";
    private static Bundle mBundleRecyclerViewState;
    private Parcelable listState;

    private SharedPreferences prefSettings;

    private SearchView searchView;
    private MenuItem search, edit, delete;

    private DBHelper db;

    private static Fragment_More instance = null;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        instance = this;
        setHasOptionsMenu(true);
    }

    public static Fragment_More getInstance() {
        return instance;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView =  inflater.inflate(R.layout.fragment_more, container, false);
        context = getContext();
        db = DBHelper.getInstance(context);
        prefSettings = getDefaultSharedPreferences(context);

        initViews();

        if (getArguments() != null) {
            itemId = getArguments().getInt("itemId");
            itemType = getArguments().getString("itemType");
            itemGenre = getArguments().getString("itemGenre");
            itemShelvesId = getArguments().getInt("itemShelvesId");
            getMoreInfo();
        }

        return rootView;
    }

    //
    private void initViews() {
        appBarLayout = this.getActivity().findViewById(R.id.appBarLayout);
        toolbar = this.getActivity().findViewById(R.id.toolbar);
        recyclerView = rootView.findViewById(R.id.recyclerView);
        recyclerView.setMotionEventSplittingEnabled(false); // no permitir la doble seleccion de items
        cv_info_no_books = rootView.findViewById(R.id.cv_info_no_books);
    }

    //
    private void getMoreInfo() {
        switch (itemType) {
            case "author":
                getMoreFromAuthor();
                itemName = db.getAuthorNameById(itemId);
                break;
            case "publisher":
                getMoreFromPublisher();
                itemName = db.getPublisherNameById(itemId);
                break;
            case "shelves":
                getMoreFromShelves();
                itemName = db.getShelvesNameById(itemId);
                break;
            case "shelf":
                getMoreFromShelf();
                itemName = db.getShelfNameById(itemId);
                break;
            case "genre":
                getMoreFromGenre();
                itemName = itemGenre;
                break;
        }
        setupRecycler();
    }

    //
    private void getMoreFromAuthor() {
        toolbar.setTitle(db.getAuthorNameById(itemId));
        bookList.clear();
        bookList.addAll(db.getBooksBeanFromAuthorById(itemId));
    }

    //
    private void getMoreFromPublisher() {
        toolbar.setTitle(db.getPublisherNameById(itemId));
        bookList.clear();
        bookList.addAll(db.getBooksBeanFromPublisherById(itemId));
    }

    //
    private void getMoreFromShelves() {
        toolbar.setTitle(db.getShelvesNameById(itemId));
        bookList.clear();
        bookList.addAll(db.getBooksBeanFromShelvesById(itemId));
    }

    //
    private void getMoreFromShelf() {
        toolbar.setTitle(db.getShelfNameById(itemId));
        bookList.clear();
        bookList.addAll(db.getBooksBeanFromShelfById(itemId));
    }

    //
    private void getMoreFromGenre() {
        toolbar.setTitle(itemGenre);
        bookList.clear();
        bookList.addAll(db.getBooksBeanFromGenre(itemGenre));
    }

    //
    private void setupRecycler() {
        toggleEmptyBooks(bookList);
        moreAdapter = new MoreAdapter(context, bookList, this);
        moreAdapter.setOnItemClickListener(new MoreAdapter.ClickListener() {
            @Override
            public void onItemClick(int position, View v) {
                // save RecyclerView state
                mBundleRecyclerViewState = new Bundle();
                listState = recyclerView.getLayoutManager().onSaveInstanceState();
                mBundleRecyclerViewState.putParcelable(KEY_RECYCLER_STATE, listState);
            }
        });
        moreAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                searchToggleEmptyBooks(bookList);
            }
        });
        // segun las preferencias del usuarios se muestran los libros en una list o en grid
        if(prefSettings.getBoolean("listype_select", false)) {
            recyclerView.setLayoutManager(new GridLayoutManager(context, 3));
        } else {
            mLayoutManager = new LinearLayoutManager(context);
            recyclerView.setLayoutManager(mLayoutManager);
        }
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(moreAdapter);
    }

    //
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_more_top, menu);
        edit = menu.findItem(R.id.action_more_edit);
        delete = menu.findItem(R.id.action_more_delete);
        if(itemType.equals("genre")) {
            edit.setVisible(false);
            delete.setVisible(false);
        }
        search = menu.findItem(R.id.action_search);
        searchView = (SearchView) search.getActionView();
        searchView.setImeOptions(EditorInfo.IME_ACTION_DONE);
        searchView.setQueryHint(getString(R.string.action_search));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                moreAdapter.getFilter().filter(s);
                return false;
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_more_edit:
                showDialogEdit();
                break;
            case R.id.action_more_delete:
                showDialogDelete();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    //
    private void showDialogEdit() {
        if(!itemType.equals("shelves") && !itemType.equals("shelf")) {
            MaterialAlertDialogBuilder editDialog = new MaterialAlertDialogBuilder(context, R.style.MaterialDialog);
            LayoutInflater inflater = LayoutInflater.from(context);
            View view = inflater.inflate(R.layout.dialog_more_edit, null);
            final TextInputLayout til_more_edit = view.findViewById(R.id.til_more_edit);
            TextInputEditText et_more_edit = view.findViewById(R.id.et_more_edit);
            et_more_edit.setText(itemName);
            editDialog.setView(view);
            editDialog.setTitle(String.format(getString(R.string.dialog_more_edit_title), itemName));
            editDialog.setPositiveButton(getString(R.string.dialog_more_edit_positive), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (!TextUtils.isEmpty(til_more_edit.getEditText().getText().toString())) {
                        String newItemText = til_more_edit.getEditText().getText().toString().trim();
                        saveEdit(newItemText);
                        dialog.cancel();
                    } else {
                        dialog.cancel();
                    }
                }
            });
            editDialog.setNegativeButton(R.string.dialog_more_edit_negative, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            editDialog.show();
        } else {
            Bundle bundle = new Bundle();
            bundle.putString("itemEdit", itemType);
            bundle.putInt("itemEditId", itemId);
            Navigation.findNavController(getActivity().getWindow().getDecorView().findViewById(R.id.nav_host_fragment)).navigate(R.id.action_global_nav_edit_shelves, bundle, null, null);
        }
    }

    //
    private void saveEdit(String newItemName) {
        switch (itemType) {
            case "author":
                if(newItemName.toLowerCase().equals(itemName.toLowerCase())) {
                    db.updateAuthorName(itemId, newItemName);
                    Snackbar.make(getActivity().getWindow().getDecorView().findViewById(android.R.id.content), getString(R.string.dialog_more_edit_edited), Snackbar.LENGTH_SHORT).show();
                    toolbar.setTitle(newItemName);
                } else {
                    if(db.getAuthorIdByName(newItemName) != 0) {
                        Log.e("A", "existe");
                        int newAuthorId = db.getAuthorIdByName(newItemName);
                        db.updateBookAuthor(newAuthorId, itemId);
                        db.deleteAuthor(itemId);
                        Navigation.findNavController(getActivity(), R.id.nav_host_fragment).popBackStack();
                        Snackbar.make(getActivity().getWindow().getDecorView().findViewById(android.R.id.content), getString(R.string.dialog_more_edit_edited), Snackbar.LENGTH_SHORT).show();
                    } else {
                        Log.e("A", "no existe");
                        db.updateAuthorName(itemId, newItemName);
                        Snackbar.make(getActivity().getWindow().getDecorView().findViewById(android.R.id.content), getString(R.string.dialog_more_edit_edited), Snackbar.LENGTH_SHORT).show();
                        toolbar.setTitle(newItemName);
                    }
                }
                break;
            case "publisher":
                if(newItemName.toLowerCase().equals(itemName.toLowerCase())) {
                    db.updatePublisherName(itemId, newItemName);
                    Snackbar.make(getActivity().getWindow().getDecorView().findViewById(android.R.id.content), getString(R.string.dialog_more_edit_edited), Snackbar.LENGTH_SHORT).show();
                    toolbar.setTitle(newItemName);
                } else {
                    if(db.getPublisherIdByName(newItemName) != 0) {
                        int newPublisherId = db.getPublisherIdByName(newItemName);
                        db.updateBookPublisher(newPublisherId, itemId);
                        db.deletePublisher(itemId);
                        Navigation.findNavController(getActivity(), R.id.nav_host_fragment).popBackStack();
                        Snackbar.make(getActivity().getWindow().getDecorView().findViewById(android.R.id.content), getString(R.string.dialog_more_edit_edited), Snackbar.LENGTH_SHORT).show();
                    } else {
                        db.updatePublisherName(itemId, newItemName);
                        Snackbar.make(getActivity().getWindow().getDecorView().findViewById(android.R.id.content), getString(R.string.dialog_more_edit_edited), Snackbar.LENGTH_SHORT).show();
                        toolbar.setTitle(newItemName);
                    }
                }
                break;
        }
        moreAdapter.notifyDataSetChanged();
        itemName = newItemName;
    }

    //
    private void showDialogDelete() {
        // mostrar un dialogo diciendo que se borrara el elemento y todos sus libros asociados
        MaterialAlertDialogBuilder deleteDialog = new MaterialAlertDialogBuilder(context, R.style.MaterialDialog);
        deleteDialog.setTitle(getString(R.string.dialog_more_delete_title));
        deleteDialog.setMessage(String.format(getString(R.string.dialog_more_delete_text), itemName));
        deleteDialog.setPositiveButton(getString(R.string.dialog_more_delete_positive), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteItem();
                dialog.cancel();
            }
        });
        deleteDialog.setNegativeButton(R.string.dialog_more_delete_negative, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        deleteDialog.show();
    }

    //
    private void deleteItem() {
        switch (itemType) {
            case "author":
                // borrar libros por autor y autor
                db.deleteBookByAuthorId(itemId);
                db.deleteAuthor(itemId);
                // se borran las editoriales asociadas al autor si no tienen mas autores
                List<Integer> publishers = db.getPublisherIdByAuthorId(itemId);
                for(int i = 0; i < publishers.size(); i++) {
                    if(db.getCountAuthorPublisherId(publishers.get(i)) == 0) {
                        db.deletePublisher(publishers.get(i));
                    }
                }
                if(db.getAllAuthors().size() == 0) {
                    db.deleteAll();
                }
                break;
            case "publisher":
                // borrar libros por editorial y editorial
                db.deleteBookByPublisherId(itemId);
                db.deletePublisher(itemId);
                // se borran los autores asociados a la editorial si no tienen mas editoriales
                List<Integer> authors = db.getAuthorIdbyPublisherId(itemId);
                for(int i = 0; i < authors.size(); i++) {
                    if(db.getCountPublishersByAuthorId(authors.get(i)) == 0) {
                        db.deleteAuthor(authors.get(i));
                    }
                }
                if(db.getAllPublishers().size() == 0) {
                    db.deleteAll();
                }
                break;
            case "shelves":
                // borrar autor en estanteria
                List<Integer> authorListShelves = new ArrayList<>(db.getAuthorIdByShelvesId(itemId));
                for(int i = 0; i < authorListShelves.size(); i++) {
                    db.deleteAuthor(authorListShelves.get(i));
                }
                // borrar editorial en estanteria
                List<Integer> publisherListShelves = new ArrayList<>(db.getPublisherIdByShelvesId(itemId));
                for(int i = 0; i < publisherListShelves.size(); i++) {
                    db.deletePublisher(publisherListShelves.get(i));
                }
                // borrar estante, estanteria y libros asociados
                db.deleteShelfByShelves(itemId);
                db.deleteBookByShelvesId(itemId);
                db.deleteShelves(itemId);
                break;
            case "shelf":
                // borrar autor en estante
                List<Integer> authorListShelf = new ArrayList<>(db.getAuthorIdByShelfId(itemId));
                for(int i = 0; i < authorListShelf.size(); i++) {
                    db.deleteAuthor(authorListShelf.get(i));
                }
                // borrar editorial en estante
                List<Integer> publisherListShelf = new ArrayList<>(db.getPublisherIdByShelfId(itemId));
                for(int i = 0; i < publisherListShelf.size(); i++) {
                    db.deletePublisher(publisherListShelf.get(i));
                }
                // comprobar si la estanteria tiene mas estantes y si no borrar
                List<Shelf> shelfList = new ArrayList<>(db.getShelfInShelvesByIdExcept(itemShelvesId, itemId));
                if(shelfList.size() == 0) {
                    db.deleteShelves(itemShelvesId);
                }
                // borrar estante y libros asociados
                db.deleteBookByShelfId(itemId);
                db.deleteShelf(itemId);
                break;
        }
        NavController navController = ((MainActivity)context).getMainController();
        navController.getPreviousBackStackEntry().getSavedStateHandle().set("update_home", getString(R.string.dialog_more_delete_deleted));
        navController.popBackStack();
    }

    //
    public void setupUpdateMore(String text) {
        Snackbar.make(getActivity().getWindow().getDecorView().findViewById(android.R.id.content), text, Snackbar.LENGTH_SHORT).show();
    }

    // se comprueba los libros que hay en la bd
    // si la lista esta vacia se vuelve al fragment anterior
    // o si el fragment anterior es Fragment_Details se vuelve a HomeFragment
    public void toggleEmptyBooks(List<BookBean> bookList) {
        if (bookList.isEmpty()) {
            if(getArguments() != null && getArguments().getString("fromDetails") != null) {
                NavController navController = ((MainActivity)context).getMainController();
                navController.popBackStack(R.id.nav_home, true);
                navController.navigate(R.id.action_global_nav_home);
            } else {
                Navigation.findNavController(getActivity(), R.id.nav_host_fragment).popBackStack();
            }
        }
    }

    // al buscar si no se encuentran libros se mostrara un mensaje
    private void searchToggleEmptyBooks(List<BookBean> bookList) {
        if (!bookList.isEmpty()) {
            cv_info_no_books.setVisibility(View.GONE);
        } else {
            cv_info_no_books.setVisibility(View.VISIBLE);
        }
    }

    // onResume
    @Override
    public void onResume() {
        super.onResume();
        // recuperar estado RecyclerView
        if (mBundleRecyclerViewState != null) {
            listState = mBundleRecyclerViewState.getParcelable(KEY_RECYCLER_STATE);
            recyclerView.getLayoutManager().onRestoreInstanceState(listState);
            mBundleRecyclerViewState.putParcelable(KEY_RECYCLER_STATE, null);
        }
    }

}
