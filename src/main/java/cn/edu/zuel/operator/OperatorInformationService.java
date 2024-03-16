package cn.edu.zuel.operator;

import cn.edu.zuel.common.module.OperatorInformation;
import cn.edu.zuel.common.module.UserInformation;
import cn.fabrice.jfinal.service.BaseService;
import com.jfinal.kit.Kv;

import java.math.BigInteger;

public class OperatorInformationService extends BaseService<OperatorInformation> {
    public OperatorInformationService() {
      // 第一个参数名需要与all.sql中配置的#namespace("Operator")保持一致
      // 第二个参数是用户类module，最后一个数据库表名
      super("operatorInformation.", OperatorInformation.class, "operatorInformation");
    }


    public OperatorInformation getByUserId(BigInteger userId) {
      Kv cond = Kv.by("userId", userId);
      return get(cond, "getByUserId");
    }
}
