#sql("getByJsonChart")
select *
from community
where json_chart = #para (jsonChart) and is_deleted = 0
#end

#sql("getById")
select *
from community
where id = #para (id) and is_deleted = 0
#end



#sql("showPushMould")
select id, created_time, updated_time, json_chart, name, content,img, praise, collect,img_id,cover
from community
where  user_id=#para(userId) and is_deleted = 0
order by created_time DESC
#end


#sql("showCollectMould")
select id, upload_created_time, upload_updated_time, upload_is_deleted, upload_user, click_created_time, click_updated_time, click_user, click_is_deleted, json_chart, name, content, img,img_id, praise, collect, click_id, type,cover
from VcommunityLike
where click_is_deleted = 0 and upload_is_deleted=0 and type =1 and click_user = #para(userId)
order by click_updated_time DESC
#end

#sql("getAll")
select c.id, c.created_time, c.updated_time, c.is_deleted, c.user_id, json_chart, c.name, content, praise, collect, img,img_id,cover, oi.name as user_name
from community c left join operator_information oi on c.user_id = oi.user_id
where c.is_deleted =0
#if(name)
  and c.name like '%#(name)%'
#end
#if(id)
  and c.id = #para (id)
#end
#if(praise)
  order by praise DESC
#else
  order by c.created_time DESC
#end
#end
