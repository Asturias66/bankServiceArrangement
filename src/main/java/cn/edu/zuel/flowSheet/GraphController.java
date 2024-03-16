package cn.edu.zuel.flowSheet;

import cn.edu.zuel.common.intercepter.AuthInterceptor;
import cn.edu.zuel.common.module.Feedback;
import cn.edu.zuel.common.module.Graph;
import cn.edu.zuel.common.module.ProductDetail;
import cn.edu.zuel.product.ProductDetailService;
import cn.fabrice.common.constant.BaseConstants;
import cn.fabrice.common.pojo.BaseResult;
import cn.fabrice.common.pojo.DataResult;
import cn.fabrice.jfinal.annotation.Param;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jfinal.aop.Clear;
import com.jfinal.aop.Inject;
import com.jfinal.core.Controller;
import com.jfinal.core.Path;
import com.jfinal.core.paragetter.Para;
import com.jfinal.json.Json;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Auther: zdp
 * @Date: 2022/2/7 11:30
 * @Description:
 */
@Path("/graph")
public class GraphController extends Controller {
    @Inject
    GraphService graphService;
    @Inject
    ProductDetailService productDetailService;

    /**
     * 新增或更新产品流程图
     * productId    产品主键
     * jsonChart    流程图json格式
     */
    @Param(name = "productId", required = true)
    @Param(name = "jsonChart", required = true)
    @Param(name = "img", required = true)
    public void addOrUpdateGraph(@Para("") Graph graph) {
        BigInteger userId = BigInteger.valueOf(getAttr(BaseConstants.ACCOUNT_ID));
        graph.setUserId(userId);
        Graph graph1 = graphService.getByProductId(graph.getProductId());
        if(graph.getJsonChart()!=null){
            DealJson dealJson = new DealJson();
            if (dealJson.getStream(graph.getJsonChart())) {
                ArrayList<JsonNode> last = dealJson.getLast();
                graph.setDealJson(JSON.toJSONString(last));
            }else {
                renderJson(BaseResult.fail("流程图操作不符合规范（如有结点脱离单独存在）"));
                return;
            }
        }
        if (graph1 == null) {
            renderJson(graph.save() ? BaseResult.ok("创建成功") : BaseResult.res(-1, "创建失败"));
        } else {
            graph.setId(graph1.getId());
            renderJson(graph.update() ? BaseResult.ok("更新成功") : BaseResult.res(-2, "更新失败"));
        }

    }

    /**
     * 根据当前流程位置和跳转条件得下一步流程
     * productId    产品主键
     * current      当前流程图的位置，即current
     * reason       跳转条件
     */
    @Param(name = "productId", required = true)
    @Param(name = "current", required = true)
    public void getNext(BigInteger productId,int current) {
        Graph graph = graphService.getByProductId(productId);
        List<JsonNode> flowSheet = JSONObject.parseArray(graph.getDealJson(), JsonNode.class);

        JsonNode node = flowSheet.get(current);
        renderJson(DataResult.data(node));

    }

    /**
     * 根据产品获得该产品对应的流程图
     * productId    产品主键
     */
    @Param(name = "productId", required = true)
    public void showByProductId(@Para("") Graph graph) {
        renderJson(DataResult.data(graphService.getByProductId(graph.getProductId())));
    }

    @Param(name = "products",required = true)
    public void test(String products){
        Map<String,String> result = new HashMap<>();
        ArrayList<String> results = new ArrayList<>();
        String[] array = products.split(",");
        for (int i=0;i<array.length;i++){
            Graph graph = graphService.getByProductId(BigInteger.valueOf(Integer.parseInt(array[i])));
            ProductDetail productDetail = productDetailService.getDetailById(BigInteger.valueOf(Integer.parseInt(array[i])));
            List<JsonNode> flowSheet = JSONObject.parseArray(graph.getDealJson(), JsonNode.class);
            int j=-1;
            for (j=0;j<flowSheet.size();j++){
                if (flowSheet.get(j).getName().equals("makeOrder")){
                    break;
                }
            }
            int flag=-1;
            if (j==flowSheet.size()){
                results.add("该产品流程图不存在必要的生成订单接口");
                flag=results.size();
            }else {
                JsonNode node = flowSheet.get(j);
                if (flowSheet.get(j-1).getName().equals("interestCaculation")){
                    if (node.getNextName().equals(",stockLock")){
                        node = flowSheet.get(++j);
                        if (node.getNextName().equals(",payMoney")){
                            node = flowSheet.get(++j);
                            if (!node.getNextName().contains("stockUpdate")){
                                results.add("该产品流程图付款接口后没有进行库存更新操作");
                                flag=results.size();
                            }
                            if (!node.getNextName().contains("stockRelease")){
                                results.add("该产品流程图生付款接口后没有进行库存释放操作");
                                flag=results.size();
                            }
                        }else {
                            results.add("该产品流程图生成订单接口后没有进行付款操作");
                            flag=results.size();
                        }
                    }else {
                        results.add("该产品流程图生成订单接口后没有进行库存锁定操作");
                        flag=results.size();
                    }
                }else {
                    results.add("该产品流程图不存在必要的利息计算接口");
                    flag=results.size();
                }
            }
            if (flag!=-1){
                productDetail.setStatus(0);
                if (productDetail.update()){
                    Feedback feedback = new Feedback();
                    feedback.setType(2);
                    feedback.setObjectId(productDetail.getUserId());
                    feedback.setUserId(BigInteger.valueOf(getAttr(BaseConstants.ACCOUNT_ID)));
                    feedback.setContent("您提交的产品" + productDetail.getName() + results.get(flag-1));
                    feedback.save();
                }
            }
        }
        renderJson(DataResult.data(results));
    }
}
