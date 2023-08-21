package iie.Utils;


import co.elastic.clients.elasticsearch._types.SortOrder;
import iie.bean.SearchFormData;
import org.apache.commons.lang3.StringUtils;
/*import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortOrder;*/

import java.text.ParseException;
import java.text.SimpleDateFormat;

public class Check {
    public static String CheckParame(SearchFormData formData)
    {
        String isSuccess = "ok";
        if (formData.getCurrentPage() <=0){
            isSuccess = "currentPage 不能少于等于0";
            return isSuccess;
        }
        if (formData.getPageSize() <=0){
            isSuccess = "pageSize 不能少于等于0";
            return isSuccess;
        }

        if (StringUtils.isAnyEmpty(formData.getEndDate(),formData.getStartDate())){
            isSuccess = "endDate和startDate 不能为空";
            return isSuccess;
        }
        //判断字符串是否日期格式
        String startDateStr = formData.getStartDate();
        String endDateStr = formData.getEndDate();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        try {
            sdf.parse(startDateStr);
            sdf.parse(endDateStr);
        } catch (ParseException e) {
            e.printStackTrace();
            isSuccess =("endDate和startDate 时间格式不合法。格式：yyyy-MM-dd" );
            return  isSuccess;
        }
        if (StringUtils.isAnyEmpty(formData.getQueryField())){
            isSuccess =("queryField 为空" );
            return isSuccess;
        }
        try {
            SearchFormData.QUERY_FIELD  queryField = SearchFormData.QUERY_FIELD.valueOf(formData.getQueryField());
        }catch (IllegalArgumentException e){
            e.printStackTrace();
            isSuccess =("queryField的值范围 all|title|content" );
            return  isSuccess;
        }

        if (StringUtils.isAnyEmpty(formData.getQueryStr())){
            isSuccess =("queryStr 不能为空" );
            return isSuccess;
        }

        //可以使用空格分开
      /*  String[] queryStrArray =  formData.getQueryStr().split(",");
        if (queryStrArray.length <= 0){
            //搜索词要有值
            isSuccess =("queryStr 不能为空" );
            return isSuccess;
        }*/


        //排序
        if (StringUtils.isAnyEmpty(formData.getSearchType())){
            isSuccess =("searchType 不能为空" );
            return isSuccess;
        }
        String searchType = formData.getSearchType();
        if (searchType.equalsIgnoreCase("true") || searchType.equalsIgnoreCase("false")){
        }else {
            isSuccess =("searchType的值范围 true|false" );
            return isSuccess;
        }

        if (StringUtils.isAnyEmpty(formData.getSortType())){
            isSuccess =("sortType 不能为空" );
            return isSuccess;
        }
        String sortType =  formData.getSortType();
        if (sortType.equalsIgnoreCase("time") ){
            formData.setSortOrder(SortOrder.Desc);
        }
        else if (sortType.equalsIgnoreCase("timeasc")){
            formData.setSortOrder(SortOrder.Asc);
        }else {
            isSuccess =("sortType的值范围 time|timeasc" );
            return isSuccess;
        }



        String type =  formData.getType();
        if (StringUtils.isEmpty(type)){
            //不限定
            //null和"" 是有含义
        }else {
            if (type.equalsIgnoreCase("image") || type.equalsIgnoreCase("video")){
            }else {
                isSuccess =("type的值范围 image|video|空字符串" );
                return isSuccess;
            }
        }

        //需要检测具体值吗
        if (StringUtils.isAnyEmpty(formData.getWebSiteType())){
            isSuccess =("webSiteType 不能为空,多个值使用逗号分割" );
            return isSuccess;
        }
        //多个值，逗号隔开
        String[] webSiteTypeArray =  formData.getWebSiteType().split(",");
        if (webSiteTypeArray.length <= 0){
            isSuccess =("webSiteType 不能为空,多个值使用逗号分割" );
            return isSuccess;
        }
        formData.setWebSiteTypeArray(webSiteTypeArray);


        for (String  webSiteType : webSiteTypeArray) {
            if (    webSiteType.equalsIgnoreCase("GOV")||
                    webSiteType.equalsIgnoreCase("HMT")||
                    webSiteType.equalsIgnoreCase("OVERSEAS")||
                    webSiteType.equalsIgnoreCase("WEBSITE")||
                    webSiteType.equalsIgnoreCase("ANTI")
            ){
            }else {
                isSuccess =("webSiteType的值范围 GOV|HMT|OVERSEAS|OVERSEAS|WEBSITE|ANTI" );
                return isSuccess;
            }
        }

        if (StringUtils.isAnyEmpty(formData.getWebSites())){
            isSuccess =("webSites 不能为空,多个值使用逗号分割" );
            return isSuccess;
        }
        //多个值，逗号隔开
        String[] getWebSitesArray =  formData.getWebSites().split(",");
        if (getWebSitesArray.length <= 0){
            isSuccess =("webSites 不能为空,多个值使用逗号分割" );
            return isSuccess;
        }
        formData.setWebSitesArray(getWebSitesArray);

        return isSuccess;
    }




/*    public static SearchSourceBuilder CreateSearchSourceBuilder (SearchFormData formData)
    {
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        if (formData.getSearchType().equalsIgnoreCase("true")){
            //精准查询

            BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
            SearchFormData.QUERY_FIELD  queryField =  SearchFormData.QUERY_FIELD.valueOf(formData.getQueryField());
            if (queryField == SearchFormData.QUERY_FIELD.all){
                boolQueryBuilder.must(QueryBuilders.multiMatchQuery(formData.getQueryStr(),
                                SearchFormData.QUERY_FIELD.title.toString(),SearchFormData.QUERY_FIELD.content.toString())
                        .type("phrase"));
            }else {
                boolQueryBuilder.must(QueryBuilders.multiMatchQuery(formData.getQueryStr(),
                                queryField.toString())
                        .type("phrase"));
            }
            //时间没有HH:mm:ss
            boolQueryBuilder.filter(QueryBuilders.rangeQuery("news_publicdate").gte(formData.getStartDate()).lte(formData.getEndDate()));

            //可以为不限定
            if (!StringUtils.isEmpty(formData.getType())){
                boolQueryBuilder.filter(QueryBuilders.termQuery("news_type", formData.getType()));
            }
            boolQueryBuilder.filter(QueryBuilders.termsQuery("news_website_type",formData.getWebSiteTypeArray() ));
            boolQueryBuilder.filter(QueryBuilders.termsQuery("news_website", formData.getWebSitesArray()));
            searchSourceBuilder.query(boolQueryBuilder);

            //时间有HH:mm:ss
            searchSourceBuilder.sort(new FieldSortBuilder("news_publictime").order(formData.getSortOrder()));
            //searchSourceBuilder.sort(SortBuilders._score);
            searchSourceBuilder.trackTotalHits(true);
            searchSourceBuilder.from(formData.getCurrentPage());
            searchSourceBuilder.size(formData.getPageSize());
        }else {

            SearchFormData.QUERY_FIELD  queryField =  SearchFormData.QUERY_FIELD.valueOf(formData.getQueryField());
            if (queryField == SearchFormData.QUERY_FIELD.all){
                searchSourceBuilder.query(QueryBuilders.multiMatchQuery(formData.getQueryStr(),
                                SearchFormData.QUERY_FIELD.title.toString(),SearchFormData.QUERY_FIELD.content.toString())
                        .type("best_fields"));
            }else {
                searchSourceBuilder.query(QueryBuilders.multiMatchQuery(formData.getQueryStr(),
                                queryField.toString())
                        .type("best_fields"));
            }

      *//*      searchSourceBuilder.sort(new FieldSortBuilder("publictime").order(SortOrder.DESC));
            searchSourceBuilder.trackTotalHits(true);
            searchSourceBuilder.from(0);
            searchSourceBuilder.size(30);*//*

        }

        searchSourceBuilder.sort(new FieldSortBuilder("news_publictime").order(formData.getSortOrder()));
        //searchSourceBuilder.sort(SortBuilders._score);
        searchSourceBuilder.trackTotalHits(true);
        searchSourceBuilder.from(formData.getCurrentPage());
        searchSourceBuilder.size(formData.getPageSize());

        return searchSourceBuilder;

    }*/

}
