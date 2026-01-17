package org.cuatrovientos.voluntariado4v.Activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButton;

import org.cuatrovientos.voluntariado4v.Adapters.ActividadesApiAdapter; // Usamos el Adapter real
import org.cuatrovientos.voluntariado4v.Models.ActividadResponse;
import org.cuatrovientos.voluntariado4v.Models.OrganizacionResponse;
import org.cuatrovientos.voluntariado4v.R;
import org.cuatrovientos.voluntariado4v.API.ApiClient;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailOrganization extends AppCompatActivity {

    private static final String TAG = "DetailOrganization";

    private TextView tvName, tvSubtitle, tvDescription;
    private TextView tvStatActivities, tvStatVolunteers, tvStatRating;
    private ImageView imgHeader, imgLogo, btnBack;
    private MaterialButton btnContact;
    private RecyclerView rvActivities;

    private OrganizacionResponse currentOrg;
    private int orgId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_detail_organization);

        initViews();

        // Recibimos ID
        orgId = getIntent().getIntExtra("ORG_ID", -1);
        String fallbackName = getIntent().getStringExtra("ORG_NAME");

        if (orgId == -1) {
            Toast.makeText(this, "Organización no encontrada", Toast.LENGTH_SHORT).show();
            if (fallbackName != null)
                tvName.setText(fallbackName);
            // Podríamos intentar buscar por nombre si tuviéramos endpoint, pero por ahora
            // mostramos error/logging
            Log.e(TAG, "Falta ORG_ID en el intent.");
        } else {
            loadOrganizationData(orgId);
            loadOrganizationActivities(orgId);
        }

        setupButtons();
    }

    private void initViews() {
        imgHeader = findViewById(R.id.headerImage);
        imgLogo = findViewById(R.id.imgOrgLogo);
        btnBack = findViewById(R.id.btnBack);

        tvName = findViewById(R.id.tvOrgName);
        tvSubtitle = findViewById(R.id.tvOrgSubtitle);
        tvDescription = findViewById(R.id.tvOrgDescription);

        tvStatActivities = findViewById(R.id.tvStatActivities);
        tvStatVolunteers = findViewById(R.id.tvStatVolunteers);
        tvStatRating = findViewById(R.id.tvStatRating);

        btnContact = findViewById(R.id.btnContact);
        rvActivities = findViewById(R.id.rvActivities);
    }

    private void loadOrganizationData(int id) {
        ApiClient.getService().getOrganizacion(id).enqueue(new Callback<OrganizacionResponse>() {
            @Override
            public void onResponse(Call<OrganizacionResponse> call, Response<OrganizacionResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    currentOrg = response.body();
                    populateUI(currentOrg);
                } else {
                    Toast.makeText(DetailOrganization.this, "Error cargando organización", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<OrganizacionResponse> call, Throwable t) {
                Toast.makeText(DetailOrganization.this, "Fallo de conexión", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadOrganizationActivities(int id) {
        ApiClient.getService().getActividadesOrganizacion(id).enqueue(new Callback<List<ActividadResponse>>() {
            @Override
            public void onResponse(Call<List<ActividadResponse>> call, Response<List<ActividadResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    setupRecycler(response.body());
                    // Actualizar contador visualmente
                    tvStatActivities.setText(String.valueOf(response.body().size()));
                }
            }

            @Override
            public void onFailure(Call<List<ActividadResponse>> call, Throwable t) {
                Log.e(TAG, "Error cargando actividades org", t);
            }
        });
    }

    private void populateUI(OrganizacionResponse org) {
        tvName.setText(org.getNombre());
        tvSubtitle.setText("Organización");
        tvDescription.setText(org.getDescripcion());

        // Prioridad Imagen: 1. Intent Extra, 2. Fallback
        String imgUrl = getIntent().getStringExtra("ORG_IMG"); // Usamos el extra pasado por DetailActivity

        Glide.with(this)
                .load(imgUrl)
                .centerCrop()
                .placeholder(R.drawable.squarelogo)
                .error(
                        Glide.with(this)
                                .load("https://placehold.co/600x400/780000/ffffff.png?text="
                                        + Uri.encode(org.getNombre()))
                                .centerCrop())
                .into(imgLogo);

        // Imagen Header: Configuramos para que sea la misma que imgOrgLogo
        Glide.with(this)
                .load(imgUrl)
                .centerCrop()
                .placeholder(R.drawable.activities1) // Placeholder genérico fondo
                .error(R.drawable.activities1)
                .into(imgHeader);

        // Estadísticas dummy
        tvStatVolunteers.setText("150+");
        tvStatRating.setText("4.8");
    }

    private void setupRecycler(List<ActividadResponse> actividades) {
        ActividadesApiAdapter adapter = new ActividadesApiAdapter(actividades, (item, position) -> {
            Intent intent = new Intent(DetailOrganization.this, DetailActivity.class);
            intent.putExtra("actividad", item); // CORREGIDO: Clave debe coincidir con DetailActivity
            startActivity(intent);
        });
        rvActivities.setLayoutManager(new LinearLayoutManager(this));
        rvActivities.setAdapter(adapter);
    }

    private void setupButtons() {
        btnBack.setOnClickListener(v -> finish());

        btnContact.setOnClickListener(v -> {
            if (currentOrg == null)
                return;

            // Intent de correo
            Intent intent = new Intent(Intent.ACTION_SENDTO);
            intent.setData(Uri.parse("mailto:")); // Solo apps de correo
            // Si la API no devuelve email, usamos uno dummy o lo protegemos
            intent.putExtra(Intent.EXTRA_EMAIL,
                    new String[] { "contacto@" + currentOrg.getNombre().toLowerCase().replace(" ", "") + ".org" });
            intent.putExtra(Intent.EXTRA_SUBJECT, "Consulta VoluntariadoApp: " + currentOrg.getNombre());

            try {
                startActivity(intent);
            } catch (Exception e) {
                Toast.makeText(this, "No se encontró app de correo", Toast.LENGTH_SHORT).show();
            }
        });
    }
}