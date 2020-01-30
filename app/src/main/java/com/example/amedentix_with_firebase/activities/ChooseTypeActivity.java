package com.example.amedentix_with_firebase.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.amedentix_with_firebase.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChooseTypeActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private FirebaseFirestore mFirestore;
    private StorageReference mStorageReference;
    private String currentUser;
    private ImageButton doctor;
    private ImageButton patient;
    private String type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_type);

        mAuth = FirebaseAuth.getInstance();
        mFirestore = FirebaseFirestore.getInstance();
        mStorageReference = FirebaseStorage.getInstance().getReference();
        currentUser = mAuth.getCurrentUser().getUid();

        doctor = (ImageButton) findViewById(R.id.doctor);
        patient = (ImageButton) findViewById(R.id.patient);

        doctor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setTpye("doctor");
            }
        });

        patient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setTpye("patient");
            }
        });
    }

    private void setTpye(String type) {
        Map<String,Object> user = new HashMap<>();
        user.put("type",type);

        mFirestore.collection("Users").document(currentUser).update(user).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful())
                    startActivity(new Intent(ChooseTypeActivity.this,HistoriesActivity.class));
                else
                    Toast.makeText(getApplicationContext(),"Error: "+task.getException().getMessage(),Toast.LENGTH_LONG).show();
            }
        });
    }
}
