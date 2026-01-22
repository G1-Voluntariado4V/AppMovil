package org.cuatrovientos.voluntariado4v.Activities;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
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
    private Toolbar toolbar;
    private SearchView searchView;
    private FloatingActionButton fab;
    private VoluntariadoApiService apiService;

    // Listas para manejar el filtrado
    private List<OdsResponse> displayList = new ArrayList<>();
    private List<OdsResponse> fullOdsList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_ods);

        // Inicializar API
        apiService = ApiClient.getService();

        initViews();
        setupRecyclerView();
        setupSearch(); // Configurar el buscador

        // Cargar datos iniciales
        loadOds();
    }

    private void initViews() {
        recyclerView = findViewById(R.id.recyclerManageOds);
        progressBar = findViewById(R.id.progressBar);
        txtEmptyView = findViewById(R.id.txtEmptyView);
        fab = findViewById(R.id.fabAddOds);
        toolbar = findViewById(R.id.toolbar);
        searchView = findViewById(R.id.searchView);

        // Configurar Toolbar
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> finish());

        // Botón Crear Nuevo
        fab.setOnClickListener(v -> openAddEditDialog(null));
    }

    private void setupRecyclerView() {
        // Mantenemos GridLayoutManager con 2 columnas como pediste
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));

        adapter = new OdsAdminAdapter(displayList, new OdsAdminAdapter.OnItemClickListener() {
            @Override
            public void onEditClick(OdsResponse ods) {
                openAddEditDialog(ods);
            }

            @Override
            public void onDeleteClick(OdsResponse ods) {
                confirmDelete(ods);
            }
        });

        recyclerView.setAdapter(adapter);
    }

    private void setupSearch() {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                filter(query);
                searchView.clearFocus(); // Ocultar teclado
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filter(newText);
                return true;
            }
        });
    }

    // Lógica de filtrado
    private void filter(String text) {
        List<OdsResponse> filteredList = new ArrayList<>();

        if (text == null || text.trim().isEmpty()) {
            filteredList.addAll(fullOdsList);
        } else {
            String filterPattern = text.toLowerCase().trim();
            for (OdsResponse item : fullOdsList) {
                if (item.getNombre() != null && item.getNombre().toLowerCase().contains(filterPattern)) {
                    filteredList.add(item);
                }
            }
        }

        // Actualizar adaptador y vista vacía
        adapter.updateList(filteredList);

        if (filteredList.isEmpty()) {
            txtEmptyView.setVisibility(View.VISIBLE);
            txtEmptyView.setText("No se encontraron resultados");
        } else {
            txtEmptyView.setVisibility(View.GONE);
        }
    }

    private void openAddEditDialog(OdsResponse ods) {
        AddEditOdsDialog dialog = AddEditOdsDialog.newInstance(ods);
        dialog.setListener(() -> loadOds());
        dialog.show(getSupportFragmentManager(), "AddEditOdsDialog");
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

                    // Actualizamos la lista maestra y aplicamos el filtro actual
                    fullOdsList.clear();
                    fullOdsList.addAll(results);

                    String currentQuery = searchView.getQuery().toString();
                    filter(currentQuery);

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
                    loadOds();
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