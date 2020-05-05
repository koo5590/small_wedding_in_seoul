package com.example.openapi;


/** contains info of user **/
public class UserData {
    private String userEmail;
    private String nickname;

    public UserData(){}

    public UserData(String userEmail, String nickname){
        this.userEmail = userEmail;
        this.nickname = nickname;
    }

    /** getter and setter **/
    public String getUserEmail() {
        return userEmail;
    }
    public String getNickname(){
        return nickname;
    }

    public void setNickname(String nickname){
        this.nickname = nickname;
    }
    public void setUserEmail(String userEmail){
        this.userEmail = userEmail;
    }
}
