package org.cuatrovientos.voluntariado4v.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;

import org.cuatrovientos.voluntariado4v.Models.LoginRequest;
import org.cuatrovientos.voluntariado4v.Models.LoginResponse;
import org.cuatrovientos.voluntariado4v.R;
import org.cuatrovientos.voluntariado4v.api.ApiClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AuthLogin extends AppCompatActivity {

    private static final String TAG = "AuthLogin";

    // ═══════════════════════════════════════════════════════════════════
    // DEBUG MODE: Cambiar a false para usar Google Sign-In real
    // ═══════════════════════════════════════════════════════════════════
    private static final boolean DEBUG_MODE = false;
    private static final int DEBUG_USER_ID = 1;

    private GoogleSignInClient googleSignInClient;
    private ActivityResultLauncher<Intent> signInLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_auth_login);

        setupGoogleSignIn();
        setupSignInLauncher();

        MaterialButton btnLogin = findViewById(R.id.btnGoogleLogin);
        btnLogin.setOnClickListener(v -> {
            if (DEBUG_MODE) {
                debugLogin();
            } else {
                startGoogleSignIn();
            }
        });
    }

    private void setupGoogleSignIn() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(this, gso);
    }

    private void setupSignInLauncher() {
        signInLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(result.getData());
                        handleSignInResult(task);
                    } else {
                        Log.e(TAG, "Sign-in cancelado o fallido");
                        Toast.makeText(this, "Login cancelado", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void startGoogleSignIn() {
        Intent signInIntent = googleSignInClient.getSignInIntent();
        signInLauncher.launch(signInIntent);
    }

    private void handleSignInResult(Task<GoogleSignInAccount> task) {
        try {
            GoogleSignInAccount account = task.getResult(ApiException.class);
            String googleId = account.getId();
            String email = account.getEmail();
            Log.d(TAG, "Google Sign-In OK: " + email);

            loginWithApi(googleId, email);

        } catch (ApiException e) {
            Log.e(TAG, "Google Sign-In error: " + e.getStatusCode());
            Toast.makeText(this, "Error de autenticación", Toast.LENGTH_SHORT).show();
        }
    }

    private void loginWithApi(String rawGoogleId, String rawEmail) {
        final String googleId = rawGoogleId != null ? rawGoogleId.trim() : null;
        final String email = rawEmail != null ? rawEmail.trim() : null;

        Log.d(TAG, "Login API: ID=" + googleId + ", Email=" + email);

        LoginRequest request = new LoginRequest(googleId, email);

        ApiClient.getService().login(request).enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    int userId = response.body().getIdUsuario();
                    String rol = response.body().getRol();
                    Log.d(TAG, "Login API OK: userId=" + userId + ", rol=" + rol);

                    saveSession(userId, googleId, email, rol);
                    goToDashboard();

                } else if (response.code() == 404) {
                    Log.d(TAG, "Usuario no registrado, ir a registro");
                    goToRegister(googleId, email);

                } else {
                    Log.e(TAG, "Error API: " + response.code());
                    Toast.makeText(AuthLogin.this, "Error del servidor", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                Log.e(TAG, "Error conexión API", t);
                Toast.makeText(AuthLogin.this, "Error de conexión", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void debugLogin() {
        Log.d(TAG, "DEBUG MODE: Saltando autenticación real");
        saveSession(DEBUG_USER_ID, "debug_google_id", "debug@test.com", "Voluntario");
        goToDashboard();
    }

    private void saveSession(int userId, String googleId, String email, String rol) {
        SharedPreferences prefs = getSharedPreferences("VoluntariadoPrefs", MODE_PRIVATE);
        prefs.edit()
                .putInt("user_id", userId)
                .putString("google_id", googleId)
                .putString("email", email)
                .putString("rol", rol)
                .putBoolean("is_logged_in", true)
                .apply();
    }

    private void goToDashboard() {
        startActivity(new Intent(this, UserDashboard.class));
        finish();
    }

    private void goToRegister(String googleId, String email) {
        Intent intent = new Intent(this, AuthCompleteProfile.class);
        intent.putExtra("google_id", googleId);
        intent.putExtra("email", email);
        startActivity(intent);
        finish();
    }
}