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
    private static final String USERNAME_VAR = "%userName%";
    private static final String LOGIN_QUERY;
    static {
        LOGIN_QUERY = new StringBuilder("SELECT * FROM User WHERE ")
                        .append(USER_COLUMN).append(" = '").append(USERNAME_VAR).append("';").toString();
    }

    
    private static final StatisticKey[] STAT_KEYS = StatisticKey.values();

    public void performLogin(Consumer<LoginResponse> consumer, String userName, String password) {
        new Thread(new LoginTask(consumer, userName, password)).start();
    }

    private final class LoginTask implements Runnable {

        private final Consumer<LoginResponse> consumer;
        private final String name;
        private final String password;

        protected LoginTask(Consumer<LoginResponse> consumer, String name, String password) {
            this.consumer = consumer;
            this.name = name;
            this.password = password;
        }

        public void run() {
            final LoginResponse response;
            try {
                openConnection();

                final ResultSet data = connection.createStatement().executeQuery(LOGIN_QUERY.replaceAll(USERNAME_VAR, name));
                if(data.next()) {
                    if(password.equals(data.getString(PASSWORD_COLUMN))) {

                        final EnumMap<StatisticKey,Integer> stats = new EnumMap<StatisticKey,Integer>(StatisticKey.class);
                        // load statistics from ResultSet object

                        response = new LoginResponse.SuccessfulLogin(new User(name, stats));
                    }
                    else response = LoginResponse.FailedLogin.WRONG_PASSWORD;
                }
                else response = LoginResponse.FailedLogin.USER_UNKNOWN;
            }
            catch (Exception ex) {
                Main.log(Level.SEVERE, "Failed to check login data", ex);
                return;
            }

            consumer.accept(response);
        }

    }

}
