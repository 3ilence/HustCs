CREATE DATABASE covid19mon;
CREATE DATABASE IF NOT EXISTS covid19mon;
USE covid19mon;

-- 表1 人员表(person)
  CREATE TABLE person
    (id INT,
     fullname CHAR(20) NOT NULL,
     telephone CHAR(11) NOT NULL,
     CONSTRAINT pk_person PRIMARY KEY(id)
    );
-- 表2 地点表(location)
  CREATE TABLE location
    (id INT,
     location_name CHAR(20) NOT NULL,
     CONSTRAINT pk_location PRIMARY KEY(id)
    ); 
-- 表3 行程表（itinerary）
  CREATE TABLE itinerary
    (id INT,
     p_id INT,
     loc_id INT,
     s_time DATETIME,
     e_time DATETIME,
     CONSTRAINT pk_itinerary PRIMARY KEY(id),
     CONSTRAINT fk_itinerary_pid FOREIGN KEY(p_id) REFERENCES person(id),#行程表的人员编号和人员表的编号
     CONSTRAINT fk_itinerary_lid FOREIGN KEY(loc_id) REFERENCES location(id)#行程表的所在地点编号和地点表的编号
    ); 
-- 表4 诊断表（diagnose_record）
 CREATE TABLE diagnose_record
    (id INT,
     p_id INT,
     diagnose_date DATETIME,
     result INT,
     CONSTRAINT pk_diagnose_record PRIMARY KEY(id),
     CONSTRAINT fk_diagnose_pid FOREIGN KEY(p_id) REFERENCES person(id)#诊断表的人员编号和人员表的编号
    );  
-- 表5 密切接触者表（close_contact）
CREATE TABLE close_contact
    (id INT,
     p_id INT,
     contact_date DATETIME,
     loc_id INT,
     case_p_id INT,
     CONSTRAINT pk_close_contact PRIMARY KEY(id),
     CONSTRAINT fk_contact_pid FOREIGN KEY(p_id) REFERENCES person(id),#密切接触者表的被接触者编号和人员表的编号
     CONSTRAINT fk_contact_lid FOREIGN KEY(loc_id) REFERENCES location(id),#密切接触者表的接触地点编号和地点表的编号
     CONSTRAINT fk_contact_caseid FOREIGN KEY(case_p_id) REFERENCES person(id)#密切接触者表病例人员编号和人员表的编号
    ); 
-- 表6 隔离地点表（isolation_location）
CREATE TABLE isolation_location
    (id INT,
     location_name CHAR(20),
     capacity INT,
     CONSTRAINT pk_isolation_loc PRIMARY KEY(id)
    ); 
-- 表7 隔离表（isolation_record）
CREATE TABLE isolation_record
    (id INT,
     p_id INT,
     s_date DATETIME,
     e_date DATETIME,
     isol_loc_id INT,
     state INT,
     CONSTRAINT pk_isolation PRIMARY KEY(id),
     CONSTRAINT fk_isolation_pid FOREIGN KEY(p_id) REFERENCES person(id),#被隔离人员编号和人员表的编号
     CONSTRAINT fk_isolation_lid FOREIGN KEY(isol_loc_id) REFERENCES isolation_location(id)#隔离地点编号和人员表的编号
    );
-- 代码结束
/* *********************************************************** */

#增
USE covid19mon;
INSERT INTO `person`(id,`fullname`,`telephone`) VALUES
	(1,'张小敏','13907110001');
INSERT INTO `person`(id,`fullname`,`telephone`) VALUES
	(2,'李大锤','18907110002');
INSERT INTO `person`(id,`fullname`,`telephone`) VALUES
	(3,'孙二娘','13307100003');
#删
USE  covid19mon;
DELETE FROM `person`
WHERE `fullname`= "李大锤";
#改
USE  covid19mon;
UPDATE `person` SET `telephone`="13607176668" WHERE `id`=1 AND `fullname`="张小敏";


#第一题：人流量大于30的地点
SELECT l.location_name,visitors
FROM location AS l, (
    SELECT loc_id,COUNT(*) AS visitors
    FROM itinerary
    GROUP BY loc_id
    HAVING visitors > 30
    ORDER BY visitors DESC
) AS i
WHERE l.id = i.loc_id
ORDER BY visitors DESC,l.location_name;

#第二关：每个隔离点正在隔离的人数
SELECT l.location_name, i.number
FROM isolation_location AS l, (
    SELECT i.isol_loc_id,COUNT(*) AS NUMBER
    FROM isolation_record AS i,isolation_location AS l
    WHERE i.isol_loc_id = l.id AND i.state = 1
    GROUP BY isol_loc_id
    ORDER BY NUMBER DESC
) AS i
WHERE l.id = i.isol_loc_id
ORDER BY NUMBER DESC,l.location_name;

#第三关：接续行程
SELECT p.id,p.fullname,p.telephone,i1.s_time AS reclosing_time,
i2.loc_id AS loc1,l2.location_name AS address1,
i1.loc_id AS loc2,l1.location_name AS address2
FROM itinerary i1, itinerary i2,person p,location l1,location l2
WHERE i1.s_time = i2.e_time AND i1.p_id > 30 AND p.id = i1.p_id 
AND p.id = i2.p_id AND l1.id = i1.loc_id AND l2.id = i2.loc_id
ORDER BY id,reclosing_time,loc1,loc2;

#第四关
USE covid19mon;
SELECT p.fullname , p.telephone, 
    IFNULL(l.location_name,'NULL') AS location_name,
    IFNULL(i.s_time,'NULL') AS s_time ,
    IFNULL(i.e_time,'NULL') AS e_time
FROM itinerary AS i
RIGHT JOIN location AS l
ON l.id=i.loc_id
RIGHT JOIN person AS p
ON p.id=i.p_id
WHERE p.fullname IN("充珉瑶","贾涵山")
ORDER BY p.id DESC;
#正确答案
SELECT fullname,telephone,location_name,s_time,e_time
FROM person LEFT JOIN itinerary AS a ON person.id=p_id
LEFT JOIN location ON location.id=a.loc_id
WHERE  fullname='充珉瑶' OR fullname='贾涵山' 
ORDER BY person.id DESC,s_time

#正确答案
SELECT p.fullname, p.telephone, IFNULL(l.location_name,"NULL") AS location_name, IFNULL(i.s_time,"NULL") AS s_time, IFNULL(i.e_time,"NULL") AS e_time
FROM person AS p, location AS l, itinerary AS i
WHERE p.fullname IN("充珉瑶","贾涵山") AND p.id=i.p_id AND l.id=i.loc_id
ORDER BY p.id DESC;
#第五关：模糊查询
SELECT id,location_name 
FROM location
WHERE location_name LIKE '%店%'
ORDER BY id;
#第六关
SELECT p.fullname, p.telephone
FROM person p,itinerary i,location l
WHERE p.id = i.p_id AND l.location_name = '活动中心' 
    AND i.loc_id = l.id 
    AND (i.s_time BETWEEN '2021-02-02 20:05:40' AND '2021-02-02 21:25:40' 
    OR i.e_time BETWEEN '2021-02-02 20:05:40' AND '2021-02-02 21:25:40')
ORDER BY fullname;
#第七关
SELECT location_name
FROM isolation_location
WHERE id IN(
    SELECT isol_loc_id 
    FROM isolation_record 
    WHERE state=1);
#第八关
SELECT p.fullname, p.telephone
FROM person p,(
    SELECT p_id
    FROM itinerary
    GROUP BY p_id
) AS n
WHERE p.id = n.p_id
LIMIT 0 ,30;

SELECT p.fullname, p.telephone
FROM person p
WHERE EXISTS(
        SELECT id 
        FROM itinerary i
        WHERE p.id = i.p_id
    )
LIMIT 0 ,30;
#第九关

SELECT COUNT(*) AS NUMBER
FROM person p
WHERE NOT EXISTS(
    SELECT p_id
    FROM itinerary i, location l
    WHERE i.p_id = p.id AND l.location_name = 'Today便利店' AND l.id = i.loc_id
);
#第十关
SELECT P.PNAME , P.CITY
FROM PROJECT AS P, BANK_P AS B
WHERE P.PID = B.PID
GROUP BY B.PID
HAVING SUM(MONEY) > 50




UPDATE PROJECT SET `SCORE`=`SCORE`+10
WHERE EXISTS(
	SELECT MONEY
	FROM BANK_P
	WHERE BANL_P.MONEY > PROJECT.REQUIRE
)
#正确答案
#所有地点都去过，即不存在有没去过的地点，即第一层子查询是查询存在有没去过的地点
SELECT fullname
FROM person
WHERE NOT EXISTS(
	SELECT *
	FROM location
	WHERE NOT EXISTS
	(
		SELECT *
		FROM itinerary
		WHERE person.id=p_id AND loc_id=location.id
	)
)
ORDER BY fullname;

#第十一关
USE covid19mon;
CREATE VIEW
isolation_location_status(id, location_name,  capacity, occupied) 
AS SELECT l.id, l.location_name,l.capacity, 
COUNT(CASE WHEN i.state=1 THEN 0 END) AS occupied
FROM isolation_location AS l, isolation_record AS i
WHERE i.isol_loc_id = l.id
GROUP BY l.id;

#第十二关
USE covid19mon;
SELECT location_name, (capacity - occupied) AS available_rooms
FROM isolation_location_status;

#第十三关
#这题注意时间的判断，不要漏了情况
USE covid19mon;
SELECT p.fullname,p.telephone
FROM person AS p, itinerary AS i, (
    SELECT person.id,itinerary.s_time,itinerary.e_time,itinerary.loc_id
    FROM person,itinerary
    WHERE person.id = itinerary.p_id AND person.fullname='靳宛儿'
) AS d
WHERE ((i.s_time BETWEEN d.s_time AND d.e_time 
    OR i.e_time BETWEEN d.s_time AND d.e_time  )
    OR (d.s_time BETWEEN i.s_time AND i.e_time 
    OR d.e_time BETWEEN i.s_time AND i.e_time  ))
    AND d.loc_id = i.loc_id AND i.p_id <> d.id AND p.id = i.p_id
ORDER BY p.fullname;

#第十四关
USE covid19mon;
SELECT location_name, COUNT(*) AS close_contact_number
FROM close_contact AS c, location AS l
WHERE c.loc_id=l.id
GROUP BY c.loc_id
ORDER BY close_contact_number DESC, location_name;

#第十五关
USE covid19mon;
SELECT c.case_p_id,p.fullname,COUNT(*) AS infected_number
FROM close_contact AS c, person AS p
WHERE c.case_p_id = p.id
GROUP BY c.case_p_id
ORDER BY infected_number DESC
LIMIT 0,1;

#第十六关
USE covid19mon;
SELECT p.fullname, COUNT(*) AS record_number
FROM person p,itinerary i
WHERE p.id = i.p_id AND (i.s_time BETWEEN '2021-02-02 10:00:00' AND '2021-02-02 14:00:00'
    OR i.e_time BETWEEN '2021-02-02 10:00:00' AND '2021-02-02 14:00:00')
GROUP BY p.fullname
ORDER BY record_number DESC,p.fullname
LIMIT 0, 3;

#第十七关
USE covid19mon;
SELECT location_name,capacity
FROM isolation_location
WHERE capacity < (
    SELECT MAX(capacity)
    FROM isolation_location
)
ORDER BY capacity DESC
LIMIT 0,1;

#触发器
DROP TRIGGER IF EXISTS tri1;
DROP TRIGGER IF EXISTS tri2;
-- 用"DELIMITER 界符"语句指定触发器定义语句的界符
DELIMITER ;;
-- 创建触发器
DROP TRIGGER IF EXISTS tri1;
DROP TRIGGER IF EXISTS tri2;

DELIMITER ;;

CREATE TRIGGER tri1 AFTER INSERT
ON diagnose_record FOR EACH ROW
BEGIN

   
    IF new.result = 1 THEN 
        UPDATE isolation_record SET state = 3
        WHERE new.p_id = isolation_record.p_id;
    END IF;
END
;;
CREATE TRIGGER tri2 AFTER UPDATE
ON diagnose_record FOR EACH ROW
BEGIN
    
    IF new.result = 1 THEN 
        UPDATE isolation_record SET state = 3
        WHERE new.p_id = isolation_record.p_id;
    END IF;
END
;;
DELIMITER ;

#函数
USE covid19mon;
-- 创建函数前，需要执行以下语句
SET GLOBAL log_bin_trust_function_creators=1;

-- 如果已经有同名函数，将其删除
DROP FUNCTION IF EXISTS Count_Records;
-- 修改界符
DELIMITER ;;
-- 创建函数
SET GLOBAL log_bin_trust_function_creators=1;
DELIMITER ;;
DROP FUNCTION IF EXISTS Count_Records;
CREATE FUNCTION Count_Records(id INT)
RETURNS INT
BEGIN
    RETURN (SELECT COUNT(*) FROM itinerary WHERE p_id = id);
END;;
DELIMITER ;

/*
(2) 利用创建的函数，仅用一条SQL语句查询在行程表中至少有3条行程记录的人员信息，查询结果依人员编号排序。*/
SELECT person.id, person.fullname, person.telephone
FROM person
WHERE Count_Records(person.id) >= 3
ORDER BY person.id;