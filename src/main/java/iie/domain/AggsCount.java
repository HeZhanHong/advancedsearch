package iie.domain;

public class AggsCount {

    private String date;
    private String type;
    private long count;

    public  AggsCount(String date,String type,long count)
    {
        this.count = count;
        this.type = type;
        this.date = date;
    }


/*    public void setType(String type) {
        this.type = type;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public void setDate(String date) {
        this.date = date;
    }*/

    public String getType() {
        return type;
    }

    public long getCount() {
        return count;
    }

    public String getDate() {
        return date;
    }


    @Override
    public String toString() {
        return "AggsCount{" +
                "date='" + date + '\'' +
                ", type='" + type + '\'' +
                ", count=" + count +
                '}';
    }
}
