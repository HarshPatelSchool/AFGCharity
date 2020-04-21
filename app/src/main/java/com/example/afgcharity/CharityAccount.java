package com.example.afgcharity;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class CharityAccount extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
    private DatabaseReference  ref=reference.child("users").child(MainActivity.user.getUid()).child("Items");
    private ArrayList<String> Userlist;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private StorageReference mStorageRef;
    private ImageView profilepic;
    private File localFile;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        mStorageRef = FirebaseStorage.getInstance().getReference().child("logos/"+MainActivity.user.getUid());
        setContentView(R.layout.view_charity_profile);
        TextView name=findViewById(R.id.charity_name);
        profilepic=findViewById((R.id.charity_logo));

        localFile = null;
        try {
            localFile = File.createTempFile("logo", "jpg");
        } catch (IOException e) {
            e.printStackTrace();
        }
        mStorageRef.getFile(localFile)
                .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {

                        profilepic.setImageURI(Uri.parse(localFile.getPath()));


                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {

                profilepic.setImageDrawable(getDrawable(R.drawable.default_logo));


            }
        });

        name.setText(MainActivity.user.getDisplayName());
        reference.child("users").child(MainActivity.user.getUid());
        Userlist = new ArrayList<String>();
        getList();

    }
    public void test(View v){
        Random r = new Random();
        DatabaseReference  ref=reference.child("users").child(MainActivity.user.getUid()).child("Items").push();

        ref.child("Clothing").setValue("T-Shirt");
        ref.child("Number").setValue(r.nextInt(1000));

   getList();
    }
    private void getList(){
        reference.child("users").child(MainActivity.user.getUid()).child("Items").addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Userlist = new ArrayList<String>();
                        // Result will be holded Here
                        for (DataSnapshot dsp : dataSnapshot.getChildren()) {
                            Userlist.add(String.valueOf(dsp.getValue())); //add result into array list
                        }
                        mAdapter = new MyAdapter(Userlist);
                        recyclerView= findViewById(R.id.charity_profile_locations_list);
                        recyclerView.setHasFixedSize(true);
                        layoutManager = new LinearLayoutManager(getBaseContext());
                        recyclerView.setLayoutManager(layoutManager);

                        recyclerView.setAdapter(mAdapter);
                        Toast.makeText(getBaseContext(), "Amount: "+mAdapter.getItemCount(),
                                Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                }
        );
    }
}
