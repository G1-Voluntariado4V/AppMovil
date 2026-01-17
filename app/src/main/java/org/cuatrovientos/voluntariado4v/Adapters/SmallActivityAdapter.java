package org.cuatrovientos.voluntariado4v.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import org.cuatrovientos.voluntariado4v.Models.ActividadResponse;
import org.cuatrovientos.voluntariado4v.R;
import java.util.List;

public class SmallActivityAdapter extends RecyclerView.Adapter<SmallActivityAdapter.ViewHolder> {

    private List<ActividadResponse> activities;
    private final OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(ActividadResponse item);
    }

    public SmallActivityAdapter(List<ActividadResponse> activities, OnItemClickListener listener) {
        this.activities = activities;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_small_card_activity, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ActividadResponse act = activities.get(position);

        holder.tvTitle.setText(act.getTitulo());

        String ubicacion = act.getUbicacion();
        if (ubicacion == null || ubicacion.trim().isEmpty() || ubicacion.equalsIgnoreCase("No definida")) {
            holder.tvLocation.setText("No definido");
        } else {
            holder.tvLocation.setText(ubicacion);
        }

        String fecha = act.getFechaInicio();
        if (fecha != null && fecha.length() > 10)
            fecha = fecha.substring(0, 10);
        holder.tvDate.setText(fecha);

        String estado = act.getEstadoPublicacion();
        if ("Publicada".equalsIgnoreCase(estado)) {
            holder.tvStatus.setText("ACTIVA");
        } else {
            holder.tvStatus.setText(estado != null ? estado.toUpperCase() : "ACTIVA");
        }

        Glide.with(holder.itemView.getContext())
                .load(act.getImagenActividad())
                .placeholder(R.drawable.activities1)
                .error(R.drawable.activities1)
                .centerCrop()
                .into(holder.imgActivity);

        holder.itemView.setOnClickListener(v -> listener.onItemClick(act));
    }

    @Override
    public int getItemCount() {
        return activities != null ? activities.size() : 0;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgActivity;
        TextView tvTitle, tvLocation, tvDate, tvStatus;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgActivity = itemView.findViewById(R.id.imgActivity);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvLocation = itemView.findViewById(R.id.tvLocation);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvStatus = itemView.findViewById(R.id.tvStatus);
        }
    }
}
