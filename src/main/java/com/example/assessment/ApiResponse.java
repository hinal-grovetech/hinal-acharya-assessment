package com.example.assessment;

public class ApiResponse<T> {
    private boolean success;
    private T data;
    private String msg;


    public ApiResponse(boolean success ,T data,String msg)
    {
        this.success = success;
        this.data = data;
        this.msg = msg;
    }


    public boolean isSuceess() { return success; }
    public T getData() { return data; }
    public String getmsg() { return msg; }
}