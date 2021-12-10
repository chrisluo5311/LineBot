# LineBot (LINE Messaging API + Spring Boot+ Rabbitmq + Selenium)
### 疫情小幫手(目錄-僅參考):
![目錄](https://github.com/chrisluo5311/LineBot/blob/master/src/main/resources/static/menufinal.jpg "line bot richmenu")

### 安裝軟體對應版本:
|  軟體  |  版本  |   說明   |
|:------:|:--------:|:------------:|
|  JDK  | 11.0.10   | Spring boot對低版支持無測試過 |
|  Redis  | 5.0.10   | 低版支持無測試過  |
|  postgresql  | 42.2.19  | 低版支持無測試過 |

### 定時任務說明

|  功能  |  任務說明  |
|:------:|:--------:|
|  查詢今日確診數   | 每30分鐘執行一次  |
|  查詢藥局口罩剩餘數目   | 每1小時執行一次  |
|  獲取pdf並取得各疫苗接踵累计人次   | 每12小時執行一次 |
|  截圖:累计接踵人次&各梯次疫苗涵蓋率圖  | 每1小時執行一次 |
|  獲取CDC各國疫情狀況csv檔  | 每6小時執行一次 |

### 目錄內容 
1. 功能表、負責人、狀態、使用技術 

|  功能  | 狀態 | 主要技術 | 內容說明 |
|:------:|:------------:|:------------:| :----------|
|  查詢今日確診數  | `完成` | okhttp、jsoup | 解析新聞稿並擷取出新增確診人數、校正回歸數、及死亡數  |
|  哪裡買口罩  | `完成` | okhttp、jsoup | 發送請求至 [口罩即時查](https://wenyo.github.io/maskmap/ "口罩即時查")，解析含經緯度的藥局資訊並發送給使用者  |
|  掃描QRCode  | `完成` | LineBot | 使用LineBot Messaging API提供的CameraAction  |
|  國外疫情  | `完成` | okhttp、jsoup<br>java util.zip(GZIPInputStream) |  解壓縮各國疫情gzip檔，將內容送入rabbitmq隊列，按國家解析各欄位  |
|  疫苗施打統計圖  | `完成` | okhttp、jsoup<br>Selenium、apache pdfbox |  1. 前往(infogram 標題:誰打了疫苗)取得累计接踵人次截圖<br> 2. 前往全球疫情地圖之疫苗接種統計圖，取得各梯次疫苗涵蓋率圖<br> 3. 解析「疫苗接踵對象累計種人次.pdf」 |
|  其他統計表  | `進行中` | okhttp、jsoup | 暫定放個人履歷資訊 |

2. 相關Maven依賴 

|  套件  |  版本  |
|:------:|:--------:|
|  okhttp  | 3.14.9 |
|  jsoup  | 1.13.1 |  
| Selenium | 3.141.59 |  
|  Redis  | 5.0.10 |  
|  lombok  | 1.18.20 |  
|  apache.pdfbox  | 2.0.24 |  
|  spring-boot-starter-amqp  | 2.4.5 |

### 測試須知
1. [ngrok下載](https://ngrok.com/download "ngrok")

### docker image
> rabbitmq
```
docker pull rabbitmq:management
```

### Data source
1. [衛福部疾管署](https://www.cdc.gov.tw/ "link") 
2. [全球疫情地圖](https://covid-19.nchc.org.tw/dt_owl.php?dt_name=3 "全球疫情地圖")
3. [疫苗接種統計圖](https://covid-19.nchc.org.tw/dt_002-csse_covid_19_daily_reports_vaccine_city2.php "疫苗接種統計圖")
4. [Infogram_疫苗累計施打人數統計圖&各縣市覆蓋率](https://infogram.com/f25f5a66-bd5e-4272-b4b4-be1258a276a8 "疫苗統計圖")
5. [CDC疫苗統計資料pdf](https://www.cdc.gov.tw/Category/Page/9jFXNbCe-sFK9EImRRi2Og "疫苗統計pdf")
6. [健保特約機構口罩剩餘數量明細](https://data.gov.tw/dataset/116285 "口罩link") 或 [口罩即時查](https://wenyo.github.io/maskmap/ "口罩即時查")(帶經緯度)
7. [台灣電子地圖服務網](https://www.map.com.tw/ "台灣電子地圖服務網")
8. [LINE Messaging API SDK for Java](https://github.com/line/line-bot-sdk-java "LineBot API SDK")
9. [Messaging API reference](https://developers.line.biz/en/reference/messaging-api/ "LineBot API 文件")

