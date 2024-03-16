#sql("getById")
select *
from file
where id=#para(id) and is_deleted=0
#end


