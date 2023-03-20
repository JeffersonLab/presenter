alter session set container = XEPDB1;

ALTER SYSTEM SET db_create_file_dest = '/opt/oracle/oradata';

create tablespace PRESENTER;

create user "PRESENTER_OWNER" profile "DEFAULT" identified by "password" default tablespace "PRESENTER" account unlock;

grant connect to PRESENTER_OWNER;
grant unlimited tablespace to PRESENTER_OWNER;

grant create view to PRESENTER_OWNER;
grant create sequence to PRESENTER_OWNER;
grant create table to PRESENTER_OWNER;
grant create procedure to PRESENTER_OWNER;
grant create type to PRESENTER_OWNER;