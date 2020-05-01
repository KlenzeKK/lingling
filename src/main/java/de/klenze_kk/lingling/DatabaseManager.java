package de.klenze_kk.lingling;

import java.sql.*;
import java.util.*;
import java.util.function.Consumer;

import de.klenze_kk.lingling.logic.*;

/*
<dependency>
    <groupId>mysql</groupId>
    <artifactId>mysql-connector-java</artifactId>
    <version>5.1.36</version>
</dependency>
*/

public final class DatabaseManager {

    private static final String DRIVER_CLASS = "com.mysql.cj.jdbc.Driver";
    private static final String URL_PREFIX = "jdbc:mysql://";

    private final String url, userName, password;

    protected Connection connection;

    public DatabaseManager(String host, short port, String database, String userName, String password) {
        this.userName = userName;
        this.password = password;
        this.url = new StringBuilder(URL_PREFIX).append(host).append(':').append(port).append('/').append(database).toString();
    }

    protected synchronized void openConnection() throws SQLException {
        if(connection != null && !connection.isClosed()) return;

        try {
            Class.forName(DRIVER_CLASS);
        }
        catch (ClassNotFoundException ex) {
            throw new Error(ex);
        }

        this.connection = DriverManager.getConnection(url, userName, password);
    }

    public void closeConnection() {
        if(connection == null) return;

        try {
            if(!connection.isClosed())
                connection.close();
            connection = null;
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void loadVocabulary(User u, Consumer<List<Vocabulary>> vocConsumer, Consumer<Set<VocabularySet>> setConsumer) {
       new Thread(new VocabularyLoader(u, vocConsumer, setConsumer)).start();
    }

    private static final String VOC_ID_COLUMN = "VocID";
    private static final String CHINESE_COLUMN = "Chinese";
    private static final String PINYIN_COLUMN = "Pinyin";
    private static final String RAW_PINYIN_COLUMN = "Raw_Pinyin";
    private static final String TRANSLATION_COLUMN = "Translation";
    private static final String TERM_COLUMN = "Term";
    private static final String PAGE_NUMBER_COLUMN = "Page_Number";
    private static final String GIF_QUERY = "SELECT * FROM Gifs";
    private static final String VOCABULARY_QUERY = 
        new StringBuilder("SELECT * FROM Vocabulary ORDER BY ").append(TRANSLATION_COLUMN).append(";").toString();

    private static final String SET_ID_COLUMN = "SetID";
    private static final String SET_NAME_COLUMN = "Name";

    private static final String CHARACTER_COLUMN = "Character";
    private static final String GIF_COLUMN = "Gif";

    private final class VocabularyLoader implements Runnable {

        private final Consumer<List<Vocabulary>> vocConsumer;
        private final Consumer<Set<VocabularySet>> setConsumer;
        private final String vocabularySetQuery;

        protected VocabularyLoader(User user, Consumer<List<Vocabulary>> vocConsumer, Consumer<Set<VocabularySet>> setConsumer) {
            this.vocConsumer = vocConsumer;
            this.setConsumer = setConsumer;
            this.vocabularySetQuery = new StringBuilder()
                .append("SELECT * FROM VocabularySet, SetContents WHERE VocabularySet.")
                .append(SET_ID_COLUMN).append(" = SetContents.")
                .append(SET_ID_COLUMN).append(" AND (")
                .append(USER_COLUMN).append(" IS NULL OR ")
                .append(USER_COLUMN).append(" = '")
                .append(user.name).append("');")
                .toString();
        }

        public void run() {
            Statement statement = null;
            ResultSet result = null;
            try {
                openConnection();

                statement = connection.createStatement();
                result = statement.executeQuery(VOCABULARY_QUERY);

                final Map<Integer,Vocabulary> vocabulary = this.loadVocabulary(result);
                result.close();
                result = null;

                setConsumer.accept(this.loadSets(result = statement.executeQuery(vocabularySetQuery), vocabulary));
                result.close();
                result = null;

                result = statement.executeQuery(GIF_QUERY);
                while (result.next())
                    Main.VOCABULARY.registerGif(result.getString(CHARACTER_COLUMN).charAt(0), result.getBytes(GIF_COLUMN));

                vocConsumer.accept(new ArrayList<Vocabulary>(vocabulary.values()));
            }
            catch (Exception ex) {
                Main.handleError("Failed to load vocabulary: " + ex, true);
                ex.printStackTrace();
                return;
            }
            finally { 
                closeResources(statement, result);
            }
        }

        private Map<Integer,Vocabulary> loadVocabulary(ResultSet result) throws SQLException {
            final Map<Integer,Vocabulary> vocabulary = new LinkedHashMap<Integer,Vocabulary>();

            int id;
            short pageNumber;
            String chinese, translation, pinyin, rawPinyin, term;
            while (result.next()) {
                id = result.getInt(VOC_ID_COLUMN);

                chinese = result.getString(CHINESE_COLUMN);
                if(chinese == null) continue;

                translation = result.getString(TRANSLATION_COLUMN);
                if(translation == null) continue;

                pinyin = result.getString(PINYIN_COLUMN);
                if(pinyin == null) pinyin = "";

                rawPinyin = result.getString(RAW_PINYIN_COLUMN);
                if(rawPinyin == null) rawPinyin = pinyin;

                term = result.getString(TERM_COLUMN);
                if(term == null) term = "";

                pageNumber = result.getShort(PAGE_NUMBER_COLUMN);

                vocabulary.put(id, new Vocabulary(id, chinese, pinyin, rawPinyin, translation, term, pageNumber));
            }

            return vocabulary;
        }

        private Set<VocabularySet> loadSets(ResultSet result, Map<Integer,Vocabulary> vocs) throws SQLException {
            final Map<Integer,VocabularySet> sets = new HashMap<Integer,VocabularySet>();

            int setId;
            String setName;
            boolean templateSet;
            VocabularySet set;
            Vocabulary currentVoc;
            while (result.next()) {
                if((set = sets.get(setId = result.getInt("VocabularySet." + SET_ID_COLUMN))) == null) {
                    templateSet = result.getString(USER_COLUMN).isEmpty();
                    setName = result.getString(SET_NAME_COLUMN);
                    if(setName == null) continue;

                    sets.put(setId, set = new VocabularySet(setId, setName, templateSet));
                }

                currentVoc = vocs.get(result.getInt(VOC_ID_COLUMN));
                if(currentVoc == null) continue;

                set.registerVoc(currentVoc);
            }

            return new HashSet<VocabularySet>(sets.values());
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
            Statement statement = null;
            ResultSet data = null;
            try {
                openConnection();

                data = (statement = connection.createStatement()).executeQuery(query);
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
            finally {
                closeResources(statement, data);
            }

            consumer.accept(user);
        }

    }

    public void registerUser(Consumer<User> consumer, String userName, String password) {
        new RegistrationTask(consumer, userName, password).run();
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
                .append("SELECT COUNT(*) AS User_Count FROM User WHERE ")
                .append(USER_COLUMN).append(" = '")
                .append(userName).append("';")
                .toString();
            this.updateCommand = new StringBuilder()
                .append("INSERT INTO User (")
                .append(USER_COLUMN).append(", ")
                .append(PASSWORD_COLUMN)
                .append(") VALUES ('")
                .append(userName).append("', '")
                .append(password).append("');")
                .toString();
        }

        public void run() { 
            Statement statement = null;
            ResultSet result = null;
            try {  
                openConnection();
                statement = connection.createStatement();
                result = statement.executeQuery(query);
                result.next();

                if(result.getInt("User_Count") <= 0) {
                    consumer.accept(new User(userName, new EnumMap<StatisticKey,Integer>(StatisticKey.class)));
                    statement.executeUpdate(updateCommand);
                }
                else consumer.accept(null);
            }
            catch (Exception ex) {
                Main.handleError("Failed to register user: " + ex, true);
            }
            finally {
                closeResources(statement, result);
            }
        }

    }

    private static final String[] SET_GENERATED_KEY_COLUMNS = { SET_ID_COLUMN }; 

    public void createVocabularySet(Consumer<VocabularySet> consumer, User user, String name, Set<Vocabulary> initialContent) {
        new Thread(new SetCreator(consumer, user, name, initialContent)).start();
    }

    private final class SetCreator implements Runnable {

        private final Consumer<VocabularySet> consumer;
        private final Set<Vocabulary> initialContent;
        private final String name;
        private final String command;

        protected SetCreator(Consumer<VocabularySet> consumer, User user, String name, Set<Vocabulary> initialContent) {
            this.consumer = consumer;
            this.name = name;
            this.initialContent = initialContent;
            this.command = new StringBuilder()
                .append("INSERT INTO VocabularySet VALUES (DEFAULT, '")
                .append(user.name).append("', '")
                .append(name).append("');")
                .toString();
        }

        public void run() {
            PreparedStatement statement = null;
            ResultSet result = null;
            try {
                openConnection();

                statement = connection.prepareStatement(command, SET_GENERATED_KEY_COLUMNS);
                statement.executeUpdate();
                result = statement.getGeneratedKeys();
                if(!result.next()) throw new SQLException("Insert failed - no generated key was returned");

                final VocabularySet createdSet = new VocabularySet(result.getInt(1), name, false);
                consumer.accept(createdSet);
                new SetModifier(createdSet, initialContent, null).run();
            }
            catch (Exception ex) {
                Main.handleError("Failed to create vocabulary set: " + ex, false);
            }
            finally {
                closeResources(statement, result);
            }
        }

    }

    private static final String SET_ADD_VOC_COMMAND = "INSERT INTO SetContents VALUES (?, ?);";
    private static final String SET_REMOVE_VOC_COMMAND = 
        new StringBuilder("DELETE FROM SetContents WHERE ")
            .append(SET_ID_COLUMN)
            .append("=? AND ")
            .append(VOC_ID_COLUMN)
            .append("=?;")
            .toString();

    public void modifyVocabularySet(VocabularySet set, Set<Vocabulary> add, Set<Vocabulary> remove) {
        new Thread(new SetModifier(set, add, remove)).start();
    }

    private final class SetModifier implements Runnable {

        private final int setId;
        private final Set<Vocabulary> add;
        private final Set<Vocabulary> remove;

        protected SetModifier(VocabularySet set, Set<Vocabulary> add, Set<Vocabulary> remove) {
            this.setId = set.id;
            this.add = add;
            this.remove = remove;
        }

        public void run() {
            Statement statement = null;
            ResultSet result = null;
            try {
                openConnection();
                
                PreparedStatement command;
                if(add != null) {
                    command = connection.prepareStatement(SET_ADD_VOC_COMMAND);
                    command.setInt(1, setId);
                    for(Vocabulary voc: add) {
                        command.setInt(2, voc.id);
                        command.executeUpdate();
                    }
                }

                if(remove != null) {
                    command = connection.prepareStatement(SET_REMOVE_VOC_COMMAND);
                    command.setInt(1, setId);
                    for(Vocabulary voc: remove) {
                        command.setInt(2, voc.id);
                        command.executeUpdate();
                    }
                }
            }
            catch (Exception ex) {
                Main.handleError("Failed to update vocabulary set: " + ex, false);
            }
            finally {
                closeResources(statement, result);
            }
        }

    }

    public void deleteVocabularySet(VocabularySet set) {
        new Thread(new SetDeletionTask(set)).start();
    }

    private final class SetDeletionTask implements Runnable {

        private final String command;

        protected SetDeletionTask(VocabularySet set) {
            this.command = new StringBuilder()
                .append("DELETE FROM VocabularySet WHERE ")
                .append(SET_ID_COLUMN).append(" = ")
                .append(set.id).append(";")
                .toString();
        }

        public void run() {
            Statement statement = null;
            try {
                openConnection();
                (statement = connection.createStatement()).executeUpdate(command);
            }
            catch (Exception ex) {
                Main.handleError("Failed to delete vocabulary set: " + ex, false);
            }
            finally {
                closeResources(statement, null);
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
            Statement statement = null;
            try {
                openConnection();
                (statement = connection.createStatement()).executeUpdate(command);
            }
            catch (Exception ex) {
                Main.handleError("Failed to store statistics: " + ex, false);
            }
            finally {
                closeResources(statement, null);
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
                .append(key.theHigherTheBetter ? " DESC;" : " ASC;")
                .toString();
        }

        public void run() {
            final LinkedHashMap<String,Integer> ranking = new LinkedHashMap<String,Integer>();
            Statement statement = null;
            ResultSet stats = null;
            try {
                openConnection();

                stats = (statement = connection.createStatement()).executeQuery(query);
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
            finally {
                closeResources(statement, stats);
            }

            consumer.accept(ranking);
        }

    }

    private static void closeResources(Statement statement, ResultSet result) {
        if(result != null) {
            try {
                result.close();
            }
            catch (Exception ex) {}
        }
        if(statement != null) {
            try {
                if(statement != null)
                    statement.close();
            }
            catch (Exception ex) {}
        }
    }

}
