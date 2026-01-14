package org.cuatrovientos.voluntariado4v.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.cuatrovientos.voluntariado4v.Models.OrganizationModel;
import org.cuatrovientos.voluntariado4v.R;

import java.util.List;

public class DashboardOrganizationsAdapter extends RecyclerView.Adapter<DashboardOrganizationsAdapter.ViewHolder> {

    private List<OrganizationModel> organizations;

    public DashboardOrganizationsAdapter(List<OrganizationModel> organizations) {
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
        OrganizationModel org = organizations.get(position);

        holder.tvName.setText(org.getName());
        holder.tvType.setText(org.getType());
        holder.imgLogo.setImageResource(org.getLogoResId());

        // Formatear contador (Ej: si son muchos, podrías poner "2k", por ahora ponemos el número directo)
        holder.tvVolunteers.setText(String.valueOf(org.getVolunteersCount()));
    }

    @Override
    public int getItemCount() {
        return organizations.size();
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