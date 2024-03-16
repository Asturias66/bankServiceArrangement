package cn.edu.zuel.common.module.base;

import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.IBean;

/**
 * Generated by JFinal, do not modify this file.
 */
@SuppressWarnings("serial")
public abstract class BaseTeamMember<M extends BaseTeamMember<M>> extends Model<M> implements IBean {

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
	 * 所属团队主键
	 */
	public void setTeam(java.math.BigInteger team) {
		set("team", team);
	}
	
	/**
	 * 所属团队主键
	 */
	public java.math.BigInteger getTeam() {
		return get("team");
	}
	
	/**
	 * 配置人员主键
	 */
	public void setMember(java.math.BigInteger member) {
		set("member", member);
	}
	
	/**
	 * 配置人员主键
	 */
	public java.math.BigInteger getMember() {
		return get("member");
	}
	
	/**
	 * 配置人员职位 1-产品开发部部长；2-产品组长；3-组员
	 */
	public void setPosition(java.lang.Integer position) {
		set("position", position);
	}
	
	/**
	 * 配置人员职位 1-产品开发部部长；2-产品组长；3-组员
	 */
	public java.lang.Integer getPosition() {
		return getInt("position");
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
	
}
