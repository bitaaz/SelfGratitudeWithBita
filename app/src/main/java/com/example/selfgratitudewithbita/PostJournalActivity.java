package com.example.selfgratitudewithbita;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.selfgratitudewithbita.model.Journal;
import com.example.selfgratitudewithbita.util.JournalApi;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Date;
import java.util.Objects;

public class PostJournalActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "PostJournal";
    private static final int GALLERY_CODE = 1;
    private ImageView backgroundImage;
    private ImageView cameraImage;
    private EditText title;
    private EditText thought;
    private ProgressBar progressBar;
    private Button saveButton;
    private TextView currentUserText;

    private String currentUserId;
    private String currentUsername;

    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser currentUser;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private StorageReference storageReference;
    private CollectionReference collectionReference = db.collection("Journal");

    private Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_journal);

        Objects.requireNonNull(getSupportActionBar()).setElevation(0);


        storageReference = FirebaseStorage.getInstance().getReference();
        firebaseAuth = FirebaseAuth.getInstance();

        backgroundImage = findViewById(R.id.post_journal_image);
        cameraImage = findViewById(R.id.camera_image);
        title = findViewById(R.id.post_journal_title);
        thought = findViewById(R.id.post_journal_thoughts);
        progressBar = findViewById(R.id.post_journal_progressbar);
        saveButton = findViewById(R.id.save_button);
        currentUserText = findViewById(R.id.username_post_journal);

        saveButton.setOnClickListener(this);
        cameraImage.setOnClickListener(this);


        Bundle bundle = getIntent().getExtras();
        if (bundle != null){

            currentUserText.setText(bundle.getString("username"));
            currentUserId = bundle.getString("userId");
            currentUsername = bundle.getString("username");
        }




        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                currentUser = firebaseAuth.getCurrentUser();

                if (currentUser != null) {

                }
                else {

                }
            }
        };


    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){

            case R.id.camera_image:
                Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, GALLERY_CODE);
                break;

            case R.id.save_button:
                saveJournal();
                break;
        }

    }

    private void saveJournal() {
        String title_str = title.getText().toString().trim();
        String thought_str = thought.getText().toString().trim();

        progressBar.setVisibility(View.VISIBLE);

        if (!TextUtils.isEmpty(title_str) && !TextUtils.isEmpty(thought_str)
                && imageUri != null){
            StorageReference filePath = storageReference
                    .child("journal_images")
                    .child("my_image_" + Timestamp.now().getSeconds());
            filePath.putFile(imageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressBar.setVisibility(View.INVISIBLE);

                            filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {

                                    Journal journal = new Journal();
                                    journal.setTitle(title_str);
                                    journal.setThought(thought_str);
                                    journal.setImageUrl(uri.toString());
                                    journal.setUsername(currentUsername);
                                    journal.setUserId(currentUserId);
                                    journal.setDateCreated(new Timestamp(new Date()));

                                    collectionReference.add(journal)
                                            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                                @Override
                                                public void onSuccess(DocumentReference documentReference) {
                                                    progressBar.setVisibility(View.INVISIBLE);
                                                    Intent intent = new Intent(PostJournalActivity.this, JournalListActivity.class);
                                                    intent.putExtra("username", currentUsername);
                                                    intent.putExtra("userId", currentUserId);
                                                    startActivity(intent);
                                                    finish();

                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.d(TAG, "onFailure: " + e.getMessage());

                                        }
                                    });


                                }
                            });

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                }
            });

        }else {
            progressBar.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_CODE && resultCode == RESULT_OK){
            if (data != null){
                imageUri = data.getData();
                backgroundImage.setImageURI(imageUri);
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        currentUser = firebaseAuth.getCurrentUser();
        firebaseAuth.addAuthStateListener(authStateListener);
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (firebaseAuth != null){
            firebaseAuth.removeAuthStateListener(authStateListener);
        }
    }
}