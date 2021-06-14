package com.hitglynorthz.mybookshelf.ui.toplevels;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.hitglynorthz.mybookshelf.R;
import com.hitglynorthz.mybookshelf.adapters.WishlistAdapter;
import com.hitglynorthz.mybookshelf.db.DBHelper;
import com.hitglynorthz.mybookshelf.db.Wishlist;
import com.hitglynorthz.mybookshelf.utils.Utils;

import java.util.ArrayList;
import java.util.List;

public class WishlistFragment extends Fragment {
    private View rootView;
    private Context context;

    private FloatingActionButton main_fab;

    private WishlistAdapter wishlistAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private List<Wishlist> wishlistList = new ArrayList<>();
    private RecyclerView recyclerView;
    private MaterialCardView cv_info_no_wishlist;

    private DBHelper db;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView =  inflater.inflate(R.layout.fragment_wishlist, container, false);
        context = getContext();
        db = DBHelper.getInstance(context);

        initViews();

        setupRecycler();
        initRecycler();

        main_fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(getActivity().getWindow().getDecorView().findViewById(R.id.nav_host_fragment)).navigate(R.id.action_global_nav_add_wishlist);
            }
        });

        return rootView;
    }

    //
    private void initViews() {
        main_fab = this.getActivity().findViewById(R.id.main_fab);
    }

    //
    private void setupRecycler() {
        recyclerView = rootView.findViewById(R.id.recyclerView);
        recyclerView.setMotionEventSplittingEnabled(false); // no permitir la doble seleccion de items
        Utils.fabScrollBehavior(recyclerView, main_fab);

        cv_info_no_wishlist = rootView.findViewById(R.id.cv_info_no_wishlist);
    }

    //
    private void initRecycler() {
        wishlistList.clear();
        wishlistList.addAll(db.getAllWishlist());
        wishlistAdapter = new WishlistAdapter(context, wishlistList);
        mLayoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(wishlistAdapter);

        toggleEmptyWishlist();
    }

    //
    private void toggleEmptyWishlist() {
        if (!db.getAllWishlist().isEmpty()) {
            cv_info_no_wishlist.setVisibility(View.GONE);
        } else {
            cv_info_no_wishlist.setVisibility(View.VISIBLE);
        }
    }

    //
    @Override
    public void onResume() {
        super.onResume();
        initRecycler();
    }
}
