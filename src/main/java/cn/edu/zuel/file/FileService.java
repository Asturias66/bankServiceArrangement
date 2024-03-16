package cn.edu.zuel.file;

import cn.edu.zuel.common.module.File;
import cn.fabrice.jfinal.service.BaseService;
import com.jfinal.kit.Kv;
import sun.misc.BASE64Decoder;

import java.io.IOException;
import java.math.BigInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FileService extends BaseService<File> {
    public FileService() {
        // 第一个参数名需要与all.sql中配置的#namespace("user")保持一致
        // 第二个参数是用户类module，最后一个数据库表名
        super("file.", File.class, "file");
    }

    public File getById(BigInteger id) {
        return get(Kv.by("id", id), "getById");
    }


    public byte[] decode(String base64Str){
        byte[] b = null;
        BASE64Decoder decoder = new BASE64Decoder();
        try {
            b = decoder.decodeBuffer(replaceEnter(base64Str));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return b;
    }

    public String replaceEnter(String str){
        String reg ="[\n-\r]";
        Pattern p = Pattern.compile(reg);
        Matcher m = p.matcher(str);
        return m.replaceAll("");
    }
}
