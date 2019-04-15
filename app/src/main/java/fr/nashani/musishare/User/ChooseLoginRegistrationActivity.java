package fr.nashani.musishare.User;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import fr.nashani.musishare.R;

public class ChooseLoginRegistrationActivity extends Activity {

    Button mLogin, mRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_login_registration);

        mLogin = findViewById(R.id.button_login);

        mLogin.setOnClickListener(v -> {
            Intent intent = new Intent(ChooseLoginRegistrationActivity.this,LoginActivity.class);
            startActivity(intent);
            finish();
            return;
        });

        mRegister = findViewById(R.id.button_register);
        mRegister.setOnClickListener(v -> {
            Intent intent = new Intent(ChooseLoginRegistrationActivity.this,RegistrationActivity.class);
            startActivity(intent);
            finish();
            return;
        });
    }
}
