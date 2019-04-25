package com.example.socialnetwork;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

public class RegisterActivity extends AppCompatActivity {

    private EditText email, password, confirmPassword;
    private Button registerButton;
    private LinearLayout registerLayout;
    private TextView welcome_message;
    protected AlphaAnimation fadeIn = new AlphaAnimation(0.0f, 1.0f);
    protected AlphaAnimation fadeOut = new AlphaAnimation(1.0f, 0.0f);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        welcome_message = findViewById(R.id.welcome_message);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        registerLayout = findViewById(R.id.register_layout);
        registerLayout.setVisibility(View.GONE);
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
                welcome_message.startAnimation(fadeOut);
                fadeOut.setDuration(1500);
                fadeOut.setFillAfter(true);
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        registerLayout.setVisibility(View.INVISIBLE);
                        registerLayout.startAnimation(fadeIn);
                        fadeIn.setDuration(1500);
                        fadeIn.setFillAfter(true);
                    }
                }, 1500);
            }
        });

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {/*
                registerLayout.startAnimation(fadeOut);
                fadeOut.setDuration(1500);
                fadeOut.setFillAfter(true);
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        welcome_message.setText("¡Muchísimas gracias! Vamos a completar tu perfil con algunos datos personales");
                        registerLayout.setVisibility(View.INVISIBLE);
                        registerLayout.startAnimation(fadeIn);
                        fadeIn.setDuration(1500);
                        fadeIn.setFillAfter(true);
                    }
                }, 1500);
            */}
        });
    }
}
