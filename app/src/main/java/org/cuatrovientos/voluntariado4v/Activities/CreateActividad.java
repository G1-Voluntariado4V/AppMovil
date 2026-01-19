package org.cuatrovientos.voluntariado4v.Activities;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.textfield.TextInputEditText;

import org.cuatrovientos.voluntariado4v.API.ApiClient;
import org.cuatrovientos.voluntariado4v.Models.ActividadCreateRequest;
import org.cuatrovientos.voluntariado4v.Models.ActividadResponse;
import org.cuatrovientos.voluntariado4v.Models.ImagenRequest;
import org.cuatrovientos.voluntariado4v.Models.MensajeResponse;
import org.cuatrovientos.voluntariado4v.Models.OdsResponse;
import org.cuatrovientos.voluntariado4v.Models.TipoVoluntariadoResponse;
import org.cuatrovientos.voluntariado4v.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CreateActividad extends AppCompatActivity {

    private static final String TAG = "CreateActividad";

    // Vistas
    private TextInputEditText etTitulo, etDescripcion, etFecha, etUbicacion, etDuracion, etCupo, etImagenUrl;
    private ChipGroup chipGroupTipos, chipGroupOds;
    private MaterialButton btnCreate;

    // Variables de lógica
    private int orgId;
    private Calendar selectedDateTime = Calendar.getInstance();
    private List<TipoVoluntariadoResponse> tiposList = new ArrayList<>();
    private List<OdsResponse> odsList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_actividad);

        // 1. Recuperar ID de la organización de las preferencias
        SharedPreferences prefs = getSharedPreferences("VoluntariadoPrefs", Context.MODE_PRIVATE);
        orgId = prefs.getInt("user_id", -1);

        // Validación de seguridad: Si no hay ID de organización, no podemos seguir
        if (orgId == -1) {
            Toast.makeText(this, "Error de sesión: Organización no identificada", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        initViews();
        setupToolbar();
        setupDatePicker();
        loadCatalogos();
        setupCreateButton();
    }

    private void initViews() {
        etTitulo = findViewById(R.id.etTitulo);
        etDescripcion = findViewById(R.id.etDescripcion);
        etFecha = findViewById(R.id.etFecha);
        etUbicacion = findViewById(R.id.etUbicacion);
        etDuracion = findViewById(R.id.etDuracion);
        etCupo = findViewById(R.id.etCupo);
        etImagenUrl = findViewById(R.id.etImagenUrl);
        chipGroupTipos = findViewById(R.id.chipGroupTipos);
        chipGroupOds = findViewById(R.id.chipGroupOds);
        btnCreate = findViewById(R.id.btnCreate);
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void setupDatePicker() {
        etFecha.setOnClickListener(v -> {
            // 1. Picker de Fecha
            DatePickerDialog dateDialog = new DatePickerDialog(this,
                    (view, year, month, dayOfMonth) -> {
                        selectedDateTime.set(Calendar.YEAR, year);
                        selectedDateTime.set(Calendar.MONTH, month);
                        selectedDateTime.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                        // 2. Picker de Hora (al cerrar la fecha)
                        TimePickerDialog timeDialog = new TimePickerDialog(this,
                                (view1, hourOfDay, minute) -> {
                                    selectedDateTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
                                    selectedDateTime.set(Calendar.MINUTE, minute);
                                    selectedDateTime.set(Calendar.SECOND, 0);

                                    // Formato compatible con Backend (SQL Standard)
                                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                                    etFecha.setText(sdf.format(selectedDateTime.getTime()));
                                },
                                selectedDateTime.get(Calendar.HOUR_OF_DAY),
                                selectedDateTime.get(Calendar.MINUTE),
                                true);
                        timeDialog.show();
                    },
                    selectedDateTime.get(Calendar.YEAR),
                    selectedDateTime.get(Calendar.MONTH),
                    selectedDateTime.get(Calendar.DAY_OF_MONTH));
            dateDialog.show();
        });
    }

    private void loadCatalogos() {
        // Cargar Tipos de Voluntariado
        ApiClient.getService().getTiposVoluntariado().enqueue(new Callback<List<TipoVoluntariadoResponse>>() {
            @Override
            public void onResponse(Call<List<TipoVoluntariadoResponse>> call, Response<List<TipoVoluntariadoResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    tiposList = response.body();
                    chipGroupTipos.removeAllViews(); // Limpiar por si acaso
                    for (TipoVoluntariadoResponse tipo : tiposList) {
                        addChip(chipGroupTipos, tipo.getId(), tipo.getNombre());
                    }
                }
            }
            @Override
            public void onFailure(Call<List<TipoVoluntariadoResponse>> call, Throwable t) {
                Log.e(TAG, "Error cargando tipos", t);
                Toast.makeText(CreateActividad.this, "Error al cargar tipos", Toast.LENGTH_SHORT).show();
            }
        });

        // Cargar ODS
        ApiClient.getService().getOds().enqueue(new Callback<List<OdsResponse>>() {
            @Override
            public void onResponse(Call<List<OdsResponse>> call, Response<List<OdsResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    odsList = response.body();
                    chipGroupOds.removeAllViews();
                    for (OdsResponse ods : odsList) {
                        addChip(chipGroupOds, ods.getId(), ods.getNombre());
                    }
                }
            }
            @Override
            public void onFailure(Call<List<OdsResponse>> call, Throwable t) {
                Log.e(TAG, "Error cargando ODS", t);
            }
        });
    }

    /**
     * Añade un Chip con el estilo personalizado:
     * - Fondo cambia de color.
     * - Borde aparece solo al seleccionar.
     */
    private void addChip(ChipGroup group, int id, String text) {
        Chip chip = new Chip(this);
        chip.setText(text);
        chip.setTag(id);
        chip.setCheckable(true);

        // Estilo visual
        chip.setChipBackgroundColorResource(R.color.bg_chip_state_list);
        chip.setTextColor(getResources().getColorStateList(R.color.text_chip_state_list, getTheme()));

        // Configuración del Borde (Stroke)
        float strokeWidth = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1.5f, getResources().getDisplayMetrics());
        chip.setChipStrokeWidth(strokeWidth);

        int colorPrimary = ContextCompat.getColor(this, R.color.primary);
        int colorTransparent = ContextCompat.getColor(this, android.R.color.transparent);

        int[][] states = new int[][] {
                new int[] { android.R.attr.state_checked }, // Seleccionado
                new int[] { }                               // Defecto
        };
        int[] colors = new int[] {
                colorPrimary,     // Borde Azul
                colorTransparent  // Borde Invisible
        };
        chip.setChipStrokeColor(new ColorStateList(states, colors));

        group.addView(chip);
    }

    private List<Integer> getSelectedIds(ChipGroup group) {
        List<Integer> ids = new ArrayList<>();
        for (int i = 0; i < group.getChildCount(); i++) {
            Chip chip = (Chip) group.getChildAt(i);
            if (chip.isChecked()) {
                ids.add((Integer) chip.getTag());
            }
        }
        return ids;
    }

    private void setupCreateButton() {
        btnCreate.setOnClickListener(v -> createActividad());
    }

    // ═══════════════════════════════════════════════════════════════════
    // LÓGICA DE INSERCIÓN EN API
    // ═══════════════════════════════════════════════════════════════════
    private void createActividad() {
        // 1. Obtener datos
        String titulo = etTitulo.getText().toString().trim();
        String descripcion = etDescripcion.getText().toString().trim();
        String fecha = etFecha.getText().toString().trim();
        String ubicacion = etUbicacion.getText().toString().trim();
        String duracionStr = etDuracion.getText().toString().trim();
        String cupoStr = etCupo.getText().toString().trim();
        String imagenUrl = etImagenUrl.getText().toString().trim();

        // 2. Validaciones
        if (titulo.isEmpty()) { etTitulo.setError("El título es obligatorio"); return; }
        if (fecha.isEmpty()) { etFecha.setError("La fecha es obligatoria"); return; }
        if (ubicacion.isEmpty()) { etUbicacion.setError("La ubicación es obligatoria"); return; }
        if (duracionStr.isEmpty()) { etDuracion.setError("Requerido"); return; }
        if (cupoStr.isEmpty()) { etCupo.setError("Requerido"); return; }

        int duracion = Integer.parseInt(duracionStr);
        int cupo = Integer.parseInt(cupoStr);

        // 3. Obtener listas de IDs seleccionados
        List<Integer> tiposIds = getSelectedIds(chipGroupTipos);
        List<Integer> odsIds = getSelectedIds(chipGroupOds);

        // Validar que al menos haya un tipo u ODS si es necesario (opcional)
        // if (tiposIds.isEmpty()) { Toast.makeText(this, "Selecciona un tipo", Toast.LENGTH_SHORT).show(); return; }

        // 4. Crear Objeto Request
        ActividadCreateRequest request = new ActividadCreateRequest(
                titulo, descripcion, fecha, duracion, cupo, ubicacion, orgId, odsIds, tiposIds);

        // Feedback visual
        btnCreate.setEnabled(false);
        btnCreate.setText("Publicando...");

        final String finalImagenUrl = imagenUrl;

        // 5. Llamada a Retrofit
        ApiClient.getService().crearActividad(orgId, request).enqueue(new Callback<ActividadResponse>() {
            @Override
            public void onResponse(Call<ActividadResponse> call, Response<ActividadResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.d(TAG, "Actividad creada con ID: " + response.body().getIdActividad());

                    int actividadId = response.body().getIdActividad();

                    // 6. Si hay imagen, la subimos en una segunda llamada
                    if (!finalImagenUrl.isEmpty()) {
                        uploadImage(actividadId, finalImagenUrl);
                    } else {
                        finishSuccess();
                    }
                } else {
                    handleError(response.code());
                }
            }

            @Override
            public void onFailure(Call<ActividadResponse> call, Throwable t) {
                Log.e(TAG, "Fallo de conexión", t);
                btnCreate.setEnabled(true);
                btnCreate.setText("Intentar de nuevo");
                Toast.makeText(CreateActividad.this, "Error de conexión: Comprueba tu internet", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void handleError(int statusCode) {
        btnCreate.setEnabled(true);
        btnCreate.setText("Crear Actividad");
        String msg = "Error al crear la actividad (" + statusCode + ")";
        if (statusCode == 404) msg = "Organización no encontrada";
        if (statusCode == 400) msg = "Datos inválidos, revisa el formulario";

        Toast.makeText(CreateActividad.this, msg, Toast.LENGTH_LONG).show();
    }

    private void uploadImage(int actividadId, String imageUrl) {
        ImagenRequest imgRequest = new ImagenRequest(imageUrl);

        ApiClient.getService().addImagenActividad(actividadId, imgRequest).enqueue(new Callback<MensajeResponse>() {
            @Override
            public void onResponse(Call<MensajeResponse> call, Response<MensajeResponse> response) {
                // Independientemente de si la imagen falla o no, la actividad ya se creó
                if (!response.isSuccessful()) {
                    Toast.makeText(CreateActividad.this, "Actividad creada, pero error al guardar imagen", Toast.LENGTH_SHORT).show();
                }
                finishSuccess();
            }

            @Override
            public void onFailure(Call<MensajeResponse> call, Throwable t) {
                Toast.makeText(CreateActividad.this, "Actividad creada, error conexión imagen", Toast.LENGTH_SHORT).show();
                finishSuccess();
            }
        });
    }

    private void finishSuccess() {
        Toast.makeText(CreateActividad.this, "¡Actividad publicada con éxito!", Toast.LENGTH_SHORT).show();
        finish(); // Cierra la actividad y vuelve al Dashboard
    }
}