#sql("showProductHavePublished")
select *
from Vproduct
where date(publish_day) <= DATE(NOW()) and is_deleted=0 and status=2
#end

#sql("showProductWaitPublishing")
select *
from Vproduct
where date(publish_day) > DATE(NOW()) and is_deleted=0 and status=2
#if(teamId)
    and user_id in (select member from VteamMember where team = #para(teamId))
#end
#end

#sql("getDetailByStatus")
select *
    from Vproduct
        where is_deleted=0
        #if(status)
            and status = #para(status)
        #end
        #if(teamId)
            and user_id in (select member from VteamMember where team = #para(teamId))
        #end
        order by created_time DESC
#end

#sql("getDetailByUserId")
select *
    from product_detail
        where user_id=#para(userId) and is_deleted=0
        order by created_time DESC
#end

#sql("getDetailByStatusAndUserId")
select *
from product_detail
where status=#para(status) and is_deleted=0  and user_id=#para(userId)
order by created_time DESC
    #end

#sql("getDetailById")
select  *
    from product_detail
        where id=#para(id) and is_deleted=0
#end

#sql("getDetailViewById")
select  *
    from Vproduct
        where id=#para(id) and is_deleted=0
#end

#sql("getDetailByNumber")
select  id, user_id, created_time, updated_time, is_deleted, number, name, introduction, term, annual_rate, initial_money, increase_money, personal_limit, daily_limit, risk_id, interest_way, amount, available, sale, status, publish_day,region,type
    from product_detail
        where number=#para(number) and is_deleted=0
#end

#sql("getDetailByName")
select id, user_id, created_time, updated_time, number, name, introduction, term, annual_rate, initial_money, increase_money, personal_limit, daily_limit, risk_id, interest_way, amount, available, sale, status, publish_day,region,type
from product_detail
where is_deleted =0
    #if(name)
    and name like '%#(name)%'
    #end
    #if(userId)
    and user_id like '%#(userId)%'
    #end
#end

#sql("getByName")
select id, user_id, created_time, updated_time, number, name, introduction, term, annual_rate, initial_money, increase_money, personal_limit, daily_limit, risk_id, interest_way, amount, available, sale, status, publish_day,region,type
from product_detail
where is_deleted =0 and name=#para(name)
#end

#sql("getProductMonthRank")
select *
from Vproduct
where is_deleted = 0
#if(productId)
    and id = #para(productId)
#end
#end

#sql("getProductFromVproduct")
select *
from Vproduct
where is_deleted = 0
#if(productId)
    and id = #para(productId)
#end
#if(teamId)
    and team_id = #para(teamId)
#end
#end
