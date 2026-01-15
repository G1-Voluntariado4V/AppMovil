package org.cuatrovientos.voluntariado4v.Activities;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.cuatrovientos.voluntariado4v.App.NavigationUtils;
import org.cuatrovientos.voluntariado4v.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;

public class UserProfile extends AppCompatActivity {

    private EditText etFechaNacimiento;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_user_profile); // Asegúrate de que coincida con el nombre del XML

        initViews();
        setupNavigation();
        setupLanguages();
        setupDatePicker(); // NUEVO: Configurar el calendario
    }

    private void initViews() {
        etFechaNacimiento = findViewById(R.id.etFechaNacimiento);
    }

    private void setupDatePicker() {
        etFechaNacimiento.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        UserProfile.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int selectedYear, int selectedMonth, int selectedDay) {
                                // Formatear: dia/mes/año
                                String selectedDate = selectedDay + "/" + (selectedMonth + 1) + "/" + selectedYear;
                                etFechaNacimiento.setText(selectedDate);
                            }
                        },
                        year, month, day
                );
                // Opcional: limitar fecha máxima a hoy
                datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
                datePickerDialog.show();
            }
        });
    }

    private void setupLanguages() {
        // ... (Tu código existente para idiomas, si lo tienes)
    }

    private void setupNavigation() {
        BottomNavigationView bottomNav = findViewById(R.id.bottomNavigation);
        // Asegúrate de usar tu NavigationUtils si lo tienes implementado
        NavigationUtils.setupNavigation(this, bottomNav, R.id.nav_profile);
    }
}