package org.cuatrovientos.voluntariado4v.Activities;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.textfield.TextInputEditText;

import org.cuatrovientos.voluntariado4v.API.ApiClient;
import org.cuatrovientos.voluntariado4v.Models.ActividadCreateRequest;
import org.cuatrovientos.voluntariado4v.Models.ActividadResponse;
import org.cuatrovientos.voluntariado4v.Models.MensajeResponse;
import org.cuatrovientos.voluntariado4v.Models.OdsResponse;
import org.cuatrovientos.voluntariado4v.Models.TipoVoluntariadoResponse;
import org.cuatrovientos.voluntariado4v.R;

import java.io.InputStream;
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
    private TextInputEditText etTitulo, etDescripcion, etFecha, etUbicacion, etDuracion, etCupo;
    private ChipGroup chipGroupTipos, chipGroupOds;
    private MaterialButton btnCreate, btnSelectImage;
    private MaterialCardView cardImagePreview;
    private ImageView ivPreview;
    private LinearLayout layoutPlaceholder;

    // Variables de lógica
    private int orgId;
    private Calendar selectedDateTime = Calendar.getInstance();
    private List<TipoVoluntariadoResponse> tiposList = new ArrayList<>();
    private List<OdsResponse> odsList = new ArrayList<>();

    // Imagen as File (Multipart)
    private java.io.File selectedImageFile = null;
    private ActivityResultLauncher<String> imagePickerLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_actividad);

        SharedPreferences prefs = getSharedPreferences("VoluntariadoPrefs", Context.MODE_PRIVATE);
        orgId = prefs.getInt("user_id", -1);

        if (orgId == -1) {
            Toast.makeText(this, "Error de sesión", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        initViews();
        setupToolbar();
        setupDatePicker();
        setupImagePicker();
        setupCreateButton();
        loadCatalogos();
    }

    private void initViews() {
        etTitulo = findViewById(R.id.etTitulo);
        etDescripcion = findViewById(R.id.etDescripcion);
        etFecha = findViewById(R.id.etFecha);
        etUbicacion = findViewById(R.id.etUbicacion);
        etDuracion = findViewById(R.id.etDuracion);
        etCupo = findViewById(R.id.etCupo);

        chipGroupTipos = findViewById(R.id.chipGroupTipos);
        chipGroupOds = findViewById(R.id.chipGroupOds);

        btnCreate = findViewById(R.id.btnCreate);
        btnSelectImage = findViewById(R.id.btnSelectImage);

        cardImagePreview = findViewById(R.id.cardImagePreview);
        ivPreview = findViewById(R.id.ivPreview);
        layoutPlaceholder = findViewById(R.id.layoutPlaceholder);
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

    private void setupImagePicker() {
        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                uri -> {
                    if (uri != null) {
                        processImageUri(uri);
                    }
                });

        View.OnClickListener listener = v -> imagePickerLauncher.launch("image/*");
        cardImagePreview.setOnClickListener(listener);
        btnSelectImage.setOnClickListener(listener);
    }

    private void processImageUri(Uri uri) {
        try {
            InputStream inputStream = getContentResolver().openInputStream(uri);
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            Bitmap resized = resizeBitmap(bitmap, 800);

            ivPreview.setImageBitmap(resized);
            layoutPlaceholder.setVisibility(View.GONE);
            btnSelectImage.setVisibility(View.VISIBLE);

            // Guardar en fichero para Multipart
            selectedImageFile = saveBitmapToFile(resized);

        } catch (Exception e) {
            Log.e(TAG, "Error procesando imagen", e);
            Toast.makeText(this, "Error al cargar la imagen", Toast.LENGTH_SHORT).show();
        }
    }

    private java.io.File saveBitmapToFile(Bitmap bitmap) {
        try {
            java.io.File cacheDir = getCacheDir();
            java.io.File f = new java.io.File(cacheDir, "upload_" + System.currentTimeMillis() + ".jpg");

            java.io.FileOutputStream fos = new java.io.FileOutputStream(f);
            // Comprimir a JPEG 85%
            bitmap.compress(Bitmap.CompressFormat.JPEG, 85, fos);
            fos.flush();
            fos.close();

            return f;
        } catch (Exception e) {
            Log.e(TAG, "Error saving bitmap", e);
            return null;
        }
    }

    private Bitmap resizeBitmap(Bitmap image, int maxSize) {
        int width = image.getWidth();
        int height = image.getHeight();
        float ratio = (float) width / (float) height;
        if (ratio > 1) {
            width = maxSize;
            height = (int) (width / ratio);
        } else {
            height = maxSize;
            width = (int) (height * ratio);
        }
        return Bitmap.createScaledBitmap(image, width, height, true);
    }

    private void loadCatalogos() {
        ApiClient.getService().getTiposVoluntariado().enqueue(new Callback<List<TipoVoluntariadoResponse>>() {
            @Override
            public void onResponse(Call<List<TipoVoluntariadoResponse>> call,
                    Response<List<TipoVoluntariadoResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    tiposList = response.body();
                    chipGroupTipos.removeAllViews();
                    for (TipoVoluntariadoResponse tipo : tiposList) {
                        addChip(chipGroupTipos, tipo.getId(), tipo.getNombre());
                    }
                }
            }

            @Override
            public void onFailure(Call<List<TipoVoluntariadoResponse>> call, Throwable t) {
            }
        });

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
            }
        });
    }

    private void addChip(ChipGroup group, int id, String text) {
        Chip chip = new Chip(this);
        chip.setText(text);
        chip.setTag(id);
        chip.setCheckable(true);
        chip.setChipBackgroundColorResource(R.color.bg_chip_state_list);
        chip.setTextColor(getResources().getColorStateList(R.color.text_chip_state_list, getTheme()));

        float strokeWidth = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1.5f,
                getResources().getDisplayMetrics());
        chip.setChipStrokeWidth(strokeWidth);

        int colorPrimary = ContextCompat.getColor(this, R.color.primary);
        int colorTransparent = ContextCompat.getColor(this, android.R.color.transparent);

        int[][] states = new int[][] { new int[] { android.R.attr.state_checked }, new int[] {} };
        int[] colors = new int[] { colorPrimary, colorTransparent };
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

    private void createActividad() {
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

        int duracion = 0, cupo = 0;
        try {
            duracion = Integer.parseInt(duracionStr);
            cupo = Integer.parseInt(cupoStr);
        } catch (NumberFormatException e) {
            return;
        }

        List<Integer> tiposIds = getSelectedIds(chipGroupTipos);
        List<Integer> odsIds = getSelectedIds(chipGroupOds);

        ActividadCreateRequest request = new ActividadCreateRequest(
                titulo, descripcion, fecha, duracion, cupo, ubicacion, orgId, odsIds, tiposIds);

        btnCreate.setEnabled(false);
        btnCreate.setText("Publicando...");

        ApiClient.getService().crearActividad(orgId, request).enqueue(new Callback<ActividadResponse>() {
            @Override
            public void onResponse(Call<ActividadResponse> call, Response<ActividadResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    int actividadId = response.body().getIdActividad();

                    if (selectedImageFile != null && selectedImageFile.exists()) {
                        uploadImage(actividadId, selectedImageFile);
                    } else {
                        finishSuccess();
                    }
                } else {
                    handleError(response.code());
                }
            }

            @Override
            public void onFailure(Call<ActividadResponse> call, Throwable t) {
                Log.e(TAG, "Error conexión", t);
                btnCreate.setEnabled(true);
                Toast.makeText(CreateActividad.this, "Error de conexión", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void handleError(int code) {
        btnCreate.setEnabled(true);
        btnCreate.setText("Crear Actividad");
        Toast.makeText(this, "Error backend: " + code, Toast.LENGTH_LONG).show();
    }

    private void uploadImage(int actividadId, java.io.File file) {
        // Multipart Implementation
        // Parte 1: El fichero
        okhttp3.RequestBody requestFile = okhttp3.RequestBody.create(okhttp3.MediaType.parse("image/jpeg"), file);

        // Parte 2: El wrapper con nombre de campo 'imagen' (que coincide con PHP)
        okhttp3.MultipartBody.Part body = okhttp3.MultipartBody.Part.createFormData("imagen", file.getName(),
                requestFile);

        ApiClient.getService().addImagenActividad(actividadId, body).enqueue(new Callback<MensajeResponse>() {
            @Override
            public void onResponse(Call<MensajeResponse> call, Response<MensajeResponse> response) {
                if (!response.isSuccessful()) {
                    Log.e(TAG, "Error upload image: " + response.code());
                }
                finishSuccess();
            }

            @Override
            public void onFailure(Call<MensajeResponse> call, Throwable t) {
                Log.e(TAG, "Error upload image network", t);
                finishSuccess();
            }
        });
    }

    private void finishSuccess() {
        Toast.makeText(this, "¡Actividad Creada Correctamente!", Toast.LENGTH_SHORT).show();
        finish();
    }
}