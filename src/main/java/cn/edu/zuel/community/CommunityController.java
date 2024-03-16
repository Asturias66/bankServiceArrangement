package cn.edu.zuel.community;

import cn.edu.zuel.common.module.Community;
import cn.edu.zuel.common.module.Graph;
import cn.edu.zuel.common.module.GraphLike;
import cn.edu.zuel.flowSheet.GraphService;
import cn.fabrice.common.constant.BaseConstants;
import cn.fabrice.common.pojo.BaseResult;
import cn.fabrice.common.pojo.DataResult;
import cn.fabrice.jfinal.annotation.Param;
import cn.fabrice.jfinal.annotation.ValidateParam;
import com.jfinal.aop.Inject;
import com.jfinal.core.Controller;
import com.jfinal.core.Path;
import com.jfinal.core.paragetter.Para;
import com.jfinal.plugin.activerecord.Record;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

@Path("/community")
@ValidateParam
public class CommunityController extends Controller {
    @Inject
    CommunityService communityService;
    @Inject
    GraphLikeService graphLikeService;
    @Inject
    GraphService graphService;

    /**
     * 上传模板
     *
     * 必填     name      模板名称
     * 必填     jsonChart json流程图内容
     * 必填     content   模板介绍
     * 必填     imgId     模板图片主键
     */
    @Param(name = "productId", required = true)
    @Param(name = "name", required = true)
    @Param(name = "content", required = true)
    @Param(name = "imgId",required = true)
    public void uploadMould(BigInteger productId, String name, String content,BigInteger imgId) {
        if (!communityService.getAll(name,null,null).isEmpty()) {
            renderJson(BaseResult.res(-2, "模板市场已存在该名称模板"));
            return;
        }
        Community community = new Community();
        Graph graph = graphService.getByProductId(productId);
        if (graph == null) {
            renderJson(BaseResult.res(-1, "没有内容进行上传"));
            return;
        }
        BigInteger userId = BigInteger.valueOf(getAttr(BaseConstants.ACCOUNT_ID));
        community.setUserId(userId);
        community.setName(name);
        community.setContent(content);
        community.setJsonChart(graph.getJsonChart());
        community.setImg(graph.getImg());
        community.setImgId(imgId);
        int coverRandom = (int)(436+Math.random()*(440-436+1));
        community.setCover(BigInteger.valueOf(coverRandom));
        renderJson(community.save() ? BaseResult.ok("上传成功") : BaseResult.fail("上传失败，数据库插入失败"));

    }

    /**
     * 删除模板
     *
     * 必填     id      模板主键
     */
    @Param(name = "id", required = true)
    public void deleteMould(@Para("") Community community) {
        Community isHaveMould = communityService.getById(community.getId());
        if (isHaveMould == null) {
            renderJson(BaseResult.res(-1, "该模板不存在"));
            return;
        }
        isHaveMould.setIsDeleted(1);
        renderJson(isHaveMould.update() ? BaseResult.ok("删除成功") : BaseResult.res(-2, "删除失败"));

    }

    /**
     * 显示当前用户发布的所有模板
     */
    public void showPushMould() {
        BigInteger userId = BigInteger.valueOf(getAttr(BaseConstants.ACCOUNT_ID));
        List<Community> mouldList = communityService.showPushMould(userId);
        List<Record> moulds = new ArrayList<>();
        for (int i = 0; i < mouldList.size(); i++) {
            BigInteger graphId = mouldList.get(i).getBigInteger("id");
            GraphLike graphLike = graphLikeService.getGraph(userId, graphId, 0, 1);
            moulds.add(mouldList.get(i).toRecord());
            if (graphLike != null) {
                moulds.get(i).set("liked", 1);
            } else {
                moulds.get(i).set("liked", 0);
            }
            graphLike = graphLikeService.getGraph(userId, graphId, 1,1);
            if (graphLike != null) {
                moulds.get(i).set("collected", 1);
            } else {
                moulds.get(i).set("collected", 0);
            }
        }
        renderJson(DataResult.data(moulds));
    }

    /**
     * 展示当前用户收藏的所有模板
     */
    public void showCollectMould() {
        BigInteger userId = BigInteger.valueOf(getAttr(BaseConstants.ACCOUNT_ID));
        List<Record> moulds = communityService.showCollectMould(userId);
        for (int i = 0; i < moulds.size(); i++) {
            BigInteger graphId = moulds.get(i).getBigInteger("id");
            GraphLike graphLike = graphLikeService.getGraph(userId, graphId, 0, 1);
            if (graphLike != null) {
                moulds.get(i).set("liked", 1);
            } else {
                moulds.get(i).set("liked", 0);
            }
            graphLike = graphLikeService.getGraph(userId, graphId, 1,1);
            if (graphLike != null) {
                moulds.get(i).set("collected", 1);
            } else {
                moulds.get(i).set("collected", 0);
            }
        }

        renderJson(DataResult.data(moulds));
    }

    /**
     * 展示模板
     *
     * name 模板名称
     * id   模板主键
     *
     * 展示所有模板，则name置空，id置空
     * 根据模板名称模糊搜索，则name不为空，id置空
     * 查看具体模板信息，则name置空，id不为空
     */
    @Param(name = "name")
    @Param(name = "id")
    @Param(name = "praise")
    public void showMould(@Para("") Community community) {
        BigInteger userId = BigInteger.valueOf(getAttr(BaseConstants.ACCOUNT_ID));
        List<Record> moulds = communityService.getAll(community.getName(),community.getId(),community.getPraise());
        for (int i = 0; i < moulds.size(); i++) {
            BigInteger graphId = moulds.get(i).getBigInteger("id");
            GraphLike graphLike = graphLikeService.getGraph(userId, graphId, 0, 1);
            if (graphLike != null) {
                moulds.get(i).set("liked", 1);
            } else {
                moulds.get(i).set("liked", 0);
            }
            graphLike = graphLikeService.getGraph(userId, graphId, 1,1);
            if (graphLike != null) {
                moulds.get(i).set("collected", 1);
            } else {
                moulds.get(i).set("collected", 0);
            }
        }

        renderJson(DataResult.data(moulds));
    }

    /**
     * 收藏或点赞模板
     * 收藏或点赞（取消）模板
     *
     *必填    graphId  操作对象——模板
     *必填    type     区分点赞和收藏，0-点赞；1-收藏
     */
    @Param(name = "graphId", required = true)
    @Param(name = "type", required = true)
    public void collectOrLikeMould(@Para("") GraphLike graphLike) {
        Community community = communityService.getById(graphLike.getGraphId());
        if (community==null){
            renderJson(BaseResult.res(-2,"没有该模板"));
            return;
        }
        //在graph_like表中添加收藏、点赞记录
        BigInteger userId = BigInteger.valueOf(getAttr(BaseConstants.ACCOUNT_ID));
        GraphLike graph = graphLikeService.getGraph(userId, graphLike.getGraphId(), graphLike.getType(),null);
        int judge = 0;
        if (graph == null) {
            graphLike.setUserId(userId);
            renderJson(graphLike.save() ? BaseResult.ok("操作(点赞或收藏)成功") : BaseResult.res(-1, "操作失败"));
        } else {
            judge = graph.getIsDeleted();
            if (judge == 1) {
                judge = 0;
            } else {
                judge = 1;
            }
            graph.setIsDeleted(judge);
            renderJson(graph.update() ? BaseResult.ok("操作成功") : BaseResult.res(-1, "操作失败"));
        }

        //在community表中update对应模板的收藏、点赞量
        int type = graphLike.getType();
        if (type == 0) {
            //点赞
            if (judge == 1) {
                community.setPraise(community.getPraise() - 1);
                renderJson(community.update() ? BaseResult.res(1000, "取消点赞") : BaseResult.res(-2, "点赞失败"));
            } else {
                community.setPraise(community.getPraise() + 1);
                renderJson(community.update() ? BaseResult.res(1000, "点赞成功") : BaseResult.res(-2, "点赞失败"));
            }
            return;
        } else if (type == 1) {
            //收藏
            if (judge == 1) {
                community.setCollect(community.getCollect() - 1);
                renderJson(community.update() ? BaseResult.res(1000, "成功取消收藏") : BaseResult.res(-2, "收藏失败"));
            } else {
                community.setCollect(community.getCollect() + 1);
                renderJson(community.update() ? BaseResult.res(1000, "收藏成功") : BaseResult.res(-2, "收藏失败"));
            }
            return;
        }
        renderJson(BaseResult.fail("type未声明"));
    }

}
