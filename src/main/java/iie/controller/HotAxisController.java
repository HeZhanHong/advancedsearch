package iie.controller;

import co.elastic.clients.elasticsearch._types.Script;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch._types.mapping.RuntimeFieldType;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.util.NamedValue;
import com.alibaba.fastjson.JSONObject;
import iie.Utils.CheckUtil;
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

import java.util.HashMap;

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


        try {
           SearchRequest.Builder builder = esClientService.CreateSearchRequest(formData);

            builder.aggregations("date", aggs ->
                    aggs.terms(t -> t.field("news_publicdate"))
                            .aggregations("type",aggs2 -> aggs2.terms(t -> t.field("news_type")))

            );

            //builder.runtimeMappings("runtime_date_type",m -> m.type(RuntimeFieldType.Keyword).script(s -> s.inline(i -> i.lang("painless").source("doc['news_publicdate'].value+'-'+doc['news_type'].value"))));
            //聚合
           // Script script =  Script.of(b -> b.inline(i -> i.source("doc['news_publicdate'].value +'-'+ doc['news_type'].value")));
         //   builder.aggregations("", a-> a.terms(jj -> jj.script(script)));

           // builder.

            searchRequest = builder.build();

        }catch (Exception e){
            //获取查询语句出错
            LOG.error(e.getMessage());
            e.printStackTrace();
            return ResponseEntity.ok().body(failRequest(e.getMessage(),failCode));
        }







        //查询个啥
        return ResponseEntity.ok(failRequest( searchRequest.toString(),200));
    }


    private JSONObject failRequest(String message,int code )
    {
        JSONObject js  = new JSONObject();
        js.put("message",message);
        js.put("code",code);

        return js;
    }

}
