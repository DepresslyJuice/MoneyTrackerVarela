package com.example.moneytrackervarela;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.moneytrackervarela.ui.forms.TransactionFormActivity;
import com.example.moneytrackervarela.ui.main.DashboardFragment;
import com.example.moneytrackervarela.ui.main.ExchangeRateFragment;
import com.example.moneytrackervarela.ui.main.InitialSetupActivity;
import com.example.moneytrackervarela.ui.main.SettingsFragment;
import com.example.moneytrackervarela.ui.main.StatsFragment;
import com.example.moneytrackervarela.ui.main.TransactionsFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MainActivity extends AppCompatActivity {

    public static final String PREFS_NAME = "MoneyTrackerPrefs";
    public static final String IS_FIRST_RUN_KEY = "isFirstRun";

    private BottomNavigationView bottomNavigationView;
    private FloatingActionButton fabAddTransaction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        boolean isFirstRun = prefs.getBoolean(IS_FIRST_RUN_KEY, true);

        if (isFirstRun) {
            startActivity(new Intent(MainActivity.this, InitialSetupActivity.class));
            finish();
            return;
        }

        setContentView(R.layout.activity_main);

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        fabAddTransaction = findViewById(R.id.fab_add_transaction);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            int itemId = item.getItemId();
            if (itemId == R.id.nav_dashboard) {
                selectedFragment = new DashboardFragment();
            } else if (itemId == R.id.nav_transactions) {
                selectedFragment = new TransactionsFragment();
            } else if (itemId == R.id.nav_stats) {
                selectedFragment = new StatsFragment();
            } else if (itemId == R.id.nav_exchange) {
                selectedFragment = new ExchangeRateFragment();
            } else if (itemId == R.id.nav_settings) {
                selectedFragment = new SettingsFragment();
            }

            if (selectedFragment != null) {
                loadFragment(selectedFragment);
            }
            return true;
        });

        fabAddTransaction.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, TransactionFormActivity.class));
        });

        if (savedInstanceState == null) {
            loadFragment(new DashboardFragment());
        }
    }

    private void loadFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, fragment);
        fragmentTransaction.commit();
    }
}
