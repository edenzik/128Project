SELECT a, count(b)
FROM (SELECT DISTINCT a,b FROM test) AS poop
GROUP BY a
HAVING count(b)>1;
