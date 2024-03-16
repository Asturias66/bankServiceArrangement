#sql("getProductUserByUserIdAndProductId")
select *
    from product_user
        where user_id = #para(userId) and product_id = #para(productId) and amount = #para(amount) and type = 0 and is_deleted=0
#end

#sql("getProductUserByUserIdAndNumber")
select *
    from product_user
        where user_id = #para(userId) and number = #para(number) and amount = #para(amount) and type = 0 and is_deleted=0
#end

#sql("getProductUserById")
select *
    from product_user
        where id = #para(itemId) and type = 0 and is_deleted=0
#end

#sql("getUserDeposit")
select sum(amount) as num
from VdepositRecord
where type = 1 and is_deleted = 0
#if(type)
    and product_type = #para(type)
#end
#if(userId)
    and user_id = #para(userId)
#end
#if(productId)
    and product_id = #para(productId)
#end
#if(endTime)
    and #para(startTime) < created_time < #para(endTime)
#end
#end

#sql("getAllDepositRecord")
select *
from VdepositRecord
where type = 1 and is_deleted = 0
#if(userId)
    and user_id = #para(userId)
#end
#if(productId)
    and product_id = #para(productId)
#end
#if(status)
    and status = #para(status)
#end
#end

#sql("getSalesCount")
select count(*) as num
from VdepositRecord
where type = 1 and is_deleted = 0
#if(type)
    and product_type = #para(type)
#end
#if(userId)
    and user_id = #para(userId)
#end
#if(productId)
    and product_id = #para(productId)
#end
#end

#sql("getProductMonthSale")
select sum(amount) as total
from VdepositRecord
where type = 1 and is_deleted = 0
#if(productId)
    and product_id = #para(productId)
#end
#if(currMonth)
    and concat(year(updated_time),'-',month(updated_time)) = concat(year(#para(currMonth)),'-',month(#para(currMonth)))
#end
#end

#sql("getProductMonthPerson")
select COUNT(user_id) as total
from VdepositRecord
where type = 1 and is_deleted = 0
#if(productId)
    and product_id = #para(productId)
#end
#if(currMonth)
    and concat(year(updated_time),'-',month(updated_time)) = concat(year(#para(currMonth)),'-',month(#para(currMonth)))
#end
#end

#sql("getProductDailySale")
select sum(amount) as total
from VdepositRecord
where type = 1 and is_deleted = 0
#if(productId)
    and product_id = #para(productId)
#end
#if(currDate)
    and concat(year(updated_time),'-',month(updated_time),'-',dayofmonth(updated_time)) = concat(year(#para(currDate)),'-',month(#para(currDate)),'-',dayofmonth(#para(currDate)))
#end
#if(teamId)
    and product_id in (select id from Vproduct where team_id = #para(teamId))
#end
#end

#sql("getRecordUser")
select distinct user_id
from VdepositRecord
where is_deleted = 0
#if(productId)
    and product_id = #para(productId)
#end
#end

