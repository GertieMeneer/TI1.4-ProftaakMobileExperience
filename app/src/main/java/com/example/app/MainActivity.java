package com.example.app;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onMapButton(View v) {
        Intent intent = new Intent(this, MapActivity.class);
        startActivity(intent);
    }

    public void onWriteButton(View v) {
        Intent intent = new Intent(this, CodeInputActivity.class);
        startActivity(intent);
    }

    public void onCharactersButton(View v) {
        Intent intent = new Intent(this, CharactersActivity.class);
        startActivity(intent);
    }
}