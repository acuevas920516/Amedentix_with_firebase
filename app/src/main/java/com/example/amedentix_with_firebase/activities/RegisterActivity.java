package com.example.amedentix_with_firebase.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.amedentix_with_firebase.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    private EditText email;
    private EditText name;
    private EditText passw;
    private EditText passw_conf;
    private Button acceder;
    private ProgressBar mProgress;
    private FirebaseAuth mAuth;
    private FirebaseFirestore mFirestore;
    private StorageReference mStorageReference;
    private String currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        name= (EditText) findViewById(R.id.name);
        email = (EditText) findViewById(R.id.email);
        passw = (EditText) findViewById(R.id.passw);
        passw_conf = (EditText) findViewById(R.id.passw_confirm);
        mProgress = (ProgressBar) findViewById(R.id.progressBar_register);
        mProgress.setIndeterminate(true);
        acceder = (Button) findViewById(R.id.acceder);
        acceder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                register();
            }
        });

        mAuth = FirebaseAuth.getInstance();
        mFirestore = FirebaseFirestore.getInstance();
        mStorageReference = FirebaseStorage.getInstance().getReference();
    }

    private void register(){
        final String nameVal = name.getText().toString().trim();
        final String emailVal = email.getText().toString().trim();
        final String passwVal = passw.getText().toString().trim();
        final String passw_confVal = passw_conf.getText().toString().trim();

        if (nameVal.isEmpty()) {
            name.setError(getString(R.string.name_null_txt));
        }

        if (emailVal.isEmpty()) {
            email.setError(getString(R.string.email_null_txt));
            email.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(emailVal).matches()) {
            email.setError(getString(R.string.email_error_txt));
            email.requestFocus();
            return;
        }

        if (passwVal.isEmpty()) {
            passw.setError(getString(R.string.passw_null_txt));
            passw.requestFocus();
            return;
        }

        if (passw_confVal.isEmpty()) {
            passw.setError(getString(R.string.passw_conf_null_txt));
            passw.requestFocus();
            return;
        }

        if (!passwVal.equals(passw_confVal)) {
            passw.setError(getString(R.string.passw_conf_null_txt));
            passw_conf.setError(getString(R.string.passw_conf_null_txt));
            passw.requestFocus();
            return;
        }

        mProgress.setVisibility(View.VISIBLE);

        mAuth.createUserWithEmailAndPassword(emailVal,passwVal).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    currentUser = task.getResult().getUser().getUid();
                    Map<String,String> user = new HashMap<>();
                    user.put("name",nameVal);
                    user.put("email",emailVal);
                    mFirestore.collection("Users").document(currentUser).set(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            startActivity(new Intent(RegisterActivity.this, ChooseTypeActivity.class));
                            finish();
                        }
                    });
                }else
                {
                    Toast.makeText(RegisterActivity.this,"Error " + task.getException().getMessage(),Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}
