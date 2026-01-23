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

import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.textfield.TextInputEditText;

import org.cuatrovientos.voluntariado4v.API.ApiClient;
import org.cuatrovientos.voluntariado4v.Models.ActividadResponse;
import org.cuatrovientos.voluntariado4v.Models.ActividadUpdateRequest;
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

public class EditActividad extends AppCompatActivity {

    private static final String TAG = "EditActividad";

    // UI
    private TextInputEditText etTitulo, etDescripcion, etFecha, etUbicacion, etDuracion, etCupo;
    private ChipGroup chipGroupTipos, chipGroupOds;
    private MaterialButton btnSave, btnSelectImage;
    private MaterialCardView cardImagePreview;
    private ImageView ivPreview;
    private LinearLayout layoutPlaceholder;

    // Datos
    private int orgId;
    private int actividadId;
    private ActividadResponse actividadOriginal;
    private Calendar selectedDateTime = Calendar.getInstance();

    // Imagen File
    private java.io.File selectedImageFile = null;
    private ActivityResultLauncher<String> imagePickerLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_actividad);

        SharedPreferences prefs = getSharedPreferences("VoluntariadoPrefs", Context.MODE_PRIVATE);
        orgId = prefs.getInt("user_id", -1);

        actividadOriginal = (ActividadResponse) getIntent().getSerializableExtra("actividad");
        if (actividadOriginal == null) {
            Toast.makeText(this, "Error: No se pudo cargar la actividad", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        actividadId = actividadOriginal.getId();

        initViews();
        setupToolbar();
        setupDatePicker();
        setupImagePicker();

        populateBasicData();
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

        chipGroupTipos = findViewById(R.id.chipGroupTipos);
        chipGroupOds = findViewById(R.id.chipGroupOds);

        btnSave = findViewById(R.id.btnCreate);
        btnSave.setText("Guardar Cambios");

        btnSelectImage = findViewById(R.id.btnSelectImage);
        cardImagePreview = findViewById(R.id.cardImagePreview);
        ivPreview = findViewById(R.id.ivPreview);
        layoutPlaceholder = findViewById(R.id.layoutPlaceholder);
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

    private void setupImagePicker() {
        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                uri -> {
                    if (uri != null) {
                        try {
                            processImageUri(uri);
                        } catch (Exception e) {
                            Log.e(TAG, "Error procesando imagen", e);
                        }
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

            selectedImageFile = saveBitmapToFile(resized);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private java.io.File saveBitmapToFile(Bitmap bitmap) {
        try {
            java.io.File cacheDir = getCacheDir();
            java.io.File f = new java.io.File(cacheDir, "upload_edit_" + System.currentTimeMillis() + ".jpg");
            java.io.FileOutputStream fos = new java.io.FileOutputStream(f);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 85, fos);
            fos.flush();
            fos.close();
            return f;
        } catch (Exception e) {
            return null;
        }
    }

    private Bitmap resizeBitmap(Bitmap image, int maxSize) {
        int width = image.getWidth();
        int height = image.getHeight();
        float bitmapRatio = (float) width / (float) height;
        if (bitmapRatio > 1) {
            width = maxSize;
            height = (int) (width / bitmapRatio);
        } else {
            height = maxSize;
            width = (int) (height * bitmapRatio);
        }
        return Bitmap.createScaledBitmap(image, width, height, true);
    }

    private void populateBasicData() {
        if (actividadOriginal.getTitulo() != null)
            etTitulo.setText(actividadOriginal.getTitulo());
        if (actividadOriginal.getDescripcion() != null)
            etDescripcion.setText(actividadOriginal.getDescripcion());
        if (actividadOriginal.getUbicacion() != null)
            etUbicacion.setText(actividadOriginal.getUbicacion());
        etDuracion.setText(String.valueOf(actividadOriginal.getDuracionHoras()));
        etCupo.setText(String.valueOf(actividadOriginal.getCupoMaximo()));

        String fecha = actividadOriginal.getFechaInicio();
        if (fecha != null && fecha.length() >= 19) {
            etFecha.setText(fecha.substring(0, 19));
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                selectedDateTime.setTime(sdf.parse(fecha.substring(0, 19)));
            } catch (Exception e) {
            }
        }

        // Cargar imagen actual con Glide si existe
        String imgUrl = actividadOriginal.getImageUrl();
        if (imgUrl != null && !imgUrl.isEmpty() && !imgUrl.contains("placehold")) {
            Glide.with(this).load(imgUrl).centerCrop().into(ivPreview);
            layoutPlaceholder.setVisibility(View.GONE);
            btnSelectImage.setVisibility(View.VISIBLE);
        }
    }

    private void loadCatalogos() {
        ApiClient.getService().getTiposVoluntariado().enqueue(new Callback<List<TipoVoluntariadoResponse>>() {
            @Override
            public void onResponse(Call<List<TipoVoluntariadoResponse>> call,
                    Response<List<TipoVoluntariadoResponse>> response) {
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
            }
        });

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
            }
        });
    }

    private void addChip(ChipGroup group, int id, String text, boolean isChecked) {
        Chip chip = new Chip(this);
        chip.setText(text);
        chip.setTag(id);
        chip.setCheckable(true);
        chip.setChecked(isChecked);
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

    private boolean isTypeSelected(String typeName) {
        if (actividadOriginal.getTipos() == null)
            return false;
        for (TipoVoluntariadoResponse t : actividadOriginal.getTipos()) {
            if (t.getNombre() != null && t.getNombre().equals(typeName))
                return true;
        }
        return false;
    }

    private boolean isOdsSelected(String odsName) {
        if (actividadOriginal.getOds() == null)
            return false;
        for (OdsResponse o : actividadOriginal.getOds()) {
            if (o.getNombre() != null && o.getNombre().equals(odsName))
                return true;
        }
        return false;
    }

    private List<Integer> getSelectedIds(ChipGroup group) {
        List<Integer> ids = new ArrayList<>();
        for (int i = 0; i < group.getChildCount(); i++) {
            Chip chip = (Chip) group.getChildAt(i);
            if (chip.isChecked())
                ids.add((Integer) chip.getTag());
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
        if (ubicacion.isEmpty()) {
            etUbicacion.setError("Requerido");
            return;
        }

        int duracion = 0, cupo = 0;
        try {
            duracion = Integer.parseInt(duracionStr);
            cupo = Integer.parseInt(cupoStr);
        } catch (Exception e) {
        }

        List<Integer> tiposIds = getSelectedIds(chipGroupTipos);
        List<Integer> odsIds = getSelectedIds(chipGroupOds);

        ActividadUpdateRequest request = new ActividadUpdateRequest(
                titulo, descripcion, fecha, duracion, cupo, ubicacion, odsIds, tiposIds);

        btnSave.setEnabled(false);
        btnSave.setText("Guardando...");

        ApiClient.getService().updateActividad(actividadId, request).enqueue(new Callback<ActividadResponse>() {
            @Override
            public void onResponse(Call<ActividadResponse> call, Response<ActividadResponse> response) {
                if (response.isSuccessful()) {
                    if (selectedImageFile != null && selectedImageFile.exists()) {
                        updateImage(actividadId, selectedImageFile);
                    } else {
                        finishSuccess();
                    }
                } else {
                    btnSave.setEnabled(true);
                    Toast.makeText(EditActividad.this, "Error guardando: " + response.code(), Toast.LENGTH_SHORT)
                            .show();
                }
            }

            @Override
            public void onFailure(Call<ActividadResponse> call, Throwable t) {
                btnSave.setEnabled(true);
                Toast.makeText(EditActividad.this, "Error conexión", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateImage(int actividadId, java.io.File file) {
        // Lógica Multipart
        okhttp3.RequestBody requestFile = okhttp3.RequestBody.create(okhttp3.MediaType.parse("image/jpeg"), file);

        okhttp3.MultipartBody.Part body = okhttp3.MultipartBody.Part.createFormData("imagen", file.getName(),
                requestFile);

        ApiClient.getService().addImagenActividad(actividadId, body).enqueue(new Callback<MensajeResponse>() {
            @Override
            public void onResponse(Call<MensajeResponse> call, Response<MensajeResponse> response) {
                finishSuccess();
            }

            @Override
            public void onFailure(Call<MensajeResponse> call, Throwable t) {
                finishSuccess();
            }
        });
    }

    private void finishSuccess() {
        Toast.makeText(EditActividad.this, "Actividad Actualizada", Toast.LENGTH_SHORT).show();
        finish();
    }
}