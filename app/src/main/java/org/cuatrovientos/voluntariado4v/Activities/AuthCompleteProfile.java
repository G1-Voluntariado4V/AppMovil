package org.cuatrovientos.voluntariado4v.Activities;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.textfield.TextInputEditText;

import org.cuatrovientos.voluntariado4v.R;

import java.util.Calendar;

public class AuthCompleteProfile extends AppCompatActivity {

    private TextInputEditText etFechaNacimiento;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_auth_complete_profile);

        // Inicializar vistas
        etFechaNacimiento = findViewById(R.id.etFechaNacimiento);

        // Configurar el DatePicker para la fecha de nacimiento
        setupDatePicker();
    }

    private void setupDatePicker() {
        etFechaNacimiento.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Obtener fecha actual para mostrarla por defecto
                final Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);

                // Crear el diálogo
                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        AuthCompleteProfile.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int selectedYear, int selectedMonth, int selectedDay) {
                                // Formatear la fecha y ponerla en el EditText
                                // Nota: el mes empieza en 0, así que sumamos 1
                                String selectedDate = selectedDay + "/" + (selectedMonth + 1) + "/" + selectedYear;
                                etFechaNacimiento.setText(selectedDate);
                            }
                        },
                        year, month, day
                );

                // Opcional: Limitar fechas (ej. no nacer en el futuro)
                datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());

                datePickerDialog.show();
            }
        });
    }
}