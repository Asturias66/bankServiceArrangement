package cn.edu.zuel.conversation;

import cn.edu.zuel.common.module.Feedback;
import cn.edu.zuel.common.module.ProductDetail;
import cn.edu.zuel.common.module.User;
import cn.edu.zuel.product.ProductDetailService;
import cn.edu.zuel.user.UserService;
import cn.fabrice.common.constant.BaseConstants;
import cn.fabrice.common.pojo.BaseResult;
import cn.fabrice.common.pojo.DataResult;
import cn.fabrice.jfinal.annotation.Param;
import cn.fabrice.jfinal.annotation.ValidateParam;
import com.jfinal.aop.Inject;
import com.jfinal.core.Controller;
import com.jfinal.core.Path;
import com.jfinal.kit.Kv;
import com.jfinal.plugin.activerecord.Page;

import java.math.BigInteger;
import java.util.List;

@Path("/feedback")
@ValidateParam
public class FeedbackController extends Controller {

    @Inject
    FeedbackService feedbackService;

    @Inject
    UserService userService;

    @Inject
    ProductDetailService productDetailService;

    //新增反馈
    @Param(name = "objectId",required = true)
    @Param(name = "content",required = true)
    @Param(name = "type",required = true)
    public void addFeedback(BigInteger objectId,String content,int type)
    {
        BigInteger userId = BigInteger.valueOf(getAttr(BaseConstants.ACCOUNT_ID));
        Feedback feedback = new Feedback();
        if(type == 0)
        {
            ProductDetail productDetail = productDetailService.getDetailById(objectId);
            if(productDetail == null)
            {
                renderJson(BaseResult.fail("新增反馈信息失败,反馈产品不存在"));
                return;
            }
        }

        if(type == 1)
        {
            User user = userService.getById(objectId);
            if(user == null)
            {
                renderJson(BaseResult.fail("新增反馈信息失败,反馈人员不存在"));
                return;
            }
        }
        feedback.setContent(content);
        feedback.setObjectId(objectId);
        feedback.setType(type);
        feedback.setUserId(userId);
        if(!feedback.save())
        {
            renderJson(BaseResult.fail("新增反馈信息失败"));
            return;
        }
        renderJson(BaseResult.ok("新增成功"));
    }

    //展示所有反馈信息
    @Param(name = "type",required = true)
    public void getOperatorFeedback(BigInteger status,int type)
    {
        if(type == 0)   //配置人员查看用户向配置人员的反馈
        {
            Kv cond = Kv.by("type",type);
            List list = feedbackService.listRecord(cond,"getAllFeedback");
            renderJson(DataResult.data(list));
        }

        if(type == 1) //管理员查看配置人员向管理员反馈
        {
            Kv cond = Kv.by("type",type).set("status",status);
            List list = feedbackService.listRecord(cond,"getAllFeedback");
            renderJson(DataResult.data(list));
        }

        if(type == 2) //配置人员查看自己的反馈记录
        {
            BigInteger userId = BigInteger.valueOf(getAttr(BaseConstants.ACCOUNT_ID));
//            System.out.println(userId);
            Kv cond = Kv.by("type",1).set("status",status).set("userId",userId);
            List list = feedbackService.list(cond,"getAllFeedback");
            renderJson(DataResult.data(list));
        }

        if(type == 3) //配置人员查看管理员的通知与公告
        {
            BigInteger userId = BigInteger.valueOf(getAttr(BaseConstants.ACCOUNT_ID));
//            System.out.println(userId);
            Kv cond = Kv.by("userId",userId).set("status",status);
            List list = feedbackService.list(cond,"getOperatorFeedback");
            renderJson(DataResult.data(list));
        }
    }

    //管理员更改反馈状态
    @Param(name = "id",required = true)
    @Param(name = "status",required = true)
    public void editOperatorFeedbackStatus(BigInteger id,int status)
    {
        BigInteger userId = BigInteger.valueOf(getAttr(BaseConstants.ACCOUNT_ID));
        Kv cond = Kv.by("id",id);
        Feedback feedback = feedbackService.get(cond,"getFeedbackById");
        if(feedback == null)
        {
            renderJson(BaseResult.fail("获取对应反馈信息失败"));
            return;
        }
        feedback.setStatus(status);
        if(!feedback.update())
        {
            renderJson(BaseResult.fail("更新反馈信息状态失败"));
            return;
        }
        if(status == 2)
        {
            Feedback feedback1 = new Feedback();
            feedback1.setType(2);
            feedback1.setObjectId(feedback.getUserId());
            feedback1.setUserId(userId);
            feedback1.setContent("您的反馈\"" + feedback.getContent() + "\"已被系统管理员解决");
            feedback1.save();
        }
        renderJson(BaseResult.ok("更新反馈信息状态成功"));
    }

    //删除自己的反馈
    @Param(name = "id",required = true)
    public void deleteFeedback(BigInteger id)
    {
        Kv cond = Kv.by("id",id);
        Feedback feedback = feedbackService.get(cond,"getFeedbackById");
        if(feedback == null)
        {
            renderJson(BaseResult.fail("获取对应反馈信息失败"));
            return;
        }
        feedback.setIsDeleted(1);
        if(!feedback.update())
        {
            renderJson(BaseResult.fail("删除反馈信息失败"));
            return;
        }
        renderJson(BaseResult.ok("删除反馈信息成功"));
    }

    //查看未读通知数量
    public void getAllNoticeNotReadNum()
    {
        BigInteger userId = BigInteger.valueOf(getAttr(BaseConstants.ACCOUNT_ID));
        Kv cond = Kv.by("userId",userId).set("status",0);
        List list = feedbackService.list(cond,"getOperatorFeedback");
        renderJson(DataResult.data(list.size()));
    }

    //更改所有未读通知为已读
    public void changeNoticeStatus()
    {
        BigInteger userId = BigInteger.valueOf(getAttr(BaseConstants.ACCOUNT_ID));
        Kv cond = Kv.by("userId",userId).set("status",0);
        List<Feedback> list = feedbackService.list(cond,"getOperatorFeedbackTable");
        for(Feedback feedback:list)
        {
            feedback.setStatus(1);
            feedback.update();
        }
        renderJson(BaseResult.ok());
    }
}
