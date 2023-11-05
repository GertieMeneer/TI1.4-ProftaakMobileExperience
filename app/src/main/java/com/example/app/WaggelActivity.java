package com.example.app;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class WaggelActivity extends AppCompatActivity {
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
        setContentView(R.layout.activity_waggels_story);
        TextView textviewwaggel = findViewById(R.id.textView19);

        textviewwaggel.setMovementMethod(new ScrollingMovementMethod());
    }

    public void onBackbutton(View v) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

}
