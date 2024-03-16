package cn.edu.zuel.user;

import cn.edu.zuel.common.module.User;
import cn.edu.zuel.common.module.UserInformation;
import cn.edu.zuel.kit.apiKit;
import cn.edu.zuel.kit.kit;
import cn.fabrice.common.pojo.BaseResult;
import cn.fabrice.jfinal.service.BaseService;
import com.jfinal.kit.Kv;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

public class UserInformationService  extends BaseService<UserInformation> {
    public UserInformationService() {
        // 第一个参数名需要与all.sql中配置的#namespace("user")保持一致
        // 第二个参数是用户类module，最后一个数据库表名
        super("userInformation.", UserInformation.class, "userInformation");
    }

    public UserInformation getByUserId(BigInteger userId) {
        Kv cond = Kv.by("userId", userId);
        return get(cond, "getByUserId");
    }

    public UserInformation getByUserNumber(String number) {
        Kv cond = Kv.by("number", number);
        return get(cond, "getByUserNumber");
    }

    //根据身份证正面图片获取个人具体信息
    public UserInformation getIdCardFrontInfo(BigInteger userId, String idCardImg) {
        Map<String, Object> json = apiKit.identifyIdCardFront(idCardImg);
        System.out.println(json);
        if (json == null) {
            return null;
        } else {
            UserInformation userInformation = new UserInformation();
            Map<String, Object> json1 = (Map<String, Object>) json.get("words_result");
            Map<String, Object> json2 = (Map<String, Object>) json1.get("姓名");
            String name = (String) json2.get("words");
            userInformation.setName(name);
            json2 = (Map<String, Object>) json1.get("民族");
            String nation = (String) json2.get("words");
            userInformation.setNation(nation);
            json2 = (Map<String, Object>) json1.get("住址");
            String address = (String) json2.get("words");
            userInformation.setAddress(address);
            json2 = (Map<String, Object>) json1.get("公民身份号码");
            String idNum = (String) json2.get("words");
            userInformation.setNumber(idNum);
            json2 = (Map<String, Object>) json1.get("出生");
            String birth = (String) json2.get("words");
            SimpleDateFormat form = new SimpleDateFormat("yyyyMMdd");
            Date date = null;
            try {
                date = form.parse(birth);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            userInformation.setBirth(date);
            json2 = (Map<String, Object>) json1.get("性别");
            String gender_str = (String) json2.get("words");
            int gender;
            if (gender_str.equals("男")) {
                gender = 0;
            } else {
                gender = 1;
            }
            userInformation.setGender(gender);

            String head_photo = (String) json.get("photo");
            userInformation.setHeadPhoto(head_photo);

            userInformation.setUserId(userId);
            return userInformation;

        }
    }

    //根据身份证反面图片获取信息
    public UserInformation getIdCardBackInfo(UserInformation userInformation, String idCardImg) {
        Map<String, Object> json = apiKit.identifyIdCardBack(idCardImg);
        if (json == null) {
            return null;
        } else {
            Map<String, Object> json1 = (Map<String, Object>) json.get("words_result");
            Map<String, Object> json2 = (Map<String, Object>) json1.get("失效日期");
            String finalDate1 = (String) json2.get("words");
            Date finalDate = kit.stringToDate(finalDate1);
            userInformation.setFinalDate(finalDate);
            json2 = (Map<String, Object>) json1.get("签发机关");
            String authority = (String) json2.get("words");
            userInformation.setAuthority(authority);
            json2 = (Map<String, Object>) json1.get("签发日期");
            String signDate1 = (String) json2.get("words");
            Date signDate = kit.stringToDate(signDate1);
            userInformation.setSignDate(signDate);
            return userInformation;
        }
    }

    //人脸比对，与身份证图片进行比对
    public boolean faceMatch(String headFace,String faceImg)
    {
        Map<String, Object> json = apiKit.faceMatch(headFace,faceImg);
        if(json == null)
        {
            return false;
        }
        Map<String, Object> json1 = (Map<String, Object>)json.get("result");
        double score1;
        BigDecimal score;
        if (json1.get("score") instanceof Integer) {
            score1 = (int)json1.get("score");
            score = BigDecimal.valueOf(score1);
        }
        else
        {
            score = (BigDecimal)json1.get("score");
        }

        if(score.floatValue() > 90)
        {
            return true;
        }
        else
        {
            return false;
        }
    }
}
