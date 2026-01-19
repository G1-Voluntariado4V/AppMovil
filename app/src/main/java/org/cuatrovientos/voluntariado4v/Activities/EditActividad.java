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
import org.cuatrovientos.voluntariado4v.Models.ActividadUpdateRequest;
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

public class EditActividad extends AppCompatActivity {

    private static final String TAG = "EditActividad";

    // UI
    private TextInputEditText etTitulo, etDescripcion, etFecha, etUbicacion, etDuracion, etCupo, etImagenUrl;
    private ChipGroup chipGroupTipos, chipGroupOds;
    private MaterialButton btnSave;

    // Datos
    private int orgId;
    private int actividadId;
    private ActividadResponse actividadOriginal;
    private Calendar selectedDateTime = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Reutilizamos el layout de creación, ya que es idéntico visualmente
        setContentView(R.layout.activity_create_actividad);

        SharedPreferences prefs = getSharedPreferences("VoluntariadoPrefs", Context.MODE_PRIVATE);
        orgId = prefs.getInt("user_id", -1);

        // Recuperar objeto enviado desde el Intent
        actividadOriginal = (ActividadResponse) getIntent().getSerializableExtra("actividad");

        // Validación de seguridad
        if (actividadOriginal == null) {
            Toast.makeText(this, "Error: No se pudo cargar la actividad", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        actividadId = actividadOriginal.getId();

        initViews();
        setupToolbar();
        setupDatePicker();

        // 1. Cargar datos básicos (Texto, fechas, etc)
        populateBasicData();

        // 2. Cargar catálogos y seleccionar los Chips correspondientes
        loadCatalogos();

        setupSaveButton();
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

        // Reutilizamos el botón de crear, cambiando su texto
        btnSave = findViewById(R.id.btnCreate);
        btnSave.setText("Guardar Cambios");
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Editar Actividad");
        toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void setupDatePicker() {
        etFecha.setOnClickListener(v -> {
            DatePickerDialog dateDialog = new DatePickerDialog(this,
                    (view, year, month, dayOfMonth) -> {
                        selectedDateTime.set(Calendar.YEAR, year);
                        selectedDateTime.set(Calendar.MONTH, month);
                        selectedDateTime.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                        TimePickerDialog timeDialog = new TimePickerDialog(this,
                                (view1, hourOfDay, minute) -> {
                                    selectedDateTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
                                    selectedDateTime.set(Calendar.MINUTE, minute);
                                    selectedDateTime.set(Calendar.SECOND, 0);

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

    private void populateBasicData() {
        if (actividadOriginal.getTitulo() != null) etTitulo.setText(actividadOriginal.getTitulo());
        if (actividadOriginal.getDescripcion() != null) etDescripcion.setText(actividadOriginal.getDescripcion());
        if (actividadOriginal.getUbicacion() != null) etUbicacion.setText(actividadOriginal.getUbicacion());
        etDuracion.setText(String.valueOf(actividadOriginal.getDuracionHoras()));
        etCupo.setText(String.valueOf(actividadOriginal.getCupoMaximo()));

        // Formato de fecha y sincronización del calendario
        String fecha = actividadOriginal.getFechaInicio();
        if (fecha != null && fecha.length() >= 19) {
            etFecha.setText(fecha.substring(0, 19));
            try {
                // Parseamos la fecha original para que el picker empiece ahí
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                selectedDateTime.setTime(sdf.parse(fecha.substring(0, 19)));
            } catch (Exception e) {
                Log.e(TAG, "Error parseando fecha original para el calendario", e);
            }
        }

        // Imagen
        String imagenUrl = actividadOriginal.getImagenActividad();
        if (imagenUrl != null && !imagenUrl.isEmpty()) {
            etImagenUrl.setText(imagenUrl);
        }
    }

    private void loadCatalogos() {
        // Cargar Tipos
        ApiClient.getService().getTiposVoluntariado().enqueue(new Callback<List<TipoVoluntariadoResponse>>() {
            @Override
            public void onResponse(Call<List<TipoVoluntariadoResponse>> call, Response<List<TipoVoluntariadoResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    chipGroupTipos.removeAllViews();
                    for (TipoVoluntariadoResponse tipo : response.body()) {
                        boolean isSelected = isTypeSelected(tipo.getNombre());
                        addChip(chipGroupTipos, tipo.getId(), tipo.getNombre(), isSelected);
                    }
                }
            }
            @Override
            public void onFailure(Call<List<TipoVoluntariadoResponse>> call, Throwable t) {
                Log.e(TAG, "Error cargando tipos", t);
            }
        });

        // Cargar ODS
        ApiClient.getService().getOds().enqueue(new Callback<List<OdsResponse>>() {
            @Override
            public void onResponse(Call<List<OdsResponse>> call, Response<List<OdsResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    chipGroupOds.removeAllViews();
                    for (OdsResponse ods : response.body()) {
                        boolean isSelected = isOdsSelected(ods.getNombre());
                        addChip(chipGroupOds, ods.getId(), ods.getNombre(), isSelected);
                    }
                }
            }
            @Override
            public void onFailure(Call<List<OdsResponse>> call, Throwable t) {
                Log.e(TAG, "Error cargando ODS", t);
            }
        });
    }

    // --- Lógica Visual de los Chips (Bordes dinámicos) ---
    private void addChip(ChipGroup group, int id, String text, boolean isChecked) {
        Chip chip = new Chip(this);
        chip.setText(text);
        chip.setTag(id);
        chip.setCheckable(true);
        chip.setChecked(isChecked);

        // Fondo (Gris -> Azul Claro)
        chip.setChipBackgroundColorResource(R.color.bg_chip_state_list);
        // Texto (Gris Oscuro -> Azul Fuerte)
        chip.setTextColor(getResources().getColorStateList(R.color.text_chip_state_list, getTheme()));

        // Borde (Transparente -> Azul Primario)
        float strokeWidth = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 1.5f, getResources().getDisplayMetrics());
        chip.setChipStrokeWidth(strokeWidth);

        int colorPrimary = ContextCompat.getColor(this, R.color.primary);
        int colorTransparent = ContextCompat.getColor(this, android.R.color.transparent);

        int[][] states = new int[][] {
                new int[] { android.R.attr.state_checked }, // Estado Seleccionado
                new int[] { }                               // Estado Defecto
        };
        int[] colors = new int[] { colorPrimary, colorTransparent };

        chip.setChipStrokeColor(new ColorStateList(states, colors));

        group.addView(chip);
    }

    // Helpers para verificar si estaba seleccionado previamente
    private boolean isTypeSelected(String typeName) {
        if (actividadOriginal.getTipos() == null) return false;
        for (TipoVoluntariadoResponse tipo : actividadOriginal.getTipos()) {
            if (tipo.getNombre() != null && tipo.getNombre().equals(typeName)) {
                return true;
            }
        }
        return false;
    }

    private boolean isOdsSelected(String odsName) {
        if (actividadOriginal.getOds() == null) return false;
        for (OdsResponse ods : actividadOriginal.getOds()) {
            if (ods.getNombre() != null && ods.getNombre().equals(odsName)) {
                return true;
            }
        }
        return false;
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

    private void setupSaveButton() {
        btnSave.setOnClickListener(v -> saveChanges());
    }

    // ═══════════════════════════════════════════════════════════════════
    // LÓGICA DE ACTUALIZACIÓN EN API
    // ═══════════════════════════════════════════════════════════════════
    private void saveChanges() {
        // 1. Recoger datos
        String titulo = etTitulo.getText().toString().trim();
        String descripcion = etDescripcion.getText().toString().trim();
        String fecha = etFecha.getText().toString().trim();
        String ubicacion = etUbicacion.getText().toString().trim();
        String duracionStr = etDuracion.getText().toString().trim();
        String cupoStr = etCupo.getText().toString().trim();
        String nuevaImagenUrl = etImagenUrl.getText().toString().trim();

        // 2. Validaciones básicas
        if (titulo.isEmpty()) { etTitulo.setError("Requerido"); return; }
        if (fecha.isEmpty()) { etFecha.setError("Requerido"); return; }
        if (ubicacion.isEmpty()) { etUbicacion.setError("Requerido"); return; }

        int duracion = Integer.parseInt(duracionStr.isEmpty() ? "0" : duracionStr);
        int cupo = Integer.parseInt(cupoStr.isEmpty() ? "0" : cupoStr);

        List<Integer> tiposIds = getSelectedIds(chipGroupTipos);
        List<Integer> odsIds = getSelectedIds(chipGroupOds);

        // 3. Crear Request de Actualización (Modelo: ActividadUpdateRequest)
        ActividadUpdateRequest request = new ActividadUpdateRequest(
                titulo, descripcion, fecha, duracion, cupo, ubicacion, odsIds, tiposIds);

        btnSave.setEnabled(false);
        btnSave.setText("Guardando...");

        // 4. Llamada API (Endpoint PUT actividades/{id})
        ApiClient.getService().updateActividad(actividadId, request).enqueue(new Callback<ActividadResponse>() {
            @Override
            public void onResponse(Call<ActividadResponse> call, Response<ActividadResponse> response) {
                if (response.isSuccessful()) {

                    // 5. Verificar si hay que actualizar la imagen
                    String imagenOriginal = actividadOriginal.getImagenActividad();
                    // Si hay URL nueva y es distinta a la original, hacemos el POST de la imagen
                    if (!nuevaImagenUrl.isEmpty() && !nuevaImagenUrl.equals(imagenOriginal)) {
                        updateImage(actividadId, nuevaImagenUrl);
                    } else {
                        finishSuccess();
                    }

                } else {
                    btnSave.setEnabled(true);
                    btnSave.setText("Guardar Cambios");
                    Log.e(TAG, "Error actualizando: " + response.code());
                    Toast.makeText(EditActividad.this, "Error al guardar: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ActividadResponse> call, Throwable t) {
                btnSave.setEnabled(true);
                btnSave.setText("Guardar Cambios");
                Log.e(TAG, "Error conexión", t);
                Toast.makeText(EditActividad.this, "Error de conexión", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateImage(int actividadId, String imageUrl) {
        ImagenRequest imgRequest = new ImagenRequest(imageUrl);
        // Endpoint POST actividades/{id}/imagenes
        ApiClient.getService().addImagenActividad(actividadId, imgRequest).enqueue(new Callback<MensajeResponse>() {
            @Override
            public void onResponse(Call<MensajeResponse> call, Response<MensajeResponse> response) {
                // Terminamos aunque falle la imagen, los datos principales ya se guardaron
                finishSuccess();
            }

            @Override
            public void onFailure(Call<MensajeResponse> call, Throwable t) {
                Toast.makeText(EditActividad.this, "Datos guardados, pero falló la imagen", Toast.LENGTH_SHORT).show();
                finishSuccess();
            }
        });
    }

    private void finishSuccess() {
        Toast.makeText(EditActividad.this, "Actividad actualizada", Toast.LENGTH_SHORT).show();
        finish();
    }
}