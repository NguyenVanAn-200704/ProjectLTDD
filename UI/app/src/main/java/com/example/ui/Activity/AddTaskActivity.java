package com.example.ui.Activity;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.ui.Enum.TaskPriority;
import com.example.ui.R;

import java.util.Arrays;
import java.util.Calendar;

public class AddTaskActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_task);

        loadSpinner();
        pickDateEvent();



    }

    void pickDateEvent(){
        EditText edtTaskDue = findViewById(R.id.edtTaskDue);

        edtTaskDue.setOnClickListener(v -> {
            final Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    AddTaskActivity.this,
                    (view, selectedYear, selectedMonth, selectedDay) -> {
                        // Format lại ngày
                        String date = selectedDay + "/" + (selectedMonth + 1) + "/" + selectedYear;
                        edtTaskDue.setText(date);
                    },
                    year, month, day
            );
            datePickerDialog.show();
        });

    }

    void loadSpinner(){
        Spinner spinner = findViewById(R.id.spinnerTaskPriority);

        String[] priorities = Arrays.stream(TaskPriority.values())
                .map(Enum::name)
                .toArray(String[]::new);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, priorities);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selected = (String) parent.getItemAtPosition(position);
                TaskPriority priority = TaskPriority.valueOf(selected);
                // Làm gì đó với priority
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Không chọn gì
            }
        });

    }

}