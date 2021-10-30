# LineBot (疫情小幫手)
![目錄](https://github.com/chrisluo5311/LineBot/blob/master/src/main/resources/static/menufinal.jpg "line bot richmenu")

### 安裝軟體對應版本(僅供参考):
|  軟體  |  版本  |   說明   |
|:------:|:--------:|:------------:|
|  JDK  | 11.0.10   | Spring boot對低版支持無測試過 |
|  Redis  | 5.0.10   | 低版支持無測試過  |
|  MS SQLServer  | MS SQLServer2019  | 低版支持無測試過 |

### 定时任务说明

|  功能  |  任務說明  |
|:------:|:--------:|
|  查詢今日確診數   | `cron`表示式: `0 0/5 14 * * ?`(每天14:00開始到14:55，每五分鐘執行一次)  |
|  查詢藥局口罩剩餘數目   | `cron`表示式: `0 0 0/1 * * ?`(每小時執行一次)  |
|  定時新增藥局資訊至資料庫   | @Scheduled(fixedRate = 1*TimeUnit.HOUR)(每小時執行一次)<br>使用自定義hibernate.jdbc.batch_size=1000。<br>Batch Size是設定對資料庫進行批量刪除，批量更新和批量插入的時候的批次大小 |

### 目錄內容 
1. 功能表、負責人、狀態、使用技術 

|  功能  |    負責人    | 狀態 | 主要技術 | 內容說明 |
|:------:|:----------:|:------------:|:------------:| :----------:|
|  查詢今日確診數  |  chris  | `完成` | okhttp、jsoup | 解析新聞稿並擷取出新增確診人數、校正回歸數、及死亡數  |
|  哪裡買口罩  |  chris  | `完成` | okhttp、jsoup | 發送請求至 [口罩即時查](https://wenyo.github.io/maskmap/ "口罩即時查") 後台的Request URL，返回含經緯度的藥局資訊，解析後發送LocationMessage給使用者   |
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


3. 相關Maven依賴 

|  套件  |  版本  |   备注   |
|:------:|:--------:|:------------:|
|  okhttp  | 3.14.9 |  |
|  jsoup  | 1.13.1 |  |
| Selenium | 3.141.59 |  |
|  Redis  | 5.0.10 |  |
|  lombok  | 1.18.20 |  |

### 測試須知
1. [ngrok下載](https://ngrok.com/download "ngrok")
2. ngrok http 9090  

### 參考網站
1. [衛福部疾管署](https://www.cdc.gov.tw/ "link") 
2. [全球疫情地圖](https://covid-19.nchc.org.tw/ "全球疫情地圖")
3. [疫苗接種統計圖](https://covid-19.nchc.org.tw/dt_002-csse_covid_19_daily_reports_vaccine_city2.php "疫苗接種統計圖")
4. [Infogram_疫苗累計施打人數統計圖&各縣市覆蓋率](https://infogram.com/f25f5a66-bd5e-4272-b4b4-be1258a276a8 "疫苗統計圖")
5. [CDC疫苗統計資料pdf](https://www.cdc.gov.tw/Category/Page/9jFXNbCe-sFK9EImRRi2Og "疫苗統計pdf")
6. [健保特約機構口罩剩餘數量明細](https://data.gov.tw/dataset/116285 "口罩link") 或 [口罩即時查](https://wenyo.github.io/maskmap/ "口罩即時查")(帶經緯度)
7. [台灣電子地圖服務網](https://www.map.com.tw/ "台灣電子地圖服務網")
8. [LINE Messaging API SDK for Java](https://github.com/line/line-bot-sdk-java "LineBot API SDK")
9. [Messaging API reference](https://developers.line.biz/en/reference/messaging-api/ "LineBot API 文件")

