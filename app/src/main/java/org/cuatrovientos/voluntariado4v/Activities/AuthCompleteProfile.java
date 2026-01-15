package org.cuatrovientos.voluntariado4v.Activities;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.materialswitch.MaterialSwitch;
import com.google.android.material.textfield.TextInputEditText;

import org.cuatrovientos.voluntariado4v.Models.CursoResponse;
import org.cuatrovientos.voluntariado4v.Models.RegisterRequest;
import org.cuatrovientos.voluntariado4v.Models.VoluntarioResponse;
import org.cuatrovientos.voluntariado4v.R;
import org.cuatrovientos.voluntariado4v.API.ApiClient;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AuthCompleteProfile extends AppCompatActivity {

    private static final String TAG = "AuthCompleteProfile";

    private TextInputEditText etNombre, etApellidos, etDni, etTelefono, etFechaNacimiento;
    private AutoCompleteTextView actvNivel, actvCiclo, actvIdiomas;
    private MaterialSwitch switchCoche;
    private MaterialButton btnFinalizar;

    private String googleId, email;
    private List<CursoResponse> cursosList = new ArrayList<>();
    private List<org.cuatrovientos.voluntariado4v.Models.IdiomaResponse> idiomasList = new ArrayList<>();

    // Selección del usuario
    private int selectedNivel = -1;
    private String selectedCiclo = null;
    private int selectedIdiomaId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_auth_complete_profile);

        googleId = getIntent().getStringExtra("google_id");
        email = getIntent().getStringExtra("email");

        if (googleId == null || email == null) {
            googleId = "test_google_id_" + System.currentTimeMillis();
            email = "test_" + System.currentTimeMillis() + "@gmail.com";
        }

        initViews();
        setupDatePicker();
        loadCursos();
        loadIdiomas();
        
        btnFinalizar.setOnClickListener(v -> attemptRegister());
    }

    private void initViews() {
        etNombre = findViewById(R.id.etNombre);
        etApellidos = findViewById(R.id.etApellidos);
        etDni = findViewById(R.id.etDni);
        etTelefono = findViewById(R.id.etTelefono);
        etFechaNacimiento = findViewById(R.id.etFechaNacimiento);
        
        actvNivel = findViewById(R.id.actvNivel);
        actvCiclo = findViewById(R.id.actvCiclo);
        actvIdiomas = findViewById(R.id.actvIdiomas);
        
        switchCoche = findViewById(R.id.switchCoche);
        btnFinalizar = findViewById(R.id.btnFinalizar);
    }

    private void setupDatePicker() {
        etFechaNacimiento.setOnClickListener(v -> {
            final Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    AuthCompleteProfile.this,
                    (view, selectedYear, selectedMonth, selectedDay) -> {
                        // Mostrar DD/MM/YYYY visualmente
                        String selectedDate = String.format("%02d/%02d/%04d", selectedDay, (selectedMonth + 1), selectedYear);
                        etFechaNacimiento.setText(selectedDate);
                    },
                    year, month, day
            );
            datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
            datePickerDialog.show();
        });
    }

    private void loadCursos() {
        ApiClient.getService().getCursos().enqueue(new Callback<List<CursoResponse>>() {
            @Override
            public void onResponse(Call<List<CursoResponse>> call, Response<List<CursoResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    cursosList = response.body();
                    setupDropdowns();
                } else {
                    Log.e(TAG, "Error cargando cursos: " + response.code());
                    Toast.makeText(AuthCompleteProfile.this, "Error cargando cursos", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<List<CursoResponse>> call, Throwable t) {
                Log.e(TAG, "Error conexión cursos", t);
            }
        });
    }

    private void loadIdiomas() {
        ApiClient.getService().getIdiomas().enqueue(new Callback<List<org.cuatrovientos.voluntariado4v.Models.IdiomaResponse>>() {
            @Override
            public void onResponse(Call<List<org.cuatrovientos.voluntariado4v.Models.IdiomaResponse>> call, Response<List<org.cuatrovientos.voluntariado4v.Models.IdiomaResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    idiomasList = response.body();
                    setupIdiomasDropdown();
                }
            }
            @Override
            public void onFailure(Call<List<org.cuatrovientos.voluntariado4v.Models.IdiomaResponse>> call, Throwable t) {
                Log.e(TAG, "Error conexión idiomas", t);
            }
        });
    }

    private void setupDropdowns() {
        Set<Integer> nivelesSet = new HashSet<>();
        for (CursoResponse c : cursosList) {
            nivelesSet.add(c.getNivel());
        }
        List<String> nivelesStr = new ArrayList<>();
        for (Integer n : nivelesSet) nivelesStr.add(String.valueOf(n));

        Set<String> ciclosSet = new HashSet<>();
        for (CursoResponse c : cursosList) {
            String raw = c.getAbreviacion();
            if (raw != null && !raw.isEmpty()) {
                if (Character.isDigit(raw.charAt(0))) ciclosSet.add(raw.substring(1));
                else ciclosSet.add(raw);
            } else if (c.getNombre() != null) {
                ciclosSet.add(c.getNombre());
            }
        }
        List<String> ciclosStr = new ArrayList<>(ciclosSet);

        ArrayAdapter<String> adapterNivel = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, nivelesStr);
        actvNivel.setAdapter(adapterNivel);

        ArrayAdapter<String> adapterCiclo = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, ciclosStr);
        actvCiclo.setAdapter(adapterCiclo);

        actvNivel.setOnItemClickListener((parent, view, position, id) -> {
            try { selectedNivel = Integer.parseInt(adapterNivel.getItem(position)); } 
            catch (Exception e) { selectedNivel = 1; }
        });

        actvCiclo.setOnItemClickListener((parent, view, position, id) -> {
            selectedCiclo = adapterCiclo.getItem(position);
        });
    }

    private void setupIdiomasDropdown() {
        List<String> nombres = new ArrayList<>();
        if (idiomasList != null) {
            for (org.cuatrovientos.voluntariado4v.Models.IdiomaResponse i : idiomasList) {
                if (i != null && i.getNombre() != null) {
                    nombres.add(i.getNombre());
                }
            }
        }
        
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, nombres);
        actvIdiomas.setAdapter(adapter);

        actvIdiomas.setOnItemClickListener((parent, view, position, id) -> {
            String nombre = adapter.getItem(position);
            for (org.cuatrovientos.voluntariado4v.Models.IdiomaResponse i : idiomasList) {
                if (i.getNombre().equals(nombre)) {
                    selectedIdiomaId = i.getId();
                    break;
                }
            }
        });
    }

    private int resolveCursoId() {
        if (cursosList.isEmpty()) return 1;
        
        String abrevEsperada = selectedNivel + selectedCiclo;
        for (CursoResponse c : cursosList) {
            String abrevReal = c.getAbreviacion();
            if (abrevReal != null && abrevReal.equalsIgnoreCase(abrevEsperada)) return c.getId();
            
            // Fallback match parcial
            if (c.getNivel() == selectedNivel 
                && selectedCiclo != null 
                && abrevReal != null 
                && abrevReal.toUpperCase().contains(selectedCiclo.toUpperCase())) {
                return c.getId();
            }
        }
        return cursosList.get(0).getId();
    }
    
    // Validaciones
    private boolean isValidDNI(String dni) {
        return dni.matches("^[0-9]{8}[TRWAGMYFPDXBNJZSQVHLCKE]$");
    }

    private boolean isValidPhone(String phone) {
        return phone.matches("^[6789][0-9]{8}$");
    }

    private String formatDateForApi(String displayDate) {
        try {
            // Entrada: DD/MM/YYYY -> Salida: YYYY-MM-DD
            String[] parts = displayDate.split("/");
            if (parts.length == 3) {
                return parts[2] + "-" + String.format("%02d", Integer.parseInt(parts[1])) + "-"  + String.format("%02d", Integer.parseInt(parts[0]));
            }
        } catch (Exception e) {}
        return displayDate;
    }

    private void attemptRegister() {
        String nombre = etNombre.getText().toString().trim();
        String apellidos = etApellidos.getText().toString().trim();
        String dni = etDni.getText().toString().trim().toUpperCase();
        String telefono = etTelefono.getText().toString().trim();
        String fechaDisplay = etFechaNacimiento.getText().toString().trim();
        
        boolean tieneCoche = switchCoche.isChecked();

        if (nombre.isEmpty() || apellidos.isEmpty()) { Toast.makeText(this, "Completa nombre y apellidos", Toast.LENGTH_SHORT).show(); etNombre.setError("X"); return; }
        if (!isValidDNI(dni)) { Toast.makeText(this, "DNI inválido", Toast.LENGTH_SHORT).show(); etDni.setError("X"); return; }
        if (!isValidPhone(telefono)) { Toast.makeText(this, "Teléfono inválido", Toast.LENGTH_SHORT).show(); etTelefono.setError("X"); return; }
        if (fechaDisplay.isEmpty()) { Toast.makeText(this, "Pon fecha de nacimiento", Toast.LENGTH_SHORT).show(); return; }
        
        int finalCursoId = resolveCursoId();
        if (finalCursoId == -1) { Toast.makeText(this, "Curso inválido", Toast.LENGTH_SHORT).show(); return; }

        String fechaApi = formatDateForApi(fechaDisplay);

        RegisterRequest request = new RegisterRequest(
                googleId, email, nombre, apellidos, 
                dni, telefono, fechaApi,
                tieneCoche, finalCursoId);

        ApiClient.getService().register(request).enqueue(new Callback<VoluntarioResponse>() {
            @Override
            public void onResponse(Call<VoluntarioResponse> call, Response<VoluntarioResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    saveSession(response.body());
                    goToDashboard();
                } else if (response.code() == 409) {
                     Log.w(TAG, "Duplicado 409.");
                     Toast.makeText(AuthCompleteProfile.this, "Usuario ya registrado. Inicia sesión.", Toast.LENGTH_LONG).show();
                     // No guardamos sesión falsa.
                     finish(); // Volver al login para que entre correctamente
                     
                } else {
                    Toast.makeText(AuthCompleteProfile.this, "Error API: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<VoluntarioResponse> call, Throwable t) {
                Toast.makeText(AuthCompleteProfile.this, "Error conexión", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveSession(VoluntarioResponse voluntario) {
        SharedPreferences prefs = getSharedPreferences("VoluntariadoPrefs", MODE_PRIVATE);
        prefs.edit()
                .putInt("user_id", voluntario.getId())
                .putString("google_id", googleId)
                .putString("email", email)
                .putString("nombre", voluntario.getNombre() + " " + voluntario.getApellidos())
                .putString("rol", "Voluntario")
                .putBoolean("is_logged_in", true)
                .apply();
    }

    private void goToDashboard() {
        Intent intent = new Intent(this, UserDashboard.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}