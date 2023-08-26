package iie.domain;

import java.util.ArrayList;
import java.util.List;

public class TypeStatInfo_series {
    //private String news_website_type ;
    private String name ;

    private List<Double> data = new ArrayList<>() ;


    public TypeStatInfo_series ()
    {

    }

    public TypeStatInfo_series (String name)
    {
        //this.news_website_type = news_website_type;
        this.name = name;
    }


/*    public void setNews_website_type(String news_website_type) {
        this.news_website_type = news_website_type;
    }*/

    public void setName(String name) {
        this.name = name;
    }

    public void setData(List<Double> data) {
        this.data = data;
    }

/*    public String getNews_website_type() {
        return news_website_type;
    }*/

    public String getName() {
        return name;
    }

    public List<Double> getData() {
        return data;
    }


    @Override
    public String toString() {
        return "TypeStatInfo{" +
                ", name='" + name + '\'' +
                ", data=" + data +
                '}';
    }
}
