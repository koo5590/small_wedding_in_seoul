package com.example.openapi;

import androidx.annotation.NonNull;
import androidx.annotation.UiThread;
import android.graphics.Color;

import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import android.content.Intent;
import android.os.Bundle;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import com.naver.maps.geometry.LatLng;
import com.naver.maps.map.CameraPosition;
import com.naver.maps.map.MapFragment;
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.OnMapReadyCallback;
import com.naver.maps.map.overlay.Marker;
import com.naver.maps.map.util.MarkerIcons;

import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;


/**** shows detail of a wedding obj and list of reviews of this place ****/
public class DetailActivity extends FragmentActivity implements OnMapReadyCallback {
    WeddingObj weddingObj;  //current wedding obj
    TextView nameText;      //name of wedding place

    ListView listView;      //list of details
    ListView reviewList;    //list of reviews
    Button writeRv;         //write review button

    //for list of details
    private MyAdaptor adaptor;
    private ArrayList<ListItem>items;

    //for list of reviews
    private ReviewAdaptor reviewAdaptor;
    private ArrayList<ListItem> reviewItems;  //ListItem contains only title&date of review

    //firebase database
    private DatabaseReference mDatabase;

    //saves ReviewItem objects (for detailReviewActivity)
    private ArrayList<ReviewItem> reviewPosts; //contains all info of review


    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        //receive one WeddingObj data from MainActivity
        Intent intent = getIntent();
        weddingObj = (WeddingObj)intent.getSerializableExtra("object");

        //show name of weddingObj on the screen
        //show name of weddingObj on the screen
        nameText = (TextView)findViewById(R.id.name);
        nameText.append(weddingObj.getName());

        //detail page
        listView = (ListView)findViewById(R.id.listView);
        //review list page
        reviewList = (ListView) findViewById(R.id.reviewList);

        //write new review button
        writeRv = (Button)findViewById(R.id.writeRv);
        //appears when there is no review yet

        //Naver Map
        initMap();  //initiate map
        initData(); //initiate data to show on listView
        printList();//show the whole data one listView

        //Firebase Database
        mDatabase = FirebaseDatabase.getInstance().getReference();

        //create Button that goes back to MainActivity

        findViewById(R.id.back).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        //detail/review switching buttons
        //detail list button
        findViewById(R.id.detailB).setOnClickListener(new Button.OnClickListener(){
            public void onClick(View v){
                listView.setVisibility(View.VISIBLE);
                reviewList.setVisibility(View.GONE);
                writeRv.setVisibility(View.GONE);
            }
        });
        //review list button
        findViewById(R.id.reviewB).setOnClickListener(new Button.OnClickListener(){
            public void onClick(View v){
                listView.setVisibility(View.GONE);
                reviewList.setVisibility(View.VISIBLE);
                writeRv.setVisibility(View.VISIBLE);

                //load reviews of this wedding obj from database
                mDatabase.child("posts").child(weddingObj.getName()).addListenerForSingleValueEvent(
                        new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                reviewItems = new ArrayList<ListItem>();    //for list view
                                reviewPosts = new ArrayList<ReviewItem>();  //for review detail

                                for(DataSnapshot postSnapShot: dataSnapshot.getChildren()){
                                    ReviewItem reviewItem = postSnapShot.getValue(ReviewItem.class);
                                    reviewPosts.add(reviewItem);
                                    //Log.d("목록", reviewItem.getContent());
                                    reviewItems.add(new ListItem(reviewItem.content, reviewItem.date));
                                }

                                //no review written yet
                                if(reviewItems.isEmpty())
                                    Toast.makeText(DetailActivity.this, "아직 후기가 없습니다.", Toast.LENGTH_SHORT).show();
                                //show list of reviews with title and written date
                                else{
                                    printReviewList();
                                    reviewList.setVisibility(View.VISIBLE);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        }
                );
            }
        });

        //click on review item
        reviewList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                ReviewItem review = reviewPosts.get(i);

                //move to review detail page
                Intent intent = new Intent(getApplicationContext(), DetailReviewActivity.class);
                intent.putExtra("post", review);
                intent.putExtra("object", weddingObj);
                intent.putExtra("from", "detail");
                startActivity(intent);
                finish();
            }
        });


        //write new review button
        writeRv.setOnClickListener(new Button.OnClickListener(){
            public void onClick(View v){
                //move to writing review page
                Intent intent = new Intent(getApplicationContext(), WriteReviewActivity.class);
                intent.putExtra("object", weddingObj);
                intent.putExtra("state", "new");
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
    }

    /********  Map Configure  ********/
    @UiThread
    @Override
    public void onMapReady(@NonNull NaverMap naverMap) {
        LatLng location = new LatLng(weddingObj.getCoord_y(), weddingObj.getCoord_x());
        CameraPosition cameraPosition = new CameraPosition(location, 15);

        naverMap.setLayerGroupEnabled(NaverMap.LAYER_GROUP_TRANSIT, true);

        //set camera position at which the place is located
        naverMap.setCameraPosition(cameraPosition);

        //create Marker and set its position at the same location as camera
        Marker marker = new Marker();
        marker.setPosition(new LatLng(weddingObj.getCoord_y(), weddingObj.getCoord_x()));
        //set marker color
        marker.setIcon(MarkerIcons.BLACK);
        marker.setIconTintColor(Color.rgb(231, 138, 138));
        //set marker size
        marker.setWidth(Marker.SIZE_AUTO);
        marker.setHeight(Marker.SIZE_AUTO);
        //set marker on map
        marker.setMap(naverMap);

    }

    /********  initialize Map  **********/
    private void initMap(){
        FragmentManager fm = getSupportFragmentManager();
        MapFragment mapFragment = (MapFragment)fm.findFragmentById(R.id.map);
        if(mapFragment == null){
            mapFragment = MapFragment.newInstance();
            fm.beginTransaction().add(R.id.map, mapFragment).commit();
        }

        mapFragment.getMapAsync(this);
    }

    private void initData(){
        items = new ArrayList<ListItem>();

        items.add(new ListItem("*주소",weddingObj.getFull_addr_new()));
        items.add(new ListItem("*전화번호",weddingObj.getPhone()));
        items.add(new ListItem("*예식장 이름",weddingObj.getPlace_name()));
        items.add(new ListItem("*교통편",weddingObj.getTransport()));
        items.add(new ListItem("*대관 비용",weddingObj.getCost()));
        items.add(new ListItem("*주차 요금",weddingObj.getParking_cost()));
        items.add(new ListItem("*사용가능 요일/시설 정보",weddingObj.getAvail_time()));
        items.add(new ListItem("*추가 제공 사항",weddingObj.getAvail_obj()));
        items.add(new ListItem("*상세 정보",weddingObj.getDetails()));

    }

    /********* show details ************/
    private void printList(){
        adaptor = new MyAdaptor(items, getApplicationContext());
        listView.setAdapter(adaptor);
    }

    /********* show list of reviews ************/
    private void printReviewList(){
        reviewAdaptor = new ReviewAdaptor(reviewPosts, getApplicationContext());
        reviewList.setAdapter(reviewAdaptor);
    }

}
