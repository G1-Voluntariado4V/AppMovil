package org.cuatrovientos.voluntariado4v;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ActivitiesAdapter extends RecyclerView.Adapter<ActivitiesAdapter.RecyclerDataHolder> {

    private ArrayList<ActivityModel> listData;
    private OnItemClickListener itemListener;

    // Interfaz para gestionar el click
    public interface OnItemClickListener {
        void onItemClick(ActivityModel item, int position);
    }

    // Constructor que recibe la lista y el listener
    public ActivitiesAdapter(ArrayList<ActivityModel> listData, OnItemClickListener listener) {
        this.listData = listData;
        this.itemListener = listener;
    }

    @NonNull
    @Override
    public RecyclerDataHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflamos el layout 'item_big_card_activity'
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_big_card_activity, parent, false);
        return new RecyclerDataHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerDataHolder holder, int position) {
        // Llamamos a assignData pasando el objeto y el listener
        holder.assignData(listData.get(position), itemListener);
    }

    @Override
    public int getItemCount() {
        return listData.size();
    }

    // Clase Holder interna (pág 12)
    public class RecyclerDataHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvOrg, tvLocation, tvDate, tvDesc;
        ImageView imgLogo;

        public RecyclerDataHolder(@NonNull View itemView) {
            super(itemView);
            // Localizamos los elementos visuales del XML
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvOrg = itemView.findViewById(R.id.tvOrgName);
            tvLocation = itemView.findViewById(R.id.tvLocation);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvDesc = itemView.findViewById(R.id.tvDesc);
            imgLogo = itemView.findViewById(R.id.imgLogo);
        }

        // Metodo assignData modificado para recibir el Listener
        public void assignData(final ActivityModel item, final OnItemClickListener onItemClickListener) {
            tvTitle.setText(item.getTitle());
            tvOrg.setText(item.getOrganization());
            tvLocation.setText(item.getLocation());
            tvDate.setText(item.getDate());
            tvDesc.setText(item.getDescription());

            // Verificamos si la imagen es válida (diferente de 0) para evitar errores
            if (item.getImageResource() != 0) {
                imgLogo.setImageResource(item.getImageResource());
            }

            // Configuramos el click en el item
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onItemClickListener.onItemClick(item, getAdapterPosition());
                }
            });
        }
    }
}