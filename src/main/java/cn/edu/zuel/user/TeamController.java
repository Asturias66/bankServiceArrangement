package cn.edu.zuel.user;

import cn.edu.zuel.common.module.Team;
import cn.edu.zuel.common.module.TeamMember;
import cn.fabrice.common.pojo.BaseResult;
import cn.fabrice.common.pojo.DataResult;
import cn.fabrice.jfinal.annotation.Param;
import cn.fabrice.jfinal.annotation.ValidateParam;
import com.jfinal.aop.Inject;
import com.jfinal.core.Controller;
import com.jfinal.core.Path;
import com.jfinal.kit.Kv;
import com.jfinal.plugin.activerecord.Record;

import java.math.BigInteger;
import java.util.List;

@Path("/team")
@ValidateParam
public class TeamController extends Controller {
    @Inject
    TeamService teamService;

    @Inject
    TeamMemberService teamMemberService;

    //添加团队
    @Param(name = "name",required = true)
    @Param(name = "leaderId",required = true)
    public void addTeam(String name, BigInteger leaderId)
    {
        Kv cond = Kv.by("name",name);
        Team teamExisted = teamService.get(cond,"getTeam");
        if(teamExisted != null)
        {
            renderJson(BaseResult.fail("该团队名称已经存在"));
            return;
        }

//        cond = Kv.by("leaderId",leaderId);
//        Team leaderExisted = teamService.get(cond,"getTeam");
//        if(leaderExisted != null)
//        {
//            renderJson(BaseResult.fail("该团队负责人已存在"));
//            return;
//        }

        Team team = new Team();

        team.setName(name);
        team.setLeader(leaderId);
        if(!team.save())
        {
            renderJson(BaseResult.fail("新增团队失败"));
            return;
        }

        cond = Kv.by("userId",leaderId);
        TeamMember teamMember = teamMemberService.get(cond,"getTeamMember");
        teamMember.setPosition(2);
        teamMember.setTeam(team.getId());

        if(!teamMember.update())
        {
            renderJson(BaseResult.fail("新增团队失败"));
            return;
        }

        renderJson(BaseResult.ok("新增团队成功"));
    }

    //查看所有团队(可根据团队名称进行查找)
    @Param(name = "name")
    public void getAllTeam(String name)
    {
        Kv cond = Kv.by("name",name);
        List teams = teamService.listRecord(cond,"getTeam");
        renderJson(DataResult.data(teams));
    }

    //删除团队
    @Param(name = "teamId",required = true)
    public void deleteTeam(BigInteger teamId)
    {
        Kv cond = Kv.by("teamId",teamId);
        Team team = teamService.get(cond,"getTeam");

        List<TeamMember> list = teamMemberService.list(cond,"getTeamMember");
        for(TeamMember teamMember:list)
        {
            teamMember.setTeam(BigInteger.valueOf(0));
            teamMember.setPosition(3);
            teamMember.update();
        }
        team.setIsDeleted(1);
        team.update();
        renderJson(BaseResult.ok("删除团队成功"));
    }

    //查看某个团队的所有成员
    @Param(name = "teamId",required = true)
    @Param(name = "userName")
    @Param(name = "number")
    public void getAllMemberByTeamId(BigInteger teamId,String userName,String number)
    {
        if(teamId.compareTo(BigInteger.valueOf(1)) == 0)
        {
            Kv cond = Kv.by("teamId",null).set("userName",userName).set("number",number);
            List records = teamMemberService.listRecord(cond,"getTeamMemberByView");
            renderJson(DataResult.data(records));
            return;
        }
        Kv cond = Kv.by("teamId",teamId).set("userName",userName).set("number",number);
        List records = teamMemberService.listRecord(cond,"getTeamMemberByView");
        renderJson(DataResult.data(records));
    }

    //获取所有未加入团队的配置人员
    public void getAllTeamMemberAvailable()
    {
        Kv cond = Kv.by("teamId",0);
        List records = teamMemberService.listRecord(cond,"getTeamMemberByView");
        renderJson(DataResult.data(records));
    }

    //添加团队成员
    @Param(name = "teamId",required = true)
    @Param(name = "userString",required = true)
    public void addTeamMember(BigInteger teamId, String userString)
    {
        String json = (String) userString.subSequence(1,userString.length()-1);
        String[] array = json.split(",");
        for(int i=0;i<array.length;i++)
        {
            String userId1 =  array[i];
            BigInteger userId = BigInteger.valueOf(Integer.parseInt(userId1));
            Kv cond = Kv.by("userId",userId);
            TeamMember teamMember = teamMemberService.get(cond,"getTeamMember");
            if(teamMember == null)
            {
                renderJson(BaseResult.fail("此配置人员不存在"));
                return;
            }
            teamMember.setTeam(teamId);
            teamMember.update();
        }
        renderJson(BaseResult.ok("新增团队成员成功"));
    }

    //删除团队成员
    @Param(name = "userId",required = true)
    public void deleteTeamMember(BigInteger userId)
    {
        Kv cond = Kv.by("userId",userId);
        TeamMember teamMember = teamMemberService.get(cond,"getTeamMember");
        if(teamMember == null)
        {
            renderJson(BaseResult.fail("此配置人员不存在"));
            return;
        }
        teamMember.setTeam(BigInteger.valueOf(0));
        teamMember.setPosition(3);
        if(!teamMember.update())
        {
            renderJson(BaseResult.fail("删除失败"));
            return;
        }
        renderJson(BaseResult.ok("成功删除"));
    }

    //更换团队负责人
    @Param(name = "userId",required = true)
    @Param(name = "teamId",required = true)
    public void changeLeader(BigInteger userId,BigInteger teamId)
    {
        Kv cond = Kv.by("userId",userId);
        TeamMember teamMember = teamMemberService.get(cond,"getTeamMember");
        if(teamMember == null || BigInteger.valueOf(0).compareTo(teamMember.getTeam()) != 0)
        {
            renderJson(BaseResult.fail("此配置人员不存在或已有所属团队"));
            return;
        }

        cond = Kv.by("teamId",teamId);
        Team team = teamService.get(cond,"getTeamTable");
        if(team == null)
        {
            renderJson(BaseResult.fail("此团队不存在"));
            return;
        }

        Kv cond1 = Kv.by("userId",team.getLeader());
        TeamMember teamMember1 = teamMemberService.get(cond1,"getTeamMember");
        teamMember1.setTeam(BigInteger.valueOf(0));
        teamMember1.setPosition(3);

        team.setLeader(userId);
        teamMember.setPosition(2);
        teamMember.setTeam(teamId);

        if(!team.update() || !teamMember.update() || !teamMember1.update() )
        {
            renderJson(BaseResult.fail("更新团队负责人失败"));
            return;
        }
        renderJson(BaseResult.ok("更新团队负责人成功"));
        return;
    }

    //查看单个团队信息
    @Param(name = "teamId",required = true)
    public void getTeamInfo(BigInteger teamId)
    {
        Kv cond = Kv.by("teamId",teamId);
        List list = teamMemberService.listRecord(cond,"getLeaderInfo");
        renderJson(DataResult.data(list));
    }

    //获取团队负责人候选人
    @Param(name = "teamId",required = true)
    public void getLeaderCandidate(BigInteger teamId)
    {
        Kv cond = Kv.by("teamId",teamId);
        List list = teamMemberService.listRecord(cond,"getLeaderCandidate");
        renderJson(DataResult.data(list));

    }

    //获取小组人数
    @Param(name = "teamId",required = true)
    public void getMemberNumOfTeam(BigInteger teamId)
    {
        Kv cond = Kv.by("teamId",teamId);
        List<Record> list = teamService.listRecord(cond,"getTeam");
        Record record = list.get(0);
        long num = record.get("member_num");
        renderJson(DataResult.data(num));
    }


}
