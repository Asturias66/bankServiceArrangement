#sql("getByUserId")
select *
from user_information
where user_id =#para(userId) and is_deleted=0
#end

#sql("getByUserNumber")
select *
from user_information
where number = #para(number) and is_deleted=0
#if(userId)
    and user_id != #para(userId)
#end
#end

#sql("getUserByCardNumber")
select *
from user_information
where card_number = #para(cardNumber) and is_deleted=0
#end