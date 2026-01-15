package org.cuatrovientos.voluntariado4v.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import org.cuatrovientos.voluntariado4v.Activities.DetailActivity;
import org.cuatrovientos.voluntariado4v.Adapters.ActivitiesAdapter;
import org.cuatrovientos.voluntariado4v.App.MockDataProvider;
import org.cuatrovientos.voluntariado4v.Models.ActivityModel;
import org.cuatrovientos.voluntariado4v.Models.OrganizationModel;
import org.cuatrovientos.voluntariado4v.R;
import java.util.ArrayList;

public class OrganizationHomeFragment extends Fragment {

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_organization_home, container, false);

        // 1. Obtener datos
        OrganizationModel currentOrg = MockDataProvider.getCurrentOrgProfile();
        int activeCount = MockDataProvider.getActiveActivitiesCount();
        int volCount = currentOrg.getVolunteersCount();

        // 2. Vincular vistas
        TextView tvName = root.findViewById(R.id.tvOrgWelcome);
        ImageView ivLogo = root.findViewById(R.id.ivOrgLogoHeader);
        TextView tvActive = root.findViewById(R.id.tvStatsActive);
        TextView tvVols = root.findViewById(R.id.tvStatsVolunteers);
        Button btnCreate = root.findViewById(R.id.btnCreateActivity);
        View btnEdit = root.findViewById(R.id.btnEditProfile); // Ahora es ImageButton, usamos View para simplificar
        RecyclerView rv = root.findViewById(R.id.rvActiveActivities);

        // 3. Asignar datos
        tvName.setText(currentOrg.getName());
        ivLogo.setImageResource(currentOrg.getLogoResId());
        tvActive.setText(String.valueOf(activeCount));
        tvVols.setText(String.valueOf(volCount));

        // 4. Lista Actividades (Solo ACTIVAS)
        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        ArrayList<ActivityModel> activeList = MockDataProvider.getOrgActivitiesByStatus("ACTIVE");

        // Usamos TYPE_SMALL_CARD para que use el layout item_small_card_activity
        ActivitiesAdapter adapter = new ActivitiesAdapter(activeList, ActivitiesAdapter.TYPE_SMALL_CARD, (item, pos) -> {
            Intent intent = new Intent(getContext(), DetailActivity.class);
            intent.putExtra("activity_data", item);
            startActivity(intent);
        });
        rv.setAdapter(adapter);

        // 5. Listeners
        btnCreate.setOnClickListener(v ->
                Toast.makeText(getContext(), "Crear Actividad", Toast.LENGTH_SHORT).show());

        btnEdit.setOnClickListener(v ->
                Toast.makeText(getContext(), "Ir a Editar Perfil", Toast.LENGTH_SHORT).show());

        return root;
    }
}