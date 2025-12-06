package com.example.moneytrackervarela.ui.main;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.moneytrackervarela.MainActivity;
import com.example.moneytrackervarela.R;
import com.google.android.material.textfield.TextInputEditText;

public class InitialSetupActivity extends AppCompatActivity {

    public static final String USER_NAME_KEY = "userName";

    private TextInputEditText nameEditText;
    private Button saveButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_initial_setup);

        nameEditText = findViewById(R.id.name_edittext);
        saveButton = findViewById(R.id.save_button);

        saveButton.setOnClickListener(v -> {
            String name = nameEditText.getText().toString().trim();
            if (name.isEmpty()) {
                Toast.makeText(this, "Por favor, ingresa tu nombre", Toast.LENGTH_SHORT).show();
                return;
            }

            // Save data to SharedPreferences
            SharedPreferences prefs = getSharedPreferences(MainActivity.PREFS_NAME, MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean(MainActivity.IS_FIRST_RUN_KEY, false);
            editor.putString(USER_NAME_KEY, name);
            // TODO: Also save monthly budget, currency, and month start day here later
            editor.apply();

            // Go to MainActivity
            Intent intent = new Intent(InitialSetupActivity.this, MainActivity.class);
            startActivity(intent);
            finish(); // Finish this activity so the user can't go back to it
        });
    }
}
