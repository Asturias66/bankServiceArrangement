package cn.edu.zuel.product;

import cn.edu.zuel.common.module.ProductDetail;
import cn.edu.zuel.common.module.ProductUser;
import cn.fabrice.jfinal.service.BaseService;

public class ProductUserService extends BaseService<ProductUser> {
    public ProductUserService(){
        // 第一个参数名需要与all.sql中配置的#namespace("product")保持一致
        // 第二个参数是用户类module，最后一个数据库表名
        super("productUser.", ProductUser.class, "product_user");
    }
}
