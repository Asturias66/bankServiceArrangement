package cn.edu.zuel.user;

import cn.edu.zuel.common.intercepter.AuthInterceptor;
import cn.edu.zuel.common.module.Card;
import cn.edu.zuel.common.module.File;
import cn.edu.zuel.common.module.User;
import cn.edu.zuel.common.module.UserInformation;
import cn.edu.zuel.file.FileService;
import cn.edu.zuel.util.Base64Util;
import cn.edu.zuel.util.FileUtil;
import cn.fabrice.common.constant.BaseConstants;
import cn.fabrice.common.pojo.BaseResult;
import cn.fabrice.common.pojo.DataResult;
import cn.fabrice.jfinal.annotation.Param;
import cn.fabrice.jfinal.annotation.ValidateParam;
import com.jfinal.aop.Clear;
import com.jfinal.aop.Inject;
import com.jfinal.core.Controller;
import com.jfinal.core.Path;
import com.jfinal.kit.Kv;
import com.jfinal.kit.PathKit;
import com.jfinal.plugin.activerecord.Record;

import java.io.IOException;
import java.math.BigInteger;
import java.util.List;

@Path("/userInformation")
@ValidateParam
public class UserInformationController extends Controller {
    @Inject
    UserService userService;
    @Inject
    UserInformationService userInformationService;
    @Inject
    CardService cardService;
    @Inject
    FileService fileService;

    /**
     * 用户登录之后查看个人信息
     *
     */
    public void scanInformation (){
        BigInteger userId = BigInteger.valueOf(getAttr(BaseConstants.ACCOUNT_ID));
        UserInformation objectInformation = userInformationService.getByUserId(userId);
        renderJson(DataResult.data(objectInformation));
    }

    //证件注册接口，根据身份证向数据库导入证件信息
    @Clear(AuthInterceptor.class)
    @Param(name = "frontImgId",required = true)
    @Param(name = "backImgId",required = true)
    @Param(name = "userId",required = true)
    public void registerIdCard(BigInteger userId, BigInteger frontImgId,BigInteger backImgId) {
        UserInformation userInformation1 = userInformationService.getByUserId(userId);
        File frontImg = fileService.getById(frontImgId);
        String idCardFrontImg = PathKit.getWebRootPath() + "/src/main/resources/file/upfile/" + frontImg.getOriginalName();
        File backImg = fileService.getById(backImgId);
        String idCardBackImg = PathKit.getWebRootPath() + "/src/main/resources/file/upfile/" + backImg.getOriginalName();
        if (userInformation1 != null)
        {
            renderJson(BaseResult.fail("当前用户已有被绑定的身份信息"));
            return;
        }
        UserInformation userInformation = userInformationService.getIdCardFrontInfo(userId,idCardFrontImg);
        if (userInformation == null) {
            renderJson(BaseResult.fail("身份证识别失败"));
            return;
        } else {
            userInformation1 = userInformationService.getByUserNumber(userInformation.getNumber());
            if (userInformation1 != null)
            {
                renderJson(BaseResult.fail("当前身份证已经注册"));
                return;
            }

            userInformation = userInformationService.getIdCardBackInfo(userInformation,idCardBackImg);

            if (userInformation.save()) {
                renderJson(DataResult.data(userInformation));
                return;
            } else {
                renderJson(BaseResult.fail("存入数据库失败"));
                return;
            }
        }
    }

    //绑定银行卡接口
    @Param(name = "cardNumber",required = true)
    @Param(name = "cardPassword",required = true)
    @Param(name = "name",required = true)
    @Param(name = "number",required = true)
    public void addBankCard(String name,String number,String cardNumber,String cardPassword) {
        BigInteger userId = BigInteger.valueOf(getAttr(BaseConstants.ACCOUNT_ID));
        UserInformation userInformation = userInformationService.getByUserId(userId);
        if (userInformation == null)
        {
            renderJson(BaseResult.fail("当前用户不存在"));
            return;
        }

        if(userInformation.getCardNumber() != null)
        {
            renderJson(BaseResult.fail("当前用户已有绑定的银行卡"));
            return;
        }

        Kv cond = Kv.by("cardNumber",cardNumber);
        Card card = cardService.get(cond,"getCardByCardNumber");
        if (card == null)
        {
            renderJson(BaseResult.fail("当前银行卡不存在"));
            return;
        }

        UserInformation userInformation1 = userInformationService.get(cond,"getUserByCardNumber");
//        if (userInformation1 != null)
//        {
//            renderJson(BaseResult.fail("该银行卡已被绑定"));
//            return;
//        }

        if (!cardPassword.equals(card.getCardPassword()))
        {
            renderJson(BaseResult.fail("银行卡密码输入错误，绑定失败"));
            return;
        }

        userInformation.setCardNumber(cardNumber);
        userInformation.setCardPassword(cardPassword);
        userInformation.update();
        renderJson(BaseResult.ok("绑定银行卡成功"));
    }

    //更改绑定银行卡接口
    @Param(name = "cardNumber",required = true)
    @Param(name = "cardPassword",required = true)
    @Param(name = "name",required = true)
    @Param(name = "number",required = true)
    public void changeBankCard(String name,String number,String cardNumber,String cardPassword) {
        BigInteger userId = BigInteger.valueOf(getAttr(BaseConstants.ACCOUNT_ID));
        UserInformation userInformation = userInformationService.getByUserId(userId);
        if (userInformation == null)
        {
            renderJson(BaseResult.fail("当前用户不存在"));
            return;
        }

        Kv cond = Kv.by("cardNumber",cardNumber);
        Card card = cardService.get(cond,"getCardByCardNumber");
        if (card == null)
        {
            renderJson(BaseResult.fail("当前银行卡不存在"));
            return;
        }

        UserInformation userInformation1 = userInformationService.get(cond,"getUserByCardNumber");
        if (userInformation1 != null)
        {
            renderJson(BaseResult.fail("该银行卡已被绑定"));
            return;
        }

        if (!cardPassword.equals(card.getCardPassword()))
        {
            renderJson(BaseResult.fail("银行卡密码输入错误，绑定失败"));
            return;
        }

        userInformation.setCardNumber(cardNumber);
        userInformation.setCardPassword(cardPassword);
        userInformation.update();
        renderJson(BaseResult.ok("更改绑定银行卡成功"));
    }

    //人脸注册接口,将人脸信息注册进入人脸库
    @Param(name = "faceImgId",required = true)
    public void faceRegister(BigInteger faceImgId)
    {
        BigInteger userId = BigInteger.valueOf(getAttr(BaseConstants.ACCOUNT_ID));
        File frontImg = fileService.getById(faceImgId);
        String faceImg = PathKit.getWebRootPath() + "/src/main/resources/file/upfile/" + frontImg.getOriginalName();
        UserInformation userInformation = userInformationService.getByUserId(userId);
        if(userInformation == null)
        {
            renderJson(BaseResult.fail("当前用户尚未实名认证，请先实名认证"));
            return;
        }
        byte[] imgData = new byte[0];
        try {
            imgData = FileUtil.readFileByBytes(faceImg);
        } catch (IOException e) {
            e.printStackTrace();
        }
        String imgStr = Base64Util.encode(imgData);

        if(!userInformationService.faceMatch(imgStr,userInformation.getHeadPhoto()))
        {
            renderJson(BaseResult.fail("人脸照片与身份证照片不相符"));
            return;
        }

        userInformation.setNewlyHead(faceImgId);

        if(userInformation.update())
        {
            renderJson(BaseResult.ok("人脸注册成功"));
            return;
        }

        renderJson(BaseResult.fail("人脸注册失败"));
    }

    //用户头像上传（储存user表中对应icon_id字段）
    @Param(name = "iconId",required = true)
    public void uploadIcon(BigInteger iconId)
    {
        BigInteger userId = BigInteger.valueOf(getAttr(BaseConstants.ACCOUNT_ID));
        User user = userService.getById(userId);
        if(user == null)
        {
            renderJson(BaseResult.fail("该用户不存在"));
            return;
        }

        user.setIconId(iconId);
        if(user.update())
        {
            renderJson(BaseResult.ok("头像上传成功"));
            return;
        }
        else
        {
            renderJson(BaseResult.fail("头像上传失败"));
            return;
        }
    }

    //更新证件注册信息接口，根据身份证向数据库重新导入证件信息
    @Param(name = "frontImgId",required = true)
    @Param(name = "backImgId",required = true)
    public void updateIdCard(BigInteger frontImgId,BigInteger backImgId) {
        BigInteger userId = BigInteger.valueOf(getAttr(BaseConstants.ACCOUNT_ID));
        UserInformation userInformation1 = userInformationService.getByUserId(userId);
        File frontImg = fileService.getById(frontImgId);
        String idCardFrontImg = PathKit.getWebRootPath() + "/src/main/resources/file/upfile/" + frontImg.getOriginalName();
        File backImg = fileService.getById(backImgId);
        String idCardBackImg = PathKit.getWebRootPath() + "/src/main/resources/file/upfile/" + backImg.getOriginalName();
        if (userInformation1 == null)
        {
            renderJson(BaseResult.fail("当前用户不存在"));
            return;
        }
        UserInformation userInformation = userInformationService.getIdCardFrontInfo(userId,idCardFrontImg);
        if (userInformation == null) {
            renderJson(BaseResult.fail("身份证识别失败"));
            return;
        } else {
            Kv cond1 = Kv.by("number",userInformation.getNumber()).set("userId",userId);
            UserInformation userInformation2 = userInformationService.get(cond1,"getByUserNumber");
            if (userInformation2 != null)
            {
                renderJson(BaseResult.fail("当前身份证已经注册"));
                return;
            }

            userInformation = userInformationService.getIdCardBackInfo(userInformation,idCardBackImg);
            userInformation1.setFinalDate(userInformation.getFinalDate());
            userInformation1.setHeadPhoto(userInformation.getHeadPhoto());
            userInformation1.setAuthority(userInformation.getAuthority());
            userInformation1.setSignDate(userInformation.getSignDate());
            userInformation1.setGender(userInformation.getGender());
            userInformation1.setBirth(userInformation.getBirth());
            userInformation1.setNumber(userInformation.getNumber());
            userInformation1.setAddress(userInformation.getAddress());
            userInformation1.setNation(userInformation.getNation());
            userInformation1.setName(userInformation.getName());

            if (userInformation1.update()) {
                renderJson(DataResult.data(userInformation1));
                return;
            } else {
                renderJson(BaseResult.fail("存入数据库失败"));
                return;
            }
        }
    }

    @Clear(AuthInterceptor.class)
    //比对用户输入的姓名和身份证号与数据库是否相符
    @Param(name = "telephone",required = true)
    @Param(name = "name",required = true)
    @Param(name = "idCardNumber",required = true)
    public void matchNameAndIdCard(String telephone,String name,String idCardNumber)
    {
        User user = userService.getByPhone(telephone);
        if(user == null)
        {
            renderJson(BaseResult.fail("当前手机号不存在"));
            return;
        }
        UserInformation userInformation = userInformationService.getByUserId(user.getId());
        if(userInformation == null)
        {
            renderJson(BaseResult.fail("该用户尚未拥有个人信息"));
            return;
        }
        if(name.equals(userInformation.getName()) && idCardNumber.equals(userInformation.getNumber()))
        {
            renderJson(BaseResult.ok("验证通过"));
            return;
        }
        renderJson(BaseResult.fail("姓名或身份证号填写有误"));
    }

    //人脸识别接口(无token)
    @Clear(AuthInterceptor.class)
    @Param(name = "faceImgId",required = true)
    @Param(name = "telephone",required = true)
    public void faceDetectionWithoutToken(BigInteger faceImgId,String telephone)
    {
        User user = userService.getByPhone(telephone);
        BigInteger userId = user.getId();
        UserInformation userInformation = userInformationService.getByUserId(userId);
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

    //风险测评接口
    @Param(name = "score",required = true)
    public void riskTest(int score)
    {
        BigInteger userId = BigInteger.valueOf(getAttr(BaseConstants.ACCOUNT_ID));
        UserInformation userInformation = userInformationService.getByUserId(userId);
        int tagId = 0;

        if(userInformation == null)
        {
            renderJson(BaseResult.fail("登录用户信息不存在"));
            return;
        }

        if(score <= 20)
        {
            tagId = 1;
            userInformation.setTagId(BigInteger.valueOf(tagId));
        }

        if(score >= 21 && score <= 45)
        {
            tagId = 2;
            userInformation.setTagId(BigInteger.valueOf(tagId));
        }

        if(score >= 46 && score <= 70)
        {
            tagId = 3;
            userInformation.setTagId(BigInteger.valueOf(tagId));
        }

        if(score >= 71 && score <= 85)
        {
            tagId = 4;
            userInformation.setTagId(BigInteger.valueOf(tagId));
        }

        if(score >= 86 && score <= 100)
        {
            tagId = 5;
            userInformation.setTagId(BigInteger.valueOf(tagId));
        }

        if(!userInformation.update())
        {
            renderJson(BaseResult.fail("用户标签更新失败"));
            return;
        }

        renderJson(DataResult.data(tagId));
    }

    //根据手机号判断用户是否实名认证
    @Clear(AuthInterceptor.class)
    @Param(name = "telephone",required = true)
    public void ifRegisterByTelephone(String telephone)
    {
        Kv cond = Kv.by("telephone",telephone);
        List list = userService.listRecord(cond,"getAllUser");
        if(list.size() == 0)
        {
            renderJson(BaseResult.fail("该手机号不存在"));
            return;
        }
        Record record = (Record) list.get(0);
        if(record.get("telephone") == null)
        {
            renderJson(BaseResult.fail("尚未实名认证"));
            return;
        }
        renderJson(BaseResult.ok());
    }

    //根据手机号判断用户是否人脸注册
    @Clear(AuthInterceptor.class)
    @Param(name = "telephone",required = true)
    public void ifFaceRegisterByTelephone(String telephone)
    {
        Kv cond = Kv.by("telephone",telephone);
        List list = userService.listRecord(cond,"getAllUser");
        if(list.size() == 0)
        {
            renderJson(BaseResult.fail("该手机号不存在"));
            return;
        }
        Record record = (Record) list.get(0);
        if(record.get("newly_head") == null)
        {
            renderJson(BaseResult.fail("尚未人脸注册"));
            return;
        }
        renderJson(BaseResult.ok());
    }


}
