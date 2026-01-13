package org.cuatrovientos.voluntariado4v;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ActivitiesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public static final int TYPE_BIG_CARD = 0;
    public static final int TYPE_SMALL_CARD = 1;

    private ArrayList<ActivityModel> listData;
    private OnItemClickListener itemListener;
    private int layoutType;

    // Interfaz (puede ser null ahora)
    public interface OnItemClickListener {
        void onItemClick(ActivityModel item, int position);
    }

    public ActivitiesAdapter(ArrayList<ActivityModel> listData, int type, OnItemClickListener listener) {
        this.listData = listData;
        this.layoutType = type;
        this.itemListener = listener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TYPE_BIG_CARD) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_big_card_activity, parent, false);
            return new BigCardHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_small_card_activity, parent, false);
            return new SmallCardHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (getItemViewType(position) == TYPE_BIG_CARD) {
            ((BigCardHolder) holder).assignData(listData.get(position), itemListener);
        } else {
            ((SmallCardHolder) holder).assignData(listData.get(position), itemListener);
        }
    }

    @Override
    public int getItemViewType(int position) {
        return layoutType;
    }

    @Override
    public int getItemCount() {
        return listData.size();
    }

    // --- HOLDER 1: Tarjeta Grande ---
    public class BigCardHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvOrg, tvLocation, tvDate, tvDesc, tvCategory;
        ImageView imgLogo;

        public BigCardHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvOrg = itemView.findViewById(R.id.tvOrgName);
            tvLocation = itemView.findViewById(R.id.tvLocation);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvDesc = itemView.findViewById(R.id.tvDesc);
            imgLogo = itemView.findViewById(R.id.imgLogo);
            tvCategory = itemView.findViewById(R.id.tvTagCategory);
        }

        public void assignData(final ActivityModel item, final OnItemClickListener listener) {
            tvTitle.setText(item.getTitle());
            tvOrg.setText(item.getOrganization());
            tvLocation.setText(item.getLocation());
            tvDate.setText(item.getDate());
            if(tvDesc != null) tvDesc.setText(item.getDescription());
            if (item.getImageResource() != 0) imgLogo.setImageResource(item.getImageResource());

            // Asignar Categoría y Color
            if (tvCategory != null) {
                tvCategory.setText(item.getCategory());
                updateCategoryColor(item.getCategory(), tvCategory);
            }

            // LÓGICA DE CLICK: Solo si hay listener, si es null no hace nada
            if (listener != null) {
                itemView.setOnClickListener(v -> listener.onItemClick(item, getAdapterPosition()));
                itemView.setClickable(true);
            } else {
                itemView.setOnClickListener(null);
                itemView.setClickable(false); // Desactiva el efecto visual de click
            }
        }

        private void updateCategoryColor(String category, TextView tv) {
            if (category == null) return;
            switch (category.toLowerCase()) {
                case "medioambiente": tv.setBackgroundResource(R.drawable.bg_tag_green); break;
                case "educación": tv.setBackgroundResource(R.drawable.bg_tag_orange); break;
                case "social": default: tv.setBackgroundResource(R.drawable.bg_tag_blue); break;
            }
        }
    }

    // --- HOLDER 2: Tarjeta Pequeña ---
    public class SmallCardHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvOrg, tvLocation, tvStatus;

        public SmallCardHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitleHistory);
            tvLocation = itemView.findViewById(R.id.tvLocation);
        }

        public void assignData(final ActivityModel item, final OnItemClickListener listener) {
            tvTitle.setText(item.getTitle());
            tvLocation.setText(item.getLocation());

            if (listener != null) {
                itemView.setOnClickListener(v -> listener.onItemClick(item, getAdapterPosition()));
                itemView.setClickable(true);
            } else {
                itemView.setOnClickListener(null);
                itemView.setClickable(false);
            }
        }
    }
}