package org.cuatrovientos.voluntariado4v.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import org.cuatrovientos.voluntariado4v.Models.InscripcionResponse;
import org.cuatrovientos.voluntariado4v.R;
import java.util.ArrayList;
import java.util.List;

public class VolunteersAdapter extends RecyclerView.Adapter<VolunteersAdapter.ViewHolder> {

    private List<InscripcionResponse> inscripciones;

    public VolunteersAdapter(List<InscripcionResponse> inscripciones) {
        this.inscripciones = inscripciones != null ? inscripciones : new ArrayList<>();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_volunteer_simple, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        InscripcionResponse insc = inscripciones.get(position);

        String nombreCompleto = insc.getNombreVoluntario();
        if (nombreCompleto == null || nombreCompleto.isEmpty()) {
            nombreCompleto = "Voluntario #" + insc.getIdVoluntario();
        }

        // Añadir estado al nombre para mostrar info completa
        String estado = insc.getEstado();
        if (estado != null && !estado.isEmpty()) {
            nombreCompleto += " (" + estado + ")";
        }

        holder.tvName.setText(nombreCompleto);
    }

    @Override
    public int getItemCount() {
        return inscripciones.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName;

        ViewHolder(View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvVolName);
        }
    }
}