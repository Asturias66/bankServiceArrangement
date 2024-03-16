#sql("getBanUserByUserId")
select *
from Vblacklist
where user_id = #para(userId) and state = 1 and is_deleted = 0
#end

#sql("getUserByUserId")
select *
from Vblacklist
where user_id = #para(userId) and is_deleted = 0
#end

