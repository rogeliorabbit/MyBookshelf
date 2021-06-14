package com.hitglynorthz.mybookshelf.ui.toplevels;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.hitglynorthz.mybookshelf.R;
import com.hitglynorthz.mybookshelf.adapters.GenresAdapter;
import com.hitglynorthz.mybookshelf.db.DBHelper;
import com.hitglynorthz.mybookshelf.utils.Utils;

import java.util.ArrayList;
import java.util.List;

public class GenresFragment extends Fragment {
    private View rootView;
    private Context context;

    private FloatingActionButton main_fab;

    private GenresAdapter genresAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private List<String> genresList = new ArrayList<>();
    private RecyclerView recyclerView;
    private MaterialCardView cv_info_no_genres;

    private DBHelper db;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_genres, container, false);
        context = getContext();
        db = DBHelper.getInstance(context);

        initViews();

        setupRecycler();
        initRecycler();

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

        cv_info_no_genres = rootView.findViewById(R.id.cv_info_no_genres);
    }

    //
    private void initRecycler() {
        genresList.clear();
        genresList.addAll(db.getAllGenres());
        genresAdapter = new GenresAdapter(context, genresList);
        mLayoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(genresAdapter);

        toggleEmptyGenres();
    }

    //
    private void toggleEmptyGenres() {
        if (!db.getAllGenres().isEmpty()) {
            cv_info_no_genres.setVisibility(View.GONE);
        } else {
            cv_info_no_genres.setVisibility(View.VISIBLE);
        }
    }

    //
    @Override
    public void onResume() {
        super.onResume();
        initRecycler();
    }
}
