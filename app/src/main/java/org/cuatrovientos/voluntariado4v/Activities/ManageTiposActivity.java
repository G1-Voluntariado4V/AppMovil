package org.cuatrovientos.voluntariado4v.Activities;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView; // Import necesario
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.cuatrovientos.voluntariado4v.API.ApiClient;
import org.cuatrovientos.voluntariado4v.API.VoluntariadoApiService;
import org.cuatrovientos.voluntariado4v.Adapters.TiposAdminAdapter;
import org.cuatrovientos.voluntariado4v.Dialogs.AddEditTipoDialog;
import org.cuatrovientos.voluntariado4v.Models.TipoVoluntariadoResponse;
import org.cuatrovientos.voluntariado4v.R;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ManageTiposActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private TiposAdminAdapter adapter;
    private ProgressBar progressBar;
    private TextView txtEmptyView;
    private androidx.appcompat.widget.Toolbar toolbar;
    private SearchView searchView; // Nuevo componente
    private FloatingActionButton fab;
    private VoluntariadoApiService apiService;

    // Listas para manejar el filtrado
    private List<TipoVoluntariadoResponse> displayList = new ArrayList<>();
    private List<TipoVoluntariadoResponse> fullTiposList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_tipos);

        apiService = ApiClient.getService();

        initViews();
        setupRecyclerView();
        setupSearch(); // Inicializar búsqueda

        loadTipos();
    }

    private void initViews() {
        recyclerView = findViewById(R.id.recyclerManageTipos);
        progressBar = findViewById(R.id.progressBar);
        txtEmptyView = findViewById(R.id.txtEmptyView);
        fab = findViewById(R.id.fabAddTipo);
        toolbar = findViewById(R.id.toolbar);
        searchView = findViewById(R.id.searchView); // Referencia al buscador

        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        fab.setOnClickListener(v -> openAddEditDialog(null));
    }

    private void setupRecyclerView() {
        // Mantenemos LinearLayoutManager (lista vertical) como pediste
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new TiposAdminAdapter(displayList, new TiposAdminAdapter.OnItemClickListener() {
            @Override
            public void onEditClick(TipoVoluntariadoResponse tipo) {
                openAddEditDialog(tipo);
            }

            @Override
            public void onDeleteClick(TipoVoluntariadoResponse tipo) {
                confirmDelete(tipo);
            }
        });

        recyclerView.setAdapter(adapter);
    }

    // Configuración del Listener del Buscador
    private void setupSearch() {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                filter(query);
                searchView.clearFocus();
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
        List<TipoVoluntariadoResponse> filteredList = new ArrayList<>();

        if (text == null || text.trim().isEmpty()) {
            filteredList.addAll(fullTiposList);
        } else {
            String filterPattern = text.toLowerCase().trim();
            for (TipoVoluntariadoResponse item : fullTiposList) {
                if (item.getNombre() != null && item.getNombre().toLowerCase().contains(filterPattern)) {
                    filteredList.add(item);
                }
            }
        }

        // Actualizamos el adaptador con la lista filtrada
        adapter.updateList(filteredList);

        // Controlamos el mensaje de "Sin resultados"
        if (filteredList.isEmpty()) {
            txtEmptyView.setVisibility(View.VISIBLE);
            txtEmptyView.setText("No se encontraron resultados");
        } else {
            txtEmptyView.setVisibility(View.GONE);
        }
    }

    private void openAddEditDialog(TipoVoluntariadoResponse tipo) {
        AddEditTipoDialog dialog = AddEditTipoDialog.newInstance(tipo);
        dialog.setListener(this::loadTipos);
        dialog.show(getSupportFragmentManager(), "AddEditTipoDialog");
    }

    private void loadTipos() {
        progressBar.setVisibility(View.VISIBLE);
        txtEmptyView.setVisibility(View.GONE);

        Call<List<TipoVoluntariadoResponse>> call = apiService.getTiposVoluntariado();
        call.enqueue(new Callback<List<TipoVoluntariadoResponse>>() {
            @Override
            public void onResponse(Call<List<TipoVoluntariadoResponse>> call, Response<List<TipoVoluntariadoResponse>> response) {
                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    List<TipoVoluntariadoResponse> results = response.body();

                    // Guardamos la lista completa original
                    fullTiposList.clear();
                    fullTiposList.addAll(results);

                    // Aplicamos el filtro (si el usuario ya escribió algo) o mostramos todo
                    filter(searchView.getQuery().toString());

                } else {
                    Toast.makeText(ManageTiposActivity.this, "Error al cargar datos", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<TipoVoluntariadoResponse>> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(ManageTiposActivity.this, "Error de conexión", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void confirmDelete(TipoVoluntariadoResponse tipo) {
        new AlertDialog.Builder(this)
                .setTitle("Eliminar Tipo")
                .setMessage("¿Estás seguro de que deseas eliminar '" + tipo.getNombre() + "'?\nEsta acción no se puede deshacer.")
                .setPositiveButton("Eliminar", (dialog, which) -> deleteTipo(tipo.getId()))
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void deleteTipo(int id) {
        progressBar.setVisibility(View.VISIBLE);
        Call<Void> call = apiService.deleteTipoVoluntariado(id);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful()) {
                    Toast.makeText(ManageTiposActivity.this, "Tipo eliminado", Toast.LENGTH_SHORT).show();
                    loadTipos();
                } else {
                    Toast.makeText(ManageTiposActivity.this, "No se pudo eliminar", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(ManageTiposActivity.this, "Error de red", Toast.LENGTH_SHORT).show();
            }
        });
    }
}