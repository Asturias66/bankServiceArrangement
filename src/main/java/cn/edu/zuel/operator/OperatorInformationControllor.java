package cn.edu.zuel.operator;

import cn.edu.zuel.common.module.*;
import cn.edu.zuel.kit.kit;
import cn.edu.zuel.product.ProductDetailService;
import cn.edu.zuel.user.UserService;
import cn.fabrice.common.constant.BaseConstants;
import cn.fabrice.common.pojo.BaseResult;
import cn.fabrice.common.pojo.DataResult;
import cn.fabrice.jfinal.annotation.Param;
import cn.fabrice.jfinal.annotation.ValidateParam;
import com.jfinal.aop.Inject;
import com.jfinal.core.Controller;
import com.jfinal.core.Path;
import com.jfinal.core.paragetter.Para;
import com.jfinal.kit.HashKit;
import com.jfinal.kit.Kv;
import org.codehaus.jackson.map.Serializers;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;

@Path("/operatorInformation")
@ValidateParam
public class OperatorInformationControllor extends Controller {

    @Inject
    UserService userService;

    @Inject
    OperatorInformationService operatorInformationService;

    @Inject
    ProductDetailService productDetailService;

    //判断当前配置人员是否完善个人信息
    public void ifInformation()
    {
        BigInteger userId = BigInteger.valueOf(getAttr(BaseConstants.ACCOUNT_ID));
        User  user = userService.getById(userId);
        if(user.getTelephone() == null)
        {
            renderJson(BaseResult.res(0,"当前配置人员没有完善个人信息"));
            return;
        }
        renderJson(BaseResult.res(1,"当前配置人员已完善个人信息"));
        return;
    }

    //增添或修改配置人员个人信息
    @Param(name = "telephone",required = true)
    public void information (String telephone){
        BigInteger userId = BigInteger.valueOf(getAttr(BaseConstants.ACCOUNT_ID));
        OperatorInformation  isHaveOperator = operatorInformationService.getByUserId(userId);
        User user = userService.getById(userId);
        OperatorInformation operatorInformation = new OperatorInformation();
//        operatorInformation.setName(name);
//        operatorInformation.setPosition(position);
        user.setTelephone(telephone);  //增加手机号码
        //operator_information表中如果没有配置人员id，则为添加
        if (isHaveOperator == null ) {
            renderJson(operatorInformation.save() && user.update()? BaseResult.ok("新增成功") : BaseResult.res(-2,"新增失败,数据库插入失败"));
            return;
        } else { //否则为修改
//            isHaveOperator.setName(name);
//            isHaveOperator.setPosition(position);
            renderJson(user.update()? BaseResult.ok("修改成功"): BaseResult.res(-1,"修改失败"));
            return;
        }
    }

    /**
     * 银行配置人员注册(由管理员为其进行注册)
     *
     *             必填     name 姓名
     *             必填     number 工号
     *             必填     telephone 手机号
     */
    @Param(name = "number", required = true)
    @Param(name = "name", required = true)
    @Param(name = "telephone",required = true)
    public void registerOperator(String number,String name,String telephone) {
        User isHaveNumber = userService.getByNumber(number);
        if (isHaveNumber == null) {
            User user = new User();
            OperatorInformation operatorInformation = new OperatorInformation();
            String salt = HashKit.generateSaltForSha256();
            user.setSalt(salt);
            user.setNumber(number);
            user.setPassword(HashKit.sha256(salt + HashKit.md5("e10adc3949ba59abbe56e057f20f883e")));
            user.setType(2);
            user.setTelephone(telephone);
            if(!user.save())
            {
                renderJson(BaseResult.fail("用户注册失败"));
                return;
            }
            TeamMember teamMember = new TeamMember();
            teamMember.setMember(user.getId());
            teamMember.setTeam(BigInteger.valueOf(0));
            teamMember.setPosition(3);
            if(!teamMember.save())
            {
                renderJson(BaseResult.fail("团队默认添加失败"));
                return;
            }
            operatorInformation.setUserId(user.getId());
            operatorInformation.setName(name);
            renderJson(operatorInformation.save()? BaseResult.ok("注册成功") : BaseResult.fail("注册失败,数据库插入失败"));
            return;
        } else {
            renderJson(BaseResult.fail("注册失败，配置人员已存在"));
            return;
        }
    }

    /**
     * 配置人员修改密码
     * @param user 前端传参
     *             必填     password 前端传来已经经过加密的密码
     *             必填     telephone 用telephone来接新密码参数
     */
    @Param(name = "password", required = true)
    @Param(name = "number", required = true)
    public void repassword(@Para("") User user){
        BigInteger operatorId = BigInteger.valueOf(getAttr(BaseConstants.ACCOUNT_ID));
        User isHaveUser = userService.getById(operatorId);
        //判断原始密码是否正确
        String hashPwd = HashKit.sha256(isHaveUser.getSalt() + HashKit.md5(user.getPassword()));
        if (hashPwd.equals(isHaveUser.getPassword())) {
            String salt = HashKit.generateSaltForSha256();
            isHaveUser.setSalt(salt);
            isHaveUser.setPassword(HashKit.sha256(salt + HashKit.md5(user.getPassword())));
            renderJson(BaseResult.ok("修改密码成功"));
            return;
        }
        renderJson(BaseResult.res(-1,"原始密码错误"));
    }

    //批量导入配置人员
    @Param(name = "arrayJson",required = true)
    public void addBatch(String arrayJson)
    {
        List<Map<String, Object>> list = kit.jsonToList(arrayJson);
        for(Map<String, Object> map:list)
        {
            String name = (String) map.get("name");
            String number = (String) map.get("number");
            String telephone = String.valueOf(map.get("telephone")) ;
            registerOperator(number,name,telephone);
        }

    }

    //获取所有配置人员（可以根据工号、姓名模糊查询）
    public void getAllOperators(String number,String name)
    {
        Kv cond = Kv.by("number",number).set("name",name);
        List list = operatorInformationService.listRecord(cond,"getAllOperator");
        if(list.isEmpty())
        {
            renderJson(BaseResult.fail("当前配置人员列表为空"));
            return;
        }
        renderJson(DataResult.data(list));
    }

    //获取配置人员的数量
    @Param(name = "teamId",required = true)
    public void getOperatorsNum(BigInteger teamId)
    {
        Kv cond = Kv.by("teamId",teamId);
        if(teamId.compareTo(BigInteger.valueOf(1)) == 0)
        {
            cond = Kv.by("teamId",null);
        }
        List list = operatorInformationService.listRecord(cond,"getAllOperator");
        renderJson(DataResult.data(list.size()));
    }

    //删除配置人员
    @Param(name = "userId",required = true)
    public void deleteOperator(BigInteger userId)
    {
        User user = userService.getById(userId);
        OperatorInformation operatorInformation = operatorInformationService.getByUserId(userId);
        if(user == null || operatorInformation == null)
        {
            renderJson(BaseResult.fail("配置人员不存在"));
            return;
        }

        List<ProductDetail> productDetails = productDetailService.getDetailByUserId(userId);

        if(!productDetails.isEmpty())
        {
            renderJson(BaseResult.fail("当前配置人员不能删除,其尚有负责的产品存在，请先更改产品负责人"));
            return;
        }

        user.setIsDeleted(1);
        operatorInformation.setIsDeleted(1);
        if(user.update() && operatorInformation.update())
        {
            renderJson(BaseResult.ok("删除配置人员成功"));
        }
        else
        {
            renderJson(BaseResult.fail("删除配置人员失败"));
        }
    }

    //获取配置人员的信息
    public void getOperatorInfo()
    {
        BigInteger userId = BigInteger.valueOf(getAttr(BaseConstants.ACCOUNT_ID));
        Kv cond = Kv.by("userId",userId);
        User user = userService.getById(userId);
        if(user.getType() == 1)
        {
            renderJson(DataResult.data(user));
            return;
        }
        List list = operatorInformationService.listRecord(cond,"getAllOperator");
        if(list.isEmpty())
        {
            renderJson(BaseResult.fail("此用户个人信息不存在"));
            return;
        }
        renderJson(DataResult.data(list.get(0)));
    }

    //根据userId获取配置人员信息
    @Param(name = "userId",required = true)
    public void getOperatorByUserId(BigInteger userId)
    {
        Kv cond = Kv.by("userId",userId);
        User user = userService.getById(userId);
        if(user.getType() == 1)
        {
            renderJson(DataResult.data(user));
            return;
        }
        List list = operatorInformationService.listRecord(cond,"getAllOperator");
        if(list.isEmpty())
        {
            renderJson(BaseResult.fail("此用户个人信息不存在"));
            return;
        }
        renderJson(DataResult.data(list.get(0)));
    }



}
