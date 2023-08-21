package iie.controller;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.FieldSort;
import co.elastic.clients.elasticsearch._types.ShardStatistics;
import co.elastic.clients.elasticsearch._types.query_dsl.*;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.elasticsearch.core.search.HitsMetadata;
import co.elastic.clients.elasticsearch.core.search.TotalHits;
import co.elastic.clients.elasticsearch.core.search.TrackHits;
import co.elastic.clients.elasticsearch.indices.CreateIndexResponse;
import co.elastic.clients.json.JsonData;
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



  /*      try {

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
        }*/






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

        //构建查询语句
        SearchRequest.Builder builder = new SearchRequest.Builder();
        if (formData.getSearchType().equalsIgnoreCase("true")) {

            MultiMatchQuery mm = MultiMatchQuery.of(v -> v.query(queryStr).fields(fields).type(TextQueryType.Phrase));

            RangeQuery rq =  RangeQuery.of(r -> r.field("news_publicdate").gte(JsonData.of(formData.getStartDate())).lte(JsonData.of(formData.getEndDate())));

            TermsQuery sq1 = TermsQuery.of(t -> t.field("news_website_type").terms(x -> x.value(formData.getWebSiteTypeArray())));
            TermsQuery sq2 = TermsQuery.of(t -> t.field("news_website").terms(x -> x.value(formData.getWebSitesArray())));
            builder = builder
                    //去哪个索引里搜索
                    .index("news_test")
                    .query(QueryBuilders.bool(bool ->
                            {
                                BoolQuery.Builder b =
                                        bool.must(must ->must.multiMatch(mm))
                                            .filter(f -> f.range(rq))
                                            .filter(f -> f.terms(sq1))
                                            .filter(f -> f.terms(sq2));

                                //需要条件判断，如果为空就不限制news_type
                                if (!StringUtils.isEmpty(formData.getType())){
                                    TermQuery tq =  TermQuery.of(t -> t.field("news_type").value(formData.getType()));
                                    b.filter(f -> f.term(tq));
                                }

                                return b;
                            }
                    ));

        }else {
            MultiMatchQuery mm = MultiMatchQuery.of(v -> v.query(queryStr).fields(fields).type(TextQueryType.BestFields));
            builder = builder
                    //去哪个索引里搜索
                    .index("news_test")
                    .query( b -> b.multiMatch(mm));
        }


        SearchRequest sr =  builder
                .sort(s -> s.field(f -> f.field("news_publictime").order(formData.getSortOrder())))
                .from(formData.getCurrentPage())
                .size(formData.getPageSize())
                .trackTotalHits(c -> c.count(100000000))
                .build();

        return sr;
    }








}




