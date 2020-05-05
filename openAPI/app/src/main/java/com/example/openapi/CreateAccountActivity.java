package com.example.openapi;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;



/** create new account with email,password, nickname **/
public class CreateAccountActivity extends AppCompatActivity{
    /** user input values **/
    EditText newUserEmail;  //email
    EditText newnickname;   //nickname
    EditText newUserPW;     //password
    EditText PWCheck;       //password check

    /** firebase **/
    private FirebaseAuth mAuth;
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference myRef = database.getReference("users");

    /** view **/
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_createaccount);

        /** initialize **/
        //EditText : gets user input
        newUserEmail = (EditText)findViewById(R.id.newUserEmail);
        newUserPW = (EditText)findViewById(R.id.newUserpassword);
        PWCheck = (EditText)findViewById(R.id.pwCheck);
        newnickname = (EditText)findViewById(R.id.newnickname);

        //firebase authentication
        mAuth = FirebaseAuth.getInstance();


        /** buttons **/
        //create account button
        findViewById(R.id.createNewButton).setOnClickListener(new Button.OnClickListener(){
            public void onClick(View v){
                String email = newUserEmail.getText().toString().trim();
                String pw = newUserPW.getText().toString().trim();
                String pwCheck = PWCheck.getText().toString().trim();
                String nickname = newnickname.getText().toString().trim();
                //create new UserData and save in firebase realtime DB/auth
                if(checkValid(email,nickname, pw, pwCheck))
                    createNewAccount(email, nickname, pw);
            }
        });
    }

    /** create new user through firebase authentification &save new user at realtime DB **/
    private boolean checkValid(final String email, final String nickname, final String password, String pwCheck) {
        //check if any input is empty
        if (nickname.equals("")||email.equals("")||password.equals("")||pwCheck.equals("")) {
            Toast.makeText(CreateAccountActivity.this, "빈칸을 전부 채워주세요", Toast.LENGTH_SHORT).show();
            return false;
        }

        //check password
        if(!password.equals(pwCheck)){
            Toast.makeText(CreateAccountActivity.this, "비밀번호 입력이 다릅니다.\n다시 확인해주세요.", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void createNewAccount(final String email, final String nickname, final String password){
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        //successfully created account at Auth
                        if (task.isSuccessful()) {
                            //get current user
                            FirebaseUser user = mAuth.getCurrentUser();
                            Toast.makeText(CreateAccountActivity.this, "가입을 환영합니다 :)", Toast.LENGTH_SHORT).show();

                            //create new UserData using current user data
                            UserData newUser = new UserData(user.getEmail(), nickname);
                            //get current user id
                            String cu = user.getUid();
                            //save UserData under user id in firebase realtime DB
                            myRef.child(cu).setValue(newUser);

                            //move to MainActivity
                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                            intent.putExtra("userInfo", user);
                            startActivity(intent);
                            finish();
                        }
                        //failed creating new account at Auth
                        else {
                            try {
                                //throw exception
                                throw task.getException();
                            } catch (FirebaseAuthWeakPasswordException e) {    //password exception
                                Toast.makeText(CreateAccountActivity.this, "비밀번호는 최소 6자 이상이어야 합니다", Toast.LENGTH_SHORT).show();
                            } catch (FirebaseAuthUserCollisionException e) {  //user with same email already exists
                                Toast.makeText(CreateAccountActivity.this, "이미 존재하는 이메일 입니다.", Toast.LENGTH_SHORT).show();
                            } catch (Exception e) {                            //wrong email address
                                Toast.makeText(CreateAccountActivity.this, "올바른 이메일 주소를 입력해주세요", Toast.LENGTH_SHORT).show();
                            }

                        }

                    }
                });
    }

}
