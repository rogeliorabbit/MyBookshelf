package com.hitglynorthz.mybookshelf.utils;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;
import com.hitglynorthz.mybookshelf.R;

import java.util.ArrayList;
import java.util.List;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class Barcode extends AppCompatActivity implements ZXingScannerView.ResultHandler {
    private ZXingScannerView mScannerView;
    private List<BarcodeFormat> formats = new ArrayList<BarcodeFormat>();

    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);
        supportRequestWindowFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION,
                WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        getSupportActionBar().setTitle(getString(R.string.nav_barcode_text));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        // iniciamos la vista del scanner
        mScannerView = new ZXingScannerView(this);
        // el codigo ISB esta formado por EAN_13
        formats.add(BarcodeFormat.EAN_13);
        mScannerView.setFormats(formats);
        // autofocus
        mScannerView.setAutoFocus(true);
        // ponemos la vista del scanner como la vista principal
        setContentView(mScannerView);
    }

    @Override
    public void onResume() {
        super.onResume();
        // llamanos al handler para los resultados del scanner
        mScannerView.setResultHandler(this);
        // iniciamos la camara
        mScannerView.startCamera();
    }

    @Override
    public void onPause() {
        super.onPause();
        // paramos la camara
        mScannerView.stopCamera();
    }

    @Override
    public void handleResult(Result rawResult) {
        Log.e("T", rawResult.getBarcodeFormat().toString());
        // recogemos el resultado en un String
        String isbnCode = rawResult.getText();
        // creamos un intent para devolver el codigo
        Intent intentBack = new Intent();
        intentBack.putExtra("isbnCode", isbnCode);
        setResult(Activity.RESULT_OK, intentBack);
        // cerramos la actividad Barcode, el intent devolverá los datos al ActivityResult
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
