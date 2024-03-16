#sql("getByUserId")
select *
from operator_information
where user_id =#para(userId) and is_deleted=0
#end

#sql("getAllOperator")
select *
from VoperatorInfo
where id!=0
#if(number)
    and number like '%#(number)%'
#end
#if(name)
    and name like '%#(name)%'
#end
#if(userId)
    and user_id = #para(userId)
#end
#if(teamId)
    and team = #para(teamId)
#end
#end
