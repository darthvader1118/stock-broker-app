package com.example.stockbroker;

public class NewsItem {
    String imageUri,source,dateFrom,title, url;

    public NewsItem(String imageUri, String source, String dateFrom,String title,String url){
        this.imageUri = imageUri;
        this.source = source;
        this.dateFrom = dateFrom;
        this.title = title;
        this.url = url;
    }


}
