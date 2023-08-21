package iie.controller;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.ShardStatistics;
import co.elastic.clients.elasticsearch._types.query_dsl.*;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.elasticsearch.core.search.HitsMetadata;
import co.elastic.clients.elasticsearch.core.search.TotalHits;
import co.elastic.clients.json.JsonData;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import com.alibaba.fastjson.JSONObject;
import iie.Utils.Check;
import iie.bean.Book;
import iie.bean.SearchFormData;
import iie.service.EsClientService;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpHost;

//import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.client.RestClient;


import org.springframework.beans.factory.annotation.Autowired;
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
    //自定义错误码
    private final Integer failCode = 999;


    @Autowired
    private EsClientService esClientService;

    //@RequestParam("name") String name
    //@ModelAttribute FormData formData

    //http://localhost:8080/ReadingAssist/advancedsearch/searchAdvanced
    @PostMapping(value = "/ReadingAssist/advancedsearch/searchAdvanced")
    public  ResponseEntity<JSONObject> postFlume(@ModelAttribute SearchFormData formData)
    {
        System.out.println(formData);
        String errMessage =  Check.CheckParame(formData);
        if (!errMessage.equalsIgnoreCase("ok")){
            System.err.println(errMessage);
            return ResponseEntity.ok().body(failRequest(errMessage,failCode));
        }

       // RestClient client =   RestClient.builder(new HttpHost("localhost", 9200, "http")).build()

        //org.elasticsearch.client.internal.ElasticsearchClient

        //RestHighLevelClient client = new RestHighLevelClient();

 /*  RestHighLevelClient client = new RestHighLevelClient(
                RestClient.builder(
                        new HttpHost("localhost", 9200, "http")));*/


        SearchRequest searchRequest = CreateSearchRequest(formData);
        System.out.println(searchRequest);

        JSONObject quest = new JSONObject();

        try {
            ElasticsearchClient  esClient = esClientService.getClient();

            if (esClient == null){
                return ResponseEntity.ok().body(failRequest("连接Es发生错误，请检查Es服务是否正常",failCode));
            }

            SearchResponse<Book> search = esClient.search(searchRequest, Book.class);

            List<JSONObject> doc = new ArrayList<>();

         /*   System.out.println("search.toString() = " + search.toString());
            long took = search.took();
            System.out.println("took = " + took);
            boolean b = search.timedOut();
            System.out.println("b = " + b);
            ShardStatistics shards = search.shards();
            System.out.println("shards = " + shards);
            */
            HitsMetadata<Book> hits = search.hits();
            TotalHits total = hits.total();
            System.out.println("命中hits.total = " + total);
/*            System.out.println("total = " + total);
            Double maxScore = hits.maxScore();
            System.out.println("maxScore = " + maxScore);*/
            List<Hit<Book>> list = hits.hits();
            for (Hit<Book> bookHit : list) {

                System.out.println("bookHit.score() = " + bookHit.score());
                System.out.println("bookHit.index() = " + bookHit.index());

                //关键在这里
                //System.out.println("bookHit.source() = " + bookHit.source());
                Book hitSource =  bookHit.source();

                JSONObject link = new JSONObject();
                link.put("news_title",hitSource.getNews_title());
                link.put("news_autho",hitSource.getNews_author());
                link.put("news_publictime",hitSource.getNews_publictime());
                link.put("news_publictime_date",hitSource.getNews_publicdate());
                link.put("news_website",hitSource.getNews_website());
                link.put("news_website_type",hitSource.getNews_website_type());
                link.put("news_content",hitSource.getNews_content_zh());
                link.put("id",hitSource.getNews_title_zh());
                link.put("media_url",hitSource.getNews_url());
                link.put("news_type",hitSource.getNews_type());

                doc.add(link);
            }


            JSONObject results = new JSONObject();
            results.put("totalResults",total.value());
            results.put("document",doc);

            quest.put("code",200);
            quest.put("message","成功");
            quest.put("results",results);


            //解析结果
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.ok().body(failRequest("Es查询时发生错误 :"+e.getMessage(),failCode));
        }

        return ResponseEntity.ok(quest);
    }

    private JSONObject failRequest(String message,int code )
    {
        JSONObject js  = new JSONObject();
        js.put("message",message);
        js.put("code",code);

        return js;
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

        //精准匹配和模糊匹配的区别，本质区别就是TextQueryType的值不同
        MultiMatchQuery mm = null;
        if (formData.getSearchType().equalsIgnoreCase("true")) {
             mm = MultiMatchQuery.of(v -> v.query(queryStr).fields(fields).type(TextQueryType.Phrase));
        }else {
             mm = MultiMatchQuery.of(v -> v.query(queryStr).fields(fields).type(TextQueryType.BestFields));
        }
        MultiMatchQuery finalMm = mm;

        //构建查询语句
        SearchRequest.Builder builder = new SearchRequest.Builder();

        RangeQuery rq =  RangeQuery.of(r -> r.field("news_publicdate").gte(JsonData.of(formData.getStartDate())).lte(JsonData.of(formData.getEndDate())));
        TermsQuery sq1 = TermsQuery.of(t -> t.field("news_website_type").terms(x -> x.value(formData.getWebSiteTypeArray())));
        TermsQuery sq2 = TermsQuery.of(t -> t.field("news_website").terms(x -> x.value(formData.getWebSitesArray())));

        builder = builder
                //去哪个索引里搜索
                .index("news_01")
                .query(QueryBuilders.bool(bool ->
                        {
                            BoolQuery.Builder b =
                                    bool.must(must ->must.multiMatch(finalMm))
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

        SearchRequest sr = builder.build();

     /*   SearchRequest sr =  builder
                .sort(s -> s.field(f -> f.field("news_publictime").order(formData.getSortOrder())))
                .from(formData.getCurrentPage())
                .size(formData.getPageSize())
                .trackTotalHits(c -> c.count(100000000))
                .build();*/

        return sr;
    }








}




