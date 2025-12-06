package com.example.moneytrackervarela.ui.main;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.moneytrackervarela.R;
import com.example.moneytrackervarela.database.DatabaseHelper;
import com.example.moneytrackervarela.models.Categoria;
import com.example.moneytrackervarela.models.Transaccion;

import java.util.List;

public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder> {

    private List<Transaccion> transactionList;
    private DatabaseHelper dbHelper;
    private Context context;

    public TransactionAdapter(Context context, List<Transaccion> transactionList) {
        this.context = context;
        this.transactionList = transactionList;
        this.dbHelper = new DatabaseHelper(context);
    }

    @NonNull
    @Override
    public TransactionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_transaction, parent, false);
        return new TransactionViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull TransactionViewHolder holder, int position) {
        Transaccion transaccion = transactionList.get(position);
        holder.description.setText(transaccion.getDescripcion());

        Categoria categoria = dbHelper.getCategoria(transaccion.getIdCategoria());
        if (categoria != null) {
            holder.category.setText(categoria.getNombre());
            // Aquí puedes establecer el ícono de la categoría si lo tienes
        }

        if (transaccion.getTipo().equals("INCOME")) {
            holder.amount.setText(String.format("+$%.2f", transaccion.getMonto()));
            holder.amount.setTextColor(ContextCompat.getColor(context, android.R.color.holo_green_dark));
        } else {
            holder.amount.setText(String.format("-$%.2f", transaccion.getMonto()));
            holder.amount.setTextColor(ContextCompat.getColor(context, android.R.color.holo_red_dark));
        }

        holder.date.setText(transaccion.getFecha());
    }

    @Override
    public int getItemCount() {
        return transactionList.size();
    }

    public void removeItem(int position) {
        Transaccion transaccion = transactionList.get(position);
        dbHelper.deleteTransaccion(transaccion.getId());
        transactionList.remove(position);
        notifyItemRemoved(position);
    }

    public static class TransactionViewHolder extends RecyclerView.ViewHolder {
        public TextView description, category, amount, date;
        public ImageView categoryIcon;

        public TransactionViewHolder(View view) {
            super(view);
            description = view.findViewById(R.id.transaction_description);
            category = view.findViewById(R.id.transaction_category);
            amount = view.findViewById(R.id.transaction_amount);
            date = view.findViewById(R.id.transaction_date);
            categoryIcon = view.findViewById(R.id.category_icon);
        }
    }
}
