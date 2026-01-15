package org.cuatrovientos.voluntariado4v.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.cuatrovientos.voluntariado4v.Adapters.DashboardOrganizationsAdapter;
import org.cuatrovientos.voluntariado4v.App.MockDataProvider;
import org.cuatrovientos.voluntariado4v.R;

public class UserHomeFragment extends Fragment {

    public UserHomeFragment() {
        // Constructor vacío requerido
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflamos el layout que contiene el diseño del Home
        return inflater.inflate(R.layout.fragment_user_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // LÓGICA MIGRA DE UserDashboard.java:
        // Configurar RecyclerView Top Organizaciones
        // Nota: usamos view.findViewById porque estamos en un Fragment
        RecyclerView rvTopOrgs = view.findViewById(R.id.rvTopOrganizations);

        if (rvTopOrgs != null) {
            // Usamos getContext() o requireContext() para el LayoutManager
            rvTopOrgs.setLayoutManager(new LinearLayoutManager(getContext()));

            // Cargamos los datos del MockDataProvider tal como hacías antes
            DashboardOrganizationsAdapter adapter = new DashboardOrganizationsAdapter(MockDataProvider.getTopOrganizations());
            rvTopOrgs.setAdapter(adapter);
        }
    }
}