create database card_game;

\c card_game;

drop table if exists "user_to_game_session_started";
drop table if exists "user_to_game_session";
drop table if exists "card_to_game_session_card_started";
drop table if exists "turn";
drop table if exists "card";
drop table if exists "game_session";
drop table if exists "user";

CREATE TABLE "user" (
	"id" SERIAL NOT NULL,
	"login" VARCHAR(255) NOT NULL,
	"name" VARCHAR(255) NOT NULL,
	"password" VARCHAR(255) NOT NULL,
	PRIMARY KEY ("id"),
	UNIQUE ("login")
);

CREATE TABLE "game_session" (
	"id" SERIAL NOT NULL,
	"state" SMALLINT NOT NULL,
	"users_num" INTEGER NOT NULL,
	"created_by" INTEGER NOT NULL,
	"cards_num" INTEGER NULL DEFAULT NULL,
	PRIMARY KEY ("id"),
	CONSTRAINT "game_session_created_by" FOREIGN KEY ("created_by") REFERENCES "user" ("id") ON UPDATE NO ACTION ON DELETE NO ACTION,
	CONSTRAINT "game_session_state_check" CHECK (((state >= 0) AND (state <= 2))),
	CONSTRAINT "game_session_users_num_check" CHECK (((users_num >= 0) AND (users_num <= 4)))
);

CREATE TABLE "card" (
	"id" SERIAL NOT NULL,
	"action_card_type" SMALLINT NULL DEFAULT NULL,
	"name" VARCHAR(255) NULL DEFAULT NULL,
	"type" SMALLINT NOT NULL,
	"value" INTEGER NOT NULL,
	PRIMARY KEY ("id"),
	UNIQUE ("name"),
	CONSTRAINT "card_action_card_type_check" CHECK (((action_card_type >= 0) AND (action_card_type <= 2))),
	CONSTRAINT "card_type_check" CHECK (((type >= 0) AND (type <= 1)))
);

CREATE TABLE "turn" (
	"id" SERIAL NOT NULL,
	"turn_num" INTEGER NOT NULL,
	"card_id" INTEGER NOT NULL,
	"game_session_id" INTEGER NOT NULL,
	"user_id" INTEGER NOT NULL,
	"points" INTEGER NULL DEFAULT NULL,
	"target_user_id" INTEGER NULL DEFAULT NULL,
	"points_difference" INTEGER NOT NULL,
	PRIMARY KEY ("id"),
	CONSTRAINT "turn_target_user_id" FOREIGN KEY ("target_user_id") REFERENCES "user" ("id") ON UPDATE NO ACTION ON DELETE NO ACTION,
	CONSTRAINT "turn_user_id" FOREIGN KEY ("user_id") REFERENCES "user" ("id") ON UPDATE NO ACTION ON DELETE NO ACTION,
	CONSTRAINT "turn_card_id" FOREIGN KEY ("card_id") REFERENCES "card" ("id") ON UPDATE NO ACTION ON DELETE NO ACTION,
	CONSTRAINT "turn_game_session_id" FOREIGN KEY ("game_session_id") REFERENCES "game_session" ("id") ON UPDATE NO ACTION ON DELETE NO ACTION
);

CREATE TABLE "card_to_game_session_card_started" (
	"card_id" INTEGER NOT NULL,
	"game_session_id" INTEGER NOT NULL,
	"is_current" BOOLEAN NULL DEFAULT NULL,
	"order_num" INTEGER NOT NULL,
	PRIMARY KEY ("card_id", "game_session_id"),
	CONSTRAINT "to_card_id" FOREIGN KEY ("card_id") REFERENCES "card" ("id") ON UPDATE NO ACTION ON DELETE NO ACTION,
	CONSTRAINT "to_game_session_id" FOREIGN KEY ("game_session_id") REFERENCES "game_session" ("id") ON UPDATE NO ACTION ON DELETE NO ACTION
);

CREATE TABLE "user_to_game_session" (
	"game_session_id" INTEGER NOT NULL,
	"user_id" INTEGER NOT NULL,
	PRIMARY KEY ("game_session_id", "user_id"),
	CONSTRAINT "to_game_session_id" FOREIGN KEY ("game_session_id") REFERENCES "game_session" ("id") ON UPDATE NO ACTION ON DELETE NO ACTION,
	CONSTRAINT "to_user_id" FOREIGN KEY ("user_id") REFERENCES "user" ("id") ON UPDATE NO ACTION ON DELETE NO ACTION
);

CREATE TABLE "user_to_game_session_started" (
	"game_session_id" INTEGER NOT NULL,
	"user_id" INTEGER NOT NULL,
	"points" INTEGER NOT NULL DEFAULT 0,
	"is_current" BOOLEAN NULL DEFAULT NULL,
	"order_num" INTEGER NOT NULL,
	PRIMARY KEY ("game_session_id", "user_id"),
	CONSTRAINT "to_game_session_id" FOREIGN KEY ("game_session_id") REFERENCES "game_session" ("id") ON UPDATE NO ACTION ON DELETE NO ACTION,
	CONSTRAINT "to_user_id" FOREIGN KEY ("user_id") REFERENCES "user" ("id") ON UPDATE NO ACTION ON DELETE NO ACTION
);

INSERT INTO "card" ("action_card_type", "name", "type", "value") VALUES (NULL, 'GainCard2', 0, 10);
INSERT INTO "card" ("action_card_type", "name", "type", "value") VALUES (0, 'BlockCard1', 1, 1);
INSERT INTO "card" ("action_card_type", "name", "type", "value") VALUES (1, 'StealCard1', 1, 5);
INSERT INTO "card" ("action_card_type", "name", "type", "value") VALUES (2, 'DoubleDown1', 1, 2);
INSERT INTO "card" ("action_card_type", "name", "type", "value") VALUES (NULL, 'GainCard1', 0, 5);
INSERT INTO "card" ("action_card_type", "name", "type", "value") VALUES (1, 'StealCard2', 1, 5);
INSERT INTO "card" ("action_card_type", "name", "type", "value") VALUES (0, 'BlockCard2', 1, 1);
INSERT INTO "card" ("action_card_type", "name", "type", "value") VALUES (NULL, 'GainCard3', 0, 7);
INSERT INTO "card" ("action_card_type", "name", "type", "value") VALUES (NULL, 'GainCard4', 0, 15);
INSERT INTO "card" ("action_card_type", "name", "type", "value") VALUES (2, 'DoubleDown2', 1, 2);
