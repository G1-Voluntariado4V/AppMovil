package org.cuatrovientos.voluntariado4v.Fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.material.button.MaterialButton;

import org.cuatrovientos.voluntariado4v.Activities.AuthLogin;
import org.cuatrovientos.voluntariado4v.Models.OrganizacionResponse;
import org.cuatrovientos.voluntariado4v.R;
import org.cuatrovientos.voluntariado4v.API.ApiClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrganizationProfileFragment extends Fragment {

    private static final String TAG = "OrgProfileFragment";

    // Vistas
    private TextView tvHeaderName, tvOrgRole;
    private EditText etOrgName, etOrgAddress, etOrgEmail, etOrgPhone, etOrgWeb, etOrgDesc;
    private ImageView btnEditInfo, btnEditObs;
    private MaterialButton btnLogout;

    // Estado
    private boolean isEditingInfo = false;
    private boolean isEditingObs = false;

    private GoogleSignInClient googleSignInClient;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_organization_profile, container, false);

        setupGoogleSignIn();
        initViews(root);
        loadOrgDataFromApi();
        setupEditLogic();
        setupLogout();

        return root;
    }

    private void setupGoogleSignIn() {
        if (getActivity() != null) {
            GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestEmail()
                    .build();
            googleSignInClient = GoogleSignIn.getClient(getActivity(), gso);
        }
    }

    private void initViews(View root) {
        tvHeaderName = root.findViewById(R.id.tvHeaderName);
        tvOrgRole = root.findViewById(R.id.tvOrgRole);

        etOrgName = root.findViewById(R.id.etOrgName);
        etOrgAddress = root.findViewById(R.id.etOrgAddress);
        etOrgEmail = root.findViewById(R.id.etOrgEmail);
        etOrgPhone = root.findViewById(R.id.etOrgPhone);
        etOrgWeb = root.findViewById(R.id.etOrgWeb);
        etOrgDesc = root.findViewById(R.id.etOrgDesc);

        btnEditInfo = root.findViewById(R.id.btnEditInfo);
        btnEditObs = root.findViewById(R.id.btnEditObs);
        btnLogout = root.findViewById(R.id.btnLogout);

        // Bloquear campos por defecto
        setFieldsEnabled(false, etOrgName, etOrgAddress, etOrgEmail, etOrgPhone, etOrgWeb);
        setFieldsEnabled(false, etOrgDesc);
    }

    private void loadOrgDataFromApi() {
        if (getActivity() == null)
            return;

        SharedPreferences prefs = getActivity().getSharedPreferences("VoluntariadoPrefs", Context.MODE_PRIVATE);
        int orgId = prefs.getInt("user_id", -1);
        String savedRole = prefs.getString("rol", "Organización");

        tvOrgRole.setText(savedRole);

        if (orgId != -1) {
            ApiClient.getService().getOrganizationDetail(orgId).enqueue(new Callback<OrganizacionResponse>() {
                @Override
                public void onResponse(Call<OrganizacionResponse> call, Response<OrganizacionResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        populateData(response.body());
                    } else {
                        Log.e(TAG, "Error API: " + response.code());
                        Toast.makeText(getContext(), "Error al cargar datos", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<OrganizacionResponse> call, Throwable t) {
                    Log.e(TAG, "Error conexión", t);
                    Toast.makeText(getContext(), "Error de conexión", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void populateData(OrganizacionResponse org) {
        if (getContext() == null)
            return;

        // Header
        tvHeaderName.setText(getValueOrDefault(org.getNombre(), "Organización"));

        // Campos editables
        etOrgName.setText(getValueOrDefault(org.getNombre(), ""));
        etOrgAddress.setText(getValueOrDefault(org.getDireccion(), "No disponible"));
        etOrgEmail.setText(getValueOrDefault(org.getEmail(), "No disponible"));
        etOrgPhone.setText(getValueOrDefault(org.getTelefono(), "No disponible"));
        etOrgWeb.setText(getValueOrDefault(org.getSitioWeb(), "No disponible"));
        etOrgDesc.setText(getValueOrDefault(org.getDescripcion(), "Sin descripción"));
    }

    private String getValueOrDefault(String value, String defaultValue) {
        return (value != null && !value.trim().isEmpty()) ? value : defaultValue;
    }

    private void setupEditLogic() {
        // Editar Información General
        btnEditInfo.setOnClickListener(v -> {
            if (!isEditingInfo) {
                isEditingInfo = true;
                setFieldsEnabled(true, etOrgName, etOrgAddress, etOrgEmail, etOrgPhone, etOrgWeb);
                btnEditInfo.setImageResource(R.drawable.ic_check_circle);
                etOrgName.requestFocus();
            } else {
                saveOrganizationChanges();
            }
        });

        // Editar Descripción
        btnEditObs.setOnClickListener(v -> {
            if (!isEditingObs) {
                isEditingObs = true;
                setFieldsEnabled(true, etOrgDesc);
                btnEditObs.setImageResource(R.drawable.ic_check_circle);
                etOrgDesc.requestFocus();
            } else {
                saveOrganizationChanges();
            }
        });
    }

    private void saveOrganizationChanges() {
        if (getActivity() == null)
            return;

        SharedPreferences prefs = getActivity().getSharedPreferences("VoluntariadoPrefs", Context.MODE_PRIVATE);
        int orgId = prefs.getInt("user_id", -1);

        if (orgId == -1) {
            Toast.makeText(getContext(), "Error: sesión no válida", Toast.LENGTH_SHORT).show();
            return;
        }

        // Recoger valores de los campos
        String nombre = etOrgName.getText().toString().trim();
        String descripcion = etOrgDesc.getText().toString().trim();
        String direccion = etOrgAddress.getText().toString().trim();
        String telefono = etOrgPhone.getText().toString().trim();
        String web = etOrgWeb.getText().toString().trim();

        // Validación básica
        if (nombre.isEmpty()) {
            Toast.makeText(getContext(), "El nombre no puede estar vacío", Toast.LENGTH_SHORT).show();
            return;
        }
        if (descripcion.isEmpty()) {
            Toast.makeText(getContext(), "La descripción no puede estar vacía", Toast.LENGTH_SHORT).show();
            return;
        }

        // Limpiar valores "No disponible" antes de enviar
        if ("No disponible".equals(direccion))
            direccion = null;
        if ("No disponible".equals(telefono))
            telefono = null;
        if ("No disponible".equals(web))
            web = null;

        org.cuatrovientos.voluntariado4v.Models.OrganizacionUpdateRequest request = new org.cuatrovientos.voluntariado4v.Models.OrganizacionUpdateRequest(
                nombre, descripcion, web, direccion, telefono);

        ApiClient.getService().updateOrganizacion(orgId, request).enqueue(new Callback<OrganizacionResponse>() {
            @Override
            public void onResponse(Call<OrganizacionResponse> call, Response<OrganizacionResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(getContext(), "Cambios guardados correctamente", Toast.LENGTH_SHORT).show();
                    populateData(response.body());

                    // Restaurar modo visualización
                    isEditingInfo = false;
                    isEditingObs = false;
                    setFieldsEnabled(false, etOrgName, etOrgAddress, etOrgEmail, etOrgPhone, etOrgWeb);
                    setFieldsEnabled(false, etOrgDesc);
                    btnEditInfo.setImageResource(R.drawable.ic_edit);
                    btnEditObs.setImageResource(R.drawable.ic_edit);
                } else {
                    Log.e(TAG, "Error guardando: " + response.code());
                    Toast.makeText(getContext(), "Error al guardar: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<OrganizacionResponse> call, Throwable t) {
                Log.e(TAG, "Error conexión", t);
                Toast.makeText(getContext(), "Error de conexión", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setFieldsEnabled(boolean enabled, EditText... fields) {
        for (EditText field : fields) {
            field.setFocusable(enabled);
            field.setFocusableInTouchMode(enabled);
            field.setClickable(enabled);
            field.setCursorVisible(enabled);
            if (enabled) {
                field.setBackgroundResource(R.drawable.bg_tab_selected); // Usar un fondo que indique edición
            } else {
                field.setBackground(null);
            }
        }
    }

    private void setupLogout() {
        btnLogout.setOnClickListener(v -> {
            if (googleSignInClient != null) {
                googleSignInClient.signOut().addOnCompleteListener(task -> {
                    performLocalLogout();
                });
            } else {
                performLocalLogout();
            }
        });
    }

    private void performLocalLogout() {
        if (getActivity() == null)
            return;

        SharedPreferences prefs = getActivity().getSharedPreferences("VoluntariadoPrefs", Context.MODE_PRIVATE);
        prefs.edit().clear().apply();

        Intent intent = new Intent(getActivity(), AuthLogin.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        getActivity().finish();
    }
}