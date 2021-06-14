package com.hitglynorthz.mybookshelf.ui.bottomlevels;

import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.view.inputmethod.EditorInfo;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.PopupMenu;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.chip.Chip;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.hitglynorthz.mybookshelf.MainActivity;
import com.hitglynorthz.mybookshelf.R;
import com.hitglynorthz.mybookshelf.adapters.ShelvesAdapter;
import com.hitglynorthz.mybookshelf.db.DBHelper;
import com.hitglynorthz.mybookshelf.db.Shelves;
import com.hitglynorthz.mybookshelf.ui.toplevels.HomeFragment;
import com.hitglynorthz.mybookshelf.utils.Utils;

import java.util.ArrayList;
import java.util.List;

public class Fragment_Shelves extends Fragment {
    private View rootView;
    private Context context;

    private Toolbar toolbar;
    private AppBarLayout appBarLayout;
    private BottomNavigationView bottomNavigationView;
    private FloatingActionButton bfab;

    private SearchView searchView;
    private MenuItem search, sort;

    private ShelvesAdapter shelvesAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private List<Shelves> shelvesList = new ArrayList<>();
    private RecyclerView recyclerView;
    private MaterialCardView cv_info_no_shelves;

    private LinearLayout ll_filter;
    private Chip chip_filter;

    private final String KEY_RECYCLER_STATE = "recycler_state";
    private static Bundle mBundleRecyclerViewState;
    private Parcelable listState;

    private DBHelper db;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_shelves, container, false);
        context = getContext();
        db = DBHelper.getInstance(context);

        initViews();
        setupLayout();

        setupRecycler();
        initRecycler(0);

        return rootView;
    }

    // inicializamos las vistas
    private void initViews() {
        toolbar = this.getActivity().findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.bnav_shelves));
        appBarLayout = this.getActivity().findViewById(R.id.appBarLayout);
        bottomNavigationView = this.getActivity().findViewById(R.id.bottomNavigationView);
        bfab = HomeFragment.getInstance().getBfab();
    }

    // configuracion del layout
    private void setupLayout() {
        // si se pulsa en el item Estanterias del bottomnavigationview
        // se hace scroll al principio
        bottomNavigationView = this.getActivity().findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnNavigationItemReselectedListener(new BottomNavigationView.OnNavigationItemReselectedListener() {
            @Override
            public void onNavigationItemReselected(@NonNull MenuItem item) {
                int itemId = item.getItemId();
                if(itemId == R.id.bnav_shelves) {
                    recyclerView.smoothScrollToPosition(0);
                    appBarLayout.setExpanded(true, false);
                }
            }
        });
    }

    // configuracion del recyclerview
    private void setupRecycler() {
        recyclerView = rootView.findViewById(R.id.recyclerView);
        // no permitir la doble seleccion de items
        recyclerView.setMotionEventSplittingEnabled(false);
        Utils.fabScrollBehavior(recyclerView, bfab);

        cv_info_no_shelves = rootView.findViewById(R.id.cv_info_no_shelves);
        ll_filter = rootView.findViewById(R.id.ll_filter);
        chip_filter = rootView.findViewById(R.id.chip_filter);
        chip_filter.setOnCloseIconClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ll_filter.setVisibility(View.GONE);
                initRecycler(0);
                animRecycler();
            }
        });
    }

    // inicializamos el recyclerview
    private void initRecycler(int sort) {
        shelvesList.clear();
        switch (sort) {
            case 0:
                shelvesList.addAll(db.getAllShelvesTimestamp());
                ll_filter.setVisibility(View.GONE);
                break;
            case 1:
                shelvesList.addAll(db.getAllShelvesAZ());
                ll_filter.setVisibility(View.VISIBLE);
                chip_filter.setText(R.string.action_popup_sort_shelves_alpha);
                break;
            case 2:
                shelvesList.addAll(db.getAllShelvesNBooks());
                ll_filter.setVisibility(View.VISIBLE);
                chip_filter.setText(R.string.action_popup_sort_shelves_nbooks);
                break;
        }
        shelvesAdapter = new ShelvesAdapter(context, shelvesList, this);
        shelvesAdapter.setOnItemClickListener(new ShelvesAdapter.ClickListener() {
            @Override
            public void onItemClick(int position, View v) {
                // save RecyclerView state
                mBundleRecyclerViewState = new Bundle();
                listState = recyclerView.getLayoutManager().onSaveInstanceState();
                mBundleRecyclerViewState.putParcelable(KEY_RECYCLER_STATE, listState);
            }
        });
        shelvesAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                toggleEmptyBooksShelves(shelvesList);
            }
        });
        mLayoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(shelvesAdapter);

        toggleEmptyBooksShelves(shelvesList);
    }

    // se comprueba el numero de Estanterias
    public void toggleEmptyBooksShelves(List<Shelves> shelvesList) {
        if (!shelvesList.isEmpty()) {
            cv_info_no_shelves.setVisibility(View.GONE);
        } else {
            cv_info_no_shelves.setVisibility(View.VISIBLE);
        }
    }

    // animacion del recyclerview
    private void animRecycler() {
        final Context context = recyclerView.getContext();
        final LayoutAnimationController controller = AnimationUtils.loadLayoutAnimation(context, R.anim.layout_anim_fall_down);
        recyclerView.setLayoutAnimation(controller);
        recyclerView.getAdapter().notifyDataSetChanged();
        recyclerView.scheduleLayoutAnimation();
    }

    @Override
    public void onCreateOptionsMenu(final Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_main, menu);
        search = menu.findItem(R.id.action_search);
        searchView = (SearchView) search.getActionView();
        sort = menu.findItem(R.id.action_sort);
        if(db.getAllBooksBean().isEmpty()) {
            search.setVisible(false);
            sort.setVisible(false);
        } else {
            search.setVisible(true);
            sort.setVisible(true);
        }
        searchView.setImeOptions(EditorInfo.IME_ACTION_DONE);
        searchView.setQueryHint(getString(R.string.action_search));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                shelvesAdapter.getFilter().filter(s);
                return false;
            }
        });
        search.setOnActionExpandListener(new MenuItem.OnActionExpandListener(){
            @Override
            public boolean onMenuItemActionCollapse(MenuItem menuItem) {
                setItemsVisibility(menu, search, true);
                return true;
            }

            @Override
            public boolean onMenuItemActionExpand(MenuItem menuItem) {
                setItemsVisibility(menu, search, false);
                return true;
            }
        });
    }
    private void setItemsVisibility(Menu menu, MenuItem exception, boolean visible) {
        for (int i = 0; i < menu.size(); ++i) {
            MenuItem item = menu.getItem(i);
            if (item != exception) item.setVisible(visible);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_sort:
                showPopupMenu();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    // popupmenu
    private void showPopupMenu() {
        View menuItemView = getActivity().findViewById(R.id.action_sort);
        PopupMenu popupMenu = new PopupMenu(context, menuItemView);
        popupMenu.inflate(R.menu.menu_popup_sort_shelves);
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                int id = item.getItemId();
                switch (id) {
                    case R.id.action_popup_sort_shelves_timestamp:
                        initRecycler(0);
                        animRecycler();
                        break;
                    case R.id.action_popup_sort_shelves_alpha:
                        initRecycler(1);
                        animRecycler();
                        break;
                    case R.id.action_popup_sort_shelves_nbooks:
                        initRecycler(2);
                        animRecycler();
                        break;
                }
                // si el fab esta oculto lo mostramos
                if(!bfab.isShown()) {
                    bfab.show();
                }
                return true;
            }
        });
        popupMenu.show();
    }

    // onResume
    @Override
    public void onResume() {
        super.onResume();
        // volver a recuperar datos db
        initRecycler(0);
        // recuperar estado RecyclerView
        if (mBundleRecyclerViewState != null) {
            listState = mBundleRecyclerViewState.getParcelable(KEY_RECYCLER_STATE);
            recyclerView.getLayoutManager().onRestoreInstanceState(listState);
            mBundleRecyclerViewState.putParcelable(KEY_RECYCLER_STATE, null);
        }
    }

}
