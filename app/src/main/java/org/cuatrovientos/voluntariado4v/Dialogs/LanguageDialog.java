package org.cuatrovientos.voluntariado4v.Dialogs;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;

import org.cuatrovientos.voluntariado4v.API.ApiClient;
import org.cuatrovientos.voluntariado4v.Models.IdiomaRequest;
import org.cuatrovientos.voluntariado4v.Models.IdiomaResponse;
import org.cuatrovientos.voluntariado4v.Models.MensajeResponse;
import org.cuatrovientos.voluntariado4v.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LanguageDialog extends DialogFragment {

    private static final String TAG = "LanguageDialog";

    private ArrayList<IdiomaItem> misIdiomas;
    private LanguageAdapter adapter;
    private OnLanguagesSavedListener listener;
    private int userId = -1;

    // Mapeo nombre -> id para los idiomas disponibles
    private Map<String, Integer> idiomasMap = new HashMap<>();
    private List<IdiomaResponse> idiomasDisponibles = new ArrayList<>();

    // Niveles disponibles
    private final String[] niveles = { "A1", "A2", "B1", "B2", "C1", "C2", "Nativo" };

    // Clase interna para mantener idioma con su id
    public static class IdiomaItem {
        public int idIdioma;
        public String nombre;
        public String nivel;

        public IdiomaItem(int idIdioma, String nombre, String nivel) {
            this.idIdioma = idIdioma;
            this.nombre = nombre;
            this.nivel = nivel;
        }

        @Override
        public String toString() {
            return nombre + " (" + nivel + ")";
        }
    }

    public interface OnLanguagesSavedListener {
        void onLanguagesSaved();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (getParentFragment() instanceof OnLanguagesSavedListener) {
            listener = (OnLanguagesSavedListener) getParentFragment();
        } else if (context instanceof OnLanguagesSavedListener) {
            listener = (OnLanguagesSavedListener) context;
        } else {
            listener = null;
        }

        // Obtener userId de SharedPreferences
        SharedPreferences prefs = context.getSharedPreferences("VoluntariadoPrefs", Context.MODE_PRIVATE);
        userId = prefs.getInt("user_id", -1);
    }

    public LanguageDialog() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_edit_lenguages, container, false);

        ImageView btnClose = view.findViewById(R.id.btnClose);
        AutoCompleteTextView dropdown = view.findViewById(R.id.etInput);
        AutoCompleteTextView dropdownNivel = view.findViewById(R.id.etLevelInput);
        MaterialButton btnAdd = view.findViewById(R.id.btnAdd);
        RecyclerView recyclerView = view.findViewById(R.id.rvEditList);
        MaterialButton btnSave = view.findViewById(R.id.btnSave);

        // Configurar niveles
        ArrayAdapter<String> nivelAdapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_dropdown_item_1line,
                niveles);
        dropdownNivel.setAdapter(nivelAdapter);
        dropdownNivel.setText(niveles[2], false); // B1 por defecto

        // Forzar despliegue al clicar
        dropdownNivel.setOnClickListener(v -> dropdownNivel.showDropDown());
        dropdownNivel.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                dropdownNivel.showDropDown();
            }
        });

        // Inicializar lista
        misIdiomas = new ArrayList<>();
        if (idiomasTemp != null) {
            misIdiomas.addAll(idiomasTemp);
        }
        adapter = new LanguageAdapter(misIdiomas);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        // Cargar idiomas disponibles de la API
        loadIdiomasDisponibles(dropdown);

        // Listener para añadir idioma
        btnAdd.setOnClickListener(v -> {
            String seleccion = dropdown.getText().toString().trim();
            String nivelSeleccionado = dropdownNivel.getText().toString().trim();

            Log.d(TAG, "Intento añadir: " + seleccion + " | Nivel: " + nivelSeleccionado);

            if (seleccion.isEmpty()) {
                Toast.makeText(getContext(), "Selecciona un idioma", Toast.LENGTH_SHORT).show();
                return;
            }

            if (nivelSeleccionado.isEmpty()) {
                nivelSeleccionado = "B1";
            }

            // Verificar que existe en el mapa
            Integer idIdioma = idiomasMap.get(seleccion);
            if (idIdioma == null) {
                Toast.makeText(getContext(), "Idioma no válido", Toast.LENGTH_SHORT).show();
                return;
            }

            // Verificar que no está ya añadido
            for (IdiomaItem item : misIdiomas) {
                if (item.idIdioma == idIdioma) {
                    Toast.makeText(getContext(), "Ya tienes este idioma añadido", Toast.LENGTH_SHORT).show();
                    return;
                }
            }

            // Añadir via API con nivel seleccionado
            addIdiomaToApi(idIdioma, seleccion, nivelSeleccionado, dropdown, dropdownNivel);
        });

        // El botón guardar simplemente cierra el diálogo
        btnSave.setOnClickListener(v -> {
            if (listener != null) {
                listener.onLanguagesSaved();
            }
            dismiss();
        });

        btnClose.setOnClickListener(v -> dismiss());

        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        }

        return view;
    }

    private void loadIdiomasDisponibles(AutoCompleteTextView dropdown) {
        ApiClient.getService().getIdiomas().enqueue(new Callback<List<IdiomaResponse>>() {
            @Override
            public void onResponse(Call<List<IdiomaResponse>> call, Response<List<IdiomaResponse>> response) {
                if (!isAdded())
                    return;

                if (response.isSuccessful() && response.body() != null) {
                    idiomasDisponibles = response.body();
                    List<String> nombres = new ArrayList<>();
                    for (IdiomaResponse idioma : idiomasDisponibles) {
                        nombres.add(idioma.getNombre());
                        idiomasMap.put(idioma.getNombre(), idioma.getId());
                    }

                    ArrayAdapter<String> dropdownAdapter = new ArrayAdapter<>(
                            requireContext(),
                            android.R.layout.simple_dropdown_item_1line,
                            nombres);
                    dropdown.setAdapter(dropdownAdapter);
                    Log.d(TAG, "Cargados " + nombres.size() + " idiomas disponibles");
                }
            }

            @Override
            public void onFailure(Call<List<IdiomaResponse>> call, Throwable t) {
                if (!isAdded())
                    return;
                Log.e(TAG, "Error cargando idiomas", t);
            }
        });
    }

    private void addIdiomaToApi(int idIdioma, String nombreIdioma, String nivel, AutoCompleteTextView dropdown,
            AutoCompleteTextView dropdownNivel) {
        if (userId == -1) {
            Toast.makeText(getContext(), "Error: Sesión no válida", Toast.LENGTH_SHORT).show();
            return;
        }

        IdiomaRequest request = new IdiomaRequest(idIdioma, nivel);
        Log.d(TAG, "Enviando idioma: id=" + idIdioma + " nivel=" + nivel);

        ApiClient.getService().addIdioma(userId, request).enqueue(new Callback<MensajeResponse>() {
            @Override
            public void onResponse(Call<MensajeResponse> call, Response<MensajeResponse> response) {
                if (!isAdded())
                    return;

                if (response.isSuccessful()) {
                    // Añadir a la lista local
                    misIdiomas.add(new IdiomaItem(idIdioma, nombreIdioma, nivel));
                    adapter.notifyItemInserted(misIdiomas.size() - 1);

                    // Resetear campos
                    dropdown.setText("");
                    dropdownNivel.setText("B1", false); // Resetear a B1 sin filtro

                    Toast.makeText(getContext(), "Idioma añadido", Toast.LENGTH_SHORT).show();
                } else if (response.code() == 409) {
                    Toast.makeText(getContext(), "Ya tienes este idioma", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "Error al añadir idioma", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Error añadiendo idioma: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<MensajeResponse> call, Throwable t) {
                if (!isAdded())
                    return;
                Toast.makeText(getContext(), "Error de conexión", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Error de red añadiendo idioma", t);
            }
        });
    }

    private void deleteIdiomaFromApi(int position) {
        if (userId == -1 || position < 0 || position >= misIdiomas.size())
            return;

        IdiomaItem item = misIdiomas.get(position);

        ApiClient.getService().deleteIdioma(userId, item.idIdioma).enqueue(new Callback<MensajeResponse>() {
            @Override
            public void onResponse(Call<MensajeResponse> call, Response<MensajeResponse> response) {
                if (!isAdded())
                    return;

                if (response.isSuccessful()) {
                    misIdiomas.remove(position);
                    adapter.notifyItemRemoved(position);
                    adapter.notifyItemRangeChanged(position, misIdiomas.size());
                    Toast.makeText(getContext(), "Idioma eliminado", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "Error al eliminar idioma", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Error eliminando idioma: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<MensajeResponse> call, Throwable t) {
                if (!isAdded())
                    return;
                Toast.makeText(getContext(), "Error de conexión", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Error de red eliminando idioma", t);
            }
        });
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

    // Método para recibir idiomas actuales desde el Fragment padre
    private List<IdiomaItem> idiomasTemp = new ArrayList<>();

    public void setIdiomasActuales(List<IdiomaItem> idiomas) {
        // Guardamos en temporal por si la vista no está lista
        if (idiomas != null) {
            this.idiomasTemp = new ArrayList<>(idiomas);
            // Si ya está activo, actualizamos
            if (misIdiomas != null) {
                misIdiomas.clear();
                misIdiomas.addAll(idiomas);
                if (adapter != null) {
                    adapter.notifyDataSetChanged();
                }
            }
        }
    }

    private class LanguageAdapter extends RecyclerView.Adapter<LanguageAdapter.ViewHolder> {
        private List<IdiomaItem> datos;

        public LanguageAdapter(List<IdiomaItem> datos) {
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
            IdiomaItem item = datos.get(position);
            holder.tvName.setText(item.toString());

            if (holder.btnDelete != null) {
                holder.btnDelete.setVisibility(View.VISIBLE);
                holder.btnDelete.setOnClickListener(v -> {
                    int adapterPosition = holder.getAdapterPosition();
                    if (adapterPosition != RecyclerView.NO_POSITION) {
                        deleteIdiomaFromApi(adapterPosition);
                    }
                });
            }
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
                btnDelete = itemView.findViewById(R.id.btnDelete);
                if (btnDelete == null)
                    btnDelete = itemView;
            }
        }
    }
}