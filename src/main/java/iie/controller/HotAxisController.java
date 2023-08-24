package iie.controller;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.Script;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch._types.aggregations.Aggregate;
import co.elastic.clients.elasticsearch._types.aggregations.LongTermsAggregate;
import co.elastic.clients.elasticsearch._types.aggregations.LongTermsBucket;
import co.elastic.clients.elasticsearch._types.aggregations.StringTermsBucket;
import co.elastic.clients.elasticsearch._types.mapping.RuntimeFieldType;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.util.NamedValue;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import iie.Utils.CheckUtil;
import iie.domain.AggsCount;
import iie.domain.News;
import iie.domain.SearchFormData;
import iie.service.EsClientService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/*08.24TODO
1，构建http返回体
2，条件限制index*/


@Controller
@CrossOrigin
public class HotAxisController {

    //自定义错误码
    private final Integer failCode = 999;


    @Autowired
    private EsClientService esClientService;


    private static final Logger LOG = LoggerFactory.getLogger(HotAxisController.class);

    @PostMapping(value = "/ReadingAssist/advancedsearch/getHotAxisByAdv")
    public ResponseEntity<JSONObject> postFlume(@ModelAttribute SearchFormData formData)
    {
        LOG.info(formData.toString());
        String errMessage =  CheckUtil.CheckParam(formData);
        if (!errMessage.equalsIgnoreCase("ok")){
            LOG.error(errMessage);
            return ResponseEntity.ok().body(failRequest(errMessage,failCode));
        }

        SearchRequest searchRequest = null;
        SearchRequest searchRequest_all = null;


        try {
            LOG.info("HOT SearchRequest.Builder");
            SearchRequest.Builder builder = esClientService.CreateSearchRequest_HOT(formData);
            LOG.info("ALL SearchRequest.Builder");
            SearchRequest.Builder builder_all = esClientService.CreateSearchRequest_ALL(formData);

            searchRequest = builder.build();
            searchRequest_all = builder_all.build();


            LOG.info("hot查询语句 :"+searchRequest.toString());
            //LOG.info("all查询语句 :"+searchRequest_all.toString());

        }catch (Exception e){
            //获取查询语句出错
            LOG.error(e.getMessage());
            e.printStackTrace();
            return ResponseEntity.ok().body(failRequest("获取查询语句出错 :" + e.getMessage(),failCode));
        }

        ElasticsearchClient esClient = esClientService.getClient();
        if (esClient == null){
            return ResponseEntity.ok().body(failRequest("连接Es发生错误，请检查Es服务是否正常",failCode));
        }


        SearchResponse<News> search = null;
        SearchResponse<News> search_all = null;
        try {

            //查询Es数据
            LOG.info("HOT SearchRequest");
             search = esClient.search(searchRequest, News.class);
            LOG.info("ALL SearchRequest");
             search_all = esClient.search(searchRequest_all, News.class);
        } catch (Exception e) {
            LOG.error(e.getMessage());
            e.printStackTrace();
            return ResponseEntity.ok().body(failRequest("查询Es数据出现问题 ：" + e.getMessage(),failCode));
        }
        //转换成java内部数据
        Map<String,Map<String, AggsCount>> hotMap =  esClientService.jiexi(search);
        Map<String,Map<String, AggsCount>> allMap = esClientService.jiexi(search_all);

        //生成Http数据
        ObjectMapper objectMapper = new ObjectMapper();
        String hotMaps =  "";
        String hotMapss = "";
        try {
            hotMaps =  objectMapper.writeValueAsString(hotMap);
            hotMapss = objectMapper.writeValueAsString(allMap);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }


        //查询个啥
        return ResponseEntity.ok(failRequest(hotMaps + "         " + hotMapss,200));


    }


    private JSONObject failRequest(String message,int code )
    {
        JSONObject js  = new JSONObject();
        js.put("message",message);
        js.put("code",code);

        return js;
    }

}
