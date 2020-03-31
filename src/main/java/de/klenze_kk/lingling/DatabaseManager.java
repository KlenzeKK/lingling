package de.klenze_kk.lingling;

import java.sql.*;
import java.util.*;
import java.util.function.Consumer;
import java.util.logging.Level;

import de.klenze_kk.lingling.logic.StatisticKey;
import de.klenze_kk.lingling.logic.User;
import de.klenze_kk.lingling.logic.Vocabulary;

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

    public void loadVocabulary(Consumer<Set<Vocabulary>> consumer) {
        new Thread(new VocabularyLoader(consumer)).start();
    }

    private static final String VOCABULARY_QUERY = "SELECT * FROM Vocabulary;";
    private static final String CHINESE_COLUMN = "Chinese";
    private static final String PINYIN_COLUMN = "Pinyin";
    private static final String RAW_PINYIN_COLUMN = "Raw_Pinyin";
    private static final String TRANSLATION_COLUMN = "Translation";
    private static final String TERM_COLUMN = "Term";
    private static final String PAGE_NUMBER_COLUMN = "Page_Number";

    private final class VocabularyLoader implements Runnable {

        private final Consumer<Set<Vocabulary>> consumer;

        protected VocabularyLoader(Consumer<Set<Vocabulary>> consumer) {
            this.consumer = consumer;
        }

        public void run() {
            final Set<Vocabulary> vocabulary = new HashSet<Vocabulary>();

            try {
                openConnection();

                final ResultSet table = connection.createStatement().executeQuery(VOCABULARY_QUERY);
                while (table.next()) {
                    final String chinese = table.getString(CHINESE_COLUMN);
                    if(chinese == null) continue;

                    final String translation = table.getString(TRANSLATION_COLUMN);
                    if(translation == null) continue;

                    String pinyin = table.getString(PINYIN_COLUMN);
                    if(pinyin == null) pinyin = "";

                    String rawPinyin = table.getString(RAW_PINYIN_COLUMN);
                    if(rawPinyin == null) rawPinyin = pinyin;

                    String term = table.getString(TERM_COLUMN);
                    if(term == null) term = "";

                    final short pageNumber = table.getShort(PAGE_NUMBER_COLUMN);

                    vocabulary.add(new Vocabulary(chinese, pinyin, rawPinyin, translation, term, pageNumber));
                }
            }
            catch (Exception ex) {
                Main.log(Level.SEVERE, "Failed to load vocabulary", ex);
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

        protected LoginTask(Consumer<User> consumer, String name, String password) {
            this.consumer = consumer;
            this.name = name;
            this.password = password;
        }

        public void run() {
            final User user;
            try {
                openConnection();

                final String query = "SELECT * FROM User WHERE " + USER_COLUMN + " = '" + name + "';";
                final ResultSet data = connection.createStatement().executeQuery(query);
                if(data.next()) {
                    if(password.equals(data.getString(PASSWORD_COLUMN))) {

                        final EnumMap<StatisticKey,Integer> stats = new EnumMap<StatisticKey,Integer>(StatisticKey.class);
                        // load statistics from ResultSet object

                        user = new User(name, stats);
                    }
                    else user = null;
                }
                else user = null;
            }
            catch (Exception ex) {
                Main.log(Level.SEVERE, "Failed to check login data", ex);
                return;
            }

            consumer.accept(user);
        }

    }

    public User registerUser(String userName, String password) {
        new Thread(new RegistrationTask(userName, password)).start();

        return new User(userName, new EnumMap<StatisticKey,Integer>(StatisticKey.class));
    }

    private final class RegistrationTask implements Runnable {

        private final String userName;
        private final String password;

        protected RegistrationTask(String userName, String password) {
            this.userName = userName;
            this.password = password;
        }

        public void run() { 
            try {
                openConnection();
                connection.createStatement().executeUpdate(this.buildCommand());
            }
            catch (Exception ex) {
                Main.log(Level.SEVERE, "Failed to save user data", ex);
            }
        }

        private String buildCommand() {
            final StatisticKey[] statKeys = StatisticKey.values();
            final StringBuilder cmdBuilder = new StringBuilder("INSERT INTO User (");
            cmdBuilder.append(USER_COLUMN).append(", ").append(PASSWORD_COLUMN);
            for(StatisticKey s: statKeys)
                cmdBuilder.append(", ").append(s.databaseColumn);

            cmdBuilder.append(") VALUES (").append(userName).append(", ").append(password);
            for(int i = 0; i < statKeys.length; i++)
                cmdBuilder.append(", ").append(0);
            
            return cmdBuilder.append(");").toString();
        }

    }

}
