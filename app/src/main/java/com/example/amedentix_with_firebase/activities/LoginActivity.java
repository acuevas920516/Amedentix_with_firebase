package com.example.amedentix_with_firebase.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.amedentix_with_firebase.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    private Button login;
    private static final int RC_SIGN_IN = 1;
    private GoogleSignInOptions gso;
    private GoogleSignInClient mGoogleSignInClient;

    private EditText email;
    private EditText password;

    private FirebaseAuth mAuth;
    private SignInButton mGoogleButton;
    private static final String TAG = "firebase";
    private ProgressBar progressBar;
    private FirebaseFirestore firebaseFirestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        login = findViewById(R.id.acceder);

        email = findViewById(R.id.email);
        password = findViewById(R.id.passw);
        progressBar = (ProgressBar) findViewById(R.id.progess_bar_login);
        progressBar.setIndeterminate(true);

        mAuth = FirebaseAuth.getInstance();

        firebaseFirestore = FirebaseFirestore.getInstance();
        mGoogleButton = (SignInButton) findViewById(R.id.sign_in_button);

        mGoogleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginWithGoogle();
            }
        });
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startLogin();
            }
        });
    }

    private void startLogin() {
        final String emailTxt = email.getText().toString().trim();
        final String passwordTxt = password.getText().toString().trim();

        if (emailTxt.isEmpty()) {
            email.setError(getString(R.string.email_null_txt));
            email.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(emailTxt).matches()) {
            email.setError(getString(R.string.email_error_txt));
            email.requestFocus();
            return;
        }

        if (passwordTxt.isEmpty()) {
            password.setError(getString(R.string.passw_null_txt));
            password.requestFocus();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);

        mAuth.signInWithEmailAndPassword(emailTxt, passwordTxt)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressBar.setVisibility(View.INVISIBLE);
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("firebase", "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
//                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("firebase", "signInWithEmail:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed." + task.getException().getMessage(),
                                    Toast.LENGTH_SHORT).show();
//                            updateUI(null);
                        }
                    }
                });

    }

    private void loginWithGoogle() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        progressBar.setVisibility(View.VISIBLE);
        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);

            // Signed in successfully, show authenticated UI.
            //TODO llamar a la vista histories
            firebaseAuthWithGoogle(account);
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w("Google sign-in", "signInResult:failed code=" + e.getStatusCode());
            e.getStackTrace();
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressBar.setVisibility(View.INVISIBLE);
                        if (task.isSuccessful()) {

                            String personName = null;
                            String personEmail = null;
                            // Sign in success, update UI with the signed-in user's information
                            final String user_id = task.getResult().getUser().getUid();
                            GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(getApplicationContext());
                            if (acct != null) {
                                personName = acct.getDisplayName();
                                personEmail = acct.getEmail();
                            }
                            Log.d(TAG, "signInWithCredential:success");
                            final String finalPersonName = personName;
                            final String finalPersonEmail = personEmail;
                            firebaseFirestore.collection("Users").document(user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                                    if(task.isSuccessful()){

                                        if(task.getResult().exists()){
                                            DocumentSnapshot document = task.getResult();
                                            if (document.getData().containsKey("type")) {
                                                startActivity(new Intent(LoginActivity.this, HistoriesActivity.class));
                                                finish();
                                            }else {
                                                startActivity(new Intent(LoginActivity.this, ChooseTypeActivity.class));
                                                finish();
                                            }
                                        }else {
                                            Map<String,String> map = new HashMap<>();
                                            map.put("name", finalPersonName);
                                            map.put("email", finalPersonEmail);
                                            firebaseFirestore.collection("Users").document(user_id).set(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        startActivity(new Intent(LoginActivity.this, ChooseTypeActivity.class));
                                                        finish();
                                                    }
                                                }
                                            });
                                        }

                                    } else {

                                        String error = task.getException().getMessage();
                                        Toast.makeText(LoginActivity.this, "(FIRESTORE Retrieve Error) : " + error, Toast.LENGTH_LONG).show();

                                    }

                                }
                            });
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                        }
                    }
                });
    }
}
