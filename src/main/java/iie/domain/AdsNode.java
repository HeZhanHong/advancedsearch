package iie.domain;

import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AdsNode {

    public enum NodeType{
        ROOT,   //根
        MONTH,  //年+月
        DAY,    //日
        TYPE   //类型
    }

    private NodeType TYPE;
    private String nodeID;

    private long count;

    private Map<String,AdsNode> childList = new HashMap<>();


    public AdsNode ( NodeType TYPE,String nodeID,long count)
    {
        this.TYPE=TYPE;
        this.nodeID=nodeID;
        this.count=count;
    }

    public void setChildList(Map<String, AdsNode> childList) {
        this.childList = childList;
    }

    public AdsNode getChildAtID (String childID)
    {
        if (childList.containsKey(childID)){
            return childList.get(childID);
        }
        return null;
    }

    public void putChild (String childID,AdsNode node)
    {
        childList.put(childID,node);
    }

    public long getCount ()
    {
        Long _count = 0L;
        if(TYPE == (NodeType.TYPE)){
            return this.count;
        }else {
            for (String childID:   childList.keySet())
            {
                _count = _count + childList.get(childID).getCount();
            }
            return _count;
        }
    }





    public static long getCount(NodeType nodeType , AdsNode rootNode,  String month,String day ,String type)
    {
        long count = 0;
        //主要这里查询，外部接口都在这里查询
        if (nodeType == NodeType.MONTH){
            if (rootNode.getChildAtID(month) != null){
                AdsNode mmNode = rootNode.getChildAtID(month);
                count=mmNode.getCount();
            }
        }else if (nodeType == NodeType.DAY){
            if (rootNode.getChildAtID(month) != null){
                AdsNode mmNode = rootNode.getChildAtID(month);
                if (mmNode.getChildAtID(day) != null){
                    AdsNode dayNode = mmNode.getChildAtID(day);
                    count=dayNode.getCount();
                }
            }
        }else if (nodeType == NodeType.TYPE){
            if (rootNode.getChildAtID(month) != null){
                AdsNode mmNode = rootNode.getChildAtID(month);
                if (mmNode.getChildAtID(day) != null){
                    AdsNode dayNode = mmNode.getChildAtID(day);
                    if (dayNode.getChildAtID(type) != null){
                        AdsNode typeNode =  dayNode.getChildAtID(type);
                        count=typeNode.getCount();
                    }
                }
            }
        }

        return count;

    }

    public static void setCount (AdsNode rootNode,  String month,String day ,String type,long count)
    {
        if (rootNode.getChildAtID(month) == null){
            rootNode.putChild(month,new AdsNode(NodeType.MONTH,month,0));
        }
        AdsNode mmNode = rootNode.getChildAtID(month);
        if (mmNode.getChildAtID(day) == null){
            mmNode.putChild(day,new AdsNode(NodeType.DAY,day,0));
        }
        AdsNode dayNode = mmNode.getChildAtID(day);
        if (dayNode.getChildAtID(type) == null){
            dayNode.putChild(type,new AdsNode(NodeType.TYPE,type,count));
        }
    }




}
