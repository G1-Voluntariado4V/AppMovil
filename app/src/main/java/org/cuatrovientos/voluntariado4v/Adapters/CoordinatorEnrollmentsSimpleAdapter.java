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

import org.cuatrovientos.voluntariado4v.Models.PendingEnrollmentResponse;
import org.cuatrovientos.voluntariado4v.R;

import java.util.ArrayList;
import java.util.List;

public class CoordinatorEnrollmentsSimpleAdapter
        extends RecyclerView.Adapter<CoordinatorEnrollmentsSimpleAdapter.ViewHolder> {

    private List<PendingEnrollmentResponse> items = new ArrayList<>();
    private Context context;
    private OnEnrollmentActionListener listener;

    public interface OnEnrollmentActionListener {
        void onApprove(PendingEnrollmentResponse item);

        void onReject(PendingEnrollmentResponse item);

        void onViewUser(PendingEnrollmentResponse item);
    }

    public CoordinatorEnrollmentsSimpleAdapter(Context context, OnEnrollmentActionListener listener) {
        this.context = context;
        this.listener = listener;
    }

    public void setList(List<PendingEnrollmentResponse> list) {
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
        PendingEnrollmentResponse item = items.get(position);

        holder.tvName.setText(item.getNombreVoluntario() + " " + item.getApellidosVoluntario());
        holder.tvEmail.setText("Actividad: " + item.getTituloActividad());

        holder.tvRole.setText("INSCRIPCIÓN");
        holder.tvRole.setBackgroundResource(R.drawable.bg_tag_purple);
        holder.tvRole.setTextColor(Color.WHITE);
        holder.viewStatus.setBackgroundColor(Color.parseColor("#7B1FA2"));

        holder.btnApproveContainer.setVisibility(View.VISIBLE);
        holder.btnRejectContainer.setVisibility(View.VISIBLE);
        holder.btnEdit.setVisibility(View.GONE);

        holder.btnApprove.setOnClickListener(v -> listener.onApprove(item));
        holder.btnReject.setOnClickListener(v -> listener.onReject(item));

        holder.itemView.setOnClickListener(v -> listener.onViewUser(item));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvEmail, tvRole;
        View viewStatus;
        View btnApproveContainer, btnRejectContainer;
        ImageButton btnApprove, btnReject, btnEdit;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvUserName);
            tvEmail = itemView.findViewById(R.id.tvUserEmail);
            tvRole = itemView.findViewById(R.id.tvUserRole);
            viewStatus = itemView.findViewById(R.id.viewStatusIndicator);

            btnApproveContainer = itemView.findViewById(R.id.btnApproveContainer);
            btnRejectContainer = itemView.findViewById(R.id.btnRejectContainer);

            btnApprove = itemView.findViewById(R.id.btnApprove);
            btnReject = itemView.findViewById(R.id.btnReject);
            btnEdit = itemView.findViewById(R.id.btnEdit);
        }
    }
}
