package com.hitglynorthz.mybookshelf.ui.crud;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;

import com.google.android.material.chip.Chip;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputLayout;
import com.hitglynorthz.mybookshelf.MainActivity;
import com.hitglynorthz.mybookshelf.R;
import com.hitglynorthz.mybookshelf.db.DBHelper;
import com.hitglynorthz.mybookshelf.db.Shelves;
import com.hitglynorthz.mybookshelf.utils.Utils;

import java.util.ArrayList;
import java.util.List;

public class Fragment_EditShelves extends Fragment {
    private View rootView;
    private Context context;

    private Toolbar toolbar;
    private FloatingActionButton eds_fab;

    private TextInputLayout til_shelves_title, til_shelf_title;
    private MaterialAutoCompleteTextView et_shelves_title, et_shelf_title;
    private LinearLayout ll_shelf;
    private TextView tv_edit_info;

    private String shelvesName, shelfName;
    private int shelvesId, shelfId;

    private Shelves lastShelves;

    private String itemEdit;
    private int itemEditId, shelvesIdOld, shelfIdOld;
    private String shelvesNameOld, shelfNameOld;

    private DBHelper db;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView =  inflater.inflate(R.layout.fragment_edit_shelves, container, false);
        context = getContext();
        db = DBHelper.getInstance(context);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        initViews();
        initFormFields();

        // recuperamos los datos que se envian al fragment
        if (getArguments() != null) {
            itemEdit = getArguments().getString("itemEdit");
            itemEditId = getArguments().getInt("itemEditId");
            toolbar.setTitle(getString(R.string.nav_add_shelves_edit_title));
            setEditData();
        }

        eds_fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFormValues();
            }
        });

        return rootView;
    }

    //
    private void initViews() {
        toolbar = this.getActivity().findViewById(R.id.toolbar);
        eds_fab = rootView.findViewById(R.id.eds_fab);
        ll_shelf = rootView.findViewById(R.id.ll_shelf);
        tv_edit_info = rootView.findViewById(R.id.tv_edit_info);
    }

    //
    private void initFormFields() {
        // shelves
        til_shelves_title = rootView.findViewById(R.id.til_shelves_title);
        et_shelves_title = rootView.findViewById(R.id.et_shelves_title);
        et_shelves_title.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                et_shelves_title.setError(null);
                if(s.length() > 0) {
                    et_shelf_title.setFocusable(true);
                    et_shelf_title.setFocusableInTouchMode(true);
                    et_shelf_title.setEnabled(true);
                } else {
                    et_shelf_title.setFocusable(false);
                    et_shelf_title.setFocusableInTouchMode(true);
                    et_shelf_title.setEnabled(false);
                }
                et_shelf_title.getText().clear();
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });
        List<String> shelvesList = new ArrayList<>(db.getListShelves());
        ArrayAdapter<String> adapterShelves = new ArrayAdapter<String>(context, android.R.layout.simple_dropdown_item_1line, shelvesList);
        et_shelves_title.setThreshold(1);
        et_shelves_title.setAdapter(adapterShelves);
        // shelfs
        til_shelf_title = rootView.findViewById(R.id.til_shelf_title);
        et_shelf_title = rootView.findViewById(R.id.et_shelf_title);
        et_shelf_title.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                et_shelf_title.setError(null);
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });
        et_shelf_title.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus) {
                    //si tiene el focus
                    if(db.getShelvesIdByName(til_shelves_title.getEditText().getText().toString()) != 0) {
                        int currentShelvesId = db.getShelvesIdByName(til_shelves_title.getEditText().getText().toString());
                        List<String> shelfList = new ArrayList<>();
                        shelfList.addAll(db.getListShelf(currentShelvesId));
                        ArrayAdapter<String> adapterShelf = new ArrayAdapter<String>(context, android.R.layout.simple_dropdown_item_1line, shelfList);
                        et_shelf_title.setThreshold(1);
                        et_shelf_title.setAdapter(adapterShelf);
                    } else {
                        et_shelf_title.setAdapter(null);
                    }
                }
            }
        });
    }

    //
    private void setEditData() {
        switch (itemEdit) {
            case "shelves":
                shelvesIdOld = itemEditId;
                shelvesNameOld = db.getShelvesNameById(shelvesIdOld);
                et_shelves_title.setText(shelvesNameOld);
                et_shelf_title.setFocusable(false);
                et_shelf_title.setFocusableInTouchMode(true);
                et_shelf_title.setEnabled(false);
                tv_edit_info.setText(getString(R.string.edit_shelves_text_info));
                break;
            case "shelf":
                et_shelves_title.setFocusable(false);
                et_shelves_title.setFocusableInTouchMode(true);
                et_shelves_title.setEnabled(false);
                shelfIdOld = itemEditId;
                shelfNameOld = db.getShelfNameById(shelfIdOld);
                shelvesIdOld = db.getShelvesByShelfId(shelfIdOld);
                shelvesNameOld = db.getShelvesNameById(shelvesIdOld);
                et_shelves_title.setText(shelvesNameOld);
                et_shelf_title.setText(shelfNameOld);
                tv_edit_info.setText(getString(R.string.edit_shelf_text_info));
                ll_shelf.setVisibility(View.VISIBLE);
                break;
        }
    }

    //
    private void getFormValues() {
        if(!TextUtils.isEmpty(til_shelves_title.getEditText().getText().toString())) {
            shelvesName = til_shelves_title.getEditText().getText().toString().trim();
            til_shelves_title.setErrorEnabled(false);
        } else {
            til_shelves_title.setError(getString(R.string.add_shelves_name) + getString(R.string.add_shelves_error_til));
        }
        if(!TextUtils.isEmpty(til_shelf_title.getEditText().getText().toString())) {
            shelfName = til_shelf_title.getEditText().getText().toString().trim();
        }
        updateData();
    }

    //
    private void updateData() {
        switch (itemEdit) {
            case "shelves":
                if(shelvesName.toLowerCase().equals(shelvesNameOld.toLowerCase())) {
                    db.updateShelvesName(shelvesIdOld, shelvesName);
                    NavController navController = ((MainActivity)context).getMainController();
                    navController.getPreviousBackStackEntry().getSavedStateHandle().set("update_home", getString(R.string.add_shelves_edit_edited));
                    navController.popBackStack();
                } else {
                    if(db.getShelvesIdByName(shelvesName) == 0) {
                        db.updateShelvesName(shelvesIdOld, shelvesName);
                        NavController navController = ((MainActivity)context).getMainController();
                        navController.getPreviousBackStackEntry().getSavedStateHandle().set("update_home", getString(R.string.add_shelves_edit_edited));
                        navController.popBackStack();
                    } else {
                        ll_shelf.setVisibility(View.VISIBLE);
                        shelvesId = db.getShelvesIdByName(shelvesName);
                        if(!TextUtils.isEmpty(shelfName)) {
                            til_shelf_title.setErrorEnabled(false);
                            et_shelf_title.setFocusable(true);
                            et_shelf_title.setFocusableInTouchMode(true);
                            et_shelf_title.setEnabled(true);
                            if(db.getShelfIdByNameAndShelves(shelfName, shelvesId) == 0) {
                                shelfId = (int) db.insertShelf(shelfName, shelvesId);
                            } else {
                                shelfId = db.getShelfIdByNameAndShelves(shelfName, shelvesId);
                            }
                            db.updateBookShelves(shelvesId, shelvesIdOld, shelfId);
                            if(db.getBooksBeanFromShelvesById(shelvesIdOld).size() == 0) {
                                db.deleteShelfByShelves(shelvesIdOld);
                                db.deleteShelves(shelvesIdOld);
                            }
                            NavController navController = ((MainActivity)context).getMainController();
                            navController.getPreviousBackStackEntry().getSavedStateHandle().set("update_home", getString(R.string.add_shelves_edit_edited));
                            navController.popBackStack();
                        } else {
                            til_shelf_title.setError(getString(R.string.add_shelf_name) + getString(R.string.add_shelves_error_til));
                            Snackbar.make(getActivity().getWindow().getDecorView().findViewById(android.R.id.content), getString(R.string.add_book_error), Snackbar.LENGTH_SHORT).setAnchorView(eds_fab).show();
                        }
                    }
                }
                break;
            case "shelf":
                if(shelfName.equals(shelfNameOld)) {
                    shelfId = shelfIdOld;
                } else {
                    if(db.getShelfIdByNameAndShelves(shelfName, shelvesIdOld) == 0) {
                        shelfId = (int) db.insertShelf(shelfName, shelvesIdOld);
                    } else {
                        shelfId = db.getShelfIdByNameAndShelves(shelfName, shelvesIdOld);
                    }
                }
                db.updateBookShelvesShelf(shelvesIdOld, shelvesIdOld, shelfId, shelfIdOld);
                if(db.getBooksBeanFromShelfById(shelfIdOld).size() == 0) {
                    db.deleteShelf(shelfIdOld);
                }
                NavController navController = ((MainActivity)context).getMainController();
                navController.getPreviousBackStackEntry().getSavedStateHandle().set("update_home", getString(R.string.add_shelves_edit_edited));
                navController.popBackStack();
                break;
        }
    }

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
