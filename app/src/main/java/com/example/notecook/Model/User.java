package com.example.notecook.Model;

import com.google.gson.annotations.SerializedName;

public class User {
    @SerializedName("id")
    private int Id_User;
    @SerializedName("firstname")
    private String firstname;
    @SerializedName("username")
    private String username;
    @SerializedName("lastname")
    private String lastname;
    @SerializedName("birthday")
    private String Birthday;
    @SerializedName("email")
    private String Email;
    //@SerializedName("icon")
    private byte[] icon;
    @SerializedName("phoneNumber")
    private String Phonenumber;
    @SerializedName("password")
    private String PassWord;
    @SerializedName("status")
    private String Status;
    @SerializedName("grade")
    private String Grade;
    @SerializedName("url")
    private String pathimageuser;

    public User() {
    }


    public User(String user_name, String user_lastname, String birthday,
                String email, byte[] icon, String phonenumber, String passWord,
                String status, String grade) {

        firstname = user_name;
        lastname = user_lastname;
        Birthday = birthday;
        Email = email;
        this.icon = icon;
        Phonenumber = phonenumber;
        PassWord = passWord;
        Status = status;
        Grade = grade;
    }
    public User(int id,String username,String firstname, String lastname, String birthday, String email, byte[] icon, String phonenumber, String passWord, String status, String grade,String pathimage) {
        this.Id_User = id;
        this.firstname = firstname;
        this.username = username;
        this.lastname = lastname;
        Birthday = birthday;
        Email = email;
        this.icon = icon;
        Phonenumber = phonenumber;
        PassWord = passWord;
        Status = status;
        Grade = grade;
        this.pathimageuser = pathimage;
    }

    public User(String username,String firstname, String lastname, String birthday, String email, byte[] icon, String phonenumber, String passWord, String status, String grade) {
        this.firstname = firstname;
        this.username = username;
        this.lastname = lastname;
        Birthday = birthday;
        Email = email;
        this.icon = icon;
        Phonenumber = phonenumber;
        PassWord = passWord;
        Status = status;
        Grade = grade;
    }

    public int getId_User() {
        return Id_User;
    }

    public void setId_User(int id_User) {
        Id_User = id_User;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getBirthday() {
        return Birthday;
    }

    public void setBirthday(String birthday) {
        Birthday = birthday;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public byte[] getIcon() {
        return icon;
    }

    public void setIcon(byte[] icon) {
        this.icon = icon;
    }

    public String getPhonenumber() {
        return Phonenumber;
    }

    public void setPhonenumber(String phonenumber) {
        Phonenumber = phonenumber;
    }

    public String getPassWord() {
        return PassWord;
    }

    public void setPassWord(String passWord) {
        PassWord = passWord;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }

    public String getGrade() {
        return Grade;
    }

    public void setGrade(String grade) {
        Grade = grade;
    }

    public String getPathimageuser() {
        return pathimageuser;
    }

    public void setPathimageuser(String pathimageuser) {
        this.pathimageuser = pathimageuser;
    }
}
