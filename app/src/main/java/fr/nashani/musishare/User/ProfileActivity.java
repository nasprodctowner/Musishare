package fr.nashani.musishare.User;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

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
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import fr.nashani.musishare.R;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class ProfileActivity extends Activity {

    private EditText mNameField, mPhoneField;
    private TextView coords, address;
    private ImageView mProfileImage;
    private Button mBack, mConfirm, btnGPSShowLocation, btnShowAddress;
    AppLocationService appLocationService;
    private DatabaseReference userDB;
    private FusedLocationProviderClient client;

    private String userId, name, phone, profileImageURL, userSex;

    private Uri resultUri;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        mNameField = findViewById(R.id.userProfileName);
        mPhoneField = findViewById(R.id.userProfilePhone);
        mProfileImage = findViewById(R.id.userProfileImage);
        mBack = findViewById(R.id.profile_back);
        mConfirm = findViewById(R.id.profile_confirm);

        // Location
        coords = findViewById(R.id.coords);
        address = findViewById(R.id.address);
        btnGPSShowLocation = findViewById(R.id.btnGPSShowLocation);
        btnShowAddress = findViewById(R.id.btnShowAddress);
        client = LocationServices.getFusedLocationProviderClient(this);
        requestPermission();


        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        userDB = FirebaseDatabase.getInstance().getReference().child("Users").child(userId);

        getUserInfo();
        mProfileImage.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            startActivityForResult(intent, 1);
        });

        mConfirm.setOnClickListener(v -> {
            saveUserInformation();
        });

        mBack.setOnClickListener(v -> {
            finish();
            return;
        });

        // **************************************************** //

        appLocationService = new AppLocationService(
                ProfileActivity.this);

        btnGPSShowLocation.setOnClickListener(arg0 -> {
            if (ActivityCompat.checkSelfPermission(ProfileActivity.this, ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(ProfileActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            client.getLastLocation().addOnSuccessListener(ProfileActivity.this, new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if (location != null) {
                        double latitude = location.getLatitude();
                        double longitude = location.getLongitude();
                        String result = "Latitude: " + location.getLatitude() +
                                " Longitude: " + location.getLongitude();
                        coords.setText(result);
                    } else {
                        showSettingsAlert();
                    }
                }
            });
        });

        btnShowAddress.setOnClickListener(arg0 -> {
            if (ActivityCompat.checkSelfPermission(ProfileActivity.this, ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(ProfileActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            client.getLastLocation().addOnSuccessListener(ProfileActivity.this, new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if (location != null) {
                        double latitude = location.getLatitude();
                        double longitude = location.getLongitude();
                        LocationAddress locationAddress = new LocationAddress();
                        locationAddress.getAddressFromLocation(latitude, longitude,
                                getApplicationContext(), new GeocoderHandler());
                    } else {
                        showSettingsAlert();
                    }
                }
            });
        });

    }

    private void requestPermission () {
        ActivityCompat.requestPermissions(ProfileActivity.this, new String[] {ACCESS_FINE_LOCATION} , 1);
    }

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
            String locationAddress;
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
