package iie.controller;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.ShardStatistics;
import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch._types.query_dsl.QueryBuilders;
import co.elastic.clients.elasticsearch._types.query_dsl.TextQueryType;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.elasticsearch.core.search.HitsMetadata;
import co.elastic.clients.elasticsearch.core.search.TotalHits;
import co.elastic.clients.elasticsearch.indices.CreateIndexResponse;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import iie.Utils.Check;
import iie.bean.Book;
import iie.bean.SearchFormData;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpHost;

//import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;


import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


//import org.elasticsearch.client.RestClient;

@Controller
@CrossOrigin
public class SearchAdvanced
{
    private Object Query;


    //@RequestParam("name") String name
    //@ModelAttribute FormData formData

    //http://localhost:8080/ReadingAssist/advancedsearch/searchAdvanced
    @PostMapping(value = "/ReadingAssist/advancedsearch/searchAdvanced")
    public  ResponseEntity<String> postFlume(@ModelAttribute SearchFormData formData)
    {

        String errMessage =  Check.CheckParame(formData);
        if (!errMessage.equalsIgnoreCase("ok")){
            System.err.println(errMessage);
            return ResponseEntity.ok(errMessage);
        }

        RestClient client = RestClient.builder(new HttpHost("localhost", 9200,"http")).build();
        ElasticsearchTransport transport = new RestClientTransport(client,new JacksonJsonpMapper());
        ElasticsearchClient ll =   new ElasticsearchClient(transport);


       // RestClient client =   RestClient.builder(new HttpHost("localhost", 9200, "http")).build()

        //org.elasticsearch.client.internal.ElasticsearchClient

        //RestHighLevelClient client = new RestHighLevelClient();

 /*  RestHighLevelClient client = new RestHighLevelClient(
                RestClient.builder(
                        new HttpHost("localhost", 9200, "http")));*/


        SearchRequest requestss = CreateSearchRequest(formData);



        try {
            SearchResponse<Book> search = ll.search(requestss, Book.class);

            System.out.println("search.toString() = " + search.toString());
            long took = search.took();
            System.out.println("took = " + took);
            boolean b = search.timedOut();
            System.out.println("b = " + b);
            ShardStatistics shards = search.shards();
            System.out.println("shards = " + shards);
            HitsMetadata<Book> hits = search.hits();
            TotalHits total = hits.total();
            System.out.println("total = " + total);
            Double maxScore = hits.maxScore();
            System.out.println("maxScore = " + maxScore);
            List<Hit<Book>> list = hits.hits();
            for (Hit<Book> bookHit : list) {
                //关键在这里
                System.out.println("bookHit.source() = " + bookHit.source());
                System.out.println("bookHit.score() = " + bookHit.score());
                System.out.println("bookHit.index() = " + bookHit.index());
            }



            //解析结果
        } catch (IOException e) {
            e.printStackTrace();
        }

 /*       try {
            SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
            // 处理查询结果...
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            client.close();  // 关闭客户端连接
        }*/




        //System.out.println(name);

    /*    String message = "";
        try {
            // do something with the file
            message = "File " + file.getOriginalFilename() + " uploaded successfully!";
        } catch (Exception e) {
            message = "Failed to upload file " + file.getOriginalFilename();
            e.printStackTrace();
        }
        System.out.println(message);*/
        return ResponseEntity.ok(requestss.toString());
    }


    public SearchRequest   CreateSearchRequest  (SearchFormData formData)
    {
        String queryStr = formData.getQueryStr();
        List<String>  fields = new ArrayList<String>();

        SearchFormData.QUERY_FIELD  queryField =  SearchFormData.QUERY_FIELD.valueOf(formData.getQueryField());
        if (queryField == SearchFormData.QUERY_FIELD.all){
            fields.add( SearchFormData.QUERY_FIELD.news_title.toString());
            fields.add( SearchFormData.QUERY_FIELD.news_content.toString());
        }else {
            fields.add( queryField.toString());
        }

        if (formData.getSearchType().equalsIgnoreCase("true")) {
            SearchRequest requestss = new SearchRequest.Builder()
                    //去哪个索引里搜索
                    .index("news_test")
                    .query(QueryBuilders.bool(bool ->
                            bool.must(must ->
                                    must.multiMatch(v ->
                                            v.query(queryStr).fields(fields).type(TextQueryType.Phrase)))

                    ))
                    .build();

            return requestss;
        }else {
            SearchRequest requestss = new SearchRequest.Builder()
                    //去哪个索引里搜索
                    .index("news_test")
                    .query( b -> b.multiMatch(v ->
                            v.query(queryStr).fields(fields).type(TextQueryType.BestFields)))
                    .build();
            return requestss;
        }

    }








}




