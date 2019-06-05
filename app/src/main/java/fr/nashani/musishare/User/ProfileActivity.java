package fr.nashani.musishare.User;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.storage.StorageManager;
import android.os.storage.StorageVolume;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import fr.nashani.musishare.R;

public class ProfileActivity extends Activity {

    private EditText mNameField, mPhoneField;
    private ImageView mProfileImage;
    private Button mBack, mConfirm;

    private DatabaseReference userDB;

    private String userId, name, phone, profileImageURL, userSex;

    private Uri resultUri;

    private static final int MY_PERMISSIONS_REQUEST_READ = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        mNameField = findViewById(R.id.userProfileName);
        mPhoneField = findViewById(R.id.userProfilePhone);
        mProfileImage = findViewById(R.id.userProfileImage);
        mBack = findViewById(R.id.profile_back);
        mConfirm = findViewById(R.id.profile_confirm);

        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        userDB = FirebaseDatabase.getInstance().getReference().child("Users").child(userId);

        getUserInfo();
        mProfileImage.setOnClickListener(v -> {
            isReadFromStoragePermitted();

        });

        mConfirm.setOnClickListener(v -> {
            saveUserInformation();
        });

        mBack.setOnClickListener(v -> {
            finish();
            return;
        });

    }

    private void getUserInfo() {
        userDB.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists() && dataSnapshot.getChildrenCount() > 0){
                    Map<String,Object> map = (Map<String,Object>) dataSnapshot.getValue();

                    if (map.get("name") != null){
                        name = map.get("name").toString();
                        mNameField.setText(name);
                    }
                    if (map.get("phone") != null){
                        name = map.get("phone").toString();
                        mPhoneField.setText(name);
                    }

                    if (map.get("sex") != null){
                        userSex = map.get("sex").toString();
                    }

                    if (map.get("profileImageUrl") != null){
                        profileImageURL = map.get("profileImageUrl").toString();

                        switch (profileImageURL){
                            case "default" : mProfileImage.setImageResource(R.drawable.ic_person_black_24dp);

                                break;

                            default: Glide.with(getApplication()).load(profileImageURL).into(mProfileImage);
                                break;

                        }

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void saveUserInformation(){
        name = mNameField.getText().toString();
        phone = mPhoneField.getText().toString();

        Map<String, Object> userInformation = new HashMap<String, Object>();
        userInformation.put("name",name);
        userInformation.put("phone",phone);

        userDB.updateChildren(userInformation);

        if(resultUri != null){
            StorageReference filepath = FirebaseStorage.getInstance().getReference().child("profileImages").child(userId);
            Bitmap bitmap = null;

            try {
                bitmap = MediaStore.Images.Media.getBitmap(getApplication().getContentResolver(),resultUri);
            } catch (IOException e) {
                e.printStackTrace();
            }

            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 20,  bos);

            byte[] data = bos.toByteArray();

            UploadTask uploadTask = filepath.putBytes(data);
            uploadTask.addOnFailureListener(e -> finish());
            uploadTask.addOnSuccessListener(taskSnapshot -> {


                Task<Uri> task = taskSnapshot.getMetadata().getReference().getDownloadUrl();
                task.addOnSuccessListener(uri -> {
                    String photolink = uri.toString();

                    userInformation.put("profileImageUrl",photolink);
                    userDB.updateChildren(userInformation);

                    finish();
                    return;

                });

            });

        }else {
            finish();
        }

    }



    public void isReadFromStoragePermitted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

                // Permission is not granted
                // Should we show an explanation?
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_READ);
                }
                else {
                    // No explanation needed; request the permission
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_READ);
                }
            }else {
                pickImageFromStorage();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(ProfileActivity.this, "Permission granted", Toast.LENGTH_LONG).show();
                    pickImageFromStorage();
                } else {
                    Toast.makeText(ProfileActivity.this, "Permission not granted", Toast.LENGTH_LONG).show();
                    break;
                }

            }

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 1 && resultCode == Activity.RESULT_OK){
            final Uri imageUri = data.getData();
            resultUri = imageUri;
            mProfileImage.setImageURI(resultUri);

        }
    }

    private void pickImageFromStorage(){
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent,1);
    }

}