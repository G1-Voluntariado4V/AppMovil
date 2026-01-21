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
import org.cuatrovientos.voluntariado4v.API.ApiClient;
import org.cuatrovientos.voluntariado4v.Models.CoordinadorResponse;
import org.cuatrovientos.voluntariado4v.Models.CoordinadorUpdateRequest;
import org.cuatrovientos.voluntariado4v.R;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CoordinatorProfileFragment extends Fragment {

    private static final String TAG = "CoordProfileFragment";

    // Vistas
    private TextView tvHeaderName, tvCoordRole;
    private EditText etCoordName, etCoordSurname, etCoordEmail, etCoordPhone;
    private ImageView btnEditInfo;
    private MaterialButton btnLogout;

    // Estado
    private boolean isEditingInfo = false;
    private GoogleSignInClient googleSignInClient;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_coordinator_profile, container, false);

        setupGoogleSignIn();
        initViews(root);
        loadCoordDataFromApi();
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
        tvCoordRole = root.findViewById(R.id.tvCoordRole);

        etCoordName = root.findViewById(R.id.etCoordName);
        etCoordSurname = root.findViewById(R.id.etCoordSurname);
        etCoordEmail = root.findViewById(R.id.etCoordEmail);
        etCoordPhone = root.findViewById(R.id.etCoordPhone);

        btnEditInfo = root.findViewById(R.id.btnEditInfo);
        btnLogout = root.findViewById(R.id.btnLogout);

        // Bloquear campos por defecto
        setFieldsEnabled(false, etCoordName, etCoordSurname, etCoordEmail, etCoordPhone);
    }

    private void loadCoordDataFromApi() {
        if (getActivity() == null) return;

        SharedPreferences prefs = getActivity().getSharedPreferences("VoluntariadoPrefs", Context.MODE_PRIVATE);
        int userId = prefs.getInt("user_id", -1);
        String savedRole = prefs.getString("rol", "Coordinador");

        tvCoordRole.setText(savedRole);

        if (userId != -1) {
            // El propio coordinador actúa como admin de su perfil
            ApiClient.getService().getCoordinadorDetail(userId, userId).enqueue(new Callback<CoordinadorResponse>() {
                @Override
                public void onResponse(Call<CoordinadorResponse> call, Response<CoordinadorResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        populateData(response.body());
                    } else {
                        Log.e(TAG, "Error API: " + response.code());
                        Toast.makeText(getContext(), "Error al cargar perfil", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<CoordinadorResponse> call, Throwable t) {
                    Log.e(TAG, "Error conexión", t);
                    Toast.makeText(getContext(), "Error de conexión", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void populateData(CoordinadorResponse coord) {
        if (getContext() == null) return;

        // Header (podemos combinar nombre y apellidos para mostrarlo bonito arriba)
        String fullName = getValueOrDefault(coord.getNombre(), "") + " " + getValueOrDefault(coord.getApellidos(), "");
        tvHeaderName.setText(fullName.trim().isEmpty() ? "Coordinador" : fullName);

        // Campos editables separados
        etCoordName.setText(getValueOrDefault(coord.getNombre(), ""));
        etCoordSurname.setText(getValueOrDefault(coord.getApellidos(), ""));
        etCoordPhone.setText(getValueOrDefault(coord.getTelefono(), ""));

        // Email (generalmente read-only)
        etCoordEmail.setText(getValueOrDefault(coord.getCorreo(), "No disponible"));
    }

    private String getValueOrDefault(String value, String defaultValue) {
        return (value != null && !value.trim().isEmpty()) ? value : defaultValue;
    }

    private void setupEditLogic() {
        btnEditInfo.setOnClickListener(v -> {
            if (!isEditingInfo) {
                isEditingInfo = true;
                // Habilitamos Nombre, Apellidos, Telefono
                setFieldsEnabled(true, etCoordName, etCoordSurname, etCoordPhone);
                btnEditInfo.setImageResource(R.drawable.ic_check_circle);
                etCoordName.requestFocus();
            } else {
                saveCoordinatorChanges();
            }
        });
    }

    private void saveCoordinatorChanges() {
        if (getActivity() == null) return;

        SharedPreferences prefs = getActivity().getSharedPreferences("VoluntariadoPrefs", Context.MODE_PRIVATE);
        int userId = prefs.getInt("user_id", -1);

        if (userId == -1) {
            Toast.makeText(getContext(), "Error: sesión no válida", Toast.LENGTH_SHORT).show();
            return;
        }

        String nombre = etCoordName.getText().toString().trim();
        String apellidos = etCoordSurname.getText().toString().trim();
        String telefono = etCoordPhone.getText().toString().trim();

        if (nombre.isEmpty()) {
            Toast.makeText(getContext(), "El nombre es obligatorio", Toast.LENGTH_SHORT).show();
            return;
        }

        CoordinadorUpdateRequest request = new CoordinadorUpdateRequest(nombre, apellidos, telefono);

        ApiClient.getService().updateCoordinador(userId, userId, request).enqueue(new Callback<CoordinadorResponse>() {
            @Override
            public void onResponse(Call<CoordinadorResponse> call, Response<CoordinadorResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(getContext(), "Perfil actualizado", Toast.LENGTH_SHORT).show();
                    populateData(response.body());

                    // Restaurar modo visualización
                    isEditingInfo = false;
                    setFieldsEnabled(false, etCoordName, etCoordSurname, etCoordPhone);
                    btnEditInfo.setImageResource(R.drawable.ic_edit);
                } else {
                    Log.e(TAG, "Error guardando: " + response.code());
                    Toast.makeText(getContext(), "Error al guardar cambios", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<CoordinadorResponse> call, Throwable t) {
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
                field.setBackgroundResource(R.drawable.bg_tab_selected);
            } else {
                field.setBackground(null);
            }
        }
    }

    private void setupLogout() {
        btnLogout.setOnClickListener(v -> {
            if (googleSignInClient != null) {
                googleSignInClient.signOut().addOnCompleteListener(task -> performLocalLogout());
            } else {
                performLocalLogout();
            }
        });
    }

    private void performLocalLogout() {
        if (getActivity() == null) return;

        SharedPreferences prefs = getActivity().getSharedPreferences("VoluntariadoPrefs", Context.MODE_PRIVATE);
        prefs.edit().clear().apply();

        Intent intent = new Intent(getActivity(), AuthLogin.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        getActivity().finish();
    }
}