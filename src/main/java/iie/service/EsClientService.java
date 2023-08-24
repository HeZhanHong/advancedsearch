package iie.service;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.ElasticsearchException;
import co.elastic.clients.elasticsearch._types.FieldValue;
import co.elastic.clients.elasticsearch._types.query_dsl.*;
import co.elastic.clients.elasticsearch.cluster.HealthResponse;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.json.JsonData;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import iie.controller.SearchAdvancedController;
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
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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

        Integer totalHits = 10000;
        try {
            totalHits = Integer.parseInt(trackTotalHits);
            if (totalHits <= 0){
                LOG.error("trackTotalHits配置 小于等于0，自动设置默认值为10000");
                totalHits = 10000;
            }
        }catch (NumberFormatException e){
            //转换报错设置默认值
            e.printStackTrace();
            LOG.error("trackTotalHits配置不是数字类型，自动设置默认值为10000");
            totalHits = 10000;
        }

        Integer finalTotalHits = totalHits;

        SearchRequest.Builder sr =  builder
                .sort(s -> s.field(f -> f.field("news_publictime").order(formData.getSortOrder())))
                .from(formData.getCurrentPage())
                .size(formData.getPageSize())
                .trackTotalHits(c -> c.count(finalTotalHits));


        return sr;
    }






}
