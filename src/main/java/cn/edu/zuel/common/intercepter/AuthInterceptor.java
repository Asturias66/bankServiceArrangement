package cn.edu.zuel.common.intercepter;

import cn.edu.zuel.common.module.Session;
import cn.edu.zuel.user.SessionService;
import cn.fabrice.common.constant.BaseConstants;
import cn.fabrice.common.pojo.BaseResult;
import com.jfinal.aop.Aop;
import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import com.jfinal.core.Controller;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.ehcache.CacheKit;


public class AuthInterceptor implements Interceptor {

    @Override
    public void intercept(Invocation invocation) {
        Controller controller = invocation.getController();
        if (StrKit.notBlank(controller.getHeader(BaseConstants.TOKEN_NAME))) {
            String token = controller.getHeader(BaseConstants.TOKEN_NAME);
            System.out.println("查询到的token:"+token);
            SessionService sessionService = Aop.get(SessionService.class);
            //获取session实体,看一下JFinal中CacheKit的用法，缓存里面没有读到就从数据库里面读取
            Session userSession = CacheKit.get(BaseConstants.ACCOUNT_CACHE_NAME, token,
                    () -> (Session)((sessionService.getSessionByToken(token)).getData()));

            if (userSession == null) {
                System.out.println("查不到token信息");
                controller.renderJson(BaseResult.illegal());
                return;
            }
            // 验证token是否过期
            if(userSession.isExpired()>0){
                sessionService.deleteByToken(token);
                controller.renderJson(BaseResult.fail("token已经失效"));
                return;
            }
            controller.setAttr(BaseConstants.ACCOUNT, userSession);
            controller.setAttr(BaseConstants.ACCOUNT_ID, userSession.getUuid().longValue());
            invocation.invoke();
            System.out.println("token信息查询成功");
            return;
        }
        System.out.println("请求头中access_token为空");
        controller.renderJson(BaseResult.illegal());
    }

}
