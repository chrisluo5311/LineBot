# LineBot (疫情小幫手)
![目錄](https://github.com/chrisluo5311/LineBot/blob/master/src/main/resources/static/menufinal.jpg "line bot richmenu")

### 安装各软件对应版本(仅供参考):
|  软件  |  版本  |   说明   |
|:------:|:--------:|:------------:|
|  JDK  |    | Spring boot对低版支持无测试过 |
|  Redis  |    |  |
|  MS SQLServer  |    |  |
### 定时任务说明

### 目錄內容 
1. 功能表、負責人、狀態、使用技術 

|  功能  |    負責人    | 狀態 | 主要技術 | 內容說明 |
|:------:|:----------:|:------------:|:------------:| :----------:|
|  查詢今日確診數  |  chris  | `完成` | okhttp、jsoup | SpringBoot Scheduling定時爬蟲，`cron`表示式: `0 0/5 14 * * ?`(每天14:00開始到14:55，每五分鐘執行一次)  |
|  哪裡買口罩  |  chris  | `尚未開始` | leaflet |           |
|  所處位置疫情狀況  |  chris  |  `尚未開始` | leaflet |           |
|  國內外疫情  |  chris  |  `尚未開始`  | okhttp、jsoup |           |
|  疫苗施打統計圖  |  chris  |  `尚未開始`  | okhttp、jsoup |            |
|  其他統計表  |  chris  |  `尚未開始`  | okhttp、jsoup |             |

2. 資料庫配置 
> sqlserver
```java
spring.jpa.hibernate.ddl-auto=update
spring.datasource.url=jdbc:sqlserver://localhost:1433;databaseName=linebot
spring.datasource.username=watcher
spring.datasource.password=P@ssw0rd
server.port=9090
```

3. 緩存 
> Redis

### 測試須知
1. [ngrok下載請點我](https://ngrok.com/download "ngrok")
2. 開啟ngrok，登入ngrok的網站
3. 於ngrok輸入 **taskkill /f /im ngrok.exe**
4. 再將ngrok網站上的authtoken複製到ngrok上面(**不要複製到斜線**)
5. 再輸ngrok http 9090  
6. 出現online連線成功

### 參考網站
1. [衛福部疾管署](https://www.cdc.gov.tw/ "link") 
2. [全球疫情地圖](https://covid-19.nchc.org.tw/ "全球疫情地圖")
3. [健保特約機構口罩剩餘數量明細](https://data.gov.tw/dataset/116285 "口罩link")
4. [台灣電子地圖服務網](https://www.map.com.tw/ "台灣電子地圖服務網")
5. [LINE Messaging API SDK for Java](https://github.com/line/line-bot-sdk-java "LineBot API SDK")
6. [Messaging API reference](https://developers.line.biz/en/reference/messaging-api/ "LineBot API 文件")
7. [口罩即時查](https://wenyo.github.io/maskmap/ "口罩即時查")
