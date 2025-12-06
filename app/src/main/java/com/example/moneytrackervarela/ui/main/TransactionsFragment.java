package com.example.moneytrackervarela.ui.main;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.moneytrackervarela.R;
import com.example.moneytrackervarela.database.DatabaseHelper;
import com.example.moneytrackervarela.models.Transaccion;
import com.example.moneytrackervarela.ui.forms.TransactionFormActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class TransactionsFragment extends Fragment {

    private RecyclerView recyclerView;
    private TransactionAdapter transactionAdapter;
    private List<Transaccion> transactionList = new ArrayList<>();
    private DatabaseHelper dbHelper;

    private final ActivityResultLauncher<Intent> transactionFormLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    refreshTransactionList();
                }
            });

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_transactions, container, false);

        recyclerView = view.findViewById(R.id.transactions_recycler_view);
        FloatingActionButton fab = view.findViewById(R.id.fab_add_transaction);

        dbHelper = new DatabaseHelper(getContext());
        transactionAdapter = new TransactionAdapter(getContext(), transactionList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(transactionAdapter);

        fab.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), TransactionFormActivity.class);
            transactionFormLauncher.launch(intent);
        });

        ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                transactionAdapter.removeItem(position);
                Toast.makeText(getContext(), "Transaction deleted", Toast.LENGTH_SHORT).show();
            }
        };

        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(recyclerView);

        refreshTransactionList(); // Initial load

        return view;
    }

    private void refreshTransactionList() {
        transactionList.clear();
        transactionList.addAll(dbHelper.getFilteredTransactions(null, null, null));
        transactionAdapter.notifyDataSetChanged();
    }
}
