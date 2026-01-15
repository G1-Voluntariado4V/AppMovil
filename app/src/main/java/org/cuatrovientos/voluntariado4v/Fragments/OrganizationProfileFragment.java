package org.cuatrovientos.voluntariado4v.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
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
        TextView tvName = root.findViewById(R.id.tvHeaderName);
        TextView tvRole = root.findViewById(R.id.tvOrgRole);
        EditText etName = root.findViewById(R.id.etOrgName);
        EditText etDesc = root.findViewById(R.id.etOrgDesc);
        EditText etEmail = root.findViewById(R.id.etOrgEmail);
        EditText etWeb = root.findViewById(R.id.etOrgWeb);
        EditText etAddress = root.findViewById(R.id.etOrgAddress);

        // Cargar datos actuales
        org = MockDataProvider.getCurrentOrgProfile();
        imgLogo.setImageResource(org.getLogoResId());
        tvName.setText(org.getName());
        etName.setText(org.getName());
        etDesc.setText(org.getDescription());
        etEmail.setText(org.getEmail());
        etWeb.setText(org.getWebsite());
        etAddress.setText(org.getAddress());

        return root;
    }
}