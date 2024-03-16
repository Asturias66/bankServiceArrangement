#sql("getCardByCardNumber")
select *
from card
where card_number = #para(cardNumber) and is_deleted = 0
#end

