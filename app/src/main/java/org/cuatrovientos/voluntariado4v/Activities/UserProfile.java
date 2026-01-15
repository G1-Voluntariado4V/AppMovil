package org.cuatrovientos.voluntariado4v.Activities;

import android.app.DatePickerDialog;
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

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.switchmaterial.SwitchMaterial;

import org.cuatrovientos.voluntariado4v.App.NavigationUtils;
import org.cuatrovientos.voluntariado4v.Dialogs.LanguageDialog;
import org.cuatrovientos.voluntariado4v.Models.VoluntarioResponse;
import org.cuatrovientos.voluntariado4v.R;
import org.cuatrovientos.voluntariado4v.api.ApiClient;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserProfile extends AppCompatActivity implements LanguageDialog.OnLanguagesSavedListener {

    private static final String TAG = "UserProfile";

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
    private SwitchMaterial switchCoche;

    // Idiomas (Vacío por ahora, API no provee)
    private RecyclerView rvLanguages;
    private ImageView btnEditLanguage;
    private SimpleLanguageAdapter languageAdapter;
    private List<String> listaIdiomas = new ArrayList<>();

    private GoogleSignInClient googleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_user_profile);

        setupGoogleSignIn();
        initViews();
        setupNavigation();
        setupDatePicker();

        // Cargar datos reales de la API
        loadUserDataFromApi();

        setupEditLogic();
    }

    private void setupGoogleSignIn() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(this, gso);
    }

    private void initViews() {
        tvUserName = findViewById(R.id.tvUserName);
        tvUserRole = findViewById(R.id.tvUserRole);

        etNombre = findViewById(R.id.etNombre);
        etApellidos = findViewById(R.id.etApellidos);
        etDni = findViewById(R.id.etDni);
        etTelefono = findViewById(R.id.etTelefono);
        etFechaNacimiento = findViewById(R.id.etFechaNacimiento);
        btnEditInfo = findViewById(R.id.btnEditInfo);

        etObservaciones = findViewById(R.id.etObservaciones);
        btnEditObs = findViewById(R.id.btnEditObs);

        switchCoche = findViewById(R.id.switchCoche);

        rvLanguages = findViewById(R.id.rvLanguages);
        btnEditLanguage = findViewById(R.id.btnEditLanguage);

        View btnLogoutView = findViewById(R.id.btnLogout);
        if (btnLogoutView != null) {
            btnLogoutView.setOnClickListener(v -> logout());
        }
    }

    private void loadUserDataFromApi() {
        SharedPreferences prefs = getSharedPreferences("VoluntariadoPrefs", MODE_PRIVATE);
        int userId = prefs.getInt("user_id", -1);
        String savedRole = prefs.getString("rol", "Voluntario");

        Log.d(TAG, "Cargando perfil para userId=" + userId);
        tvUserRole.setText(savedRole);

        if (userId != -1) {
            ApiClient.getService().getVoluntario(userId).enqueue(new Callback<VoluntarioResponse>() {
                @Override
                public void onResponse(Call<VoluntarioResponse> call, Response<VoluntarioResponse> response) {
                    Log.d(TAG, "Respuesta API: code=" + response.code());
                    if (response.isSuccessful() && response.body() != null) {
                        VoluntarioResponse user = response.body();
                        Log.d(TAG,
                                "Datos recibidos: nombre=" + user.getNombre() + ", apellidos=" + user.getApellidos());
                        populateData(user);
                    } else if (response.code() == 404) {
                        Log.w(TAG, "Perfil 404 - No existe. Redirigiendo a completar perfil.");
                        Toast.makeText(UserProfile.this, "Perfil no encontrado. Completa tus datos.", Toast.LENGTH_LONG)
                                .show();
                        Intent intent = new Intent(UserProfile.this, AuthCompleteProfile.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Log.e(TAG, "Error API: " + response.code() + " - " + response.message());
                        Toast.makeText(UserProfile.this, "Error cargando perfil: " + response.code(),
                                Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<VoluntarioResponse> call, Throwable t) {
                    Log.e(TAG, "Fallo red perfil", t);
                    Toast.makeText(UserProfile.this, "Sin conexión. Mostrando caché local...", Toast.LENGTH_SHORT)
                            .show();
                }
            });
        } else {
            Log.e(TAG, "userId es -1, no se puede cargar perfil");
            Toast.makeText(this, "Sesión inválida. Por favor, inicia sesión.", Toast.LENGTH_LONG).show();
        }
    }

    private void populateData(VoluntarioResponse user) {
        tvUserName.setText(user.getNombreCompleto());
        etNombre.setText(user.getNombre());
        etApellidos.setText(user.getApellidos());
        etDni.setText(user.getDni());
        etTelefono.setText(user.getTelefono());

        // Formatear fecha para visualización (YYYY-MM-DD -> DD/MM/YYYY)
        etFechaNacimiento.setText(formatDateForDisplay(user.getFechaNac()));

        switchCoche.setChecked(user.isCarnetConducir());

        // Campos no disponibles en API (Observaciones / Idiomas) -> Vacíos
        etObservaciones.setText("");
        setupLanguagesRecycler(new ArrayList<>());

        // Bloquear edición al inicio
        setFieldsEnabled(false, etNombre, etApellidos, etDni, etTelefono, etFechaNacimiento);
        setFieldsEnabled(false, etObservaciones);
        switchCoche.setEnabled(false);
    }

    private String formatDateForDisplay(String apiDate) {
        // API: 2000-12-25 -> Display: 25/12/2000
        if (apiDate == null || !apiDate.contains("-"))
            return apiDate;
        try {
            String[] parts = apiDate.split("-");
            if (parts.length == 3) {
                return parts[2] + "/" + parts[1] + "/" + parts[0];
            }
        } catch (Exception e) {
        }
        return apiDate;
    }

    private void setupLanguagesRecycler(List<String> idiomas) {
        listaIdiomas = idiomas;
        languageAdapter = new SimpleLanguageAdapter(listaIdiomas);
        rvLanguages.setLayoutManager(new LinearLayoutManager(this));
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
                // Guardar (Mock por ahora, API no tiene PUT implementado en Service)
                Toast.makeText(this, "Guardado no disponible en API aún", Toast.LENGTH_SHORT).show();

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
            dialog.show(getSupportFragmentManager(), "LanguageDialog");
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
            if (!isEditingInfo)
                return;

            final Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(UserProfile.this,
                    (view, selectedYear, selectedMonth, selectedDay) -> {
                        String selectedDate = selectedDay + "/" + (selectedMonth + 1) + "/" + selectedYear;
                        etFechaNacimiento.setText(selectedDate);
                    }, year, month, day);
            datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
            datePickerDialog.show();
        });
    }

    // Auth Logic
    private void logout() {
        googleSignInClient.signOut().addOnCompleteListener(this, task -> {
            SharedPreferences prefs = getSharedPreferences("VoluntariadoPrefs", MODE_PRIVATE);
            prefs.edit().clear().apply();

            Intent intent = new Intent(this, AuthLogin.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }

    private void setupNavigation() {
        BottomNavigationView bottomNav = findViewById(R.id.bottomNavigation);
        NavigationUtils.setupNavigation(this, bottomNav, R.id.nav_profile);
    }

    @Override
    public void onLanguagesSaved() {
        // Callback del dialogo (Mock)
        Toast.makeText(this, "Idiomas actualizados (Visual)", Toast.LENGTH_SHORT).show();
    }

    // Adapter
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