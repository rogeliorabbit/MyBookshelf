package com.hitglynorthz.mybookshelf.ui.toplevels;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;
import androidx.preference.SwitchPreferenceCompat;

import com.google.android.material.snackbar.Snackbar;
import com.hitglynorthz.mybookshelf.MainActivity;
import com.hitglynorthz.mybookshelf.R;
import com.hitglynorthz.mybookshelf.db.Book;
import com.hitglynorthz.mybookshelf.db.BookBean;
import com.hitglynorthz.mybookshelf.db.DBHelper;
import com.hitglynorthz.mybookshelf.utils.Utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class SettingsFragment extends PreferenceFragmentCompat {
    private View prefView;
    private Context context;

    private Preference backup, restore, csvPref, deleteAll;
    private SwitchPreferenceCompat badges, listType, smallBooks, smallShelves;
    private ListPreference theme, isbn;

    private SharedPreferences prefs;

    private static int RESTORE_REQUEST = 444;
    private static int BACKUP_REQUEST = 443;
    private static int CSV_REQUEST = 323;

    private DBHelper db;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        prefView = super.onCreateView(inflater, container, savedInstanceState);
        if(prefView != null) {
            prefView.setBackgroundColor(ContextCompat.getColor(context, R.color.colorBackground));
        }
        return prefView;
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.preferences, rootKey);
        context = getContext();
        db = DBHelper.getInstance(context);
        prefs = PreferenceManager.getDefaultSharedPreferences(context);

        theme = findPreference("theme_select");
        badges = findPreference("badge_select");
        listType = findPreference("listype_select");
        smallBooks = findPreference("book_select");
        smallShelves = findPreference("shelves_select");
        isbn = findPreference("isbn_select");
        backup = findPreference("backup_select");
        restore = findPreference("restore_select");
        csvPref = findPreference("csv_select");
        deleteAll = findPreference("delete_all");

        initPrefs();

        // Design Prefs
        theme.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                switch ((String) newValue) {
                    case "0":
                        theme.setSummary(getString(R.string.settings_theme_op1));
                        break;
                    case "1":
                        theme.setSummary(getString(R.string.settings_theme_op2));
                        break;
                    case "2":
                        theme.setSummary(getString(R.string.settings_theme_op3));
                        break;
                }
                return true;
            }
        });
        badges.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                if((boolean) newValue) {
                    badges.setSummary(getString(R.string.settings_badge_subtitle2));
                } else {
                    badges.setSummary(getString(R.string.settings_badge_subtitle1));
                }
                return true;
            }
        });
        listType.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                if((boolean) newValue) {
                    smallBooks.setEnabled(false);
                    listType.setSummary(getString(R.string.settings_list_subtitle2));
                    listType.setIcon(R.drawable.ic_view_grid_tint);
                } else {
                    smallBooks.setEnabled(true);
                    listType.setSummary(getString(R.string.settings_list_subtitle1));
                    listType.setIcon(R.drawable.ic_view_list_tint);
                }
                return true;
            }
        });
        smallBooks.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                if((boolean) newValue) {
                    smallBooks.setSummary(getString(R.string.settings_small_books_subtitle2));
                } else {
                    smallBooks.setSummary(getString(R.string.settings_small_books_subtitle1));
                }
                return true;
            }
        });
        smallShelves.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                if((boolean) newValue) {
                    smallShelves.setSummary(getString(R.string.settings_small_shelves_subtitle2));
                } else {
                    smallShelves.setSummary(getString(R.string.settings_small_shelves_subtitle1));
                }
                return true;
            }
        });

        // API ISBN
        isbn.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                switch ((String) newValue) {
                    case "0":
                        isbn.setSummary(getString(R.string.settings_isbn_op1));
                        break;
                    case "1":
                        isbn.setSummary(getString(R.string.settings_isbn_op2));
                        break;
                    case "2":
                        isbn.setSummary(getString(R.string.settings_isbn_op3));
                        break;
                }
                return true;
            }
        });

        // Backup
        backup.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
                intent.addCategory(Intent.CATEGORY_DEFAULT);
                intent.setFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
                startActivityForResult(intent, BACKUP_REQUEST);
                return false;
            }
        });
        // Restore
        restore.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("*/*"); // "application/x-sqlite3" no funciona
                intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
                startActivityForResult(intent, RESTORE_REQUEST);
                return false;
            }
        });
        // CSV
        csvPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
                intent.addCategory(Intent.CATEGORY_DEFAULT);
                intent.setFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
                startActivityForResult(intent, CSV_REQUEST);
                return false;
            }
        });
        // deleteAll
        deleteAll.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                deleteAll();
                return false;
            }
        });

    }

    // se inicializan las preferencias con los valores seleccionados por el usuario
    private void initPrefs() {
        switch (prefs.getString("theme_select", "0")) {
            case "0":
                theme.setSummary(getString(R.string.settings_theme_op1));
                break;
            case "1":
                theme.setSummary(getString(R.string.settings_theme_op2));
                break;
            case "2":
                theme.setSummary(getString(R.string.settings_theme_op3));
                break;
        }
        if(prefs.getBoolean("badge_select", false)) {
            badges.setSummary(getString(R.string.settings_badge_subtitle2));
        } else {
            badges.setSummary(getString(R.string.settings_badge_subtitle1));
        }
        if(prefs.getBoolean("listype_select", false)) {
            smallBooks.setEnabled(false);
            listType.setSummary(getString(R.string.settings_list_subtitle2));
            listType.setIcon(R.drawable.ic_view_grid_tint);
        } else {
            smallBooks.setEnabled(true);
            listType.setSummary(getString(R.string.settings_list_subtitle1));
            listType.setIcon(R.drawable.ic_view_list_tint);
        }
        if(prefs.getBoolean("book_select", false)) {
            smallBooks.setSummary(getString(R.string.settings_small_books_subtitle2));
        } else {
            smallBooks.setSummary(getString(R.string.settings_small_books_subtitle1));
        }
        if(prefs.getBoolean("shelves_select", false)) {
            smallShelves.setSummary(getString(R.string.settings_small_shelves_subtitle2));
        } else {
            smallShelves.setSummary(getString(R.string.settings_small_shelves_subtitle1));
        }
        switch (prefs.getString("isbn_select", "0")) {
            case "0":
                isbn.setSummary(getString(R.string.settings_isbn_op1));
                break;
            case "1":
                isbn.setSummary(getString(R.string.settings_isbn_op2));
                break;
            case "2":
                isbn.setSummary(getString(R.string.settings_isbn_op3));
                break;
        }
    }

    //
    private void backupDB(Uri pathUri) {
        String docId = DocumentsContract.getTreeDocumentId(pathUri);
        Uri dirUri = DocumentsContract.buildDocumentUriUsingTree(pathUri, docId);
        String fileName = new SimpleDateFormat("dd_MM_yyyy_HH_mm_ss_", Locale.getDefault()).format(new Date()) + "books_db.hnz.db";
        try {
            Uri destUri = DocumentsContract.createDocument(context.getContentResolver(), dirUri, "*/*", fileName);
            InputStream inputStream = new FileInputStream(Environment.getDataDirectory() + "/data/com.hitglynorthz.mybookshelf/databases/books_db.hnz.db");
            OutputStream outputStream = context.getContentResolver().openOutputStream(destUri, "w");
            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) > 0)
                outputStream.write(buffer, 0, length);
            inputStream.close();
            outputStream.flush();
            outputStream.close();
            Snackbar.make(getActivity().getWindow().getDecorView().findViewById(android.R.id.content), getString(R.string.settings_backup_done), Snackbar.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
            Snackbar.make(getActivity().getWindow().getDecorView().findViewById(android.R.id.content), getString(R.string.settings_backup_fail), Snackbar.LENGTH_SHORT).show();
        }
    }

    //
    private void restoreDB(Uri fileUri) {
        ContentResolver contentResolver = context.getContentResolver();
        Cursor cursor = contentResolver.query(fileUri, null, null, null, null);
        if(cursor != null && cursor.moveToFirst()) {
            if(cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)).contains("books_db.hnz.db")) {
                try {
                    InputStream inputStream = context.getContentResolver().openInputStream(fileUri);
                    OutputStream outputStream = new FileOutputStream(Environment.getDataDirectory() + "/data/com.hitglynorthz.mybookshelf/databases/books_db.hnz.db");
                    byte[] buffer = new byte[1024];
                    int length;
                    while((length = inputStream.read(buffer)) > 0){
                        outputStream.write(buffer, 0, length);
                    }
                    outputStream.flush();
                    outputStream.close();
                    inputStream.close();

                    // https://www.sqlite.org/fileformat.html#the_database_header
                    // en los primeros 16 bytes el header de una db SQLITE contiene SQLite format 3\0000
                    // se comprueba que el archivo importado tiene ese header
                    File check = new File(Environment.getDataDirectory() + "/data/com.hitglynorthz.mybookshelf/databases/books_db.hnz.db");
                    FileReader fr = new FileReader(check);
                    char[] bufferCheck = new char[16];
                    fr.read(bufferCheck, 0, 16);
                    String str = String.valueOf(bufferCheck);
                    fr.close();
                    if(!str.equals("SQLite format 3\u0000")) {
                        check.delete();
                        Snackbar.make(getActivity().getWindow().getDecorView().findViewById(android.R.id.content), getString(R.string.settings_restore_check_fail), Snackbar.LENGTH_SHORT).show();
                    }
                    Snackbar.make(getActivity().getWindow().getDecorView().findViewById(android.R.id.content), getString(R.string.settings_restore_check_done), Snackbar.LENGTH_SHORT).show();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                Snackbar.make(getActivity().getWindow().getDecorView().findViewById(android.R.id.content), getString(R.string.settings_restore_check_fail), Snackbar.LENGTH_SHORT).show();
            }
        }
    }

    //
    private void createCSV(Uri pathUri) {
        String docId = DocumentsContract.getTreeDocumentId(pathUri);
        Uri dirUri = DocumentsContract.buildDocumentUriUsingTree(pathUri, docId);
        String fileName = new SimpleDateFormat("dd_MM_yyyy_HH_mm_ss_", Locale.getDefault()).format(new Date()) + "mybooks";
        try {
            // archivo temporal
            File tempFile = File.createTempFile(fileName, ".csv", context.getCacheDir());
            FileOutputStream tempFileStream = new FileOutputStream(tempFile);
            OutputStreamWriter fileWriter = new OutputStreamWriter(tempFileStream, "8859_1"); // http://www.cafeconleche.org/books/xmljava/chapters/ch03s03.html
            List<BookBean> books = new ArrayList<>(db.getAllBooksBean());
            BookBean book;
            if(books.size() > 0) {
                for(int i = 0; i < books.size(); i++) {
                    book = books.get(i);
                    fileWriter.append(book.getTitle());
                    fileWriter.append(",");
                    fileWriter.append(book.getAuthorName());
                    fileWriter.append(",");
                    fileWriter.append(book.getPublisherName());
                    fileWriter.append(",");
                    fileWriter.append(book.getIsbn());
                    fileWriter.append(",");
                    fileWriter.append(book.getShelvesName());
                    fileWriter.append(",");
                    fileWriter.append(book.getShelfName());
                    fileWriter.append("\n");
                }
            }
            fileWriter.close();
            tempFileStream.close();
            Uri destUri = DocumentsContract.createDocument(context.getContentResolver(), dirUri, "*/*", fileName + ".csv");
            InputStream inputStream = new FileInputStream(tempFile);
            OutputStream outputStream = context.getContentResolver().openOutputStream(destUri, "w");
            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) > 0)
                outputStream.write(buffer, 0, length);
            inputStream.close();
            outputStream.flush();
            outputStream.close();
            tempFile.delete();
            Snackbar.make(getActivity().getWindow().getDecorView().findViewById(android.R.id.content), getString(R.string.settings_csv_done), Snackbar.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
            Snackbar.make(getActivity().getWindow().getDecorView().findViewById(android.R.id.content), getString(R.string.settings_csv_error), Snackbar.LENGTH_SHORT).show();
        }
    }

    //
    private void deleteAll() {
        new AlertDialog.Builder(context)
                .setTitle(getString(R.string.settings_dialog_reset_title))
                .setMessage(getString(R.string.settings_dialog_reset_message))
                .setPositiveButton(getString(R.string.settings_dialog_reset_positive), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Utils.deleteAllCovers(context);
                        db.deleteAll();
                        ((MainActivity)context).setupStatsItem();
                        Snackbar.make(getActivity().getWindow().getDecorView().findViewById(android.R.id.content), getString(R.string.settings_dialog_snackbar_reset), Snackbar.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }
                })
                .setNegativeButton(getString(R.string.settings_dialog_reset_negative), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create().show();
    }

    //
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == BACKUP_REQUEST && resultCode == Activity.RESULT_OK) {
            Uri treeUri = data.getData();
            backupDB(treeUri);
        }
        if(requestCode == RESTORE_REQUEST && resultCode == Activity.RESULT_OK) {
            Uri treeUri = data.getData();
            restoreDB(treeUri);
        }
        if(requestCode == CSV_REQUEST && resultCode == Activity.RESULT_OK) {
            Uri treeUri = data.getData();
            createCSV(treeUri);
        }
    }

}
