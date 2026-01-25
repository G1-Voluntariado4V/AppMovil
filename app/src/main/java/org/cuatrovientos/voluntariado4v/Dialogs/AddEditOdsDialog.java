package org.cuatrovientos.voluntariado4v.Dialogs;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import org.cuatrovientos.voluntariado4v.API.ApiClient;
import org.cuatrovientos.voluntariado4v.API.VoluntariadoApiService;
import org.cuatrovientos.voluntariado4v.Models.OdsResponse;
import org.cuatrovientos.voluntariado4v.R;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddEditOdsDialog extends DialogFragment {

    private TextInputEditText etName, etDescription;
    private ImageView imgOds;
    private TextView txtTitle;
    private MaterialButton btnSave;

    private VoluntariadoApiService apiService;
    private OdsResponse currentOds;
    private OnOdsSavedListener listener;

    // Variable para guardar la URI de la imagen seleccionada
    private Uri selectedImageUri;

    // Lanzador para seleccionar imagen de la galería
    private final ActivityResultLauncher<String> pickImageLauncher = registerForActivityResult(
            new ActivityResultContracts.GetContent(),
            uri -> {
                if (uri != null) {
                    selectedImageUri = uri;
                    imgOds.setImageURI(uri); // Mostrar la imagen seleccionada
                    imgOds.setColorFilter(null); // Quitar el tinte si tenía (para que se vea el color real)
                    imgOds.setPadding(0, 0, 0, 0); // Quitar padding si es necesario
                }
            });

    // Interfaz para comunicar el éxito a la Activity padre
    public interface OnOdsSavedListener {
        void onOdsSaved();
    }

    public void setListener(OnOdsSavedListener listener) {
        this.listener = listener;
    }

    public static AddEditOdsDialog newInstance(OdsResponse ods) {
        AddEditOdsDialog dialog = new AddEditOdsDialog();
        Bundle args = new Bundle();
        if (ods != null) {
            args.putSerializable("ODS_DATA", ods);
        }
        dialog.setArguments(args);
        return dialog;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_add_edit_ods, container, false);

        // Configurar fondo transparente para el Dialog
        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        }

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        apiService = ApiClient.getService();
        initViews(view);

        if (getArguments() != null && getArguments().containsKey("ODS_DATA")) {
            currentOds = (OdsResponse) getArguments().getSerializable("ODS_DATA");
            setupEditMode();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        // Ajustar el ancho del dialog al 90% de la pantalla
        if (getDialog() != null && getDialog().getWindow() != null) {
            int width = (int) (getResources().getDisplayMetrics().widthPixels * 0.90);
            getDialog().getWindow().setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
    }

    private void initViews(View view) {
        etName = view.findViewById(R.id.etName);
        etDescription = view.findViewById(R.id.etDescription);
        imgOds = view.findViewById(R.id.imgOds);
        txtTitle = view.findViewById(R.id.txtDialogTitle);
        btnSave = view.findViewById(R.id.btnSave);
        ImageButton btnClose = view.findViewById(R.id.btnClose);
        View imageContainer = view.findViewById(R.id.imageContainer);

        btnClose.setOnClickListener(v -> dismiss());

        // Al hacer clic en la imagen, abrir galería
        imageContainer.setOnClickListener(v -> openGallery());
        imgOds.setOnClickListener(v -> openGallery());

        btnSave.setOnClickListener(v -> saveOds());
    }

    private void openGallery() {
        pickImageLauncher.launch("image/*");
    }

    private void setupEditMode() {
        txtTitle.setText("Editar ODS");
        etName.setText(currentOds.getNombre());
        etDescription.setText(currentOds.getDescripcion());
        btnSave.setText("Actualizar");

        // Aquí podrías cargar la imagen actual si viniera de la API (usando
        // Glide/Picasso)
        // Por ahora, como OdsResponse no tiene URL de imagen, dejamos el placeholder.
    }

    private void saveOds() {
        String name = etName.getText() != null ? etName.getText().toString().trim() : "";
        String desc = etDescription.getText() != null ? etDescription.getText().toString().trim() : "";

        if (name.isEmpty() || desc.isEmpty()) {
            Toast.makeText(getContext(), "Completa todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        OdsResponse odsData = new OdsResponse();
        odsData.setNombre(name);
        odsData.setDescripcion(desc);

        Call<OdsResponse> call;
        if (currentOds == null) {
            call = apiService.createOds(odsData);
        } else {
            odsData.setId(currentOds.getId());
            call = apiService.updateOds(currentOds.getId(), odsData);
        }

        btnSave.setEnabled(false);
        btnSave.setText("Guardando...");

        call.enqueue(new Callback<OdsResponse>() {
            @Override
            public void onResponse(Call<OdsResponse> call, Response<OdsResponse> response) {
                if (isAdded()) {
                    if (response.isSuccessful() && response.body() != null) {
                        int odsId = response.body().getId();

                        // Si se seleccionó imagen, la subimos ahora
                        if (selectedImageUri != null) {
                            uploadImage(odsId);
                        } else {
                            // Si no hay imagen, terminamos
                            finishSuccess();
                        }
                    } else {
                        btnSave.setEnabled(true);
                        btnSave.setText(currentOds == null ? "Crear" : "Actualizar");
                        Toast.makeText(getContext(), "Error al guardar datos: " + response.code(), Toast.LENGTH_SHORT)
                                .show();
                    }
                }
            }

            @Override
            public void onFailure(Call<OdsResponse> call, Throwable t) {
                if (isAdded()) {
                    btnSave.setEnabled(true);
                    btnSave.setText(currentOds == null ? "Crear" : "Actualizar");
                    Toast.makeText(getContext(), "Error de conexión", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void uploadImage(int odsId) {
        if (getContext() == null || selectedImageUri == null)
            return;

        try {
            // Preparar el archivo desde la URI
            java.io.InputStream inputStream = getContext().getContentResolver().openInputStream(selectedImageUri);
            if (inputStream == null) {
                finishSuccess(); // Fallo silencioso o mostrar error
                return;
            }

            byte[] bytes = new byte[inputStream.available()];
            inputStream.read(bytes);

            // Determinar tipo mime
            String mimeType = getContext().getContentResolver().getType(selectedImageUri);
            if (mimeType == null)
                mimeType = "image/jpeg";

            // Crear RequestBody
            okhttp3.RequestBody requestFile = okhttp3.RequestBody.create(okhttp3.MediaType.parse(mimeType), bytes);

            // Crear MultipartBody.Part (nombre del campo 'imagen' debe coincidir con API)
            okhttp3.MultipartBody.Part body = okhttp3.MultipartBody.Part.createFormData("imagen", "ods_image.jpg",
                    requestFile);

            apiService.uploadOdsImage(odsId, body)
                    .enqueue(new Callback<org.cuatrovientos.voluntariado4v.Models.MensajeResponse>() {
                        @Override
                        public void onResponse(Call<org.cuatrovientos.voluntariado4v.Models.MensajeResponse> call,
                                Response<org.cuatrovientos.voluntariado4v.Models.MensajeResponse> response) {
                            // Independientemente de si sube o falla, cerramos (o podríamos notificar error)
                            if (!response.isSuccessful()) {
                                Toast.makeText(getContext(), "Datos guardados pero falló imagen: " + response.code(),
                                        Toast.LENGTH_SHORT).show();
                            }
                            finishSuccess();
                        }

                        @Override
                        public void onFailure(Call<org.cuatrovientos.voluntariado4v.Models.MensajeResponse> call,
                                Throwable t) {
                            Toast.makeText(getContext(), "Datos guardados pero error red imagen", Toast.LENGTH_SHORT)
                                    .show();
                            finishSuccess();
                        }
                    });

        } catch (Exception e) {
            e.printStackTrace();
            finishSuccess();
        }
    }

    private void finishSuccess() {
        if (isAdded()) {
            Toast.makeText(getContext(), "Guardado correctamente", Toast.LENGTH_SHORT).show();
            if (listener != null)
                listener.onOdsSaved();
            dismiss();
        }
    }
}