package com.example.easyattend.models;

import java.io.Serializable;

public class UserModel implements Serializable {
    String userId, userType, userName, userEmail, userPhone, userPassword, userImage, teacherSubject, active;

    public UserModel() {
    }

    public UserModel(String userId, String userType, String userName, String userEmail, String userPhone, String userPassword, String userImage, String teacherSubject, String active) {
        this.userId = userId;
        this.userType = userType;
        this.userName = userName;
        this.userEmail = userEmail;
        this.userPhone = userPhone;
        this.userPassword = userPassword;
        this.userImage = userImage;
        this.teacherSubject = teacherSubject;
        this.active = active;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getUserPhone() {
        return userPhone;
    }

    public void setUserPhone(String userPhone) {
        this.userPhone = userPhone;
    }

    public String getUserPassword() {
        return userPassword;
    }

    public void setUserPassword(String userPassword) {
        this.userPassword = userPassword;
    }

    public String getUserImage() {
        return userImage;
    }

    public void setUserImage(String userImage) {
        this.userImage = userImage;
    }

    public String getTeacherSubject() {
        return teacherSubject;
    }

    public void setTeacherSubject(String teacherSubject) {
        this.teacherSubject = teacherSubject;
    }

    public String getActive() {
        return active;
    }

    public void setActive(String active) {
        this.active = active;
    }
}
