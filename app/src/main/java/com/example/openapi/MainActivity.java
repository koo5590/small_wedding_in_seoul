package com.example.openapi;

import android.os.Bundle;
import android.view.View;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Button;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.widget.EditText;
import android.widget.GridView;
import android.view.KeyEvent;

import java.util.ArrayList;

import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;


public class MainActivity extends AppCompatActivity {
    boolean isUp;
    View filter_view;
    View main_view;
    GridView gridView;
    Spinner spinner;

    Button closeButton;
    Button filterButton;

    RadioButton cost1;
    RadioButton cost2;
    RadioButton cost3;
    RadioButton cost4;

    RadioButton parking1;
    RadioButton parking2;

    String gu;
    int cost;
    int parking;

    private EditText search_by_text;
    String getData;

    private ArrayList<WeddingObj> weddingList = new ArrayList<>();
    private ArrayList<WeddingObj> partWeddingList = new ArrayList<>();

    private ImageAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // filter screen
        filter_view = (View)findViewById(R.id.filter_screen);
        main_view = (View)findViewById(R.id.main);

        //set filter screen invisible
        filter_view.setVisibility(View.INVISIBLE);
        isUp = false;

        //radio buttons
        gu = "전체";
        cost = parking = 0; //whole

        RadioButton.OnClickListener onClickCost = new RadioButton.OnClickListener(){
            public void onClick(View v){
                if(cost1.isChecked())
                    cost = 0;  //whole
                else if(cost2.isChecked())
                    cost = 1; //free
                else if(cost3.isChecked())
                    cost = 2; //less than 100,000
                else
                    cost = 3; //less than 300,000
            }
        };

        RadioButton.OnClickListener onClickParking = new RadioButton.OnClickListener(){
            public void onClick(View v){
                if(parking1.isChecked())
                    parking = 0; //whole
                else
                    parking = 1; //free
            }
        };

        //cost radio buttons
        cost1 = (RadioButton)findViewById(R.id.cost1);
        cost2 = (RadioButton)findViewById(R.id.cost2);
        cost3 = (RadioButton)findViewById(R.id.cost3);
        cost4 = (RadioButton)findViewById(R.id.cost4);
        cost1.setOnClickListener(onClickCost);
        cost2.setOnClickListener(onClickCost);
        cost3.setOnClickListener(onClickCost);
        cost4.setOnClickListener(onClickCost);
        cost1.setChecked(true); //cost1 is checked

        //parking radio buttons
        parking1 = (RadioButton)findViewById(R.id.parking1);
        parking2 = (RadioButton)findViewById(R.id.parking2);
        parking1.setOnClickListener(onClickParking);
        parking2.setOnClickListener(onClickParking);
        parking1.setChecked(true); //parking1 is checked

        closeButton = (Button)findViewById(R.id.close_filter);
        filterButton = (Button) findViewById(R.id.filter_button);
        search_by_text = (EditText) findViewById(R.id.search_text);
        gridView = (GridView)findViewById(R.id.item);

        weddingList = ManageAPI.getInstance().getWeddingArrayList();
        partWeddingList.addAll(weddingList);
        adapter = new ImageAdapter(this,partWeddingList);
        gridView.setAdapter(adapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            public void onItemClick(AdapterView<?> parent, View view, int position, long id){
                Intent intent = new Intent(getApplicationContext(), DetailActivity.class);

                WeddingObj obj = partWeddingList.get(position);
                intent.putExtra("object", obj);

                startActivity(intent);
            }
        });

        // spinner for Gu
        spinner = (Spinner)findViewById(R.id.gu);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                gu = (String)adapterView.getItemAtPosition(i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        main_view.bringToFront();
        main_view.requestLayout();

        search_by_text.setOnKeyListener(new View.OnKeyListener(){
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event){
                if((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)){
                    searchButtonClicked(gridView);
                    return true;
                }
                return false;
            }
        });


    }

    @Override
    public void onBackPressed(){
        if(isUp) {
            slideDown(filter_view);
            isUp = false;
        }
        else {
            closeProgram(main_view);
        }
    }

    //slide up screen
    public void slideUp(View view){
        view.setVisibility(View.VISIBLE);
        TranslateAnimation animate = new TranslateAnimation(0,0,view.getHeight(),0);
        animate.setDuration(500);
        animate.setFillAfter(true);

        view.startAnimation(animate);
        gridView.setEnabled(false);
        view.bringToFront();
        view.requestLayout();

    }

    //slide down screen
    public void slideDown(View view){
        TranslateAnimation animate = new TranslateAnimation(0,0,0,view.getHeight());
        animate.setDuration(500);
        animate.setFillAfter(true);
        view.startAnimation(animate);

        view.setVisibility(View.INVISIBLE);
        main_view.requestLayout();
        gridView.setEnabled(true);
        main_view.bringToFront();
        main_view.requestLayout();
    }

    //slide control
    public void onSlideViewButtonClick(View view){
        if(isUp){
            slideDown(filter_view);
        }
        else{
            slideUp(filter_view);
        }
        isUp = !isUp;
    }

    public void showMessage(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("알림");
        builder.setMessage("존재하지 않는 결혼식장명입니다.");

        builder.setPositiveButton("닫기",new DialogInterface.OnClickListener(){

            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    //실질적 검색
    public void search(String text,ArrayList<WeddingObj> initialWeddingList){
        //입력이 들어올 때 마다 리스트를 비워준다.
        partWeddingList.clear();
        //아무 입력이 없는 경우는 모든 결혼식장을 보여준다.
        if(text.length()==0){
            partWeddingList.addAll(initialWeddingList);
            //partWeddingList=weddingList;
        }
        //입력한 문자가 있는 경우
        else{
            for(int i=0;i<initialWeddingList.size();i++){
                //입력한 문자가 포함된 이름을 가진 결혼식장인 경우
                if(initialWeddingList.get(i).getName().contains(text)){
                    partWeddingList.add(initialWeddingList.get(i));
                }
            }
        }
        //어댑터 갱신
        adapter.notifyDataSetChanged();
        gridView.setAdapter(adapter);
    }

    public void validateFilter(View view) {

        WeddingFilter.getInstance();
        WeddingFilter.getInstance().setCost(cost);
        WeddingFilter.getInstance().setGu(gu);
        WeddingFilter.getInstance().setParking_cost(parking);
        WeddingFilter.getInstance().setWeddingListClear();

        WeddingFilter.getInstance().setWeddingObjList(weddingList);
        WeddingFilter.getInstance().filter();


        partWeddingList.clear();
        partWeddingList.addAll(WeddingFilter.getInstance().getWeddingList());

        adapter.notifyDataSetChanged();
        gridView.setAdapter(adapter);
        onSlideViewButtonClick(filter_view);
    }

    public void searchButtonClicked(View view){
        String text = search_by_text.getText().toString();
        search(text,weddingList);

        if(partWeddingList.isEmpty()){
            showMessage();
        }
    }

    public void closeProgram(View view){
        new AlertDialog.Builder(this)
                .setTitle("스몰 웨딩 in SEOUL")
                .setMessage("종료하시겠습니까?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        moveTaskToBack(true);
                        finish();
                        android.os.Process.killProcess(android.os.Process.myPid());
                    }})
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        ;
                    }})
                .show();
    }


}