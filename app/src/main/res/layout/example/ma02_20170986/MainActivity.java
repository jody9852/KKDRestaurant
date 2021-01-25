package com.example.ma02_20170986;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.clickMap :
                Intent mapIntent = new Intent(this, MapActivity.class);
                startActivity(mapIntent);
                break;
            case R.id.clickAdd :
                Intent addIntent = new Intent(this, AddReviewActivity.class);
                startActivity(addIntent);
                break;
        }
    }
}
