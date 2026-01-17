package org.cuatrovientos.voluntariado4v.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.cuatrovientos.voluntariado4v.Models.TopOrganizacionResponse;
import org.cuatrovientos.voluntariado4v.R;

import java.util.List;

public class DashboardOrganizationsAdapter extends RecyclerView.Adapter<DashboardOrganizationsAdapter.ViewHolder> {

    private List<TopOrganizacionResponse> organizations;

    public DashboardOrganizationsAdapter(List<TopOrganizacionResponse> organizations) {
        this.organizations = organizations;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_organization_dashboard, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        TopOrganizacionResponse org = organizations.get(position);

        holder.tvName.setText(org.getNombre());
        // Usamos descripción o acortamos
        String desc = org.getDescripcion();
        if (desc != null && desc.length() > 30)
            desc = desc.substring(0, 30) + "...";
        holder.tvType.setText(desc != null ? desc : "Organización");

        holder.imgLogo.setImageResource(R.drawable.amavir); // Placeholder por ahora

        holder.tvVolunteers.setText(String.valueOf(org.getTotalVoluntarios()));

        holder.itemView.setOnClickListener(v -> {
            android.content.Intent intent = new android.content.Intent(v.getContext(),
                    org.cuatrovientos.voluntariado4v.Activities.DetailOrganization.class);
            intent.putExtra("ORG_ID", org.getIdOrganizacion());
            intent.putExtra("ORG_NAME", org.getNombre());
            // No pasamos ORG_IMG porque el DTO top no la tiene, DetailOrganization usará
            // fallback
            v.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return organizations != null ? organizations.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvType, tvVolunteers;
        ImageView imgLogo;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvOrgName);
            tvType = itemView.findViewById(R.id.tvOrgType);
            tvVolunteers = itemView.findViewById(R.id.tvVolunteersCount);
            imgLogo = itemView.findViewById(R.id.imgOrgLogo);
        }
    }
}