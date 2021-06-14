package com.hitglynorthz.mybookshelf.ui.crud;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageDecoder;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
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
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.documentfile.provider.DocumentFile;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.material.bottomappbar.BottomAppBar;
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
import com.hitglynorthz.mybookshelf.db.Wishlist;
import com.hitglynorthz.mybookshelf.ui.toplevels.HomeFragment;
import com.hitglynorthz.mybookshelf.utils.Barcode;
import com.hitglynorthz.mybookshelf.utils.Search_GISBN;
import com.hitglynorthz.mybookshelf.utils.Search_OISBN;
import com.hitglynorthz.mybookshelf.utils.Utils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static androidx.preference.PreferenceManager.getDefaultSharedPreferences;

public class Fragment_AddBook extends Fragment {
    private View rootView;
    private Context context;

    private FloatingActionButton add_fab;

    private ImageView iv_cover;
    private TextInputLayout til_title, til_author, til_publisher, til_isbn, til_genre,
            til_year, til_pages, til_lang, til_shelves, til_shelf, til_descp;
    private TextInputEditText et_title, et_isbn, et_genre, et_year, et_pages, et_lang, et_descp;
    private MaterialAutoCompleteTextView et_author, et_publisher, et_shelves, et_shelf;
    private ChipGroup chipgroup_read_status;
    private Chip chip_read_status_1, chip_read_status_2, chip_read_status_3;
    private Button btn_search_isbn_info;

    private String title, authorName, publisherName, genre, lang, cover = "", isbn, descp, web_link = "", shelvesName, shelfName;
    private int year, pages, read_status;
    private int authorId, publisherId, shelvesId, shelfId;
    private Bitmap photo;
    private int fromCam = 0;

    private int id_wishlist;
    private String add_from_wishlist;
    private Wishlist wishlist;

    private SharedPreferences prefSettings;

    private static int CAMERA_REQUEST = 713;
    private static int BARCODE_REQUEST = 843;
    private static int PHOTOFOLDER_REQUEST = 807;

    private DBHelper db;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView =  inflater.inflate(R.layout.fragment_add_book, container, false);
        context = getContext();
        db = DBHelper.getInstance(context);
        prefSettings = getDefaultSharedPreferences(context);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        initViews();
        initFormFields();

        // recuperamos los datos que se envian al fragment
        // estos datos vienen de Wishlist para añadir un libro a la libreria
        if (getArguments() != null) {
            id_wishlist = getArguments().getInt("wishlist_id");
            add_from_wishlist = getArguments().getString("add_from_wishlist");
            wishlist = db.getWishlist(id_wishlist);
            setWislistData();
        }

        add_fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFormValues();
            }
        });

        return rootView;
    }

    // inicializamos las vistas
    private void initViews() {
        add_fab = rootView.findViewById(R.id.add_fab);
    }

    // inicializamos los campos del formulario
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

    // comprobacion del ISBN
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

    // dialogo para seleccionar API para buscar ISBN
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

    // buscar ISBN con la API de Google Books
    private void getFromGoogleApi() {
        Search_GISBN searchISBN = Search_GISBN.newInstance(til_isbn.getEditText().getText().toString());
        searchISBN.show(getActivity().getSupportFragmentManager(), "search_isbn");
        searchISBN.setMyCustomListener(new Search_GISBN.CustomListener() {
            @Override
            public void onMyCustomAction(HashMap hashMap) {
                Log.e("HASHMAP", hashMap.toString());
                if(hashMap.containsKey("error")) {
                    Snackbar.make(getActivity().getWindow().getDecorView().findViewById(android.R.id.content),
                            getString(R.string.add_book_get_isbn_info_error), Snackbar.LENGTH_SHORT).setAnchorView(add_fab).show();
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

    // buscar ISBN con la API de OpenBooks
    private void getFromOpenBooksApi() {
        Search_OISBN searchOISBN = Search_OISBN.newInstance(til_isbn.getEditText().getText().toString());
        searchOISBN.show(getActivity().getSupportFragmentManager(), "search_isbn");
        searchOISBN.setMyCustomListener(new Search_OISBN.CustomListener() {
            @Override
            public void onMyCustomAction(HashMap hashMap) {
                Log.e("HASHMAP", hashMap.toString());
                if(hashMap.containsKey("error")) {
                    Snackbar.make(getActivity().getWindow().getDecorView().findViewById(android.R.id.content),
                            getString(R.string.add_book_get_isbn_info_error), Snackbar.LENGTH_SHORT).setAnchorView(add_fab).show();
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

    // se ponen los datos de un libro de deseados en los campos de texto
    // esto solo es para cuando un libro se añade desde deseados
    private void setWislistData() {
        et_title.setText(wishlist.getTitle());
        et_author.setText(wishlist.getAuthor());
        et_publisher.setText(wishlist.getPublisher());
        et_isbn.setText(wishlist.getIsbn());
    }

    // se recogen los datos del formulario
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
            authorName = til_author.getEditText().getText().toString().trim();
            til_author.setErrorEnabled(false);
        } else {
            til_author.setError(getString(R.string.add_book_author) + getString(R.string.add_book_error_til));
        }
        // publisher
        if(!TextUtils.isEmpty(til_publisher.getEditText().getText().toString())) {
            publisherName = til_publisher.getEditText().getText().toString().trim();
            til_publisher.setErrorEnabled(false);
        } else {
            til_publisher.setError(getString(R.string.add_book_publisher) + getString(R.string.add_book_error_til));
        }
        // ISBN
        isbn = til_isbn.getEditText().getText().toString();
        if(!TextUtils.isEmpty(til_isbn.getEditText().getText().toString()) || til_isbn.getEditText().getText().toString().length() > 9) {
            btn_search_isbn_info.setClickable(true);
            btn_search_isbn_info.setEnabled(true);
        } else {
            btn_search_isbn_info.setClickable(false);
            btn_search_isbn_info.setEnabled(true);
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
        // si se ha cambiado el read_status se vuelve a recoger
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
        // genre
        genre = til_genre.getEditText().getText().toString().trim();
        // year
        if(!TextUtils.isEmpty(til_year.getEditText().getText().toString())) {
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
        // comprobar campos requeridos
        if(!TextUtils.isEmpty(title) && !TextUtils.isEmpty(authorName) && !TextUtils.isEmpty(publisherName) && !TextUtils.isEmpty(shelvesName) && !TextUtils.isEmpty(shelfName)) {
            // check data
            checkValues();
        } else {
            Snackbar.make(getActivity().getWindow().getDecorView().findViewById(android.R.id.content), getString(R.string.add_book_error), Snackbar.LENGTH_SHORT).setAnchorView(add_fab).show();
        }
    }

    // se comprueban los valores del formulario
    private void checkValues() {
        // ocultamos el teclado
        Utils.hideSoftKeyboard(getActivity());
        // cover
        if(fromCam == 1) {
            // si viene de la camara guardamos
            String coverName = Utils.createCoverName(title);
            cover = Utils.saveImage(context, photo, coverName);
        }
        // check Author
        authorId = db.getAuthorIdByName(authorName);
        if(authorId == 0) {
            // si no existe se crea uno
            authorId = (int) db.insertAuthor(authorName);
        }
        // check Publisher
        publisherId = db.getPublisherIdByName(publisherName);
        if(publisherId == 0) {
            // si no existe se crea uno
            publisherId = (int) db.insertPublisher(publisherName);
        }
        // check Shelves
        shelvesId = db.getShelvesIdByName(shelvesName);
        if(shelvesId == 0) {
            // si no existe se crea uno
            shelvesId = (int) db.insertShelves(shelvesName);
        }
        // check Shelf
        shelfId = db.getShelfIdByNameAndShelves(shelfName, shelvesId);
        if(shelfId == 0) {
            // si no existe se crea uno
            shelfId = (int) db.insertShelf(shelfName, shelvesId);
        }
        // guardamos el libro
        long newBookId = db.insertBook(title, authorId, publisherId, genre, year, pages, lang, read_status, isbn, cover, shelvesId, shelfId, descp, web_link);
        Book newbook = db.getBook(newBookId);
        if (newbook != null) {
            // actualizamos el timestamp de shelves
            db.updateShelvesTimestamp(shelvesId);
            // si viene de wishlist lo borramos de la tabla
            if(getArguments() != null && add_from_wishlist.equals("add_from_wishlist")) {
                db.deleteWishlist(id_wishlist);
            }
            NavController navController = ((MainActivity)context).getMainController();
            navController.getPreviousBackStackEntry().getSavedStateHandle().set("update_home", String.format(getString(R.string.add_book_saved_ok), title));
            navController.popBackStack();
        }
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

    // dialogo para sacar foto o seleccionar de carpeta
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
    // este control se hace en checkValues()
    // tambien se recupera los datos del scanner ISBN
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK) {
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
