package org.cuatrovientos.voluntariado4v.Activities;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import org.cuatrovientos.voluntariado4v.API.ApiClient;
import org.cuatrovientos.voluntariado4v.API.VoluntariadoApiService;
import org.cuatrovientos.voluntariado4v.Adapters.OdsAdminAdapter;
import org.cuatrovientos.voluntariado4v.Dialogs.AddEditOdsDialog;
import org.cuatrovientos.voluntariado4v.Models.OdsResponse;
import org.cuatrovientos.voluntariado4v.R;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ManageOdsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private OdsAdminAdapter adapter;
    private ProgressBar progressBar;
    private TextView txtEmptyView;
    private androidx.appcompat.widget.Toolbar toolbar;
    private VoluntariadoApiService apiService;
    private List<OdsResponse> odsList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_ods);

        // Inicializar API
        apiService = ApiClient.getService();

        initViews();
        setupRecyclerView();

        // Cargar datos iniciales
        loadOds();
    }

    private void initViews() {
        recyclerView = findViewById(R.id.recyclerManageOds);
        progressBar = findViewById(R.id.progressBar);
        txtEmptyView = findViewById(R.id.txtEmptyView);
        FloatingActionButton fab = findViewById(R.id.fabAddOds);
        toolbar = findViewById(R.id.toolbar);

        // Configurar Toolbar
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        // Botón Crear Nuevo -> Abre el Dialog (Popup)
        fab.setOnClickListener(v -> openAddEditDialog(null));
    }

    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Configuramos el adaptador con las acciones de click
        adapter = new OdsAdminAdapter(odsList, new OdsAdminAdapter.OnItemClickListener() {
            @Override
            public void onEditClick(OdsResponse ods) {
                // EDITAR: Abrimos el Dialog pasando el objeto ODS
                openAddEditDialog(ods);
            }

            @Override
            public void onDeleteClick(OdsResponse ods) {
                // ELIMINAR: Pedimos confirmación antes
                confirmDelete(ods);
            }
        });

        recyclerView.setAdapter(adapter);
    }

    /**
     * Método para abrir el Popup de Crear/Editar
     */
    private void openAddEditDialog(OdsResponse ods) {
        // Creamos una instancia del Dialog usando el método estático
        AddEditOdsDialog dialog = AddEditOdsDialog.newInstance(ods);

        // Asignamos el listener para recargar la lista cuando se guarde correctamente
        dialog.setListener(() -> {
            loadOds(); // Recargar la lista para ver los cambios
        });

        // Mostramos el Dialog
        dialog.show(getSupportFragmentManager(), "AddEditOdsDialog");
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadOds();
    }

    private void loadOds() {
        progressBar.setVisibility(View.VISIBLE);
        txtEmptyView.setVisibility(View.GONE);

        Call<List<OdsResponse>> call = apiService.getOds();
        call.enqueue(new Callback<List<OdsResponse>>() {
            @Override
            public void onResponse(Call<List<OdsResponse>> call, Response<List<OdsResponse>> response) {
                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    List<OdsResponse> results = response.body();
                    odsList.clear();
                    odsList.addAll(results);

                    if (odsList.isEmpty()) {
                        txtEmptyView.setVisibility(View.VISIBLE);
                    } else {
                        txtEmptyView.setVisibility(View.GONE); // Aseguramos ocultarlo si hay datos
                        adapter.updateList(odsList);
                    }
                } else {
                    Toast.makeText(ManageOdsActivity.this, "Error al cargar datos", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<OdsResponse>> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(ManageOdsActivity.this, "Error de conexión", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void confirmDelete(OdsResponse ods) {
        new AlertDialog.Builder(this)
                .setTitle("Eliminar ODS")
                .setMessage("¿Estás seguro de que deseas eliminar '" + ods.getNombre() + "'?\nEsta acción no se puede deshacer.")
                .setPositiveButton("Eliminar", (dialog, which) -> deleteOds(ods.getId()))
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void deleteOds(int id) {
        progressBar.setVisibility(View.VISIBLE);

        Call<Void> call = apiService.deleteOds(id);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful()) {
                    Toast.makeText(ManageOdsActivity.this, "ODS eliminada", Toast.LENGTH_SHORT).show();
                    loadOds(); // Recargar la lista para quitar el elemento borrado
                } else {
                    Toast.makeText(ManageOdsActivity.this, "No se pudo eliminar: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(ManageOdsActivity.this, "Error de red", Toast.LENGTH_SHORT).show();
            }
        });
    }
}