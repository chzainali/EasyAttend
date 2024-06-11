package com.example.easyattend.models;

import java.io.Serializable;

public class LeaveModel implements Serializable {
    String leaveId, userId, teacherId, subject, image, type, date, reason, status;

    public LeaveModel() {
    }

    public LeaveModel(String leaveId, String userId, String teacherId, String subject, String image, String type, String date, String reason, String status) {
        this.leaveId = leaveId;
        this.userId = userId;
        this.teacherId = teacherId;
        this.subject = subject;
        this.image = image;
        this.type = type;
        this.date = date;
        this.reason = reason;
        this.status = status;
    }

    public String getLeaveId() {
        return leaveId;
    }

    public void setLeaveId(String leaveId) {
        this.leaveId = leaveId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getTeacherId() {
        return teacherId;
    }

    public void setTeacherId(String teacherId) {
        this.teacherId = teacherId;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
