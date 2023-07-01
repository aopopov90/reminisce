 -- liquibase formatted sql

-- changeset antonpopov:1688207012599-1
CREATE SEQUENCE  IF NOT EXISTS "_user_seq" AS bigint START WITH 1 INCREMENT BY 50 MINVALUE 1 MAXVALUE 9223372036854775807 CACHE 1;

-- changeset antonpopov:1688207012599-2
CREATE SEQUENCE  IF NOT EXISTS "comment_seq" AS bigint START WITH 1 INCREMENT BY 50 MINVALUE 1 MAXVALUE 9223372036854775807 CACHE 1;

-- changeset antonpopov:1688207012599-3
CREATE SEQUENCE  IF NOT EXISTS "participation_seq" AS bigint START WITH 1 INCREMENT BY 50 MINVALUE 1 MAXVALUE 9223372036854775807 CACHE 1;

-- changeset antonpopov:1688207012599-4
CREATE SEQUENCE  IF NOT EXISTS "reaction_seq" AS bigint START WITH 1 INCREMENT BY 50 MINVALUE 1 MAXVALUE 9223372036854775807 CACHE 1;

-- changeset antonpopov:1688207012599-5
CREATE SEQUENCE  IF NOT EXISTS "session_seq" AS bigint START WITH 1 INCREMENT BY 50 MINVALUE 1 MAXVALUE 9223372036854775807 CACHE 1;

-- changeset antonpopov:1688207012599-6
CREATE TABLE "_user" ("id" BIGINT NOT NULL, "display_name" VARCHAR(255), "password" VARCHAR(255), "role" VARCHAR(255), CONSTRAINT "_user_pkey" PRIMARY KEY ("id"));

-- changeset antonpopov:1688207012599-7
CREATE TABLE "comment" ("id" BIGINT NOT NULL, "authored_by" VARCHAR(255), "category_id" INTEGER, "created_on" TIMESTAMP WITH TIME ZONE, "session_id" BIGINT, "text" VARCHAR(255), CONSTRAINT "comment_pkey" PRIMARY KEY ("id"));

-- changeset antonpopov:1688207012599-8
CREATE TABLE "participation" ("id" BIGINT NOT NULL, "added_at" TIMESTAMP WITH TIME ZONE, "added_by" VARCHAR(255), "participant_name" VARCHAR(255), "session_id" BIGINT NOT NULL, CONSTRAINT "participation_pkey" PRIMARY KEY ("id"));

-- changeset antonpopov:1688207012599-9
CREATE TABLE "reaction" ("id" BIGINT NOT NULL, "authored_by" VARCHAR(255), "comment_id" BIGINT, "created_on" TIMESTAMP WITH TIME ZONE, "reaction_type" VARCHAR(255), CONSTRAINT "reaction_pkey" PRIMARY KEY ("id"));

-- changeset antonpopov:1688207012599-10
CREATE TABLE "session" ("id" BIGINT NOT NULL, "created_by" VARCHAR(255), "created_on" TIMESTAMP WITH TIME ZONE, "ended_on" TIMESTAMP WITH TIME ZONE, "name" VARCHAR(255), "status" VARCHAR(255), CONSTRAINT "session_pkey" PRIMARY KEY ("id"));

-- changeset antonpopov:1688207012599-11
ALTER TABLE "comment" ADD CONSTRAINT "fkgbigtugv1y5rq2dv37vamljf8" FOREIGN KEY ("session_id") REFERENCES "session" ("id") ON UPDATE NO ACTION ON DELETE NO ACTION;

-- changeset antonpopov:1688207012599-12
ALTER TABLE "reaction" ADD CONSTRAINT "fkskbqddo2ffvogxr3f22awp2wa" FOREIGN KEY ("comment_id") REFERENCES "comment" ("id") ON UPDATE NO ACTION ON DELETE NO ACTION;

