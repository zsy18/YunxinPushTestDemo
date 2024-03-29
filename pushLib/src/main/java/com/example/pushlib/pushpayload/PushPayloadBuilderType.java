package com.example.pushlib.pushpayload;

public enum PushPayloadBuilderType {
    XIAOMI(""),
    HUAWEI("hwField"),
    VIVO("vivoField"),
    OPPO("oppoField"),
    MEIZU(""),
    FCS("fcmField"),
    HONER("honorField");
    private String type;
    PushPayloadBuilderType(String type){
        this.type = type;
    }
    public String getType(){
        return type;
    }
}
