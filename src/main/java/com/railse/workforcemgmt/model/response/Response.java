package com.railse.workforcemgmt.model.response;

public class Response<T> {
    private T data;
    private String message;
    private boolean success;

    // Constructor for success response with data
    public Response(T data) {
        this.data = data;
        this.success = true;
        this.message = "Success";
    }

    //Constructor with custom message
    public Response(T data, String message) {
        this.data = data;
        this.message = message;
        this.success = true;
    }

    // Constructor for failure
    public Response(String message, boolean success) {
        this.message = message;
        this.success = success;
    }

    // Getters and Setters
    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }
}
