-- Active: 1764757016611@@localhost@3306@study_01
create database mybatis;
use mybatis;
DROP TABLE IF EXISTS tb_user;
create table tb_user(
    id int primary key AUTO_INCREMENT,
    username VARCHAR(20),
    password VARCHAR(20),
    gender char(1),
    addr varchar(30)
);
INSERT INTO tb_user VALUES (1,'zhangsan','123','男','天津');
INSERT INTO tb_user VALUES (2,'lisi','234','女','北京');
INSERT INTO tb_user VALUES (3,'wangwu','345','男','河北');


drop table if exists tb_brand;
use mybatis;
CREATE table tb_brand(
    id int PRIMARY KEY auto_increment,
    brand_name varchar(20),
    company_name varchar(20),
    ordered int,
    description VARCHAR(100),
    status int
);
insert into tb_brand(brand_name,company_name,ordered,description,status)
 VALUES('三只松鼠','三只松鼠股份有限公司',5,'好吃不上火',0),
('华为','华为技术有限公司',100,'华为是一家公司',1),
('三只松鼠','小米科技有限公司',50,'are you ok',1);