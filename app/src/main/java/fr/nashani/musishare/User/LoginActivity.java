package fr.nashani.musishare.User;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import fr.nashani.musishare.MainActivity;
import fr.nashani.musishare.R;

/**
 * The type Login activity.
 */
public class LoginActivity extends Activity {

    private Button mLogin;
    private TextView mRegister;
    private EditText mEmail, mPassword;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener firebaseAuthStateListener;
    private int backButtonCount = 0;

    private static void onClick(View view) {
    }

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
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();
        firebaseAuthStateListener = firebaseAuth -> {
            final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if(user != null){
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
                return;
            }
        };

        mLogin = findViewById(R.id.button_submit_login);
        mRegister = findViewById(R.id.textView_register);
        mEmail = findViewById(R.id.text_email_login);
        mPassword = findViewById(R.id.text_password_login);

        mLogin.setOnClickListener(v -> {
            final String email = mEmail.getText().toString();
            final String password = mPassword.getText().toString();
            mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(LoginActivity .this, task -> {
                if(!task.isSuccessful()){
                    Toast.makeText(LoginActivity.this,"signin error",Toast.LENGTH_SHORT).show();
                }
            });

        });

        mRegister.setOnClickListener(view -> {
            Intent intent = new Intent(LoginActivity.this,RegistrationActivity.class);
            startActivity(intent);
            finish();
            return;
        });
    }

    /*@Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(LoginActivity.this,ChooseLoginRegistrationActivity.class);
        startActivity(intent);
        finish();
        return;
    }*/
    /**
     * Back button listener.
     * Will close the application if the back button pressed twice.
     */
    @Override
    public void onBackPressed()
    {
        if(backButtonCount >= 1)
        {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
        else
        {
            Toast.makeText(this, "Press the back button once again to close the application.", Toast.LENGTH_SHORT).show();
            backButtonCount++;
        }
    }
}
