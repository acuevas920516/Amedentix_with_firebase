package com.example.amedentix_with_firebase.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.amedentix_with_firebase.R;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private Button unirme;
    private Button acceder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        acceder = (Button) findViewById(R.id.acceder);
        acceder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                unirmeEvent();
            }
        });

        unirme = (Button) findViewById(R.id.unirme);
        unirme.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,RegisterActivity.class));
            }
        });
        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() != null){
                    startActivity(new Intent(MainActivity.this,HistoriesActivity.class));
                    finish();
                }
            }
        };
    }

    public void unirmeEvent() {
        startActivity(new Intent(MainActivity.this,LoginActivity.class));
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }
}
