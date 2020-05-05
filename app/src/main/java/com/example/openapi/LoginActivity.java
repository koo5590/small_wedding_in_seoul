package com.example.openapi;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {
    TextView userEmail;     //email input text
    TextView userPassword;  //password input text

    private FirebaseAuth mAuth;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //TextViews
        userEmail = findViewById(R.id.userEmail);
        userPassword = findViewById(R.id.userPassword);

        //Buttons
        //sign in button
        findViewById(R.id.signinButton).setOnClickListener(new Button.OnClickListener(){
            public void onClick(View v){
                signIn(userEmail.getText().toString(), userPassword.getText().toString());
            }
        });
        //go to create account screen
        findViewById(R.id.createButton).setOnClickListener(new Button.OnClickListener(){
            public void onClick(View v){
                Intent intent = new Intent(getApplicationContext(), CreateAccountActivity.class);
                startActivity(intent);
            }
        });

        mAuth = FirebaseAuth.getInstance();
    }

    //sign in
    private void signIn(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        //login successful
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();

                            //go to main page
                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                            //pass over user data
                            intent.putExtra("userInfo", user);
                            startActivity(intent);
                            finish();
                        }
                        //log in failed
                        else {
                            Toast.makeText(LoginActivity.this, "로그인에 실패하였습니다. 이메일과 비밀번호를 다시 확인해주세요.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

}
