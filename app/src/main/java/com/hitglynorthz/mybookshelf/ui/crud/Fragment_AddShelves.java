package com.hitglynorthz.mybookshelf.ui.crud;

import android.content.Context;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.hitglynorthz.mybookshelf.MainActivity;
import com.hitglynorthz.mybookshelf.R;
import com.hitglynorthz.mybookshelf.db.DBHelper;
import com.hitglynorthz.mybookshelf.db.Shelves;
import com.hitglynorthz.mybookshelf.utils.Utils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.google.android.material.textfield.TextInputLayout.END_ICON_DROPDOWN_MENU;

public class Fragment_AddShelves extends Fragment {
    private View rootView;
    private Context context;

    private Toolbar toolbar;
    private FloatingActionButton add_fab;

    private TextInputLayout til_shelves_title;
    private MaterialAutoCompleteTextView et_shelves_title;
    private Chip chip_last_shelves;
    private MaterialButton bt_addShelf;
    private TextView tv_count_shelves;
    private LinearLayout ll_shelf;

    private String shelvesName, shelfName;
    private int shelvesId, shelfId;

    private int numShelf = 0;
    private List<TextInputEditText> tietList = new ArrayList<>();
    private String[] shelfStrings;

    private Shelves lastShelves;

    private DBHelper db;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView =  inflater.inflate(R.layout.fragment_add_shelves, container, false);
        context = getContext();
        db = DBHelper.getInstance(context);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        initViews();
        initFormFields();

        add_fab.setOnClickListener(new View.OnClickListener() {
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
        add_fab = rootView.findViewById(R.id.add_fab);
        tv_count_shelves = rootView.findViewById(R.id.tv_count_shelves);
    }

    //
    private void initFormFields() {
        // chips
        chip_last_shelves = rootView.findViewById(R.id.chip_last_shelves);
        // shelves
        til_shelves_title = rootView.findViewById(R.id.til_shelves_title);
        et_shelves_title = rootView.findViewById(R.id.et_shelves_title);
        List<String> shelvesList = new ArrayList<>(db.getListShelves());
        ArrayAdapter<String> adapterShelves = new ArrayAdapter<String>(context, android.R.layout.simple_dropdown_item_1line, shelvesList);
        et_shelves_title.setThreshold(1);
        et_shelves_title.setAdapter(adapterShelves);
        if(db.getListShelves().size() > 0) {
            lastShelves = db.getLastShelves();
            chip_last_shelves.setText(lastShelves.getShelves_name());
            chip_last_shelves.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    et_shelves_title.setText(chip_last_shelves.getText());
                }
            });
        } else {
            chip_last_shelves.setVisibility(View.GONE);
        }
        //
        ll_shelf = rootView.findViewById(R.id.ll_shelf);
        bt_addShelf = rootView.findViewById(R.id.bt_addShelf);
        bt_addShelf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(numShelf <= 10) {
                    numShelf++;
                    tv_count_shelves.setText("#" + numShelf);
                    addShelf();
                } else {
                    Snackbar.make(getActivity().getWindow().getDecorView().findViewById(android.R.id.content), getString(R.string.add_shelves_shelf_limit), Snackbar.LENGTH_SHORT).setAnchorView(add_fab).show();
                }
            }
        });
    }

    // crear dinamicamente los TextInputLayout
    private void addShelf() {
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams. MATCH_PARENT ,
                LinearLayout.LayoutParams. WRAP_CONTENT ) ;
        layoutParams.setMargins(0, 0, 0, 16);
        final TextInputLayout til = new TextInputLayout(rootView.getContext());
        til.setBoxBackgroundMode(TextInputLayout.BOX_BACKGROUND_FILLED);
        til.setHint(getString(R.string.add_shelf_name));
        til.setStartIconDrawable(R.drawable.ic_shelf_tint);
        til.setHintTextColor(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.colorSecondary)));
        til.setEndIconMode(TextInputLayout.END_ICON_CUSTOM);
        til.setEndIconDrawable(R.drawable.ic_remove_black);
        til.setEndIconOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ll_shelf.removeView(til);
                numShelf--;
                tv_count_shelves.setText("#" + numShelf);
            }
        });
        TextInputEditText tiet = new TextInputEditText(til.getContext());
        tiet.setImeOptions(EditorInfo.IME_ACTION_DONE);
        tiet.setInputType(InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
        tiet.setSingleLine();
        tiet.setBackground(null);
        try {
            // https://stackoverflow.com/a/26544231/7619127
            Field f = TextView.class.getDeclaredField("mCursorDrawableRes");
            f.setAccessible(true);
            f.set(tiet, R.drawable.color_cursor);
        } catch (Exception ignored) {
        }
        til.addView(tiet);
        ll_shelf.addView(til, layoutParams);
        tietList.add(tiet);
    }

    //
    private void getFormValues() {
        // shelves
        if(!TextUtils.isEmpty(til_shelves_title.getEditText().getText().toString())) {
            shelvesName = til_shelves_title.getEditText().getText().toString().trim();
            til_shelves_title.setErrorEnabled(false);
        } else {
            til_shelves_title.setError(getString(R.string.add_shelves_name) + getString(R.string.add_shelves_error_til));
        }
        // shelf
        shelfStrings = new String[tietList.size()];
        for(int i = 0; i < tietList.size(); i++) {
            shelfStrings[i] = tietList.get(i).getText().toString().trim();
        }
        //
        if(!TextUtils.isEmpty(shelvesName)) {
            saveData();
        } else {
            Snackbar.make(getActivity().getWindow().getDecorView().findViewById(android.R.id.content), getString(R.string.add_book_error), Snackbar.LENGTH_SHORT).setAnchorView(add_fab).show();
        }
    }

    //
    private void saveData() {
        // check Shelves
        shelvesId = db.getShelvesIdByName(shelvesName);
        if(shelvesId == 0) {
            // si no existe se crea uno
            shelvesId = (int) db.insertShelves(shelvesName);
        }
        // check shelf
        for (String shelfName : shelfStrings) {
            if(!shelfName.equals("")) {
                shelfId = db.getShelfIdByNameAndShelves(shelfName, shelvesId);
                if(shelfId == 0) {
                    // si no existe se crea uno
                    shelfId = (int) db.insertShelf(shelfName, shelvesId);
                }
            }
        }
        NavController navController = ((MainActivity)context).getMainController();
        navController.getPreviousBackStackEntry().getSavedStateHandle().set("update_home", getString(R.string.add_shelves_added));
        navController.popBackStack();
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
