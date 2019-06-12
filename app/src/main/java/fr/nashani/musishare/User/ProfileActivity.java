package fr.nashani.musishare.User;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
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

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.location.Location;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;


import fr.nashani.musishare.R;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

/**
 * The type Profile activity.
 */
public class ProfileActivity extends Activity {

    private static final int MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION = 1;
    private EditText mNameField, mAgeField;
    private TextView address;
    private ImageView mProfileImage;
    private Button mBack, mConfirm, btnShowAddress;
    // AppLocationService appLocationService;
    private DatabaseReference userDB;
    private FusedLocationProviderClient client;
    private double latitude ,longitude;
    private RadioGroup radio_sex_choice;
    private RadioButton radio_sex_male;
    private RadioButton radio_sex_female;
    private RadioButton radio_sex_both;


    private String userId, name, age, profileImageURL, userSex, locationAddress;

    private Uri resultUri;

    private static final int MY_PERMISSIONS_REQUEST_READ = 0;
    private FirebaseAuth mAuth ;
    int selectId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        mAuth = FirebaseAuth.getInstance();

        mNameField = findViewById(R.id.userProfileName);
        mAgeField = findViewById(R.id.userProfilePhone);
        mProfileImage = findViewById(R.id.userProfileImage);
        mBack = findViewById(R.id.profile_back);
        mConfirm = findViewById(R.id.profile_confirm);
        radio_sex_choice = findViewById(R.id.radio_sexChoice);
        selectId = radio_sex_choice.getCheckedRadioButtonId();
        radio_sex_male = findViewById(R.id.radio_male);
        radio_sex_female = findViewById(R.id.radio_female);
        radio_sex_both = findViewById(R.id.radio_both);

        // Location
        address = findViewById(R.id.address);
        btnShowAddress = findViewById(R.id.btnShowAddress);
        client = LocationServices.getFusedLocationProviderClient(this);
        // requestPermission();
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        userDB = FirebaseDatabase.getInstance().getReference().child("Users").child(userId);

        getUserInfo();
        mProfileImage.setOnClickListener(v -> {

            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            startActivityForResult(intent, 1);

            isReadFromStoragePermitted();


        });

        mConfirm.setOnClickListener(v -> {
            saveUserInformation();
        });

        mBack.setOnClickListener(v -> {
            finish();
            return;
        });

        // Get Address From location GPS

        btnShowAddress.setOnClickListener(arg0 -> {
            if (ContextCompat.checkSelfPermission(ProfileActivity.this,
                    Manifest.permission.ACCESS_COARSE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {

                // Permission is not granted
                // Should we show an explanation?
                if (ActivityCompat.shouldShowRequestPermissionRationale(ProfileActivity.this,
                        Manifest.permission.ACCESS_COARSE_LOCATION)) {
                    // Show an explanation to the user *asynchronously* -- don't block
                    // this thread waiting for the user's response! After the user
                    // sees the explanation, try again to request the permission.

                    new android.support.v7.app.AlertDialog.Builder(this)
                            .setTitle("Required Location Permission")
                            .setMessage("You have to give this permission to acess this feature")
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    ActivityCompat.requestPermissions(ProfileActivity.this,
                                            new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                                            MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION);
                                }
                            })
                            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.dismiss();
                                }
                            })
                            .create()
                            .show();


                } else {
                    // No explanation needed; request the permission
                    ActivityCompat.requestPermissions(ProfileActivity.this,
                            new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                            MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION);

                    // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                    // app-defined int constant. The callback method gets the
                    // result of the request.
                }
            } else {
                // Permission has already been granted
                client.getLastLocation().addOnSuccessListener(ProfileActivity.this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) {
                             latitude = location.getLatitude();
                             longitude = location.getLongitude();
                            LocationAddress locationAddress = new LocationAddress();
                            locationAddress.getAddressFromLocation(latitude, longitude,
                                    getApplicationContext(), new GeocoderHandler());
                        } else {
                            showSettingsAlert();
                        }
                    }
                });
            }
        });

    }

    /**
     * Show settings alert.
     */
    public void showSettingsAlert() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(
                ProfileActivity.this);
        alertDialog.setTitle("SETTINGS");
        alertDialog.setMessage("Enable Location Provider! Go to settings menu?");
        alertDialog.setPositiveButton("Settings",
                (dialog, which) -> {
                    Intent intent = new Intent(
                            Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    ProfileActivity.this.startActivity(intent);
                });
        alertDialog.setNegativeButton("Cancel",
                (dialog, which) -> dialog.cancel());
        alertDialog.show();
    }

    private class GeocoderHandler extends Handler {
        @Override
        public void handleMessage(Message message) {

            switch (message.what) {
                case 1:
                    Bundle bundle = message.getData();
                    locationAddress = bundle.getString("address");
                    break;
                default:
                    locationAddress = null;
            }
            address.setText(locationAddress);
        }
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
                    if (map.get("age") != null){
                        age = map.get("age").toString();
                        mAgeField.setText(age);
                    }

                    if (map.get("sex") != null){
                        userSex = map.get("sex").toString();
                    }
                    if(map.get("sexPreference") != null){
                        if(map.get("sexPreference").equals("Male")){
                            radio_sex_male.setChecked(true);
                            radio_sex_female.setChecked(false);
                            radio_sex_both.setChecked(false);
                        }else if(map.get("sexPreference").equals("Female")){
                            radio_sex_female.setChecked(true);
                            radio_sex_male.setChecked(false);
                            radio_sex_both.setChecked(false);
                        }else {
                            radio_sex_both.setChecked(true);
                            radio_sex_female.setChecked(false);
                            radio_sex_male.setChecked(false);
                        }
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
        age = mAgeField.getText().toString();
        int selectId = radio_sex_choice.getCheckedRadioButtonId();

        final RadioButton mSexChoice = findViewById(selectId);

        Map<String, Object> userInformation = new HashMap<String, Object>();
        userInformation.put("name",name);
        userInformation.put("age", age);
        userInformation.put("latitude",latitude);
        userInformation.put("longitude",longitude);
        userInformation.put("address",locationAddress);
        userInformation.put("sexPreference",mSexChoice.getText().toString());

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


    /**
     * Is read from storage permitted.
     */
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

    /**
     * Logout user.
     *
     * @param view the view
     */
    public void logOutUser (View view){
        mAuth.signOut();
        Intent intent = new Intent(ProfileActivity.this, ChooseLoginRegistrationActivity.class);
        startActivity(intent);
        finish();
        return;
    }

}