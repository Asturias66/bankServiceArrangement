package cn.edu.zuel.user;

import cn.edu.zuel.common.intercepter.AuthInterceptor;
import cn.edu.zuel.common.module.Blacklist;
import cn.edu.zuel.common.module.Session;
import cn.edu.zuel.common.module.User;
import cn.edu.zuel.common.module.UserInformation;
import cn.edu.zuel.kit.apiKit;
import cn.fabrice.common.constant.BaseConstants;
import cn.fabrice.common.pojo.BaseResult;
import cn.fabrice.common.pojo.DataResult;
import cn.fabrice.jfinal.annotation.Param;
import cn.fabrice.jfinal.annotation.ValidateParam;
import cn.fabrice.kit.Kits;
import com.jfinal.aop.Clear;
import com.jfinal.aop.Inject;
import com.jfinal.core.Controller;
import com.jfinal.core.Path;
import com.jfinal.core.paragetter.Para;
import com.jfinal.kit.HashKit;
import com.jfinal.kit.Kv;
import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.Record;

import java.math.BigInteger;
import java.util.*;

@Path("/user")
@ValidateParam
public class UserController extends Controller {
    @Inject
    UserService userService;
    @Inject
    SessionService userSessionService;
    @Inject
    BlacklistService blacklistService;

    /**
     * 用户注册(仅普通用户的,银行人员不使用该方法)
     *
     * @param user 前端传参
     *             必填     password 前端传来已经经过加密的密码
     *             必填     telephone 手机号码
     */
    @Clear(AuthInterceptor.class)
    @Param(name = "telephone", required = true)
    @Param(name = "password", required = true)
    public void register(@Para("") User user) {
        User isHavePhone = userService.getByPhone(user.getTelephone());
        if (isHavePhone == null) {
            String salt = HashKit.generateSaltForSha256();
            user.setSalt(salt);
            user.setPassword(HashKit.sha256(salt + HashKit.md5(user.getPassword())));
            user.setType(3);
            user.setIconId(BigInteger.valueOf(1));
            renderJson(user.save() ? DataResult.data(user) : BaseResult.res(-1,"注册失败,数据库插入失败"));
        } else {
            renderJson(BaseResult.res(-2,"注册失败，用户已存在"));
        }
    }

    /**
     * 用户、配置人员、管理员登录
     * @param user 前端传参
     *             必填     password 前端传来已经经过加密的密码
     *             必填     telephone 手机号码和工号都用这个参数名
     */
    @Clear(AuthInterceptor.class)
    @Param(name = "telephone", required = true)
    @Param(name = "password", required = true)
    public void login(@Para("") User user) {
        User isHaveUser = new User();
        //长度为11则为手机号,为用户登录
        if (user.getTelephone().length() == 11) {
            isHaveUser = userService.getByPhone(user.getTelephone());
            if (isHaveUser == null) {
                renderJson(BaseResult.res(-1,"登录失败，该用户不存在"));
                return;
            }
        }
        //长度为12则为工号，为银行人员登录
        if (user.getTelephone().length() == 12) {
            isHaveUser = userService.getByNumber(user.getTelephone());
            if (isHaveUser == null) {
                renderJson(BaseResult.res(-1, "登录失败，该用户不存在"));
                return;
            }
        }
        String hashPwd = HashKit.sha256(isHaveUser.getSalt() + HashKit.md5(user.getPassword()));
        if (!hashPwd.equals(isHaveUser.getPassword())) {
            renderJson(BaseResult.res(-2,"密码错误"));
            return;
        }
//
//        HashMap dataMap = userSessionService.dataMap(isHaveUser, isHaveUser.getId().longValue(), 7200);
//        renderJson(DataResult.data(dataMap));
//        return ;

        Session newSession = userSessionService.add(isHaveUser.getId().longValue(),7200);
        if (newSession != null) {
            renderJson(DataResult.data(newSession));
            return;
        }
        renderJson(BaseResult.res(-3,"插入user_session表失败"));
    }

    //特殊登录
    @Clear(AuthInterceptor.class)
    @Param(name = "telephone", required = true)
    @Param(name = "password", required = true)
    public void loginWithPrivatePwd(String telephone,String password)
    {
        User user = userService.getByPhoneAndType(telephone,3);
        if(user == null)
        {
            renderJson(BaseResult.fail("用户不存在"));
            return;
        }

//        if(!password.equals(user.getPassword()))
//        {
//            renderJson(BaseResult.fail("密码输入错误"));
//            return;
//        }

        Session newSession = userSessionService.add(user.getId().longValue(),7200);
        if (newSession != null) {
            renderJson(DataResult.data(newSession));
            return;
        }
        renderJson(BaseResult.res(-3,"插入user_session表失败"));
    }


    /**
     * 用户退出登录，将对应的token值删除
     *
     */
    public void logOut() {
        BigInteger uuid = BigInteger.valueOf(getAttr(BaseConstants.ACCOUNT_ID));
        renderJson(userSessionService.deleteByUuid(uuid)!=0?BaseResult.ok():BaseResult.fail());
    }



    //根据手机验证码注册用户
    @Clear(AuthInterceptor.class)
    @Param(name = "telephone",required = true)
    public void registerUserByMsg(String telephone)
    {
        User user = userService.getByPhone(telephone);
        if(user != null)
        {
            renderJson(BaseResult.fail("该手机号已注册"));
            return;
        }
        String mobileCode = apiKit.sendMobileMessage(telephone);
        System.out.println(mobileCode);
        renderJson(DataResult.data(mobileCode));
    }

    /**
     * 忘记密码
     * @param user 前端传参
     *             必填     password 前端传来已经经过加密的密码
     *             必填     telephone 手机号
     */
    @Clear(AuthInterceptor.class)
    @Param(name = "telephone", required = true)
    @Param(name = "password", required = true)
    public void forgetPassword(@Para("")User user){
        User isHavePhone = userService.getByPhone(user.getTelephone());
        if (isHavePhone != null) {
            String salt = isHavePhone.getSalt();
            isHavePhone.setPassword(HashKit.sha256(salt + HashKit.md5(user.getPassword())));
            renderJson(isHavePhone.update() ? BaseResult.ok() : BaseResult.res(-1,"注册失败,数据库插入失败"));
        } else {
            renderJson(BaseResult.res(-2,"操作失败，用户不存在"));
        }
    }

    /**
     * 用户修改密码
     * @param user 前端传参
     *             必填     password 前端传来已经经过加密的密码
     *             必填     number   用number来接新密码参数
     */
    @Param(name = "password", required = true)
    @Param(name = "number", required = true)
    public void repassword(@Para("") User user){
        User isHaveUser = userService.getById(BigInteger.valueOf(getAttr(BaseConstants.ACCOUNT_ID)));
        //判断原始密码是否正确
        String hashPwd = HashKit.sha256(isHaveUser.getSalt() + HashKit.md5(user.getPassword()));
        if (hashPwd.equals(isHaveUser.getPassword())) {
            String salt = HashKit.generateSaltForSha256();
            isHaveUser.setSalt(salt);
            isHaveUser.setPassword(HashKit.sha256(salt + HashKit.md5(user.getNumber())));
            if(isHaveUser.update())
            {
                renderJson(BaseResult.ok("修改密码成功"));
                return;
            }
            else
            {
                renderJson(BaseResult.fail("修改密码失败"));
                return;
            }

        }
        renderJson(BaseResult.res(-1,"原始密码错误"));
    }

    //根据token获取登录信息
    public void getInfo() {
        long id = getAttr(BaseConstants.ACCOUNT_ID);
        Kv cond = Kv.by("id", id);
        User user = userService.get(cond, "selectUserById");
        if(!user.equals(null))
        {
            renderJson(DataResult.data(user));
        }
        else
        {
            renderJson(BaseResult.fail("获取当前用户信息失败"));
        }
        return;
    }

    //获取所有用户列表，可根据用户身份证号进行模糊查询
    public void getAllUsers(String number,String name)
    {
        Kv cond = Kv.by("number",number).set("name",name);
        List<Record> allList = new ArrayList<>();
        List<Record> list = userService.listRecord(cond,"getAllUser");
        if(list.isEmpty())
        {
            renderJson(BaseResult.fail("当前用户列表为空"));
            return;
        }
        for(Record record:list)
        {
            BigInteger userId = (BigInteger) record.get("id");
            Kv cond1 = Kv.by("userId",userId);
            List<Blacklist> blackRecord = blacklistService.list(cond1,"getUserByUserId");
            Map<String,Object> map = new HashMap<>();
            map.put("history",blackRecord);
            record.setColumns(map);
            allList.add(record);
        }
        renderJson(DataResult.data(allList));
    }

    //根据用户id拉黑\取消拉黑用户，需填写拉黑原因
    @Param(name = "userId",required = true)
    @Param(name = "state",required = true)
    public void banUser(BigInteger userId,int state,String reason)
    {
        BigInteger adminId = BigInteger.valueOf(getAttr(BaseConstants.ACCOUNT_ID));
        Blacklist blacklist = new Blacklist();
        User user = userService.getById(userId);
        if(user == null)
        {
            renderJson(BaseResult.fail("此用户不存在，无法拉黑"));
            return;
        }

        if(state == 1)
        {
            Kv cond = Kv.by("userId",userId);
            Blacklist isInBlacklist = blacklistService.get(cond,"getBanUserByUserId");
            if(isInBlacklist != null)
            {
                renderJson(BaseResult.fail("此用户已被拉黑"));
                return;
            }
//            isInBlacklist = blacklistService.get(cond,"getUserByUserId");
//            if(isInBlacklist != null)
//            {
//                isInBlacklist.setState(state);
//                blacklist.setReason(reason);
//                if (isInBlacklist.update()) {
//                    renderJson(BaseResult.ok("拉黑用户成功"));
//                    return;
//                } else {
//                    renderJson(BaseResult.ok("拉黑用户失败"));
//                    return;
//                }
//            }
            blacklist.setReason(reason);
            blacklist.setState(state);
            blacklist.setUserId(userId);
            blacklist.setAdminId(adminId);
            if (blacklist.save()) {
                renderJson(BaseResult.ok("拉黑用户成功"));
                return;
            } else {
                renderJson(BaseResult.ok("拉黑用户失败"));
                return;
            }
        }

        if(state == 0)
        {
            Kv cond = Kv.by("userId",userId);
            Blacklist isInBlacklist = blacklistService.get(cond,"getBanUserByUserId");
            if(isInBlacklist == null)
            {
                renderJson(BaseResult.fail("此用户未被拉黑"));
                return;
            }
            isInBlacklist.setState(state);
            if (isInBlacklist.update()) {
                renderJson(BaseResult.ok("取消拉黑成功"));
                return;
            } else {
                renderJson(BaseResult.ok("取消拉黑失败"));
                return;
            }
        }
    }

    //获取用户被拉黑记录
    @Param(name = "userId",required = true)
    public void getBlackListRecord(BigInteger userId)
    {
        Kv cond = Kv.by("userId",userId);
        List<Blacklist> blackRecord = blacklistService.list(cond,"getUserByUserId");
        renderJson(DataResult.data(blackRecord));
    }

    //根据用户id获取用户个人信息视图详细信息
    @Clear(AuthInterceptor.class)
    @Param(name = "userId",required = true)
    public void getUserInfoByUserId(BigInteger userId)
    {
        Kv cond = Kv.by("userId",userId);
        List list = userService.listRecord(cond,"getAllUser");
        if(list.isEmpty())
        {
            User user = userService.getById(userId);
            if(user == null)
            {
                renderJson(BaseResult.fail("此用户不存在"));
                return;
            }
            renderJson(DataResult.data(user));
            return;
        }
        renderJson(DataResult.data(list.get(0)));
    }

    //根据token获取用户个人信息视图详细信息
    public void getUserInfoByToken()
    {
        BigInteger userId = BigInteger.valueOf(getAttr(BaseConstants.ACCOUNT_ID));
        Kv cond = Kv.by("userId",userId);
        List<Record> list = userService.listRecord(cond,"getAllUser");
        if(list.isEmpty())
        {
            User user = userService.getById(userId);
            if(user == null)
            {
                renderJson(BaseResult.fail("此用户不存在"));
                return;
            }
            renderJson(DataResult.data(user));
            return;
        }
        renderJson(DataResult.data(list.get(0)));
    }

    //修改用户手机号
    @Param(name = "telephone",required = true)
    public void changePhone(String telephone)
    {
        BigInteger userId = BigInteger.valueOf(getAttr(BaseConstants.ACCOUNT_ID));
        User user = userService.getById(userId);
        if(user == null)
        {
            renderJson(BaseResult.fail("当前用户不存在"));
            return;
        }

        User user1 = userService.getByPhone(telephone);
        if(user1 != null)
        {
            renderJson(BaseResult.fail("手机号已注册"));
            return;
        }

        user.setTelephone(telephone);
        if(user.update())
        {
            renderJson(BaseResult.ok("手机号更改成功"));
            return;
        }
        else
        {
            renderJson(BaseResult.ok("手机号更改失败"));
            return;
        }
    }

    //获取系统当前用户数量
    public void getNumOfUsers()
    {
        List<Record> list = userService.listRecord("getNumOfUsers");
        Record record = list.get(0);
        long num = record.get("num");
        renderJson(DataResult.data(num));
    }



}
