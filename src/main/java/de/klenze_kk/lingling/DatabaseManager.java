package de.klenze_kk.lingling;

import java.sql.*;
import java.util.*;
import java.util.function.Consumer;

import de.klenze_kk.lingling.logic.*;

public final class DatabaseManager {

    private static final String DRIVER_CLASS = "com.mysql.jdbc.Driver";
    private static final String URL_PREFIX = "jdbc:mysql://";

    private final String host, database, userName, password;
    private final short port;

    protected Connection connection;

    public DatabaseManager(String host, short port, String database, String userName, String password) {
        this.host = host;
        this.database = database;
        this.userName = userName;
        this.password = password;
        this.port = port;
    }

    protected synchronized void openConnection() throws SQLException {
        if(connection != null && !connection.isClosed()) return;

        try {
            Class.forName(DRIVER_CLASS);
        }
        catch (ClassNotFoundException ex) {
            throw new Error(ex);
        }

        String url = new StringBuilder(URL_PREFIX).append(host).append(':').append(port).append('/').append(database).toString();
        this.connection = DriverManager.getConnection(url, userName, password);
    }

    public void loadVocabulary(Consumer<List<Vocabulary>> consumer) {
        new Thread(new VocabularyLoader(consumer)).start();
    }

    private static final String VOCABULARY_QUERY = "SELECT * FROM Vocabulary;";
    private static final String CHINESE_COLUMN = "Chinese";
    private static final String PINYIN_COLUMN = "Pinyin";
    private static final String RAW_PINYIN_COLUMN = "Raw_Pinyin";
    private static final String TRANSLATION_COLUMN = "Translation";
    private static final String TERM_COLUMN = "Term";
    private static final String PAGE_NUMBER_COLUMN = "Page_Number";
    private static final String GIF_COLUMN = "Gif";

    private final class VocabularyLoader implements Runnable {

        private final Consumer<List<Vocabulary>> consumer;

        protected VocabularyLoader(Consumer<List<Vocabulary>> consumer) {
            this.consumer = consumer;
        }

        public void run() {
            final List<Vocabulary> vocabulary = new LinkedList<Vocabulary>();

            try {
                openConnection();

                final ResultSet table = connection.createStatement().executeQuery(VOCABULARY_QUERY);
                String chinese, translation, pinyin, rawPinyin, term;
                short pageNumber;
                byte[] gif;
                while (table.next()) {
                    chinese = table.getString(CHINESE_COLUMN);
                    if(chinese == null) continue;

                    translation = table.getString(TRANSLATION_COLUMN);
                    if(translation == null) continue;

                    pinyin = table.getString(PINYIN_COLUMN);
                    if(pinyin == null) pinyin = "";

                    rawPinyin = table.getString(RAW_PINYIN_COLUMN);
                    if(rawPinyin == null) rawPinyin = pinyin;

                    term = table.getString(TERM_COLUMN);
                    if(term == null) term = "";

                    pageNumber = table.getShort(PAGE_NUMBER_COLUMN);

                    gif = table.getBytes(GIF_COLUMN);
                    if(gif == null) gif = new byte[] {};

                    vocabulary.add(new Vocabulary(chinese, pinyin, rawPinyin, translation, term, pageNumber, gif));
                }
            }
            catch (Exception ex) {
                Main.handleError("Failed to load vocabulary: " + ex, true);
                return;
            }

            consumer.accept(vocabulary);
        }

    }

    private static final String USER_COLUMN = "Username";
    private static final String PASSWORD_COLUMN = "Password";

    public void performLogin(Consumer<User> consumer, String userName, String password) {
        new Thread(new LoginTask(consumer, userName, password)).start();
    }

    private final class LoginTask implements Runnable {

        private final Consumer<User> consumer;
        private final String name;
        private final String password;
        private final String query;

        protected LoginTask(Consumer<User> consumer, String name, String password) {
            this.consumer = consumer;
            this.name = name;
            this.password = password;
            this.query = new StringBuilder()
                .append("SELECT * FROM User WHERE ")
                .append(USER_COLUMN)
                .append(" = '").append(name)
                .append("';").toString();
        }

        public void run() {
            final User user;
            try {
                openConnection();

                final ResultSet data = connection.createStatement().executeQuery(query);
                if(data.next()) {
                    if(password.equals(data.getString(PASSWORD_COLUMN))) {

                        final EnumMap<StatisticKey,Integer> stats = new EnumMap<StatisticKey,Integer>(StatisticKey.class);
                        for(StatisticKey key: StatisticKey.values())
                            stats.put(key, data.getInt(key.databaseColumn));

                        user = new User(name, stats);
                    }
                    else user = null;
                }
                else user = null;
            }
            catch (Exception ex) {
                Main.handleError("Failed to check login data: " + ex, true);
                return;
            }

            consumer.accept(user);
        }

    }

    public void registerUser(Consumer<User> consumer, String userName, String password) {
        new Thread(new RegistrationTask(consumer, userName, password)).start();
    }

    private final class RegistrationTask implements Runnable {

        private final Consumer<User> consumer;
        private final String userName;
        private final String query;
        private final String updateCommand;

        protected RegistrationTask(Consumer<User> consumer, String userName, String password) {
            this.consumer = consumer;
            this.userName = userName;
            this.query = new StringBuilder()
                .append("SELECT COUNT(*) AS Nutzerzahl FROM User WHERE ")
                .append(USER_COLUMN).append(" = '")
                .append(userName).append("';")
                .toString();
            this.updateCommand = new StringBuilder()
                .append("INSERT INTO User (")
                .append(USER_COLUMN).append(", ")
                .append(PASSWORD_COLUMN)
                .append(") VALUES (")
                .append(userName).append(",")
                .append(password).append(");")
                .toString();
        }

        public void run() { 
            try {  
                openConnection();
                final Statement statement = connection.createStatement();
                final ResultSet result = statement.executeQuery(query);
                result.next();

                if(result.getInt("Nutzerzahl") <= 0) {
                    consumer.accept(new User(userName, new EnumMap<StatisticKey,Integer>(StatisticKey.class)));
                    statement.executeUpdate(updateCommand);
                }
                else consumer.accept(null);
            }
            catch (Exception ex) {
                Main.handleError("Failed to register user: " + ex, true);
            }
        }

    }

    public void updateStats(User user, StatisticKey key, int newValue) {
        new Thread(new StatisticUpdater(user, key, newValue)).start();
    }

    private final class StatisticUpdater implements Runnable {

        private final String command;

        protected StatisticUpdater(User user, StatisticKey key, int newValue) {
            this.command = new StringBuilder()
                .append("UPDATE User SET ")
                .append(key.databaseColumn)
                .append(" = ").append(newValue)
                .append("WHERE").append(USER_COLUMN)
                .append(" = '").append(user.name)
                .append("';")
                .toString();
        }

        public void run() {
            try {
                openConnection();
                connection.createStatement().executeUpdate(command);
            }
            catch (Exception ex) {
                Main.handleError("Failed to store statistics: " + ex, false);
            }
        }

    }

    public void createRanking(Consumer<LinkedHashMap<String,Integer>> consumer, StatisticKey key) {
        new Thread(new RankingCreator(consumer, key)).start();
    }

    private final class RankingCreator implements Runnable {

        private final Consumer<LinkedHashMap<String,Integer>> consumer;
        private final StatisticKey key;
        private final String query;

        protected RankingCreator(Consumer<LinkedHashMap<String,Integer>> consumer, StatisticKey key) {
            this.consumer = consumer;
            this.key = key;
            this.query = new StringBuilder()
                .append("SELECT ")
                .append(USER_COLUMN).append(", ")
                .append(key.databaseColumn)
                .append(" FROM User ORDER BY ")
                .append(key.databaseColumn)
                .append(" DESC;")
                .toString();
        }

        public void run() {
            final LinkedHashMap<String,Integer> ranking = new LinkedHashMap<String,Integer>();
            try {
                openConnection();

                final ResultSet stats = connection.createStatement().executeQuery(query);
                String userName;
                while (stats.next()) {
                    userName = stats.getString(USER_COLUMN);
                    if(userName == null) continue;

                    ranking.put(userName, stats.getInt(key.databaseColumn));
                }
            }
            catch (Exception ex) {
                consumer.accept(null);
                Main.handleError("Failed to load statistics: " + ex, false);
                return;
            }

            consumer.accept(ranking);
        }

    }

}
