package cn.edu.zuel.product;

import cn.edu.zuel.common.intercepter.AuthInterceptor;
import cn.edu.zuel.common.module.*;
import cn.edu.zuel.conversation.FeedbackService;
import cn.edu.zuel.flowSheet.GraphService;
import cn.edu.zuel.kit.kit;
import cn.edu.zuel.operator.OperatorInformationService;
import cn.edu.zuel.user.*;
import cn.fabrice.common.constant.BaseConstants;
import cn.fabrice.common.pojo.BaseResult;
import cn.fabrice.common.pojo.DataResult;
import cn.fabrice.jfinal.annotation.Param;
import cn.fabrice.jfinal.annotation.ValidateParam;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.jfinal.aop.Clear;
import com.jfinal.aop.Inject;
import com.jfinal.core.Controller;
import com.jfinal.core.Path;
import com.jfinal.core.paragetter.Para;
import com.jfinal.kit.HashKit;
import com.jfinal.kit.Kv;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Record;


import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;

@Path("/product")
@ValidateParam
public class ProductDetailController extends Controller {
    @Inject
    ProductDetailService productDetailService;
    @Inject
    GraphService graphService;
    @Inject
    ProductUserService productUserService;
    @Inject
    OperatorInformationService operatorInformationService;
    @Inject
    UserService userService;
    @Inject
    FeedbackService feedbackService;
    @Inject
    UserInformationService userInformationService;
    @Inject
    CardService cardService;
    @Inject
    TeamMemberService teamMemberService;
    @Inject
    TeamService teamService;

    /**
     * 新增产品
     *
     * @param detail 前端传参
     */
    @Param(name = "name", required = true)
    @Param(name = "introduction", required = true)
    @Param(name = "term", required = true)
    @Param(name = "annualRate", required = true)
    @Param(name = "initialMoney", required = true)
    @Param(name = "increaseMoney", required = true)
    @Param(name = "personalLimit", required = true)
    @Param(name = "dailyLimit", required = true)
    @Param(name = "riskId", required = true)
    @Param(name = "interestWay", required = true)
    @Param(name = "amount", required = true)
    @Param(name = "region",required = true)
    @Param(name = "type",required = true)
    public void addProduct(@Para("") ProductDetail detail) {
        if (!productDetailService.getByName(detail.getName()).isEmpty()){
            renderJson(BaseResult.res(-3,"产品中已存在该名称产品"));
            return;
        }
        BigInteger userId = BigInteger.valueOf(getAttr(BaseConstants.ACCOUNT_ID));
        detail.setUserId(userId);
        //可购量 = 库存
        detail.setAvailable(detail.getAmount());
        //销量 = 0
        detail.setSale(0);
        int operatorCoverRandom = (int)(441+Math.random()*(445-441+1));
        //获取用户封面随机数
        int userCoverRandom = (int)(446+Math.random()*(448-446+1));
        detail.setOperatorCover(BigInteger.valueOf(operatorCoverRandom));
        detail.setUserCover(BigInteger.valueOf(userCoverRandom));
        renderJson(detail.save() ? BaseResult.ok("新增成功") : BaseResult.res(-1, "新增失败,数据库插入失败"));
        //加密编号
        detail.setNumber(Integer.toString(detail.getId().intValue()+1000));
        //获取配置人员封面随机数
        renderJson(detail.update() ? BaseResult.ok() : BaseResult.res(-2, "更新失败"));
    }


    /**
     * 修改产品基础属性
     */
    @Param(name = "id", required = true)
    @Param(name = "status", required = true)
    @Param(name = "number", required = true)
    @Param(name = "name", required = true)
    @Param(name = "introduction", required = true)
    @Param(name = "term", required = true)
    @Param(name = "annualRate", required = true)
    @Param(name = "initialMoney", required = true)
    @Param(name = "increaseMoney", required = true)
    @Param(name = "personalLimit", required = true)
    @Param(name = "dailyLimit", required = true)
    @Param(name = "riskId", required = true)
    @Param(name = "interestWay", required = true)
    @Param(name = "amount", required = true)
    @Param(name = "available", required = true)
    @Param(name = "sale", required = true)
    @Param(name = "publishDay", required = true)
    @Param(name = "region",required = true)
    @Param(name = "type",required = true)
    public void updateProduct(@Para("") ProductDetail detail) {
        detail.setUserId(BigInteger.valueOf(getAttr(BaseConstants.ACCOUNT_ID)));
        ProductDetail productDetail = productDetailService.getDetailById(detail.getId());
        if (!detail.getName().equals(productDetail.getName())){
            if (!productDetailService.getDetailByName(null,detail.getName()).isEmpty()){
                renderJson(BaseResult.res(-1,"模板市场已存在该名称模板"));
                return;
            }
        }
        renderJson(detail.update() ? BaseResult.ok("更新成功") : BaseResult.res(-2, "更新失败"));
    }


    /*
    * 修改产品单个基础属性
    * */
    @Param(name = "id",required = true)
    @Param(name = "key",required = true)
    @Param(name = "status")
    @Param(name = "number")
    @Param(name = "name")
    @Param(name = "introduction")
    @Param(name = "term")
    @Param(name = "annualRate")
    @Param(name = "initialMoney")
    @Param(name = "increaseMoney")
    @Param(name = "personalLimit")
    @Param(name = "dailyLimit")
    @Param(name = "riskId")
    @Param(name = "interestWay")
    @Param(name = "amount")
    @Param(name = "available")
    @Param(name = "sale")
    @Param(name = "publishDay")
    @Param(name = "region")
    @Param(name = "type")
    public void updateSingle(String key,@Para("")ProductDetail detail){
        ProductDetail productDetail = productDetailService.getDetailById(detail.getId());
        if (productDetail==null){
            renderJson(BaseResult.fail("不存在该产品"));
            return;
        }
        productDetail.set(key,detail.get(key));
        renderJson(productDetail.update()?BaseResult.ok():BaseResult.fail());
    }

    /**
     * 修改产品状态status
     * 0-未发布;1-已提交管理员审核;2-已发布
     */
//    @Clear(AuthInterceptor.class)
    @Param(name = "id", required = true)
    @Param(name = "publishDay")
    @Param(name = "status", required = true)
    public void alterProductStatus(@Para("") ProductDetail detail) {
        BigInteger userId = BigInteger.valueOf(getAttr(BaseConstants.ACCOUNT_ID));
        ProductDetail detail1 = productDetailService.getDetailById(detail.getId());
        if (detail1 == null) {
            renderJson(BaseResult.res(-1, "没有该产品"));
            return;
        }
        if(detail.getStatus()==1){
            if (graphService.getByProductId(detail.getId()) == null || graphService.getByProductId(detail.getId()).getJsonChart()==null){
                renderJson(BaseResult.res(-3, "上传产品，作品json不能为空"));
                return;
            }
        }
        detail1.setStatus(detail.getStatus());
        if (detail.getStatus()==2){
            detail1.setPublishDay(detail.getPublishDay());
            if (detail1.update()){
                Feedback feedback = new Feedback();
                feedback.setType(2);
                feedback.setObjectId(detail1.getUserId());
                feedback.setUserId(userId);
                feedback.setContent("您提交的产品" + detail1.getName() + "已通过管理员审核上线");
                feedback.save();
                renderJson(BaseResult.ok());
            }else {
                renderJson(BaseResult.res(-2, "更新失败"));

            }
        }else {
            renderJson(detail1.update()?BaseResult.ok("更新成功"):BaseResult.res(-2, "更新失败"));
        }
    }

    /**
     * 修改产品负责人
     */
    @Param(name = "id", required = true)
    @Param(name = "userId", required = true)
    public void alterProductOwner(@Para("") ProductDetail detail) {
        Graph graph = graphService.getByProductId(detail.getId());
        if (graph != null) {
            graph.setUserId(detail.getUserId());
            renderJson(graph.update() ? BaseResult.ok("流程图更新成功") : BaseResult.res(-1, "更新失败"));
        }
        ProductDetail detail1 = productDetailService.getDetailById(detail.getId());
        if (detail1 == null) {
            renderJson(BaseResult.res(-2, "没有该产品"));
            return;
        }
        detail1.setUserId(detail.getUserId());
        renderJson(detail1.update() ? BaseResult.ok("更新成功") : BaseResult.res(-3, "更新失败"));
    }


    /**
     * 删除产品，同时删除产品流程图
     */
    @Param(name = "id", required = true)
    public void deleteProduct(@Para("") ProductDetail detail) {
        ProductDetail isHaveProduct = productDetailService.getDetailById(detail.getId());
        if (isHaveProduct == null) {
            renderJson(BaseResult.res(-1, "该产品不存在"));
            return;
        }
        isHaveProduct.setIsDeleted(1);
        renderJson(isHaveProduct.update() ? BaseResult.ok("更新成功") : BaseResult.res(-2, "产品更新失败"));
        Graph graph = graphService.getByProductId(detail.getId());
        if (graph == null) {
            renderJson(BaseResult.ok("该产品流程图不存在"));
        } else {
            graph.setIsDeleted(1);
            renderJson(graph.update() ? BaseResult.ok("更新成功") : BaseResult.res(-2, "产品流程图更新失败"));
        }
    }

    /**
     * 根据产品主键查找产品
     */
    @Clear(AuthInterceptor.class)
    @Param(name = "id", required = true)
    public void showProductById(@Para("") ProductDetail detail) {
        ProductDetail detail1=productDetailService.getDetailById(detail.getId());
        List<Record> records = productDetailService.getDetailViewById(detail.getId());
        Record recordView = (Record) records.get(0);
        if (detail1==null){
            renderJson(BaseResult.res(-1,"不存在该产品"));
            return;
        }
        User user = userService.getById(detail1.getUserId());
        OperatorInformation operatorInformation = operatorInformationService.getByUserId(detail1.getUserId());
        Record record = detail1.toRecord();
        if (user==null){
            record.set("op_number"," ");
            record.set("op_name"," ");
            record.set("op_position"," ");
        }else {
            record.set("op_number",user.getNumber());
            record.set("op_name",operatorInformation.getName());
//            record.set("op_position",operatorInformation.getPosition());
        }
        record.set("img",recordView.get("img"));
        renderJson(DataResult.data(record));
    }

    /**
     * 展示已经上线的所有产品
     * 产品状态 status 0-未发布；1-已提交管理员审核；2-已发布
     */
    @Clear(AuthInterceptor.class)
    public void showProductHavePublished(BigInteger teamId) {
        Kv cond = Kv.by("teamId",teamId);
        renderJson(DataResult.data(productDetailService.showProductHavePublished(cond)));
    }

    /**
     * 展示待上线的所有产品
     * 产品状态 status 0-未发布；1-已提交管理员审核；2-已发布
     */
    @Param(name = "teamId",required = true)
    public void showProductWaitPublishing(BigInteger teamId) {
        if(teamId.compareTo(BigInteger.valueOf(1)) == 0)
        {
            Kv cond = Kv.by("teamId",null);
            renderJson(DataResult.data(productDetailService.showProductWaitPublishing(cond)));
            return;
        }
        Kv cond = Kv.by("teamId",teamId);
        renderJson(DataResult.data(productDetailService.showProductWaitPublishing(cond)));
    }


    /**
     * 根据产品状态查找产品
     * 产品状态 status 0-未发布；1-已提交管理员审核；2-已发布
     */
    @Clear(AuthInterceptor.class)
    @Param(name = "status", required = true)
    @Param(name = "teamId",required = true)
    public void showProductByStatus(BigInteger status,BigInteger teamId) {
        if(teamId.compareTo(BigInteger.valueOf(1)) == 0)
        {
            Kv cond = Kv.by("teamId",null);
            renderJson(DataResult.data(productDetailService.getDetailByStatus(status,null)));
            return;
        }
        renderJson(DataResult.data(productDetailService.getDetailByStatus(status,teamId)));
    }

    /**
     * 根据配置人员主键查找其负责的产品，且可以分类别展示，status为-1表示全部展示
     */
    @Param(name = "status",required = true)
    public void showProductToConfig(@Para("") ProductDetail detail) {
        BigInteger userId = BigInteger.valueOf(getAttr(BaseConstants.ACCOUNT_ID));
        if (detail.getStatus() == -1) {
            renderJson(DataResult.data(productDetailService.getDetailByUserId(userId)));
        } else {
            renderJson(DataResult.data(productDetailService.getDetailByStatusAndUserId(detail.getStatus(), userId)));
        }
    }

    /**
     * 根据配置人员主键查找其负责的产品，且可以分类别展示，status为-1表示全部展示(管理员使用)
     */
    @Clear(AuthInterceptor.class)
    @Param(name = "status",required = true)
    @Param(name = "userId",required = true)
    public void showProductToAdmin(@Para("") ProductDetail detail) {
        if (detail.getStatus() == -1) {
            renderJson(DataResult.data(productDetailService.getDetailByUserId(detail.getUserId())));
        } else {
            renderJson(DataResult.data(productDetailService.getDetailByStatusAndUserId(detail.getStatus(),detail.getUserId())));
        }
    }


    /**
     * 根据产品名称模糊查找产品信息
     * @param detail
     */
    @Param(name = "name")
    public void getDetailByName(@Para("")ProductDetail detail){
        BigInteger userId = BigInteger.valueOf(getAttr(BaseConstants.ACCOUNT_ID));
        User user = userService.getById(userId);
        if (user.getType()==1){
            renderJson(DataResult.data(productDetailService.getDetailByName(null,detail.getName())));
        }else {
            renderJson(DataResult.data(productDetailService.getDetailByName(userId,detail.getName())));
        }
    }

    /**
     * 返回已发布产品的个数
     * 产品状态 0-未发布；1-已提交管理员审核；2-已发布
     */
    @Param(name = "teamId",required = true)
    public void showProductNum(BigInteger teamId) {
        if(teamId.compareTo(BigInteger.valueOf(1)) == 0)
        {
            renderJson(DataResult.data(productDetailService.getDetailByStatus(BigInteger.valueOf(2),null).size()));
            return;
        }
        renderJson(DataResult.data(productDetailService.getDetailByStatus(BigInteger.valueOf(2),teamId).size()));
    }

    //获取用户存款
    public void getUserDeposit(BigInteger type)
    {
        BigInteger userId = BigInteger.valueOf(getAttr(BaseConstants.ACCOUNT_ID));
        UserInformation userInformation = userInformationService.getByUserId(userId);
        if(userInformation == null || userInformation.getCardNumber() == null)
        {
            renderJson(BaseResult.fail("当前用户尚未绑定银行卡"));
            return;
        }
        Kv cond = Kv.by("userId",userId).set("type",type);
        List<Record> list = productUserService.listRecord(cond,"getUserDeposit");
        Record record = list.get(0);
        double num;
        if(record.get("num") == null)
        {
            num = 0;
        }
        else {
            num = record.get("num");
        }
        if(type == null)
        {
            renderJson(DataResult.data(num));
            return;
        }
        if(type.compareTo(BigInteger.valueOf(2)) == 0)
        {
            String cardNumber = userInformation.getCardNumber();
            Kv cond1 = Kv.by("cardNumber",cardNumber);
            Card card = cardService.get(cond1,"getCardByCardNumber");
            if(card == null)
            {
                renderJson(BaseResult.fail("银行卡不存在"));
                return;
            }
            Kv cond2 = Kv.by("userId",userId);
            List<Record> list1 = productUserService.listRecord(cond2,"getUserDeposit");
            if(list1.get(0).get("num") == null)
            {
                num = 0;
            }
            else {
                num = list1.get(0).get("num");
            }
            num = num + card.getBalance();
        }
        if(type.compareTo(BigInteger.valueOf(3)) == 0)
        {
            String cardNumber = userInformation.getCardNumber();
            Kv cond1 = Kv.by("cardNumber",cardNumber);
            Card card = cardService.get(cond1,"getCardByCardNumber");
            if(card == null)
            {
                renderJson(BaseResult.fail("银行卡不存在"));
                return;
            }
            num = card.getBalance();
        }
        renderJson(DataResult.data(num));
    }

    //获取购买记录
    public void getAllDepositRecord(BigInteger userId,BigInteger productId)
    {
        Kv cond = Kv.by("userId",userId).set("productId",productId);
        List productUsers = productUserService.listRecord(cond,"getAllDepositRecord");
        if(productUsers.isEmpty())
        {
            renderJson(BaseResult.ok("当前记录为空"));
            return;
        }
        renderJson(DataResult.data(productUsers));
    }

    //获取产品的购买量
    @Param(name = "productId",required = true)
    public void getNumOfSales(BigInteger productId)
    {
        Kv cond = Kv.by("productId",productId);
        List records = productUserService.listRecord(cond,"getUserDeposit");
        Record record = (Record) records.get(0);
        double num = 0;
        if(record.get("num") != null)
        {
            num = (double)record.get("num");
        }
        renderJson(DataResult.data(num));
    }

    //获取产品的购买次数
    @Param(name = "productId",required = true)
    public void getCountOfSales(BigInteger productId)
    {
        Kv cond = Kv.by("productId",productId);
        List records = productUserService.listRecord(cond,"getSalesCount");
        Record record = (Record) records.get(0);
        long count = 0;
        if(record.get("num") != null)
        {
            count = (long)record.get("num");
        }
        renderJson(DataResult.data(count));
    }

    //预览操作后删除预览记录(0-开发中的预览，1-审核中的预览)
    @Param(name = "productId",required = true)
    @Param(name = "status",required = true)
    public void deletePreviewRecords(BigInteger productId,int status)
    {
        BigInteger userId = BigInteger.valueOf(getAttr(BaseConstants.ACCOUNT_ID));
        Kv cond = Kv.by("productId",productId).set("status",status);
        List<ProductUser> list = productUserService.list(cond,"getAllDepositRecord");
        UserInformation userInformation = userInformationService.getByUserId(userId);
        Kv cond1 = Kv.by("cardNumber",userInformation.getCardNumber());
        Card card = cardService.get(cond1,"getCardByCardNumber");
        for(ProductUser productUser:list)
        {
            if(productUser.getType() == 1)
            {
                ProductDetail productDetail = productDetailService.getDetailById(productId);
                productDetail.setAmount((int) (productDetail.getAmount() + productUser.getAmount()));
                productDetail.setSale((int) (productDetail.getSale() - productUser.getAmount()));
                productDetail.setAvailable((int) (productDetail.getAvailable() + productUser.getAmount()));
                productDetail.update();

                card.setBalance(card.getBalance() + productUser.getAmount());
                card.update();
            }
             productUser.setIsDeleted(1);
             productUser.update();
        }
        renderJson(BaseResult.ok("记录删除成功"));

    }

    //显示指定产品的过去30天购买金额量
    @Param(name = "productId",required = true)
    public void getProductMonthSale(BigInteger productId)
    {
        List list = new ArrayList();
        for(int i = 31;i >= 2;i--)
        {
            String date = kit.getDayBefore(1-i);
            System.out.println(date);
            Kv cond = Kv.by("productId",productId).set("currDate",date);
            List list1 = productUserService.listRecord(cond,"getProductDailySale");
            Record record = (Record)list1.get(0);
            double total = 0;
            if(record.get("total") != null)
            {
                total = record.get("total");
            }
            list.add(total);
        }
        renderJson(DataResult.data(list));

    }

    //获取产品比对昨日上涨额
    @Param(name = "productId",required = true)
    public void getProductDailyChange(BigInteger productId)
    {
        //获取今天的日期实例
        Calendar cal = Calendar.getInstance();
        Date date = cal.getTime();
        Kv cond = Kv.by("productId",productId).set("currDate",date);
        List list = productUserService.listRecord(cond,"getProductDailySale");
        Record record = (Record)list.get(0);
        double total = 0;
        if(record.get("total") != null)
        {
            total = record.get("total");
        }

        cal.setTime(date);
        cal.add(Calendar.DATE, -1);
        Date date1 = cal.getTime();
        Kv cond1 = Kv.by("productId",productId).set("currDate",date1);
        List list1 = productUserService.listRecord(cond1,"getProductDailySale");
        Record record1 = (Record)list1.get(0);
        double total1 = 0;
        if(record1.get("total") != null)
        {
            total1 = record1.get("total");
        }
        double change = 0;
        if(total - total1 > 0)
        {
            change = total - total1;
        }
        renderJson(DataResult.data(change));
    }

    //获取小组所有产品销售总量对比昨日上涨额
    @Param(name = "teamId",required = true)
    public void getTeamProductDailyChange(BigInteger teamId)
    {
        //获取今天的日期实例
        Calendar cal = Calendar.getInstance();
        Date date = cal.getTime();
        Kv cond = Kv.by("teamId",teamId).set("currDate",date);
        List list = productUserService.listRecord(cond,"getProductDailySale");
        Record record = (Record)list.get(0);
        double total = 0;
        if(record.get("total") != null)
        {
            total = record.get("total");
        }

        cal.setTime(date);
        cal.add(Calendar.DATE, -1);
        Date date1 = cal.getTime();
        Kv cond1 = Kv.by("teamId",teamId).set("currDate",date1);
        List list1 = productUserService.listRecord(cond1,"getProductDailySale");
        Record record1 = (Record)list1.get(0);
        double total1 = 0;
        if(record1.get("total") != null)
        {
            total1 = record1.get("total");
        }
        double change = 0;
        if(total - total1 > 0)
        {
            change = total - total1;
        }
        renderJson(DataResult.data(change));
    }

    //获取产品的本月排名
    @Param(name = "productId",required = true)
    public void getRankThisMonth(BigInteger productId)
    {
        Kv cond = Kv.by("productId",productId);
        List list = productDetailService.listRecord(cond,"getProductMonthRank");
        if(list.size() == 0)
        {
            renderJson(BaseResult.fail("当前产品不存在"));
            return;
        }
        Record record = (Record) list.get(0);
        BigInteger rank = record.get("ranking");
        renderJson(DataResult.data(rank));
    }

    //获取产品每月的购买次数
    @Param(name = "productId",required = true)
    public void getProductMonthUser(BigInteger productId)
    {
        List list = new ArrayList();
        for(int i = 13;i >= 2;i--)
        {
            String month = kit.getMonthDateBefore(1-i);
            Kv cond = Kv.by("productId",productId).set("currMonth",month);
            List list1 = productUserService.listRecord(cond,"getProductMonthPerson");
            Record record = (Record)list1.get(0);
            long total = 0;
            if(record.get("total") != null)
            {
                total = record.get("total");
            }
            list.add(total);
        }
        renderJson(DataResult.data(list));
    }

    //获取产品购买用户中各标签用户所占比例
    @Param(name = "productId",required = true)
    public void getUserProportionWithTag(BigInteger productId)
    {
        List list = new ArrayList();
        int count1 = 0;//保守型人数
        int count2 = 0;//稳健型人数
        int count3 = 0;//平衡型人数
        int count4 = 0;//成长型人数
        int count5 = 0;//进取型人数
        Kv cond = Kv.by("productId",productId);
        List<Record> list1 = productUserService.listRecord(cond,"getRecordUser");
        for(Record record:list1) {
            BigInteger userId = (BigInteger) record.get("user_id");
            UserInformation userInformation = userInformationService.getByUserId(userId);
            if(userInformation == null)
            {
                continue;
            }
            switch (userInformation.getTagId().intValue()) {
                case 1:
                    count1++;
                    break;
                case 2:
                    count2++;
                    break;
                case 3:
                    count3++;
                    break;
                case 4:
                    count4++;
                    break;
                case 5:
                    count5++;
                    break;
                default:
                    break;
            }
        }
        list.add(count1);
        list.add(count2);
        list.add(count3);
        list.add(count4);
        list.add(count5);
        renderJson(DataResult.data(list));
    }


    //获取产品月销量排行榜
    public void getRankRecordThisMonth(BigInteger productId)
    {
        List list = productDetailService.listRecord("getProductMonthRank");
        if(list.size() == 0)
        {
            renderJson(BaseResult.fail("当前产品不存在"));
            return;
        }
        renderJson(DataResult.data(list));
    }

    //获取小组内各种类型产品数量
    public void getNumOfProductsByStatus(BigInteger status,BigInteger teamId)
    {
        List<Record> list = productDetailService.getDetailByStatus(status,teamId);
        renderJson(DataResult.data(list.size()));
    }

    //获取小组内各所有成员已完成产品数
    @Param(name = "teamId",required = true)
    public void getNumOfProductsByUser(BigInteger teamId)
    {
        List<Map<Object,Object>> list1 = new ArrayList();
        List<Map<Object,Object>> list2 = new ArrayList();
        Map<Object,Object> map = new HashMap();
        Kv cond = Kv.by("teamId",teamId);
        List<Record> list = teamMemberService.listRecord(cond,"getTeamMemberByView");
        for(Record record:list)
        {
            BigInteger id = (BigInteger) record.get("id");
            list1.add(record.get("user_name"));
            list2.add(record.get("product_online"));
        }
        map.put("name",list1);
        map.put("value",list2);
        renderJson(DataResult.data(map));
    }

    //获取部门月销售冠军
    @Param(name = "teamId",required = true)
    public void getTeamMonthChampion(BigInteger teamId)
    {
        List list = productDetailService.listRecord("getProductMonthRank");
        Record record = (Record) list.get(0);
        if(record == null)
        {
            BaseResult.fail("当前小组尚无销售额");
        }
        renderJson(DataResult.data(record));
    }

    //获取小组某周所有产品每天购买量
    @Param(name = "teamId",required = true)
    @Param(name = "week",required = true)
    public void getTeamProductDailySaleInWeek(BigInteger teamId,int week)
    {
        List list = new ArrayList();
        if(week == 0)
        {
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.DATE,-1);
            int weekDay = cal.get(Calendar.DAY_OF_WEEK);
//            Date date = cal.getTime();
            for(int i = weekDay;i > 0;i--)
            {
//                System.out.println(i);
                String date = kit.getDayBefore(1-i);
                System.out.println(date);
                Kv cond = Kv.by("teamId",teamId).set("currDate",date);
                List list1 = productUserService.listRecord(cond,"getProductDailySale");
                Record record = (Record)list1.get(0);
                double total = 0;
                if(record.get("total") != null)
                {
                    total = record.get("total");
                }
                list.add(total);
            }

        }

        if(week == -1)
        {
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.DATE,-1);
            int weekDay = cal.get(Calendar.DAY_OF_WEEK);
//            Date date = cal.getTime();
            for(int i = 7;i > 0;i--)
            {
//                System.out.println(i);
                String date = kit.getDayBefore(-1 * (weekDay+i) + 1);
                System.out.println(date);
                Kv cond = Kv.by("teamId",teamId).set("currDate",date);
                List list1 = productUserService.listRecord(cond,"getProductDailySale");
                Record record = (Record)list1.get(0);
                double total = 0;
                if(record.get("total") != null)
                {
                    total = record.get("total");
                }
                list.add(total);
            }

        }
        renderJson(DataResult.data(list));

    }

    //获取小组本周所有产品总购买量
    @Param(name = "teamId",required = true)
    @Param(name = "week",required = true)
    public void getTeamProductWeekSale(BigInteger teamId,int week)
    {
        double sum = 0;
        if(week == 0)
        {
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.DATE,-1);
            int weekDay = cal.get(Calendar.DAY_OF_WEEK);
//            System.out.println(weekDay);
//            Date date = cal.getTime();
            for(int i = weekDay;i > 0;i--)
            {
//                System.out.println(i);
                String date = kit.getDayBefore(1-i);
                System.out.println(date);
                Kv cond = Kv.by("teamId",teamId).set("currDate",date);
                List list1 = productUserService.listRecord(cond,"getProductDailySale");
                Record record = (Record)list1.get(0);
                double total = 0;
                if(record.get("total") != null)
                {
                    total = record.get("total");
                }
                sum = sum + total;
            }

        }

        if(week == -1)
        {
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.DATE,-1);
            int weekDay = cal.get(Calendar.DAY_OF_WEEK);
//            Date date = cal.getTime();
            for(int i = 7;i > 0;i--)
            {
//                System.out.println(i);
                String date = kit.getDayBefore(-1 * (weekDay+i) + 1);
//                System.out.println(date);
                Kv cond = Kv.by("teamId",teamId).set("currDate",date);
                List list1 = productUserService.listRecord(cond,"getProductDailySale");
                Record record = (Record)list1.get(0);
                double total = 0;
                if(record.get("total") != null)
                {
                    total = record.get("total");
                }
                sum = sum + total;
            }

        }
        renderJson(DataResult.data(sum));
    }





}
