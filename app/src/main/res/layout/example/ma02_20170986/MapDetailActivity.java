package com.example.ma02_20170986;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MapDetailActivity extends AppCompatActivity {

    EditText etName;
    EditText etPhone;
    EditText etAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_detail);

        Intent intent = getIntent();

        etName = findViewById(R.id.etName);
        etPhone = findViewById(R.id.etPhone);
        etAddress = findViewById(R.id.etAddress);

        etName.setText(intent.getStringExtra("name"));
        etPhone.setText(intent.getStringExtra("phone"));
        etAddress.setText(intent.getStringExtra("address"));
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnSave:
                Toast.makeText(this, "Save Place information", Toast.LENGTH_SHORT).show();
                break;
            case R.id.btnClose:
                finish();
                break;
        }
    }
}
