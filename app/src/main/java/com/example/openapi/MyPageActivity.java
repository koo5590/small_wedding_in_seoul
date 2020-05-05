package com.example.openapi;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.util.Log;

public class MyPageActivity extends AppCompatActivity {

    ListView myReviewList;    //list of reviews
    LinearLayout myInfo;

    TextView myNickname;

    EditText changeNickName;
    EditText changePW;
    EditText changePWCheck;
    EditText curPW;

    WeddingObj weddingObj;

    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference myRef = database.getReference();
    private FirebaseAuth mAuth;

    private ReviewAdaptor reviewAdaptor;
    private ArrayList<ListItem> reviewItems;
    private ArrayList<ReviewItem> reviewPosts;


    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mypage);

        Intent intent = getIntent();
        weddingObj = (WeddingObj)intent.getSerializableExtra("object");

        mAuth = FirebaseAuth.getInstance();

        myReviewList = (ListView)findViewById(R.id.myReviewList);
        myInfo = (LinearLayout)findViewById(R.id.myInfo);

        myNickname = (TextView)findViewById(R.id.curNickname);

        changeNickName = (EditText)findViewById(R.id.newNicknameText);
        changePW = (EditText)findViewById(R.id.newPWText);
        changePWCheck = (EditText)findViewById(R.id.newPWCheckText);
        curPW = (EditText)findViewById(R.id.curPWText);

        //show current nickname of user
        final String uid = mAuth.getCurrentUser().getUid();
        //Log.d("uid",uid);
        myRef.child("users").child(uid).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(!dataSnapshot.exists()){
                            Toast.makeText(MyPageActivity.this, "잘못된 접근입니다.", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        String nickname = dataSnapshot.getValue(UserData.class).getNickname();
                        myNickname.append(nickname);
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                }
        );

        findViewById(R.id.changeNickB).setOnClickListener(new Button.OnClickListener(){
            public void onClick(View v){
                String newNickname = changeNickName.getText().toString();

                if(newNickname.length()<2){
                    Toast.makeText(MyPageActivity.this, "별명은 두 글자 이상 이어야 합니다.", Toast.LENGTH_SHORT).show();
                    return;
                }
                Map<String, Object> taskMap = new HashMap<>();
                taskMap.put("users/"+uid+"/nickname", newNickname);
                myRef.updateChildren(taskMap);
                Toast.makeText(MyPageActivity.this, "성공적으로 변경되었습니다.", Toast.LENGTH_SHORT).show();
            }
        });

        findViewById(R.id.saveChangeB).setOnClickListener(new Button.OnClickListener(){
            public void onClick(View v){
                final String newPW = changePW.getText().toString();
                final String newPWCheck = changePWCheck.getText().toString();
                final String oldPW = curPW.getText().toString();

                if(!newPW.equals(newPWCheck) || newPW.length()<6){
                    Toast.makeText(MyPageActivity.this, "비밀번호를 다시 확인해주세요.", Toast.LENGTH_SHORT).show();
                    return;
                }

                final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                AuthCredential credential = EmailAuthProvider.getCredential(mAuth.getCurrentUser().getEmail(), oldPW);

                user.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            user.updatePassword(newPW).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        Toast.makeText(MyPageActivity.this, "성공적으로 변경되었습니다.", Toast.LENGTH_SHORT).show();
                                    }
                                    else{
                                        Toast.makeText(MyPageActivity.this, "비밀번호를 다시 확인해주세요.", Toast.LENGTH_SHORT).show();
                                    }

                                }
                            });
                        }
                        else{
                            Toast.makeText(MyPageActivity.this, "비밀번호를 다시 확인해주세요.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }

        });

        findViewById(R.id.back).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                finish();
            }
        });

        findViewById(R.id.InfoB).setOnClickListener(new Button.OnClickListener(){
            public void onClick(View v){
                myInfo.setVisibility(View.VISIBLE);
                myReviewList.setVisibility(View.GONE);
            }
        });

        findViewById(R.id.reviewB).setOnClickListener(new Button.OnClickListener(){
            public void onClick(View v){
                myInfo.setVisibility(View.GONE);
                myReviewList.setVisibility(View.VISIBLE);

                //load reviews of this wedding obj from database
                myRef.child("users").child(mAuth.getCurrentUser().getUid()).child("posts").addListenerForSingleValueEvent(
                        new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                reviewItems = new ArrayList<ListItem>();    //for list view
                                reviewPosts = new ArrayList<ReviewItem>();  //for review detail

                                for(DataSnapshot postSnapShot: dataSnapshot.getChildren()){
                                    ReviewItem reviewItem = postSnapShot.getValue(ReviewItem.class);
                                    reviewPosts.add(reviewItem);
                                    reviewItems.add(new ListItem(reviewItem.content, reviewItem.date));
                                }

                                //no review written yet
                                if(reviewItems.isEmpty())
                                    Toast.makeText(MyPageActivity.this, "아직 후기가 없습니다.", Toast.LENGTH_SHORT).show();
                                //show list of reviews with title and written date
                                else{
                                    printReviewList();
                                    myReviewList.setVisibility(View.VISIBLE);
                                }

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        }
                );
            }
        });

        myReviewList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                ReviewItem review = reviewPosts.get(i);

                Intent intent = new Intent(getApplicationContext(), DetailReviewActivity.class);
                intent.putExtra("post", review);
                intent.putExtra("from", "mypage");
                intent.putExtra("object", weddingObj);
                startActivity(intent);
                finish();
            }
        });

    }

    private void printReviewList(){
        reviewAdaptor = new ReviewAdaptor(reviewPosts, getApplicationContext());
        myReviewList.setAdapter(reviewAdaptor);
    }
}
