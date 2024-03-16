#sql("authUserByTelephone")
select *
from user
where telephone = #para(telephone) and is_deleted = 0 and status = 1
#end

#sql("selectUserById")
select *
from user
where id = #para(id)
#end

#sql("getAllUser")
select *
from VuserInfo a
where information_deleted = 0
#if(number)
    and a.number = #para(number)
#end
#if(name)
    and a.name like '%#(name)%'
#end
#if(userId)
    and a.id = #para(userId)
#end
#if(telephone)
    and a.telephone = #para(telephone)
#end
#end

#sql("getByNumber")
select  password , id, salt, number ,type
    from user
        where number=#para(number) and is_deleted=0
#end

#sql("getByPhone")
select id, type, password, salt, telephone, number
    from user
        where telephone=#para(telephone) and is_deleted=0
#if(type)
    and type = #para(type)
#end
#end

#sql("getById")
select *
    from user
        where id=#para(id) and is_deleted=0
#end

#sql("getNumOfUsers")
select count(*) as num
from VuserInfo
where information_deleted = 0
#end

