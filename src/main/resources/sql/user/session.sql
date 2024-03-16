#sql("deleteByUuid")
update session
    set is_deleted=1
    where uuid=#para(uuid) and is_deleted=0
#end
------备用方案-------
#sql("deleteByToken")
update session
    set is_deleted=1
    where access_token=#para(token) and is_deleted=0
#end