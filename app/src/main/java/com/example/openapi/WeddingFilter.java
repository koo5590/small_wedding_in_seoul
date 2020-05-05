package com.example.openapi;
import java.util.ArrayList;
import android.util.Log;


public class WeddingFilter {
    private static WeddingFilter weddingFilter;

    private int cost=0;
    private int parking_cost =0;
    private String gu = "전체";
    private boolean check[]= {false,false,false};

    private ArrayList<WeddingObj> weddingObjList;
    private ArrayList<WeddingObj> filteredWedding;

    public static WeddingFilter getInstance(){
        if(weddingFilter==null){
            weddingFilter = new WeddingFilter();
        }

        return weddingFilter;
    }

    /******** constructor **********/
    private WeddingFilter(){
        this.weddingObjList = new ArrayList<WeddingObj>();
        this.filteredWedding = new ArrayList<WeddingObj>();
    }

    /****** getter and setter ********/
    public int getCost() {
        return cost;
    }

    public void setCost(int cost) {
        this.cost = cost;
    }

    public int getParking_cost() {
        return parking_cost;
    }

    public void setParking_cost(int parking_cost) {
        this.parking_cost = parking_cost;
    }

    public String getGu() {
        return gu;
    }

    public void setGu(String gu) {
        this.gu = gu;
    }

    public void setWeddingObjList(ArrayList<WeddingObj> weddingObjList){
        this.weddingObjList = weddingObjList;
    }

    public ArrayList<WeddingObj> getWeddingList(){
        return filteredWedding;
    }

    public void printWedding() {
        for(int i=0;i<filteredWedding.size();i++) {
            System.out.println(filteredWedding.get(i).getName());
        }
    }

    public void setWeddingListClear(){
        this.filteredWedding.clear();
    }


    /******** filter **********/
    public void filter(){
        boolean flag;
        WeddingObj wedding;
        String item_cost;
        String item_cost_replaced;
        int item_cost_int;

        check[0] = check[1] = check[2] = false;

        //check if whole
        if(this.cost == 0) {
            this.check[0]=true;
        }
        if(this.parking_cost == 0) {
            this.check[1]=true;
        }
        if(this.gu.equals("전체")) {
            this.check[2]=true;
        }

        //check each weddingObj
        for(int i=0;i<weddingObjList.size();i++) {
            flag = true;
            wedding = weddingObjList.get(i);
            //cost: if not "whole"
            if(check[0]==false) {
                item_cost = wedding.getCost();

                //if free, set item_cost to "0"
                if(item_cost.contains("무료") || item_cost.equals("-"))
                    item_cost ="0";
                else if(item_cost.contains("유료") || item_cost.contains("시간당"))
                    item_cost="10";
                //convert ALL non-numeric character to empty string
                item_cost_replaced = item_cost.replaceAll("[^0-9]+", "");
                //convert String to Integer
                item_cost_int = Integer.parseInt(item_cost_replaced);

                switch(this.cost){
                    //free
                    case 1:
                        if(item_cost_int!=0)
                            flag = false;
                        break;
                    //less than 100,000
                    case 2:
                        if(item_cost_int > 100000)
                            flag = false;
                        break;
                    //less than 300,000
                    case 3:
                        if(item_cost_int > 300000)
                            flag = false;
                        break;
                }

            }
            if(check[1]==false) {
                if(!(wedding.getParking_cost().equals("무료"))) {
                    flag=false;
                }
            }
            if(check[2]==false) {
                if(!(this.gu.equals(wedding.getGu()))) {
                    flag=false;
                }
            }
            if(flag==true) {
                filteredWedding.add(wedding);
            }
        }
    }


}