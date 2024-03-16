package cn.edu.zuel.flowSheet;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import javafx.print.Collation;

import java.util.*;

/**
 * @Auther: zdp
 * @Date: 2022/1/23 20:01
 * @Description:
 */
public class DealJson {
    private JSONObject outJson1;
    // 通过解析dealInJson，得到的含有相同开始结点的联系，key是startId，value是连线的编号
    private final Map<String, ArrayList<Integer>> referenceStart = new HashMap<>();
    // 通过解析dealInJson，得到连线的起始结点和跳转条件，key是连线的编号
    // value——数组第一个元素是开始结点；第二个是结束结点；第三个是跳转条件
    private final Map<Integer, ArrayList<String>> referenceLine = new HashMap<>();
    private final ArrayList<JsonNode> last = new ArrayList<>();
    // 逻辑进行的当前判断结点
    private int currentIndex = 1;
    // 当前已经遍历过的结点名称和当前结点下一个结点名称
    private final ArrayList<String> dealedAndNextArray = new ArrayList<>();

    public ArrayList<JsonNode> getLast() {
        return this.last;
    }

    public boolean getStream(String inJson) {
        dealInJson(inJson);

        dealedAndNextArray.add(0, "end");
        JsonNode node0 = new JsonNode();
        node0.setCurrent("0");
        node0.setName("end");
        node0.setWhy("~#");
        node0.setNext(",");
        node0.setNextName(",");
        node0.setIsJumped(0);
        node0.setParameter("[]");
        last.add(node0);

        dealedAndNextArray.add(1, "start");
        while (currentIndex <= referenceStart.size()) {
            JsonNode node = getNode();
            if (node == null) {
                return false;
            }
            last.add(node);
            currentIndex++;
        }
        return true;
    }

    /**
     * 获得当前结点基础信息，包含编号，名字，与之相连下一个结点的编号，跳转条件
     */
    public JsonNode getNode() {
        JsonNode node = new JsonNode();
        String next = "";
        String why = "";
        String nextName = "";
        String startId = dealedAndNextArray.get(currentIndex);
        node.setCurrent(Integer.toString(currentIndex));
        String name = startId.split("_")[0];
        node.setName(name);

        JSONObject nodeDetail = getNodeDetail(startId);
        String properties = nodeDetail.getString("properties");
        Map map = JSON.parseObject(properties, Map.class);
        String values = map.values().toString();
        node.setParameter(values);

        if (name.equals("identifyUser")||name.equals("identifyIdCard")||name.equals("faceDetection")||name.equals("interestCaculation")||name.equals("makeOrder")||name.equals("stockUpdate")){
            node.setIsJumped(1);
        }else {
            node.setIsJumped(0);
        }

        if (!startId.equals("end")) {
            // 与当前结点相连的所有结点
            ArrayList<Integer> nextNodeList = referenceStart.get(startId);
            // 树的末枝都是结束结点
            if (nextNodeList == null) {
                return null;
            } else {
                for (int j = 0; j < nextNodeList.size(); j++) {
                    // 标志当前循环最前沿的结点编号
                    int lastedNode = dealedAndNextArray.size();
                    String nextStart = referenceLine.get(nextNodeList.get(j)).get(1);
                    if (nextStart.equals("end")) {
                        next = next + "," + 0;
                        why = why + "~" + referenceLine.get(nextNodeList.get(j)).get(2);
                        nextName = nextName + "," + "end";
                        continue;
                    }
                    // 当该结点的下一个结点是原本已经出现的结点时
                    int help = dealedAndNextArray.indexOf(nextStart);
                    if (help != -1) {
                        next = next + "," + help;
                    } else {
                        next = next + "," + lastedNode;
                        dealedAndNextArray.add(lastedNode, nextStart); // 该连线的末端
                    }
                    why = why + "~" + referenceLine.get(nextNodeList.get(j)).get(2);
                    nextName = nextName + "," + nextStart.split("_")[0];
                }
            }
        }
        node.setNext(next);
        node.setWhy(why);
        node.setNextName(nextName);
        return node;
    }


    /**
     * 处理json数据，将连线的起始结点抽出来进行存储
     *
     * @param inJson 原始json数据
     */
    public void dealInJson(String inJson) {
        // 将传来的json数据第一格式化
        outJson1 = JSONObject.parseObject(inJson);
        // 获得连接线处的json数据
        String linkListStr = outJson1.getString("edges");
        // 因为连线处的json外层是数组
        JSONArray linkJson = JSON.parseArray(linkListStr);



        // 连线编号
        int index = 0;
        // 使用迭代器进行遍历
        Iterator<Object> iterator = linkJson.iterator();
        while (iterator.hasNext()) {
            JSONObject lineJson = (JSONObject) iterator.next();
            // 根据key获得连线的起始结点的名称
            String startId = lineJson.getString("sourceNodeId");
            String endId = lineJson.getString("targetNodeId");

            // 将连线中起点id一样的编号，作为value存入map中，防止键值对重复
            ArrayList<Integer> startIndex = new ArrayList<>();
            if (referenceStart.get(startId) != null) {
                startIndex = referenceStart.get(startId);
            }
            startIndex.add(index);
            referenceStart.put(startId,startIndex);

            // 将结点的跳转条件写明白，如果没有则是“#”
            String desc = "#";
            if (lineJson.getString("text") != null) {
                desc = JSONObject.parseObject(lineJson.getString("text")).getString("value");
            }
            ArrayList<String> lineId = new ArrayList<>();
            lineId.add(startId);
            lineId.add(endId);
            lineId.add(desc);
            referenceLine.put(index, lineId);// 序号在前面
            index++;
        }
    }

    public JSONObject getNodeDetail(String key){
        // 获得连接线处的json数据
        String linkListStr = outJson1.getString("nodes");
        // 因为连线处的json外层是数组
        JSONArray linkJson = JSON.parseArray(linkListStr);

        Iterator<Object> iterator = linkJson.iterator();
        while (iterator.hasNext()) {
            JSONObject lineJson = (JSONObject) iterator.next();
            if (lineJson.getString("id").equals(key)){
                return lineJson;
            }
        }
        return null;
    }
}
