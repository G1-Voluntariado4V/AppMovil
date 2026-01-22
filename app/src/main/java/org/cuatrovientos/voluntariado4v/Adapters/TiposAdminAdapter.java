package org.cuatrovientos.voluntariado4v.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import org.cuatrovientos.voluntariado4v.Models.TipoVoluntariadoResponse;
import org.cuatrovientos.voluntariado4v.R;
import java.util.List;

public class TiposAdminAdapter extends RecyclerView.Adapter<TiposAdminAdapter.ViewHolder> {

    private List<TipoVoluntariadoResponse> tiposList;
    private final OnItemClickListener listener;

    public interface OnItemClickListener {
        void onEditClick(TipoVoluntariadoResponse tipo);
        void onDeleteClick(TipoVoluntariadoResponse tipo);
    }

    public TiposAdminAdapter(List<TipoVoluntariadoResponse> tiposList, OnItemClickListener listener) {
        this.tiposList = tiposList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_tipo_admin, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        TipoVoluntariadoResponse tipo = tiposList.get(position);
        holder.name.setText(tipo.getNombre());

        holder.btnEdit.setOnClickListener(v -> listener.onEditClick(tipo));
        holder.btnDelete.setOnClickListener(v -> listener.onDeleteClick(tipo));
    }

    @Override
    public int getItemCount() {
        return tiposList != null ? tiposList.size() : 0;
    }

    public void updateList(List<TipoVoluntariadoResponse> newList) {
        this.tiposList = newList;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView name;
        ImageButton btnEdit, btnDelete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.txtTipoName);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}