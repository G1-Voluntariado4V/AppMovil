package org.cuatrovientos.voluntariado4v;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;

public class DetailOrg extends AppCompatActivity {

    private TextView tvName, tvSubtitle, tvDescription;
    private TextView tvStatActivities, tvStatVolunteers, tvStatRating;
    private ImageView imgHeader, imgLogo, btnBack;
    private MaterialButton btnContact;
    private RecyclerView rvActivities;
    private OrganizationModel currentOrg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_detail_org);

        initViews();

        // 1. Recibimos el nombre de la organización
        String orgName = getIntent().getStringExtra("ORG_NAME");

        // Si no llega nombre (seguridad), usamos uno por defecto
        if (orgName == null || orgName.isEmpty()) {
            orgName = "Amavir";
        }

        // 2. Cargamos los datos
        loadOrganizationData(orgName);

        setupButtons();
    }

    private void initViews() {
        // Imágenes
        imgHeader = findViewById(R.id.headerImage);
        imgLogo = findViewById(R.id.imgOrgLogo);
        btnBack = findViewById(R.id.btnBack);

        // Textos principales
        tvName = findViewById(R.id.tvOrgName);
        tvSubtitle = findViewById(R.id.tvOrgSubtitle);
        tvDescription = findViewById(R.id.tvOrgDescription);

        // Estadísticas
        tvStatActivities = findViewById(R.id.tvStatActivities);
        tvStatVolunteers = findViewById(R.id.tvStatVolunteers);
        tvStatRating = findViewById(R.id.tvStatRating);

        // Botones y lista
        btnContact = findViewById(R.id.btnContact);
        rvActivities = findViewById(R.id.rvActivities);
    }

    private void loadOrganizationData(String orgName) {
        currentOrg = MockDataProvider.getOrganizationDetails(orgName);
        ArrayList<ActivityModel> orgActivities = MockDataProvider.getActivitiesByOrganization(orgName);

        if (currentOrg != null) {
            // Setear textos
            tvName.setText(currentOrg.getName());
            tvSubtitle.setText(currentOrg.getType());
            tvDescription.setText(currentOrg.getDescription());

            // Setear imágenes
            imgLogo.setImageResource(currentOrg.getLogoResId());
            imgHeader.setImageResource(currentOrg.getHeaderResId());

            // Setear estadísticas
            tvStatActivities.setText(String.valueOf(orgActivities.size()));
            tvStatVolunteers.setText(String.valueOf(currentOrg.getVolunteersCount()));
            tvStatRating.setText(String.valueOf(currentOrg.getRating()));
        }

        // Configurar RecyclerView con tarjetas pequeñas
        ActivitiesAdapter adapter = new ActivitiesAdapter(orgActivities, ActivitiesAdapter.TYPE_SMALL_CARD, null);
        rvActivities.setLayoutManager(new LinearLayoutManager(this));
        rvActivities.setAdapter(adapter);
    }

    private void setupButtons() {
        btnBack.setOnClickListener(v -> finish());

        btnContact.setOnClickListener(v -> {
            if (currentOrg == null) return;

            String subject = "Consulta VoluntariadoApp: " + currentOrg.getName();
            Intent intent = new Intent(Intent.ACTION_SENDTO);
            intent.setData(Uri.parse("mailto:"));
            intent.putExtra(Intent.EXTRA_EMAIL, new String[]{currentOrg.getEmail()});
            intent.putExtra(Intent.EXTRA_SUBJECT, subject);

            try {
                startActivity(intent);
            } catch (Exception e) {
                Toast.makeText(this, "No se encontró aplicación de correo", Toast.LENGTH_SHORT).show();
            }
        });
    }
}