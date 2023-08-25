package iie.domain;

public class RepNews {

    private String news_title;
    private String news_author;
    private String news_publictime;   //date
    private String news_publicdate;   //date
    private String news_website;
    private String news_website_type;
    private String news_content_zh;   //text
    private String id;
    private String news_url;
    private String news_type;


    public void setNews_website_type(String news_website_type) {
        this.news_website_type = news_website_type;
    }

    public void setNews_website(String news_website) {
        this.news_website = news_website;
    }

    public void setNews_url(String news_url) {
        this.news_url = news_url;
    }

    public void setNews_type(String news_type) {
        this.news_type = news_type;
    }

    public void setNews_publictime(String news_publictime) {
        this.news_publictime = news_publictime;
    }

    public void setNews_publicdate(String news_publicdate) {
        this.news_publicdate = news_publicdate;
    }

    public void setNews_content_zh(String news_content_zh) {
        this.news_content_zh = news_content_zh;
    }

    public void setNews_author(String news_author) {
        this.news_author = news_author;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setNews_title(String news_title) {
        this.news_title = news_title;
    }




    public String getNews_website_type() {
        return news_website_type;
    }

    public String getNews_website() {
        return news_website;
    }

    public String getNews_url() {
        return news_url;
    }

    public String getNews_type() {
        return news_type;
    }

    public String getNews_publictime() {
        return news_publictime;
    }

    public String getNews_publicdate() {
        return news_publicdate;
    }

    public String getNews_content_zh() {
        return news_content_zh;
    }

    public String getNews_author() {
        return news_author;
    }

    public String getId() {
        return id;
    }

    public String getNews_title() {
        return news_title;
    }


    @Override
    public String toString() {
        return "RepNews{" +
                "news_title='" + news_title + '\'' +
                ", news_author='" + news_author + '\'' +
                ", news_publictime='" + news_publictime + '\'' +
                ", news_publicdate='" + news_publicdate + '\'' +
                ", news_website='" + news_website + '\'' +
                ", news_website_type='" + news_website_type + '\'' +
                ", news_content_zh='" + news_content_zh + '\'' +
                ", id='" + id + '\'' +
                ", news_url='" + news_url + '\'' +
                ", news_type='" + news_type + '\'' +
                '}';
    }
}
