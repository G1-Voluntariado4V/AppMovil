package org.cuatrovientos.voluntariado4v.Dialogs;

import android.app.Dialog;
import android.content.Context;
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

import org.cuatrovientos.voluntariado4v.App.MockDataProvider;
import org.cuatrovientos.voluntariado4v.R;

import java.util.ArrayList;
import java.util.List;

public class LanguageDialog extends DialogFragment {

    private ArrayList<String> misIdiomas; // Copia temporal para editar
    private LanguageAdapter adapter;
    private OnLanguagesSavedListener listener;

    // Interfaz para comunicar cambios a la Activity
    public interface OnLanguagesSavedListener {
        void onLanguagesSaved();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof OnLanguagesSavedListener) {
            listener = (OnLanguagesSavedListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement OnLanguagesSavedListener");
        }
    }

    public LanguageDialog() {
        // Constructor vacío requerido
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_edit_lenguages, container, false);

        ImageView btnClose = view.findViewById(R.id.btnClose);
        AutoCompleteTextView dropdown = view.findViewById(R.id.etInput);
        MaterialButton btnAdd = view.findViewById(R.id.btnAdd);
        RecyclerView recyclerView = view.findViewById(R.id.rvEditList);
        MaterialButton btnSave = view.findViewById(R.id.btnSave);

        // Opciones disponibles
        String[] idiomasDisponibles = new String[]{
                "Español", "Inglés", "Francés", "Alemán",
                "Italiano", "Portugués", "Euskera", "Catalán", "Gallego"
        };
        ArrayAdapter<String> dropdownAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, idiomasDisponibles);
        dropdown.setAdapter(dropdownAdapter);

        // Cargar datos actuales del usuario (creamos una nueva lista para no modificar la original hasta guardar)
        misIdiomas = new ArrayList<>(MockDataProvider.getLoggedUser().idiomas);

        adapter = new LanguageAdapter(misIdiomas);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        btnAdd.setOnClickListener(v -> {
            String seleccion = dropdown.getText().toString();
            if (!seleccion.isEmpty()) {
                if (!misIdiomas.contains(seleccion)) {
                    misIdiomas.add(seleccion);
                    adapter.notifyItemInserted(misIdiomas.size() - 1);
                    dropdown.setText("");
                } else {
                    Toast.makeText(getContext(), "Ya tienes este idioma añadido", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnSave.setOnClickListener(v -> {
            // Guardar cambios en el MockData
            MockDataProvider.getLoggedUser().idiomas = new ArrayList<>(misIdiomas);

            // Notificar a la actividad principal para que refresque la pantalla
            if (listener != null) {
                listener.onLanguagesSaved();
            }
            Toast.makeText(getContext(), "Idiomas guardados", Toast.LENGTH_SHORT).show();
            dismiss();
        });

        btnClose.setOnClickListener(v -> dismiss());

        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        }

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            int width = (int) (getResources().getDisplayMetrics().widthPixels * 0.90);
            dialog.getWindow().setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
    }

    private class LanguageAdapter extends RecyclerView.Adapter<LanguageAdapter.ViewHolder> {
        private List<String> datos;

        public LanguageAdapter(List<String> datos) {
            this.datos = datos;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_language, parent, false);
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            String item = datos.get(position);
            holder.tvName.setText(item);
            holder.btnDelete.setVisibility(View.VISIBLE); // En este diálogo permitimos borrar

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
                btnDelete = itemView.findViewById(R.id.btnDelete); // Asegúrate que existe en item_language.xml
                if(btnDelete == null) btnDelete = itemView; // Fallback
            }
        }
    }
}