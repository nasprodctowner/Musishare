package fr.nashani.musishare;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
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

public class ProfileActivity extends AppCompatActivity {

    private EditText mNameField, mPhoneField;
    private ImageView mProfileImage;
    private Button mBack, mConfirm;

    private FirebaseAuth mAuth ;
    private DatabaseReference userDB;

    private String userId, name, phone, profileImageURL;

    private Uri resultUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        mNameField = findViewById(R.id.userProfileName);
        mPhoneField = findViewById(R.id.userProfilePhone);
        mProfileImage = findViewById(R.id.userProfileImage);
        String userSex = getIntent().getExtras().getString("userSex");
        mBack = findViewById(R.id.profile_back);
        mConfirm = findViewById(R.id.profile_confirm);

        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        userDB = FirebaseDatabase.getInstance().getReference().child("Users").child(userSex).child(userId);

        getUserInfo();
        mProfileImage.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            startActivityForResult(intent,1);
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


                    if (map.get("profileImageUrl") != null){
                        profileImageURL = map.get("profileImageUrl").toString();

                        switch (profileImageURL){
                            case "default" : mProfileImage.setImageResource(R.drawable.ic_person_black_24dp);

                                break;

                            default: Glide.with(getApplication()).load(profileImageURL).into(mProfileImage);
                                break;

                        }

                        //URL TO IMAGE

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

                Task<Uri> downloadUrl = taskSnapshot.getStorage().getDownloadUrl();



            });

        }else {
            finish();
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
}
