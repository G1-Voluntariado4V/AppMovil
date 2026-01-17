package org.cuatrovientos.voluntariado4v.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import org.cuatrovientos.voluntariado4v.Models.ActividadResponse;
import org.cuatrovientos.voluntariado4v.R;

import java.util.ArrayList;
import java.util.List;

public class ActividadesApiAdapter extends RecyclerView.Adapter<ActividadesApiAdapter.ViewHolder> {

    private List<ActividadResponse> actividades;
    private OnItemClickListener listener;
    private OnItemClickListener editListener;
    private boolean showEditButton = false;

    public interface OnItemClickListener {
        void onItemClick(ActividadResponse actividad, int position);
    }

    public ActividadesApiAdapter(List<ActividadResponse> actividades, OnItemClickListener listener) {
        this.actividades = actividades != null ? actividades : new ArrayList<>();
        this.listener = listener;
        this.editListener = null;
        this.showEditButton = false;
    }

    public ActividadesApiAdapter(List<ActividadResponse> actividades, OnItemClickListener listener,
            OnItemClickListener editListener, boolean showEditButton) {
        this.actividades = actividades != null ? actividades : new ArrayList<>();
        this.listener = listener;
        this.editListener = editListener;
        this.showEditButton = showEditButton;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_big_card_activity, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(actividades.get(position), listener, editListener, showEditButton);
    }

    @Override
    public int getItemCount() {
        return actividades.size();
    }

    public void updateData(List<ActividadResponse> newData) {
        this.actividades = newData != null ? newData : new ArrayList<>();
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvOrg, tvLocation, tvDate, tvDesc, tvCategory;
        ImageView imgLogo;
        ImageButton btnEdit;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvOrg = itemView.findViewById(R.id.tvOrgName);
            tvLocation = itemView.findViewById(R.id.tvLocation);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvDesc = itemView.findViewById(R.id.tvDesc);
            imgLogo = itemView.findViewById(R.id.imgLogo);
            tvCategory = itemView.findViewById(R.id.tvTagCategory);
            btnEdit = itemView.findViewById(R.id.btnEdit);
        }

        private String formatFecha(String fechaCompleta) {
            if (fechaCompleta == null || fechaCompleta.isEmpty())
                return "";
            try {
                // Limpiar formato API: 2026-01-20 17:00:00.000000 -> 2026-01-20 17:00:00
                String cleanDate = fechaCompleta;
                if (cleanDate.length() > 19) {
                    cleanDate = cleanDate.substring(0, 19);
                }
                cleanDate = cleanDate.replace("T", " ");

                java.text.SimpleDateFormat inputFormat = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss",
                        java.util.Locale.getDefault());
                java.util.Date date = inputFormat.parse(cleanDate);

                // Salida: 20 de Enero
                java.text.SimpleDateFormat outputFormat = new java.text.SimpleDateFormat("d 'de' MMMM",
                        new java.util.Locale("es", "ES"));
                return outputFormat.format(date);
            } catch (Exception e) {
                android.util.Log.e("Adapter", "Error parseando fecha: " + fechaCompleta, e);
                // Fallback a YYYY-MM-DD
                try {
                    return fechaCompleta.substring(0, 10);
                } catch (Exception ex) {
                    return fechaCompleta;
                }
            }
        }

        void bind(ActividadResponse item, OnItemClickListener listener,
                OnItemClickListener editListener, boolean showEditButton) {
            tvTitle.setText(item.getTitulo());
            tvOrg.setText(item.getNombreOrganizacion());
            tvLocation.setText(item.getUbicacion());
            tvDate.setText(formatFecha(item.getFechaInicio()));
            if (tvDesc != null)
                tvDesc.setText(item.getDescripcion());

            // LOG Debug Imagen
            String imageUrl = item.getImageUrl();
            android.util.Log.d("ActividadesAdapter",
                    "Cargando imagen para ID " + item.getIdActividad() + ": " + imageUrl);

            Glide.with(itemView.getContext())
                    .load(imageUrl)
                    .centerCrop()
                    .placeholder(R.drawable.activities1)
                    .error(
                            Glide.with(itemView.getContext())
                                    .load("https://placehold.co/600x400/780000/ffffff.png?text=Error")
                                    .centerCrop())
                    .into(imgLogo);

            if (tvCategory != null) {
                String tipo = item.getTipo();
                android.util.Log.d("ActividadesAdapter", "Tipo raw: " + tipo);

                if (tipo != null && !tipo.isEmpty()) {
                    switch (tipo) {
                        case "1":
                            tipo = "Social";
                            break;
                        case "2":
                            tipo = "Medioambiente";
                            break;
                        case "3":
                            tipo = "Sanitario";
                            break;
                        case "4":
                            tipo = "Cultural";
                            break;
                        case "5":
                            tipo = "Educativo";
                            break;
                        case "6":
                            tipo = "Deportivo";
                            break;
                    }
                    tvCategory.setText(tipo);
                } else {
                    tvCategory.setText("General");
                }
            }

            if (item.getUbicacion() != null && !item.getUbicacion().isEmpty()) {
                tvLocation.setText(item.getUbicacion());
                tvLocation.setVisibility(View.VISIBLE);
            } else {
                tvLocation.setText("Sin ubicación");
                tvLocation.setVisibility(View.VISIBLE);
            }

            if (listener != null) {
                itemView.setOnClickListener(v -> listener.onItemClick(item, getAdapterPosition()));
            }

            // Manejar botón de editar (solo para actividades en revisión)
            if (btnEdit != null) {
                String estado = item.getEstadoPublicacion();
                boolean isEnRevision = estado != null && estado.equalsIgnoreCase("En revision");

                if (showEditButton && editListener != null && isEnRevision) {
                    btnEdit.setVisibility(View.VISIBLE);
                    btnEdit.setOnClickListener(v -> editListener.onItemClick(item, getAdapterPosition()));
                } else {
                    btnEdit.setVisibility(View.GONE);
                }
            }
        }
    }
}
