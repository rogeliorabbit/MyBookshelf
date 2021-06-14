package com.hitglynorthz.mybookshelf.ui.crud;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.hitglynorthz.mybookshelf.R;
import com.hitglynorthz.mybookshelf.db.DBHelper;
import com.hitglynorthz.mybookshelf.db.Wishlist;
import com.hitglynorthz.mybookshelf.utils.Utils;

public class Fragment_AddWishlist extends Fragment {
    private View rootView;
    private Context context;

    private FloatingActionButton main_fab;

    private TextInputLayout til_title, til_author, til_publisher, til_isbn;
    private TextInputEditText et_title, et_author, et_publisher, et_isbn;

    private String title, author, publisher, isbn;

    private int id_wishlist;
    private String edit_wishlist;
    private Wishlist wishlist;

    private DBHelper db;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView =  inflater.inflate(R.layout.fragment_add_wishlist, container, false);
        context = getContext();
        db = DBHelper.getInstance(context);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        initFormFields();

        // recuperamos los datos que se envian al fragment
        if (getArguments() != null) {
            id_wishlist = getArguments().getInt("wishlist_id");
            edit_wishlist = getArguments().getString("edit_wishlist");
            wishlist = db.getWishlist(id_wishlist);
            setWislistData();
        }

        main_fab = this.getActivity().findViewById(R.id.main_fab);
        main_fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFormValues();
            }
        });

        return rootView;
    }

    //
    private void initFormFields() {
        til_title = rootView.findViewById(R.id.til_title);
        et_title = rootView.findViewById(R.id.et_title);
        et_title.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                til_title.setError(null);
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });
        til_author = rootView.findViewById(R.id.til_author);
        et_author = rootView.findViewById(R.id.et_author);
        til_publisher = rootView.findViewById(R.id.til_publisher);
        et_publisher = rootView.findViewById(R.id.et_publisher);
        til_isbn = rootView.findViewById(R.id.til_isbn);
        et_isbn = rootView.findViewById(R.id.et_isbn);
    }

    //
    private void setWislistData() {
        et_title.setText(wishlist.getTitle());
        et_author.setText(wishlist.getAuthor());
        et_publisher.setText(wishlist.getPublisher());
        et_isbn.setText(wishlist.getIsbn());
    }

    //
    private void getFormValues() {
        // title
        if(!TextUtils.isEmpty(til_title.getEditText().getText().toString())) {
            title = til_title.getEditText().getText().toString().trim();
            til_title.setErrorEnabled(false);
        } else {
            til_title.setError(getString(R.string.add_book_title) + getString(R.string.add_book_error_til));
        }
        // author
        if(!TextUtils.isEmpty(til_author.getEditText().getText().toString())) {
            author = til_author.getEditText().getText().toString().trim();
        } else {
            author = getString(R.string.add_wishlist_no_author);
        }
        // publisher
        if(!TextUtils.isEmpty(til_publisher.getEditText().getText().toString())) {
            publisher = til_publisher.getEditText().getText().toString().trim();
        } else {
            publisher = getString(R.string.add_wishlist_no_publisher);
        }
        // isbn
        isbn = til_isbn.getEditText().getText().toString();
        // comprobar campos requeridos
        if(!TextUtils.isEmpty(title)) {
            // check data
            saveWishlist();
        } else {
            Snackbar.make(getActivity().getWindow().getDecorView().findViewById(android.R.id.content), getString(R.string.add_wishlist_error), Snackbar.LENGTH_SHORT).setAnchorView(main_fab).show();
        }
    }

    //
    private void saveWishlist() {
        if(getArguments() != null && edit_wishlist.equals("edit")) {
            db.updateWIshlist(id_wishlist, title, author, publisher, isbn);
            Snackbar.make(getActivity().getWindow().getDecorView().findViewById(android.R.id.content), String.format(getString(R.string.add_wishlist_saved_ok), title), Snackbar.LENGTH_SHORT).setAnchorView(main_fab).show();
            Navigation.findNavController(getActivity(), R.id.nav_host_fragment).popBackStack();
        } else {
            long newWishlistId = db.insertWishlist(title, author, publisher, isbn);
            Wishlist newWishlist = db.getWishlist(newWishlistId);
            if(newWishlist != null) {
                Snackbar.make(getActivity().getWindow().getDecorView().findViewById(android.R.id.content), String.format(getString(R.string.add_wishlist_saved_ok), title), Snackbar.LENGTH_SHORT).setAnchorView(main_fab).show();
                Navigation.findNavController(getActivity(), R.id.nav_host_fragment).popBackStack();
            }
        }
    }

    // onResume
    @Override
    public void onResume() {
        super.onResume();
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
    }

    // onPause
    @Override
    public void onPause() {
        super.onPause();
        // ocultamos el teclado si esta abierto
        Utils.hideSoftKeyboard(getActivity());
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
    }
}

