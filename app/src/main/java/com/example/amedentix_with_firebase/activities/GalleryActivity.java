package com.example.amedentix_with_firebase.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.amedentix_with_firebase.R;
import com.example.amedentix_with_firebase.adapters.MediaAdapter;
import com.example.amedentix_with_firebase.models.History;
import com.example.amedentix_with_firebase.models.ImageUrl;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class GalleryActivity extends AppCompatActivity {

    private ImageView imageView;
    private RecyclerView recyclerView;
    private GridLayoutManager gridLayoutManager;
    private FirebaseFirestore firebaseFirestore;
    private ArrayList<ImageUrl> imageUrlList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);
        imageView = (ImageView) findViewById(R.id.imageViewGallery);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerViewGallery);
        gridLayoutManager = new GridLayoutManager(getApplicationContext(), 2);
        recyclerView.setLayoutManager(gridLayoutManager);
        imageUrlList = new ArrayList<ImageUrl>();

        firebaseFirestore = FirebaseFirestore.getInstance();

        Query firstQuery = firebaseFirestore.collection("Posts").orderBy("timestamp", Query.Direction.DESCENDING).limit(3);
        firstQuery.addSnapshotListener(GalleryActivity.this, new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {

                if (!documentSnapshots.isEmpty()) {

                    for (DocumentChange doc : documentSnapshots.getDocumentChanges()) {

                        if (doc.getType() == DocumentChange.Type.ADDED) {

                            String blogPostId = doc.getDocument().getId();
                            History blogPost = doc.getDocument().toObject(History.class).withId(blogPostId);
                            ImageUrl imageUrl = new ImageUrl();
                            imageUrl.setImageUrl(blogPost.getImage_url());
                            imageUrlList.add(imageUrl);
                        }
                    }
                }
                InitializeView();
            }
        });
    }

    private void InitializeView() {
        MediaAdapter dataAdapter = new MediaAdapter(getApplicationContext(), imageUrlList);
        recyclerView.setAdapter(dataAdapter);

    }
}
