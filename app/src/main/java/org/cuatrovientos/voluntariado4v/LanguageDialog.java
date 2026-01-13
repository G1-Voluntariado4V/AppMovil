package org.cuatrovientos.voluntariado4v;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;

public class LanguageDialog extends DialogFragment {

    private ArrayList<String> misIdiomas; // Lista del usuario
    private LanguageAdapter adapter;

    public LanguageDialog() {
        // Constructor vacío requerido
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflamos el XML que acabamos de crear
        View view = inflater.inflate(R.layout.dialog_edit_lenguages, container, false);

        // 1. Localizar componentes
        ImageView btnClose = view.findViewById(R.id.btnClose);
        AutoCompleteTextView dropdown = view.findViewById(R.id.etInput);
        MaterialButton btnAdd = view.findViewById(R.id.btnAdd);
        RecyclerView recyclerView = view.findViewById(R.id.rvEditList);
        MaterialButton btnSave = view.findViewById(R.id.btnSave);

        // 2. Configurar el Desplegable (Opciones disponibles)
        String[] idiomasDisponibles = new String[]{
                "Español", "Inglés", "Francés", "Alemán",
                "Italiano", "Portugués", "Euskera", "Catalán", "Gallego"
        };
        ArrayAdapter<String> dropdownAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, idiomasDisponibles);
        dropdown.setAdapter(dropdownAdapter);

        // 3. Inicializar datos del usuario (Simulado)
        misIdiomas = new ArrayList<>();
        misIdiomas.add("Español");
        misIdiomas.add("Inglés");

        // 4. Configurar RecyclerView
        adapter = new LanguageAdapter(misIdiomas);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        // 5. Botón Añadir (+)
        btnAdd.setOnClickListener(v -> {
            String seleccion = dropdown.getText().toString();
            if (!seleccion.isEmpty()) {
                if (!misIdiomas.contains(seleccion)) {
                    misIdiomas.add(seleccion);
                    adapter.notifyItemInserted(misIdiomas.size() - 1);
                    dropdown.setText(""); // Limpiar input
                } else {
                    Toast.makeText(getContext(), "Ya tienes este idioma añadido", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // 6. Botón Guardar
        btnSave.setOnClickListener(v -> {
            // TODO: Enviar 'misIdiomas' a la API
            Toast.makeText(getContext(), "Idiomas guardados", Toast.LENGTH_SHORT).show();
            dismiss();
        });

        // 7. Botón Cerrar
        btnClose.setOnClickListener(v -> dismiss());

        // Hacer fondo transparente para las esquinas redondeadas
        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        }

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        // Ajustar ancho al 90% de la pantalla
        Dialog dialog = getDialog();
        if (dialog != null) {
            int width = (int) (getResources().getDisplayMetrics().widthPixels * 0.90);
            dialog.getWindow().setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
    }

    // --- ADAPTER INTERNO ---
    private class LanguageAdapter extends RecyclerView.Adapter<LanguageAdapter.ViewHolder> {
        private List<String> datos;

        public LanguageAdapter(List<String> datos) {
            this.datos = datos;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            // Usamos el diseño de fila con papelera (item_edit_preference_row o item_preference si tiene papelera)
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_language, parent, false);
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            String item = datos.get(position);
            holder.tvName.setText(item);

            holder.btnDelete.setOnClickListener(v -> {
                datos.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, datos.size());
            });
        }

        @Override
        public int getItemCount() {
            return datos.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            TextView tvName;
            View btnDelete;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                tvName = itemView.findViewById(R.id.tvLanguageName);
                btnDelete = itemView.findViewById(R.id.btnDeleteRow);
            }
        }
    }
}