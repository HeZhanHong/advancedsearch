package iie.controller;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import iie.Utils.CheckUtil;
import iie.domain.*;
import iie.service.EsClientService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

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
        List<String> newsTypeList = new ArrayList<>();
        AdsNode hotRoot = new AdsNode(AdsNode.NodeType.ROOT,"hotRoot",0);
        AdsNode allRoot = new AdsNode(AdsNode.NodeType.ROOT,"allRoot",0);
        boolean aggs = false;
        try {
            aggs = esClientService.parse2(hotRoot,search,null);
            if (aggs == false){
                //聚合数据为空
                return ResponseEntity.ok(failRequest("无聚合数据",200));
            }
            esClientService.parse2(allRoot,search_all,newsTypeList);
        } catch (ParseException e) {
            LOG.error(e.getMessage());
            e.printStackTrace();
            return ResponseEntity.ok().body(failRequest("解析数据失败 ：" + e.getMessage(),failCode));
        }

        //构建返回体
        JSONObject repBody = esClientService.RepBodyBuilder(newsTypeList);
        //返回体输入数据
        try {
            SetValueOfBody(repBody,formData,hotRoot,allRoot,newsTypeList);
        } catch (Exception e) {
            LOG.error(e.getMessage());
            e.printStackTrace();
            return ResponseEntity.ok().body(failRequest("返回体生成错误 ：" + e.getMessage(),failCode));
        }


        repBody.put("code",200);
        repBody.put("message","success");

        return ResponseEntity.ok(repBody);


    }

    private void SetValueOfBody (JSONObject repJsonObj,SearchFormData formData,AdsNode hotRoot,AdsNode allRoot,List<String> newsTypeList )
    {

        JSONArray countStatInfo = repJsonObj.getJSONObject("countStatInfo").getJSONArray("statistic");
        JSONArray dayJsonArrayCountStat =   countStatInfo.getJSONArray(0);
        JSONArray monthJsonArrayCountStat =   countStatInfo.getJSONArray(1);

        JSONArray hotStatInfo  = repJsonObj.getJSONObject("hotStatInfo").getJSONArray("statistic");
        JSONArray dayJsonArrayhotStat =   hotStatInfo.getJSONArray(0);
        JSONArray monthJsonArrayhotStat =   hotStatInfo.getJSONArray(1);

        JSONArray  typeStatInfo =  repJsonObj.getJSONObject("typeStatInfo").getJSONArray("statistic");
        TypeStatInfo dayJSONObjectTypeStat = (TypeStatInfo)typeStatInfo.get(0);
        TypeStatInfo monthJSONObjectTypeStat = (TypeStatInfo)typeStatInfo.get(1);


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
        Calendar calendar =  Calendar.getInstance();
        calendar.setTime(startDay);
        String currMonth = "";
        while (true){
            Date Date= calendar.getTime();
            String DateStr = sdf.format(Date);
            String month =   sdf_month.format(Date);
            String day =   sdf_day.format(Date);
            currMonth = month;


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
            //所有类型
            for (int t = 0; t < newsTypeList.size(); t++)
            {
                String newsType = newsTypeList.get(t);
                long typeCountHot =  AdsNode.getCount(AdsNode.NodeType.DAY, hotRoot,month,day,newsType);
                long typeCountAll =  AdsNode.getCount(AdsNode.NodeType.DAY, allRoot,month,day,newsType);
                if (typeCountHot <= 0 || typeCountAll <= 0){
                    dayJSONObjectTypeStat.setSeriesValue(newsType,0d);
                }else {

                    double percent = (double) typeCountHot / typeCountAll * 100;
                    dayJSONObjectTypeStat.setSeriesValue(newsType,percent);
                }
            }


            //下一天
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
                    //
                    double percent = (double) monthCountHot / monthCountAll * 100;
                    monthJsonArrayhotStat.add(new CountStatInfo<Double>(month,percent));
                }



                monthJSONObjectTypeStat.getxAxis().add(month);
                //所有类型
                for (int t = 0; t < newsTypeList.size(); t++)
                {
                    String newsType = newsTypeList.get(t);
                    long typeCountHot =  AdsNode.getCount(AdsNode.NodeType.MONTH, hotRoot,month,day,newsType);
                    long typeCountAll =  AdsNode.getCount(AdsNode.NodeType.MONTH, allRoot,month,day,newsType);
                    if (typeCountHot <= 0 || typeCountAll <= 0){
                        monthJSONObjectTypeStat.setSeriesValue(newsType,0d);
                    }else {

                        double percent = (double) typeCountHot / typeCountAll * 100;
                        monthJSONObjectTypeStat.setSeriesValue(newsType,percent);
                    }
                }


            }
            //结束
            if (nextDate.after(endDay)){
                break;
            }
        }
    }



    private JSONObject failRequest(String message,int code )
    {
        JSONObject js  = new JSONObject();
        js.put("message",message);
        js.put("code",code);

        return js;
    }

}
