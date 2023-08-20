package iie.bean;

import org.elasticsearch.search.sort.SortOrder;

public class SearchFormData {

    public  enum QUERY_FIELD {
        all,
        title,
        content
    }


    public Integer currentPage;
    public String endDate;
    public Integer pageSize;
    public String queryField;
    public String queryStr;
    public String searchType;
    public String sortType;
    public String startDate;
    public String type;
    public String webSiteType;
    public String webSites;


    private String[] webSiteTypeArray;
    private String[] webSitesArray;
    private SortOrder sortOrder;



    public String[] getWebSitesArray() {
        return webSitesArray;
    }

    public String[] getWebSiteTypeArray() {
        return webSiteTypeArray;
    }

    public void setWebSitesArray(String[] webSitesArray) {
        this.webSitesArray = webSitesArray;
    }

    public void setWebSiteTypeArray(String[] webSiteTypeArray) {
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


    @Override
    public String toString() {
        return "SearchFormData{" +
                "currentPage=" + currentPage +
                ", endDate='" + endDate + '\'' +
                ", pageSize=" + pageSize +
                ", queryField='" + queryField + '\'' +
                ", queryStr='" + queryStr + '\'' +
                ", searchType='" + searchType + '\'' +
                ", sortType='" + sortType + '\'' +
                ", startDate='" + startDate + '\'' +
                ", type='" + type + '\'' +
                ", webSiteType='" + webSiteType + '\'' +
                ", webSites='" + webSites + '\'' +
                '}';
    }
}
