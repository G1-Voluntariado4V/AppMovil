package org.cuatrovientos.voluntariado4v;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.ChipGroup;

public class FilterDialog extends DialogFragment {

    public FilterDialog() {
        // Constructor vacío requerido
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_filters, container, false);

        // 1. Localizar componentes
        ImageView btnClose = view.findViewById(R.id.btnClose);
        MaterialButton btnClear = view.findViewById(R.id.btnClear);
        MaterialButton btnApply = view.findViewById(R.id.btnApply);
        ChipGroup chipGroupCategory = view.findViewById(R.id.chipGroupCategory);
        ChipGroup chipGroupDate = view.findViewById(R.id.chipGroupDate);

        // 2. Acción Cerrar (X)
        btnClose.setOnClickListener(v -> dismiss());

        // 3. Acción Borrar Filtros
        btnClear.setOnClickListener(v -> {
            chipGroupCategory.clearCheck(); // Quita selección de categorías
            // Para fecha, volvemos a marcar "Cualquier fecha" (chipAny) por defecto
            chipGroupDate.check(R.id.chipAny);
            Toast.makeText(getContext(), "Filtros reiniciados", Toast.LENGTH_SHORT).show();
        });

        // 4. Acción Aplicar
        btnApply.setOnClickListener(v -> {
            // Aquí iría la lógica para filtrar la lista real
            Toast.makeText(getContext(), "Filtros aplicados", Toast.LENGTH_SHORT).show();
            dismiss();
        });

        // Fondo transparente para ver esquinas redondeadas
        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        }

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        // Ancho del popup al 90% de la pantalla
        Dialog dialog = getDialog();
        if (dialog != null) {
            int width = (int) (getResources().getDisplayMetrics().widthPixels * 0.90);
            dialog.getWindow().setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
    }
}