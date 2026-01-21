package org.cuatrovientos.voluntariado4v.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.cuatrovientos.voluntariado4v.Models.ActividadResponse;
import org.cuatrovientos.voluntariado4v.R;

import java.util.ArrayList;
import java.util.List;

public class CoordinatorActivitiesAdapter extends RecyclerView.Adapter<CoordinatorActivitiesAdapter.ViewHolder> {

    public static final int TYPE_PENDING = 1;
    public static final int TYPE_CATALOG = 2;

    private int currentType;
    private List<ActividadResponse> items = new ArrayList<>();
    private Context context;
    private OnActivityActionListener listener;

    public interface OnActivityActionListener {
        void onPublish(int id);
        void onReject(int id);
        void onDelete(int id);
        void onEdit(ActividadResponse actividad);
        void onClick(ActividadResponse actividad);
    }

    public CoordinatorActivitiesAdapter(Context context, int type, OnActivityActionListener listener) {
        this.context = context;
        this.currentType = type;
        this.listener = listener;
    }

    public void setActivitiesList(List<ActividadResponse> list) {
        this.items.clear();
        this.items.addAll(list);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_coordinator_activity, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ActividadResponse actividad = items.get(position);

        holder.tvTitle.setText(actividad.getTitulo());
        holder.tvOrg.setText(actividad.getNombreOrganizacion());

        holder.itemView.setOnClickListener(v -> listener.onClick(actividad));

        if (currentType == TYPE_PENDING) {
            bindPending(holder, actividad);
        } else {
            bindCatalog(holder, actividad);
        }
    }

    private void bindPending(ViewHolder holder, ActividadResponse act) {
        // CAMBIO: Ahora mostramos "PENDIENTE" en lugar de "REVISIÓN"
        holder.tvStatus.setText("PENDIENTE");
        holder.tvStatus.setBackgroundResource(R.drawable.bg_tag_orange);
        holder.viewStatus.setBackgroundColor(Color.parseColor("#FF9800")); // Naranja

        holder.btnApproveContainer.setVisibility(View.VISIBLE);
        holder.btnRejectContainer.setVisibility(View.VISIBLE);

        holder.btnApprove.setOnClickListener(v -> listener.onPublish(act.getId()));
        holder.btnReject.setOnClickListener(v -> listener.onReject(act.getId()));
        holder.btnEdit.setOnClickListener(v -> listener.onEdit(act));
    }

    private void bindCatalog(ViewHolder holder, ActividadResponse act) {
        String estado = act.getEstadoPublicacion() != null ? act.getEstadoPublicacion().toUpperCase() : "DESCONOCIDO";

        // Mapeo visual de estados para el catálogo
        switch (estado) {
            case "PUBLICADA":
                holder.tvStatus.setText("PUBLICADA");
                holder.tvStatus.setBackgroundResource(R.drawable.bg_tag_green);
                holder.viewStatus.setBackgroundColor(Color.parseColor("#43A047"));
                break;
            case "CANCELADA":
            case "RECHAZADA":
                holder.tvStatus.setText(estado); // "CANCELADA" o "RECHAZADA"
                holder.tvStatus.setBackgroundResource(R.drawable.bg_tag_red);
                holder.viewStatus.setBackgroundColor(Color.parseColor("#E53935"));
                break;
            default:
                // Por si se cuela alguna otra cosa
                holder.tvStatus.setText(estado);
                holder.tvStatus.setBackgroundResource(R.drawable.bg_tag_blue);
                holder.viewStatus.setBackgroundColor(Color.parseColor("#1E88E5"));
                break;
        }

        holder.btnApproveContainer.setVisibility(View.GONE);
        holder.btnRejectContainer.setVisibility(View.VISIBLE);

        holder.btnReject.setOnClickListener(v -> listener.onDelete(act.getId()));
        holder.btnEdit.setOnClickListener(v -> listener.onEdit(act));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvOrg, tvStatus;
        View viewStatus;
        View btnApproveContainer, btnRejectContainer;
        ImageButton btnApprove, btnReject, btnEdit;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvOrg = itemView.findViewById(R.id.tvOrganization);
            tvStatus = itemView.findViewById(R.id.tvStatusBadge);
            viewStatus = itemView.findViewById(R.id.viewStatusIndicator);

            btnApproveContainer = itemView.findViewById(R.id.btnApproveContainer);
            btnRejectContainer = itemView.findViewById(R.id.btnRejectContainer);

            btnApprove = itemView.findViewById(R.id.btnApprove);
            btnReject = itemView.findViewById(R.id.btnReject);
            btnEdit = itemView.findViewById(R.id.btnEdit);
        }
    }
}