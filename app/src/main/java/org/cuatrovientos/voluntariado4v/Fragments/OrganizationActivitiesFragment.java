package org.cuatrovientos.voluntariado4v.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import org.cuatrovientos.voluntariado4v.Activities.DetailActivity;
import org.cuatrovientos.voluntariado4v.Adapters.ActivitiesAdapter;
import org.cuatrovientos.voluntariado4v.App.MockDataProvider;
import org.cuatrovientos.voluntariado4v.R;

public class OrganizationActivitiesFragment extends Fragment {

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_organization_activities, container, false);

        RecyclerView rv = root.findViewById(R.id.rvAllActivities);
        rv.setLayoutManager(new LinearLayoutManager(getContext()));

        // Cargar TODAS las actividades (null filter)
        ActivitiesAdapter adapter = new ActivitiesAdapter(MockDataProvider.getOrgActivitiesByStatus(null), ActivitiesAdapter.TYPE_SMALL_CARD, (item, pos) -> {
            Intent intent = new Intent(getContext(), DetailActivity.class);
            intent.putExtra("activity_data", item);
            startActivity(intent);
        });
        rv.setAdapter(adapter);

        return root;
    }
}