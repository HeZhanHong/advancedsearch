package iie.domain;

public class News {

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

    private String id;
    private Integer news_source;
    private String news_website;
    private String news_website_type;
    private String news_url;
    private String news_title;
    private String news_content;
    private String news_author;
    private String news_media_name;
    private String news_publictime;   //date
    private String news_publicdate;   //date
    private String news_language;
    private String news_title_zh;    //text
    private String news_content_zh;   //text
    private String news_keywords;
    private String news_summary;     //text
    private String news_entities;
    private String news_entities_person;
    private String news_entities_place;
    private String news_entities_organization;
    private String news_type;
    private String crawl_time;           //date
    private String domain;
    private String title_content_vector;   //dense_vector

    //set


    public String getNews_title() {
        return news_title;
    }

    public String getNews_content() {
        return news_content;
    }

    public Integer getNews_source() {
        return news_source;
    }

    public String getCrawl_time() {
        return crawl_time;
    }

    public String getDomain() {
        return domain;
    }

    public String getId() {
        return id;
    }

    public String getNews_author() {
        return news_author;
    }

    public String getNews_content_zh() {
        return news_content_zh;
    }

    public String getNews_entities() {
        return news_entities;
    }

    public String getNews_entities_organization() {
        return news_entities_organization;
    }

    public String getNews_entities_person() {
        return news_entities_person;
    }

    public String getNews_entities_place() {
        return news_entities_place;
    }

    public String getNews_keywords() {
        return news_keywords;
    }

    public String getNews_language() {
        return news_language;
    }

    public String getNews_media_name() {
        return news_media_name;
    }

    public String getNews_publicdate() {
        return news_publicdate;
    }

    public String getNews_publictime() {
        return news_publictime;
    }

    public String getNews_summary() {
        return news_summary;
    }

    public String getNews_title_zh() {
        return news_title_zh;
    }

    public String getNews_type() {
        return news_type;
    }

    public String getNews_url() {
        return news_url;
    }

    public String getNews_website() {
        return news_website;
    }

    public String getNews_website_type() {
        return news_website_type;
    }

    public String getTitle_content_vector() {
        return title_content_vector;
    }



    //set


    public void setNews_title(String news_title) {
        this.news_title = news_title;
    }

    public void setNews_content(String news_content) {
        this.news_content = news_content;
    }

    public void setCrawl_time(String crawl_time) {
        this.crawl_time = crawl_time;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setNews_author(String news_author) {
        this.news_author = news_author;
    }

    public void setNews_content_zh(String news_content_zh) {
        this.news_content_zh = news_content_zh;
    }

    public void setNews_entities(String news_entities) {
        this.news_entities = news_entities;
    }

    public void setNews_entities_person(String news_entities_person) {
        this.news_entities_person = news_entities_person;
    }

    public void setNews_entities_organization(String news_entities_organization) {
        this.news_entities_organization = news_entities_organization;
    }

    public void setNews_entities_place(String news_entities_place) {
        this.news_entities_place = news_entities_place;
    }

    public void setNews_keywords(String news_keywords) {
        this.news_keywords = news_keywords;
    }

    public void setNews_language(String news_language) {
        this.news_language = news_language;
    }

    public void setNews_media_name(String news_media_name) {
        this.news_media_name = news_media_name;
    }

    public void setNews_publicdate(String news_publicdate) {
        this.news_publicdate = news_publicdate;
    }

    public void setNews_publictime(String news_publictime) {
        this.news_publictime = news_publictime;
    }

    public void setNews_source(Integer news_source) {
        this.news_source = news_source;
    }

    public void setNews_summary(String news_summary) {
        this.news_summary = news_summary;
    }

    public void setNews_title_zh(String news_title_zh) {
        this.news_title_zh = news_title_zh;
    }

    public void setNews_type(String news_type) {
        this.news_type = news_type;
    }

    public void setNews_url(String news_url) {
        this.news_url = news_url;
    }

    public void setNews_website(String news_website) {
        this.news_website = news_website;
    }

    public void setNews_website_type(String news_website_type) {
        this.news_website_type = news_website_type;
    }


    public void setTitle_content_vector(String title_content_vector) {
        this.title_content_vector = title_content_vector;
    }


    @Override
    public String toString() {
        return "Book{" +
                "id='" + id + '\'' +
                ", news_source=" + news_source +
                ", news_website='" + news_website + '\'' +
                ", news_website_type='" + news_website_type + '\'' +
                ", news_url='" + news_url + '\'' +
                ", news_title='" + news_title + '\'' +
                ", news_content='" + news_content + '\'' +
                ", news_author='" + news_author + '\'' +
                ", news_media_name='" + news_media_name + '\'' +
                ", news_publictime='" + news_publictime + '\'' +
                ", news_publicdate='" + news_publicdate + '\'' +
                ", news_language='" + news_language + '\'' +
                ", news_title_zh='" + news_title_zh + '\'' +
                ", news_content_zh='" + news_content_zh + '\'' +
                ", news_keywords='" + news_keywords + '\'' +
                ", news_summary='" + news_summary + '\'' +
                ", news_entities='" + news_entities + '\'' +
                ", news_entities_person='" + news_entities_person + '\'' +
                ", news_entities_place='" + news_entities_place + '\'' +
                ", news_entities_organization='" + news_entities_organization + '\'' +
                ", news_type='" + news_type + '\'' +
                ", crawl_time='" + crawl_time + '\'' +
                ", domain='" + domain + '\'' +
                ", title_content_vector='" + title_content_vector + '\'' +
                '}';
    }
}
