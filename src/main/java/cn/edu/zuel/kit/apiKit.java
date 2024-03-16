package cn.edu.zuel.kit;

import cn.edu.zuel.common.config.MyConstants;
import cn.edu.zuel.util.*;
import cn.fabrice.common.pojo.BaseResult;
import com.alibaba.fastjson.JSONObject;
import com.tencentcloudapi.common.exception.TencentCloudSDKException;
import com.tencentcloudapi.common.profile.ClientProfile;
import com.tencentcloudapi.common.profile.HttpProfile;
import com.zhenzi.sms.ZhenziSmsClient;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.PostMethod;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import com.tencentcloudapi.common.Credential;
import com.tencentcloudapi.common.exception.TencentCloudSDKException;

//导入可选配置类
import com.tencentcloudapi.common.profile.ClientProfile;
import com.tencentcloudapi.common.profile.HttpProfile;

// 导入对应SMS模块的client
import com.tencentcloudapi.sms.v20210111.SmsClient;

// 导入要请求接口对应的request response类
import com.tencentcloudapi.sms.v20210111.models.SendSmsRequest;
import com.tencentcloudapi.sms.v20210111.models.SendSmsResponse;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.*;

public class apiKit {


    // 此接口支持发送验证码短信、订单通知短信；
    // 调试期间，请使用测试专用短信模板：您的验证码是：1234。请不要把验证码泄露给其他人；
    // 请求参数中的account和password分别为 APIID、APIKEY，请在本页面上方处获取。
    public static String sendMobileMessage(String mobilePhone)
    {
        String apiUrl = "https://sms_developer.zhenzikj.com";
        String appId = "111717";           //应用id
        String appSecret = "xxxx";       //应用secret
        String templateId = "9489";      //模板id
        String invalidTimer = "1";    //失效时间
        //生成验证码
        String code = randomKit.generateValidateCode(4);
        //调用service的方法来发送验证码短信
        //榛子云短信 客户端
        //请求地址，个人开发者使用https://sms_developer.zhenzikj.com，企业开发者使用https://sms.zhenzikj.com
        ZhenziSmsClient client = new ZhenziSmsClient(apiUrl, appId, appSecret);
        //存放请求参数的map集合
        Map<String, Object> params = new HashMap<String, Object>();
        //接收者手机号码
        params.put("number", mobilePhone);
        //短信模板ID
        params.put("templateId", templateId);
        //短信模板参数
        String[] templateParams = new String[2];
        templateParams[0] = code;
        templateParams[1] = invalidTimer;
        params.put("templateParams", templateParams);
        /**
         * 1.send方法用于单条发送短信,所有请求参数需要封装到Map中;
         * 2.返回结果为json串：{ "code":0,"data":"发送成功"}
         * 3.备注：（code: 发送状态，0为成功。非0为发送失败，可从data中查看错误信息）
         */
        String result = null;
        try {
            result = client.send(params);
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println(result);
        return code;
//        boolean b = smsService.sendShortMessage(phone, code);
//        if (b) {
//            //将验证码存入redis
//            String key = "ymjr:sms:code:" + phone;
//            redisUtils.set(key, code, 120);
//            return Result.ok().message("短信发送成功");
//        }
//        return Result.error().message("短信发送失败");
//        String userid = "";
//        String pwd = "";
//        Boolean isEncryptPwd = false;
//        try
//        {
//            // 参数类
//            Message message = new Message();
//            // 实例化短信处理对象
//            CHttpPost cHttpPost = new CHttpPost();
//
//            // 设置账号   将 userid转成大写,以防大小写不一致
//            message.setUserid(userid.toUpperCase());
//
//            //判断密码是否加密。
//            //密码加密，则对密码进行加密
//            if(isEncryptPwd)
//            {
//                // 设置时间戳
//                String timestamp = sdf.format(Calendar.getInstance().getTime());
//                message.setTimestamp(timestamp);
//
//                // 对密码进行加密
//                String encryptPwd = cHttpPost.encryptPwd(message.getUserid(),pwd, message.getTimestamp());
//                // 设置加密后的密码
//                message.setPwd(encryptPwd);
//
//            }else
//            {
//                // 设置密码
//                message.setPwd(pwd);
//            }
//
//            // 设置手机号码 此处只能设置一个手机号码
//            message.setMobile("159XXXXXXXX");
//            // 设置内容
//            message.setContent("测试短信");
//            // 设置扩展号
//            message.setExno("11");
//            // 用户自定义流水编号
//            message.setCustid("20160929194950100001");
//            // 自定义扩展数据
//            message.setExdata("abcdef");
//            //业务类型
//            message.setSvrtype("SMS001");
//
//            // 返回的平台流水编号等信息
//            StringBuffer msgId = new StringBuffer();
//            // 返回值
//            int result = -310099;
//            // 发送短信
//            result = cHttpPost.singleSend(message, msgId);
//            // result为0:成功;非0:失败
//            if(result == 0)
//            {
//                System.out.println("单条发送提交成功！");
//
//                System.out.println(msgId.toString());
//
//            }
//            else
//            {
//                System.out.println("单条发送提交失败,错误码：" + result);
//            }
//        }
//        catch (Exception e)
//        {
//            //异常处理
//            e.printStackTrace();
//        }
    }

    //人脸识别检测
    public static BaseResult faceDetection(String faceImg)
    {
        // 请求url
        String url = "https://aip.baidubce.com/rest/2.0/face/v3/search";
        try {
            byte[] imgData = FileUtil.readFileByBytes(faceImg);
            String imgStr = Base64Util.encode(imgData);

            Map<String, Object> map = new HashMap<>();
            map.put("image", imgStr);
            map.put("liveness_control", "NORMAL");
            map.put("group_id_list", "1");
            map.put("image_type", "BASE64");
            map.put("quality_control", "LOW");

            String param = GsonUtils.toJson(map);

            // 注意这里仅为了简化编码每一次请求都去获取access_token，线上环境access_token有过期时间， 客户端可自行缓存，过期后重新获取。
            String accessToken = BaiduToken.getAuth(MyConstants.BAIDU_FACE_AK, MyConstants.BAIDU_FACE_SK);

            String result = HttpUtil.post(url, accessToken, "application/json", param);
            System.out.println(result);
            Map<String, Object> jsonMap = JSONObject.parseObject(result);
            int errorcode = (int) jsonMap.get("error_code");
            String errormsg = (String) jsonMap.get("error_msg");
            if (errorcode == 0)
            {
                return BaseResult.ok("人脸识别成功");
            }
            else
            {
                return BaseResult.fail(errormsg);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return BaseResult.fail();
    }

    //人脸注册
    public static BaseResult faceRegister(String faceImg)
    {
        // 请求url
        String url = "https://aip.baidubce.com/rest/2.0/face/v3/faceset/user/add";
        try {
            byte[] imgData = FileUtil.readFileByBytes(faceImg);
            String imgStr = Base64Util.encode(imgData);
//            imgStr = Base64Util.resizeImageTo40K(imgStr);
//            String imgParam = URLEncoder.encode(imgStr, "UTF-8");

            Map<String, Object> map = new HashMap<>();
            map.put("image", imgStr);
            map.put("group_id", "1");
            map.put("user_id", "1");
//            map.put("user_info", "abc");
//            map.put("liveness_control", "NORMAL");
            map.put("image_type", "BASE64");
//            map.put("quality_control", "NONE");

            String param = GsonUtils.toJson(map);

            // 注意这里仅为了简化编码每一次请求都去获取access_token，线上环境access_token有过期时间， 客户端可自行缓存，过期后重新获取。
            String accessToken = BaiduToken.getAuth(MyConstants.BAIDU_FACE_AK, MyConstants.BAIDU_FACE_SK);

            String result = HttpUtil.post(url, accessToken, "application/json", param);
            System.out.println(result);
            Map<String, Object> jsonMap = JSONObject.parseObject(result);
            int errorcode = (int) jsonMap.get("error_code");
            String errormsg = (String) jsonMap.get("error_msg");
            if (errorcode == 0)
            {
                return BaseResult.ok("人脸注册成功");
            }
            else
            {
                return BaseResult.fail(errormsg);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return BaseResult.fail();

    }

    //身份证正面识别
    public static Map<String, Object> identifyIdCardFront(String idCardImg)
    {
        // 请求url
        String url = "https://aip.baidubce.com/rest/2.0/ocr/v1/idcard";
        try {
            // 本地文件路径
            byte[] imgData = FileUtil.readFileByBytes(idCardImg);
            String imgStr = Base64Util.encode(imgData);
            String imgParam = URLEncoder.encode(imgStr, "UTF-8");

            String param = "id_card_side=" + "front" + "&image=" + imgParam + "&detect_photo=" + "true";

            // 注意这里仅为了简化编码每一次请求都去获取access_token，线上环境access_token有过期时间， 客户端可自行缓存，过期后重新获取。
            String accessToken = BaiduToken.getAuth(MyConstants.BAIDU_TEXT_AK, MyConstants.BAIDU_TEXT_SK);

            String result = HttpUtil.post(url, accessToken, param);
            System.out.println(result);
            Map<String, Object> jsonMap = JSONObject.parseObject(result);
            int idcardType = (int)jsonMap.get("idcard_number_type");
            if (idcardType != -1)
            {
                return jsonMap;
            }
            else
            {
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    //身份证反面识别
    public static Map<String, Object> identifyIdCardBack(String idCardImg)
    {
        // 请求url
        String url = "https://aip.baidubce.com/rest/2.0/ocr/v1/idcard";
        try {
            // 本地文件路径
            byte[] imgData = FileUtil.readFileByBytes(idCardImg);
            String imgStr = Base64Util.encode(imgData);
            String imgParam = URLEncoder.encode(imgStr, "UTF-8");

            String param = "id_card_side=" + "back" + "&image=" + imgParam;

            // 注意这里仅为了简化编码每一次请求都去获取access_token，线上环境access_token有过期时间， 客户端可自行缓存，过期后重新获取。
            String accessToken = BaiduToken.getAuth(MyConstants.BAIDU_TEXT_AK, MyConstants.BAIDU_TEXT_SK);

            String result = HttpUtil.post(url, accessToken, param);
            System.out.println(result);
            Map<String, Object> jsonMap = JSONObject.parseObject(result);
            Map<String, Object> word_result = (Map<String, Object>)jsonMap.get("words_result");
            if (!word_result.isEmpty())
            {
                return jsonMap;
            }
            else
            {
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    //人脸比对
    public static Map<String, Object> faceMatch(String face1,String face2) {
        // 请求url
        String url = "https://aip.baidubce.com/rest/2.0/face/v3/match";
        try {
            List<Map<String,Object>> maps = new ArrayList<>();

            Map<String,Object> map1 = new HashMap<>();
            Map<String,Object> map2 = new HashMap<>();
            map1.put("image",face1);
            map1.put("image_type","BASE64");

            map2.put("image",face2);
            map2.put("image_type","BASE64");

            maps.add(map1);
            maps.add(map2);

            System.out.println(maps);

            String param = GsonUtils.toJson(maps);

            System.out.println(param);

            // 注意这里仅为了简化编码每一次请求都去获取access_token，线上环境access_token有过期时间， 客户端可自行缓存，过期后重新获取。
            String accessToken = BaiduToken.getAuth(MyConstants.BAIDU_FACE_AK, MyConstants.BAIDU_FACE_SK);

            String result = HttpUtil.post(url, accessToken, "application/json", param);
            System.out.println(result);
            Map<String, Object> jsonMap = JSONObject.parseObject(result);
            int errorcode = (int) jsonMap.get("error_code");
            String errormsg = (String) jsonMap.get("error_msg");
            if (errorcode == 0)
            {
                return jsonMap;
            }
            else
            {
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


}
