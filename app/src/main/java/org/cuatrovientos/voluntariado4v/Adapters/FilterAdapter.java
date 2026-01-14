package org.cuatrovientos.voluntariado4v.Adapters;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.card.MaterialCardView;

import org.cuatrovientos.voluntariado4v.R;

import java.util.List;

public class FilterAdapter extends RecyclerView.Adapter<FilterAdapter.FilterViewHolder> {

    private List<String> categories;
    private OnCategoryClickListener listener;
    private int selectedPosition = 0; // Por defecto el primero ("Todos") está seleccionado

    // Interfaz para comunicar el clic a la Actividad
    public interface OnCategoryClickListener {
        void onCategoryClick(String category);
    }

    public FilterAdapter(List<String> categories, OnCategoryClickListener listener) {
        this.categories = categories;
        this.listener = listener;
    }

    @NonNull
    @Override
    public FilterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_filter, parent, false);
        return new FilterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FilterViewHolder holder, int position) {
        String category = categories.get(position);
        holder.tvFilterName.setText(category);

        // Lógica Visual: Negro si está seleccionado, Blanco/Gris si no
        if (selectedPosition == position) {
            holder.cardFilter.setCardBackgroundColor(Color.parseColor("#101828")); // Negro
            holder.tvFilterName.setTextColor(Color.WHITE);
            holder.cardFilter.setStrokeWidth(0);
        } else {
            holder.cardFilter.setCardBackgroundColor(Color.WHITE);
            holder.tvFilterName.setTextColor(Color.parseColor("#757575")); // Gris texto
            holder.cardFilter.setStrokeColor(Color.parseColor("#E0E0E0")); // Borde gris
            holder.cardFilter.setStrokeWidth(2);
        }

        holder.itemView.setOnClickListener(v -> {
            int previousItem = selectedPosition;
            selectedPosition = holder.getAdapterPosition();
            notifyItemChanged(previousItem);
            notifyItemChanged(selectedPosition);
            listener.onCategoryClick(category);
        });
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    public static class FilterViewHolder extends RecyclerView.ViewHolder {
        TextView tvFilterName;
        MaterialCardView cardFilter;

        public FilterViewHolder(@NonNull View itemView) {
            super(itemView);
            // Asegúrate de que estos IDs existan en tu 'item_filter.xml'
            tvFilterName = itemView.findViewById(R.id.tvFilterName);
            cardFilter = itemView.findViewById(R.id.cardFilter);
        }
    }
}