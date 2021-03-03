package com.example.selfgratitudewithbita;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.selfgratitudewithbita.util.JournalApi;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class CreateAccountActivity extends AppCompatActivity {
    private static final String TAG = "Create Account";
    private EditText username;
    private EditText password;
    private AutoCompleteTextView email;
    private Button createAccount;
    private ProgressBar progressBar;

    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser firebaseUser;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    private CollectionReference collectionReference = db.collection("Users");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        Objects.requireNonNull(getSupportActionBar()).setElevation(0);


        username = findViewById(R.id.username_create_account);
        email = findViewById(R.id.email_create_account);
        password = findViewById(R.id.password_create_account);
        createAccount = findViewById(R.id.create_account_button_create_account);
        progressBar = findViewById(R.id.create_account_progressbar);

        firebaseAuth = FirebaseAuth.getInstance();

        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                firebaseUser = firebaseAuth.getCurrentUser();
                if (firebaseUser != null){

                }else {

                }
            }
        };

        createAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(username.getText()) && !TextUtils.isEmpty(email.getText())
                        && !TextUtils.isEmpty(password.getText())){

                    String username_str = username.getText().toString().trim();
                    String email_str = email.getText().toString().trim();
                    String password_str = password.getText().toString().trim();

                    createAccountWithEmail(username_str, email_str, password_str);

                }
                else {
                    Toast.makeText(CreateAccountActivity.this, "Empty Fields Not Allowed", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void createAccountWithEmail(String username_str, String email_str, String password_str) {
        progressBar.setVisibility(View.VISIBLE);

        firebaseAuth.createUserWithEmailAndPassword(email_str, password_str)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            firebaseUser = firebaseAuth.getCurrentUser();
                            assert firebaseUser != null;
                            String userId = firebaseUser.getUid();

                            Map<String, Object> userObj = new HashMap<>();
                            userObj.put("userId", userId);
                            userObj.put("username", username_str);

                            collectionReference.add(userObj)
                                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                        @Override
                                        public void onSuccess(DocumentReference documentReference) {
                                            documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                    if (Objects.requireNonNull(task.getResult()).exists()){
                                                        progressBar.setVisibility(View.INVISIBLE);
                                                        String name = task.getResult().getString("username");

                                                        JournalApi journalApi = new JournalApi();
                                                        journalApi.setUsername(name);
                                                        journalApi.setUserId(userId);

                                                        Intent intent = new Intent(CreateAccountActivity.this, PostJournalActivity.class);
                                                        intent.putExtra("username", journalApi.getUsername());
                                                        intent.putExtra("userId", userId);
                                                        startActivity(intent);

                                                    }
                                                    else {
                                                        progressBar.setVisibility(View.INVISIBLE);
                                                    }


                                                }
                                            });

                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.w(TAG, "Error adding document", e);
                                }
                            });

                        }else {
                            progressBar.setVisibility(View.INVISIBLE);
                        }

                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();


    }
}