package cn.edu.zuel.conversation;

import cn.edu.zuel.common.module.Notice;
import cn.edu.zuel.common.module.User;
import cn.fabrice.jfinal.service.BaseService;

public class NoticeService extends BaseService<Notice> {
    public NoticeService() {
        // 第一个参数名需要与all.sql中配置的#namespace("user")保持一致
        // 第二个参数是用户类module，最后一个数据库表名
        super("notice.", Notice.class, "notice");
    }
}
