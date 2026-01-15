package org.cuatrovientos.voluntariado4v.Activities;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.switchmaterial.SwitchMaterial;

import org.cuatrovientos.voluntariado4v.App.MockDataProvider;
import org.cuatrovientos.voluntariado4v.App.NavigationUtils;
import org.cuatrovientos.voluntariado4v.Dialogs.LanguageDialog;
import org.cuatrovientos.voluntariado4v.R;

import java.util.Calendar;
import java.util.List;

public class UserProfile extends AppCompatActivity implements LanguageDialog.OnLanguagesSavedListener {

    // Vistas de Header
    private TextView tvUserName, tvUserRole;

    // Campos de Información Personal
    private EditText etNombre, etApellidos, etDni, etTelefono, etFechaNacimiento;
    private ImageView btnEditInfo;
    private boolean isEditingInfo = false;

    // Campos de Observaciones
    private EditText etObservaciones;
    private ImageView btnEditObs;
    private boolean isEditingObs = false;

    // Info Adicional
    private SwitchMaterial switchCoche;

    // Idiomas
    private RecyclerView rvLanguages;
    private ImageView btnEditLanguage;
    private SimpleLanguageAdapter languageAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_user_profile);

        initViews();
        setupNavigation();
        setupDatePicker();

        // Cargar datos iniciales
        loadUserData();

        // Configurar botones de edición
        setupEditLogic();
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
    }

    private void loadUserData() {
        MockDataProvider.MockUser user = MockDataProvider.getLoggedUser();

        // Header
        tvUserName.setText(user.nombre + " " + user.apellidos);
        tvUserRole.setText(user.rol);

        // Info Personal
        etNombre.setText(user.nombre);
        etApellidos.setText(user.apellidos);
        etDni.setText(user.dni);
        etTelefono.setText(user.telefono);
        etFechaNacimiento.setText(user.fechaNacimiento);

        // Observaciones
        etObservaciones.setText(user.observaciones);

        // Coche
        switchCoche.setChecked(user.cochePropio);

        // Idiomas
        setupLanguagesRecycler(user.idiomas);

        // Asegurar estado inicial (bloqueado)
        setFieldsEnabled(false, etNombre, etApellidos, etDni, etTelefono, etFechaNacimiento);
        setFieldsEnabled(false, etObservaciones);
        switchCoche.setEnabled(false); // Asumimos que también se bloquea hasta editar algo, o se deja libre.
        // Si quieres que el switch sea editable solo con botón, habría que añadir un botón para esa sección o meterlo en Info Personal.
        // Por simplicidad, lo dejaremos habilitado siempre o lo vincularemos a Info Personal.
        switchCoche.setEnabled(true);
    }

    private void setupLanguagesRecycler(List<String> idiomas) {
        languageAdapter = new SimpleLanguageAdapter(idiomas);
        rvLanguages.setLayoutManager(new LinearLayoutManager(this));
        rvLanguages.setAdapter(languageAdapter);
    }

    private void setupEditLogic() {
        // --- Lógica Editar Info Personal ---
        btnEditInfo.setOnClickListener(v -> {
            if (!isEditingInfo) {
                // Activar Edición
                isEditingInfo = true;
                setFieldsEnabled(true, etNombre, etApellidos, etDni, etTelefono, etFechaNacimiento);
                btnEditInfo.setImageResource(R.drawable.ic_check_circle); // Cambiar icono a guardar (necesitas un icono de check)
                btnEditInfo.setBackgroundResource(R.drawable.bg_tag_green); // Cambiar color a verde
                etNombre.requestFocus();
            } else {
                // Guardar Cambios
                savePersonalInfo();
                isEditingInfo = false;
                setFieldsEnabled(false, etNombre, etApellidos, etDni, etTelefono, etFechaNacimiento);
                btnEditInfo.setImageResource(R.drawable.ic_edit); // Volver icono editar
                btnEditInfo.setBackgroundResource(R.drawable.bg_tag_blue); // Volver color azul
                Toast.makeText(this, "Datos guardados correctamente", Toast.LENGTH_SHORT).show();
            }
        });

        // --- Lógica Editar Observaciones ---
        btnEditObs.setOnClickListener(v -> {
            if (!isEditingObs) {
                isEditingObs = true;
                setFieldsEnabled(true, etObservaciones);
                btnEditObs.setImageResource(R.drawable.ic_check_circle);
                btnEditObs.setBackgroundResource(R.drawable.bg_tag_green);
                etObservaciones.requestFocus();
            } else {
                saveObservations();
                isEditingObs = false;
                setFieldsEnabled(false, etObservaciones);
                btnEditObs.setImageResource(R.drawable.ic_edit);
                btnEditObs.setBackgroundResource(R.drawable.bg_tag_blue);
                Toast.makeText(this, "Observaciones actualizadas", Toast.LENGTH_SHORT).show();
            }
        });

        // --- Lógica Editar Idiomas (Abre Dialog) ---
        btnEditLanguage.setOnClickListener(v -> {
            LanguageDialog dialog = new LanguageDialog();
            dialog.show(getSupportFragmentManager(), "LanguageDialog");
        });

        // Listener para cambio en el Switch de coche (se guarda al momento)
        switchCoche.setOnCheckedChangeListener((buttonView, isChecked) -> {
            MockDataProvider.getLoggedUser().cochePropio = isChecked;
        });
    }

    private void savePersonalInfo() {
        MockDataProvider.MockUser user = MockDataProvider.getLoggedUser();
        user.nombre = etNombre.getText().toString();
        user.apellidos = etApellidos.getText().toString();
        user.dni = etDni.getText().toString();
        user.telefono = etTelefono.getText().toString();
        user.fechaNacimiento = etFechaNacimiento.getText().toString();

        // Actualizar header también por si cambió el nombre
        tvUserName.setText(user.nombre + " " + user.apellidos);
    }

    private void saveObservations() {
        MockDataProvider.getLoggedUser().observaciones = etObservaciones.getText().toString();
    }

    // Helper para activar/desactivar edición
    private void setFieldsEnabled(boolean enabled, EditText... fields) {
        for (EditText field : fields) {
            field.setFocusable(enabled);
            field.setFocusableInTouchMode(enabled);
            field.setClickable(enabled);
            field.setCursorVisible(enabled);
            // Opcional: Cambiar visualmente el fondo para indicar que es editable
            if (enabled) {
                field.setBackgroundResource(R.drawable.bg_circle_white); // Borde visible
            } else {
                field.setBackground(null); // Transparente o estilo plano
            }
        }
        // El campo fecha siempre requiere click especial, así que si está disabled, anulamos su click listener
        etFechaNacimiento.setClickable(enabled);
    }

    private void setupDatePicker() {
        etFechaNacimiento.setOnClickListener(v -> {
            if (!isEditingInfo) return; // Solo abrir si estamos editando

            final Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    UserProfile.this,
                    (view, selectedYear, selectedMonth, selectedDay) -> {
                        String selectedDate = selectedDay + "/" + (selectedMonth + 1) + "/" + selectedYear;
                        etFechaNacimiento.setText(selectedDate);
                    },
                    year, month, day
            );
            datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
            datePickerDialog.show();
        });
    }

    private void setupNavigation() {
        BottomNavigationView bottomNav = findViewById(R.id.bottomNavigation);
        NavigationUtils.setupNavigation(this, bottomNav, R.id.nav_profile);
    }

    // Implementación de la interfaz del Dialog para refrescar la lista
    @Override
    public void onLanguagesSaved() {
        // Recargar la lista desde MockData
        List<String> updatedLanguages = MockDataProvider.getLoggedUser().idiomas;
        setupLanguagesRecycler(updatedLanguages);
    }

    // --- Adapter Interno Sencillo para Mostrar Idiomas (Solo lectura) ---
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
            // En la vista de perfil no mostramos el botón de borrar, eso es solo en el diálogo
            if (holder.btnDelete != null) {
                holder.btnDelete.setVisibility(View.GONE);
            }
        }

        @Override
        public int getItemCount() {
            return languages.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            TextView tvName;
            View btnDelete; // Referencia al botón borrar si existe en el layout

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                tvName = itemView.findViewById(R.id.tvLanguageName);
                // Intentamos buscar el botón borrar para ocultarlo
                btnDelete = itemView.findViewById(R.id.btnDelete); // Asegúrate que tu item_language tiene este ID
            }
        }
    }
}