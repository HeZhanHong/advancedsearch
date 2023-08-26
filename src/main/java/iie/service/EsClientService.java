package iie.service;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.ElasticsearchException;
import co.elastic.clients.elasticsearch._types.FieldValue;
import co.elastic.clients.elasticsearch._types.SortOptions;
import co.elastic.clients.elasticsearch._types.aggregations.LongTermsBucket;
import co.elastic.clients.elasticsearch._types.aggregations.StringTermsBucket;
import co.elastic.clients.elasticsearch._types.query_dsl.*;
import co.elastic.clients.elasticsearch.cluster.HealthResponse;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.SourceConfig;
import co.elastic.clients.json.JsonData;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import iie.controller.SearchAdvancedController;
import iie.domain.AdsNode;
import iie.domain.AggsCount;
import iie.domain.News;
import iie.domain.SearchFormData;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.StringReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class EsClientService {

    private static final Logger LOG = LoggerFactory.getLogger(EsClientService.class);

/*   es.Hosts=localhost:9200,localhost:9200
    es.userName=
    es.passWord=
    es.queryIndex=news_01*/

    @Value("${es.Hosts}")
    public String esHosts;

    @Value("${es.security.enable}")
    public String security;

    @Value("${es.security.user}")
    public String userName;

    @Value("${es.security.pass}")
    public String passWord;

    @Value("${es.queryIndex}")
    public String queryIndex;

    @Value("${es.trackTotalHits}")
    public String trackTotalHits;


    private ElasticsearchClient client = null;



    public EsClientService()
    {
       /* RestClientTransport ss =  (RestClientTransport)client._transport();
        ss.restClient().isRunning()*/
    }

    public ElasticsearchClient getClient()
    {
        if (client == null){
            //首次连接
            ConnectES();
        }else {

            RestClientTransport restClientTransport =  (RestClientTransport)client._transport();
            RestClient restClient = restClientTransport.restClient();
            try {
                if (restClient.isRunning() && client.ping().value()){
                    //客户端正常
                }else {
                   //客户端不正常
                    if (restClient.isRunning()){
                        restClient.close();
                    }
                    client = null;
                    //重连
                    ConnectES();
                }
            } catch (ElasticsearchException | IOException e) {
                e.printStackTrace();
                client = null;

            }finally {
                return client;
            }
        }

        return client;
    }

    private void ConnectES() {
        if (client != null) {
        } else {
            LOG.info("连接Es中....");
            try {

                LOG.info("es.Hosts :"+esHosts);
       /*         LOG.info(userName);
                LOG.info(passWord);*/
                LOG.info("es.security.enable :"+ security);
                LOG.info("es.queryIndex :" +queryIndex);


                HttpHost[] httpHosts = Arrays.stream(esHosts.split(",")).map(x -> {
                    String[] hostInfo = x.split(":");
                    return new HttpHost(hostInfo[0], Integer.parseInt(hostInfo[1]));
                }).toArray(HttpHost[]::new);
                LOG.info(esHosts);

                RestClientBuilder builder = RestClient.builder(httpHosts);

                //开启安全认证
                if(security.equalsIgnoreCase("true")){
                    CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
                    credentialsProvider.setCredentials(
                            AuthScope.ANY, new UsernamePasswordCredentials(userName, passWord));//设置账号密码

                    builder.setHttpClientConfigCallback(httpClientBuilder -> httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider));
                }

                // Create the low-level client
                RestClient restClient = builder.build();
                // Create the transport with a Jackson mapper
                ElasticsearchTransport transport = new RestClientTransport(
                        restClient, new JacksonJsonpMapper(),null);
                // And create the API client
                client = new ElasticsearchClient(transport);

                HealthResponse  resp = client.cluster().health();

                LOG.info("cluster : " + resp.toString());
                //获取连接
/*
                RestClient restClient = RestClient.builder(new HttpHost(esIP, Integer.parseInt(esPort) , "http")).build();
                RestClientTransport transport = new RestClientTransport(restClient, new JacksonJsonpMapper(), null);
                client = new ElasticsearchClient(transport);*/
            }catch (Exception e)
            {
                LOG.error("连接Es出现异常!!!!");
                e.printStackTrace();
                client = null;
                return;
            }
            LOG.info("连接Es成功");

        }
    }




    public SearchRequest.Builder CreateSearchRequest  (SearchFormData formData)
    {
        String queryStr = formData.getQueryStr();
        List<String> fields = new ArrayList<String>();

        SearchFormData.QUERY_FIELD  queryField =  SearchFormData.QUERY_FIELD.valueOf(formData.getQueryField());
        if (queryField == SearchFormData.QUERY_FIELD.all){
            fields.add( SearchFormData.QUERY_FIELD.news_title_zh.toString());
            fields.add( SearchFormData.QUERY_FIELD.news_content_zh.toString());
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

        builder = builder
                //去哪个索引里搜索
                .index(queryIndex)
                .query(QueryBuilders.bool(bool ->
                        {
                            BoolQuery.Builder b =
                                    bool.must(must ->must.multiMatch(finalMm))
                                            .filter(f -> f.range(rq));


                            List<FieldValue> webSiteTypeArray =   formData.getWebSiteTypeArray();
                            if (webSiteTypeArray != null && webSiteTypeArray.size() > 0 ){
                                TermsQuery sq1 = TermsQuery.of(t -> t.field("news_website_type").terms(x -> x.value(webSiteTypeArray)));
                                b = b.filter(f -> f.terms(sq1));
                            }

                            List<FieldValue> webSitesArray =   formData.getWebSitesArray();
                            if (webSitesArray != null && webSitesArray.size() > 0 ){
                                TermsQuery sq2 = TermsQuery.of(t -> t.field("news_website").terms(x -> x.value(webSitesArray)));
                                b = b.filter(f -> f.terms(sq2));
                            }

                            //需要条件判断，如果为空就不限制news_type
                            if (!StringUtils.isEmpty(formData.getType())){
                                TermQuery tq =  TermQuery.of(t -> t.field("news_type").value(formData.getType()));
                                b.filter(f -> f.term(tq));
                            }

                            return b;
                        }
                ));

        Integer totalHits = 1000;
        try {
            totalHits = Integer.parseInt(trackTotalHits);
            if (totalHits <= 0){
                LOG.error("trackTotalHits配置 小于等于0，自动设置默认值为1000");
                totalHits = 1000;
            }
        }catch (NumberFormatException e){
            //转换报错设置默认值
            e.printStackTrace();
            LOG.error("trackTotalHits配置不是数字类型，自动设置默认值为1000");
            totalHits = 1000;
        }

        Integer finalTotalHits = totalHits;


        SortOptions scoreSort = SortOptions.of(s -> s.field(f ->f.field("_score").order(formData.getSortOrder()) ));
        SortOptions timeSort = SortOptions.of(s -> s.field(f ->f.field("news_publictime").order(formData.getSortOrder()) ));


     /*   StringReader sourcejson = new StringReader("{\n" +
                "\t\"_source\": [\"news_title\", \"news_author\", \"news_publictime\", \"news_publicdate\", \"news_website\", \"news_website_type\", \"news_content_zh\", \"id\", \"news_url\", \"news_type\"]\n" +
                "}");*/

        SearchRequest.Builder sr =  builder
                .sort(scoreSort,timeSort)
                .source(s ->s.filter(f -> f.includes("news_title","news_author",
                        "news_publictime","news_publicdate","news_website","news_website_type","news_content_zh","id","news_url","news_type")))
              //  .withJson(sourcejson)
                .from(formData.getCurrentPage())
                .size(formData.getPageSize())
                .trackTotalHits(c -> c.count(finalTotalHits));


        return sr;
    }


     public SearchRequest.Builder CreateSearchRequest_HOT (SearchFormData formData)
    {
        SearchRequest.Builder builder =   CreateSearchRequest(formData);
        //聚合
        builder = aggs_date_type(builder);

        return builder;
    }

    public SearchRequest.Builder CreateSearchRequest_ALL (SearchFormData formData)
    {
        //无需任何过滤，只要当天的总数量，包括所有Type

        Integer totalHits = 1000;
        try {
            totalHits = Integer.parseInt(trackTotalHits);
            if (totalHits <= 0){
                LOG.error("trackTotalHits配置 小于等于0，自动设置默认值为1000");
                totalHits = 1000;
            }
        }catch (NumberFormatException e){
            //转换报错设置默认值
            e.printStackTrace();
            LOG.error("trackTotalHits配置不是数字类型，自动设置默认值为1000");
            totalHits = 1000;
        }

        Integer finalTotalHits = totalHits;


        SearchRequest.Builder builder = new SearchRequest.Builder();
        builder =  builder
                .index(queryIndex)
                .sort(s -> s.field(f -> f.field("news_publictime").order(formData.getSortOrder())))
                .source(s ->s.filter(f -> f.includes("id")))
                .from(formData.getCurrentPage())
                .size(formData.getPageSize())
                .trackTotalHits(c -> c.count(finalTotalHits));



        //聚合
        builder = aggs_date_type(builder);

        return builder;
    }


    private SearchRequest.Builder aggs_date_type (SearchRequest.Builder builder)
    {

        //builder.runtimeMappings("runtime_date_type",m -> m.type(RuntimeFieldType.Keyword).script(s -> s.inline(i -> i.lang("painless").source("doc['news_publicdate'].value+'-'+doc['news_type'].value"))));
        //聚合
        // Script script =  Script.of(b -> b.inline(i -> i.source("doc['news_publicdate'].value +'-'+ doc['news_type'].value")));
        //   builder.aggregations("", a-> a.terms(jj -> jj.script(script)));

        // builder.

        builder.aggregations("date", aggs ->
                aggs.terms(t -> t.field("news_publicdate"))
                        .aggregations("type",aggs2 -> aggs2.terms(t -> t.field("news_type")))

        );

        return builder;
    }


    public  Map<String, Map<String, AggsCount>> parse (SearchResponse<News> search)
    {

        Map<String, Map<String, AggsCount>> aggsInfo = new HashMap<>();
        //即使没匹配到数据，date聚合器还是不会空。
        List<LongTermsBucket>   dateBuckets =  search.aggregations().get("date").lterms().buckets().array();
        if (dateBuckets.size() <= 0){
            //return ResponseEntity.ok(failRequest("匹配不到数据，Es语句 :"+searchRequest ,200));
            return aggsInfo;
        }else {
            dateBuckets.forEach(k ->
                    {
                        //聚合的日期
                        String date = k.keyAsString();
                        if (!aggsInfo.containsKey(date)){
                            aggsInfo.put(date,new HashMap<>());
                        }

                        //二次索引，也是必须有的，无需判空
                        List<StringTermsBucket> typeBuckets =  k.aggregations().get("type").sterms().buckets().array();
                        for (StringTermsBucket bucket:typeBuckets) {

                            String news_type =  bucket.key()._toJsonString();
                            Long news_count =  bucket.docCount();

                            AggsCount aggsCount =   new AggsCount(date,news_type,news_count);
                            aggsInfo.get(date).put(news_type,aggsCount);

                        }
                    }
            );
        }

        return aggsInfo;
    }

    public  Map<String, Map<String, AggsCount>> parse2 (SearchResponse<News> search)
    {


        int perMon = 0;

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        //即使没匹配到数据，date聚合器还是不会空。
        List<LongTermsBucket>   dateBuckets =  search.aggregations().get("date").lterms().buckets().array();
        if (dateBuckets.size() <= 0){
            //return ResponseEntity.ok(failRequest("匹配不到数据，Es语句 :"+searchRequest ,200));
            return null;
        }else {
            dateBuckets.forEach(k ->
                    {
                        //聚合的日期
                        String date = k.keyAsString();
                        Date nowDate = null;
                        try {
                             nowDate =   sdf.parse(date);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }



                        AdsNode dayeNode = new AdsNode(AdsNode.NodeType.DAY,date,0,null);
                        Map<String,AdsNode> childList = new HashMap<>();

                       /* if (!aggsInfo.containsKey(date)){
                            aggsInfo.put(date,new HashMap<>());
                        }*/

                        //二次索引，也是必须有的，无需判空
                        List<StringTermsBucket> typeBuckets =  k.aggregations().get("type").sterms().buckets().array();
                        for (StringTermsBucket bucket:typeBuckets) {

                            String news_type =  bucket.key()._toJsonString();
                            Long news_count =  bucket.docCount();

                           /* AggsCount aggsCount =   new AggsCount(date,news_type,news_count);
                            aggsInfo.get(date).put(news_type,aggsCount);*/

                            AdsNode typeNode = new AdsNode(AdsNode.NodeType.TYPE,news_type,news_count,null);
                            childList.put(news_type,typeNode);
                        }
                        dayeNode.setChildList(childList);


                    }
            );
        }

        return null;
    }












}
