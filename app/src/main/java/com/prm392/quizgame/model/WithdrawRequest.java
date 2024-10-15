package com.prm392.quizgame.model;

import com.google.firebase.firestore.ServerTimestamp;
import com.google.type.DateTime;

import java.util.Date;

public class WithdrawRequest {
    private String userId;
    private String emailAddress;
    private String requestBody;

    public WithdrawRequest() {
    }

    public WithdrawRequest(String userId, String emailAddress, String requestBody) {
        this.userId = userId;
        this.emailAddress = emailAddress;
        this.requestBody = requestBody;

    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public String getRequestBody() {
        return requestBody;
    }

    public void setRequestBody(String requestBody) {
        this.requestBody = requestBody;
    }
    @ServerTimestamp
    private Date createdAt;
    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }


}
