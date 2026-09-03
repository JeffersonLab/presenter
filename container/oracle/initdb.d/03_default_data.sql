alter session set container = XEPDB1;

insert into PRESENTER_OWNER.TEAM (TEAM_ID, NAME, DESCRIPTION, DIRECTORY_GROUP_NAME, WEIGHT, ARCHIVED_YN) values (PRESENTER_OWNER.TEAM_ID.nextval, 'Injector', 'Accelerator division center for injectors and sources group', 'cis', 0, 'N');
insert into PRESENTER_OWNER.TEAM (TEAM_ID, NAME, DESCRIPTION, DIRECTORY_GROUP_NAME, WEIGHT, ARCHIVED_YN) values (PRESENTER_OWNER.TEAM_ID.nextval, 'Mechanical Support', 'Accelerator division electrical engineering support services group', 'eess', 1, 'N');
insert into PRESENTER_OWNER.TEAM (TEAM_ID, NAME, DESCRIPTION, DIRECTORY_GROUP_NAME, WEIGHT, ARCHIVED_YN) values (PRESENTER_OWNER.TEAM_ID.nextval, 'RF', 'Physics division radio frequency electrical work group', 'rf', 2, 'N');
insert into PRESENTER_OWNER.TEAM (TEAM_ID, NAME, DESCRIPTION, DIRECTORY_GROUP_NAME, WEIGHT, ARCHIVED_YN) values (PRESENTER_OWNER.TEAM_ID.nextval, 'SRF', 'Physics division super radio frequency work group', 'srf', 3, 'N');
insert into PRESENTER_OWNER.TEAM (TEAM_ID, NAME, DESCRIPTION, DIRECTORY_GROUP_NAME, WEIGHT, ARCHIVED_YN) values (PRESENTER_OWNER.TEAM_ID.nextval, 'Vacuum', 'Physics division vacuum group', 'vac', 4, 'N');
insert into PRESENTER_OWNER.TEAM (TEAM_ID, NAME, DESCRIPTION, DIRECTORY_GROUP_NAME, WEIGHT, ARCHIVED_YN) values (PRESENTER_OWNER.TEAM_ID.nextval, 'Accelerator Computing', 'Accelerator division computing group', 'acg', 5, 'N');
insert into PRESENTER_OWNER.TEAM (TEAM_ID, NAME, DESCRIPTION, DIRECTORY_GROUP_NAME, WEIGHT, ARCHIVED_YN) values (PRESENTER_OWNER.TEAM_ID.nextval, 'Hall A', 'Physics division Hall A', 'halla', 6, 'N');


insert into PRESENTER_OWNER.TEAM_STATUS_REPORT(TEAM_STATUS_REPORT_ID) values (PRESENTER_OWNER.TEAM_STATUS_REPORT_ID.nextval, 1, sysdate, 'Nothing Accomplished!', 'Nothing in progress!', 'Nothing planned', 'Nothing blocking!')