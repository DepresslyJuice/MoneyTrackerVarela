package com.example.moneytrackervarela.ui.main;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.moneytrackervarela.MainActivity;
import com.example.moneytrackervarela.R;
import com.google.android.material.textfield.TextInputEditText;

public class SettingsFragment extends Fragment {

    public static final String MONTHLY_BUDGET_KEY = "monthlyBudget";

    private TextInputEditText nameEditText;
    private TextInputEditText budgetEditText;
    private Button saveButton;
    private SharedPreferences prefs;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        prefs = getContext().getSharedPreferences(MainActivity.PREFS_NAME, Context.MODE_PRIVATE);

        nameEditText = view.findViewById(R.id.settings_name_edittext);
        budgetEditText = view.findViewById(R.id.settings_budget_edittext);
        saveButton = view.findViewById(R.id.settings_save_button);

        loadSettings();

        saveButton.setOnClickListener(v -> saveSettings());
    }

    private void loadSettings() {
        String currentName = prefs.getString(InitialSetupActivity.USER_NAME_KEY, "");
        float currentBudget = prefs.getFloat(MONTHLY_BUDGET_KEY, 1000.0f); // Default 1000

        nameEditText.setText(currentName);
        budgetEditText.setText(String.valueOf(currentBudget));
    }

    private void saveSettings() {
        String newName = nameEditText.getText().toString().trim();
        String newBudgetString = budgetEditText.getText().toString().trim();

        if (newName.isEmpty() || newBudgetString.isEmpty()) {
            Toast.makeText(getContext(), "Los campos no pueden estar vacíos", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            float newBudget = Float.parseFloat(newBudgetString);

            SharedPreferences.Editor editor = prefs.edit();
            editor.putString(InitialSetupActivity.USER_NAME_KEY, newName);
            editor.putFloat(MONTHLY_BUDGET_KEY, newBudget);
            editor.apply();

            Toast.makeText(getContext(), "Configuración guardada exitosamente", Toast.LENGTH_SHORT).show();

        } catch (NumberFormatException e) {
            Toast.makeText(getContext(), "El presupuesto debe ser un número válido", Toast.LENGTH_SHORT).show();
        }
    }
}
