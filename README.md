# 前提条件
在开始运行示例项目之前，请确保您已经完成了以下条目：
* 已在云信控制台创建应用，获取 App Key，并开通了IM功能。
* 已注册云信 IM 账号，获取 accid 和 token。
* 已经申请好要测试推送的证书，并在"云信控制台-证书管理页面"配置了对应推送证书
# 跑通流程
## 一、下载demo
1. demo地址为：点击下载
2. 将demo源码导入到Android Studio，其代码结构为：
目录说明app应用主入口pushLib推送模块相关代码，主要包含MixPushConfig和Pushpayload的生成。pushConfig.gradle推送配置文件，在项目根目录下
## 二、添加配置
### 基本信息配置
1. 首先在项目根目录中找到pushConfig.gradle文件，需要先配置您项目的包名和云信的appkey
```java
    //云信的appkey，对应清单文件中的com.netease.nim.appKey的值
    APP_KEY = "set your appkey"
    //配置本次打包的应用包名，需要和厂商推送上申请证书用到的包名保持一致
    APPLICATION_ID = "set your applicationId"
```
> 更改 APPLICATION_ID 后同时需要把app模块下的“agconnect-services.json”、“google-services.json”、“mcs-services.json”三个文件删除或者替换成您自己配置文件，否则会导致应用编译报错。
### FCM配置
1. 在pushConfig.gradle文件，添加推送相关配置信息
```java
//云信控制台上FCM推送证书的名
fcmCertificateName = ""
//fcm通知类型推送使用的通知通道，如果不配置服务端会默认指定为“nim_message_channel_001”
fcmChannelId = ""
//是否使用新版（FCM HTTP V1 推送）,根据云信控制台对应的证书名是否为V1版本配置。
fcmHttpV1Enable = true
```
2. 使用FCM官网上下载的客户端json证书替换项目中app模块下的google-services.json文件
### 荣耀推送配置
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
### 华为推送配置
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
### OPPO推送配置
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
### vivo推送配置
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
### 小米推送配置
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
### 魅族推送配置
1. 在pushConfig.gradle文件，添加推送相关配置信息
```java
mzAppId = ""
mzAppKey = ""
// 传入云信控制台上配置的魅族推送证书名称
mzCertificateName = ""
```
## 三、测试推送
1. 测试某个厂商推送需要把应用安装在对应厂商的设备上，fcm建议使用google或者三星的手机测试。
2. 登录IM，确认获取到token后杀死接收端应用。
3. 登录IM，填入接收推送账号的accid，并发送消息。


