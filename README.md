# LineBot聊天機器人 & Covid19疫情資訊爬蟲

<div>
 <img src="https://komarev.com/ghpvc/?username=chrisluo5311&label=Profile%20views&color=red&style=flat" alt="chrisluo5311" />
 <img src="https://img.shields.io/github/v/tag/chrisluo5311/LineBot" alt="chrisluo5311" />
 <img src="https://img.shields.io/github/languages/code-size/chrisluo5311/LineBot" alt="chrisluo5311" />
 <img src="https://img.shields.io/github/commit-activity/m/chrisluo5311/LineBot" alt="chrisluo5311" />
 </div> 
 
 ---
 
 <h2 > 🔧 Technologies & Tools </h2>
 <div >
 <img src="https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=java&logoColor=white" />
   <img src="https://img.shields.io/badge/Hibernate-59666C?style=for-the-badge&logo=Hibernate&logoColor=white" />
   <img src="https://img.shields.io/badge/Spring_Boot-F2F4F9?style=for-the-badge&logo=spring-boot" />
   <img src="https://img.shields.io/badge/Selenium-43B02A?style=for-the-badge&logo=Selenium&logoColor=white" />
   <img src="https://img.shields.io/badge/-linebot-brightgreen?style=for-the-badge&logo=line&logoColor=white" />
   <img src="https://img.shields.io/badge/PostgreSQL-316192?style=for-the-badge&logo=postgresql&logoColor=white" />
   <img src="https://img.shields.io/badge/rabbitmq-%23FF6600.svg?&style=for-the-badge&logo=rabbitmq&logoColor=white" />
  <img src="https://img.shields.io/badge/redis-%23DD0031.svg?&style=for-the-badge&logo=redis&logoColor=white" />
  </div>
 
 ---
 
 <h2 ><img src="https://img.icons8.com/office/30/000000/training.png"/> &nbsp專案介紹: </h2>
 
#### 由於COVID19疫情肆虐，本專案針對每日新增確診數、口罩剩餘資訊、QRCode掃描、各國疫情以及疫苗接踵統計表等等，進行爬蟲、截圖、解析資料並提供給用戶。鑒於一般人並非每天都會至疾管署平台觀看疫情資訊，因此以台灣人最頻繁使用的社交軟體Line來製作LineBot，讓用戶很方便地獲取疫情資訊，並且保持資料定時更新至最新消息，減少政府與民間的資訊不對稱。 
 
 ---
 
<h2 ><img src="https://img.icons8.com/offices/30/000000/content.png"/>&nbsp疫情小幫手(目錄):</h2>

![目錄](https://github.com/chrisluo5311/LineBot/blob/master/src/main/resources/static/menufinal.jpg "line bot richmenu")

### 安裝軟體對應版本:
|  軟體  |  版本  |  
|:------:|:--------:|
|  SpringBoot  | `2.4.5`   | 
|  JDK  | `11.0.10`   | 
|  Redis  | `5.0.10`   | 
|  postgresql  | `42.2.19`  | 

### 定時任務說明

|  功能  |  任務說明  |
|:------:|:--------:|
|  查詢今日確診數   | 每30分鐘執行一次  |
|  查詢藥局口罩剩餘數目   | 每1小時執行一次  |
|  獲取pdf並取得各疫苗接踵累计人次   | 每12小時執行一次 |
|  截圖:累计接踵人次&各梯次疫苗涵蓋率圖  | 每1小時執行一次 |
|  獲取CDC各國疫情狀況csv檔  | 每6小時執行一次 |

### 目錄內容 
1. 功能表、狀態、使用技術 

|  功能  | 狀態 | 主要技術 | 內容說明 |
|:------:|:------------:|:------------:| :----------|
|  查詢今日<br>確診數  | `完成` | okhttp、jsoup | 解析新聞稿並擷取出新增確診人數、校正回歸數、及死亡數  |
|  哪裡<br>買口罩  | `完成` | okhttp、jsoup | 發送請求至 [口罩即時查](https://wenyo.github.io/maskmap/ "口罩即時查")，解析含經緯度的藥局資訊並發送給使用者  |
|  掃描QRCode  | `完成` | LineBot | 使用LineBot Messaging API提供的CameraAction  |
|  國外疫情  | `完成` | okhttp、jsoup<br>java util.zip(GZIPInputStream) |  解壓縮各國疫情gzip檔，將內容送入rabbitmq隊列，按國家解析各欄位  |
|  疫苗施打<br>統計圖  | `完成` | okhttp、jsoup<br>Selenium、apache pdfbox |  1. 前往(infogram 標題:誰打了疫苗)取得累计接踵人次截圖<br> 2. 前往全球疫情地圖之疫苗接種統計圖，取得各梯次疫苗涵蓋率圖<br> 3. 解析「疫苗接踵對象累計種人次.pdf」 |
|  其他  | `進行中` |  | 暫定放個人履歷資訊 |

2. 相關Maven依賴 

|  套件  |  版本  |
|:------:|:--------:|
|  okhttp  | `3.14.9` |
|  jsoup  | `1.13.1` |  
| Selenium | `3.141.59` |  
|  Redis  | `5.0.10` |  
|  lombok  | `1.18.20` |  
|  apache.pdfbox  | `2.0.24` |  
|  spring-boot-starter-amqp  | `2.4.5` |

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

### Demo 示範

