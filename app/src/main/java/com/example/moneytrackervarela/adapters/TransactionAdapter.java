package com.example.moneytrackervarela.adapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.moneytrackervarela.R;
import com.example.moneytrackervarela.models.Categoria;
import com.example.moneytrackervarela.models.Transaccion;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder> {

    private List<Transaccion> transactionList;
    private Map<Integer, Categoria> categoryMap;
    private Context context;

    public TransactionAdapter(Context context, List<Transaccion> transactionList, List<Categoria> categoryList) {
        this.context = context;
        this.transactionList = transactionList;
        this.categoryMap = categoryList.stream().collect(Collectors.toMap(Categoria::getId, categoria -> categoria));
    }

    @NonNull
    @Override
    public TransactionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.transaction_item, parent, false);
        return new TransactionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TransactionViewHolder holder, int position) {
        Transaccion transaction = transactionList.get(position);
        Categoria category = categoryMap.get(transaction.getIdCategoria());

        holder.descriptionTextView.setText(transaction.getDescripcion());
        holder.dateTextView.setText(transaction.getFecha());

        if (category != null) {
            holder.categoryNameTextView.setText(category.getNombre());
            // Set icon and background color
            // This is a simplified example. You might need a more robust way to handle icons.
            int iconResourceId = context.getResources().getIdentifier("ic_category_" + category.getIcon(), "drawable", context.getPackageName());
            if (iconResourceId != 0) {
                holder.categoryIconImageView.setImageResource(iconResourceId);
            }
            
            GradientDrawable background = (GradientDrawable) holder.categoryIconImageView.getBackground();
            try {
                background.setColor(Color.parseColor(category.getColor()));
            } catch (IllegalArgumentException e) {
                background.setColor(Color.LTGRAY);
            }

        } else {
            holder.categoryNameTextView.setText("Sin categoría");
        }

        if ("EXPENSE".equals(transaction.getTipo())) {
            holder.amountTextView.setText(String.format("-$%.2f", transaction.getMonto()));
            holder.amountTextView.setTextColor(context.getResources().getColor(android.R.color.holo_red_dark));
        } else {
            holder.amountTextView.setText(String.format("+$%.2f", transaction.getMonto()));
            holder.amountTextView.setTextColor(context.getResources().getColor(android.R.color.holo_green_dark));
        }
    }

    @Override
    public int getItemCount() {
        return transactionList.size();
    }

    public static class TransactionViewHolder extends RecyclerView.ViewHolder {
        ImageView categoryIconImageView;
        TextView categoryNameTextView;
        TextView descriptionTextView;
        TextView amountTextView;
        TextView dateTextView;

        public TransactionViewHolder(@NonNull View itemView) {
            super(itemView);
            categoryIconImageView = itemView.findViewById(R.id.category_icon_imageview);
            categoryNameTextView = itemView.findViewById(R.id.category_name_textview);
            descriptionTextView = itemView.findViewById(R.id.description_textview);
            amountTextView = itemView.findViewById(R.id.amount_textview);
            dateTextView = itemView.findViewById(R.id.date_textview);
        }
    }
}
