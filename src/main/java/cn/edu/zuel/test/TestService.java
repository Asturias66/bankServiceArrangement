package cn.edu.zuel.test;

import cn.fabrice.common.pojo.BaseResult;
import cn.fabrice.common.pojo.DataResult;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jfinal.kit.HttpKit;

import java.util.HashMap;
import java.util.Map;


public class TestService {
    String requestUrl="http://pv.sohu.com/cityjson";

    Map<String, String> maps2=new HashMap<String,String>();
    public TestService()
    {
        maps2.put("Content-Type","application/json;charset=utf-8");
    }

    public String getAddressByIp()
    {
        String ip = "117.190.241.64";
        JSONObject params = new JSONObject();
        params.put("ip",ip);

        String data = HttpKit.post(requestUrl,null,maps2);
        JSONObject json= JSON.parseObject(data);
        System.out.println(data);
        return data;
    }
}
