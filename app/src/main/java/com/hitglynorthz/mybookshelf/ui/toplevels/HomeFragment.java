package com.hitglynorthz.mybookshelf.ui.toplevels;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleEventObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;
import androidx.preference.PreferenceManager;

import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.hitglynorthz.mybookshelf.MainActivity;
import com.hitglynorthz.mybookshelf.R;
import com.hitglynorthz.mybookshelf.db.DBHelper;

import static androidx.preference.PreferenceManager.getDefaultSharedPreferences;

public class HomeFragment extends Fragment implements SharedPreferences.OnSharedPreferenceChangeListener {
    private View rootView;
    private Context context;

    private Toolbar toolbar;
    private BottomNavigationView bottomNavigationView;
    private NavController navController, mainController;

    private FloatingActionButton bfab;

    private BadgeDrawable badgeBooks, badgeAuthors, badgePublishers, badgeShelves;

    private SharedPreferences prefSettings;

    private DBHelper db;

    private static HomeFragment instance = null;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        instance = this;
    }

    public static HomeFragment getInstance() {
        return instance;
    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_home, container, false);
        context = getContext();
        db = DBHelper.getInstance(context);

        initViews();

        checkPrefSettings();

        setupFab();

        // se usa para mostrar snackbar al añadir o eliminar libros/autores/editoriales etc
        mainController = ((MainActivity)context).getMainController();
        MutableLiveData<String> liveData = mainController.getCurrentBackStackEntry()
                .getSavedStateHandle()
                .getLiveData("update_home");
        liveData.observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                setupUpdateHome(s);
                mainController.getCurrentBackStackEntry().getSavedStateHandle().remove("update_home");
            }
        });

        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // para que esto funcione los id tanto del navgraph como del menu tienen que coincidir
        navController = Navigation.findNavController(requireActivity(), R.id.nav_tab_fragment);
        NavigationUI.setupWithNavController(bottomNavigationView, navController);
    }

    // funcion para comprobar las preferencias de usuario
    // para mostrar u ocultar las badges en bottomnavigationview
    private void checkPrefSettings() {
        prefSettings = getDefaultSharedPreferences(context);
        prefSettings.registerOnSharedPreferenceChangeListener(this);
        setupBadges(prefSettings.getBoolean("badge_select", false));
    }

    // inicializamos las vistas
    private void initViews() {
        toolbar = getActivity().findViewById(R.id.toolbar);
        bottomNavigationView = rootView.findViewById(R.id.bottomNavigationView);
        bfab = rootView.findViewById(R.id.bfab);
    }

    // configuracion del fab
    private void setupFab() {
        bfab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bfab.hide();
                selectAddDialog();
            }
        });
    }

    // alertdialog para la seleccion de añadir libro, estanteria
    private void selectAddDialog() {
        MaterialAlertDialogBuilder selectAdd = new MaterialAlertDialogBuilder(context, R.style.MaterialDialog);
        CharSequence[] choices = {getString(R.string.dialog_select_add_book), getString(R.string.dialog_select_add_shelves), getString(R.string.dialog_select_add_wishlist)};
        selectAdd.setItems(choices, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch(which) {
                    case 0:
                        ((MainActivity)getActivity()).getMainController().navigate(R.id.action_global_nav_add_book);
                        dialog.dismiss();
                        break;
                    case 1:
                        ((MainActivity)getActivity()).getMainController().navigate(R.id.action_global_nav_add_shelves);
                        dialog.dismiss();
                        break;
                    case 2:
                        ((MainActivity)getActivity()).getMainController().navigate(R.id.action_global_nav_wishlist);
                        dialog.dismiss();
                        break;
                }
            }
        });
        selectAdd.setTitle(getString(R.string.dialog_select_add_title));
        selectAdd.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                bfab.show();
            }
        });
        selectAdd.show();
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if(key.equals("badge_select")) {
            setupBadges(prefSettings.getBoolean("badge_select", false));
        }
    }

    // funcion para configurar las badges
    private void setupBadges(boolean badgeValue) {
        badgeBooks = bottomNavigationView.getOrCreateBadge(R.id.bnav_books);
        badgeAuthors = bottomNavigationView.getOrCreateBadge(R.id.bnav_authors);
        badgePublishers = bottomNavigationView.getOrCreateBadge(R.id.bnav_publishers);
        badgeShelves = bottomNavigationView.getOrCreateBadge(R.id.bnav_shelves);
        badgeBooks.setNumber(db.getAllBooksBean().size());
        badgeAuthors.setNumber(db.getAllAuthors().size());
        badgePublishers.setNumber(db.getAllPublishers().size());
        badgeShelves.setNumber(db.getAllShelvesAZ().size());
        badgeShelves.setBackgroundColor(ContextCompat.getColor(context, R.color.colorSecondary));
        badgePublishers.setBackgroundColor(ContextCompat.getColor(context, R.color.colorSecondary));
        badgeAuthors.setBackgroundColor(ContextCompat.getColor(context, R.color.colorSecondary));
        badgeBooks.setBackgroundColor(ContextCompat.getColor(context, R.color.colorSecondary));
        if(badgeValue) {
            badgeBooks.setVisible(true);
            badgeAuthors.setVisible(true);
            badgePublishers.setVisible(true);
            badgeShelves.setVisible(true);
        } else {
            bottomNavigationView.removeBadge(R.id.bnav_books);
            bottomNavigationView.removeBadge(R.id.bnav_authors);
            bottomNavigationView.removeBadge(R.id.bnav_publishers);
            bottomNavigationView.removeBadge(R.id.bnav_shelves);
            badgeBooks.setVisible(false);
            badgeAuthors.setVisible(false);
            badgePublishers.setVisible(false);
            badgeShelves.setVisible(false);
        }
    }

    // funcion para actualizar elementos de HomeFragment desde otros fragments o adapters
    public void setupUpdateHome(String text) {
        Snackbar.make(getActivity().getWindow().getDecorView().findViewById(android.R.id.content), text, Snackbar.LENGTH_SHORT).setAnchorView(bfab).show();
        checkPrefSettings();
    }

    // funcion para usar el FAB de HomeFragment desde otros fragments o adapters
    public FloatingActionButton getBfab() {
        return bfab;
    }

    @Override
    public void onResume() {
        super.onResume();
        setupBadges(prefSettings.getBoolean("badge_select", false));
        // actualizar el drawer
        ((MainActivity)context).setupStatsItem();
    }
}
