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

    // Constantes para definir el tipo de vista
    public static final int TYPE_BIG_CARD = 0;
    public static final int TYPE_SMALL_CARD = 1;

    private ArrayList<ActivityModel> listData;
    private OnItemClickListener itemListener;
    private int layoutType; // Variable para saber qué diseño usar

    // Interfaz común
    public interface OnItemClickListener {
        void onItemClick(ActivityModel item, int position);
    }

    // Constructor modificado: ahora recibe el 'type'
    public ActivitiesAdapter(ArrayList<ActivityModel> listData, int type, OnItemClickListener listener) {
        this.listData = listData;
        this.layoutType = type;
        this.itemListener = listener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Aquí decidimos qué XML inflar según el tipo
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
        // Detectamos qué tipo de Holder es y llamamos a su assignData
        if (getItemViewType(position) == TYPE_BIG_CARD) {
            ((BigCardHolder) holder).assignData(listData.get(position), itemListener);
        } else {
            ((SmallCardHolder) holder).assignData(listData.get(position), itemListener);
        }
    }

    @Override
    public int getItemViewType(int position) {
        // Le dice al RecyclerView qué tipo es este item.
        // En este caso, toda la lista es del mismo tipo definido en el constructor.
        return layoutType;
    }

    @Override
    public int getItemCount() {
        return listData.size();
    }

    // --- HOLDER 1: Tarjeta Grande (Explore y Activas) ---
    public class BigCardHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvOrg, tvLocation, tvDate, tvDesc;
        ImageView imgLogo;

        public BigCardHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvOrg = itemView.findViewById(R.id.tvOrgName);
            tvLocation = itemView.findViewById(R.id.tvLocation);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvDesc = itemView.findViewById(R.id.tvDesc);
            imgLogo = itemView.findViewById(R.id.imgLogo);
        }

        public void assignData(final ActivityModel item, final OnItemClickListener listener) {
            tvTitle.setText(item.getTitle());
            tvOrg.setText(item.getOrganization());
            tvLocation.setText(item.getLocation());
            tvDate.setText(item.getDate());
            if(tvDesc != null) tvDesc.setText(item.getDescription());
            if (item.getImageResource() != 0) imgLogo.setImageResource(item.getImageResource());

            itemView.setOnClickListener(v -> listener.onItemClick(item, getAdapterPosition()));
        }
    }

    // --- HOLDER 2: Tarjeta Pequeña (Historial) ---
    public class SmallCardHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvOrg, tvLocation, tvStatus;

        public SmallCardHolder(@NonNull View itemView) {
            super(itemView);
            // IDs del layout item_small_card_activity.xml
            tvTitle = itemView.findViewById(R.id.tvTitleHistory);
            tvOrg = itemView.findViewById(R.id.tvOrg);
            tvLocation = itemView.findViewById(R.id.tvLocation);
            tvStatus = itemView.findViewById(R.id.tvStatus);
        }

        public void assignData(final ActivityModel item, final OnItemClickListener listener) {
            tvTitle.setText(item.getTitle());
            tvOrg.setText(item.getOrganization());
            tvLocation.setText(item.getLocation());
            // Simulamos estado
            if(tvStatus != null) tvStatus.setText("FINALIZADO");

            itemView.setOnClickListener(v -> listener.onItemClick(item, getAdapterPosition()));
        }
    }
}