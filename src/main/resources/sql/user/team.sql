#sql("getTeam")
select *
from Vteam
where is_deleted = 0
#if(name)
    and name like '%#(name)%'
#end
#if(leaderId)
    and leader = #para(leaderId)
#end
#if(teamId)
    and id = #para(teamId)
#end
#end

#sql("getTeamTable")
select *
from team
where is_deleted = 0
#if(name)
    and name like '%#(name)%'
#end
#if(leaderId)
    and leader = #para(leaderId)
#end
#if(teamId)
    and id = #para(teamId)
#end
#end