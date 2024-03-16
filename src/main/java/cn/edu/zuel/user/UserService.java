package cn.edu.zuel.user;

import cn.edu.zuel.common.module.User;
import cn.edu.zuel.common.module.UserInformation;
import cn.fabrice.jfinal.service.BaseService;
import com.jfinal.kit.Kv;

import java.math.BigInteger;

public class UserService extends BaseService<User> {
    public UserService() {
        // 第一个参数名需要与all.sql中配置的#namespace("user")保持一致
        // 第二个参数是用户类module，最后一个数据库表名
        super("user.", User.class, "user");
    }

    public User getById(BigInteger id) {
        Kv cond = Kv.by("id", id);
        return get(cond, "getById");
    }

    public User getByNumber(String number) {
        Kv cond = Kv.by("number", number);
        return get(cond, "getByNumber");
      }

    public  User getByPhone(String telephone){
        Kv cond = Kv.by("telephone", telephone);
        return get(cond, "getByPhone");
    }

    public  User getByPhoneAndType(String telephone,int type){
        Kv cond = Kv.by("telephone", telephone).set("type",type);
        return get(cond, "getByPhone");
    }

}
