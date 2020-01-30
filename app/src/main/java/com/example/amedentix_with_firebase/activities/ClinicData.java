package com.example.amedentix_with_firebase.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.amedentix_with_firebase.R;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;

public class ClinicData extends AppCompatActivity {

    private CircleImageView clinicImage;

    private FirebaseFirestore mFirestore;
    private FirebaseAuth mAuth;
    private StorageReference storageReference;
    private Button mSave;
    private EditText mName;
    private EditText mTel;
    private EditText mAddr;
    private EditText mWebs;

    private Bitmap clinicImageFile;
    private Uri mainImageURI = null;
    private ProgressBar clinicPb;

    private String user_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clinic_data);

        mFirestore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        user_id = mAuth.getCurrentUser().getUid();


        clinicImage = findViewById(R.id.clinic_image);

        mSave = (Button) findViewById(R.id.create_clinic);
        clinicPb = (ProgressBar) findViewById(R.id.clinic_progress);
        clinicPb.setIndeterminate(true);
        mName = (EditText) findViewById(R.id.clinic_name);
        mAddr = (EditText) findViewById(R.id.clinic_adddres);
        mTel = (EditText) findViewById(R.id.clinic_tel);
        mWebs = (EditText) findViewById(R.id.clinic_website);
        mainImageURI = null;
        clinicImageFile= null;


        mSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String nameVal = mName.getText().toString().trim();
                final String addressVal = mAddr.getText().toString().trim();
                final String telVal = mTel.getText().toString().trim();
                final String websiteVal = mWebs.getText().toString().trim();
                clinicPb.setVisibility(View.VISIBLE);
                mFirestore.collection("Clinics").whereEqualTo("user_id",user_id).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        for (DocumentSnapshot doc: task.getResult())
                        {
                            final String clinic_id = doc.getId();
                            File newImageFile = new File(mainImageURI.getPath());
                            try {

                                clinicImageFile = new Compressor(ClinicData.this)
                                        .setMaxHeight(125)
                                        .setMaxWidth(125)
                                        .setQuality(50)
                                        .compressToBitmap(newImageFile);

                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                            ByteArrayOutputStream baos = new ByteArrayOutputStream();
                            clinicImageFile.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                            byte[] thumbData = baos.toByteArray();

                            final StorageReference image_path = storageReference.child("clinic_images").child(user_id + ".jpg");
                            UploadTask uploadTask = image_path.putBytes(thumbData);
                            Task<Uri> uriTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                                @Override
                                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                                    if (!task.isSuccessful())
                                        throw task.getException();

                                    return image_path.getDownloadUrl();
                                }
                            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                                @Override
                                public void onComplete(@NonNull Task<Uri> task) {
                                    String download_uri = task.getResult().toString();
                                    if (task.isSuccessful()) {
                                        Map<String, Object> map = new HashMap<>();
                                        map.put("name",nameVal);
                                        map.put("address",addressVal);
                                        map.put("telephone",telVal);
                                        map.put("website",websiteVal);
                                        storeFirestore(download_uri, map, clinic_id);

                                    } else {

                                        String error = task.getException().getMessage();
                                        Toast.makeText(ClinicData.this, "(IMAGE Error) : " + error, Toast.LENGTH_LONG).show();

                                        clinicPb.setVisibility(View.INVISIBLE);

                                    }
                                }
                            });
                        }
                    }
                });
            }
        });

        clinicImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){

                    if(ContextCompat.checkSelfPermission(ClinicData.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){

                        Toast.makeText(ClinicData.this, "Permission Denied", Toast.LENGTH_LONG).show();
                        ActivityCompat.requestPermissions(ClinicData.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);

                    } else {

                        BringImagePicker();

                    }

                } else {

                    BringImagePicker();

                }

            }

        });
    }

    private void storeFirestore(@NonNull String image_uri, Map<String, Object> hashMap, String clinic_id) {

        String download_uri = image_uri;

        hashMap.put("image", download_uri);

        mFirestore.collection("Clinics").document(clinic_id).update(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){

                    Toast.makeText(ClinicData.this, R.string.clinica_creada, Toast.LENGTH_LONG).show();
                    Intent mainIntent = new Intent(ClinicData.this, MainActivity.class);
                    startActivity(mainIntent);
                    finish();

                } else {

                    String error = task.getException().getMessage();
                    Toast.makeText(ClinicData.this, "(FIRESTORE Error) : " + error, Toast.LENGTH_LONG).show();

                }

                clinicPb.setVisibility(View.INVISIBLE);
            }
        });


    }

    private void BringImagePicker() {

        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .setAspectRatio(1, 1)
                .start(ClinicData.this);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {

                mainImageURI = result.getUri();
                clinicImage.setImageURI(mainImageURI);

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {

                Exception error = result.getError();

            }
        }

    }
}
