package com.example.socialnetwork;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class RegisterActivity extends AppCompatActivity {

    private EditText registerEmail, registerPassword, registerConfirmPassword;
    private Button registerButton;
    private LinearLayout registerLayout;
    private TextView welcome_message;
    protected AlphaAnimation fadeIn = new AlphaAnimation(0.0f, 1.0f);
    protected AlphaAnimation fadeOut = new AlphaAnimation(1.0f, 0.0f);
    private ProgressDialog loadingBar;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();

        registerEmail = findViewById(R.id.register_email);
        registerPassword = findViewById(R.id.register_password);
        registerConfirmPassword = findViewById(R.id.register_confirm_password);
        registerButton = findViewById(R.id.register_button);
        loadingBar = new ProgressDialog(this);

        welcome_message = findViewById(R.id.welcome_message);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        registerLayout = findViewById(R.id.register_layout);
        registerLayout.setVisibility(View.INVISIBLE);
        welcome_message.setVisibility(View.INVISIBLE);

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                welcome_message.startAnimation(fadeIn);
                fadeIn.setDuration(1500);
                fadeIn.setFillAfter(true);
            }
        }, 1500);

        welcome_message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WelcomingMessage();
            }
        });

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CreateNewAccount();
            }
        });
    }

    private void WelcomingMessage() {
        welcome_message.startAnimation(fadeOut);
        fadeOut.setDuration(1500);
        fadeOut.setFillAfter(true);
        welcome_message.setVisibility(View.GONE);
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                registerLayout.startAnimation(fadeIn);
                fadeIn.setDuration(1500);
                fadeIn.setFillAfter(true);
            }
        }, 1500);
    }

    private void CreateNewAccount() {
        String userEmail = registerEmail.getText().toString();
        String userPassword = registerPassword.getText().toString();
        String confirmPassword = registerConfirmPassword.getText().toString();

        if (TextUtils.isEmpty(userEmail)){
            Toast.makeText(this, "Por favor, introduce tu email", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(userPassword)){
            Toast.makeText(this, "Por favor, introduce tu contraseña", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(confirmPassword)){
            Toast.makeText(this, "Por favor, introduce tu confirmación de contraseña", Toast.LENGTH_SHORT).show();
        } else if (!userPassword.equals(confirmPassword)){
            Toast.makeText(this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show();
        } else {
            loadingBar.setTitle("Creando una nueva cuenta");
            loadingBar.setMessage("por favor, espera mientras creamos tu nueva cuenta");
            loadingBar.show();
            loadingBar.setCanceledOnTouchOutside(true);

            mAuth.createUserWithEmailAndPassword(userEmail, userPassword)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()){
                        SendUserToSetupActivity();
                        Toast.makeText(RegisterActivity.this, "Credenciales creadas correctamente", Toast.LENGTH_SHORT).show();
                        loadingBar.dismiss();
                    } else {
                        String errorMessage = task.getException().getMessage();
                        Toast.makeText(RegisterActivity.this, "Error: " + errorMessage, Toast.LENGTH_SHORT).show();
                        loadingBar.dismiss();
                    }
                }
            });
        }

    }

    private void SendUserToSetupActivity() {
        Intent setupIntent = new Intent(RegisterActivity.this, SetupActivity.class);
        setupIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(setupIntent);
        finish();

    }
}
