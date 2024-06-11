package com.example.easyattend.models;

public class AttendanceModel {
    String attendanceId, userId, teacherId, subject, date, status;

    public AttendanceModel() {
    }

    public AttendanceModel(String attendanceId, String userId, String teacherId, String subject, String date, String status) {
        this.attendanceId = attendanceId;
        this.userId = userId;
        this.teacherId = teacherId;
        this.subject = subject;
        this.date = date;
        this.status = status;
    }

    public String getAttendanceId() {
        return attendanceId;
    }

    public void setAttendanceId(String attendanceId) {
        this.attendanceId = attendanceId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
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
