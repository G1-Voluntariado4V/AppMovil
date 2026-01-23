package org.cuatrovientos.voluntariado4v.Activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar; // Importante

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.cuatrovientos.voluntariado4v.API.ApiClient;
import org.cuatrovientos.voluntariado4v.API.VoluntariadoApiService;
import org.cuatrovientos.voluntariado4v.Models.EstadoRequest;
import org.cuatrovientos.voluntariado4v.Models.MensajeResponse;
import org.cuatrovientos.voluntariado4v.Models.OrganizacionResponse;
import org.cuatrovientos.voluntariado4v.Models.OrganizacionUpdateRequest;
import org.cuatrovientos.voluntariado4v.Models.VoluntarioResponse;
import org.cuatrovientos.voluntariado4v.Models.VoluntarioUpdateRequest;
import org.cuatrovientos.voluntariado4v.R;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditUserActivity extends AppCompatActivity {

    // UI Components
    private AutoCompleteTextView spinnerRole, spinnerStatus;
    private TextInputEditText etName, etSurname, etEmail, etPhone, etDescription, etWeb, etAddress;
    private TextInputLayout tilSurname, tilWeb, tilAddress;
    private MaterialButton btnSave;
    private FrameLayout loadingOverlay;
    private Toolbar toolbar; // Nuevo

    // Data
    private int targetUserId;
    private String originalRole;
    private int currentAdminId;
    private VoluntariadoApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_user);

        targetUserId = getIntent().getIntExtra("USER_ID", -1);
        originalRole = getIntent().getStringExtra("USER_ROLE");

        SharedPreferences prefs = getSharedPreferences("AppPrefs", Context.MODE_PRIVATE);
        currentAdminId = prefs.getInt("user_id", -1);

        if (targetUserId == -1) {
            Toast.makeText(this, "Error: Usuario no válido", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        initViews();
        setupToolbar(); // Configurar la Toolbar nueva
        setupSpinners();

        apiService = ApiClient.getService();
        loadUserData();

        // Eliminar btnBack manual, ahora lo maneja setupToolbar()
        // findViewById(R.id.btnBack).setOnClickListener(v -> finish()); <-- BORRAR ESTO

        // Listener seguro
        if (btnSave != null) {
            btnSave.setOnClickListener(v -> saveChanges());
        }
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbar); // Nuevo ID del XML

        spinnerRole = findViewById(R.id.spinnerRole);
        spinnerStatus = findViewById(R.id.spinnerStatus);

        etName = findViewById(R.id.etName);
        etSurname = findViewById(R.id.etSurname);
        etEmail = findViewById(R.id.etEmail);
        etPhone = findViewById(R.id.etPhone);
        etDescription = findViewById(R.id.etDescription);
        etWeb = findViewById(R.id.etWeb);
        etAddress = findViewById(R.id.etAddress);

        tilSurname = findViewById(R.id.tilSurname);
        tilWeb = findViewById(R.id.tilWeb);
        tilAddress = findViewById(R.id.tilAddress);

        btnSave = findViewById(R.id.btnSaveChanges);
        loadingOverlay = findViewById(R.id.loadingOverlay);
    }

    // Método nuevo para manejar la flecha de atrás de la Toolbar
    private void setupToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setTitle("Editar Usuario");
        }
        toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void setupSpinners() {
        String[] roles = new String[] { "Voluntario", "Organización", "Coordinador" };
        ArrayAdapter<String> adapterRoles = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line,
                roles);
        spinnerRole.setAdapter(adapterRoles);

        String[] estados = new String[] { "Pendiente", "Activa", "Bloqueada", "Rechazada" };
        ArrayAdapter<String> adapterStatus = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line,
                estados);
        spinnerStatus.setAdapter(adapterStatus);
    }

    private void loadUserData() {
        showLoading(true);
        String normalizedRole = originalRole != null ? originalRole.toLowerCase() : "";

        if (normalizedRole.contains("voluntario")) {
            spinnerRole.setText("Voluntario", false);
            setupUIForVolunteer();
            loadVoluntarioData();
        } else if (normalizedRole.contains("org")) {
            spinnerRole.setText("Organización", false);
            setupUIForOrganization();
            loadOrganizacionData();
        } else {
            spinnerRole.setText("Coordinador", false);
            showLoading(false);
        }
    }

    private void setupUIForVolunteer() {
        tilSurname.setVisibility(View.VISIBLE);
        tilWeb.setVisibility(View.GONE);
        tilAddress.setVisibility(View.GONE);
    }

    private void setupUIForOrganization() {
        tilSurname.setVisibility(View.GONE);
        tilWeb.setVisibility(View.VISIBLE);
        tilAddress.setVisibility(View.VISIBLE);
    }

    private void loadVoluntarioData() {
        apiService.getVoluntarioDetail(targetUserId).enqueue(new Callback<VoluntarioResponse>() {
            @Override
            public void onResponse(Call<VoluntarioResponse> call, Response<VoluntarioResponse> response) {
                showLoading(false);
                if (response.isSuccessful() && response.body() != null) {
                    VoluntarioResponse v = response.body();
                    etName.setText(v.getNombre());
                    etSurname.setText(v.getApellidos());
                    etEmail.setText(v.getCorreo());
                    etPhone.setText(v.getTelefono());
                    etDescription.setText(v.getDescripcion());
                }
            }

            @Override
            public void onFailure(Call<VoluntarioResponse> call, Throwable t) {
                showLoading(false);
            }
        });
    }

    private void loadOrganizacionData() {
        apiService.getOrganizationDetail(targetUserId).enqueue(new Callback<OrganizacionResponse>() {
            @Override
            public void onResponse(Call<OrganizacionResponse> call, Response<OrganizacionResponse> response) {
                showLoading(false);
                if (response.isSuccessful() && response.body() != null) {
                    OrganizacionResponse o = response.body();
                    etName.setText(o.getNombre());
                    etEmail.setText(o.getEmail());
                    etPhone.setText(o.getTelefono());
                    etDescription.setText(o.getDescripcion());
                    etWeb.setText(o.getSitioWeb());
                    etAddress.setText(o.getDireccion());
                }
            }

            @Override
            public void onFailure(Call<OrganizacionResponse> call, Throwable t) {
                showLoading(false);
            }
        });
    }

    private void saveChanges() {
        showLoading(true);
        updateStatus();

        String currentRole = spinnerRole.getText().toString().toLowerCase();
        if (currentRole.contains("vol")) {
            updateVoluntarioData();
        } else if (currentRole.contains("org")) {
            updateOrganizacionData();
        } else {
            showLoading(false);
            Toast.makeText(this, "Datos guardados", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void updateStatus() {
        String status = spinnerStatus.getText().toString();
        String rolPath = originalRole.toLowerCase().contains("org") ? "organizaciones" : "voluntarios";

        apiService.updateUserStatus(currentAdminId, rolPath, targetUserId, new EstadoRequest(status))
                .enqueue(new Callback<MensajeResponse>() {
                    @Override
                    public void onResponse(Call<MensajeResponse> c, Response<MensajeResponse> r) {
                    }

                    @Override
                    public void onFailure(Call<MensajeResponse> c, Throwable t) {
                    }
                });
    }

    private void updateVoluntarioData() {
        String nombre = etName.getText().toString().trim();
        String apellidos = etSurname.getText().toString().trim();
        String telefono = etPhone.getText().toString().trim();
        String descripcion = etDescription.getText().toString().trim();

        if (nombre.isEmpty()) {
            etName.setError("Requerido");
            return;
        }
        if (nombre.length() > 100) {
            etName.setError("Máx 100 caracteres");
            return;
        }
        if (apellidos.length() > 100) {
            etSurname.setError("Máx 100 caracteres");
            return;
        }
        if (telefono.length() > 15) {
            etPhone.setError("Teléfono muy largo");
            return;
        } // General check
        if (descripcion.length() > 2000) {
            etDescription.setError("Máx 2000 caracteres");
            return;
        }

        VoluntarioUpdateRequest request = new VoluntarioUpdateRequest(nombre, apellidos, telefono, descripcion, true,
                null);

        apiService.updateVoluntarioAdmin(currentAdminId, targetUserId, request)
                .enqueue(new Callback<MensajeResponse>() {
                    @Override
                    public void onResponse(Call<MensajeResponse> call, Response<MensajeResponse> response) {
                        showLoading(false);
                        if (response.isSuccessful()) {
                            Toast.makeText(EditUserActivity.this, "Usuario actualizado", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            Toast.makeText(EditUserActivity.this, "Error al guardar", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<MensajeResponse> call, Throwable t) {
                        showLoading(false);
                    }
                });
    }

    private void updateOrganizacionData() {
        String nombre = etName.getText().toString().trim();
        String descripcion = etDescription.getText().toString().trim();
        String web = etWeb.getText().toString().trim();
        String direccion = etAddress.getText().toString().trim();
        String telefono = etPhone.getText().toString().trim();

        if (nombre.isEmpty()) {
            etName.setError("Requerido");
            return;
        }
        if (nombre.length() > 100) {
            etName.setError("Máx 100 caracteres");
            return;
        }
        if (descripcion.length() > 2000) {
            etDescription.setError("Máx 2000 caracteres");
            return;
        }
        if (web.length() > 255) {
            etWeb.setError("Máx 255 caracteres");
            return;
        }
        if (direccion.length() > 255) {
            etAddress.setError("Máx 255 caracteres");
            return;
        }
        if (telefono.length() > 15) {
            etPhone.setError("Teléfono muy largo");
            return;
        }

        if (descripcion.isEmpty())
            descripcion = "Sin descripción";

        OrganizacionUpdateRequest request = new OrganizacionUpdateRequest(nombre, descripcion, web, direccion,
                telefono);

        apiService.updateOrganizacionAdmin(currentAdminId, targetUserId, request)
                .enqueue(new Callback<MensajeResponse>() {
                    @Override
                    public void onResponse(Call<MensajeResponse> call, Response<MensajeResponse> response) {
                        showLoading(false);
                        if (response.isSuccessful()) {
                            Toast.makeText(EditUserActivity.this, "Organización actualizada", Toast.LENGTH_SHORT)
                                    .show();
                            finish();
                        } else {
                            Toast.makeText(EditUserActivity.this, "Error al guardar", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<MensajeResponse> call, Throwable t) {
                        showLoading(false);
                    }
                });
    }

    private void showLoading(boolean show) {
        loadingOverlay.setVisibility(show ? View.VISIBLE : View.GONE);
    }
}