package org.cuatrovientos.voluntariado4v;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class CategoriesAdapter extends RecyclerView.Adapter<CategoriesAdapter.CategoryViewHolder> {

    private ArrayList<String> categories;
    private int selectedPosition = 0; // Por defecto el primero ("Todo") está seleccionado
    private OnCategoryClickListener listener;

    public interface OnCategoryClickListener {
        void onCategoryClick(String category);
    }

    public CategoriesAdapter(ArrayList<String> categories, OnCategoryClickListener listener) {
        this.categories = categories;
        this.listener = listener;
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_filter, parent, false);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        String category = categories.get(position);
        holder.tvName.setText(category);

        // Lógica visual de selección
        if (selectedPosition == position) {
            // ESTILO SELECCIONADO (Azul y texto blanco)
            // Nota: Usamos bg_chip_selected si lo tienes, o un color directo para simplificar
            holder.tvName.setBackgroundResource(R.drawable.bg_tag_blue);
            holder.tvName.setTextColor(Color.WHITE);
        } else {
            // ESTILO NO SELECCIONADO (Gris y texto gris oscuro)
            holder.tvName.setBackgroundResource(R.drawable.bg_rounded_gray);
            holder.tvName.setTextColor(Color.parseColor("#667085"));
        }

        holder.itemView.setOnClickListener(v -> {
            // Actualizamos la posición seleccionada
            int previousPosition = selectedPosition;
            selectedPosition = holder.getAdapterPosition();

            // Notificamos cambios para refrescar colores
            notifyItemChanged(previousPosition);
            notifyItemChanged(selectedPosition);

            // Avisamos a la actividad
            if (listener != null) {
                listener.onCategoryClick(category);
            }
        });
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    public static class CategoryViewHolder extends RecyclerView.ViewHolder {
        TextView tvName;

        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvFilterName);
        }
    }
}