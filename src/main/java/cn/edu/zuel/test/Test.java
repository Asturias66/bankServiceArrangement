package cn.edu.zuel.test;



// 此接口支持发送验证码短信、订单通知短信；
// 调试期间，请使用测试专用短信模板：您的验证码是：1234。请不要把验证码泄露给其他人；
// 请求参数中的account和password分别为 APIID、APIKEY，请在本页面上方处获取。

import java.io.IOException;

import cn.edu.zuel.common.config.MyConstants;
import cn.edu.zuel.kit.apiKit;
import cn.edu.zuel.kit.phoneRegionKit.PhoneNumberInfo;
import cn.edu.zuel.kit.phoneRegionKit.PhoneRegionKit;
import cn.edu.zuel.util.*;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jfinal.kit.HashKit;
import com.jfinal.kit.HttpKit;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.PostMethod;

import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

public class Test {



    public static void main(String [] args) {
//        PhoneRegionKit phoneRegionKit = new PhoneRegionKit();
//        PhoneNumberInfo phoneNumberInfo = phoneRegionKit.lookup("13965277952");
//        System.out.println(phoneNumberInfo);
//        int num = apiKit.sendMobileMessage("13965277952");
//        System.out.println(num);
    }

    public void getSaltAndPwd()
    {
        String salt = HashKit.generateSaltForSha256();
        String pwd = HashKit.sha256(salt + HashKit.md5("e10adc3949ba59abbe56e057f20f883e"));
        System.out.println(salt);
        System.out.println(pwd);
    }


}



