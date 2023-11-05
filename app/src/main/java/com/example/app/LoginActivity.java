package com.example.app;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;

public class LoginActivity extends AppCompatActivity {
    private MediaPlayer mp;

    private Button submitButton;
    private TextInputEditText textNameInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        submitButton = findViewById(R.id.submitButton);
        textNameInput = findViewById(R.id.textNameInput);

        mp = MediaPlayer.create(this, R.raw.androide);
        mp.start();
    }

    public void onSubmitButton(View v) {
        if (String.valueOf(textNameInput.getText()).equals("")) {
            Toast toast = Toast.makeText(this, "Vergeet je naam niet in te vullen!", Toast.LENGTH_LONG);
            toast.show();
        } else {
            Info.username = String.valueOf(textNameInput.getText());
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }
}
