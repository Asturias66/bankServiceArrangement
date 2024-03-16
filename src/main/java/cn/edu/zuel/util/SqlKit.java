package cn.edu.zuel.util;

import com.jfinal.kit.Kv;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.SqlPara;

import java.util.List;

public class SqlKit {
    /**
     * 将 id 列表 join 起来，用逗号分隔，并且用小括号括起来
     */
    public static void joinIds(List<String> idList, StringBuilder ret) {
        ret.append("(");
        boolean isFirst = true;
        for (String id : idList) {
            if (isFirst) {
                isFirst = false;
            } else {
                ret.append(", ");
            }
            ret.append(id);
        }
        ret.append(")");
    }

    public static Kv page(String sqlParaKey, Kv cond, int pageNumber, int pageSize) {
        SqlPara sqlPara= Db.getSqlPara(sqlParaKey, cond);
        Page<Record> data= Db.paginate(pageNumber, pageSize, sqlPara);
        Kv kv=Kv.by("rows", data.getList()).set("totalRecords",data.getTotalRow());
        return kv;
    }

    public static Kv list(String sqlParaKey, Kv cond) {
        SqlPara sqlPara= Db.getSqlPara(sqlParaKey, cond);
        List<Record> data= Db.find(sqlPara);
        Kv kv=Kv.by("rows", data);
        return kv;
    }

    public static Record get(String sqlParaKey, Kv cond) {
        SqlPara sqlPara= Db.getSqlPara(sqlParaKey, cond);
        Record data= Db.findFirst(sqlPara);
        return data;
    }
}
