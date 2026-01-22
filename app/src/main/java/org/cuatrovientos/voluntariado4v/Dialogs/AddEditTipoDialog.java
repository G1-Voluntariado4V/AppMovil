package org.cuatrovientos.voluntariado4v.Dialogs;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import org.cuatrovientos.voluntariado4v.API.ApiClient;
import org.cuatrovientos.voluntariado4v.API.VoluntariadoApiService;
import org.cuatrovientos.voluntariado4v.Models.TipoVoluntariadoResponse;
import org.cuatrovientos.voluntariado4v.R;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddEditTipoDialog extends DialogFragment {

    private TextInputEditText etName;
    private TextView txtTitle;
    private MaterialButton btnSave;

    private VoluntariadoApiService apiService;
    private TipoVoluntariadoResponse currentTipo;
    private OnTipoSavedListener listener;

    public interface OnTipoSavedListener {
        void onTipoSaved();
    }

    public void setListener(OnTipoSavedListener listener) {
        this.listener = listener;
    }

    public static AddEditTipoDialog newInstance(TipoVoluntariadoResponse tipo) {
        AddEditTipoDialog dialog = new AddEditTipoDialog();
        Bundle args = new Bundle();
        if (tipo != null) {
            args.putSerializable("TIPO_DATA", tipo);
        }
        dialog.setArguments(args);
        return dialog;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_add_edit_tipo, container, false);
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

        if (getArguments() != null && getArguments().containsKey("TIPO_DATA")) {
            currentTipo = (TipoVoluntariadoResponse) getArguments().getSerializable("TIPO_DATA");
            setupEditMode();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getDialog() != null && getDialog().getWindow() != null) {
            int width = (int) (getResources().getDisplayMetrics().widthPixels * 0.90);
            getDialog().getWindow().setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
    }

    private void initViews(View view) {
        etName = view.findViewById(R.id.etName);
        txtTitle = view.findViewById(R.id.txtDialogTitle);
        btnSave = view.findViewById(R.id.btnSave);
        ImageButton btnClose = view.findViewById(R.id.btnClose);

        btnClose.setOnClickListener(v -> dismiss());
        btnSave.setOnClickListener(v -> saveTipo());
    }

    private void setupEditMode() {
        txtTitle.setText("Editar Tipo");
        etName.setText(currentTipo.getNombre());
        btnSave.setText("Actualizar");
    }

    private void saveTipo() {
        String name = etName.getText() != null ? etName.getText().toString().trim() : "";

        if (name.isEmpty()) {
            Toast.makeText(getContext(), "Escribe un nombre", Toast.LENGTH_SHORT).show();
            return;
        }

        TipoVoluntariadoResponse tipoData = new TipoVoluntariadoResponse();
        tipoData.setNombre(name);

        Call<TipoVoluntariadoResponse> call;
        if (currentTipo == null) {
            call = apiService.createTipoVoluntariado(tipoData);
        } else {
            tipoData.setId(currentTipo.getId());
            call = apiService.updateTipoVoluntariado(currentTipo.getId(), tipoData);
        }

        btnSave.setEnabled(false);
        btnSave.setText("Guardando...");

        call.enqueue(new Callback<TipoVoluntariadoResponse>() {
            @Override
            public void onResponse(Call<TipoVoluntariadoResponse> call, Response<TipoVoluntariadoResponse> response) {
                if (isAdded()) {
                    if (response.isSuccessful()) {
                        Toast.makeText(getContext(), "Guardado correctamente", Toast.LENGTH_SHORT).show();
                        if (listener != null) listener.onTipoSaved();
                        dismiss();
                    } else {
                        btnSave.setEnabled(true);
                        btnSave.setText("Guardar");
                        Toast.makeText(getContext(), "Error: " + response.code(), Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<TipoVoluntariadoResponse> call, Throwable t) {
                if (isAdded()) {
                    btnSave.setEnabled(true);
                    btnSave.setText("Guardar");
                    Toast.makeText(getContext(), "Error de conexión", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}