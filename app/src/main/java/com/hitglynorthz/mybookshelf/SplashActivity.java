package com.hitglynorthz.mybookshelf;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.util.List;

import static androidx.preference.PreferenceManager.getDefaultSharedPreferences;

public class SplashActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener {
    private Handler handler = new Handler();

    private SharedPreferences prefSettings;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        prefSettings = getDefaultSharedPreferences(this);
        prefSettings.registerOnSharedPreferenceChangeListener(this);
        setupTheme(prefSettings.getString("theme_select", "0"));

        requestPermissions();

    }

    //
    private void initApp() {
        final Intent intent = new Intent(this, MainActivity.class);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                finish();
            }
        },500);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if(key.equals("theme_select")) {
            setupTheme(prefSettings.getString("theme_select", "0"));
        }
    }

    //
    public void setupTheme(String themeValue) {
        switch (themeValue) {
            case "0":
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                break;
            case "1":
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                break;
            case "2":
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
                break;
        }
    }

    //
    private void requestPermissions() {
        Dexter.withActivity(this)
                .withPermissions(
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.CAMERA)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        // si todos los permisos se conceden
                        if(report.areAllPermissionsGranted()) {
                            initApp();
                        }
                        // si algun permiso no se ha aceptado
                        if(!report.areAllPermissionsGranted()) {
                            showSettingsDialog();
                        }
                        // si algun permiso se deniega permanentemente
                        if(report.isAnyPermissionPermanentlyDenied()) {
                            showSettingsDialog();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                })
                .onSameThread()
                .check();
    }

    //
    private void showSettingsDialog() {
        new MaterialAlertDialogBuilder(this, R.style.MaterialDialog)
                .setTitle(getString(R.string.dialog_permissions_settings_title))
                .setMessage(getString(R.string.dialog_permissions_settings_text))
                .setPositiveButton(getString(R.string.dialog_permissions_settings_positive), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        openSettings();
                    }
                })
                .setNegativeButton(getString(R.string.dialog_permissions_negative), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        finish();
                    }
                })
                .setCancelable(false)
                .show();
    }

    // abrir los ajustes principales de la aplicacion para que el usuario pueda conceder los permisos necesarios
    private void openSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        startActivityForResult(intent, 101);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getDefaultSharedPreferences(this).registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    protected void onDestroy() {
        handler.removeCallbacksAndMessages(null);
        super.onDestroy();
        getDefaultSharedPreferences(this).unregisterOnSharedPreferenceChangeListener(this);
    }

}
