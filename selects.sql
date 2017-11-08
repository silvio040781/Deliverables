SELECT * FROM LOG_INFORMATIONS;

SELECT * FROM LOG_INFORMATIONS 
WHERE DATE_REQUEST >= date_format('2017-01-01 00:00:12', "%y-%m-%d %H:%i:%s") AND DATE_REQUEST <= date_format('2017-01-01 00:00:21', "%y-%m-%d %H:%i:%s") ;

select COUNT(ip) AS TOTAL_REQUEST , ip AS IP_REQUEST
from LOG_INFORMATIONS 
where DATE_REQUEST >= date_format('2017-01-01 00:00:12', "%y-%m-%d %H:%i:%s")
group by ip
having count(ip) > 100;

SELECT * FROM LOG_INFORMATIONS WHERE IP = '192.168.169.194';
