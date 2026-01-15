package org.cuatrovientos.voluntariado4v.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import org.cuatrovientos.voluntariado4v.Models.ActivityModel;
import org.cuatrovientos.voluntariado4v.R;
import java.util.ArrayList;

public class ActivitiesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public static final int TYPE_BIG_CARD = 0;
    public static final int TYPE_SMALL_CARD = 1;

    private ArrayList<ActivityModel> listData;
    private OnItemClickListener itemListener;
    private int layoutType;

    // Interfaz para clicks
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

    public void updateData(ArrayList<ActivityModel> newList) {
        this.listData = newList;
        notifyDataSetChanged();
    }

    // --- MÉTODO RECUPERADO (Soluciona el error en UserActivities) ---
    public ArrayList<ActivityModel> getDataList() {
        return listData;
    }

    // --- HOLDERS ---

    // Holder para Tarjetas Grandes (Voluntario - Dashboard/Mis Actividades)
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

            if (item.getImageResource() != 0) {
                imgLogo.setImageResource(item.getImageResource());
            }

            // Categoría y color
            if (tvCategory != null) {
                tvCategory.setText(item.getCategory());
                if (item.getCategory().equalsIgnoreCase("medioambiente")) {
                    tvCategory.setBackgroundResource(R.drawable.bg_tag_green);
                } else if (item.getCategory().equalsIgnoreCase("educación")) {
                    tvCategory.setBackgroundResource(R.drawable.bg_tag_orange);
                } else {
                    tvCategory.setBackgroundResource(R.drawable.bg_tag_blue);
                }
            }

            if (listener != null) {
                itemView.setOnClickListener(v -> listener.onItemClick(item, getAdapterPosition()));
            }
        }
    }

    // Holder para Tarjetas Pequeñas (Organización y Perfiles)
    public class SmallCardHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvLocation, tvDate, tvStatus;
        ImageView imgActivity;

        public SmallCardHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvLocation = itemView.findViewById(R.id.tvLocation);
            tvDate = itemView.findViewById(R.id.tvDate);
            imgActivity = itemView.findViewById(R.id.imgActivity);
            tvStatus = itemView.findViewById(R.id.tvStatus); // TextView para el estado
        }

        public void assignData(final ActivityModel item, final OnItemClickListener listener) {
            tvTitle.setText(item.getTitle());
            tvLocation.setText(item.getLocation());
            if (tvDate != null) tvDate.setText(item.getDate());

            if (imgActivity != null && item.getImageResource() != 0) {
                imgActivity.setImageResource(item.getImageResource());
            }

            // Lógica visual del Estado (Activa, Finalizada, Cancelada)
            if (tvStatus != null) {
                String status = item.getStatus();
                if (status != null && !status.isEmpty()) {
                    tvStatus.setVisibility(View.VISIBLE);
                    switch (status.toUpperCase()) {
                        case "ACTIVE":
                            tvStatus.setText("ACTIVA");
                            tvStatus.setBackgroundResource(R.drawable.bg_tag_green);
                            break;
                        case "FINISHED":
                            tvStatus.setText("FINALIZADA");
                            tvStatus.setBackgroundResource(R.drawable.bg_tag_blue);
                            break;
                        case "CANCELLED":
                            tvStatus.setText("CANCELADA");
                            tvStatus.setBackgroundResource(R.drawable.bg_tag_red);
                            break;
                        default:
                            tvStatus.setVisibility(View.GONE);
                    }
                } else {
                    tvStatus.setVisibility(View.GONE);
                }
            }

            if (listener != null) {
                itemView.setOnClickListener(v -> listener.onItemClick(item, getAdapterPosition()));
            }
        }
    }
}