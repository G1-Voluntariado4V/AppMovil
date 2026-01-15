package org.cuatrovientos.voluntariado4v.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import com.google.android.material.textfield.TextInputEditText;
import org.cuatrovientos.voluntariado4v.App.MockDataProvider;
import org.cuatrovientos.voluntariado4v.Models.OrganizationModel;
import org.cuatrovientos.voluntariado4v.R;

public class OrganizationProfileFragment extends Fragment {

    private OrganizationModel org;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_organization_profile, container, false);

        // Vincular vistas
        ImageView imgLogo = root.findViewById(R.id.ivOrgLogoProfile);
        TextInputEditText etName = root.findViewById(R.id.etOrgName);
        TextInputEditText etDesc = root.findViewById(R.id.etOrgDesc);
        TextInputEditText etEmail = root.findViewById(R.id.etOrgEmail);
        TextInputEditText etWeb = root.findViewById(R.id.etOrgWeb);
        TextInputEditText etAddress = root.findViewById(R.id.etOrgAddress);
        Button btnSave = root.findViewById(R.id.btnSaveProfile);

        // Cargar datos actuales
        org = MockDataProvider.getCurrentOrgProfile();
        imgLogo.setImageResource(org.getLogoResId());
        etName.setText(org.getName());
        etDesc.setText(org.getDescription());
        etEmail.setText(org.getEmail());
        etWeb.setText(org.getWebsite());
        etAddress.setText(org.getAddress());

        // Botón Guardar
        btnSave.setOnClickListener(v -> {
            // Actualizar modelo simulado
            org.setName(etName.getText().toString());
            org.setDescription(etDesc.getText().toString());
            org.setEmail(etEmail.getText().toString());
            org.setWebsite(etWeb.getText().toString());
            org.setAddress(etAddress.getText().toString());

            Toast.makeText(getContext(), "Perfil actualizado correctamente", Toast.LENGTH_SHORT).show();
        });

        return root;
    }
}