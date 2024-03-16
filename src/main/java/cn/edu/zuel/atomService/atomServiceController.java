package cn.edu.zuel.atomService;

import cn.edu.zuel.common.module.*;
import cn.edu.zuel.file.FileService;
import cn.edu.zuel.kit.apiKit;
import cn.edu.zuel.kit.kit;
import cn.edu.zuel.product.ProductDetailService;
import cn.edu.zuel.product.ProductUserService;
import cn.edu.zuel.user.BlacklistService;
import cn.edu.zuel.user.CardService;
import cn.edu.zuel.user.UserInformationService;
import cn.edu.zuel.user.UserService;
import cn.edu.zuel.util.Base64Util;
import cn.edu.zuel.util.FileUtil;
import cn.fabrice.common.constant.BaseConstants;
import cn.fabrice.common.pojo.BaseResult;
import cn.fabrice.common.pojo.DataResult;
import cn.fabrice.jfinal.annotation.Param;
import cn.fabrice.jfinal.annotation.ValidateParam;
import cn.fabrice.jfinal.controller.BaseController;
import com.jfinal.aop.Inject;
import com.jfinal.core.Path;
import com.jfinal.kit.Kv;
import com.jfinal.kit.PathKit;
import com.jfinal.plugin.activerecord.Record;

import java.io.IOException;
import java.math.BigInteger;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Path("/atomService")
@ValidateParam
public class atomServiceController extends BaseController {

    @Inject
    UserService userService;

    @Inject
    UserInformationService userInformationService;

    @Inject
    ProductDetailService productDetailService;

    @Inject
    ProductUserService productUserService;

    @Inject
    FileService fileService;

    @Inject
    BlacklistService blacklistService;

    @Inject
    CardService cardService;

    //人脸识别接口
    @Param(name = "faceImgId",required = true)
    public void faceDetection(BigInteger faceImgId)
    {
        BigInteger userId = BigInteger.valueOf(getAttr(BaseConstants.ACCOUNT_ID));
        UserInformation userInformation = userInformationService.getByUserId(userId);

        if(userInformation == null)
        {
            renderJson(BaseResult.fail("当前用户尚未实名认证，请先实名认证"));
            return;
        }

        if(userInformation.getNewlyHead() == null)
        {
            renderJson(BaseResult.fail("当前用户尚未启用人脸功能，认证后方可使用人脸检测"));
            return;
        }

        File faceImg = fileService.getById(faceImgId);
        String faceImgStr = PathKit.getWebRootPath() + "/src/main/resources/file/upfile/" + faceImg.getOriginalName();
        byte[] imgData = new byte[0];
        try {
            imgData = FileUtil.readFileByBytes(faceImgStr);
        } catch (IOException e) {
            e.printStackTrace();
        }
        String imgStr = Base64Util.encode(imgData);

        File faceImg1 = fileService.getById(userInformation.getNewlyHead());
        String faceImgStr1 = PathKit.getWebRootPath() + "/src/main/resources/file/upfile/" + faceImg1.getOriginalName();
        byte[] imgData1 = new byte[0];
        try {
            imgData1 = FileUtil.readFileByBytes(faceImgStr1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        String imgStr1 = Base64Util.encode(imgData1);

        if(!userInformationService.faceMatch(imgStr,imgStr1))
        {
            renderJson(BaseResult.fail("人脸识别失败"));
            return;
        }

        renderJson(BaseResult.ok("人脸识别成功"));
    }

    //对于用户的证件信息进行检验
    @Param(name = "idCardImgId",required = true)
    @Param(name = "backImgId",required = true)
    public void identifyIdCard(BigInteger idCardImgId,BigInteger backImgId)
    {
        BigInteger userId = BigInteger.valueOf(getAttr(BaseConstants.ACCOUNT_ID));
        UserInformation userInformation1 = userInformationService.getByUserId(userId);
        if(userInformation1 == null)
        {
            renderJson(BaseResult.fail("当前用户尚未实名认证，请先实名认证"));
            return;
        }

        File idCardImg = fileService.getById(idCardImgId);
        String idCardImgStr = PathKit.getWebRootPath() + "" + idCardImg.getOriginalName();
        UserInformation userInformation = userInformationService.getIdCardFrontInfo(userId,idCardImgStr);
        String number = userInformation.getNumber();
        userInformation1 = userInformationService.getByUserNumber(number);
        if(userInformation1 == null)
        {
            renderJson(BaseResult.fail("该证件上的用户在系统内不存在"));
            return;
        }
        else
        {
            renderJson(BaseResult.ok("用户证件审查通过"));
            return;
        }
    }

    //手机验证码验证用户接口
    @Param(name = "telephone",required = true)
    public void identifyUser(String telephone)
    {
        User user = userService.getByPhone(telephone);
        if(user == null)
        {
            renderJson(BaseResult.fail("该手机号不存在"));
            return;
        }
        String mobileCode1 = apiKit.sendMobileMessage(telephone);
        int mobileCode = Integer.parseInt(mobileCode1);
        System.out.println(mobileCode);
        renderJson(DataResult.data(mobileCode));
    }

    //利息计算接口
    @Param(name = "term",required = true)
    @Param(name = "annualRate",required = true)
    @Param(name = "money",required = true)
    @Param(name = "interestWay",required = true)
    public void interestCaculation(int term, float annualRate,float money,int interestWay)
    {
        annualRate = annualRate/100;
        float year = (float)term/360;
        float interest = money*year*annualRate;
        switch (interestWay)
        {
            case 0: //到期付息
                renderJson(DataResult.data(interest));
                return;
            case 1: //按月付息
                renderJson(DataResult.data(interest/year/12));
                return;
            case 2:  //按年付息
                renderJson(DataResult.data(interest/year));
                return;
            case 3:  //按季付息
                renderJson(DataResult.data(interest/year/4));
                return;
            default:
                renderJson(BaseResult.fail("计息方式有误"));
                return;
        }

    }

    //库存锁定接口
    @Param(name = "productId",required = true)
    @Param(name = "money",required = true)
    public void stockLock(BigInteger productId,float money)
    {
        Kv cond = Kv.by("id",productId);
        ProductDetail productDetail = productDetailService.get(cond,"getDetailById");
        if(productDetail == null)
        {
            renderJson(BaseResult.fail("当前产品不存在"));
            return;
        }
        int available = productDetail.getAvailable() - (int)money;
        if(available < 0)
        {
            renderJson(BaseResult.fail("产品库存不足"));
            return;
        }
        productDetail.setAvailable(available);
        productDetail.update();
        renderJson(BaseResult.ok("库存锁定成功"));
    }

    //库存释放接口
    @Param(name = "productId",required = true)
    @Param(name = "money",required = true)
    public void stockRelease(BigInteger productId,float money)
    {
        BigInteger userId = BigInteger.valueOf(getAttr(BaseConstants.ACCOUNT_ID));
        Kv cond = Kv.by("id",productId);
        ProductDetail productDetail = productDetailService.get(cond,"getDetailById");
        if(productDetail == null)
        {
            renderJson(BaseResult.fail("当前产品不存在"));
            return;
        }
        int available = productDetail.getAvailable() + (int)money;
        productDetail.setAvailable(available);

        cond = Kv.by("userId",userId).set("productId",productId).set("amount",money);
        ProductUser productUser = productUserService.get(cond,"getProductUserByUserIdAndProductId");
        if(productUser == null)
        {
            renderJson(BaseResult.fail("获取购买订单失败"));
            return;
        }
        productUser.setIsDeleted(1);
        if(!(productDetail.update() && productUser.update()))
        {
            renderJson(BaseResult.fail("库存释放失败"));
            return;
        }
        renderJson(BaseResult.ok("库存释放成功"));
    }

    //库存更新接口
    @Param(name = "productId",required = true)
    @Param(name = "money",required = true)
    public void stockUpdate(BigInteger productId,float money)
    {
        BigInteger userId = BigInteger.valueOf(getAttr(BaseConstants.ACCOUNT_ID));
        UserInformation userInformation = userInformationService.getByUserId(userId);
        Kv cond = Kv.by("id",productId);
        ProductDetail productDetail = productDetailService.get(cond,"getDetailById");
        if(productDetail == null)
        {
            renderJson(BaseResult.fail("当前产品不存在"));
            return;
        }
        int amount = productDetail.getAmount() - (int)money;
        productDetail.setAmount(amount);
        productDetail.setSale(productDetail.getSale()+(int)money);

        cond = Kv.by("userId",userId).set("productId",productId).set("amount",money);
        ProductUser productUser = productUserService.get(cond,"getProductUserByUserIdAndProductId");
        if(productUser == null)
        {
            renderJson(BaseResult.fail("获取购买订单失败"));
            return;
        }
        productUser.setType(1);
        if(!(productDetail.update() && productUser.update()))
        {
            renderJson(BaseResult.fail("库存更新失败"));
            return;
        }

        Kv cond1 = Kv.by("cardNumber",userInformation.getCardNumber());
        System.out.println(userInformation.getCardNumber());
        Card card = cardService.get(cond1,"getCardByCardNumber");
        if(card == null)
        {
            renderJson(BaseResult.fail("银行卡不存在"));
            return;
        }
        card.setBalance(card.getBalance() - money);
        if(!card.update())
        {
            renderJson(BaseResult.fail("更新余额失败"));
            return;
        }
        renderJson(DataResult.data(productUser));
    }

    //生成订单接口
    @Param(name = "productId",required = true)
    @Param(name = "ammount",required = true)
    public void makeOrder(BigInteger productId,float ammount)
    {
        BigInteger userId = BigInteger.valueOf(getAttr(BaseConstants.ACCOUNT_ID));
        User user = userService.getById(userId);
        if(user == null)
        {
            renderJson(BaseResult.fail("当前用户不存在"));
            return;
        }
        Kv cond = Kv.by("id",productId);
        ProductDetail productDetail = productDetailService.get(cond,"getDetailById");
        if(productDetail == null)
        {
            renderJson(BaseResult.fail("当前产品不存在"));
            return;
        }

        //获取当前日期
        Date date = new Date();

        //获取当天10点的时间戳
        Calendar calendar = Calendar.getInstance();
        calendar.set(calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH),22,0,0);
        Date date1 = calendar.getTime();
        System.out.println(date1);

        Date startDate = new Date();
        //如果当前购买时间小于10点，则当日计息
        if(date.before(date1))
        {
            calendar.set(calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH));
            startDate = calendar.getTime();
        }
        else
        {
            calendar.set(calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH));
            calendar.add(Calendar.DAY_OF_MONTH,1);
            startDate = calendar.getTime();
        }

        int term = productDetail.getTerm();
        //获取存款期限
        int month_term = term / 30;

        //获取存款到期日
        Date endDay = kit.getMonthDate(startDate,month_term);
//        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//        String string = simpleDateFormat.format(endDay);
//        calendar.setTime(endDay);
//        Date endDay1 = calendar.getTime();
//        try {
//            endDay1 = simpleDateFormat.parse(string);
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }

        ProductUser productUser = new ProductUser();
        productUser.setAmount(ammount);
        productUser.setProductId(productId);
        productUser.setUserId(userId);
        productUser.setInterestStart(startDate);
        if(productDetail.getType() == 0)
        {
            endDay = null;
        }
        productUser.setEndDay(endDay);
        System.out.println(endDay);
        if(productUser.save())
        {
            Kv cond1 = Kv.by("itemId",productUser.getId());
            productUser = productUserService.get(cond1,"getProductUserById");
            renderJson(DataResult.data(productUser));
            return;
        }
        else
        {
            renderJson(BaseResult.fail("生成订单失败"));
        }
    }

    //付款接口
    @Param(name = "cardPassword",required = true)
    @Param(name = "amount",required = true)
    public void payMoney(String cardPassword,float amount)
    {
        BigInteger userId = BigInteger.valueOf(getAttr(BaseConstants.ACCOUNT_ID));
        UserInformation userInformation = userInformationService.getByUserId(userId);
        if(userInformation == null)
        {
            renderJson(BaseResult.fail("当前用户不存在"));
            return;
        }

//        if(!cardNumber.equals(userInformation.getCardNumber()))
//        {
//            renderJson(BaseResult.fail("银行卡卡号输入错误"));
//            return;
//        }

        if(!cardPassword.equals(userInformation.getCardPassword()))
        {
            renderJson(BaseResult.fail("银行卡密码输入错误"));
            return;
        }

        Kv cond = Kv.by("cardNumber",userInformation.getCardNumber());
        Card card = cardService.get(cond,"getCardByCardNumber");
        if(card == null)
        {
            renderJson(BaseResult.fail("银行卡不存在"));
            return;
        }
        if(amount > card.getBalance())
        {
            renderJson(BaseResult.fail("当前账户余额不足"));
            return;
        }

        renderJson(BaseResult.ok("付款成功"));
    }

    //白名单控制接口
    public void blackListIdentity()
    {
        BigInteger userId = BigInteger.valueOf(getAttr(BaseConstants.ACCOUNT_ID));
        Kv cond = Kv.by("userId",userId);
        System.out.println(userId);
        Blacklist blacklist = blacklistService.get(cond,"getBanUserByUserId");
        if(blacklist != null)
        {
            renderJson(DataResult.fail("当前用户已被拉黑，无法进行购买",blacklist));
            return;
        }
        renderJson(BaseResult.ok());

    }

    //地域控制接口
    @Param(name = "region",required = true)   //用户所在区域
    @Param(name = "productRegion",required = true)   //产品控制区域
    public void addressControl(String productRegion,String region)
    {
//        ProductDetail productDetail = productDetailService.getDetailById(productId);
//        if(productDetail == null)
//        {
//            renderJson(BaseResult.fail("当前产品不存在"));
//            return;
//        }
        if(productRegion == "不限地区" || region.contains(productRegion))
        {
            renderJson(BaseResult.ok());
            return;
        }
        renderJson(BaseResult.fail("当前所在地域不支持购买"));
    }

    //标签控制接口
    @Param(name = "tagId",required = true)  //用户标签id
    @Param(name = "riskId",required = true)  //产品风险id
    public void tagControl(BigInteger tagId,BigInteger riskId)
    {
        if(tagId.compareTo(riskId) == 1 || tagId.compareTo(riskId) == 0)
        {
            renderJson(BaseResult.ok("建议购买"));
            return;
        }

        if(tagId.compareTo(riskId) == -1)
        {
            renderJson(BaseResult.ok("不建议购买"));
            return;
        }

    }

    //人脸注册检验接口
    public void ifFaceRegister()
    {
        BigInteger userId = BigInteger.valueOf(getAttr(BaseConstants.ACCOUNT_ID));
        UserInformation userInformation = userInformationService.getByUserId(userId);

        if(userInformation == null)
        {
            renderJson(BaseResult.fail("当前用户尚未实名认证，请先实名认证"));
            return;
        }

        if(userInformation.getNewlyHead() == null)
        {
            renderJson(BaseResult.fail("当前用户尚未启用人脸功能，认证后方可使用人脸检测"));
            return;
        }

        renderJson(BaseResult.ok());
    }

    //用户信息检验接口
    public void ifIdCardRegister()
    {
        BigInteger userId = BigInteger.valueOf(getAttr(BaseConstants.ACCOUNT_ID));
        UserInformation userInformation = userInformationService.getByUserId(userId);

        if(userInformation == null)
        {
            renderJson(BaseResult.fail("当前用户尚未实名认证，请先实名认证"));
            return;
        }

//        if(userInformation.getCardNumber() == null)
//        {
//            renderJson(BaseResult.fail("当前用户尚未绑定银行卡，请先进行绑定再进行购买"));
//            return;
//        }

        renderJson(BaseResult.ok());
    }

    //单日购买限额控制
    @Param(name = "productLimit",required = true)
    @Param(name = "productId",required = true)
    @Param(name = "amount",required = true)
    public void dailyLimit(float productLimit,BigInteger productId,float amount)
    {
        BigInteger userId = BigInteger.valueOf(getAttr(BaseConstants.ACCOUNT_ID));

        Calendar calendar = Calendar.getInstance();
        calendar.set(calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH),0,0,0);
        Date startTime = calendar.getTime();

        calendar.set(calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH),24,0,0);
        Date endTime = calendar.getTime();

        Kv cond = Kv.by("userId",userId).set("productId",productId).set("startTime",startTime).set("endTime",endTime);
        List records = productUserService.listRecord(cond,"getUserDeposit");
        Record record = (Record) records.get(0);
        double num = 0;
        if(record.get("num") != null)
        {
            num = (double)record.get("num");
        }
        if(num + amount > productLimit)
        {
            renderJson(BaseResult.fail("该产品购买量已超单日限额，请明日再进行购买"));
            return;
        }
        renderJson(BaseResult.ok());
    }

    //单人购买限额控制
    @Param(name = "personalLimit",required = true)
    @Param(name = "productId",required = true)
    @Param(name = "amount",required = true)
    public void personalLimit(float personalLimit,BigInteger productId,float amount)
    {
        BigInteger userId = BigInteger.valueOf(getAttr(BaseConstants.ACCOUNT_ID));

        Kv cond = Kv.by("userId",userId).set("productId",productId);
        List records = productUserService.listRecord(cond,"getUserDeposit");
        Record record = (Record) records.get(0);
        double num = 0;
        if(record.get("num") != null)
        {
            num = (double)record.get("num");
        }
        if(num + amount > personalLimit)
        {
            renderJson(BaseResult.fail("该产品购买量已超单人限额，无法进行购买"));
            return;
        }
        renderJson(BaseResult.ok());
    }

    //递增金额控制接口
    @Param(name = "increaseMoney",required = true)
    @Param(name = "productId",required = true)
    @Param(name = "amount",required = true)
    public void increaseMoneyControl(float increaseMoney,BigInteger productId,float amount)
    {
        BigInteger userId = BigInteger.valueOf(getAttr(BaseConstants.ACCOUNT_ID));

        Kv cond = Kv.by("userId",userId).set("productId",productId);
        List records = productUserService.listRecord(cond,"getUserDeposit");
        Record record = (Record) records.get(0);

        if(record.get("num") != null)
        {
            if(amount < increaseMoney || amount % increaseMoney != 0)
            {
                renderJson(BaseResult.fail("再次购买金额需大于递增金额且是递增金额的整数倍"));
                return;
            }
        }

        renderJson(BaseResult.ok());
    }

}
