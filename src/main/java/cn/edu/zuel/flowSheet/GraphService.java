package cn.edu.zuel.flowSheet;

import cn.edu.zuel.common.module.Graph;
import cn.edu.zuel.common.module.ProductUser;
import cn.edu.zuel.common.module.base.BaseGraph;
import cn.fabrice.jfinal.service.BaseService;
import com.jfinal.kit.Kv;

import java.math.BigInteger;

/**
 * @Auther: zdp
 * @Date: 2022/2/7 11:29
 * @Description:
 */
public class GraphService extends BaseService<Graph> {
    public GraphService(){
        // 第一个参数名需要与all.sql中配置的#namespace("product")保持一致
        // 第二个参数是用户类module，最后一个数据库表名
        super("graph.", Graph.class, "graph");
    }

    public Graph getById(BigInteger id){
        return get(Kv.by("id",id),"getById");
    }

    public Graph getByProductId(BigInteger productId){
        return get(Kv.by("productId",productId),"getByProductId");
    }
}
