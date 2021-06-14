package com.hitglynorthz.mybookshelf.utils;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.NonNull;
import androidx.palette.graphics.Palette;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.hitglynorthz.mybookshelf.R;
import com.hitglynorthz.mybookshelf.db.DBHelper;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Utils {

    // quitar acentos
    public static String removeAccents(String text) {
        return text.replace("Á", "A")
                .replace("É", "E")
                .replace("Í", "I")
                .replace("Ó", "O")
                .replace("Ú", "U")
                .replace("á", "a")
                .replace("é", "e")
                .replace("í", "i")
                .replace("ó", "o")
                .replace("ú", "u");
    }

    // split year
    public static String splitYear(String year) {
        if (year.contains("-") && year.length() == 4) {
            String[] parts = year.split("-");
            return parts[0];
        } else {
            return year;
        }
    }

    //
    public static void fabScrollBehavior(RecyclerView recyclerView, final FloatingActionButton fab) {
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0 && fab.getVisibility() == View.VISIBLE) {
                    fab.hide();
                } else if (dy < 0 && fab.getVisibility() != View.VISIBLE) {
                    fab.show();
                }
            }
        });
    }

    //
    public static Palette.Swatch getPalette(Palette pCustom, String typeSwatch) {
        switch (typeSwatch) {
            case "vibrantSwatch":
                Palette.Swatch vibrantSwatch = pCustom.getVibrantSwatch();
                return vibrantSwatch;
            case "vibrantLightSwatch":
                Palette.Swatch vibrantLightSwatch = pCustom.getLightVibrantSwatch();
                return vibrantLightSwatch;
            case "vibrantDarkSwatch":
                Palette.Swatch vibrantDarkSwatch = pCustom.getDarkVibrantSwatch();
                return vibrantDarkSwatch;
            case "mutedSwatch":
                Palette.Swatch mutedSwatch = pCustom.getMutedSwatch();
                return mutedSwatch;
            case "mutedLightSwatch":
                Palette.Swatch mutedLightSwatch = pCustom.getLightMutedSwatch();
                return mutedLightSwatch;
            case "mutedDarkSwatch":
                Palette.Swatch mutedDarkSwatch = pCustom.getDarkMutedSwatch();
                return mutedDarkSwatch;
        }
        return null;
    }

    /**
     * https://stackoverflow.com/questions/33072365/how-to-darken-a-given-color-int
     * @param color color provided
     * @param factor factor to make color darker
     * @return int as darker color
     */
    public static int manipulateColor(int color, float factor) {
        int a = Color.alpha(color);
        int r = Math.round(Color.red(color) * factor);
        int g = Math.round(Color.green(color) * factor);
        int b = Math.round(Color.blue(color) * factor);
        return Color.argb(a,
                Math.min(r, 255),
                Math.min(g, 255),
                Math.min(b, 255));
    }

    // ocultar el teclado
    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        if (inputMethodManager.isAcceptingText()) {
            inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
        }
    }

    // guardar foto
    // https://developer.android.com/reference/android/content/Context.html#getExternalFilesDir(java.lang.String)
    // /storage/emulated/0/Android/data/com.hitglynorthz.mybookshelf/files/My Bookshelf/Covers/...
    public static String saveImage(Context context, Bitmap bitmap, String fileName) {
        String path = "/My Bookshelf/Covers/";
        File filePath = new File(context.getExternalFilesDir(null), path);
        if (!filePath.exists()) {
            filePath.mkdirs();
        }
        File imageFile = new File(filePath, fileName + ".png");
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(imageFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        try{
            bitmap.compress(Bitmap.CompressFormat.PNG, 50, out);
            out.flush();
            out.close();
            return String.valueOf(imageFile);
        } catch(Exception e) {
            return "";
        }
    }

    // borrar foto
    public static void deleteCover(String cover) {
        File imageFile = new File(cover);
        if(imageFile.exists()) {
            imageFile.delete();
        }
    }

    // borrar todas las fotos
    public static void deleteAllCovers(Context context) {
        String path = "/My Bookshelf/Covers/";
        File filePath = new File(context.getExternalFilesDir(null), path);
        if(filePath.isDirectory()) {
            String[] children = filePath.list();
            for (String child : children) {
                new File(filePath, child).delete();
            }
        }
    }

    //
    public static String createCoverName(String title) {
        String coverName;
        String timeStamp = new SimpleDateFormat("dd_MM_yyyy_HHmmss", Locale.getDefault()).format(new Date());
        coverName = (title + timeStamp).trim();
        coverName = removeAccents(coverName);
        coverName = coverName.replaceAll("[^a-zA-Z0-9]", "");

        return coverName;
    }

    //
    public static String getDateTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        Date date = new Date();
        return dateFormat.format(date);
    }

}
