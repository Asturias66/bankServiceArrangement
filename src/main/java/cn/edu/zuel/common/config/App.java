package cn.edu.zuel.common.config;

import cn.edu.zuel.common.intercepter.AuthInterceptor;
import cn.edu.zuel.common.module._MappingKit;
import cn.fabrice.common.constant.BaseConstants;
import cn.fabrice.jfinal.ext.cros.interceptor.CrossInterceptor;
import cn.fabrice.jfinal.interceptor.ParaValidateInterceptor;
import cn.fabrice.jfinal.plugin.ValidationPlugin;
import com.alibaba.druid.filter.stat.StatFilter;
import com.alibaba.druid.wall.WallFilter;
import com.jfinal.config.*;
import com.jfinal.json.FastJsonFactory;
import com.jfinal.kit.Prop;
import com.jfinal.kit.PropKit;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.ActiveRecordPlugin;
import com.jfinal.plugin.druid.DruidPlugin;
import com.jfinal.plugin.ehcache.EhCachePlugin;
import com.jfinal.server.undertow.UndertowServer;
import com.jfinal.template.Engine;

public class App extends JFinalConfig {
    @Override
    public void configConstant(Constants constants) {
        PropKit.use("base_config.properties");
        //设置开发模式，与base_config.properties中名字保持一致
        constants.setDevMode(PropKit.getBoolean("devMode", true));
        //设置日志，Jfinal4.几版本日志的设置
        constants.setToSlf4jLogFactory();
        //允许AOP注入，允许后文使用“@Inject”
        constants.setInjectDependency(true);
        //如果要上传文件等，也要在此配置
//        constants.setBaseUploadPath("file/upfile");
//        constants.setBaseDownloadPath("file/downfile");
        constants.setBaseUploadPath("src/main/resources/file/upfile");
        constants.setBaseDownloadPath("src/main/resources/file/upfile");
//        constants.setBaseUploadPath("/root/back_code/bankService/config/file/upfile");
//        constants.setBaseDownloadPath("/root/back_code/bankService/config/file/downfile");
        // 设置文件的最大，可G上网查，20M
        constants.setMaxPostSize(20*1024*1024);
        constants.setJsonFactory(new FastJsonFactory());
        //驼峰形式传参给前端

        // 使用BaseResultConstants需要做的工作
//        UserConstants.Result.init();
    }

    @Override
    public void configRoute(Routes routes) {
        // 扫描基础的包
        routes.scan("cn.edu.zuel.");
    }

    @Override
    public void configEngine(Engine engine) {
        // 只有在使用JFinal中原生的html使用enjoy引擎时需要配置
    }

    @Override
    public void configPlugin(Plugins plugins) {
        // 读取数据库配置文件
        Prop prop = PropKit.use("db_config.properties");
        // 使用druid数据库连接池进行操作
        DruidPlugin druidPlugin = new DruidPlugin(prop.get("database_url").trim(),
                prop.get("database_user").trim(), prop.get("database_password").trim());
        // 加强数据库安全
        WallFilter wallFilter = new WallFilter();
        wallFilter.setDbType("mysql");
        druidPlugin.addFilter(wallFilter);
        // 添加 StatFilter 才会有统计数据
        druidPlugin.addFilter(new StatFilter());

        druidPlugin.setMaxActive(20);
        druidPlugin.setMinIdle(5);
        druidPlugin.setInitialSize(5);
        druidPlugin.setConnectionInitSql("set names utf8mb4");
        druidPlugin.setValidationQuery("select 1 from dual");
        plugins.add(druidPlugin);
        // 配置数据库活动记录插件
        ActiveRecordPlugin arp = new ActiveRecordPlugin(druidPlugin);
        Engine engine = arp.getEngine();
        // 上面的代码获取到了用于 sql 管理功能的 Engine 对象，接着就可以开始配置了
        // 下面因为使用sql文件，必须配置
        engine.setToClassPathSourceFactory();
        engine.addSharedMethod(StrKit.class);
        arp.setShowSql(PropKit.getBoolean("dev", true));
        //设置sql文件存储的基础路径，此段代码表示设置为classpath目录
        arp.setBaseSqlTemplatePath(null);
        //所有模块sql
        arp.addSqlTemplate("sql/all.sql");
        // 对应关系映射（需等待运行Generator语句生成后替换）
        _MappingKit.mapping(arp);
        plugins.add(arp);

        //添加规则校验插件
        plugins.add(new ValidationPlugin());
        //增加缓存插件
        plugins.add(new EhCachePlugin());
    }

    @Override
    public void configInterceptor(Interceptors interceptors) {
        // 首先添加跨域拦截器
        interceptors.add(new CrossInterceptor(BaseConstants.TOKEN_NAME,true));
        // 然后添加全局拦截器
        interceptors.add(new AuthInterceptor());
        // 最后添加参数校验拦截器
        interceptors.add(new ParaValidateInterceptor());
    }

    @Override
    public void configHandler(Handlers handlers) {

    }

    @Override
    public void onStart() {
        System.out.println("app starting...");
    }

    @Override
    public void onStop() {
        System.out.println("app stopping");
    }

    public static void main(String[] args) {
        // 可以自己设端口，如果没有设置，会自动找到undertow.txt
        // 在undertow.txt中可以改undertow.host=0.0.0.0 undertow.port=20212端口号
        UndertowServer.start(App.class);
    }
}
