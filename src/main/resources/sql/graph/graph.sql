#sql("getById")
select id, product_id, json_chart, user_id,deal_json,img
    from graph
        where id =#para(id) and is_deleted=0
#end

#sql("getByProductId")
select id, product_id, json_chart, user_id,deal_json,img
from graph
where product_id = #para(productId) and is_deleted=0
#end