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
        // CARGAR IMAGEN CON GLIDE
        // Priorizar el campo 'imgUrl' que devuelve la API (ruta relativa:
        // /uploads/ods/...)
        String imgUrl = ods.getImgUrl();

        // Si es nulo, probar con 'imgOds' (nombre archivo)
        if (imgUrl == null || imgUrl.isEmpty()) {
            imgUrl = ods.getImagen();
        }

        if (imgUrl != null && !imgUrl.isEmpty()) {
            // Construir URL completa si es relativa
            if (!imgUrl.startsWith("http")) {
                if (imgUrl.startsWith("/")) {
                    // Caso: /uploads/ods/archivo.jpg
                    // Quitar la barra final de BASE_URL si existe para evitar doble //
                    String baseUrl = ApiClient.BASE_URL.endsWith("/")
                            ? ApiClient.BASE_URL.substring(0, ApiClient.BASE_URL.length() - 1)
                            : ApiClient.BASE_URL;
                    imgUrl = baseUrl + imgUrl;
                } else {
                    // Caso solo nombre de archivo sin ruta
                    imgUrl = ApiClient.BASE_URL + "uploads/ods/" + imgUrl;
                }
            }

            Glide.with(holder.itemView.getContext())
                    .load(imgUrl)
                    .transform(new CenterCrop(), new RoundedCorners(12))
                    .placeholder(R.drawable.bg_circle_light_blue)
                    .error(R.drawable.ic_globe)
                    .into(holder.icon);

            // Quitar estilos de placeholder (para imagen real)
            holder.icon.setPadding(0, 0, 0, 0);
            holder.icon.setBackground(null);
            holder.icon.setColorFilter(null);
            holder.icon.setScaleType(ImageView.ScaleType.CENTER_CROP);
        } else {
            // Imagen por defecto (Estilo icono)
            holder.icon.setImageResource(R.drawable.ic_globe);
            holder.icon.setBackgroundResource(R.drawable.bg_circle_light_blue);

            // Padding para que el icono respire dentro del círculo
            int padding = (int) (12 * holder.itemView.getContext().getResources().getDisplayMetrics().density);
            holder.icon.setPadding(padding, padding, padding, padding);

            // Configuración específica para el icono
            holder.icon.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            holder.icon.setColorFilter(android.graphics.Color.parseColor("#424242"));
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