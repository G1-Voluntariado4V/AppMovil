package org.cuatrovientos.voluntariado4v.Activities;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.textfield.TextInputEditText;

import org.cuatrovientos.voluntariado4v.API.ApiClient;
import org.cuatrovientos.voluntariado4v.Models.ActividadUpdateRequest;
import org.cuatrovientos.voluntariado4v.Models.ActividadResponse;
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

public class EditActividadActivity extends AppCompatActivity {

    private static final String TAG = "EditActividad";

    private TextInputEditText etTitulo, etDescripcion, etFecha, etUbicacion, etDuracion, etCupo, etImagenUrl;
    private ChipGroup chipGroupTipos, chipGroupOds;
    private MaterialButton btnSave;

    private int orgId;
    private int actividadId;
    private ActividadResponse actividadOriginal;
    private Calendar selectedDateTime = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_actividad);

        SharedPreferences prefs = getSharedPreferences("VoluntariadoPrefs", Context.MODE_PRIVATE);
        orgId = prefs.getInt("user_id", -1);

        actividadOriginal = (ActividadResponse) getIntent().getSerializableExtra("actividad");
        if (actividadOriginal == null) {
            Toast.makeText(this, "Error al cargar actividad", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        actividadId = actividadOriginal.getId();
        android.util.Log.d(TAG, "Editando actividad ID: " + actividadId);

        initViews();
        setupToolbar();
        setupDatePicker();
        loadCatalogos();
        populateData();
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

                                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",
                                            Locale.getDefault());
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

    private void populateData() {
        etTitulo.setText(actividadOriginal.getTitulo());
        etDescripcion.setText(actividadOriginal.getDescripcion());
        etUbicacion.setText(actividadOriginal.getUbicacion());
        etDuracion.setText(String.valueOf(actividadOriginal.getDuracionHoras()));
        etCupo.setText(String.valueOf(actividadOriginal.getCupoMaximo()));

        // Fecha
        String fecha = actividadOriginal.getFechaInicio();
        if (fecha != null && fecha.length() >= 19) {
            etFecha.setText(fecha.substring(0, 19));
        }

        // Imagen URL
        String imagenUrl = actividadOriginal.getImagenActividad();
        if (imagenUrl != null && !imagenUrl.isEmpty()) {
            etImagenUrl.setText(imagenUrl);
        }
    }

    private void loadCatalogos() {
        ApiClient.getService().getTiposVoluntariado().enqueue(new Callback<List<TipoVoluntariadoResponse>>() {
            @Override
            public void onResponse(Call<List<TipoVoluntariadoResponse>> call,
                    Response<List<TipoVoluntariadoResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    for (TipoVoluntariadoResponse tipo : response.body()) {
                        addChip(chipGroupTipos, tipo.getId(), tipo.getNombre(),
                                isTypeSelected(tipo.getNombre()));
                    }
                }
            }

            @Override
            public void onFailure(Call<List<TipoVoluntariadoResponse>> call, Throwable t) {
                Log.e(TAG, "Error cargando tipos", t);
            }
        });

        ApiClient.getService().getOds().enqueue(new Callback<List<OdsResponse>>() {
            @Override
            public void onResponse(Call<List<OdsResponse>> call, Response<List<OdsResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    for (OdsResponse ods : response.body()) {
                        addChip(chipGroupOds, ods.getId(), ods.getNombre(),
                                isOdsSelected(ods.getNombre()));
                    }
                }
            }

            @Override
            public void onFailure(Call<List<OdsResponse>> call, Throwable t) {
                Log.e(TAG, "Error cargando ODS", t);
            }
        });
    }

    private boolean isTypeSelected(String typeName) {
        if (actividadOriginal.getTipos() == null)
            return false;
        for (TipoVoluntariadoResponse tipo : actividadOriginal.getTipos()) {
            if (tipo.getNombre() != null && tipo.getNombre().equals(typeName)) {
                return true;
            }
        }
        return false;
    }

    private boolean isOdsSelected(String odsName) {
        if (actividadOriginal.getOds() == null)
            return false;
        for (OdsResponse ods : actividadOriginal.getOds()) {
            if (ods.getNombre() != null && ods.getNombre().equals(odsName)) {
                return true;
            }
        }
        return false;
    }

    private void addChip(ChipGroup group, int id, String text, boolean checked) {
        Chip chip = new Chip(this);
        chip.setText(text);
        chip.setTag(id);
        chip.setCheckable(true);
        chip.setChecked(checked);
        chip.setChipBackgroundColorResource(R.color.chip_background_color);
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

    private void setupSaveButton() {
        btnSave.setOnClickListener(v -> saveChanges());
    }

    private void saveChanges() {
        String titulo = etTitulo.getText().toString().trim();
        String descripcion = etDescripcion.getText().toString().trim();
        String fecha = etFecha.getText().toString().trim();
        String ubicacion = etUbicacion.getText().toString().trim();
        String duracionStr = etDuracion.getText().toString().trim();
        String cupoStr = etCupo.getText().toString().trim();

        if (titulo.isEmpty()) {
            etTitulo.setError("Requerido");
            return;
        }
        if (fecha.isEmpty()) {
            etFecha.setError("Requerido");
            return;
        }

        int duracion = Integer.parseInt(duracionStr.isEmpty() ? "0" : duracionStr);
        int cupo = Integer.parseInt(cupoStr.isEmpty() ? "0" : cupoStr);

        List<Integer> tiposIds = getSelectedIds(chipGroupTipos);
        List<Integer> odsIds = getSelectedIds(chipGroupOds);

        ActividadUpdateRequest request = new ActividadUpdateRequest(
                titulo, descripcion, fecha, duracion, cupo, ubicacion, odsIds, tiposIds);

        btnSave.setEnabled(false);
        btnSave.setText("Guardando...");

        ApiClient.getService().updateActividad(actividadId, request).enqueue(new Callback<ActividadResponse>() {
            @Override
            public void onResponse(Call<ActividadResponse> call, Response<ActividadResponse> response) {
                btnSave.setEnabled(true);
                btnSave.setText("Guardar Cambios");

                if (response.isSuccessful()) {
                    Toast.makeText(EditActividadActivity.this,
                            "Cambios guardados", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Log.e(TAG, "Error actualizando: " + response.code());
                    Toast.makeText(EditActividadActivity.this,
                            "Error: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ActividadResponse> call, Throwable t) {
                btnSave.setEnabled(true);
                btnSave.setText("Guardar Cambios");
                Log.e(TAG, "Error conexión", t);
                Toast.makeText(EditActividadActivity.this,
                        "Error de conexión", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
