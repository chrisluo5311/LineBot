# LineBot chatbot & Covid 19 epidemic information crawler

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
 
 <h2 ><img src="https://img.icons8.com/office/30/000000/training.png"/> &nbspProject Introduction: </h2>
 
#### Due to the COVID-19 pandemic, this project crawls and parses data such as daily new confirmed cases, remaining mask information, QR code scanning, global epidemic statistics, and vaccine registration statistics, and provides this information to users. As people may not check the health department's platform every day for epidemic information, a LineBot is created using Line, the most frequently used social media platform in Taiwan, to conveniently provide users with epidemic information and ensure that the data is updated to the latest news, reducing information asymmetry between the government and the public.
 
 ---
 
<h2 ><img src="https://img.icons8.com/offices/30/000000/content.png"/>&nbspTable of Contents:</h2>

<!-- ![目錄](https://github.com/chrisluo5311/LineBot/blob/master/src/main/resources/static/menufinal.jpg "line bot richmenu") -->
<img src="https://github.com/chrisluo5311/LineBot/blob/master/src/main/resources/static/menufinal.jpg" width="600" height="400">


### Software version:
|  Software  |  version  |  
|:------:|:--------:|
|  SpringBoot  | `2.4.5`   | 
|  JDK  | `11.0.10`   | 
|  Redis  | `5.0.10`   | 
|  postgresql  | `42.2.19`  | 

### Scheduled task description

|  Function  |  Explanation  |
|:------:|:--------:|
|  Query the number of confirmed cases today   | every 30 mins  |
|  Check the remaining number of masks in the pharmacy   | every hour  |
|  Get pdf and get the cumulative number of visits for each vaccine   | every 12 hours |
|  Screenshot: Cumulative number of admissions & vaccine coverage rate chart for each tier  | every hour |
|  Obtain csv files of CDC in various countries  | every 6 hours |

### Content Descriptions
1. Menu, status, techniques 

|  Function  | status | Main techniques | Explanation |
|:------:|:------------:|:------------:| :----------|
|  Query today<br>cases  | `Finished` | okhttp、jsoup | Parse the press release and extract the number of new confirmed cases, adjusted regression numbers, and deaths  |
|  where to<br>buy mask  | `Finished` | okhttp、jsoup | Send API request to [口罩即時查](https://wenyo.github.io/maskmap/ "口罩即時查")，and parse pharmacy information including latitude and longitude and send it to the user  |
|  Scan QRCode  | `Finished` | LineBot | Using the CameraAction provided by the LineBot Messaging API  |
|  Foreign situation  | `Finished` | okhttp、jsoup<br>java util.zip(GZIPInputStream) |  Unzip the gzip file of the epidemic situation in each country, send the content to the rabbitmq queue, and analyze each field by country.  |
|  Vaccination<br>Statistics Chart  | `Finished` | okhttp、jsoup<br>Selenium、apache pdfbox |  1. Go to (infogram title: Who has been vaccinated) to get the screenshot of the cumulative number of recipients<br> 2. Go to the vaccination statistics chart of the global epidemic map to obtain the vaccine coverage rate chart for each tier<br> 3. Analyze "Cumulative Number of People Vaccinated.pdf" |


2. Maven 

|  Libraries  |  Version  |
|:------:|:--------:|
|  okhttp  | `3.14.9` |
|  jsoup  | `1.13.1` |  
| Selenium | `3.141.59` |  
|  Redis  | `5.0.10` |  
|  lombok  | `1.18.20` |  
|  apache.pdfbox  | `2.0.24` |  
|  spring-boot-starter-amqp  | `2.4.5` |

3. Strategy pattern: according to different functions of the directory, different categories are called for processing.
<img width="1000" height="500" src="https://github.com/chrisluo5311/LineBot/blob/master/src/main/resources/static/%E7%AD%96%E7%95%A5%E6%A8%A1%E5%BC%8FUML.png" /> 


### Testing instructions
1. [ngrok download](https://ngrok.com/download "ngrok")

2. docker image
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
6. [健保特約機構口罩剩餘數量明細](https://data.gov.tw/dataset/116285 "口罩link") or [口罩即時查](https://wenyo.github.io/maskmap/ "口罩即時查")(With latitude and longitude)
7. [台灣電子地圖服務網](https://www.map.com.tw/ "台灣電子地圖服務網")
8. [LINE Messaging API SDK for Java](https://github.com/line/line-bot-sdk-java "LineBot API SDK")
9. [Messaging API reference](https://developers.line.biz/en/reference/messaging-api/ "LineBot API 文件")


