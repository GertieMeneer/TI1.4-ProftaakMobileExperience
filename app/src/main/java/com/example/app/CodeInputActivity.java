package com.example.app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class CodeInputActivity extends AppCompatActivity {
    private TextView codeInput;

    @Override
    protected void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        setContentView(R.layout.activity_code_input);
        codeInput=findViewById(R.id.codeField);
    }

    public void onCodeInputButton(View v) {
        if(codeInput.getText().toString().equals("7385")) {
            if(!Info.unlockedAnimals.contains("pig")) {
                LED_MatrixActivity.currentAnimal = "pig";
                Intent intent = new Intent(CodeInputActivity.this, LED_MatrixActivity.class);
                startActivity(intent);
                finish();
            } else {
                Toast toast = Toast.makeText(this, "Je hebt dit dier al ontgrendeld!", Toast.LENGTH_SHORT);
                toast.show();
            }
        } else {
            Toast toast = Toast.makeText(this, "De code is fout!", Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    public void onBackButton(View v) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
