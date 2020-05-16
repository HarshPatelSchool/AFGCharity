package com.example.afgcharity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.util.Linkify;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.util.ArrayList;

public class UserViewsCharityProfile extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
    private ArrayList<Apparel> Userlist;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private StorageReference mStorageRef;
    private ImageView profilepic;
    private TextView description;
    private TextView website;
    private Bundle extras;
    TextView name;
    private File localFile;
    private boolean testing = true;

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        extras = getIntent().getExtras();
        mStorageRef = FirebaseStorage.getInstance().getReference().child("logos/" + extras.getString("User"));
        setContentView(R.layout.user_view_charity_profile);
        name = findViewById(R.id.charity_name);
        description = findViewById(R.id.charityDescription);
        website = findViewById(R.id.website_link_placeholder);

        //Toast.makeText(getBaseContext(), description, Toast.LENGTH_SHORT).show();
        profilepic = findViewById((R.id.charity_logo));
        mStorageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri downloadUrl) {
                Glide.with(getBaseContext())
                        .load(downloadUrl.toString())
                        .placeholder(R.drawable.default_logo)
                        .error(R.drawable.default_logo)
                        .into(profilepic);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                profilepic.setImageResource(R.drawable.default_logo);
            }
        });

        Userlist = new ArrayList<Apparel>();
        getList();
        ActionBar actionBar = getSupportActionBar();
    }

    private void getList() {
        reference.child("users").child(extras.getString("User")).addValueEventListener(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Userlist = new ArrayList<Apparel>();
                        // Result will be held Here
                        name.setText(String.valueOf(dataSnapshot.child("Info").child("name").getValue()));
                        description.setText(String.valueOf(dataSnapshot.child("Info").child("description").getValue()));
                        website.setText(String.valueOf(dataSnapshot.child("Info").child("website").getValue()));
                        Linkify.addLinks(website, Linkify.WEB_URLS);
                        for (DataSnapshot dsp : dataSnapshot.child("Items").getChildren()) {
                            if (String.valueOf(dsp.child("Clothing").getValue()) != null && String.valueOf(dsp.child("Number").getValue()) != null)
                                Userlist.add(new Apparel(extras.getString("User"),
                                        String.valueOf(dsp.child("Clothing").getValue()),
                                        Integer.parseInt(String.valueOf(dsp.child("Number").getValue())),
                                        dsp.getKey(), String.valueOf(dataSnapshot.child("Info").child("name").getValue())));//add result into array list
                        }
                        mAdapter = new MyAdapter(Userlist, getBaseContext());
                        recyclerView = findViewById(R.id.charity_profile_locations_list);
                        recyclerView.setHasFixedSize(true);
                        layoutManager = new LinearLayoutManager(getBaseContext());
                        recyclerView.setLayoutManager(layoutManager);

                        recyclerView.setAdapter(mAdapter);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                }
        );
    }
}