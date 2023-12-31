package iie.controller;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.query_dsl.*;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.elasticsearch.core.search.HitsMetadata;
import co.elastic.clients.elasticsearch.core.search.TotalHits;
import co.elastic.clients.json.JsonData;
import com.alibaba.fastjson.JSONObject;
import iie.Utils.CheckUtil;

import iie.domain.RepNews;
import iie.domain.SearchFormData;
import iie.service.EsClientService;
import org.apache.commons.lang3.StringUtils;

//import org.elasticsearch.action.search.SearchRequest;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
public class SearchAdvancedController
{

    private static final Logger LOG = LoggerFactory.getLogger(SearchAdvancedController.class);
    
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
        LOG.info(formData.toString());
        String errMessage =  CheckUtil.CheckParam(formData);
        if (!errMessage.equalsIgnoreCase("ok")){
            LOG.error(errMessage);
            return ResponseEntity.ok().body(failRequest(errMessage,failCode));
        }

       // RestClient client =   RestClient.builder(new HttpHost("localhost", 9200, "http")).build()

        //org.elasticsearch.client.internal.ElasticsearchClient

        //RestHighLevelClient client = new RestHighLevelClient();

 /*  RestHighLevelClient client = new RestHighLevelClient(
                RestClient.builder(
                        new HttpHost("localhost", 9200, "http")));*/

        SearchRequest  searchRequest = null;

        try {
            SearchRequest.Builder builder = esClientService.CreateSearchRequest(formData);
            builder = esClientService.SortAndSourceBuilder(builder,formData.getSortOrder());

            searchRequest =  builder.build();

            LOG.info("查询语句 :"+searchRequest.toString());
        }catch (Exception e){
            //获取查询语句出错
            LOG.error(e.getMessage());
            e.printStackTrace();
            return ResponseEntity.ok().body(failRequest("获取查询语句出错 :" + e.getMessage(),failCode));
        }


        ElasticsearchClient  esClient = esClientService.getClient();

        if (esClient == null){
            return ResponseEntity.ok().body(failRequest("连接Es发生错误，请检查Es服务是否正常",failCode));
        }

        SearchResponse<RepNews> search = null;
        try {
             search = esClient.search(searchRequest, RepNews.class);
        } catch (Exception e) {
            LOG.error(e.getMessage());
            e.printStackTrace();
            return ResponseEntity.ok().body(failRequest("查询Es数据出现问题 ：" + e.getMessage(),failCode));
        }

        //解析结果
        List<JSONObject> doc = new ArrayList<>();

         /*   LOG.info("search.toString() = " + search.toString());
            long took = search.took();
            LOG.info("took = " + took);
            boolean b = search.timedOut();
            LOG.info("b = " + b);
            ShardStatistics shards = search.shards();
            LOG.info("shards = " + shards);
            */
        HitsMetadata<RepNews> hits = search.hits();
        TotalHits total = hits.total();
        LOG.info("命中数量 :" + total);
/*            LOG.info("total = " + total);
            Double maxScore = hits.maxScore();
            LOG.info("maxScore = " + maxScore);*/
        List<Hit<RepNews>> list = hits.hits();
        for (Hit<RepNews> newsHit : list) {

    /*            LOG.info("newsHit.score() = " + newsHit.score());
                LOG.info("newsHit.index() = " + newsHit.index());*/

            //关键在这里
            //LOG.info("newsHit.source() = " + newsHit.source());
            RepNews hitSource =  newsHit.source();

            JSONObject link = new JSONObject();
            link.put("news_title",hitSource.getNews_title());
            link.put("news_autho",hitSource.getNews_author());
            link.put("news_publictime",hitSource.getNews_publictime());
            link.put("news_publictime_date",hitSource.getNews_publicdate());
            link.put("news_website",hitSource.getNews_website());
            link.put("news_website_type",hitSource.getNews_website_type());
            link.put("news_content",hitSource.getNews_content_zh());
            link.put("id",hitSource.getId());
            link.put("media_url",hitSource.getNews_url());
            link.put("news_type",hitSource.getNews_type());

            doc.add(link);
        }


        JSONObject results = new JSONObject();
        results.put("totalResults",total.value());
        results.put("document",doc);
        JSONObject quest = new JSONObject();
        quest.put("code",200);
        quest.put("message","成功");
        quest.put("results",results);

        return ResponseEntity.ok(quest);
    }

    private JSONObject failRequest(String message,int code )
    {
        JSONObject js  = new JSONObject();
        js.put("message",message);
        js.put("code",code);

        return js;
    }










}




