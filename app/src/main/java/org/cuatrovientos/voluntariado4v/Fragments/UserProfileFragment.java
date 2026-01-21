package org.cuatrovientos.voluntariado4v.Fragments;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.material.materialswitch.MaterialSwitch;

import org.cuatrovientos.voluntariado4v.Activities.AuthCompleteProfile;
import org.cuatrovientos.voluntariado4v.Activities.AuthLogin;
import org.cuatrovientos.voluntariado4v.Dialogs.LanguageDialog;
import org.cuatrovientos.voluntariado4v.Models.VoluntarioResponse;
import org.cuatrovientos.voluntariado4v.Models.VoluntarioUpdateRequest;
import org.cuatrovientos.voluntariado4v.R;
import org.cuatrovientos.voluntariado4v.API.ApiClient;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserProfileFragment extends Fragment implements LanguageDialog.OnLanguagesSavedListener {

    private static final String TAG = "UserProfileFragment";

    // Vistas de Header
    private TextView tvUserName, tvUserRole;
    private ImageView btnLogout;

    // Campos de Información Personal
    private EditText etNombre, etApellidos, etDni, etTelefono, etFechaNacimiento;
    private ImageView btnEditInfo;
    private boolean isEditingInfo = false;
    private boolean isLoading = false;

    // Campos de Observaciones (Solo visual, no API)
    private EditText etObservaciones;
    private ImageView btnEditObs;
    private boolean isEditingObs = false;

    // Info Adicional
    private MaterialSwitch switchCoche;

    // Idiomas
    private RecyclerView rvLanguages;
    private ImageView btnEditLanguage;
    private SimpleLanguageAdapter languageAdapter;
    private List<VoluntarioResponse.IdiomaInfo> listaIdiomas = new ArrayList<>();
    private List<String> listaNombresIdiomas = new ArrayList<>();

    private GoogleSignInClient googleSignInClient;

    public UserProfileFragment() {
        // Constructor vacío requerido
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        // Inflar el layout del fragmento
        return inflater.inflate(R.layout.fragment_user_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupGoogleSignIn();
        initViews(view);
        setupDatePicker();

        // Cargar datos reales de la API
        loadUserDataFromApi();

        setupEditLogic();
    }

    private void setupGoogleSignIn() {
        if (getContext() == null)
            return;

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(requireContext(), gso);
    }

    private void initViews(View view) {
        tvUserName = view.findViewById(R.id.tvUserName);
        tvUserRole = view.findViewById(R.id.tvUserRole);

        etNombre = view.findViewById(R.id.etNombre);
        etApellidos = view.findViewById(R.id.etApellidos);
        etDni = view.findViewById(R.id.etDni);
        etTelefono = view.findViewById(R.id.etTelefono);
        etFechaNacimiento = view.findViewById(R.id.etFechaNacimiento);
        btnEditInfo = view.findViewById(R.id.btnEditInfo);

        etObservaciones = view.findViewById(R.id.etObservaciones);
        btnEditObs = view.findViewById(R.id.btnEditObs);

        switchCoche = view.findViewById(R.id.switchCoche);
        // UX improvement: Click en el contenedor activa el switch
        View parent = (View) switchCoche.getParent();
        if (parent != null) {
            parent.setOnClickListener(v -> {
                if (switchCoche.isEnabled()) {
                    switchCoche.toggle();
                }
            });
        }

        // Autoguardado al cambiar el switch
        switchCoche.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (!isLoading) {
                saveProfileToApi();
            }
        });

        rvLanguages = view.findViewById(R.id.rvLanguages);
        btnEditLanguage = view.findViewById(R.id.btnEditLanguage);

        // Botón Logout
        View btnLogoutView = view.findViewById(R.id.btnLogout);
        if (btnLogoutView != null) {
            btnLogoutView.setOnClickListener(v -> logout());
        }
    }

    private void loadUserDataFromApi() {
        if (getContext() == null)
            return;

        SharedPreferences prefs = requireContext().getSharedPreferences("VoluntariadoPrefs", Context.MODE_PRIVATE);
        int userId = prefs.getInt("user_id", -1);
        String savedRole = prefs.getString("rol", "Voluntario");

        Log.d(TAG, "Cargando perfil para userId=" + userId);
        if (tvUserRole != null) {
            tvUserRole.setText(savedRole);
        }

        if (userId != -1) {
            ApiClient.getService().getVoluntarioDetail(userId).enqueue(new Callback<VoluntarioResponse>() {
                @Override
                public void onResponse(Call<VoluntarioResponse> call, Response<VoluntarioResponse> response) {
                    if (!isAdded())
                        return; // Verificar si el fragmento sigue activo

                    Log.d(TAG, "Respuesta API: code=" + response.code());
                    if (response.isSuccessful() && response.body() != null) {
                        VoluntarioResponse user = response.body();
                        Log.d(TAG, "Datos recibidos: " + user.getNombre());
                        populateData(user);
                    } else if (response.code() == 404) {
                        Log.w(TAG, "Perfil 404 - No existe. Redirigiendo a completar perfil.");
                        Toast.makeText(getContext(), "Perfil no encontrado. Completa tus datos.", Toast.LENGTH_LONG)
                                .show();
                        Intent intent = new Intent(getContext(), AuthCompleteProfile.class);
                        startActivity(intent);
                        if (getActivity() != null)
                            getActivity().finish();
                    } else {
                        Log.e(TAG, "Error API: " + response.code());
                        Toast.makeText(getContext(), "Error cargando perfil: " + response.code(), Toast.LENGTH_SHORT)
                                .show();
                    }
                }

                @Override
                public void onFailure(Call<VoluntarioResponse> call, Throwable t) {
                    if (!isAdded())
                        return;
                    Log.e(TAG, "Fallo red perfil", t);
                    Toast.makeText(getContext(), "Sin conexión. No se pudo cargar el perfil.", Toast.LENGTH_SHORT)
                            .show();
                }
            });
        } else {
            Log.e(TAG, "userId es -1");
            Toast.makeText(getContext(), "Sesión inválida. Por favor, inicia sesión.", Toast.LENGTH_LONG).show();
        }
    }

    private void populateData(VoluntarioResponse user) {
        isLoading = true;
        tvUserName.setText(user.getNombreCompleto());
        etNombre.setText(user.getNombre());
        etApellidos.setText(user.getApellidos());

        // Datos reales de la API
        etDni.setText(user.getDni());
        etTelefono.setText(user.getTelefono());
        etFechaNacimiento.setText(formatDateForDisplay(user.getFechaNac()));
        switchCoche.setChecked(user.isCarnetConducir());

        // Descripción/Observaciones
        etObservaciones.setText(user.getDescripcion() != null ? user.getDescripcion() : "");

        // Mostrar idiomas reales de la API
        // Mostrar idiomas reales de la API
        listaIdiomas.clear();
        listaNombresIdiomas.clear();

        if (user.getIdiomas() != null) {
            listaIdiomas.addAll(user.getIdiomas());
            for (VoluntarioResponse.IdiomaInfo idioma : user.getIdiomas()) {
                listaNombresIdiomas.add(idioma.getIdioma() + " (" + idioma.getNivel() + ")");
            }
        }
        setupLanguagesRecycler(listaNombresIdiomas);

        // Bloquear edición inicial (excepto Switch Coche que es always-on)
        setFieldsEnabled(false, etNombre, etApellidos, etDni, etTelefono, etFechaNacimiento);
        setFieldsEnabled(false, etObservaciones);

        // Switch siempre habilitado
        switchCoche.setEnabled(true);
        switchCoche.setClickable(true);
        switchCoche.setFocusable(true);
        switchCoche.setAlpha(1.0f);
        isLoading = false;
    }

    private String formatDateForDisplay(String apiDate) {
        if (apiDate == null || !apiDate.contains("-"))
            return apiDate;
        try {
            String[] parts = apiDate.split("-");
            if (parts.length == 3) {
                return parts[2] + "/" + parts[1] + "/" + parts[0];
            }
        } catch (Exception e) {
            // Ignorar error de formato
        }
        return apiDate;
    }

    private void setupLanguagesRecycler(List<String> nombresIdiomas) {
        // Usamos la lista de nombres para el adaptador visual
        languageAdapter = new SimpleLanguageAdapter(nombresIdiomas);
        rvLanguages.setLayoutManager(new LinearLayoutManager(getContext()));
        rvLanguages.setAdapter(languageAdapter);
    }

    private void setupEditLogic() {
        // Info Personal
        btnEditInfo.setOnClickListener(v -> {
            if (!isEditingInfo) {
                isEditingInfo = true;
                setFieldsEnabled(true, etNombre, etApellidos, etDni, etTelefono, etFechaNacimiento);

                btnEditInfo.setImageResource(R.drawable.ic_check_circle);
                etNombre.requestFocus();
            } else {
                // Guardar cambios en la API
                saveProfileToApi();
            }
        });

        // Observaciones
        btnEditObs.setOnClickListener(v -> {
            if (!isEditingObs) {
                isEditingObs = true;
                setFieldsEnabled(true, etObservaciones);
                btnEditObs.setImageResource(R.drawable.ic_check_circle);
            } else {
                // Guardar cambios en la API
                saveProfileToApi();
                isEditingObs = false;
                setFieldsEnabled(false, etObservaciones);
                btnEditObs.setImageResource(R.drawable.ic_edit);
            }
        });

        // Idiomas Dialog
        // Idiomas Dialog
        btnEditLanguage.setOnClickListener(v -> {
            LanguageDialog dialog = new LanguageDialog();

            // Pasar lista actual al diálogo
            List<LanguageDialog.IdiomaItem> items = new ArrayList<>();
            for (VoluntarioResponse.IdiomaInfo info : listaIdiomas) {
                items.add(new LanguageDialog.IdiomaItem(info.getIdIdioma(), info.getIdioma(), info.getNivel()));
            }
            dialog.setIdiomasActuales(items);

            dialog.show(getChildFragmentManager(), "LanguageDialog");
        });
    }

    private void saveProfileToApi() {
        if (getContext() == null)
            return;

        SharedPreferences prefs = requireContext().getSharedPreferences("VoluntariadoPrefs", Context.MODE_PRIVATE);
        int userId = prefs.getInt("user_id", -1);

        if (userId == -1) {
            Toast.makeText(getContext(), "Error: Sesión no válida", Toast.LENGTH_SHORT).show();
            return;
        }

        // Recoger datos de los campos
        String nombre = etNombre.getText().toString().trim();
        String apellidos = etApellidos.getText().toString().trim();
        String telefono = etTelefono.getText().toString().trim();
        String fechaDisplay = etFechaNacimiento.getText().toString().trim();
        boolean carnetConducir = switchCoche.isChecked();
        String descripcion = etObservaciones.getText().toString().trim();

        // Convertir fecha de DD/MM/YYYY a YYYY-MM-DD
        String fechaApi = formatDateForApi(fechaDisplay);

        // Crear el DTO
        VoluntarioUpdateRequest request = new VoluntarioUpdateRequest(
                nombre, apellidos, telefono, fechaApi, carnetConducir, descripcion);

        Log.d(TAG, "Guardando perfil para userId=" + userId);

        // Llamar a la API
        ApiClient.getService().updateVoluntario(userId, userId, request).enqueue(new Callback<VoluntarioResponse>() {
            @Override
            public void onResponse(Call<VoluntarioResponse> call, Response<VoluntarioResponse> response) {
                if (!isAdded())
                    return;

                if (response.isSuccessful() && response.body() != null) {
                    Log.d(TAG, "Perfil actualizado correctamente");
                    Toast.makeText(getContext(), "Perfil guardado", Toast.LENGTH_SHORT).show();

                    // Actualizar UI con los datos devueltos
                    populateData(response.body());

                    // Volver a modo visualización
                    isEditingInfo = false;
                    setFieldsEnabled(false, etNombre, etApellidos, etDni, etTelefono, etFechaNacimiento);
                    btnEditInfo.setImageResource(R.drawable.ic_edit);
                } else {
                    Log.e(TAG, "Error al guardar: " + response.code());
                    Toast.makeText(getContext(), "Error al guardar: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<VoluntarioResponse> call, Throwable t) {
                if (!isAdded())
                    return;
                Log.e(TAG, "Error de red al guardar", t);
                Toast.makeText(getContext(), "Error de conexión", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String formatDateForApi(String displayDate) {
        if (displayDate == null || displayDate.isEmpty() || !displayDate.contains("/")) {
            return null;
        }
        try {
            String[] parts = displayDate.split("/");
            if (parts.length == 3) {
                return parts[2] + "-" + parts[1] + "-" + parts[0]; // YYYY-MM-DD
            }
        } catch (Exception e) {
            Log.e(TAG, "Error formateando fecha", e);
        }
        return null;
    }

    private void setFieldsEnabled(boolean enabled, EditText... fields) {
        for (EditText field : fields) {
            field.setFocusable(enabled);
            field.setFocusableInTouchMode(enabled);
            field.setClickable(enabled);
            field.setCursorVisible(enabled);
            if (enabled) {
                field.setBackgroundResource(R.drawable.bg_tab_selected);
            } else {
                field.setBackground(null);
            }
        }
        etFechaNacimiento.setClickable(enabled);
    }

    private void setupDatePicker() {
        etFechaNacimiento.setOnClickListener(v -> {
            if (!isEditingInfo)
                return;

            final Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            if (getContext() != null) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(),
                        (view, selectedYear, selectedMonth, selectedDay) -> {
                            String selectedDate = selectedDay + "/" + (selectedMonth + 1) + "/" + selectedYear;
                            etFechaNacimiento.setText(selectedDate);
                        }, year, month, day);
                datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
                datePickerDialog.show();
            }
        });
    }

    private void logout() {
        if (getActivity() == null)
            return;

        googleSignInClient.signOut().addOnCompleteListener(getActivity(), task -> {
            SharedPreferences prefs = requireContext().getSharedPreferences("VoluntariadoPrefs", Context.MODE_PRIVATE);
            prefs.edit().clear().apply();

            Intent intent = new Intent(getContext(), AuthLogin.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            // Cerramos la Activity contenedora (UserDashboard)
            getActivity().finish();
        });
    }

    @Override
    public void onLanguagesSaved() {
        // Recargar el perfil para mostrar los idiomas actualizados
        loadUserDataFromApi();
    }

    // Adapter Interno
    private class SimpleLanguageAdapter extends RecyclerView.Adapter<SimpleLanguageAdapter.ViewHolder> {
        private List<String> languages;

        public SimpleLanguageAdapter(List<String> languages) {
            this.languages = languages;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_language, parent, false);
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            holder.tvName.setText(languages.get(position));
            if (holder.btnDelete != null)
                holder.btnDelete.setVisibility(View.GONE);
        }

        @Override
        public int getItemCount() {
            return languages.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            TextView tvName;
            View btnDelete;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                tvName = itemView.findViewById(R.id.tvLanguageName);
                btnDelete = itemView.findViewById(R.id.btnDelete);
            }
        }
    }
}