package com.hitglynorthz.mybookshelf.ui.crud;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.hitglynorthz.mybookshelf.MainActivity;
import com.hitglynorthz.mybookshelf.R;
import com.hitglynorthz.mybookshelf.db.Book;
import com.hitglynorthz.mybookshelf.db.DBHelper;
import com.hitglynorthz.mybookshelf.utils.Barcode;
import com.hitglynorthz.mybookshelf.utils.Search_GISBN;
import com.hitglynorthz.mybookshelf.utils.Search_OISBN;
import com.hitglynorthz.mybookshelf.utils.Utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static androidx.preference.PreferenceManager.getDefaultSharedPreferences;

public class Fragment_EditBook extends Fragment {
    private View rootView;
    private Context context;

    private FloatingActionButton edb_fab;

    private int bookId;
    private Book book;

    private ImageView iv_cover;
    private TextInputLayout til_title, til_author, til_publisher, til_isbn, til_genre,
            til_year, til_pages, til_lang, til_shelves, til_shelf, til_descp;
    private TextInputEditText et_title, et_isbn, et_genre, et_year, et_pages, et_lang, et_descp;
    private MaterialAutoCompleteTextView et_author, et_publisher, et_shelves, et_shelf;
    private ChipGroup chipgroup_read_status;
    private Chip chip_read_status_1, chip_read_status_2, chip_read_status_3;
    private Button btn_search_isbn_info;

    private String title, authorName, authorOld, publisherName, publisherOld, genre, lang, cover = "", isbn, descp, web_link = "", shelvesName, shelvesOld, shelfName, shelfOld;
    private int year, pages, read_status;
    private int authorId, publisherId, shelvesId, shelfId;
    private Bitmap photo;
    private int fromCam = 0;

    private SharedPreferences prefSettings;

    private static int CAMERA_REQUEST = 713;
    private static int BARCODE_REQUEST = 843;
    private static int PHOTOFOLDER_REQUEST = 807;

    private DBHelper db;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView =  inflater.inflate(R.layout.fragment_edit_book, container, false);
        context = getContext();
        db = DBHelper.getInstance(context);
        prefSettings = getDefaultSharedPreferences(context);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        initViews();
        initFormFields();

        // recuperamos los datos que se envian al fragment
        if (getArguments() != null) {
            bookId = getArguments().getInt("id_book");
            book = db.getBook(bookId);
            setBookData();
        }

        edb_fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFormValues();
            }
        });

        return rootView;
    }

    //
    private void initViews() {
        edb_fab = rootView.findViewById(R.id.edb_fab);
    }

    //
    private void initFormFields() {
        // cover
        iv_cover = rootView.findViewById(R.id.iv_cover);
        // TextInputLayout
        til_title = rootView.findViewById(R.id.til_title);
        til_author = rootView.findViewById(R.id.til_author);
        til_publisher = rootView.findViewById(R.id.til_publisher);
        til_isbn = rootView.findViewById(R.id.til_isbn);
        til_genre = rootView.findViewById(R.id.til_genre);
        til_year = rootView.findViewById(R.id.til_year);
        til_pages = rootView.findViewById(R.id.til_pages);
        til_lang = rootView.findViewById(R.id.til_lang);
        til_shelves = rootView.findViewById(R.id.til_shelves);
        til_shelf = rootView.findViewById(R.id.til_shelf);
        til_descp = rootView.findViewById(R.id.til_descp);
        // TextInputEditText & MaterialAutoCompleteTextView
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
        et_author = rootView.findViewById(R.id.et_author);
        et_author.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                til_author.setError(null);
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });
        List<String> authorList = new ArrayList<>(db.getListAuthors());
        ArrayAdapter<String> adapterAuthor = new ArrayAdapter<String>(context, android.R.layout.simple_dropdown_item_1line, authorList);
        et_author.setThreshold(1);
        et_author.setAdapter(adapterAuthor);
        et_publisher = rootView.findViewById(R.id.et_publisher);
        et_publisher.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                til_publisher.setError(null);
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });
        List<String> publisherList = new ArrayList<>(db.getListPublishers());
        ArrayAdapter<String> adapterPublisher = new ArrayAdapter<String>(context, android.R.layout.simple_dropdown_item_1line, publisherList);
        et_publisher.setThreshold(1);
        et_publisher.setAdapter(adapterPublisher);
        et_isbn = rootView.findViewById(R.id.et_isbn);
        et_isbn.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // el isbn tiene una longitud de 10 o 13
                if(s.length() > 9) {
                    btn_search_isbn_info.setClickable(true);
                    btn_search_isbn_info.setEnabled(true);
                } else {
                    btn_search_isbn_info.setClickable(false);
                    btn_search_isbn_info.setEnabled(false);
                }
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });
        et_genre = rootView.findViewById(R.id.et_genre);
        et_year = rootView.findViewById(R.id.et_year);
        et_pages = rootView.findViewById(R.id.et_pages);
        et_lang = rootView.findViewById(R.id.et_lang);
        et_shelves = rootView.findViewById(R.id.et_shelves);
        et_shelves.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                et_shelves.setError(null);
                if(s.length() > 0) {
                    et_shelf.setFocusable(true);
                    et_shelf.setFocusableInTouchMode(true);
                    et_shelf.setEnabled(true);
                } else {
                    et_shelf.setFocusable(false);
                    et_shelf.setFocusableInTouchMode(true);
                    et_shelf.setEnabled(false);
                }
                et_shelf.getText().clear();
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });
        List<String> shelvesList = new ArrayList<>(db.getListShelves());
        ArrayAdapter<String> adapterShelves = new ArrayAdapter<String>(context, android.R.layout.simple_dropdown_item_1line, shelvesList);
        et_shelves.setThreshold(1);
        et_shelves.setAdapter(adapterShelves);
        et_shelf = rootView.findViewById(R.id.et_shelf);
        et_shelf.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                et_shelf.setError(null);
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });
        et_shelf.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus) {
                    //si tiene el focus
                    if(db.getShelvesIdByName(til_shelves.getEditText().getText().toString()) != 0) {
                        int currentShelvesId = db.getShelvesIdByName(til_shelves.getEditText().getText().toString());
                        List<String> shelfList = new ArrayList<>();
                        shelfList.addAll(db.getListShelf(currentShelvesId));
                        ArrayAdapter<String> adapterShelf = new ArrayAdapter<String>(context, android.R.layout.simple_dropdown_item_1line, shelfList);
                        et_shelf.setThreshold(1);
                        et_shelf.setAdapter(adapterShelf);
                    } else {
                        et_shelf.setAdapter(null);
                    }
                }
            }
        });
        et_descp = rootView.findViewById(R.id.et_descp);
        // ChipGroup & Chips
        chipgroup_read_status = rootView.findViewById(R.id.chipgroup_read_status);
        chipgroup_read_status.setOnCheckedChangeListener(new ChipGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(ChipGroup group, int checkedId) {
                for (int i = 0; i < chipgroup_read_status.getChildCount(); i++) {
                    Chip chip = (Chip) chipgroup_read_status.getChildAt(i);
                    if (chip != null) {
                        chip.setClickable(!(chip.getId() == chipgroup_read_status.getCheckedChipId()));
                    }
                }
            }
        });
        chip_read_status_1 = rootView.findViewById(R.id.chip_read_status_1);
        chip_read_status_2 = rootView.findViewById(R.id.chip_read_status_2);
        chip_read_status_3 = rootView.findViewById(R.id.chip_read_status_3);
        // ISBN Button
        btn_search_isbn_info = rootView.findViewById(R.id.btn_search_isbn_info);
        btn_search_isbn_info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkISBNSearch();
            }
        });
    }

    //
    private void checkISBNSearch() {
        // segun settings se muestra una opcion u otra para buscar por ISBN
        switch(prefSettings.getString("isbn_select", "0")) {
            case "0":
                getFromIsbnDialog();
                break;
            case "1":
                getFromGoogleApi();
                break;
            case "2":
                getFromOpenBooksApi();
                break;
        }
    }

    //
    private void getFromIsbnDialog() {
        MaterialAlertDialogBuilder selectISBN = new MaterialAlertDialogBuilder(context, R.style.MaterialDialog);
        CharSequence[] choices = {getString(R.string.dialog_select_isbn_op1), getString(R.string.dialog_select_isbn_op2)};
        selectISBN.setItems(choices, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        getFromGoogleApi();
                        break;
                    case 1:
                        getFromOpenBooksApi();
                        break;
                }
            }
        });
        selectISBN.setTitle(getString(R.string.dialog_select_isbn_title));
        selectISBN.show();
    }

    //
    private void getFromGoogleApi() {
        Search_GISBN searchISBN = Search_GISBN.newInstance(til_isbn.getEditText().getText().toString());
        searchISBN.show(getActivity().getSupportFragmentManager(), "search_isbn");
        searchISBN.setMyCustomListener(new Search_GISBN.CustomListener() {
            @Override
            public void onMyCustomAction(HashMap hashMap) {
                Log.e("HASHMAP", hashMap.toString());
                if(hashMap.containsKey("error")) {
                    Snackbar.make(getActivity().getWindow().getDecorView().findViewById(android.R.id.content),
                            getString(R.string.add_book_get_isbn_info_error), Snackbar.LENGTH_SHORT).setAnchorView(edb_fab).show();
                } else {
                    if(hashMap.containsKey("title") && hashMap.get("title") != null) {
                        et_title.setText(hashMap.get("title").toString());
                    }
                    if(hashMap.containsKey("author") && hashMap.get("author") != null) {
                        et_author.setText(hashMap.get("author").toString());
                    }
                    if(hashMap.containsKey("publisher") && hashMap.get("publisher") != null) {
                        et_publisher.setText(hashMap.get("publisher").toString());
                    }
                    if(hashMap.containsKey("genre") && hashMap.get("genre") != null) {
                        et_genre.setText(hashMap.get("genre").toString());
                    }
                    if(hashMap.containsKey("year") && hashMap.get("year") != null) {
                        et_year.setText(Utils.splitYear(hashMap.get("year").toString()));
                    }
                    if(hashMap.containsKey("pages") && hashMap.get("pages") != null) {
                        et_pages.setText(hashMap.get("pages").toString());
                    }
                    if(hashMap.containsKey("lang") && hashMap.get("lang") != null) {
                        et_lang.setText(hashMap.get("lang").toString());
                    }
                    if(hashMap.containsKey("descp") && hashMap.get("descp") != null) {
                        et_descp.setText(hashMap.get("descp").toString());
                    }
                    if(hashMap.containsKey("cover") && hashMap.get("cover") != null) {
                        fromCam = 0;
                        Glide.with(context)
                                .load(hashMap.get("cover").toString())
                                .centerCrop()
                                .error(R.drawable.ic_broken_image_white)
                                .placeholder(R.drawable.ic_book_white)
                                .into(iv_cover);
                        cover = hashMap.get("cover").toString();
                    }
                    if(hashMap.containsKey("web_link") && hashMap.get("web_link") != null) {
                        web_link = hashMap.get("web_link").toString();
                    }
                }
            }
        });
    }

    //
    private void getFromOpenBooksApi() {
        Search_OISBN searchOISBN = Search_OISBN.newInstance(til_isbn.getEditText().getText().toString());
        searchOISBN.show(getActivity().getSupportFragmentManager(), "search_isbn");
        searchOISBN.setMyCustomListener(new Search_OISBN.CustomListener() {
            @Override
            public void onMyCustomAction(HashMap hashMap) {
                Log.e("HASHMAP", hashMap.toString());
                if(hashMap.containsKey("error")) {
                    Snackbar.make(getActivity().getWindow().getDecorView().findViewById(android.R.id.content),
                            getString(R.string.add_book_get_isbn_info_error), Snackbar.LENGTH_SHORT).setAnchorView(edb_fab).show();
                } else {
                    if(hashMap.containsKey("title") && hashMap.get("title") != null) {
                        et_title.setText(hashMap.get("title").toString());
                    }
                    if(hashMap.containsKey("author") && hashMap.get("author") != null) {
                        et_author.setText(hashMap.get("author").toString());
                    }
                    if(hashMap.containsKey("publisher") && hashMap.get("publisher") != null) {
                        et_publisher.setText(hashMap.get("publisher").toString());
                    }
                    if(hashMap.containsKey("genre") && hashMap.get("genre") != null) {
                        et_genre.setText(hashMap.get("genre").toString());
                    }
                    if(hashMap.containsKey("year") && hashMap.get("year") != null) {
                        et_year.setText(Utils.splitYear(hashMap.get("year").toString()));
                    }
                    if(hashMap.containsKey("pages") && hashMap.get("pages") != null) {
                        et_pages.setText(hashMap.get("pages").toString());
                    }
                    if(hashMap.containsKey("lang") && hashMap.get("lang") != null) {
                        et_lang.setText(hashMap.get("lang").toString());
                    }
                    if(hashMap.containsKey("descp") && hashMap.get("descp") != null) {
                        et_descp.setText(hashMap.get("descp").toString());
                    }
                    if(hashMap.containsKey("cover") && hashMap.get("cover") != null) {
                        fromCam = 0;
                        Glide.with(context)
                                .load(hashMap.get("cover").toString())
                                .centerCrop()
                                .error(R.drawable.ic_broken_image_white)
                                .placeholder(R.drawable.ic_book_white)
                                .into(iv_cover);
                        cover = hashMap.get("cover").toString();
                    }
                    if(hashMap.containsKey("web_link") && hashMap.get("web_link") != null) {
                        web_link = hashMap.get("web_link").toString();
                    }
                }
            }
        });
    }

    //
    private void setBookData() {
        // cover
        Glide.with(context)
                .load(book.getCover())
                .centerCrop()
                .placeholder(R.drawable.ic_book_white)
                .into(iv_cover);
        // title
        et_title.setText(book.getTitle());
        // author
        et_author.setText(db.getAuthorNameById(book.getAuthor()));
        authorOld = db.getAuthorNameById(book.getAuthor());
        // publisher
        et_publisher.setText(db.getPublisherNameById(book.getPublisher()));
        publisherOld = db.getPublisherNameById(book.getPublisher());
        // read_status
        switch (book.getRead_status()) {
            case 0:
                chipgroup_read_status.check(R.id.chip_read_status_1);
                break;
            case 1:
                chipgroup_read_status.check(R.id.chip_read_status_2);
                break;
            case 2:
                chipgroup_read_status.check(R.id.chip_read_status_3);
                break;
        }
        // isbn
        et_isbn.setText(book.getIsbn());
        // genre
        et_genre.setText(book.getGenre());
        // year
        if(!(String.valueOf(book.getYear()).length() < 4)) {
            et_year.setText(String.valueOf(book.getYear()));
        }
        // pages
        if(book.getPages() > 0) {
            et_pages.setText(String.valueOf(book.getPages()));
        }
        // lang
        et_lang.setText(book.getLang());
        // shelves
        et_shelves.setText(db.getShelvesNameById(book.getShelves()));
        shelvesOld = db.getShelvesNameById(book.getShelves());
        // shelf
        et_shelf.setText(db.getShelfNameById(book.getShelf()));
        shelfOld = db.getShelfNameById(book.getShelf());
        // descp
        et_descp.setText(book.getDescp());
    }

    //
    private void getFormValues() {
        // cover
        if (cover.equals("")) {
            cover = book.getCover();
        }
        // title
        if (!TextUtils.isEmpty(til_title.getEditText().getText().toString())) {
            title = til_title.getEditText().getText().toString().trim();
            til_title.setErrorEnabled(false);
        } else {
            til_title.setError(getString(R.string.add_book_title) + getString(R.string.add_book_error_til));
        }
        // author
        if (!TextUtils.isEmpty(til_author.getEditText().getText().toString())) {
            authorName = til_author.getEditText().getText().toString().trim();
            til_author.setErrorEnabled(false);
        } else {
            til_author.setError(getString(R.string.add_book_author) + getString(R.string.add_book_error_til));
        }
        // publisher
        if (!TextUtils.isEmpty(til_publisher.getEditText().getText().toString())) {
            publisherName = til_publisher.getEditText().getText().toString().trim();
            til_publisher.setErrorEnabled(false);
        } else {
            til_publisher.setError(getString(R.string.add_book_publisher) + getString(R.string.add_book_error_til));
        }
        // read_status
        int chipId = chipgroup_read_status.getCheckedChipId();
        switch (chipId) {
            case R.id.chip_read_status_1:
                read_status = 0;
                break;
            case R.id.chip_read_status_2:
                read_status = 1;
                break;
            case R.id.chip_read_status_3:
                read_status = 2;
                break;
        }
        // si se ha cambiado el estado de lectura se vuelve a recoger
        chipgroup_read_status.setOnCheckedChangeListener(new ChipGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(ChipGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.chip_read_status_1:
                        read_status = 0;
                        break;
                    case R.id.chip_read_status_2:
                        read_status = 1;
                        break;
                    case R.id.chip_read_status_3:
                        read_status = 2;
                        break;
                }
            }
        });
        // isbn
        isbn = til_isbn.getEditText().getText().toString();
        // el isbn tiene una longitud de 10 o 13
        if (!TextUtils.isEmpty(til_isbn.getEditText().getText().toString()) || til_isbn.getEditText().getText().toString().length() > 9) {
            btn_search_isbn_info.setClickable(true);
            btn_search_isbn_info.setEnabled(true);
        } else {
            btn_search_isbn_info.setClickable(false);
            btn_search_isbn_info.setEnabled(true);
        }
        // genre
        genre = til_genre.getEditText().getText().toString().trim();
        // year
        if (!TextUtils.isEmpty(til_year.getEditText().getText().toString())) {
            year = Integer.parseInt(til_year.getEditText().getText().toString());
        } else {
            year = 0;
        }
        // pages
        if(!TextUtils.isEmpty(til_pages.getEditText().getText().toString())) {
            pages = Integer.parseInt(til_pages.getEditText().getText().toString());
        } else {
            pages = 0;
        }
        // lang
        lang = til_lang.getEditText().getText().toString();
        // shelves
        if(!TextUtils.isEmpty(til_shelves.getEditText().getText().toString())) {
            shelvesName = til_shelves.getEditText().getText().toString().trim();
            til_shelves.setErrorEnabled(false);
        } else {
            til_shelves.setError(getString(R.string.add_shelves_name) + getString(R.string.add_shelves_error_til));
        }
        // shelf
        if(!TextUtils.isEmpty(til_shelf.getEditText().getText().toString())) {
            shelfName = til_shelf.getEditText().getText().toString().trim();
            til_shelf.setErrorEnabled(false);
        } else {
            til_shelf.setError(getString(R.string.add_shelf_name) + getString(R.string.add_shelves_error_til));
        }
        // descp
        descp = til_descp.getEditText().getText().toString();
        // update book
        if(!TextUtils.isEmpty(title) && !TextUtils.isEmpty(authorName) && !TextUtils.isEmpty(publisherName)
                && !TextUtils.isEmpty(shelfName) && !TextUtils.isEmpty(shelfName)) {
            checkToUpdate();
        } else {
            Snackbar.make(getActivity().getWindow().getDecorView().findViewById(android.R.id.content), getString(R.string.edit_book_error), Snackbar.LENGTH_SHORT).setAnchorView(edb_fab).show();
        }
    }

    //
    private void checkToUpdate() {
        // cover
        if(fromCam == 1) {
            // si viene de la camara guardamos
            String coverName = Utils.createCoverName(title);
            cover = Utils.saveImage(context, photo, coverName);
        }
        // check author
        if(authorName.equals(authorOld)) {
            authorId = book.getAuthor();
        } else {
            if(db.getAuthorIdByName(authorName) == 0) {
                authorId = (int) db.insertAuthor(authorName);
            } else {
                authorId = db.getAuthorIdByName(authorName);
            }
        }
        // check publisher
        if(publisherName.equals(publisherOld)) {
            publisherId = book.getPublisher();
        } else {
            if(db.getPublisherIdByName(publisherName) == 0) {
                publisherId = (int) db.insertPublisher(publisherName);
            } else {
                publisherId = db.getPublisherIdByName(publisherName);
            }
        }
        // check shelves
        if(shelvesName.equals(shelvesOld)) {
            shelvesId = book.getShelves();
        } else {
            if(db.getShelvesIdByName(shelvesName) == 0) {
                shelvesId = (int) db.insertShelves(shelvesName);
            } else {
                shelvesId = db.getShelvesIdByName(shelvesName);
            }
        }
        // check shelf
        if(shelvesName.equals(shelvesOld) && shelfName.equals(shelfOld)) {
            // si shelves & shelf iguales
            shelfId = book.getShelf();
        } else {
            if(db.getShelfIdByNameAndShelves(shelfName, shelvesId) == 0) {
                shelfId = (int) db.insertShelf(shelfName, shelvesId);
            } else {
                shelfId = db.getShelfIdByNameAndShelves(shelfName, shelvesId);
            }
        }
        // update
        db.updateBook(bookId, title, authorId, publisherId, genre, year, pages, lang, read_status, isbn, cover, shelvesId, shelfId, descp, web_link);
        // check author
        if(db.getBooksBeanFromAuthorById(book.getAuthor()).size() == 0) {
            db.deleteAuthor(book.getAuthor());
        }
        // check publisher
        if(db.getBooksBeanFromPublisherById(book.getPublisher()).size() == 0) {
            db.deletePublisher(book.getPublisher());
        }
        // check shelves & shelf
        if(db.getBooksBeanFromShelvesById(book.getShelves()).size() == 0) {
            db.deleteShelfByShelves(book.getShelves());
            db.deleteShelves(book.getShelves());
        }
        if(db.getBooksBeanFromShelfById(book.getShelf()).size() == 0) {
            db.deleteShelf(book.getShelf());
        }
        if(db.getShelfInShelvesById(shelvesId).size() == 0) {
            db.deleteShelves(book.getShelves());
        }
        // actualizamos el timestamp de shelves
        db.updateShelvesTimestamp(shelvesId);
        NavController navController = ((MainActivity)context).getMainController();
        navController.getPreviousBackStackEntry().getSavedStateHandle().set("update_details", getString(R.string.edit_book_edited));
        navController.popBackStack();
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.menu_add_book, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_add_camera:
                dialogSelectPhoto();
                break;
            case R.id.action_add_barcode:
                Intent barcode_intent = new Intent(getActivity(), Barcode.class);
                startActivityForResult(barcode_intent, BARCODE_REQUEST);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    //
    private void dialogSelectPhoto() {
        MaterialAlertDialogBuilder selectISBN = new MaterialAlertDialogBuilder(context, R.style.MaterialDialog);
        CharSequence[] choices = {getString(R.string.action_add_camera), getString(R.string.add_cover_from_foder)};
        selectISBN.setItems(choices, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        Intent camera_intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        startActivityForResult(camera_intent, CAMERA_REQUEST);
                        break;
                    case 1:
                        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                        intent.addCategory(Intent.CATEGORY_OPENABLE);
                        intent.setType("image/*");
                        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
                        startActivityForResult(intent, PHOTOFOLDER_REQUEST);
                        break;
                }
            }
        });
        selectISBN.setTitle(getString(R.string.dialog_select_isbn_title));
        selectISBN.show();
    }

    // onActivityResult para recuperar la imagen sacada con la camara
    // fromCam controla si la foto es de la camara o pillada por ISBN
    // este control se hace en checkToUpdate()
    // tambien se recupera los datos del scanner ISBN
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_REQUEST && resultCode == getActivity().RESULT_OK) {
            photo = (Bitmap)data.getExtras().get("data");
            fromCam = 1;
            Glide.with(context)
                    .load(photo)
                    .centerCrop()
                    .error(R.drawable.ic_broken_image_white)
                    .placeholder(R.drawable.ic_book_white)
                    .into(iv_cover);
        }
        if(requestCode == BARCODE_REQUEST && resultCode == Activity.RESULT_OK) {
            et_isbn.setText(String.valueOf(data.getExtras().get("isbnCode")));
            checkISBNSearch();
        }
        if(requestCode == PHOTOFOLDER_REQUEST && resultCode == Activity.RESULT_OK) {
            Uri treeUri = data.getData();
            Glide.with(context)
                    .asBitmap()
                    .load(treeUri)
                    .override(100, 150)
                    .centerCrop()
                    .error(R.drawable.ic_broken_image_white)
                    .placeholder(R.drawable.ic_book_white)
                    .into(new CustomTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                            iv_cover.setImageBitmap(resource);
                            iv_cover.buildDrawingCache();
                            photo = resource;
                        }
                        @Override
                        public void onLoadCleared(@Nullable Drawable placeholder) { }
                    });
            fromCam = 1;
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
