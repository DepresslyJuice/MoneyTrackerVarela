package com.example.moneytrackervarela.ui.forms;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.moneytrackervarela.R;
import com.example.moneytrackervarela.database.DatabaseHelper;
import com.example.moneytrackervarela.models.Categoria;
import com.example.moneytrackervarela.models.Transaccion;
import com.google.android.material.textfield.TextInputEditText;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class TransactionFormActivity extends AppCompatActivity {

    private RadioGroup typeRadioGroup;
    private RadioButton expenseRadioButton;
    private TextInputEditText amountEditText;
    private AutoCompleteTextView categoryAutoComplete;
    private TextInputEditText descriptionEditText;
    private TextInputEditText dateEditText;
    private AutoCompleteTextView paymentMethodAutoComplete;
    private Button saveTransactionButton;

    private DatabaseHelper dbHelper;
    private List<Categoria> allCategories;
    private ArrayAdapter<Categoria> categoryAdapter;
    private Calendar selectedDate = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_form);

        dbHelper = new DatabaseHelper(this);

        // Initialize views
        typeRadioGroup = findViewById(R.id.type_radiogroup);
        expenseRadioButton = findViewById(R.id.expense_radiobutton);
        amountEditText = findViewById(R.id.amount_edittext);
        categoryAutoComplete = findViewById(R.id.category_actv);
        descriptionEditText = findViewById(R.id.description_edittext);
        dateEditText = findViewById(R.id.date_edittext);
        paymentMethodAutoComplete = findViewById(R.id.payment_method_actv);
        saveTransactionButton = findViewById(R.id.save_transaction_button);

        // Setup UI components
        setupCategorySpinner();
        setupPaymentMethodSpinner();
        setupDatePicker();

        typeRadioGroup.setOnCheckedChangeListener((group, checkedId) -> updateCategorySpinner());

        saveTransactionButton.setOnClickListener(v -> saveTransaction());
        
        // Set default date
        updateDateEditText();
    }

    private void setupCategorySpinner() {
        allCategories = dbHelper.getAllCategorias();
        updateCategorySpinner(); // Initial population
    }

    private void updateCategorySpinner() {
        String selectedType = expenseRadioButton.isChecked() ? "EXPENSE" : "INCOME";
        List<Categoria> filteredCategories = allCategories.stream()
                .filter(c -> selectedType.equals(c.getTipo()))
                .collect(Collectors.toList());

        categoryAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, filteredCategories);
        categoryAutoComplete.setAdapter(categoryAdapter);
        categoryAutoComplete.setText("", false); // Clear selection when type changes
    }

    private void setupPaymentMethodSpinner() {
        String[] paymentMethods = {"Efectivo", "Tarjeta de Crédito", "Tarjeta de Débito", "Transferencia Bancaria"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, paymentMethods);
        paymentMethodAutoComplete.setAdapter(adapter);
    }

    private void setupDatePicker() {
        dateEditText.setOnClickListener(v -> {
            DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                    (view, year, month, dayOfMonth) -> {
                        selectedDate.set(year, month, dayOfMonth);
                        updateDateEditText();
                    },
                    selectedDate.get(Calendar.YEAR),
                    selectedDate.get(Calendar.MONTH),
                    selectedDate.get(Calendar.DAY_OF_MONTH));
            datePickerDialog.show();
        });
    }

    private void updateDateEditText() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        dateEditText.setText(sdf.format(selectedDate.getTime()));
    }

    private void saveTransaction() {
        // --- Validation ---
        if (amountEditText.getText().toString().isEmpty()) {
            Toast.makeText(this, "El monto no puede estar vacío", Toast.LENGTH_SHORT).show();
            return;
        }
        if (categoryAutoComplete.getText().toString().isEmpty()) {
            Toast.makeText(this, "Debes seleccionar una categoría", Toast.LENGTH_SHORT).show();
            return;
        }

        // --- Get Data ---
        String type = expenseRadioButton.isChecked() ? "EXPENSE" : "INCOME";
        double amount = Double.parseDouble(amountEditText.getText().toString());
        String description = descriptionEditText.getText().toString();
        String date = dateEditText.getText().toString();
        String paymentMethod = paymentMethodAutoComplete.getText().toString();

        // Find selected category object to get its ID
        String categoryName = categoryAutoComplete.getText().toString();
        Categoria selectedCategory = allCategories.stream()
                .filter(c -> categoryName.equals(c.getNombre()))
                .findFirst().orElse(null);
        
        if (selectedCategory == null) {
             Toast.makeText(this, "Categoría inválida", Toast.LENGTH_SHORT).show();
            return;
        }

        // --- Create and Save Transaction ---
        Transaccion newTransaction = new Transaccion();
        newTransaction.setTipo(type);
        newTransaction.setMonto(amount);
        newTransaction.setIdCategoria(selectedCategory.getId());
        newTransaction.setDescripcion(description);
        newTransaction.setFecha(date);
        newTransaction.setPaymentMethod(paymentMethod);

        long result = dbHelper.addTransaccion(newTransaction);

        if (result != -1) {
            Toast.makeText(this, "Transacción guardada exitosamente", Toast.LENGTH_SHORT).show();
            finish(); // Close activity and go back
        } else {
            Toast.makeText(this, "Error al guardar la transacción", Toast.LENGTH_SHORT).show();
        }
    }
    
    @Override
    protected void onDestroy() {
        dbHelper.close();
        super.onDestroy();
    }
}
