
/* Drop Tables */

DROP TABLE IF EXISTS Retweeters;
DROP TABLE IF EXISTS RetweetedTweets;
DROP TABLE IF EXISTS StoredAccessTokens;
DROP TABLE IF EXISTS AuthorizedUsers;
DROP TABLE IF EXISTS Retweets;




/* Create Tables */

CREATE TABLE StoredAccessTokens
(
	-- ユーザID
	UserID bigint NOT NULL,
	Account varchar(256) NOT NULL UNIQUE,
	AccessToken varchar(50) NOT NULL,
	AccessTokenSecret varchar(50) NOT NULL,
	RegisteredAt timestamp DEFAULT now() NOT NULL,
	UpdatedAt timestamp DEFAULT now() NOT NULL,
	PRIMARY KEY (UserID)
) WITHOUT OIDS;


CREATE TABLE AuthorizedUsers
(
	RememberMe varchar(128) NOT NULL,
	UserID bigint NOT NULL,
	RegisteredAt timestamp DEFAULT now() NOT NULL,
	PRIMARY KEY (RememberMe)
) WITHOUT OIDS;


CREATE TABLE RetweetedTweets
(
	TweetID bigint NOT NULL,
	-- ユーザID
	UserID bigint NOT NULL,
	RegisteredAt timestamp DEFAULT now() NOT NULL,
	PRIMARY KEY (TweetID)
) WITHOUT OIDS;


CREATE TABLE Retweeters
(
	RetweetTo bigint NOT NULL,
	RetweeterID bigint NOT NULL,
	-- ユーザID
	UserID bigint NOT NULL,
	RegisteredAt timestamp DEFAULT now() NOT NULL,
	PRIMARY KEY (RetweetTo, RetweeterID)
) WITHOUT OIDS;


CREATE TABLE Retweets
(
	TweetID bigint NOT NULL,
	RetweetTo bigint NOT NULL,
	RetweeterID bigint NOT NULL,
	ScreenName varchar NOT NULL,
	RegisteredAt timestamp DEFAULT now() NOT NULL,
	PRIMARY KEY (TweetID)
) WITHOUT OIDS;



/* Create Foreign Keys */

ALTER TABLE Retweeters
	ADD FOREIGN KEY (UserID)
	REFERENCES StoredAccessTokens (UserID)
	ON UPDATE RESTRICT
	ON DELETE RESTRICT
;


ALTER TABLE RetweetedTweets
	ADD FOREIGN KEY (UserID)
	REFERENCES StoredAccessTokens (UserID)
	ON UPDATE RESTRICT
	ON DELETE RESTRICT
;



/* Comments */

COMMENT ON COLUMN StoredAccessTokens.UserID IS 'ユーザID';
COMMENT ON COLUMN RetweetedTweets.UserID IS 'ユーザID';
COMMENT ON COLUMN Retweeters.UserID IS 'ユーザID';



