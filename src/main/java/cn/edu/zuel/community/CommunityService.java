package cn.edu.zuel.community;

import cn.edu.zuel.common.module.Community;
import cn.edu.zuel.common.module.Feedback;
import cn.fabrice.jfinal.service.BaseService;
import com.jfinal.kit.Kv;
import com.jfinal.plugin.activerecord.Record;

import java.math.BigInteger;
import java.util.List;

public class CommunityService extends BaseService<Community> {
    public CommunityService() {
        // 第一个参数名需要与all.sql中配置的#namespace("user")保持一致
        // 第二个参数是用户类module，最后一个数据库表名
        super("community.", Community.class, "community");
    }

    public List<Record> getAll(String name,BigInteger id,Integer praise) {
        Kv cond = Kv.by("name",name).set("id",id).set("praise",praise);
        return listRecord(cond,"getAll");
    }

    public Community getById(BigInteger id) {
        Kv cond = Kv.by("id",id);
        return get(cond , "getById");
    }

    public Community getByJsonChart(String jsonChart) {
        Kv cond = Kv.by("jsonChart",jsonChart);
        return get(cond , "getByJsonChart");
    }


    public List<Community> showPushMould (BigInteger userId){
        Kv cond = Kv.by("userId", userId);
        return list(cond, "showPushMould");
    }

    public List<Record> showCollectMould(BigInteger userId) {
        Kv cond = Kv.by("userId", userId);
        return listRecord(cond, "showCollectMould");
    }
}
