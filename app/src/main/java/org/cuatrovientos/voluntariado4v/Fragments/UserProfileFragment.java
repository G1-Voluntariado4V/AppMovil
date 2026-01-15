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
    private List<String> listaIdiomas = new ArrayList<>();

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
        if (getContext() == null) return;

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

        rvLanguages = view.findViewById(R.id.rvLanguages);
        btnEditLanguage = view.findViewById(R.id.btnEditLanguage);

        // Botón Logout
        View btnLogoutView = view.findViewById(R.id.btnLogout);
        if (btnLogoutView != null) {
            btnLogoutView.setOnClickListener(v -> logout());
        }
    }

    private void loadUserDataFromApi() {
        if (getContext() == null) return;

        SharedPreferences prefs = requireContext().getSharedPreferences("VoluntariadoPrefs", Context.MODE_PRIVATE);
        int userId = prefs.getInt("user_id", -1);
        String savedRole = prefs.getString("rol", "Voluntario");

        Log.d(TAG, "Cargando perfil para userId=" + userId);
        if (tvUserRole != null) {
            tvUserRole.setText(savedRole);
        }

        if (userId != -1) {
            ApiClient.getService().getVoluntario(userId).enqueue(new Callback<VoluntarioResponse>() {
                @Override
                public void onResponse(Call<VoluntarioResponse> call, Response<VoluntarioResponse> response) {
                    if (!isAdded()) return; // Verificar si el fragmento sigue activo

                    Log.d(TAG, "Respuesta API: code=" + response.code());
                    if (response.isSuccessful() && response.body() != null) {
                        VoluntarioResponse user = response.body();
                        Log.d(TAG, "Datos recibidos: " + user.getNombre());
                        populateData(user);
                    } else if (response.code() == 404) {
                        Log.w(TAG, "Perfil 404 - No existe. Redirigiendo a completar perfil.");
                        Toast.makeText(getContext(), "Perfil no encontrado. Completa tus datos.", Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(getContext(), AuthCompleteProfile.class);
                        startActivity(intent);
                        if (getActivity() != null) getActivity().finish();
                    } else {
                        Log.e(TAG, "Error API: " + response.code());
                        Toast.makeText(getContext(), "Error cargando perfil: " + response.code(), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<VoluntarioResponse> call, Throwable t) {
                    if (!isAdded()) return;
                    Log.e(TAG, "Fallo red perfil", t);
                    Toast.makeText(getContext(), "Sin conexión. No se pudo cargar el perfil.", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Log.e(TAG, "userId es -1");
            Toast.makeText(getContext(), "Sesión inválida. Por favor, inicia sesión.", Toast.LENGTH_LONG).show();
        }
    }

    private void populateData(VoluntarioResponse user) {
        tvUserName.setText(user.getNombreCompleto());
        etNombre.setText(user.getNombre());
        etApellidos.setText(user.getApellidos());
        etDni.setText(user.getDni());
        etTelefono.setText(user.getTelefono());

        // Formatear fecha
        etFechaNacimiento.setText(formatDateForDisplay(user.getFechaNac()));

        switchCoche.setChecked(user.isCarnetConducir());

        // Campos mock
        etObservaciones.setText("");
        setupLanguagesRecycler(new ArrayList<>());

        // Bloquear edición inicial
        setFieldsEnabled(false, etNombre, etApellidos, etDni, etTelefono, etFechaNacimiento);
        setFieldsEnabled(false, etObservaciones);
        switchCoche.setEnabled(false);
    }

    private String formatDateForDisplay(String apiDate) {
        if (apiDate == null || !apiDate.contains("-")) return apiDate;
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

    private void setupLanguagesRecycler(List<String> idiomas) {
        listaIdiomas = idiomas;
        languageAdapter = new SimpleLanguageAdapter(listaIdiomas);
        rvLanguages.setLayoutManager(new LinearLayoutManager(getContext()));
        rvLanguages.setAdapter(languageAdapter);
    }

    private void setupEditLogic() {
        // Info Personal
        btnEditInfo.setOnClickListener(v -> {
            if (!isEditingInfo) {
                isEditingInfo = true;
                setFieldsEnabled(true, etNombre, etApellidos, etDni, etTelefono, etFechaNacimiento);
                switchCoche.setEnabled(true);
                btnEditInfo.setImageResource(R.drawable.ic_check_circle);
                etNombre.requestFocus();
            } else {
                // Guardar (Mock por ahora)
                Toast.makeText(getContext(), "Guardado no disponible en API aún", Toast.LENGTH_SHORT).show();

                isEditingInfo = false;
                setFieldsEnabled(false, etNombre, etApellidos, etDni, etTelefono, etFechaNacimiento);
                switchCoche.setEnabled(false);
                btnEditInfo.setImageResource(R.drawable.ic_edit);
            }
        });

        // Observaciones (Mock)
        btnEditObs.setOnClickListener(v -> {
            if (!isEditingObs) {
                isEditingObs = true;
                setFieldsEnabled(true, etObservaciones);
                btnEditObs.setImageResource(R.drawable.ic_check_circle);
            } else {
                isEditingObs = false;
                setFieldsEnabled(false, etObservaciones);
                btnEditObs.setImageResource(R.drawable.ic_edit);
            }
        });

        // Idiomas Dialog
        btnEditLanguage.setOnClickListener(v -> {
            LanguageDialog dialog = new LanguageDialog();
            // Nota: Es posible que necesites ajustar LanguageDialog para que reconozca el Fragment
            dialog.show(getChildFragmentManager(), "LanguageDialog");
        });
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
            if (!isEditingInfo) return;

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
        if (getActivity() == null) return;

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
        Toast.makeText(getContext(), "Idiomas actualizados (Visual)", Toast.LENGTH_SHORT).show();
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