package cn.edu.zuel.test;

import cn.edu.zuel.common.intercepter.AuthInterceptor;
import cn.fabrice.common.pojo.DataResult;
import cn.fabrice.jfinal.controller.BaseController;
import com.alibaba.fastjson.JSONObject;
import com.jfinal.aop.Clear;
import com.jfinal.core.Path;

@Path("/test")
@Clear(AuthInterceptor.class)
public class TestController extends BaseController {
    TestService testService = new TestService();
    public void getAddressByIp()
    {
        String json = testService.getAddressByIp();
        DataResult.data(json);
        renderJson(json);
    }
}
