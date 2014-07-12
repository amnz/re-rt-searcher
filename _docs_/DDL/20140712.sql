ALTER TABLE RetweetedTweets RENAME TO RetweetedTweetsBackup20140712;
ALTER TABLE Retweets RENAME TO RetweetsBackup20140712;



CREATE TABLE RetweetedTweets
(
	TweetID bigint NOT NULL,
	UserID bigint NOT NULL,
	TweetText varchar,
	CreatedAt timestamp,
	RegisteredAt timestamp DEFAULT now() NOT NULL,
	PRIMARY KEY (TweetID)
) WITHOUT OIDS;


CREATE TABLE Retweets
(
	TweetID bigint NOT NULL,
	RetweetTo bigint NOT NULL,
	RetweeterID bigint NOT NULL,
	ScreenName varchar NOT NULL,
	TweetText varchar,
	CreatedAt timestamp,
	RegisteredAt timestamp DEFAULT now() NOT NULL,
	PRIMARY KEY (TweetID, RetweetTo)
) WITHOUT OIDS;



ALTER TABLE Retweets ADD Skiptweets int DEFAULT 0 NOT NULL;
