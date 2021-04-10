package com.example.blogtown;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;
import java.util.Map;

public class Profile_Activity extends AppCompatActivity {

    private Toolbar profile_Toolbar;
    private ImageView profile_image;
    private Uri mainImageURI=null;
    private EditText profile_name;
    private Button setup_btn;
    private StorageReference storageReference;
    private FirebaseAuth firebaseAuth;
    private ProgressBar profile_progressBar;
    private FirebaseFirestore firebaseFirestore;
    private String user_id;
    private boolean isChanged = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_);

        firebaseAuth = FirebaseAuth.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        firebaseFirestore = FirebaseFirestore.getInstance();

        profile_Toolbar = findViewById(R.id.profile_toolbar);
        profile_image = (ImageView) findViewById(R.id.profile_image);
        profile_name = (EditText) findViewById(R.id.profile_name);
        setup_btn = (Button) findViewById(R.id.setup_btn);
        profile_progressBar = (ProgressBar) findViewById(R.id.profile_progressBar);

        user_id = firebaseAuth.getCurrentUser().getUid();

        profile_progressBar.setVisibility(View.VISIBLE);
        setup_btn.setEnabled(false);

        firebaseFirestore.collection("Users").document(user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    if(task.getResult().exists()){
                        String name = task.getResult().getString("name");
                        String image = task.getResult().getString("image");
                        mainImageURI = Uri.parse(image);
                        profile_name.setText(name);

                        RequestOptions placeholderRequest = new RequestOptions();
                        placeholderRequest.placeholder(R.drawable.blog_town_profile_photo);
                        Glide.with(Profile_Activity.this).setDefaultRequestOptions(placeholderRequest).load(image).into(profile_image);
                    }
                }else{
                    String errorMessage = task.getException().getMessage();
                    Toast.makeText(Profile_Activity.this, "FireStore Error: "+errorMessage, Toast.LENGTH_LONG).show();
                }
                profile_progressBar.setVisibility(View.INVISIBLE);
                setup_btn.setEnabled(true);
            }
        });

        setSupportActionBar(profile_Toolbar);
        getSupportActionBar().setTitle("Profile Setup");

        profile_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                    if(ContextCompat.checkSelfPermission(Profile_Activity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                        Toast.makeText(Profile_Activity.this, "Permission Denied", Toast.LENGTH_SHORT).show();
                        ActivityCompat.requestPermissions(Profile_Activity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},1);
                    }else{
                        CropImage.activity()
                                .setGuidelines(CropImageView.Guidelines.ON)
                                .setAspectRatio(1,1)
                                .start(Profile_Activity.this);
                    }
                }else{
                    CropImage.activity()
                            .setGuidelines(CropImageView.Guidelines.ON)
                            .setAspectRatio(1,1)
                            .start(Profile_Activity.this);
                }
            }
        });

        setup_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = profile_name.getText().toString();
                if (!TextUtils.isEmpty(username)) {
                    profile_progressBar.setVisibility(View.VISIBLE);
                    if (isChanged) {
                        StorageReference image_path = storageReference.child("Profile_Images").child(user_id + ".jpg");
                        image_path.putFile(mainImageURI).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                if (task.isSuccessful()) {
                                    storeFirestore(task, username);
                                } else {
                                    String errorMessage = task.getException().getMessage();
                                    Toast.makeText(Profile_Activity.this, "Image Error: " + errorMessage, Toast.LENGTH_LONG).show();
                                    profile_progressBar.setVisibility(View.INVISIBLE);
                                }
                            }
                        });
                    } else {
                        storeFirestore(null, username);
                    }
                }
            }
        });
    }

    private void storeFirestore(@NonNull Task<UploadTask.TaskSnapshot> task, String username) {
        Uri download_uri;
        if(task!=null){
            download_uri = task.getResult().getUploadSessionUri();
        }else{
            download_uri = mainImageURI;
        }
        Map<String,String> userMap = new HashMap<>();
        userMap.put("name", username);
        userMap.put("image",download_uri.toString());

        firebaseFirestore.collection("Users").document(user_id).set(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(Profile_Activity.this, "The user profile has been updated", Toast.LENGTH_LONG).show();
                    Intent mainIntent = new Intent(Profile_Activity.this, MainActivity.class);
                    startActivity(mainIntent);
                    finish();
                }else{
                    String errorMessage = task.getException().getMessage();
                    Toast.makeText(Profile_Activity.this, "FireStore Error: "+errorMessage, Toast.LENGTH_LONG).show();
                }
                profile_progressBar.setVisibility(View.INVISIBLE);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                mainImageURI = result.getUri();
                profile_image.setImageURI(mainImageURI);
                isChanged = true;
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }
}