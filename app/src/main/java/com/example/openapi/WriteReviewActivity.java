package com.example.openapi;

import android.os.Bundle;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import android.content.Intent;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


/** write new review of a certain wedding place **/
public class WriteReviewActivity extends AppCompatActivity {
    Button saveButton;      //save reviews
    EditText newPost;       //contains user input
    WeddingObj weddingObj;  //object of this wedding place
    String state;
    ReviewItem item;
    String from;
    String weddingName;

    //Firebase realtime DB and Authentication
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference myRef = database.getReference();
    private FirebaseAuth mAuth;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write);

        Intent intent = getIntent();
        weddingObj = (WeddingObj)intent.getSerializableExtra("object");
        state = (String)intent.getSerializableExtra("state");
        weddingName = (String)intent.getSerializableExtra("name");

        mAuth = FirebaseAuth.getInstance();
        newPost = (EditText)findViewById(R.id.writeText);
        saveButton = (Button)findViewById(R.id.saveB);

        //go back to detail page without writing review
        findViewById(R.id.back).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent = new Intent(getApplicationContext(), DetailActivity.class);
                intent.putExtra("object", weddingObj);
                startActivity(intent);
                finish();
            }
        });

        //save review
        saveButton.setOnClickListener(new Button.OnClickListener(){
            public void onClick(View v){

                //get info of currently logged in user
                String uid = mAuth.getCurrentUser().getUid();
                myRef.child("users").child(uid).addListenerForSingleValueEvent(
                        new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                //get data of this review
                                String nickname = dataSnapshot.getValue(UserData.class).getNickname();
                                String content = newPost.getText().toString();
                                Date currentTime = Calendar.getInstance().getTime();
                                String date = new SimpleDateFormat("yyyy-MM-EE", Locale.getDefault()).format(currentTime);

                                //if text is not long enough
                                if(content.length()<50){
                                    Toast.makeText(WriteReviewActivity.this, "후기는 50자 이상을 남겨주세요!", Toast.LENGTH_SHORT).show();
                                    return;
                                }
                                //get user info
                                String uid = mAuth.getCurrentUser().getUid();

                                //new post
                                if(state.equals("new")) {
                                    String key = myRef.child("posts").push().getKey();

                                    //create new ReviewItem object and save data in DB
                                    ReviewItem newReview = new ReviewItem(weddingObj.getName(), content, nickname, date, key);
                                    myRef.child("posts").child(weddingObj.getName()).child(key).setValue(newReview);
                                    myRef.child("users").child(uid).child("posts").child(key).setValue(newReview);

                                    //alert user
                                    Toast.makeText(WriteReviewActivity.this, "소중한 후기를 남겨주셔서 감사합니다 :)", Toast.LENGTH_SHORT).show();
                                }

                                else{
                                    Map<String, Object> taskMap = new HashMap<>();
                                    taskMap.put(item.getId()+"/content", content);
                                    taskMap.put(item.getId()+"/date",new SimpleDateFormat("yyyy-MM-EE", Locale.getDefault()).format(currentTime));
                                    myRef.child("posts").child(weddingName).updateChildren(taskMap);
                                    myRef.child("users").child(uid).child("posts").updateChildren(taskMap);
                                    Toast.makeText(WriteReviewActivity.this, "글이 수정되었습니다 :)", Toast.LENGTH_SHORT).show();
                                }

                                //go back to detail page
                                /*Intent intent = new Intent(getApplicationContext(), DetailActivity.class);
                                intent.putExtra("object", weddingObj);
                                startActivity(intent);*/
                                finish();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        }
                );

            }
        });
    }

}
