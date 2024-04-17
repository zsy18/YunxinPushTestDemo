package com.example.pushlib.pushpayload;

public enum PushPayloadBuilderType {
    XIAOMI(""),
    HUAWEI("hwField"),
    VIVO("vivoField"),
    OPPO("oppoField"),
    MEIZU(""),
    FCM("fcmField"),
    FCMV1("fcmFieldV1"),
    HONER("honorField"),
    APNS("apsField");
    private String type;
    PushPayloadBuilderType(String type){
        this.type = type;
    }
    public String getType(){
        return type;
    }
}
