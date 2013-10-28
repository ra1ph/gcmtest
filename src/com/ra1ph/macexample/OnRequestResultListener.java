package com.ra1ph.macexample;

public interface OnRequestResultListener {
    public void onSuccess(RequestResult result);
    public void onFail(RequestResult result);
    public void onProgressUpdate(Integer progress);
}
