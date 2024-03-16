package cn.edu.zuel.common.module.base;

import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.IBean;

/**
 * Generated by JFinal, do not modify this file.
 */
@SuppressWarnings("serial")
public abstract class BaseCommunity<M extends BaseCommunity<M>> extends Model<M> implements IBean {

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
	 * 配置人员id 发布模板的配置人员id
	 */
	public void setUserId(java.math.BigInteger userId) {
		set("user_id", userId);
	}
	
	/**
	 * 配置人员id 发布模板的配置人员id
	 */
	public java.math.BigInteger getUserId() {
		return get("user_id");
	}
	
	/**
	 * json数据 储存前端传来的流程图json数据
	 */
	public void setJsonChart(java.lang.String jsonChart) {
		set("json_chart", jsonChart);
	}
	
	/**
	 * json数据 储存前端传来的流程图json数据
	 */
	public java.lang.String getJsonChart() {
		return getStr("json_chart");
	}
	
	/**
	 * 模板名称 模板名称
	 */
	public void setName(java.lang.String name) {
		set("name", name);
	}
	
	/**
	 * 模板名称 模板名称
	 */
	public java.lang.String getName() {
		return getStr("name");
	}
	
	/**
	 * 模板介绍 模板介绍即产品介绍
	 */
	public void setContent(java.lang.String content) {
		set("content", content);
	}
	
	/**
	 * 模板介绍 模板介绍即产品介绍
	 */
	public java.lang.String getContent() {
		return getStr("content");
	}
	
	/**
	 * 图片主键
	 */
	public void setImgId(java.math.BigInteger imgId) {
		set("img_id", imgId);
	}
	
	/**
	 * 图片主键
	 */
	public java.math.BigInteger getImgId() {
		return get("img_id");
	}
	
	/**
	 * 图片base64数据方式
	 */
	public void setImg(java.lang.String img) {
		set("img", img);
	}
	
	/**
	 * 图片base64数据方式
	 */
	public java.lang.String getImg() {
		return getStr("img");
	}
	
	/**
	 * 点赞量 该模板被点赞的次数，取消点赞会减1
	 */
	public void setPraise(java.lang.Integer praise) {
		set("praise", praise);
	}
	
	/**
	 * 点赞量 该模板被点赞的次数，取消点赞会减1
	 */
	public java.lang.Integer getPraise() {
		return getInt("praise");
	}
	
	/**
	 * 收藏量 该模板被收藏的次数，取消收藏会减1
	 */
	public void setCollect(java.lang.Integer collect) {
		set("collect", collect);
	}
	
	/**
	 * 收藏量 该模板被收藏的次数，取消收藏会减1
	 */
	public java.lang.Integer getCollect() {
		return getInt("collect");
	}
	
	/**
	 * 模板封面图片 
	 */
	public void setCover(java.math.BigInteger cover) {
		set("cover", cover);
	}
	
	/**
	 * 模板封面图片 
	 */
	public java.math.BigInteger getCover() {
		return get("cover");
	}
	
}
