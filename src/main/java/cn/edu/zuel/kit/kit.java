package cn.edu.zuel.kit;



import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.PostMethod;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import cn.edu.zuel.util.StringUtil;

public class kit {
    //将字符串格式的日期转化为date格式
    public static Date stringToDate(String time)
    {
        SimpleDateFormat form = new SimpleDateFormat("yyyyMMdd");
        Date date = null;
        try {
            date = form.parse(time);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    //获取month个月份以前月份的第一天(month为负数，表示多少个月之前)
    public static String getMonthDateBefore(int beforeMonth)
    {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH, beforeMonth);    //得到前一个月
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH)+1;
        String date = "" + year + "-" + month + "-01";
        return date;
    }

    //获取day个日子以前的日期(day为负数，表示多少个day之前)
    public static String getDayBefore(int day)
    {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, day);    //得到day天
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH)+1;
        int date = calendar.get(Calendar.DATE);
        String date1 = "" + year + "-" + month + "-" + date;
//        Date date = calendar.getTime();
        return date1;
    }

    //获取month个月份以后今天的日期
    public static Date getMonthDate(Date startDate,int month){
        LocalDateTime localDateTime = startDate.toInstant()
                .atZone(ZoneId.systemDefault() )
                .toLocalDateTime().plusMonths(month);
        Date date = Date.from(localDateTime.atZone( ZoneId.systemDefault()).toInstant());
        return date;
    }

    //将json字符串解析转换为列表
    public static List<Map<String, Object>> jsonToList(String jsonString)
    {
        String json = (String) jsonString.subSequence(1,jsonString.length()-2);
        System.out.println(json);
        String[] jsonArray = json.split("},");
        System.out.println(jsonArray.length);
        List<Map<String, Object>> list = new ArrayList<>();
        for(int i=0;i<jsonArray.length;i++)
        {
            jsonArray[i] = jsonArray[i]+"}";
            System.out.println(jsonArray[i]);
            Map<String, Object> map = JSONObject.parseObject(jsonArray[i]);
            System.out.println(map);
            list.add(map);
        }
        return list;
    }


}
