package cn.edu.zuel.product;

import cn.edu.zuel.common.module.ProductDetail;
import cn.fabrice.jfinal.service.BaseService;
import com.jfinal.kit.Kv;
import com.jfinal.plugin.activerecord.Record;


import java.math.BigInteger;
import java.util.List;

public class ProductDetailService extends BaseService<ProductDetail> {
    public ProductDetailService() {
        // 第一个参数名需要与all.sql中配置的#namespace("product")保持一致
        // 第二个参数是用户类module，最后一个数据库表名
        super("productDetail.", ProductDetail.class, "productDetail");
    }

    public List<Record> showProductHavePublished(Kv cond){
        return listRecord(cond,"showProductHavePublished");
    }

    public List<Record> showProductWaitPublishing(Kv cond){
        return listRecord(cond,"showProductWaitPublishing");
    }

    //根据status查看
    public List<Record> getDetailByStatus(BigInteger status,BigInteger teamId) {
        Kv cond = Kv.by("status", status).set("teamId",teamId);
        return listRecord(cond, "getDetailByStatus");
    }

    //根据配置人员ID查看
    public List<ProductDetail> getDetailByUserId(BigInteger userId) {
        Kv cond = Kv.by("userId", userId);
        return list(cond, "getDetailByUserId");
    }

    //根据status和产品所属用户查看
    public List<ProductDetail> getDetailByStatusAndUserId(int status, BigInteger userId) {
        Kv cond = Kv.by("status", status).set("userId",userId);
        return list(cond, "getDetailByStatusAndUserId");
    }

    //根据ID查找产品信息
    public ProductDetail getDetailById(BigInteger id) {
        Kv cond = Kv.by("id", id);
        return get(cond, "getDetailById");
    }

    //根据产品名称模糊查找产品信息
    public List<ProductDetail> getDetailByName(BigInteger userId, String name) {
        Kv cond = Kv.by("userId", userId).set("name",name);
        return list(cond, "getDetailByName");
    }

    public List<ProductDetail> getByName(String name) {
        Kv cond = Kv.by("name",name);
        return list(cond, "getByName");
    }

    //根据ID查找产品视图信息
    public List<Record> getDetailViewById(BigInteger id) {
        Kv cond = Kv.by("id", id);
        return listRecord(cond, "getDetailViewById");
    }

}
