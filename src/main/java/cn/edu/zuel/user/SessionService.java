package cn.edu.zuel.user;

import cn.edu.zuel.kit.BaseResponse;
import cn.edu.zuel.kit.ResultCodeEnum;
import cn.edu.zuel.common.module.Session;
import cn.fabrice.jfinal.service.BaseService;
import cn.fabrice.kit.Kits;
import com.jfinal.kit.Kv;

import java.math.BigInteger;

public class SessionService extends BaseService<Session> {
    Session sessionDao = new Session().dao();
    BaseResponse baseResponse = new BaseResponse();

    public SessionService() {
        super("session.", Session.class, "session");
    }

    public boolean deleteByToken(String token) {
        Kv cond = Kv.by("token", token);
        return delete(cond, "deleteByToken");
    }

    public int deleteByUuid(BigInteger uuid) {
        Kv cond = Kv.by("uuid", uuid);
        return update(cond, "deleteByUuid");
    }

    /**
     * 根据token查询session信息
     *
     * @param token
     * @return
     */

    public BaseResponse getSessionByToken(String token) {
        Session session = sessionDao.findFirst("select * from session where access_token = ? and is_deleted=0", token);
        if (session == null) {
            baseResponse.setResult(ResultCodeEnum.DB_FIND_FAILURE);
        } else {
            baseResponse.setData(session);
            baseResponse.setResult(ResultCodeEnum.DB_FIND_SUCCESS);
        }
        return baseResponse;
    }

    /**
     * 根据accountId获取用户的session信息
     *
     * @param
     * @return
     */
    public BaseResponse getSessionByAccount(long accountId) {
        Session session = sessionDao.findFirst("select * from session where uuid = ? and is_deleted=0", accountId);
        if (session == null) {
            baseResponse.setResult(ResultCodeEnum.DB_FIND_FAILURE);
        } else {
            baseResponse.setData(session);
            baseResponse.setResult(ResultCodeEnum.DB_FIND_SUCCESS);
        }
        return baseResponse;
    }

//    public HashMap add(BigInteger id, String token) {
////        userSession.setUuid(id);
////        userSession.setAccessToken(token);
////        userSession.setExpiresIn(BigInteger.valueOf(30*60));
////        long currentTime = System.currentTimeMillis();
////        currentTime = currentTime / 1000;
////        long expiresTime = currentTime + 30 * 60 * 60;
////        userSession.setExpiresTime(BigInteger.valueOf(System.currentTimeMillis() + expiresTime * 1000));
////        userSession.setAccessToken(Kits.getUuid());
////        return userSession.save();
//    }
//
//    public HashMap dataMap(User user, long uuid, long expiresIn) {
//        Session userSession = new Session();
//        userSession.setUuid(BigInteger.valueOf(uuid));
//        userSession.setExpiresIn(BigInteger.valueOf(expiresIn));
//        //记录过期时间戳（毫秒）
//        userSession.setExpiresTime(BigInteger.valueOf(System.currentTimeMillis() + expiresIn * 1000));
//        userSession.setAccessToken(Kits.getUuid());
//
//        HashMap dataMap = new HashMap<String, Object>();
//        dataMap.put("session", userSession);
//        dataMap.put("user", user);
//        return dataMap;
//    }

    public Session add(long uuid, long expiresIn) {
        Session userSession = new Session();
        userSession.setUuid(BigInteger.valueOf(uuid));
        userSession.setExpiresIn(BigInteger.valueOf(expiresIn));
        //记录过期时间戳（毫秒）
        userSession.setExpiresTime(BigInteger.valueOf(System.currentTimeMillis() + expiresIn * 1000));
        userSession.setAccessToken(Kits.getUuid());

        return userSession.save() ? userSession : null;
    }


}
