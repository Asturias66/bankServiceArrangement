#--------------用户模块---------------#
#namespace("user")
#include("user/user.sql")
#end

#namespace("userInformation")
#include("user/user_information.sql")
#end

#namespace("card")
#include("user/card.sql")
#end

#namespace("session")
#include("user/session.sql")
#end

#namespace("blacklist")
#include("user/blacklist.sql")
#end

#namespace("team")
#include("user/team.sql")
#end

#namespace("teamMember")
#include("user/team_member.sql")
#end

#--------------系统沟通模块---------------#

#namespace("notice")
#include("conversation/notice.sql")
#end

#namespace("feedback")
#include("conversation/feedback.sql")
#end

#--------------产品模块---------------#

#namespace("productDetail")
#include("product/product_detail.sql")
#end

#namespace("productUser")
#include("product/product_user.sql")
#end

#------------配置人员模块-------------#
#namespace("operatorInformation")
#include("operator/operator_information.sql")
#end


#------------文件模块-------------#
#namespace("file")
#include("file/file.sql")
#end

#------------流程图模块-------------#
#namespace("graph")
#include("graph/graph.sql")
#end

#------------模板社区模块-------------#
#namespace("community")
#include("community/community.sql")
#end

#namespace("graphLike")
#include("community/graph_like.sql")
#end