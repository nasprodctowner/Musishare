package fr.nashani.musishare;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

public class RegistrationActivity extends AppCompatActivity {

    private Button mRegister;
    private EditText mEmail, mPassword, mName;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener firebaseAuthStateListener;
    private RadioGroup mSexe;



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
                Intent intent = new Intent(RegistrationActivity.this,MainActivity.class);
                startActivity(intent);
                finish();
                return;
            }
        };

        mRegister = findViewById(R.id.button_submit_registration);
        mEmail = findViewById(R.id.text_email);
        mPassword = findViewById(R.id.text_password);
        mSexe =  findViewById(R.id.radio_sexe);
        mName =  findViewById(R.id.text_name);


        mRegister.setOnClickListener(v -> {

            int selectId = mSexe.getCheckedRadioButtonId();

            final RadioButton mSexeChoice = findViewById(selectId);

            if(mSexeChoice.getText() == null){
                return;
            }

            final String email = mEmail.getText().toString();
            final String password = mPassword.getText().toString();
            final String name = mName.getText().toString();

            final String sexe = mSexeChoice.getText().toString();
            System.out.println(sexe);
                mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(RegistrationActivity.this, task -> {
                    if(!task.isSuccessful()) {
                        Toast.makeText(RegistrationActivity.this,"signup error",Toast.LENGTH_SHORT).show();
                    }else {

                        String userId = mAuth.getCurrentUser().getUid() ;

                        DatabaseReference currentUserDB = FirebaseDatabase.getInstance().getReference().child("Users").child(sexe).child(userId).child("name");

                        currentUserDB.setValue(name);

                    }
                });

        });


    }
}
