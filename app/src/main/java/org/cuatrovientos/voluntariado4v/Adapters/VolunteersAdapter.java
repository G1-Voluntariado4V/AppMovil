package org.cuatrovientos.voluntariado4v.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import org.cuatrovientos.voluntariado4v.Models.VoluntarioResponse;
import org.cuatrovientos.voluntariado4v.R;
import java.util.ArrayList;
import java.util.List;

public class VolunteersAdapter extends RecyclerView.Adapter<VolunteersAdapter.ViewHolder> {

    private List<VoluntarioResponse> voluntarios;

    public VolunteersAdapter(List<VoluntarioResponse> voluntarios) {
        this.voluntarios = voluntarios != null ? voluntarios : new ArrayList<>();
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
        VoluntarioResponse vol = voluntarios.get(position);
        // Asumiendo que VoluntarioResponse tiene getNombre() o similar.
        // Si no, ajusta al metodo correcto (ej. getEmail() si nombre es null).
        String nombreCompleto = "Voluntario #" + vol.getId();
        if(vol.getNombre() != null) {
            nombreCompleto = vol.getNombre() + " " + (vol.getApellidos() != null ? vol.getApellidos() : "");
        }

        holder.tvName.setText(nombreCompleto);
    }

    @Override
    public int getItemCount() {
        return voluntarios.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName;

        ViewHolder(View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvVolName);
        }
    }
}