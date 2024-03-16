#sql("getTeamMemberByView")
select *
from VteamMember
where is_deleted = 0 and position = 3
#if(userName)
    and user_name like '%#(userName)%'
#end
#if(teamId)
    and team = #para(teamId)
#end
#if(userId)
    and member = #para(userId)
#end
#if(number)
    and number like '%#(number)%'
#end
#end

#sql("getTeamMember")
select *
from team_member
where is_deleted = 0
#if(teamId)
    and team = #para(teamId)
#end
#if(userId)
    and member = #para(userId)
#end
#end

#sql("getLeaderCandidate")
select *
from VteamMember
where is_deleted = 0
and (team = 0 or team = #para(teamId)) and position = 3
#end

#sql("getLeaderInfo")
select *
from VteamMember
where is_deleted = 0
and team = #para(teamId) and position != 3
#end