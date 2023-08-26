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
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import iie.Utils.CheckUtil;
import iie.domain.*;
import iie.service.EsClientService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;


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


        SearchResponse<RepNews> search = null;
        SearchResponse<RepNews> search_all = null;
        try {

            //查询Es数据
            LOG.info("HOT SearchRequest");
             search = esClient.search(searchRequest, RepNews.class);
            LOG.info("ALL SearchRequest");
             search_all = esClient.search(searchRequest_all, RepNews.class);
        } catch (Exception e) {
            LOG.error(e.getMessage());
            e.printStackTrace();
            return ResponseEntity.ok().body(failRequest("查询Es数据出现问题 ：" + e.getMessage(),failCode));
        }



        //转换成java内部数据
        List<String> newsType = new ArrayList<>();
        AdsNode hotRoot = new AdsNode(AdsNode.NodeType.ROOT,"hotRoot",0);
        AdsNode allRoot = new AdsNode(AdsNode.NodeType.ROOT,"allRoot",0);
        boolean aggs = false;
        try {
            aggs = esClientService.parse2(hotRoot,search,null);
            if (aggs == false){
                //聚合数据为空
                return ResponseEntity.ok(failRequest("聚合数据为空",200));
            }
            esClientService.parse2(allRoot,search_all,newsType);
        } catch (ParseException e) {
            LOG.error(e.getMessage());
            e.printStackTrace();
            return ResponseEntity.ok().body(failRequest("parse2出现问题 ：" + e.getMessage(),failCode));
        }


        Date startDay = null;
        Date endDay = null;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat sdf_month = new SimpleDateFormat("yyyy-MM");
        SimpleDateFormat sdf_day = new SimpleDateFormat("dd");
        try {
            startDay =   sdf.parse(formData.getStartDate());
            endDay =   sdf.parse(formData.getEndDate());
        } catch (ParseException e) {
            e.printStackTrace();
        }

        JSONObject countStatInfo = new JSONObject();
        countStatInfo.put("alerts",new ArrayList<Double>() {{
            add(104065.01);
            add(3121950.2);
        }});
        countStatInfo.put("tag",new ArrayList<String>() {{
            add("日");
            add("月");
        }});
        //嵌套List
        countStatInfo.put("statistic",new ArrayList<ArrayList<CountStatInfo<Long>>>());
        //日和月
        countStatInfo.getJSONArray("statistic").add(new ArrayList<CountStatInfo<Double>>());
        countStatInfo.getJSONArray("statistic").add(new ArrayList<CountStatInfo<Double>>());

        JSONObject hotStatInfo = new JSONObject();
        hotStatInfo.put("alerts",new ArrayList<Long>() {{
            add(150L);
            add(4500L);
        }});
        hotStatInfo.put("tag",new ArrayList<String>() {{
            add("日");
            add("月");
        }});
        //嵌套List
        hotStatInfo.put("statistic",new ArrayList<ArrayList<CountStatInfo<Double>>>());
        hotStatInfo.getJSONArray("statistic").add(new ArrayList<CountStatInfo<Double>>());
        hotStatInfo.getJSONArray("statistic").add(new ArrayList<CountStatInfo<Double>>());


        //类型
        JSONObject typeStatInfo = new JSONObject();
        typeStatInfo.put("alerts",new ArrayList<Double>() {{
            add(146.40778);
            add(4392.2334);
        }});
        typeStatInfo.put("tag",new ArrayList<String>() {{
            add("日");
            add("月");
        }});
        //嵌套List
        ArrayList<TypeStatInfo> typeStatInfos = new ArrayList<TypeStatInfo>();
        typeStatInfo.put("statistic",typeStatInfos);

        TypeStatInfo dayJSONObjectTypeStat= new TypeStatInfo();
        TypeStatInfo monthJSONObjectTypeStat =new TypeStatInfo();

        List<TypeStatInfo_series> series = new ArrayList<>();
        for (int i = 0; i < newsType.size(); i++) {
            series.add(new TypeStatInfo_series(newsType.get(i)));
        }
        List<TypeStatInfo_series> copiedList = new ArrayList<>(series);

        dayJSONObjectTypeStat.setSeries(series);
        monthJSONObjectTypeStat.setSeries(copiedList);
        dayJSONObjectTypeStat.setLegend(newsType);
        monthJSONObjectTypeStat.setLegend(newsType);


        typeStatInfos.add(dayJSONObjectTypeStat);
        typeStatInfos.add(monthJSONObjectTypeStat);



        JSONArray dayJsonArrayCountStat =   countStatInfo.getJSONArray("statistic").getJSONArray(0);
        JSONArray monthJsonArrayCountStat =   countStatInfo.getJSONArray("statistic").getJSONArray(1);

        JSONArray dayJsonArrayhotStat =   hotStatInfo.getJSONArray("statistic").getJSONArray(0);
        JSONArray monthJsonArrayhotStat =   hotStatInfo.getJSONArray("statistic").getJSONArray(1);





        Calendar calendar =  Calendar.getInstance();
        calendar.setTime(startDay);
        String currMonth = "";
        while (true){
            Date Date= calendar.getTime();
            String DateStr = sdf.format(Date);
            String month =   sdf_month.format(Date);
            String day =   sdf_day.format(Date);
            currMonth = month;



            hotStatInfo.getJSONArray("statistic").get(0);

            long dayCountHot =  AdsNode.getCount(AdsNode.NodeType.DAY, hotRoot,month,day,"");
            long dayCountAll =  AdsNode.getCount(AdsNode.NodeType.DAY, allRoot,month,day,"");
            if (dayCountHot <= 0 || dayCountAll <= 0){
                dayJsonArrayCountStat.add(new CountStatInfo<Long>(DateStr,0l));
                dayJsonArrayhotStat.add(new CountStatInfo<Double>(DateStr,0d));
            }else {

                dayJsonArrayCountStat.add(new CountStatInfo<Long>(DateStr,dayCountHot));

                double percent = (double) dayCountHot / dayCountAll * 100;
                dayJsonArrayhotStat.add(new CountStatInfo<Double>(DateStr,percent));
            }

            dayJSONObjectTypeStat.getxAxis().add(DateStr);
            for (int t = 0; t < newsType.size(); t++)
            {

            }


            //加一天
            calendar.add(Calendar.DAY_OF_MONTH, 1);
            Date nextDate =  calendar.getTime();
            String nextMonth =  sdf_month.format(nextDate);

            //月末或endtime结束
            if (!currMonth.equals(nextMonth) || nextDate.after(endDay)){
                //月结
                long monthCountHot =AdsNode.getCount(AdsNode.NodeType.MONTH, hotRoot,month,"","");
                long monthCountAll =AdsNode.getCount(AdsNode.NodeType.MONTH, allRoot,month,"","");

                if (monthCountHot <= 0 || monthCountAll <= 0){

                    monthJsonArrayCountStat.add(new CountStatInfo<Long>(month,0L));
                    monthJsonArrayhotStat.add(new CountStatInfo<Double>(month,0d));
                }else {

                    monthJsonArrayCountStat.add(new CountStatInfo<Long>(month,monthCountHot));

                    //月的百分比计算有点特殊
                    double percent = (double) monthCountHot / monthCountAll * 100;
                    monthJsonArrayhotStat.add(new CountStatInfo<Double>(month,percent));
                }
            }
            //结束
            if (nextDate.after(endDay)){
                break;
            }
        }

        JSONObject rep = new JSONObject();

        rep.put("code",200);
        rep.put("message","success");
        rep.put("countStatInfo",countStatInfo);
        rep.put("hotStatInfo",hotStatInfo);


        return ResponseEntity.ok(failRequest(rep.toString(),200));


    }


    private JSONObject failRequest(String message,int code )
    {
        JSONObject js  = new JSONObject();
        js.put("message",message);
        js.put("code",code);

        return js;
    }

}
