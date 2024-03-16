package cn.edu.zuel.conversation;

import cn.edu.zuel.common.module.Feedback;
import cn.edu.zuel.common.module.Notice;
import cn.fabrice.common.pojo.BaseResult;
import cn.fabrice.common.pojo.DataResult;
import cn.fabrice.jfinal.annotation.Param;
import cn.fabrice.jfinal.annotation.ValidateParam;
import com.jfinal.aop.Inject;
import com.jfinal.core.Controller;
import com.jfinal.core.Path;
import com.jfinal.kit.Kv;
import com.jfinal.plugin.activerecord.Page;

import java.util.List;

@Path("/notice")
@ValidateParam
public class NoticeController extends Controller {

    @Inject
    NoticeService noticeService;

    //增加公告
    @Param(name = "content",required = true)
    public void addNotice(String content)
    {
        Notice notice = new Notice();
        notice.setContent(content);
        if(!notice.save())
        {
            renderJson(BaseResult.fail("增加公告失败"));
            return;
        }
        renderJson(BaseResult.ok("增加公告成功"));

    }

    //查看所有历史公告
    public void getAllNotice()
    {
        List<Notice> list = noticeService.list("getAllNotice");
        if (list.isEmpty()) {
            renderJson(BaseResult.fail("当前公告栏为空"));
            return;
        } else {
            renderJson(DataResult.data(list));
            return;
        }
    }
}
