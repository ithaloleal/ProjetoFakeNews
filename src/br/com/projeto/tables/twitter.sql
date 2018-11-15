CREATE TABLE twitter
(
  id_tweet bigint NOT NULL,
  message text,
  created_at date,
  latitude double precision,
  longitude double precision,
  favorite_count integer,
  retweet_count integer,
  id_user bigint,
  name_user character varying(100),
  is_retweet boolean,
  is_retweeted boolean,
  tag character varying(100) NOT NULL,
  id bigserial NOT NULL,
  class character(100),
  CONSTRAINT twitter_pkey PRIMARY KEY (id)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE twitter
  OWNER TO postgres;