package com.example.afgcharity;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.FileUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class NewAccount extends AppCompatActivity {
    private StorageReference mStorageRef;
    private FirebaseAuth mAuth;
    private String displayname;
    private String email;
    private boolean test;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mStorageRef= FirebaseStorage.getInstance().getReference();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_chairty_profile);
        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();


    }
    public void makeAccount(View v){
    test = false;
        EditText emailET=findViewById(R.id.enterUsernameCreateProfile);
        EditText passwordET=findViewById(R.id.enterPasswordCreateProfile);
        EditText passwordV=findViewById(R.id.enterConfirmPassword);
        EditText name=findViewById(R.id.enterCharityName);

        email= emailET.getText().toString();
        String password=passwordET.getText().toString();
        String passwordConf=passwordV.getText().toString();
        displayname = name.getText().toString();
        if(password.equals(passwordConf)) {
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {

                                // Sign in success, update UI with the signed-in user's information
                                Toast.makeText(getBaseContext(), "createUserWithEmail:success",
                                        Toast.LENGTH_SHORT).show();

                                FirebaseUser user = mAuth.getCurrentUser();
                                test = true;

                                newAccount(user);

                                transfer();
                            } else {
                                // If sign in fails, display a message to the user.
                                Toast.makeText(getBaseContext(), "createUserWithEmail:failure",
                                        Toast.LENGTH_SHORT).show();
                                // updateUI(null);
                            }

                            // ...
                        }
                    });
        }else{
            Toast.makeText(getBaseContext(), "Confirmation Password Does Not Match",
                    Toast.LENGTH_SHORT).show();
        }
        /*
        if(test=true){
            Intent intent = new Intent(this, CharityLogin.class);
            startActivity(intent);
            test = false;
        }
         */
    }
    private void newAccount(FirebaseUser user){
        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(displayname)
                //.setPhotoUri(Uri.parse("https://example.com/jane-q-user/profile.jpg"))
                .build();

        user.updateProfile(profileUpdates)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(getBaseContext(), "ProfileUpdate:success",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("users").child(user.getUid());
        reference.child("email").setValue(email);
        reference.child("name").setValue(displayname);

    }

    private void transfer(){
        Intent intent = new Intent(this, CharityLogin.class);

        startActivity(intent);
    }
    private static final int FILE_SELECT_CODE = 0;


    public void chooseFile(View v) {
        if((Build.VERSION.SDK_INT>Build.VERSION_CODES.M) && checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)!=
                PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 2);
        }else {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("*/*");
            intent.addCategory(Intent.CATEGORY_OPENABLE);

            try {
                startActivityForResult(
                        Intent.createChooser(intent, "Select a File to Upload"),
                        FILE_SELECT_CODE);
            } catch (android.content.ActivityNotFoundException ex) {
                // Potentially direct the user to the Market with a Dialog
                Toast.makeText(this, "Please install a File Manager.",
                        Toast.LENGTH_SHORT).show();
            }

        }

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case FILE_SELECT_CODE:
                if (resultCode == RESULT_OK) {
                    // Get the Uri of the selected file
                    Uri uri = data.getData();
                    Toast.makeText(getBaseContext(), "File Uri: " + uri.toString(),
                            Toast.LENGTH_SHORT).show();
                    // Get the path
                    String path = uri.getPath();
                    Toast.makeText(getBaseContext(), "File Uri: " + path,
                            Toast.LENGTH_SHORT).show();
                    // Get the file instance
                    // File file = new File(path);
                    // Initiate the upload
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case 2:{
                if (grantResults[0]==PackageManager.PERMISSION_GRANTED)
                    Toast.makeText(getBaseContext(), "Permission Granted",
                            Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(getBaseContext(), "Permission Denied",
                            Toast.LENGTH_SHORT).show();
            }

        }
    }
}