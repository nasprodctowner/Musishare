package fr.nashani.musishare.User;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

import fr.nashani.musishare.MainActivity;
import fr.nashani.musishare.R;

public class RegistrationActivity extends Activity {

    private Button mRegister;
    private EditText mEmail, mPassword, mName;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener firebaseAuthStateListener;
    private RadioGroup mSex;



    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(firebaseAuthStateListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mAuth.removeAuthStateListener(firebaseAuthStateListener);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        mAuth = FirebaseAuth.getInstance();
        firebaseAuthStateListener = firebaseAuth -> {
            final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if(user != null){
                Intent intent = new Intent(RegistrationActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
                return;
            }
        };

        mRegister = findViewById(R.id.button_submit_registration);
        mEmail = findViewById(R.id.text_email);
        mPassword = findViewById(R.id.text_password);
        mSex =  findViewById(R.id.radio_sex);
        mName =  findViewById(R.id.text_name);


        mRegister.setOnClickListener(v -> {

            int selectId = mSex.getCheckedRadioButtonId();

            final RadioButton mSexChoice = findViewById(selectId);

            if(mSexChoice.getText() == null){
                return;
            }

            final String email = mEmail.getText().toString();
            final String password = mPassword.getText().toString();
            final String name = mName.getText().toString();

            final String sex = mSexChoice.getText().toString();

                mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(RegistrationActivity.this, task -> {
                    if(!task.isSuccessful()) {
                        Toast.makeText(RegistrationActivity.this,"Signup error",Toast.LENGTH_SHORT).show();
                    }else {

                        String userId = mAuth.getCurrentUser().getUid() ;

                        DatabaseReference currentUserDB = FirebaseDatabase.getInstance().getReference().child("Users").child(userId);

                        Map<String, Object> userInformation = new HashMap<>();

                        userInformation.put("name",name);
                        userInformation.put("sex", sex);
                        userInformation.put("profileImageUrl","default");
                        currentUserDB.setValue(name);

                        currentUserDB.updateChildren(userInformation);
                    }
                });

        });


    }
}
