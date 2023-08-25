package iie.domain;


import co.elastic.clients.elasticsearch._types.FieldValue;
import co.elastic.clients.elasticsearch._types.SortOrder;

import java.util.List;

public class SearchFormData {

    public  enum QUERY_FIELD {
        all,
        news_title_zh,
        news_content_zh
    }


    private Integer currentPage;
    private String endDate;
    private Integer pageSize;
    private String queryField;
    private String queryStr;
    private String searchType;
    private String sortID;
    private String sortType;
    private String startDate;
    private String type;
    private String webSiteType;
    private String webSites;



    //需要自己赋值
    private List<FieldValue> webSiteTypeArray;
    private List<FieldValue> webSitesArray;
    private SortOrder sortOrder;



    public List<FieldValue> getWebSitesArray() {
        return webSitesArray;
    }

    public List<FieldValue> getWebSiteTypeArray() {
        return webSiteTypeArray;
    }

    public void setWebSitesArray(List<FieldValue> webSitesArray) {
        this.webSitesArray = webSitesArray;
    }

    public void setWebSiteTypeArray(List<FieldValue> webSiteTypeArray) {
        this.webSiteTypeArray = webSiteTypeArray;
    }

    public SortOrder getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(SortOrder sortOrder) {
        this.sortOrder = sortOrder;
    }





    //get
    public Integer getCurrentPage() {
        return currentPage;
    }
    public String getEndDate() {
        return endDate;
    }
    public Integer getPageSize() {
        return pageSize;
    }
    public String getQueryField() {
        return queryField;
    }
    public String getQueryStr() {
        return queryStr;
    }
    public String getSearchType() {
        return searchType;
    }
    public String getSortType() {
        return sortType;
    }
    public String getStartDate() {
        return startDate;
    }
    public String getType() {
        return type;
    }
    public String getWebSites() {
        return webSites;
    }
    public String getWebSiteType() {
        return webSiteType;
    }

    public String getSortID() {
        return sortID;
    }

    //set
    public void setQueryStr(String queryStr) {
        this.queryStr = queryStr;
    }

    public void setQueryField(String queryField) {
        this.queryField = queryField;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public void setCurrentPage(Integer currentPage) {
        this.currentPage = currentPage;
    }

    public void setSearchType(String searchType) {
        this.searchType = searchType;
    }

    public void setSortType(String sortType) {
        this.sortType = sortType;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setWebSites(String webSites) {
        this.webSites = webSites;
    }

    public void setWebSiteType(String webSiteType) {
        this.webSiteType = webSiteType;
    }

    public void setSortID(String sortID) {
        this.sortID = sortID;
    }

    @Override
    public String toString() {
        return "SearchFormData{" +
                "currentPage=" + currentPage +
                ", endDate='" + endDate + '\'' +
                ", pageSize=" + pageSize +
                ", queryField='" + queryField + '\'' +
                ", queryStr='" + queryStr + '\'' +
                ", searchType='" + searchType + '\'' +
                ", sortID='" + sortID + '\'' +
                ", sortType='" + sortType + '\'' +
                ", startDate='" + startDate + '\'' +
                ", type='" + type + '\'' +
                ", webSiteType='" + webSiteType + '\'' +
                ", webSites='" + webSites + '\'' +
                '}';
    }
}
