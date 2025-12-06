package com.example.moneytrackervarela.ui.main;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.moneytrackervarela.MainActivity;
import com.example.moneytrackervarela.R;
import com.example.moneytrackervarela.adapters.TransactionAdapter;
import com.example.moneytrackervarela.database.DatabaseHelper;
import com.example.moneytrackervarela.models.Categoria;
import com.example.moneytrackervarela.models.Transaccion;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class DashboardFragment extends Fragment {

    private static final double BUDGET_ALERT_THRESHOLD = 0.80; // 80%

    private TextView welcomeTextView;
    private TextView incomeTextView;
    private TextView expensesTextView;
    private TextView balanceTextView;
    private ProgressBar budgetProgressBar;
    private TextView budgetProgressTextView;
    private TextView budgetAlertTextView;
    private RecyclerView recentTransactionsRecyclerView;
    private TransactionAdapter transactionAdapter;

    private DatabaseHelper dbHelper;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        dbHelper = new DatabaseHelper(getContext());
        return inflater.inflate(R.layout.fragment_dashboard, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        welcomeTextView = view.findViewById(R.id.welcome_textview);
        incomeTextView = view.findViewById(R.id.income_textview);
        expensesTextView = view.findViewById(R.id.expenses_textview);
        balanceTextView = view.findViewById(R.id.balance_textview);
        budgetProgressBar = view.findViewById(R.id.budget_progressbar);
        budgetProgressTextView = view.findViewById(R.id.budget_progress_textview);
        budgetAlertTextView = view.findViewById(R.id.budget_alert_textview);
        recentTransactionsRecyclerView = view.findViewById(R.id.recent_transactions_recyclerview);

        recentTransactionsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recentTransactionsRecyclerView.setNestedScrollingEnabled(false); // To ensure smooth scrolling inside the ScrollView
    }

    @Override
    public void onResume() {
        super.onResume();
        loadDashboardData();
    }

    private void loadDashboardData() {
        SharedPreferences prefs = getContext().getSharedPreferences(MainActivity.PREFS_NAME, Context.MODE_PRIVATE);

        // Load user name
        String userName = prefs.getString(InitialSetupActivity.USER_NAME_KEY, "Usuario");
        welcomeTextView.setText(String.format("Hola, %s", userName));
        
        // Load budget
        float monthlyBudget = prefs.getFloat(SettingsFragment.MONTHLY_BUDGET_KEY, 1000.0f);

        // Get date range for the current month
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        String startDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(calendar.getTime());
        calendar.add(Calendar.MONTH, 1);
        calendar.add(Calendar.DAY_OF_MONTH, -1);
        String endDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(calendar.getTime());

        // Update summary
        double totalIncome = dbHelper.getSumByType("INCOME", startDate, endDate);
        double totalExpenses = dbHelper.getSumByType("EXPENSE", startDate, endDate);
        double balance = totalIncome - totalExpenses;

        incomeTextView.setText(String.format(Locale.US, "$%.2f", totalIncome));
        expensesTextView.setText(String.format(Locale.US, "$%.2f", totalExpenses));
        balanceTextView.setText(String.format(Locale.US, "$%.2f", balance));

        // Update budget progress
        updateBudget(totalExpenses, monthlyBudget);

        // Load recent transactions
        List<Transaccion> recentTransactions = dbHelper.getFilteredTransactions(null, null, null);
        List<Categoria> allCategories = dbHelper.getAllCategorias();

        transactionAdapter = new TransactionAdapter(getContext(), recentTransactions, allCategories);
        recentTransactionsRecyclerView.setAdapter(transactionAdapter);
    }

    private void updateBudget(double totalExpenses, double monthlyBudget) {
        // Avoid division by zero if budget is 0
        int progress = (monthlyBudget > 0) ? (int) ((totalExpenses / monthlyBudget) * 100) : 0;
        budgetProgressBar.setProgress(progress);
        budgetProgressTextView.setText(String.format(Locale.US, "$%.2f / $%.2f", totalExpenses, monthlyBudget));

        if (totalExpenses > monthlyBudget) {
            budgetAlertTextView.setText("¡Presupuesto excedido!");
            budgetAlertTextView.setVisibility(View.VISIBLE);
            budgetProgressBar.setProgressDrawable(ContextCompat.getDrawable(getContext(), R.drawable.progress_bar_danger));

        } else if (totalExpenses > monthlyBudget * BUDGET_ALERT_THRESHOLD) {
            budgetAlertTextView.setText("Alerta: Límite de presupuesto cerca");
            budgetAlertTextView.setVisibility(View.VISIBLE);
            budgetProgressBar.setProgressDrawable(ContextCompat.getDrawable(getContext(), R.drawable.progress_bar_warning));

        } else {
            budgetAlertTextView.setVisibility(View.GONE);
             budgetProgressBar.setProgressDrawable(ContextCompat.getDrawable(getContext(), R.drawable.progress_bar_normal));
        }
    }

    @Override
    public void onDestroy() {
        dbHelper.close();
        super.onDestroy();
    }
}
