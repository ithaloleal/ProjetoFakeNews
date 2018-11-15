package br.com.projeto.classes;

import twitter4j.*;
import twitter4j.conf.ConfigurationBuilder;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ColetaAPI {
    private static ConexaoBD conexaoBD = new ConexaoBD();

    public static void main(String[] args) throws SQLException {
        try {
            Connection con = conexaoBD.getCon();

            String dt_inicial = "2018-10-22";
            String dt_final = "2018-10-28";

            ArrayList<String> tags = new ArrayList();
            Collections.addAll(tags, "jair messias bolsonaro", "fernando haddad",
                    "vera lucia pereira da silva salgado", "alvaro dias", "cabo daciolo",
                    "ciro gomes", "jose maria eymael", "geraldo alckmin", "guilherme boulos",
                    "henrique meirelles", "joão amoêdo", "joão goulart filho", "marina silva",
                    "bolsonaro", "haddad", "daciolo", "ciro", "alckmin", "boulos", "meirelles",
                    "amoêdo", "joão goulart", "marina", "bolsonaro17", "haddad13", "ciro12",
                    "daciolo51", "alvaro19", "eymael27", "alckmin45", "boulos50", "meirelles15",
                    "amoedo30", "joaogoulart54", "marina18", "vera16");

            ConfigurationBuilder cb = new ConfigurationBuilder();
            cb.setDebugEnabled(true)
                    .setOAuthConsumerKey("")
                    .setOAuthConsumerSecret("")
                    .setOAuthAccessToken("")
                    .setOAuthAccessTokenSecret("")
                    .setTweetModeExtended(true);
            TwitterFactory tf = new TwitterFactory(cb.build());
            Twitter twitter = tf.getInstance();

            Query query = new Query(tags.get(0));
            query.setSince(dt_inicial);
            query.setUntil(dt_final);
            query.setSinceId(ultimoId(tags.get(0)));
            query.setCount(90);
            query.setLang("pt");
            QueryResult result = null;
            result = twitter.search(query);


            int pos = 0;

            do {
                try {

                    List<Status> tweets = result.getTweets();

                    for (Status tweet : tweets) {
                        System.out.println(tweet.getId());
                        if (tweet.getRetweetedStatus() != null) {
                            System.out.println(tweet.getRetweetedStatus().getText());
                        } else if (tweet.getRetweetedStatus() == null) {
                            System.out.println(tweet.getText());
                        }
                        System.out.println(tweet.getText());
                        System.out.println(new java.sql.Date(tweet.getCreatedAt().getTime()));
                        System.out.println(tweet.getGeoLocation() == null ? 0 : tweet.getGeoLocation().getLatitude());
                        System.out.println(tweet.getGeoLocation() == null ? 0 : tweet.getGeoLocation().getLongitude());
                        System.out.println(tweet.getFavoriteCount());
                        System.out.println(tweet.getRetweetCount());
                        System.out.println(tweet.getUser().getId());
                        System.out.println(tweet.getUser().getName());
                        System.out.println(tweet.isRetweet());
                        System.out.println(tweet.isRetweeted());
                        System.out.println(tags.get(pos));
                        System.out.println("\n\n\n");

                        PreparedStatement cmd = con.prepareStatement("INSERT INTO twitter (id_tweet, message, created_at, latitude, " +
                                "longitude, favorite_count, retweet_count, id_user, name_user, is_retweet, is_retweeted, tag) " +
                                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ? ,?, ?)");
                        cmd.setLong(1, tweet.getId());
                        if (tweet.getRetweetedStatus() != null) {
                            cmd.setString(2, tweet.getRetweetedStatus().getText());
                        } else if (tweet.getRetweetedStatus() == null) {
                            cmd.setString(2, tweet.getText());
                        }
                        cmd.setDate(3, new java.sql.Date(tweet.getCreatedAt().getTime()));
                        cmd.setDouble(4, tweet.getGeoLocation() == null ? 0 : tweet.getGeoLocation().getLatitude());
                        cmd.setDouble(5, tweet.getGeoLocation() == null ? 0 : tweet.getGeoLocation().getLongitude());
                        cmd.setInt(6, tweet.getFavoriteCount());
                        cmd.setInt(7, tweet.getRetweetCount());
                        cmd.setLong(8, tweet.getUser().getId());
                        cmd.setString(9, tweet.getUser().getName());
                        cmd.setBoolean(10, tweet.isRetweet());
                        cmd.setBoolean(11, tweet.isRetweeted());
                        cmd.setString(12, tags.get(pos));
                        cmd.executeUpdate();

                    }
                    if (twitter.getRateLimitStatus().get("/search/tweets").getRemaining() - 20 <= 0) {
                        System.out.println("Pausado mineração...");
                        Thread.sleep(918000);
                        System.out.println("Pausado mineração...");
                    }
                    query = result.nextQuery();

                    if (query != null) {
                        query.setSinceId(ultimoId(tags.get(pos)));
                        result = twitter.search(query);
                    } else {
                        pos++;
                        if (pos < tags.size()) {
                            query = new Query(tags.get(pos));
                            query.setSince(dt_inicial);
                            query.setUntil(dt_final);
                            query.setSinceId(ultimoId(tags.get(pos)));
                            query.setCount(90);
                            query.setLang("pt");
                            result = twitter.search(query);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    System.out.println(e.getMessage());
                }
            } while (query != null);

        } catch (TwitterException e) {
            e.printStackTrace();
        }
    }

    private static Long ultimoId(String tag) throws SQLException {
        PreparedStatement cmd = conexaoBD.getCon().prepareStatement("SELECT id_tweet FROM twitter WHERE tag = ? ORDER BY id_tweet DESC LIMIT 1");
        cmd.setString(1, tag);
        ResultSet result = cmd.executeQuery();
        if (result.next()) {
            return result.getLong("id_tweet");
        }
        return Long.MIN_VALUE;
    }
}
