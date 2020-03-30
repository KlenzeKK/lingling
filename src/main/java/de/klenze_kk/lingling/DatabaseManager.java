package de.klenze_kk.lingling;

import java.sql.*;
import java.util.*;
import java.util.function.Consumer;
import java.util.logging.Level;

import de.klenze_kk.lingling.logic.Vocabulary;

public final class DatabaseManager {

    private static final String DRIVER_CLASS = "com.mysql.jdbc.Driver";
    private static final String URL_PREFIX = "jdbc:mysql://";

    protected final String host, database, userName, password;
    protected final short port;

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
        new Thread(new VocabularyLoder(consumer)).start();
    }

    private static final String VOCABULARY_QUERY = "SELECT * FROM Vocabulary;";
    private static final String CHINESE_COLUMN = "Chinese";
    private static final String PINYIN_COLUMN = "Pinyin";
    private static final String RAW_PINYIN_COLUMN = "Raw_Pinyin";
    private static final String TRANSLATION_COLUMN = "Translation";
    private static final String TERM_COLUMN = "Term";
    private static final String PAGE_NUMBER_COLUMN = "Page_Number";

    private final class VocabularyLoder implements Runnable {

        private final Consumer<Set<Vocabulary>> consumer;

        protected VocabularyLoder(Consumer<Set<Vocabulary>> consumer) {
            this.consumer = consumer;
        }

        public synchronized void run() {
            final Set<Vocabulary> vocabulary = new HashSet<Vocabulary>();

            try {
                openConnection();

                final ResultSet table = connection.createStatement().executeQuery(VOCABULARY_QUERY);
                while (table.next()) {
                    final String chinese = table.getString(CHINESE_COLUMN);
                    if(chinese == null) continue;

                    String pinyin = table.getString(PINYIN_COLUMN);
                    if(pinyin == null) pinyin = "";

                    String rawPinyin = table.getString(RAW_PINYIN_COLUMN);
                    if(rawPinyin == null) rawPinyin = pinyin;

                    final String translation = table.getString(TRANSLATION_COLUMN);
                    if(translation == null) continue;

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

}
