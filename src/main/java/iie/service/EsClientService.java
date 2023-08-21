package iie.service;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.ElasticsearchException;
import co.elastic.clients.elasticsearch._types.query_dsl.*;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.json.JsonData;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import iie.controller.SearchAdvancedController;
import iie.domain.SearchFormData;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class EsClientService {

    private static final Logger LOG = LoggerFactory.getLogger(EsClientService.class);

    @Value("${es.ip}")
    public String esIP;

    @Value("${es.port}")
    public String esPort;

    @Value("${es.queryIndex}")
    public String queryIndex;

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
            LOG.info("连接Es中......");
            try {
                RestClient restClient = RestClient.builder(new HttpHost(esIP, Integer.parseInt(esPort) , "http")).build();
                RestClientTransport transport = new RestClientTransport(restClient, new JacksonJsonpMapper(), null);
                client = new ElasticsearchClient(transport);
            }catch (Exception e)
            {
                LOG.error("连接Es出现异常!!!!");
                e.printStackTrace();
                client = null;
            }
        }
    }




    public SearchRequest CreateSearchRequest  (SearchFormData formData)
    {
        String queryStr = formData.getQueryStr();
        List<String> fields = new ArrayList<String>();

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
                .index(queryIndex)
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
