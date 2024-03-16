#sql("getAllFeedback")
select *
from Vfeedback
where type =#para(type) and is_deleted=0
#if(status)
    and status = #para(status)
#end
#if(userId)
    and object_id = #para(userId)
#end
order by created_time desc
#end

#sql("getFeedbackById")
select *
from feedback
where id =#para(id) and is_deleted=0
#end

#sql("getOperatorFeedback")
select *
from Vfeedback
where type = 2 and (object_id = #para(userId) or object_id = 0) and is_deleted=0
#if(status)
    and status = #para(status)
#end
order by created_time desc
#end

#sql("getOperatorFeedbackTable")
select *
from feedback
where type = 2 and (object_id = #para(userId) or object_id = 0) and is_deleted=0
#if(status)
    and status = #para(status)
#end
order by created_time desc
#end



