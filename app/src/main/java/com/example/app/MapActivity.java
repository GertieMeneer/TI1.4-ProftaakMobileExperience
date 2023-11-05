package com.example.app;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

public class MapActivity extends AppCompatActivity {
    private ImageView mapView;
    private Button button;
    private boolean zoomed = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
    }

    public void openMainActivity(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    public void onEnlargeButton(View v) {
        mapView = findViewById(R.id.mapImageView);
        button = findViewById(R.id.enlargeButton);
        if (zoomed) {
            mapView.setImageResource(R.drawable.esstelingmap);
            button.setText("Groter Maken");
            zoomed = false;
        } else {
            mapView.setImageResource(R.drawable.esstelingmapzoom);
            button.setText("Kleiner Maken");
            zoomed = true;
        }
    }
}