package com.example.openapi;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;


/**** shows detail of review ****/
public class DetailReviewActivity extends AppCompatActivity {
    ReviewItem reviewItem;  //review Item to be shown
    WeddingObj weddingObj;  //from DetailActivity
    String from;            //DetailActivity: "detail", MyPageActivity: "mypage"
    String weddingName;     //name of this wedding place

    TextView placeName;     //where weddingName shows

    //info of review
    TextView rDate;     //written date
    TextView rNickname; //writer
    TextView rContent;  //content of review

    private DatabaseReference myRef = FirebaseDatabase.getInstance().getReference();
    private FirebaseAuth mAuth;

    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        Intent intent = getIntent();
        reviewItem = (ReviewItem) intent.getSerializableExtra("post");
        from = (String) intent.getSerializableExtra("from");
        //from MyPageActivity

        //from DetailActivity
        weddingObj = (WeddingObj) intent.getSerializableExtra("object");
        weddingName = weddingObj.getName();

        //show name of this place
        placeName = (TextView)findViewById(R.id.placeName);
        placeName.append(weddingName);

        //show info of review
        rDate = (TextView)findViewById(R.id.reviewDate);
        rNickname = (TextView)findViewById(R.id.nickname);
        rContent = (TextView)findViewById(R.id.reviewContent);

        rDate.append(reviewItem.date);
        rNickname.append(reviewItem.nickname);
        rContent.append(reviewItem.content);

        rContent.setVisibility(View.VISIBLE);

        mAuth = FirebaseAuth.getInstance();

        //go back button
        findViewById(R.id.back).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent;
                //go back to my page
                if(from.equals("mypage"))
                    intent = new Intent(getApplicationContext(), MyPageActivity.class);
                //go back to detail page
                else
                    intent = new Intent(getApplicationContext(), DetailActivity.class);

                intent.putExtra("object", weddingObj);
                startActivity(intent);
                finish();

            }
        });

        findViewById(R.id.mypageB).setOnClickListener(new Button.OnClickListener(){
            public void onClick(View v){
                Intent intent2 = new Intent(getApplicationContext(), MyPageActivity.class);
                intent2.putExtra("object", weddingObj);
                startActivity(intent2);
                finish();
            }
        });

        myRef.child("users").child(mAuth.getCurrentUser().getUid()).child("posts").addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.hasChild(reviewItem.getId())){
                            findViewById(R.id.delete).setVisibility(View.VISIBLE);
                            findViewById(R.id.edit).setVisibility(View.VISIBLE);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                }
        );

        findViewById(R.id.edit).setOnClickListener(new Button.OnClickListener(){
            public void onClick(View v){
                Intent intent2 = new Intent(getApplicationContext(), WriteReviewActivity.class);
                intent2.putExtra("object", weddingObj);
                intent2.putExtra("state" , "edit");
                intent2.putExtra("item", reviewItem);
                intent2.putExtra("name", weddingName);
                startActivity(intent2);
                finish();

            }
        });

        //delete button
        final AlertDialog.Builder dialog = new AlertDialog.Builder(DetailReviewActivity.this);
        dialog.setTitle("후기 삭제하기").setMessage("글을 삭제하시겠습니까?")
                .setPositiveButton("네", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Map<String, Object> taskMap = new HashMap<>();
                        ReviewItem temp=null;
                        taskMap.put("/posts/"+reviewItem.getWeddingName()+"/"+reviewItem.getId(), temp);
                        taskMap.put("users/"+mAuth.getCurrentUser().getUid()+"/posts/"+reviewItem.getId(), temp);
                        myRef.updateChildren(taskMap);

                        Toast.makeText(DetailReviewActivity.this, "글이 삭제되었습니다.", Toast.LENGTH_SHORT).show();
                        if(from.equals("mypage")){
                            Intent intent = new Intent(getApplicationContext(), MyPageActivity.class);
                            intent.putExtra("object", weddingObj);
                            startActivity(intent);
                            finish();
                        }
                        else {
                            Intent intent = new Intent(getApplicationContext(), DetailActivity.class);
                            intent.putExtra("object", weddingObj);
                            startActivity(intent);
                            finish();
                        }

                        /*.addListenerForSingleValueEvent(
                                new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        if(!dataSnapshot.exists()){
                                            Toast.makeText(DetailReviewActivity.this, "잘못된 접근입니다.", Toast.LENGTH_SHORT).show();
                                        }



                                        Toast.makeText(DetailReviewActivity.this, "성공적으로 삭제 되었습니다.", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(getApplicationContext(), DetailActivity.class);
                                        intent.putExtra("object", weddingObj);
                                        startActivity(intent);
                                        finish();
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                }
                        );*/
                    }
                }).setNegativeButton("아니요", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });

        findViewById(R.id.delete).setOnClickListener(new Button.OnClickListener(){
            public void onClick(View v){
                dialog.create();
                dialog.show();
            }
        });

    }
}
