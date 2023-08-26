package iie.domain;

import java.util.ArrayList;
import java.util.List;

public class TypeStatInfo {

    private List<String> xAxis= new ArrayList<>();;
    private List<String> legend= new ArrayList<>();;
    private List<TypeStatInfo_series> series = new ArrayList<>();


    public void setLegend(List<String> legend) {
        this.legend = legend;
    }

    public void setSeries(List<TypeStatInfo_series> series) {
        this.series = series;
    }

    public void setxAxis(List<String> xAxis) {
        this.xAxis = xAxis;
    }

    public List<String> getLegend() {
        return legend;
    }

    public List<String> getxAxis() {
        return xAxis;
    }

    public List<TypeStatInfo_series> getSeries() {
        return series;
    }

    public void setSeriesValue (String type,double value)
    {
        for (int i = 0; i < series.size(); i++) {
            if (series.get(i).getName().equals(type)){
                series.get(i).getData().add(value);
                break;
            }
        }
    }





    @Override
    public String toString() {
        return "TypeStatInfo{" +
                "xAxis=" + xAxis +
                ", legend=" + legend +
                ", series=" + series +
                '}';
    }
}
