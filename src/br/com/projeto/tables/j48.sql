CREATE TABLE j48
(
  opiniao numeric(10,5),
  fato numeric(10,5),
  fake numeric(10,5),
  twitter_id bigint NOT NULL,
  CONSTRAINT "Ref_j48_to_twitter" FOREIGN KEY (twitter_id)
      REFERENCES twitter (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)