package iie.bean;

public class Book {

    /*
           "news_title": "Boris Kollár Podporíme vznik ministerstva cestovn",
          "news_author": "未知",
          "news_publictime": "2023-03-26 00:02:09",
          "news_publictime_date": "2023-03-26 00:02:09",
          "news_website": "Aktualne新闻社(斯洛伐克)",
          "news_website_type": "OVERSEAS",
          "news_content": "Nezaradený poslanec Tomáš Taraba predkladá po dohode so SNS do Národnej rady SR zákon na zriadenie ministerstva cestovného ruchu a športu SR.",
          "id": "65358d7c235f9c4586a0c26f056c473a",
          "media_url": [
            "http://28.66.100.209:8000/ReadAssist/image/2023-07-24/IMG/F3BB34F1C82C0CA2E6EA3B9E007DA89DBE424AF2FD08B6BF19DD167D5DC6B2AB.jpg"
          ],
          "news_type": "image"
        },
    * */

    //查询返回的结果只需要这几个字段。
    //数据库的表还需要建立筛选字段，这里字段全是keyword，除了日期是Date

    private String news_title;
    private String news_author;
    private String news_publictime;
    private String news_publicdate;
    private String news_website;
    private String news_website_type;
    private String news_content;
    private String news_url;
    private String news_type;




}
