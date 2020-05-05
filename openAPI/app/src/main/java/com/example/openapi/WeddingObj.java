package com.example.openapi;

import java.io.Serializable;

public class WeddingObj implements Serializable {
    private String full_addr_new;  //도로명 주소
    private String full_addr_old;  //지번 주소
    private double coord_x;        //위도
    private double coord_y;        //경도
    private String name;           //장소 이름
    private String gu;             //구
    private String dong;           //동
    private String phone;          //전화번호
    private String place_name;     //예식장 이름
    private String transport;      //대중교통 이용
    private String cost;              //가격
    private String parking_cost;      //주차 요금
    private String avail_time;     //가능 요일
    private String avail_obj;      //사용 가능한 시설
    private String details;        //상세 정보

    public WeddingObj(){}

    public WeddingObj(String full_addr_new, String full_addr_old, double coord_x, double coord_y, String name, String gu, String dong,
                   String phone, String place_name, String transport, String cost, String parking_cost, String avail_time, String avail_obj, String details){
        this.full_addr_new = full_addr_new; this.full_addr_old = full_addr_old;
        this.coord_x = coord_x; this.coord_y = coord_y;
        this.name = name; this.gu = gu; this.dong = dong;
        this.phone = phone; this.place_name = place_name;
        this.transport = transport; this.cost= cost; this.parking_cost = parking_cost;
        this.avail_time = avail_time; this.avail_obj = avail_obj;
        this.details = details;
    }

    public String getFull_addr_new() {
        return full_addr_new;
    }

    public void setFull_addr_new(String full_addr_new) {
        this.full_addr_new = full_addr_new;
    }

    public String getFull_addr_old() {
        return full_addr_old;
    }

    public void setFull_addr_old(String full_addr_old) {
        this.full_addr_old = full_addr_old;
    }

    public double getCoord_x() {
        return coord_x;
    }

    public void setCoord_x(double coord_x) {
        this.coord_x = coord_x;
    }

    public double getCoord_y() {
        return coord_y;
    }

    public void setCoord_y(double coord_y) {
        this.coord_y = coord_y;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGu() {
        return gu;
    }

    public void setGu(String gu) {
        this.gu = gu;
    }

    public String getDong() {
        return dong;
    }

    public void setDong(String dong) {
        this.dong = dong;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPlace_name() {
        return place_name;
    }

    public void setPlace_name(String place_name) {
        this.place_name = place_name;
    }

    public String getTransport() {
        return transport;
    }

    public void setTransport(String transport) {
        this.transport = transport;
    }

    public String getCost() {
        return cost;
    }

    public void setCost(String cost) {
        this.cost = cost;
    }

    public String getParking_cost() {
        return parking_cost;
    }

    public void setParking_cost(String parking_cost) {
        this.parking_cost = parking_cost;
    }

    public String getAvail_time() {
        return avail_time;
    }

    public void setAvail_time(String avail_time) {
        this.avail_time = avail_time;
    }

    public String getAvail_obj() {
        return avail_obj;
    }

    public void setAvail_obj(String avail_obj) {
        this.avail_obj = avail_obj;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }
}
