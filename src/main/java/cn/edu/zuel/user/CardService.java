package cn.edu.zuel.user;

import cn.edu.zuel.common.module.Card;
import cn.edu.zuel.common.module.User;
import cn.fabrice.jfinal.service.BaseService;

public class CardService extends BaseService<Card> {
    public CardService() {
        // 第一个参数名需要与all.sql中配置的#namespace("user")保持一致
        // 第二个参数是用户类module，最后一个数据库表名
        super("card.", Card.class, "card");
    }
}
