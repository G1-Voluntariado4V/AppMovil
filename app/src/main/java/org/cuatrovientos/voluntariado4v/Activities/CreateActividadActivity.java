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

public class CreateActividadActivity extends AppCompatActivity {

    private static final String TAG = "CreateActividad";

    private TextInputEditText etTitulo, etDescripcion, etFecha, etUbicacion, etDuracion, etCupo, etImagenUrl;
    private ChipGroup chipGroupTipos, chipGroupOds;
    private MaterialButton btnCreate;

    private int orgId;
    private Calendar selectedDateTime = Calendar.getInstance();

    private List<TipoVoluntariadoResponse> tiposList = new ArrayList<>();
    private List<OdsResponse> odsList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_actividad);

        // Obtener ID de organización
        SharedPreferences prefs = getSharedPreferences("VoluntariadoPrefs", Context.MODE_PRIVATE);
        orgId = prefs.getInt("user_id", -1);

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
            DatePickerDialog dateDialog = new DatePickerDialog(this,
                    (view, year, month, dayOfMonth) -> {
                        selectedDateTime.set(Calendar.YEAR, year);
                        selectedDateTime.set(Calendar.MONTH, month);
                        selectedDateTime.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                        // Elegir hora después
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

    private void loadCatalogos() {
        // Cargar Tipos de Voluntariado
        ApiClient.getService().getTiposVoluntariado().enqueue(new Callback<List<TipoVoluntariadoResponse>>() {
            @Override
            public void onResponse(Call<List<TipoVoluntariadoResponse>> call,
                    Response<List<TipoVoluntariadoResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    tiposList = response.body();
                    for (TipoVoluntariadoResponse tipo : tiposList) {
                        addChip(chipGroupTipos, tipo.getId(), tipo.getNombre());
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
                    odsList = response.body();
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

    private void addChip(ChipGroup group, int id, String text) {
        Chip chip = new Chip(this);
        chip.setText(text);
        chip.setTag(id);
        chip.setCheckable(true);
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

    private void setupCreateButton() {
        btnCreate.setOnClickListener(v -> createActividad());
    }

    private void createActividad() {
        // Validaciones
        String titulo = etTitulo.getText().toString().trim();
        String descripcion = etDescripcion.getText().toString().trim();
        String fecha = etFecha.getText().toString().trim();
        String ubicacion = etUbicacion.getText().toString().trim();
        String duracionStr = etDuracion.getText().toString().trim();
        String cupoStr = etCupo.getText().toString().trim();
        String imagenUrl = etImagenUrl.getText().toString().trim();

        if (titulo.isEmpty()) {
            etTitulo.setError("Requerido");
            return;
        }
        if (fecha.isEmpty()) {
            etFecha.setError("Requerido");
            return;
        }
        if (ubicacion.isEmpty()) {
            etUbicacion.setError("Requerido");
            return;
        }
        if (duracionStr.isEmpty()) {
            etDuracion.setError("Requerido");
            return;
        }
        if (cupoStr.isEmpty()) {
            etCupo.setError("Requerido");
            return;
        }

        int duracion = Integer.parseInt(duracionStr);
        int cupo = Integer.parseInt(cupoStr);

        List<Integer> tiposIds = getSelectedIds(chipGroupTipos);
        List<Integer> odsIds = getSelectedIds(chipGroupOds);

        ActividadCreateRequest request = new ActividadCreateRequest(
                titulo, descripcion, fecha, duracion, cupo, ubicacion, orgId, odsIds, tiposIds);

        btnCreate.setEnabled(false);
        btnCreate.setText("Creando...");

        final String finalImagenUrl = imagenUrl;

        ApiClient.getService().crearActividad(orgId, request).enqueue(new Callback<ActividadResponse>() {
            @Override
            public void onResponse(Call<ActividadResponse> call, Response<ActividadResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    int actividadId = response.body().getIdActividad();

                    // Si hay URL de imagen, subirla
                    if (!finalImagenUrl.isEmpty()) {
                        uploadImage(actividadId, finalImagenUrl);
                    } else {
                        finishSuccess();
                    }
                } else {
                    btnCreate.setEnabled(true);
                    btnCreate.setText("Crear Actividad");
                    Log.e(TAG, "Error creando: " + response.code());
                    Toast.makeText(CreateActividadActivity.this,
                            "Error: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ActividadResponse> call, Throwable t) {
                btnCreate.setEnabled(true);
                btnCreate.setText("Crear Actividad");
                Log.e(TAG, "Error conexión", t);
                Toast.makeText(CreateActividadActivity.this,
                        "Error de conexión", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void uploadImage(int actividadId, String imageUrl) {
        ImagenRequest imgRequest = new ImagenRequest(imageUrl);

        ApiClient.getService().addImagenActividad(actividadId, imgRequest).enqueue(new Callback<MensajeResponse>() {
            @Override
            public void onResponse(Call<MensajeResponse> call, Response<MensajeResponse> response) {
                if (response.isSuccessful()) {
                    Log.d(TAG, "Imagen añadida correctamente");
                } else {
                    Log.w(TAG, "Actividad creada pero error al añadir imagen: " + response.code());
                }
                finishSuccess();
            }

            @Override
            public void onFailure(Call<MensajeResponse> call, Throwable t) {
                Log.w(TAG, "Actividad creada pero error de conexión al añadir imagen", t);
                finishSuccess();
            }
        });
    }

    private void finishSuccess() {
        btnCreate.setEnabled(true);
        btnCreate.setText("Crear Actividad");
        Toast.makeText(CreateActividadActivity.this,
                "Actividad creada (en revisión)", Toast.LENGTH_SHORT).show();
        finish();
    }
}
