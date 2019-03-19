package fr.nashani.musishare;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class ChooseLoginRegistrationActivity extends AppCompatActivity {

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
