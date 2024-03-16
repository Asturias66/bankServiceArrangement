package cn.edu.zuel.common.module.base;

import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.IBean;

/**
 * Generated by JFinal, do not modify this file.
 */
@SuppressWarnings("serial")
public abstract class BaseSession<M extends BaseSession<M>> extends Model<M> implements IBean {

	/**
	 * 主键 自增主键
	 */
	public void setId(java.math.BigInteger id) {
		set("id", id);
	}
	
	/**
	 * 主键 自增主键
	 */
	public java.math.BigInteger getId() {
		return get("id");
	}
	
	/**
	 * 创建时间 创建时间
	 */
	public void setCreatedTime(java.util.Date createdTime) {
		set("created_time", createdTime);
	}
	
	/**
	 * 创建时间 创建时间
	 */
	public java.util.Date getCreatedTime() {
		return get("created_time");
	}
	
	/**
	 * 更新时间 更新时间
	 */
	public void setUpdatedTime(java.util.Date updatedTime) {
		set("updated_time", updatedTime);
	}
	
	/**
	 * 更新时间 更新时间
	 */
	public java.util.Date getUpdatedTime() {
		return get("updated_time");
	}
	
	/**
	 * 是否删除 0-没有删除；1-已经删除
	 */
	public void setIsDeleted(java.lang.Integer isDeleted) {
		set("is_deleted", isDeleted);
	}
	
	/**
	 * 是否删除 0-没有删除；1-已经删除
	 */
	public java.lang.Integer getIsDeleted() {
		return getInt("is_deleted");
	}
	
	/**
	 * uuid 标识该token所属的对象
	 */
	public void setUuid(java.math.BigInteger uuid) {
		set("uuid", uuid);
	}
	
	/**
	 * uuid 标识该token所属的对象
	 */
	public java.math.BigInteger getUuid() {
		return get("uuid");
	}
	
	/**
	 * access_token 存储用户的token信息
	 */
	public void setAccessToken(java.lang.String accessToken) {
		set("access_token", accessToken);
	}
	
	/**
	 * access_token 存储用户的token信息
	 */
	public java.lang.String getAccessToken() {
		return getStr("access_token");
	}
	
	/**
	 * 有效时间 session有效时间 单位s，0表示永久有效
	 */
	public void setExpiresIn(java.math.BigInteger expiresIn) {
		set("expires_in", expiresIn);
	}
	
	/**
	 * 有效时间 session有效时间 单位s，0表示永久有效
	 */
	public java.math.BigInteger getExpiresIn() {
		return get("expires_in");
	}
	
	/**
	 * 过期时间 session过期时间 0表示永久有效，否则为session过期时间
	 */
	public void setExpiresTime(java.math.BigInteger expiresTime) {
		set("expires_time", expiresTime);
	}
	
	/**
	 * 过期时间 session过期时间 0表示永久有效，否则为session过期时间
	 */
	public java.math.BigInteger getExpiresTime() {
		return get("expires_time");
	}
	
}
