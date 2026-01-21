package org.cuatrovientos.voluntariado4v.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import org.cuatrovientos.voluntariado4v.R;

public class CoordinatorHomeFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Por ahora inflamos algo genérico o creamos una vista simple
        // Mas adelante crearemos: R.layout.fragment_coordinator_home
        TextView textView = new TextView(getContext());
        textView.setText("Panel de Estadísticas (Próximamente)");
        textView.setGravity(android.view.Gravity.CENTER);
        return textView;
    }
}