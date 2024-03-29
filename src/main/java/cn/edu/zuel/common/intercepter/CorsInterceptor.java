package cn.edu.zuel.common.intercepter;

import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import com.jfinal.core.Controller;

public class CorsInterceptor implements Interceptor {

    @Override
    public void intercept(Invocation invocation) {
        Controller c = invocation.getController();
        c.getResponse().setHeader("Access-Control-Allow-Origin","*");
        //允许跨域的方法
        c.getResponse().setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE");
        //表示允许的额外header
        c.getResponse().setHeader("Access-Control-Allow-Headers","Authorization,access-token, mobile, power-by, token, Accept, Origin, X-Requested-With, Content-Type, Last-Modified,withCredentials,multipart/form-data");
        c.getResponse().setHeader("Access-Control-Max-Age", "3600");
        //必须为true
        c.getResponse().setHeader("Access-Control-Allow-Credentials", "true");
        invocation.invoke();
    }
}
