package org.cuatrovientos.voluntariado4v.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.cuatrovientos.voluntariado4v.Models.UserResponse;
import org.cuatrovientos.voluntariado4v.R;

import java.util.ArrayList;
import java.util.List;

public class CoordinatorUsersAdapter extends RecyclerView.Adapter<CoordinatorUsersAdapter.ViewHolder> {

    public static final int TYPE_PENDING = 1;
    public static final int TYPE_ALL_USERS = 2;

    private int currentType;
    private List<UserResponse> items = new ArrayList<>(); // Lista unificada de UserResponse
    private Context context;
    private OnUserActionListener listener;

    public interface OnUserActionListener {
        void onApprove(int id);
        void onReject(int id);
        void onEditRole(UserResponse user);
    }

    public CoordinatorUsersAdapter(Context context, int type, OnUserActionListener listener) {
        this.context = context;
        this.currentType = type;
        this.listener = listener;
    }

    // Método unificado para actualizar la lista
    public void setUsersList(List<UserResponse> list) {
        this.items.clear();
        this.items.addAll(list);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_coordinator_user, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        UserResponse user = items.get(position);

        if (currentType == TYPE_PENDING) {
            bindPending(holder, user);
        } else {
            bindUser(holder, user);
        }
    }

    private void bindPending(ViewHolder holder, UserResponse user) {
        // En pendientes, mostramos el correo si no hay nombre completo disponible
        holder.tvName.setText(user.getCorreo());
        holder.tvEmail.setText("Rol solicitado: " + user.getRol());

        // Estilo visual Naranja
        holder.tvRole.setText("SOLICITUD");
        holder.tvRole.setBackgroundResource(R.drawable.bg_tag_orange);
        holder.viewStatus.setBackgroundColor(Color.parseColor("#FF9800"));

        holder.btnApproveContainer.setVisibility(View.VISIBLE);
        holder.btnRejectContainer.setVisibility(View.VISIBLE);
        holder.btnEdit.setVisibility(View.GONE);

        holder.btnApprove.setOnClickListener(v -> listener.onApprove(user.getId()));
        holder.btnReject.setOnClickListener(v -> listener.onReject(user.getId()));
    }

    private void bindUser(ViewHolder holder, UserResponse user) {
        holder.tvName.setText(user.getCorreo());
        holder.tvEmail.setText(user.getEstadoCuenta());

        String role = user.getRol().toLowerCase();

        // Configuración de colores según rol
        if (role.startsWith("coor") || role.startsWith("admin")) {
            holder.tvRole.setText("COORDINADOR");
            holder.tvRole.setBackgroundResource(R.drawable.bg_tag_red);
            holder.viewStatus.setBackgroundColor(Color.parseColor("#E53935"));
        } else if (role.startsWith("org")) {
            holder.tvRole.setText("ORGANIZACIÓN");
            holder.tvRole.setBackgroundResource(R.drawable.bg_tag_green);
            holder.viewStatus.setBackgroundColor(Color.parseColor("#43A047"));
        } else {
            holder.tvRole.setText("VOLUNTARIO");
            holder.tvRole.setBackgroundResource(R.drawable.bg_tag_blue);
            holder.viewStatus.setBackgroundColor(Color.parseColor("#1E88E5"));
        }

        holder.btnApproveContainer.setVisibility(View.GONE);
        holder.btnRejectContainer.setVisibility(View.GONE);
        holder.btnEdit.setVisibility(View.VISIBLE);

        holder.btnEdit.setOnClickListener(v -> listener.onEditRole(user));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvEmail, tvRole;
        View viewStatus;
        ImageView ivUserIcon;
        View btnApproveContainer, btnRejectContainer;
        ImageButton btnApprove, btnReject, btnEdit;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvUserName);
            tvEmail = itemView.findViewById(R.id.tvUserEmail);
            tvRole = itemView.findViewById(R.id.tvUserRole);
            viewStatus = itemView.findViewById(R.id.viewStatusIndicator);
            ivUserIcon = itemView.findViewById(R.id.ivUserIcon);

            btnApproveContainer = itemView.findViewById(R.id.btnApproveContainer);
            btnRejectContainer = itemView.findViewById(R.id.btnRejectContainer);

            btnApprove = itemView.findViewById(R.id.btnApprove);
            btnReject = itemView.findViewById(R.id.btnReject);
            btnEdit = itemView.findViewById(R.id.btnEdit);
        }
    }
}