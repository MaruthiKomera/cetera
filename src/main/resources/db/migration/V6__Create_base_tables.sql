ALTER TABLE CETERA.RESOURCES_QA ADD STATUS VARCHAR2(20);
UPDATE CETERA.RESOURCES_QA SET STATUS = 'ACTIVE';
ALTER TABLE CETERA.RESOURCES_QA MODIFY STATUS VARCHAR2(20) NOT NULL;