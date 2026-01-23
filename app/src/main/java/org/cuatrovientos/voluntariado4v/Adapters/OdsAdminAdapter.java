package org.cuatrovientos.voluntariado4v.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import org.cuatrovientos.voluntariado4v.API.ApiClient;
import org.cuatrovientos.voluntariado4v.Models.OdsResponse;
import org.cuatrovientos.voluntariado4v.R;
import java.util.List;

public class OdsAdminAdapter extends RecyclerView.Adapter<OdsAdminAdapter.ViewHolder> {

    private List<OdsResponse> odsList;
    private final OnItemClickListener listener;

    public interface OnItemClickListener {
        void onEditClick(OdsResponse ods);

        void onDeleteClick(OdsResponse ods);
    }

    public OdsAdminAdapter(List<OdsResponse> odsList, OnItemClickListener listener) {
        this.odsList = odsList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_ods_admin, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        OdsResponse ods = odsList.get(position);

        holder.name.setText(ods.getNombre());
        holder.description.setText(ods.getDescripcion());

        // CARGAR IMAGEN CON GLIDE
        String imgUrl = ods.getImagen();
        if (imgUrl != null && !imgUrl.isEmpty()) {
            // Si es relativa, se antepone el host
            if (!imgUrl.startsWith("http") && !imgUrl.startsWith("content")) {
                imgUrl = ApiClient.BASE_URL + "uploads/ods/" + imgUrl;
            }

            Glide.with(holder.itemView.getContext())
                    .load(imgUrl)
                    .transform(new CenterCrop(), new RoundedCorners(12))
                    .placeholder(R.drawable.bg_circle_light_blue)
                    .error(R.drawable.ic_globe)
                    .into(holder.icon);

            // Se quita el padding para imagen real
            holder.icon.setPadding(0, 0, 0, 0);
            holder.icon.setBackground(null);
        } else {
            // Imagen por defecto
            holder.icon.setImageResource(R.drawable.ic_globe);
            holder.icon.setBackgroundResource(R.drawable.bg_circle_light_blue);
            int padding = (int) (10 * holder.itemView.getContext().getResources().getDisplayMetrics().density);
            holder.icon.setPadding(padding, padding, padding, padding);
        }

        holder.btnEdit.setOnClickListener(v -> listener.onEditClick(ods));
        holder.btnDelete.setOnClickListener(v -> listener.onDeleteClick(ods));
    }

    @Override
    public int getItemCount() {
        return odsList != null ? odsList.size() : 0;
    }

    public void updateList(List<OdsResponse> newList) {
        this.odsList = newList;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView name, description;
        ImageButton btnEdit, btnDelete;
        ImageView icon; // Referencia a la imagen

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.txtOdsName);
            description = itemView.findViewById(R.id.txtOdsDesc);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
            icon = itemView.findViewById(R.id.imgOdsIcon); // Asegúrate de que este ID existe en item_ods_admin.xml
        }
    }
}