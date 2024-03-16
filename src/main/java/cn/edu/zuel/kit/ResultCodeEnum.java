package cn.edu.zuel.kit;

public enum ResultCodeEnum {
    DB_FIND_SUCCESS("1","查找成功"),
    DB_FIND_FAILURE("2","查找失败"),
    DB_UPDATE_SUCCESS("3","修改数据成功"),
    DB_UPDATE_FAILURE("4","修改数据失败"),
    DB_DELETE_FAILURE("5","数据删除失败"),
    DB_DELETE_SUCCESS("6","数据删除成功"),
    DB_ADD_SUCCESS("7","添加成功"),
    DB_ADD_FAILURE("8","添加失败"),

    DB_LOGIN_FAILUR("1001","密码错误"),
    DB_lOGIN_SUCCESS("1002","登录成功"),
    TOKEN_UNFILL("1003","token非法"),
    TOKEN_FAIL("1004","token失效"),
    LOGIN_UNFINISHED("1005","登录过期,请重新登录"),
    DB_USER_EXIST("1006","用户已存在"),
    INFORMATION_NOT_COMPLETE("1007","注册信息不完整"),
    LOGIN_NOT_COMPLETE("1008","登录信息不完整"),
    DB_REGISTER_SUCCESS("1009","用户注册成功"),
    ACTION_SUCCESS("200","操作成功"),

    MANAGER_LOGIN_FAILURE("3001","使用管理员登录zabbix失败"),
    ADD_HOSTGROUP_FAILURE("3002","添加主机群组失败"),
    ADD_USERGROUP_FAILURE("3003","添加用户群组失败"),
    ADD_USER_FAILURE("3004","添加用户失败"),
    USER_LOGIN_FAILURE("3005","用户登录zabbix失败"),
    ADD_HOST_FAILURE("3006","添加主机失败"),
    HOSTNUMBER_FAILURE("3007","添加主机失败，已达该用户所监控主机上限")
    ;

    //DB_FIND_SUCCESS("2000","数据库查找成功"),
    //DB_FIND_FAILURE("2001","数据库查找失败，没有该条记录"),
    //PARA_BLANK_FAILURE("2002","输入数据为空"),
    //DB_UPDATE_SUCCESS("2003","数据更新成功"),
    //DB_UPDATE_FAILURE("2004","数据更新失败"),
    //DB_DELETE_SUCCESS("2005","数据删除成功"),
    //DB_DELETE_FAILURE("2006","数据删除失败"),
    //DB_ADD_SUCCESS("2007","数据增加成功"),
    //DB_ADD_FAILURE("2008","数据增加失败");

    private String code;
    private String desc;
    ResultCodeEnum(String code, String desc)
    {
        this.code = code;
        this.desc = desc;
    }
    public String getCode()
    {
        return code;
    }
    public String getDesc()
    {
        return desc;
    }
}
