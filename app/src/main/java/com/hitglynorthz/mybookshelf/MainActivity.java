package com.hitglynorthz.mybookshelf;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;
import com.hitglynorthz.mybookshelf.db.DBHelper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.HashSet;
import java.util.Set;

import static androidx.preference.PreferenceManager.getDefaultSharedPreferences;

public class MainActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener {
    private Toolbar toolbar;

    private DrawerLayout drawer;
    private NavigationView navigationView;
    private AppBarConfiguration mAppBarConfiguration;
    private NavController navController;

    private Set<Integer> topLevelDestinations = new HashSet<>();

    private FloatingActionButton main_fab;

    private SharedPreferences prefSettings;

    private TextView tv_version;

    private DBHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();
        setSupportActionBar(toolbar);
        db = DBHelper.getInstance(this);

        prefSettings = getDefaultSharedPreferences(this);
        prefSettings.registerOnSharedPreferenceChangeListener(this);
        setupTheme(prefSettings.getString("theme_select", "0"));

        initDrawer();

        setupNavigation();

    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if(key.equals("theme_select")) {
            setupTheme(prefSettings.getString("theme_select", "0"));
        }
    }

    // funcion para configurar el theme segun las preferencias de usuario
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

    // inicializamos las vistas
    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        main_fab = findViewById(R.id.main_fab);
        tv_version = findViewById(R.id.tv_version);
    }

    // inicializamos el drawer
    private void initDrawer() {
        drawer = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        tv_version.setText(String.format(getString(R.string.app_version), BuildConfig.VERSION_NAME));
    }

    // configuramos la navegacion con navigation component
    private void setupNavigation() {
        topLevelDestinations.add(R.id.nav_home);
        topLevelDestinations.add(R.id.nav_wishlist);
        topLevelDestinations.add(R.id.nav_genres);
        topLevelDestinations.add(R.id.nav_stats);
        topLevelDestinations.add(R.id.nav_settings);
        setupStatsItem();

        mAppBarConfiguration = new AppBarConfiguration.Builder(topLevelDestinations)
                .setOpenableLayout(drawer)
                .build();
        navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        navController.addOnDestinationChangedListener(new NavController.OnDestinationChangedListener() {
            @Override
            public void onDestinationChanged(@NonNull NavController controller, @NonNull NavDestination destination,
                                             @Nullable Bundle arguments) {
                setupDestinationsLayout(destination.getId());
            }
        });
    }

    // funcion para modificar el layout segun el destino/fragment
    private void setupDestinationsLayout(int destinationId) {
        if(destinationId == R.id.nav_wishlist || destinationId == R.id.nav_add_wishlist) {
            if(!main_fab.isShown()) {
                main_fab.show();
            }
        } else {
            if(main_fab.isShown()) {
                main_fab.setVisibility(View.INVISIBLE);
            }
        }
        // quitar drawer
        if(topLevelDestinations.contains(destinationId)) {
            drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
        } else {
            drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        }
        // wishlist
        if(destinationId == R.id.nav_wishlist) {
            main_fab.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_add_white));
        }
        // add wishlist
        if(destinationId == R.id.nav_add_wishlist) {
            main_fab.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_save_white));
        }
    }

    // get Main NavController - devuelve el NavController principal
    public NavController getMainController() {
        return navController;
    }

    // si hay libros se muestra Stats en el drawer
    // esta funcion se llama desde distintos fragments
    public void setupStatsItem() {
        if(db.getAllBooksBean().size() > 0) {
            navigationView.getMenu().getItem(3).setVisible(true);
        } else {
            navigationView.getMenu().getItem(3).setVisible(false);
        }
    }

    //
    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    // controla la pulsación de atrás por el usuario
    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        getDefaultSharedPreferences(this).registerOnSharedPreferenceChangeListener(this);
        setupStatsItem();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        getDefaultSharedPreferences(this).unregisterOnSharedPreferenceChangeListener(this);
    }
}
