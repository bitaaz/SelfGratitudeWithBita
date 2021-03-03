package com.example.selfgratitudewithbita;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.selfgratitudewithbita.model.Journal;
import com.example.selfgratitudewithbita.ui.RecyclerViewAdapter;
import com.example.selfgratitudewithbita.util.JournalApi;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class JournalListActivity extends AppCompatActivity {

    private RecyclerViewAdapter adapter;
    private RecyclerView recyclerView;

    private FirebaseAuth firebaseAuth;
    private FirebaseUser user;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private StorageReference storageReference;
    private CollectionReference collectionReference = db.collection("Journal");

    private List<Journal> journalList;
    private TextView noJournalEntry;

    private String currentUserId;
    private String currentUsername;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_journal_list);

        Objects.requireNonNull(getSupportActionBar()).setElevation(0);


        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();

        noJournalEntry = findViewById(R.id.list_no_thoughts);
        journalList = new ArrayList<>();

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));


        Bundle bundle = getIntent().getExtras();
        if (bundle != null){

            currentUserId = bundle.getString("userId");
            currentUsername = bundle.getString("username");
        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onStart() {
        super.onStart();

        collectionReference.whereEqualTo("userId", currentUserId)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (!queryDocumentSnapshots.isEmpty()){

                            for (QueryDocumentSnapshot snapshot : queryDocumentSnapshots){

                                Journal journal = snapshot.toObject(Journal.class);
                                journalList.add(journal);

                            }

                            adapter = new RecyclerViewAdapter(JournalListActivity.this, journalList);

                            recyclerView.setAdapter(adapter);
                            adapter.notifyDataSetChanged();
                        }
                        else {
                            noJournalEntry.setVisibility(View.INVISIBLE);
                        }

                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){

            case R.id.add_action:
                Intent intent = new Intent(JournalListActivity.this, PostJournalActivity.class);
                intent.putExtra("username", currentUsername);
                intent.putExtra("userId", currentUserId);
                startActivity(intent);
                finish();
                break;

            case R.id.sign_out:
                if (user != null && firebaseAuth != null){
                    firebaseAuth.signOut();

                    startActivity(new Intent(JournalListActivity.this, MainActivity.class));
                    finish();
                }
                break;




        }


        return super.onOptionsItemSelected(item);
    }
}