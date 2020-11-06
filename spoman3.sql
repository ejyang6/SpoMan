
drop table ccp;
drop table cpicture;

--���ӻ���ø ���̺�(exerd����)
ALTER TABLE cpicture
	DROP
		CONSTRAINT FK_member_TO_cpicture
		CASCADE;

ALTER TABLE cpicture
	DROP
		PRIMARY KEY
		CASCADE
		KEEP INDEX;

DROP INDEX PK_cpicture;

/* ���ӻ���ø */
DROP TABLE cpicture 
	CASCADE CONSTRAINTS;

/* ���ӻ���ø */
CREATE TABLE cpicture (
	cpno NUMBER(4) NOT NULL, /* ������ȣ */
	cpimage VARCHAR2(100) NOT NULL, /* �̹��� */
	cptitle VARCHAR2(50) NOT NULL, /* ������ */
  cpdate DATE, /*����� default sysdate*/
  cno NUMBER(4),  /*���ӹ�ȣ*/
	idx NUMBER(4) /* ȸ����ȣ */
);

CREATE UNIQUE INDEX PK_cpicture
	ON cpicture (
		cpno ASC
	);

ALTER TABLE cpicture
	ADD
		CONSTRAINT PK_cpicture
		PRIMARY KEY (
			cpno
		);

ALTER TABLE cpicture
	ADD
		CONSTRAINT FK_member_TO_cpicture
		FOREIGN KEY (
			idx
		)
		REFERENCES member (
			idx
		);

-----insert
drop sequence cpicture_seq;
create sequence cpicture_seq
start with 1 increment by 1;

--4�� ������
insert into cpicture(cpno, cpimage, cpdate, cptitle, cno, idx)
values(cpicture_seq.nextval, 'soccerplay1.jpg', sysdate, '�౸�ϴ� ��', 46, 1);

select * from cpicture;

select cpimage, cptitle, cpdate from cpicture  where cno=46 and idx=2; 

--����_����------------------------
ALTER TABLE ccp
	DROP
		CONSTRAINT FK_club_TO_ccp
		CASCADE;

ALTER TABLE ccp
	DROP
		CONSTRAINT FK_cpicture_TO_ccp
		CASCADE;

/* ����-���� */
DROP TABLE ccp 
	CASCADE CONSTRAINTS;

/* ����-���� */
CREATE TABLE ccp (
	cno NUMBER(4), /* Ŭ����ȣ */
	cpno NUMBER(4) /* ������ȣ */
);


ALTER TABLE ccp
	ADD
		CONSTRAINT FK_club_TO_ccp
		FOREIGN KEY (
			cno
		)
		REFERENCES club (
			cno
		);

ALTER TABLE ccp
	ADD
		CONSTRAINT FK_cpicture_TO_ccp
		FOREIGN KEY (
			cpno
		)
		REFERENCES cpicture (
			cpno
		);
-----insert
insert into ccp(cno, cpno) values(46, 1);
insert into ccp(cno, cpno) values(46, 2);
insert into ccp(cno, cpno) values(46, 3);
insert into ccp(cno, cpno) values(46, 4);

select * from cpicture;


--------3�� ����(ȸ����,ȸ�����̵�, �����̸� ���)
select cpimage, cptitle, cpdate, name, id from ccp
join cpicture cp on cp.cpno=ccp.cpno 
join member m on cp.idx=m.idx where cno=46;

select cpimage, cptitle, cpdate, name, id from cpicture cp
join member m on m.idx=cp.idx where cno=46; 

--���ӰԽ���----------------------------------------------------------------
ALTER TABLE cboard
	DROP
		CONSTRAINT FK_club_TO_cboard
		CASCADE;

ALTER TABLE cboard
	DROP
		CONSTRAINT FK_member_TO_cboard
		CASCADE;

ALTER TABLE cboard
	DROP
		PRIMARY KEY
		CASCADE
		KEEP INDEX;

DROP INDEX PK_cboard;

/* ���ӰԽ��� */
DROP TABLE cboard 
	CASCADE CONSTRAINTS;

/* ���ӰԽ��� */
CREATE TABLE cboard (
	cbno NUMBER(4) NOT NULL, /* �۹�ȣ */
	cno NUMBER(4) NOT NULL, /* Ŭ����ȣ */
	idx NUMBER(4) NOT NULL, /* ȸ����ȣ */
	cbtitle VARCHAR2(50) NOT NULL, /* ������ */
	cbcontent VARCHAR2(2000) NOT NULL, /* �۳��� */ 
  cbreadnum number(8) default 0, --��ȸ��
	cbimage VARCHAR2(100), /* �̹��� */
	cbupdate TIMESTAMP default systimestamp /* ����� */
);

CREATE UNIQUE INDEX PK_cboard
	ON cboard (
		cbno ASC
	);

ALTER TABLE cboard
	ADD
		CONSTRAINT PK_cboard
		PRIMARY KEY (
			cbno
		);

ALTER TABLE cboard
	ADD
		CONSTRAINT FK_club_TO_cboard
		FOREIGN KEY (
			cno
		)
		REFERENCES club (
			cno
		);

ALTER TABLE cboard
	ADD
		CONSTRAINT FK_member_TO_cboard
		FOREIGN KEY (
			idx
		)
		REFERENCES member (
			idx
		);


--------------------------------------------
drop table cboard;

select * from cboard;

create sequence cboard_seq;

--�۹�ȣ, Ŭ����ȣ, ȸ����ȣ, ��ȸ��(cbreadnum),cbupdate(timestamp)=>default ���� 
insert into cboard(cbno, cno, idx, cbtitle, cbcontent, cbimage)
values(cboard_seq.nextval, 46, 1, '�����λ��ؿ�!', '�ȳ��ϼ���?? �� ���� �����߾��!', null);
insert into cboard(cbno, cno, idx, cbtitle, cbcontent, cbimage)
values(cboard_seq.nextval, 46, 2, '�����λ��ؿ�!', '�ȳ��ϼ���?? �� ���� �����߾��!', null);
insert into cboard(cbno, cno, idx, cbtitle, cbcontent, cbimage)
values(cboard_seq.nextval, 46, 1, '���� �����߾��.', '���� �𿴴µ� ���� ��վ����!', null);
insert into cboard(cbno, cno, idx, cbtitle, cbcontent, cbimage)
values(cboard_seq.nextval, 46, 3, '���� �����߾��.', '���� �𿴴µ� ���� ��վ����!', null);

update cboard set cbimage='cboard1.jpg' where idx=1;  

---cboadmapper
select cbno, cbimage, cbtitle, cbupdate, m.idx, name, id, cbreadnum from cboard cb
join member m on m.idx=cb.idx where cno=46;

select cbno, cbimage, cbtitle, cbupdate, name, id, cbreadnum
from (select ROW_NUMBER () OVER (order by cbno desc) rn, 
cb.*, m.* from cboard cb join member m on m.idx=cb.idx where cno=46) 
where rn between 1 and 17;




--notice
CREATE TABLE notice (
	nno number(4) primary key
);

select * from member;
alter table member change img mark varchar2();
