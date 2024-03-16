-- #sql("setCollectOrLike")
-- update graph_like
-- set type
-- where id = #para (id) and is_deleted = 0
-- #end


#sql("getCollectOrLike")
select *
from graph_like
where user_id = #para (userId) and graph_id = #para(graphId) and  type = #para(type)
#if(help)
  and is_deleted = 0
#end
#end
