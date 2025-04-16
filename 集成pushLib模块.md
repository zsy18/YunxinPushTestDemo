# 前言
本文档主要介绍集成完云信IM之后，如何快速集成推送的功能。通过把推送测试demo中的pushLib库集成到现有项目中，来快速完成推送功能的实现。
在开始集成pushLib库之前请确保您已：
* 已在云信控制台创建应用，获取 App Key。
* 已注册云信 IM 账号，获取 accid 和 token。并已经完成登录功能实现。
* 已经申请好要测试推送的证书，并在"云信控制台-证书管理页面"配置了对应推送证书
# 源码导入
1. 下载demo源码，源码地址https://github.com/zsy18/YunxinPushTestDemo/tree/main
2. 复制pushLib模块到您项目中
3. 复制pushConfig.gradle配置文件到您项目的根目录中，并在项目级的build.gradle文件中引入该配置文件
```java
apply from:"pushConfig.gradle"
```
# 添加配置
## 基本信息配置
1. 首先在项目根目录中找到pushConfig.gradle文件，需要先配置您项目的包名和云信的appkey
```java
   //主项目使用到的IM版本
   imVersion = "9.14.0"
   //云信的appkey，对应清单文件中的com.netease.nim.appKey的值
   APP_KEY = "set your appkey"
   //配置本次打包的应用包名，需要和厂商推送上申请证书用到的包名保持一致
   APPLICATION_ID = "set your applicationId"
```
> 更改 APPLICATION_ID 后同时需要把app模块下的“agconnect-services.json”、“google-services.json”、“mcs-services.json”三个文件删除或者替换成您自己配置文件，否则会导致应用编译报错。
2. 根据当前使用的IM版本，需要在pushConfig.gradle中调整对应的厂商推送的版本，对应关系可以参考云信官网文档https://doc.yunxin.163.com/messaging/concept/DM1MjI5MTE?platform=android
```java
    //fcm推送远端依赖
   PUSH_SDK_FCM = "com.google.firebase:firebase-messaging:23.4.1"
   //华为推送远端依赖
   PUSH_SDK_HW = "com.huawei.hms:push:6.9.0.300"
   //荣耀推送远端依赖
   PUSH_SDK_HONOR = "com.hihonor.mcs:push:7.0.41.301"
   //魅族推送远端依赖
   PUSH_SDK_MZ = "com.meizu.flyme.internet:push-internal:4.2.3"
   //小米推送库本地文件路径，替换pushLib/lib下的推送sdk后需要更改此处配置的文件名
   PUSH_SDK_XM = "libs/MiPush_SDK_Client_5_6_2-C_3rd.aar"
   //vivo推送库本地文件路径，替换pushLib/lib下的推送sdk后需要更改此处配置的文件名
   PUSH_SDK_VIVO = "libs/vivo_pushSDK_v3.0.0.4_484.aar"
   //oppo推送库本地文件路径，替换pushLib/lib下的推送sdk后需要更改此处配置的文件名
   PUSH_SDK_OPPO = "libs/com.heytap.msp_3.1.0.aar"
```
## FCM配置
1. 在pushConfig.gradle文件，添加推送相关配置信息
```java
   //云信控制台上FCM推送证书的证书名称
   fcmCertificateName = ""
   //fcm通知类型推送使用的通知通道，如果不配置服务端会默认指定为“nim_message_channel_001”
   fcmChannelId = "nim_message_channel_001"
   //是否使用新版（FCM HTTP V1 推送）,根据云信控制台对应的证书名是否为V1版本配置。
   fcmHttpV1Enable = true
```
2. 使用FCM官网上下载的客户端json证书替换项目中app模块下的google-services.json文件
3. 在项目的根目录build.gradle文件中的buildscript或者plugins中添加Google 服务插件
```java
buildscript{
   dependencies{
      //这里可以换为您自己项目中AGP的版本
      classpath 'com.android.tools.build:gradle:7.2.0'
      classpath 'com.google.gms:google-services:4.3.15'
   }
}
```
## 荣耀推送配置
1. 在pushConfig.gradle文件，添加推送相关配置信息
```java
   // 传入荣耀推送证书名
   honorAppId = ""
   //云信控制台上对应推送证书的证书名称
   honorCertificateName = ""
   //推送消息分类-配置前需在第三方平台（荣耀）申请消息自分类权益，具体详见 荣耀官方文档
   //此处配置后会替换云信控制台中-“第三方消息推送分类 子功能配置”中的设置
   honorImportance = "NORMAL"
```
2. 使用荣耀官网上下载的客户端json证书替换项目中app模块下的mcs-services.json文件
3. 把项目中签名证书的SHA256证书指纹添加到荣耀控制台应用下，或者用已经配置好的签名证书替换项目中的debug.keystore文件
```java
//项目中的debug.keystore文件的SHA256证书指纹为：
45:C9:74:1D:F5:67:38:42:A8:0A:C4:BD:BF:45:01:D9:4B:E5:04:79:F1:07:5E:28:CC:E8:12:D1:B2:C4:E8:8C
```
4. 在项目的根目录build.gradle文件中的buildscript或者plugins中添加荣耀服务插件
```java
buildscript{
dependencies{
//这里可以换为您自己项目中AGP的版本
classpath 'com.android.tools.build:gradle:7.2.0'
classpath 'com.hihonor.mcs:asplugin:2.0.1.300'
}
}
```
## 华为推送配置
1. 在pushConfig.gradle文件，添加推送相关配置信息
```java
// 传入华为推送的APP ID
hwAppId = ""
// 传入云信控制台上华为推送证书名称
hwCertificateName = ""
//推送消息分类-配置前需在第三方平台（华为）申请消息自分类权益，具体详见 华为官方文档
//此处配置后会替换云信控制台中-“第三方消息推送分类 子功能配置”中的设置
hwCategory = "IM"
//推送消息使用的通知通道
hwChannelId = ""
```
2. 使用华为官网上下载的客户端json证书替换项目中app模块下的agconnect-services.json文件
3. 把项目中签名证书的SHA256证书指纹添加到华为控制台应用下，或者用已经配置好的签名证书替换项目中的debug.keystore文件
```java
//项目中的debug.keystore文件的SHA256证书指纹为：
45:C9:74:1D:F5:67:38:42:A8:0A:C4:BD:BF:45:01:D9:4B:E5:04:79:F1:07:5E:28:CC:E8:12:D1:B2:C4:E8:8C
```
4. 在项目的根目录build.gradle文件中的buildscript或者plugins中添加华为服务插件
```java
buildscript{
dependencies{
//这里可以换为您自己项目中AGP的版本
classpath 'com.android.tools.build:gradle:7.2.0'
classpath 'com.huawei.agconnect:agcp:1.6.0.300'
}
}
```
## OPPO推送配置
1. 在pushConfig.gradle文件，添加推送相关配置信息
```java
oppoAppId = ""
oppoAppKey = ""
// 注意区分AppSercet与MasterSecret
oppoAppSercet = ""
// 传入云信控制台上配置的oppo推送证书名称
oppoCertificateName = ""
//推送消息分类-请提前在第三方平台（OPPO）申请channel并完成消息分类配置后，将channel对应的channel_id获取填入上方即可完成配置，具体详见 OPPO官方文档
//此处配置后会替换云信控制台中-“第三方消息推送分类 子功能配置”中的设置
oppoChannelId = ""
```
## vivo推送配置
1. 在pushConfig.gradle文件，添加推送相关配置信息
```java
// 传入云信控制台上配置的vivo推送证书名称
vivoCertificateName = ""
//你的 vivo 推送 app key
vivoAppKey = ""
//你的 vivo 推送的 app id
vivoAppId = ""
//推送消息分类-请提前在第三方平台（OPPO）申请channel并完成消息分类配置后，将channel对应的channel_id获取填入下方即可完成配置，具体详见 OPPO官方文档
//此处配置后会替换云信控制台中-“第三方消息推送分类 子功能配置”中的设置
vivoCategory = "IM"
```
## 小米推送配置
1. 在pushConfig.gradle文件，添加推送相关配置信息
```java
//小米推送平台获取到的AppId
xmAppId = "2882303761520055541"
//小米推送平台获取到的AppKey
xmAppKey = "5222005592541"
//云信控制台上小米推送对应的证书名称
xmCertificateName = "xiaomi_test1"
//推送消息分类-请提前在第三方平台（小米）申请channel并完成消息分类配置后，将channel对应的channel_id获取填入上方即可完成配置，具体详见 小米官方文档
//此处配置后会替换云信控制台中-“第三方消息推送分类 子功能配置”中的设置
xmChannelId = ""
```
## 魅族推送配置
1. 在pushConfig.gradle文件，添加推送相关配置信息
```java
mzAppId = ""
mzAppKey = ""
// 传入云信控制台上配置的魅族推送证书名称
mzCertificateName = ""
```
# 开始使用
## Step1：获取初始化配置信息
当初始化IM的时候，需要把pushConfig.gradle文件中的推送配置信息设置给IM，具体代码如下：
```java
SDKOptions sdkOptions = new SDKOptions();
sdkOptions.mixPushConfig = MixPushConfigGenerator.loadPushConfig();
NIMClient.init(this, loginInfo, sdkOptions);
```
## Step2：获取Payload信息
当发送消息、自定义系统通知、信令消息的时候，如果您需要定制厂商推送的个性行为，IM则需要您通过传入push payload参数来实现该功能。pushLib提供了快速获取push payload的功能，以“点击跳转”和“自定义参数”为例：
```java
NotifyClickAction clickAction = new NotifyClickAction.Builder()
        //设置点击行为为打开应用内任意一页面
        .setNotifyEffect(NotifyEffectMode.EFFECT_MODE_CONTENT)
        //设置需要打开页面配置的自定义IntentAction字段
        .setIntentAction("设置需要打开页面配置的自定义IntentAction字段")
        .build();
PushPayloadBuilder builder = new PushPayloadBuilder()
        //设置点击行为
        .setClickAction(clickAction);
//添加自定义字段
        .addCustomData("key", "value");
//生成payload字段
Map<String, Object>  payloadMap = builder.generatePayload();
```