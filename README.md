# LineBot
![目錄](https://github.com/chrisluo5311/LineBot/blob/master/src/main/resources/static/menufinal.jpg "line bot richmenu")
### 目錄內容可彈性更改(都只是初步想法) 
1. 功能表、負責人、狀態、使用技術 

|  功能  |    負責人    | 狀態 | 主要使用技術 |
|:------:|:----------:|:------------:|:------------:|
|  查詢今日確診數  |  chris  | `完成` | okhttp、jsoup |
|  哪裡買口罩  |  chris  | `尚未開始` | leaflet |
|  所處位置疫情狀況  |  chris  |  `尚未開始` | leaflet |
|  國內外疫情  |  chris  |  `尚未開始`  | okhttp、jsoup |
|  疫苗施打統計圖  |  chris  |  `尚未開始`  | okhttp、jsoup |
|  其他統計表  |  chris  |  `尚未開始`  | okhttp、jsoup | 

2. 資料庫配置 
> sqlserver
```java
spring.jpa.hibernate.ddl-auto=update
spring.datasource.url=jdbc:sqlserver://localhost:1433;databaseName=linebot
spring.datasource.username=watcher
spring.datasource.password=P@ssw0rd
```

3. 緩存 
> Redis


### 參考網站
1. [衛福部疾管署](https://www.cdc.gov.tw/ "link") 
2. [全球疫情地圖](https://covid-19.nchc.org.tw/ "")
