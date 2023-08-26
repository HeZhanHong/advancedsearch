package iie.domain;

public class CountStatInfo<T> {

    private String date ;
    private T value ;


    public CountStatInfo (String date,T value )
    {
        this.date = date;
        this.value = value;
    }


    public void setDate(String date) {
        this.date = date;
    }

    public void setValue(T value) {
        this.value = value;
    }

    public String getDate() {
        return date;
    }

    public T getValue() {
        return value;
    }


    @Override
    public String toString() {
        return "CountStatInfo{" +
                "date='" + date + '\'' +
                ", value=" + value +
                '}';
    }
}
