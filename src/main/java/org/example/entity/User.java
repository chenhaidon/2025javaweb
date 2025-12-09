package org.example.entity;

import java.util.List;

public class User {

    private int id;
    private String username;
    private String password;

    private String phone;
    private String gender;
    private String hobby;
    private List<byte[]> avatar;
    
    // 权限字段：0-普通用户、1-管理员、2-超级管理员
    private int role = 0;

    // 构造方法、getter和setter方法

    public User() {
    }

    public User(int id, String username,  String hobby, String phone, String password, String gender,List<byte[]> avatar) {
        this.id = id;
        this.avatar = avatar;
        this.username = username;
        this.hobby = hobby;
        this.phone = phone;
        this.password = password;
        this.gender = gender;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public List<byte[]> getAvatar() {
        return avatar;
    }

    public void setAvatar(List<byte[]> avatar) {
        this.avatar = avatar;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getHobby() {
        return hobby;
    }

    public void setHobby(String hobby) {
        this.hobby = hobby;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public int getRole() {
        return role;
    }

    public void setRole(int role) {
        this.role = role;
    }
}
