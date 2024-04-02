package com.example.pushlib.pushpayload;

public enum NotifyEffectMode {
    /**
     * 启动首页，默认模式
     */
    EFFECT_MODE_APP(0),
    /**
     * 启动任意页面
     */
    EFFECT_MODE_CONTENT(1),
    /**
     * 启动一个web页面
     */
    EFFECT_MODE_WEB(2);
    private int type;
    NotifyEffectMode(int type){
        this.type = type;
    }
    public int getType(){
        return type;
    }
}
