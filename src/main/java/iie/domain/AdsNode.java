package iie.domain;

import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AdsNode {

    public enum NodeType{
        ROOT,
        MONTH,
        DAY,
        TYPE
    }

    private NodeType TYPE;
    private String childID;

    private long count;
    private Map<String,AdsNode> childList = new HashMap<>();


    public AdsNode ( NodeType TYPE,String childID,long count, Map<String,AdsNode> childList)
    {
        this.TYPE=TYPE;
        this.childID=childID;
        this.count=count;
        this.childList=childList;
    }

    public void setCount(long count) {
        this.count = count;
    }

    public void setChildID(String childID) {
        this.childID = childID;
    }

    public void setChildList(Map<String, AdsNode> childList) {
        this.childList = childList;
    }

    public static void setRootNode(AdsNode rootNode) {
        AdsNode.rootNode = rootNode;
    }

    public void setTYPE(NodeType TYPE) {
        this.TYPE = TYPE;
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




    public static AdsNode rootNode = new AdsNode(NodeType.ROOT,"root",0,null);

    public static long Gount(String month,String day ,String type)
    {
        //主要这里查询，外部接口都在这里查询

        return 0;

    }

    public static void setCount (String month,String day ,String type,AdsNode node)
    {
        if (rootNode.getChildAtID(month) == null){

        }
    }




}
