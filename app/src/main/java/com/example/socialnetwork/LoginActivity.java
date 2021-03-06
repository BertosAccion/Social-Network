package com.example.socialnetwork;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private Button loginButton;
    private EditText userEmail, userPassword;
    private TextView newAccountLink;
    private ProgressDialog loadingBar;

    private static final String TAG = "LoginActivity";

    private ImageView googleSignButton;
    private static final int RC_SIGN_IN = 1;
    private GoogleApiClient mGoogleSignInClient;

    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        newAccountLink = findViewById(R.id.newAccountLink);
        userEmail = findViewById(R.id.login_email);
        userPassword = findViewById(R.id.login_password);
        loginButton = findViewById(R.id.login_button);
        loadingBar = new ProgressDialog(this);

        googleSignButton = findViewById(R.id.google_login_button);

        mAuth = FirebaseAuth.getInstance();

        newAccountLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SendUserToRegisterActivity();
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserLogingIn();
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            SendUserToMainActivity();
        }
    }

    private void UserLogingIn() {
        String loginEmail = userEmail.getText().toString();
        String loginPassword = userPassword.getText().toString();

        if (TextUtils.isEmpty(loginEmail)) {
            Toast.makeText(this, "Por favor, introduzca un email", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(loginPassword)) {
            Toast.makeText(this, "Por favor, introduzca la contraseña", Toast.LENGTH_SHORT).show();
        } else {

            loadingBar.setTitle("Iniciando sesión...");
            loadingBar.setMessage("por favor, espera unos segundos");
            loadingBar.setCanceledOnTouchOutside(true);
            loadingBar.show();

            mAuth.signInWithEmailAndPassword(loginEmail, loginPassword)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                SendUserToMainActivity();

                                Toast.makeText(LoginActivity.this, "Sesión iniciada correctamente", Toast.LENGTH_SHORT).show();
                                loadingBar.dismiss();
                            } else {
                                String errorMessage = task.getException().getMessage();
                                Toast.makeText(LoginActivity.this, "Error: " + errorMessage, Toast.LENGTH_SHORT).show();
                                loadingBar.dismiss();
                            }
                        }
                    });
        }
    }

    private void SendUserToMainActivity() {
        Intent mainIntent = new Intent(LoginActivity.this, MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        finish();
    }

    private void SendUserToLoginActivity() {
        Intent mainIntent = new Intent(LoginActivity.this, LoginActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        finish();
    }

    private void SendUserToRegisterActivity() {
        Intent registerIntent = new Intent(LoginActivity.this, RegisterActivity.class);
        startActivity(registerIntent);
    }
}
