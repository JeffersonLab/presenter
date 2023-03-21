alter session set container = XEPDB1;

--DROP SEQUENCE PRESENTER_OWNER.ACC_ACTIVITY_RECORD_ID;
--DROP SEQUENCE PRESENTER_OWNER.ACCESS_RECORD_ID;
--DROP SEQUENCE PRESENTER_OWNER.SLIDE_ID;
--DROP SEQUENCE PRESENTER_OWNER.PRESENTATION_ID;
--DROP SEQUENCE PRESENTER_OWNER.BTA_RECORD_ID;
--DROP SEQUENCE PRESENTER_OWNER.BEAM_TO_HALL_RECORD_ID;

--DROP TABLE PRESENTER_OWNER.IFRAME_SLIDE CASCADE CONSTRAINTS PURGE;
--DROP TABLE PRESENTER_OWNER.PD_INFO_SLIDE CASCADE CONSTRAINTS PURGE;
--DROP TABLE PRESENTER_OWNER.BTA_RECORD CASCADE CONSTRAINTS PURGE;
--DROP TABLE PRESENTER_OWNER.BTA_SHIFT_INFO_SLIDE CASCADE CONSTRAINTS PURGE;
--DROP TABLE PRESENTER_OWNER.SHIFT_INFO_SLIDE CASCADE CONSTRAINTS PURGE;
--DROP TABLE PRESENTER_OWNER.TITLE_BODY_SLIDE CASCADE CONSTRAINTS PURGE;
--DROP TABLE PRESENTER_OWNER.BODY_SLIDE CASCADE CONSTRAINTS PURGE;
--DROP TABLE PRESENTER_OWNER.TITLE_BODY_IMAGE_SLIDE CASCADE CONSTRAINTS PURGE;
--DROP TABLE PRESENTER_OWNER.TITLE_IMAGE_SLIDE CASCADE CONSTRAINTS PURGE;
--DROP TABLE PRESENTER_OWNER.IMAGE_SLIDE CASCADE CONSTRAINTS PURGE;
--DROP TABLE PRESENTER_OWNER.BEAM_TO_HALL_RECORD CASCADE CONSTRAINTS PURGE;
--DROP TABLE PRESENTER_OWNER.ACC_ACTIVITY_RECORD CASCADE CONSTRAINTS PURGE;
--DROP TABLE PRESENTER_OWNER.PD_BEAM_ACC_SLIDE CASCADE CONSTRAINTS PURGE;
--DROP TABLE PRESENTER_OWNER.ACCESS_RECORD CASCADE CONSTRAINTS PURGE;
--DROP TABLE PRESENTER_OWNER.PD_ACCESS_SLIDE CASCADE CONSTRAINTS PURGE;
--DROP TABLE PRESENTER_OWNER.SLIDE CASCADE CONSTRAINTS PURGE;
--DROP TABLE PRESENTER_OWNER.PRESENTATION_LOG CASCADE CONSTRAINTS PURGE;
--DROP TABLE PRESENTER_OWNER.UITF_PRESENTATION CASCADE CONSTRAINTS PURGE;
--DROP TABLE PRESENTER_OWNER.LO_PRESENTATION CASCADE CONSTRAINTS PURGE;
--DROP TABLE PRESENTER_OWNER.LASO_PRESENTATION CASCADE CONSTRAINTS PURGE;
--DROP TABLE PRESENTER_OWNER.CC_PRESENTATION CASCADE CONSTRAINTS PURGE;
--DROP TABLE PRESENTER_OWNER.PD_PRESENTATION CASCADE CONSTRAINTS PURGE;
--DROP TABLE PRESENTER_OWNER.PRESENTATION CASCADE CONSTRAINTS PURGE;

CREATE SEQUENCE PRESENTER_OWNER.ACC_ACTIVITY_RECORD_ID
    START WITH 1
    NOCYCLE
    NOCACHE
    ORDER;

CREATE SEQUENCE PRESENTER_OWNER.ACCESS_RECORD_ID
    START WITH 1
    NOCYCLE
    NOCACHE
    ORDER;

CREATE SEQUENCE PRESENTER_OWNER.BEAM_TO_HALL_RECORD_ID
    START WITH 1
    NOCYCLE
    NOCACHE
    ORDER;

CREATE SEQUENCE PRESENTER_OWNER.BTA_RECORD_ID
    START WITH 1
    NOCYCLE
    NOCACHE
    ORDER;

CREATE SEQUENCE PRESENTER_OWNER.PRESENTATION_ID
    START WITH 1
    NOCYCLE
    NOCACHE
    ORDER;

CREATE SEQUENCE PRESENTER_OWNER.SLIDE_ID
    START WITH 1
    NOCYCLE
    NOCACHE
    ORDER;

CREATE TABLE PRESENTER_OWNER.PRESENTATION
(
    PRESENTATION_ID      INTEGER NOT NULL ,
    PRESENTATION_TYPE    VARCHAR2(24) DEFAULT 'PRESENTATION' NOT NULL ,
    LAST_USERNAME        VARCHAR2(32 CHAR) NULL ,
    LAST_MODIFIED_DATE   DATE DEFAULT sysdate NOT NULL ,
    CONSTRAINT PRESENTATION_PK PRIMARY KEY (PRESENTATION_ID),
    CONSTRAINT PRESENTATION_CK1 CHECK ( PRESENTATION_TYPE IN ('PRESENTATION','CC_PRESENTATION','PD_PRESENTATION','LASO_PRESENTATION','LO_PRESENTATION','UITF_PRESENTATION') )
);

CREATE TABLE PRESENTER_OWNER.LASO_PRESENTATION
(
    PRESENTATION_ID      INTEGER NOT NULL ,
    YMD                  TIMESTAMP(0) NOT NULL  CONSTRAINT  LASO_PRESENTATION_CK1 CHECK (EXTRACT(HOUR FROM YMD) = 0 AND EXTRACT(MINUTE FROM YMD) = 0 AND EXTRACT(SECOND FROM YMD) = 0),
    SHIFT                VARCHAR2(5) NOT NULL  CONSTRAINT  LASO_PRESENTATION_CK2 CHECK (SHIFT IN ('OWL', 'DAY', 'SWING')),
    CONSTRAINT LASO_PRESENTATION_PK PRIMARY KEY (PRESENTATION_ID),
    CONSTRAINT  LASO_PRESENTATION_AK1 UNIQUE (YMD,SHIFT),
    CONSTRAINT LASO_PRESENTATION_FK1 FOREIGN KEY (PRESENTATION_ID) REFERENCES PRESENTER_OWNER.PRESENTATION (PRESENTATION_ID) ON DELETE CASCADE
);

CREATE TABLE PRESENTER_OWNER.UITF_PRESENTATION
(
    PRESENTATION_ID      INTEGER NOT NULL ,
    YMD                  TIMESTAMP(0) NOT NULL CONSTRAINT  UITF_PRESENTATION_CK1 CHECK (EXTRACT(HOUR FROM YMD) = 0 AND EXTRACT(MINUTE FROM YMD) = 0 AND EXTRACT(SECOND FROM YMD) = 0),
    SHIFT                VARCHAR2(5) NOT NULL CONSTRAINT  UITF_PRESENTATION_CK2 CHECK (SHIFT IN ('OWL', 'DAY', 'SWING')),
    CONSTRAINT UITF_PRESENTATION_PK PRIMARY KEY (PRESENTATION_ID),
    CONSTRAINT UITF_PRESENTATION_AK1 UNIQUE (YMD,SHIFT),
    CONSTRAINT UITF_PRESENTATION_FK1 FOREIGN KEY (PRESENTATION_ID) REFERENCES PRESENTER_OWNER.PRESENTATION (PRESENTATION_ID) ON DELETE CASCADE
);

CREATE TABLE PRESENTER_OWNER.LO_PRESENTATION
(
    PRESENTATION_ID      INTEGER NOT NULL ,
    YMD                  TIMESTAMP(0) NOT NULL CONSTRAINT LO_PRESENTATION_CK1 CHECK (EXTRACT(HOUR FROM YMD) = 0 AND EXTRACT(MINUTE FROM YMD) = 0 AND EXTRACT(SECOND FROM YMD) = 0),
    SHIFT                VARCHAR2(5) NOT NULL CONSTRAINT LO_PRESENTATION_CK2 CHECK (SHIFT IN ('OWL', 'DAY', 'SWING')),
    CONSTRAINT LO_PRESENTATION_PK PRIMARY KEY (PRESENTATION_ID),
    CONSTRAINT LO_PRESENTATION_AK1 UNIQUE (YMD,SHIFT),
    CONSTRAINT LO_PRESENTATION_FK1 FOREIGN KEY (PRESENTATION_ID) REFERENCES PRESENTER_OWNER.PRESENTATION (PRESENTATION_ID) ON DELETE CASCADE
);

CREATE TABLE PRESENTER_OWNER.PD_PRESENTATION
(
    PRESENTATION_ID      INTEGER NOT NULL ,
    DELIVERY_YMD         TIMESTAMP(0) NOT NULL  CONSTRAINT  PD_PRESENTATION_CK1 CHECK (EXTRACT(HOUR FROM DELIVERY_YMD) = 0 AND EXTRACT(MINUTE FROM DELIVERY_YMD) = 0 AND EXTRACT(SECOND FROM DELIVERY_YMD) = 0),
    PD_PRESENTATION_TYPE VARCHAR2(16) NOT NULL  CONSTRAINT  PD_PRESENTATION_CK2 CHECK (PD_PRESENTATION_TYPE IN ('RUN', 'SAD', 'LSD', 'SUM1', 'SUM2', 'SUM3', 'HCO', 'PD')),
    SHIFT_LOG_DAYS       INTEGER NULL ,
    CONSTRAINT PD_PRESENTATION_PK PRIMARY KEY (PRESENTATION_ID),
    CONSTRAINT PD_PRESENTATION_AK1 UNIQUE (DELIVERY_YMD,PD_PRESENTATION_TYPE),
    CONSTRAINT PD_PRESENTATION_FK1 FOREIGN KEY (PRESENTATION_ID) REFERENCES PRESENTER_OWNER.PRESENTATION (PRESENTATION_ID) ON DELETE CASCADE
);

CREATE TABLE PRESENTER_OWNER.CC_PRESENTATION
(
    PRESENTATION_ID      INTEGER NOT NULL ,
    YMD                  TIMESTAMP(0) NOT NULL  CONSTRAINT  CC_PRESENTATION_CK1 CHECK (EXTRACT(HOUR FROM YMD) = 0 AND EXTRACT(MINUTE FROM YMD) = 0 AND EXTRACT(SECOND FROM YMD) = 0),
    SHIFT                VARCHAR2(5) NOT NULL  CONSTRAINT  CC_PRESENTATION_CK2 CHECK (SHIFT IN ('OWL', 'DAY', 'SWING')),
    CONSTRAINT CC_PRESENTATION_PK PRIMARY KEY (PRESENTATION_ID),
    CONSTRAINT CC_PRESENTATION_AK1 UNIQUE (YMD,SHIFT),
    CONSTRAINT CC_PRESENTATION_FK1 FOREIGN KEY (PRESENTATION_ID) REFERENCES PRESENTER_OWNER.PRESENTATION (PRESENTATION_ID) ON DELETE CASCADE
);

CREATE TABLE PRESENTER_OWNER.PRESENTATION_LOG
(
    PRESENTATION_ID      INTEGER NOT NULL ,
    LOG_ID               INTEGER NOT NULL ,
    USERNAME             VARCHAR2(32) NOT NULL ,
    CONSTRAINT PRESENTATION_LOG_PK PRIMARY KEY (PRESENTATION_ID,LOG_ID,USERNAME),
    CONSTRAINT PRESENTATION_LOG_FK1 FOREIGN KEY (PRESENTATION_ID) REFERENCES PRESENTER_OWNER.PRESENTATION (PRESENTATION_ID)
);

CREATE TABLE PRESENTER_OWNER.SLIDE
(
    SLIDE_ID             INTEGER NOT NULL ,
    PRESENTATION_ID      INTEGER NOT NULL ,
    ORDER_ID             INTEGER NOT NULL ,
    SLIDE_TYPE           VARCHAR2(64) NOT NULL ,
    SYNC_FROM_SLIDE_ID   INTEGER NULL ,
    LABEL                VARCHAR2(64 CHAR) NULL ,
    CONSTRAINT SLIDE_PK PRIMARY KEY (SLIDE_ID),
    CONSTRAINT SLIDE_AK1 UNIQUE (PRESENTATION_ID,ORDER_ID)  DEFERRABLE  INITIALLY DEFERRED,
    CONSTRAINT SLIDE_FK1 FOREIGN KEY (PRESENTATION_ID) REFERENCES PRESENTER_OWNER.PRESENTATION (PRESENTATION_ID) ON DELETE CASCADE,
    CONSTRAINT SLIDE_CK1 CHECK ( ORDER_ID > 0 ),
    CONSTRAINT SLIDE_CK2 CHECK ( SLIDE_TYPE IN ('SLIDE','BODY_SLIDE','TITLE_BODY_SLIDE','IMAGE_SLIDE','TITLE_IMAGE_SLIDE','TITLE_BODY_IMAGE_SLIDE','IFRAME_SLIDE','SHIFT_INFO_SLIDE','PD_INFO_SLIDE','BTA_SHIFT_INFO_SLIDE','PD_BEAM_ACC_SLIDE','PD_ACCESS_SLIDE') )
);

CREATE TABLE PRESENTER_OWNER.PD_ACCESS_SLIDE
(
    SLIDE_ID             INTEGER NOT NULL ,
    BODY                 CLOB NULL ,
    CONSTRAINT PD_ACCESS_SLIDE_PK PRIMARY KEY (SLIDE_ID),
    CONSTRAINT PD_ACCESS_SLIDE_FK1 FOREIGN KEY (SLIDE_ID) REFERENCES PRESENTER_OWNER.SLIDE (SLIDE_ID) ON DELETE CASCADE
);

CREATE TABLE PRESENTER_OWNER.ACCESS_RECORD
(
    ACCESS_RECORD_ID     INTEGER NOT NULL ,
    SLIDE_ID             INTEGER NULL ,
    ACCESS_TYPE          VARCHAR2(64) NULL ,
    HALL_A               NUMBER(12,4) NULL ,
    HALL_B               NUMBER(12,4) NULL ,
    HALL_C               NUMBER(12,4) NULL ,
    HALL_D               NUMBER(12,4) NULL ,
    ACCEL                NUMBER(12,4) NULL ,
    ORDER_ID             INTEGER NOT NULL ,
    CONSTRAINT ACCESS_RECORD_PK PRIMARY KEY (ACCESS_RECORD_ID),
    CONSTRAINT ACCESS_RECORD_AK1 UNIQUE (SLIDE_ID,ORDER_ID)  DEFERRABLE  INITIALLY DEFERRED,
    CONSTRAINT ACCESS_RECORD_FK1 FOREIGN KEY (SLIDE_ID) REFERENCES PRESENTER_OWNER.PD_ACCESS_SLIDE (SLIDE_ID) ON DELETE CASCADE
);

CREATE TABLE PRESENTER_OWNER.PD_BEAM_ACC_SLIDE
(
    SLIDE_ID             INTEGER NOT NULL ,
    CONSTRAINT PD_BEAM_ACC_SLIDE_PK PRIMARY KEY (SLIDE_ID),
    CONSTRAINT PD_BEAM_ACC_SLIDE_FK1 FOREIGN KEY (SLIDE_ID) REFERENCES PRESENTER_OWNER.SLIDE (SLIDE_ID) ON DELETE CASCADE
);

CREATE TABLE PRESENTER_OWNER.ACC_ACTIVITY_RECORD
(
    SLIDE_ID               INTEGER NULL ,
    ACC_ACTIVITY_RECORD_ID INTEGER NOT NULL ,
    ACTIVITY_TYPE          VARCHAR2(36) NULL ,
    SCHEDULED              NUMBER(12,4) NULL ,
    ACTUAL                 NUMBER(12,4) NULL ,
    ORDER_ID               INTEGER NOT NULL ,
    CONSTRAINT ACC_ACTIVITY_RECORD_PK PRIMARY KEY (ACC_ACTIVITY_RECORD_ID),
    CONSTRAINT ACC_ACTIVITY_RECORD_AK1 UNIQUE (SLIDE_ID,ORDER_ID) DEFERRABLE INITIALLY DEFERRED,
    CONSTRAINT ACC_ACTIVITY_RECORD_FK1 FOREIGN KEY (SLIDE_ID) REFERENCES PRESENTER_OWNER.PD_BEAM_ACC_SLIDE (SLIDE_ID) ON DELETE CASCADE
);

CREATE TABLE PRESENTER_OWNER.BEAM_TO_HALL_RECORD
(
    BEAM_TO_HALL_RECORD_ID INTEGER NOT NULL ,
    SLIDE_ID             INTEGER NULL ,
    HALL                 VARCHAR2(12) NULL ,
    SCHEDULED            NUMBER(12,4) NULL ,
    ACC_AVAIL            NUMBER(12,4) NULL ,
    ACCEPT               NUMBER(12,4) NULL ,
    HALL_AVAIL           NUMBER(12,4) NULL ,
    ORDER_ID             INTEGER NOT NULL ,
    ACTUAL               NUMBER(12,4) NULL ,
    CONSTRAINT BEAM_TO_HALL_RECORD_PK PRIMARY KEY (BEAM_TO_HALL_RECORD_ID),
    CONSTRAINT BEAM_TO_HALL_RECORD_AK1 UNIQUE (SLIDE_ID,ORDER_ID) DEFERRABLE INITIALLY DEFERRED,
    CONSTRAINT BEAM_TO_HALL_RECORD_FK1 FOREIGN KEY (SLIDE_ID) REFERENCES PRESENTER_OWNER.PD_BEAM_ACC_SLIDE (SLIDE_ID) ON DELETE CASCADE
);

CREATE TABLE PRESENTER_OWNER.IMAGE_SLIDE
(
    SLIDE_ID             INTEGER NOT NULL ,
    IMAGE_URL            CLOB NULL ,
    CONSTRAINT IMAGE_SLIDE_PK PRIMARY KEY (SLIDE_ID),
    CONSTRAINT IMAGE_SLIDE_FK1 FOREIGN KEY (SLIDE_ID) REFERENCES PRESENTER_OWNER.SLIDE (SLIDE_ID) ON DELETE CASCADE
);

CREATE TABLE PRESENTER_OWNER.TITLE_IMAGE_SLIDE
(
    SLIDE_ID             INTEGER NOT NULL ,
    TITLE                VARCHAR2(128) NULL ,
    CONSTRAINT TITLE_IMAGE_SLIDE_PK PRIMARY KEY (SLIDE_ID),
    CONSTRAINT TITLE_IMAGE_SLIDE_FK1 FOREIGN KEY (SLIDE_ID) REFERENCES PRESENTER_OWNER.IMAGE_SLIDE (SLIDE_ID) ON DELETE CASCADE
);

CREATE TABLE PRESENTER_OWNER.TITLE_BODY_IMAGE_SLIDE
(
    SLIDE_ID             INTEGER NOT NULL ,
    BODY                 CLOB NULL ,
    CONSTRAINT  TITLE_BODY_IMAGE_SLIDE_PK PRIMARY KEY (SLIDE_ID),
    CONSTRAINT TITLE_BODY_IMAGE_SLIDE_FK1 FOREIGN KEY (SLIDE_ID) REFERENCES PRESENTER_OWNER.TITLE_IMAGE_SLIDE (SLIDE_ID) ON DELETE CASCADE
);

CREATE TABLE PRESENTER_OWNER.BODY_SLIDE
(
    SLIDE_ID             INTEGER NOT NULL ,
    BODY                 CLOB NULL ,
    BODY_SLIDE_TYPE      VARCHAR2(24) NOT NULL CONSTRAINT BODY_SLIDE_CK1 CHECK (BODY_SLIDE_TYPE IN ('SHIFT_OVERFLOW', 'PD_SUMMARY_OVERFLOW', 'PD_SUMMARY_PART_FOUR')),
    CONSTRAINT  BODY_SLIDE_PK PRIMARY KEY (SLIDE_ID),
    CONSTRAINT BODY_SLIDE_FK1 FOREIGN KEY (SLIDE_ID) REFERENCES PRESENTER_OWNER.SLIDE (SLIDE_ID) ON DELETE CASCADE
);

CREATE TABLE PRESENTER_OWNER.TITLE_BODY_SLIDE
(
    SLIDE_ID             INTEGER NOT NULL ,
    TITLE                VARCHAR2(128) NULL ,
    BODY                 CLOB NULL ,
    TITLE_BODY_SLIDE_TYPE VARCHAR2(24) NOT NULL CONSTRAINT TITLE_BODY_SLIDE_CK1 CHECK (TITLE_BODY_SLIDE_TYPE IN ('SINGLE_COLUMN', 'DYNAMIC_TWO_COLUMN')),
    CONSTRAINT TITLE_BODY_SLIDE_PK PRIMARY KEY (SLIDE_ID),
    CONSTRAINT TITLE_BODY_SLIDE_FK1 FOREIGN KEY (SLIDE_ID) REFERENCES PRESENTER_OWNER.SLIDE (SLIDE_ID) ON DELETE CASCADE
);

CREATE TABLE PRESENTER_OWNER.SHIFT_INFO_SLIDE
(
    SLIDE_ID             INTEGER NOT NULL ,
    YMD                  TIMESTAMP(0) NOT NULL  CONSTRAINT  SHIFT_INFO_SLIDE_CK1 CHECK (EXTRACT(HOUR FROM YMD) = 0 AND EXTRACT(MINUTE FROM YMD) = 0 AND EXTRACT(SECOND FROM YMD) = 0),
    SHIFT                VARCHAR2(5) NOT NULL ,
    TEAM                 VARCHAR2(48) NULL ,
    PROGRAM              VARCHAR2(48) NULL ,
    BODY                 CLOB NULL ,
    SHIFT_SLIDE_TYPE     VARCHAR2(20 CHAR) DEFAULT  'LSD'  NOT NULL  CONSTRAINT  SHIFT_INFO_SLIDE_CK2 CHECK (SHIFT_SLIDE_TYPE IN ('LSD', 'HCO', 'PD', 'LASO','LO','UITF')),
    CONSTRAINT  SHIFT_INFO_SLIDE_PK PRIMARY KEY (SLIDE_ID),
    CONSTRAINT SHIFT_INFO_SLIDE_FK1 FOREIGN KEY (SLIDE_ID) REFERENCES PRESENTER_OWNER.SLIDE (SLIDE_ID) ON DELETE CASCADE
);

CREATE TABLE PRESENTER_OWNER.BTA_SHIFT_INFO_SLIDE
(
    SLIDE_ID             INTEGER NOT NULL ,
    CONSTRAINT BTA_SHIFT_INFO_SLIDE_PK PRIMARY KEY (SLIDE_ID),
    CONSTRAINT BTA_SHIFT_INFO_SLIDE_FK1 FOREIGN KEY (SLIDE_ID) REFERENCES PRESENTER_OWNER.SHIFT_INFO_SLIDE (SLIDE_ID) ON DELETE CASCADE
);

CREATE TABLE PRESENTER_OWNER.BTA_RECORD
(
    BTA_RECORD_ID        INTEGER NOT NULL ,
    ORDER_ID             INTEGER NOT NULL ,
    HALL_PROGRAM         VARCHAR2(96) NULL ,
    SLIDE_ID             INTEGER NULL ,
    SCHEDULED            NUMBER NULL ,
    ACTUAL               NUMBER NULL ,
    ABU                  NUMBER NULL ,
    BANU                 NUMBER NULL ,
    CONSTRAINT BTA_RECORD_PK PRIMARY KEY (BTA_RECORD_ID),
    CONSTRAINT BTA_RECORD_AK1 UNIQUE (SLIDE_ID,ORDER_ID) DEFERRABLE INITIALLY DEFERRED,
    CONSTRAINT BTA_RECORD_FK1 FOREIGN KEY (SLIDE_ID) REFERENCES PRESENTER_OWNER.BTA_SHIFT_INFO_SLIDE (SLIDE_ID) ON DELETE CASCADE
);

CREATE TABLE PRESENTER_OWNER.PD_INFO_SLIDE
(
    SLIDE_ID             INTEGER NOT NULL ,
    PD                   VARCHAR2(64) NULL ,
    FROM_DATE            TIMESTAMP(0) NOT NULL  CONSTRAINT  PD_INFO_SLIDE_CK2 CHECK (EXTRACT(HOUR FROM FROM_DATE) = 0 AND EXTRACT(MINUTE FROM FROM_DATE) = 0 AND EXTRACT(SECOND FROM FROM_DATE) = 0),
    TO_DATE              TIMESTAMP(0) NOT NULL  CONSTRAINT  PD_INFO_SLIDE_CK3 CHECK (EXTRACT(HOUR FROM TO_DATE) = 0 AND EXTRACT(MINUTE FROM TO_DATE) = 0 AND EXTRACT(SECOND FROM TO_DATE) = 0),
    BODY                 CLOB NULL ,
    PD_INFO_SLIDE_TYPE   VARCHAR2(24) NOT NULL  CONSTRAINT  PD_INFO_SLIDE_CK1 CHECK (PD_INFO_SLIDE_TYPE IN ('FIRST_SUMMARY', 'SECOND_SUMMARY')),
    CONSTRAINT PD_INFO_SLIDE_PK PRIMARY KEY (SLIDE_ID),
    CONSTRAINT PD_INFO_SLIDE_FK1 FOREIGN KEY (SLIDE_ID) REFERENCES PRESENTER_OWNER.SLIDE (SLIDE_ID) ON DELETE CASCADE
);

CREATE TABLE PRESENTER_OWNER.IFRAME_SLIDE
(
    SLIDE_ID             INTEGER NOT NULL ,
    IFRAME_URL           VARCHAR2(1024) NOT NULL ,
    CONSTRAINT IFRAME_SLIDE_PK PRIMARY KEY (SLIDE_ID),
    CONSTRAINT IFRAME_SLIDE_FK1 FOREIGN KEY (SLIDE_ID) REFERENCES PRESENTER_OWNER.SLIDE (SLIDE_ID) ON DELETE CASCADE
);
