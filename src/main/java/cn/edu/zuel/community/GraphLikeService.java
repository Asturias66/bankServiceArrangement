package cn.edu.zuel.community;

import cn.edu.zuel.common.module.GraphLike;
import cn.edu.zuel.common.module.ProductDetail;
import cn.fabrice.jfinal.service.BaseService;
import com.jfinal.kit.Kv;

import java.math.BigInteger;

public class GraphLikeService extends BaseService<GraphLike> {

    public GraphLikeService(){
      // 第一个参数名需要与all.sql中配置的#namespace("graph_like")保持一致
      // 第二个参数是用户类module，最后一个数据库表名
      super("graphLike.",GraphLike.class,"graphLike");
    }

  public GraphLike getGraph(BigInteger userId, BigInteger graphId, Integer type, Integer help) {
        Kv cond = Kv.by("userId",userId).set("graphId",graphId).set("type",type).set("help",help);
        return get(cond,"getCollectOrLike");
  }
}
