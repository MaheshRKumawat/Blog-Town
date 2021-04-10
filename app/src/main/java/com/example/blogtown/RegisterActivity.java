package com.example.blogtown;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class RegisterActivity extends AppCompatActivity {

    private EditText reg_email_text;
    private EditText reg_password_text;
    private EditText reg_confirm_password_text;
    private Button reg_btn;
    private Button reg_login_btn;
    private ProgressBar reg_progress_bar;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();
        reg_email_text= (EditText) findViewById(R.id.reg_email_text);
        reg_password_text = (EditText) findViewById(R.id.reg_password_text);
        reg_confirm_password_text = (EditText) findViewById(R.id.reg_confirm_password_text);
        reg_btn = (Button) findViewById(R.id.reg_button);
        reg_login_btn = (Button) findViewById(R.id.reg_login_btn);
        reg_progress_bar = (ProgressBar) findViewById(R.id.reg_progressBar);

        reg_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String reg_email = reg_email_text.getText().toString();
                String reg_password = reg_password_text.getText().toString();
                String reg_confirm_password = reg_confirm_password_text.getText().toString();

                if(!TextUtils.isEmpty(reg_email) && !TextUtils.isEmpty(reg_password) && !TextUtils.isEmpty(reg_confirm_password)){
                    if(reg_password.equals(reg_confirm_password)){

                        reg_progress_bar.setVisibility(View.VISIBLE);

                        mAuth.createUserWithEmailAndPassword(reg_email, reg_password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if(task.isSuccessful()){
                                    Intent profileIntent = new Intent(RegisterActivity.this, Profile_Activity.class);
                                    startActivity(profileIntent);
                                    finish();
                                }else{
                                    String errorMessage = task.getException().getMessage();
                                    Toast.makeText(RegisterActivity.this, "Error: "+errorMessage, Toast.LENGTH_SHORT).show();
                                }
                                reg_progress_bar.setVisibility(View.INVISIBLE);
                            }
                        });
                    }else{
                        Toast.makeText(RegisterActivity.this, "Confirm Password and Password don't match", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        reg_login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent LoginIntent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(LoginIntent);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            sendToMain();
        }
    }

    private void sendToMain(){
        Intent mainIntent = new Intent(RegisterActivity.this, MainActivity.class);
        startActivity(mainIntent);
        finish();
    }
}