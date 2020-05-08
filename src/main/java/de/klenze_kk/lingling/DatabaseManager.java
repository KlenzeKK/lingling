package de.klenze_kk.lingling;

import java.sql.*;
import java.util.*;
import java.util.function.Consumer;

import de.klenze_kk.lingling.games.RankingTable;
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

    protected synchronized PreparedStatement createStatement(String sqlTemplate) throws SQLException {
        openConnection();
        return connection.prepareStatement(sqlTemplate);
    }

    public synchronized void closeConnection() {
        if(connection == null) return;

        try {
            if(!connection.isClosed())
                connection.close();
            connection = null;
        }
        catch (Exception ex) {
            Main.handleError("Failed to close SQL connection: " + ex, false);
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
    private static final String VOCABULARY_QUERY = "SELECT * FROM vocabulary ORDER BY Translation;";

    private static final String USER_COLUMN = "Username";
    private static final String PASSWORD_COLUMN = "Password";

    private static final String SET_ID_COLUMN = "SetID";
    private static final String SET_NAME_COLUMN = "Name";    
    private static final String SET_QUERY = "SELECT * FROM VocabularySet, setcontents WHERE VocabularySet.SetID = setcontents.SetID AND (Username IS NULL OR Username=?);";

    private static final String GIF_QUERY = "SELECT * FROM gifs";
    private static final String CHARACTER_COLUMN = "Character";
    private static final String GIF_COLUMN = "Gif";

    private final class VocabularyLoader implements Runnable {

        private final String userName;
        private final Consumer<List<Vocabulary>> vocConsumer;
        private final Consumer<Set<VocabularySet>> setConsumer;

        protected VocabularyLoader(User user, Consumer<List<Vocabulary>> vocConsumer, Consumer<Set<VocabularySet>> setConsumer) {
            this.userName = user.name;
            this.vocConsumer = vocConsumer;
            this.setConsumer = setConsumer;
        }

        public void run() {
            PreparedStatement statement = null;
            ResultSet result = null;
            try {
                result = (statement = createStatement(VOCABULARY_QUERY)).executeQuery();

                final Map<Integer,Vocabulary> vocabulary = this.loadVocabulary(result);
                closeResources(statement, result);

                statement = createStatement(SET_QUERY);
                statement.setString(1, userName);
                setConsumer.accept(this.loadSets(result = statement.executeQuery(), vocabulary));
                closeResources(statement, result);

                result = (statement = createStatement(GIF_QUERY)).executeQuery();
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
                    templateSet = result.getString(USER_COLUMN) == null;
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

    public void performLogin(Consumer<User> consumer, String userName, String password) {
      new Thread(new LoginTask(consumer, userName, password)).start();
    }

    private static final String LOGIN_QUERY = "SELECT * FROM user WHERE Username=?;";

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
            PreparedStatement statement = null;
            ResultSet data = null;
            try {
                (statement = createStatement(LOGIN_QUERY)).setString(1, name);
                if((data = statement.executeQuery()).next()) {
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

    private static final String USERNAME_COUNT_QUERY = "SELECT COUNT(*) FROM user WHERE Username=?;";
    private static final String REGISTRATION_COMMAND = "INSERT INTO user (Username, Password) VALUES (?, ?);";

    public void registerUser(Consumer<User> consumer, String userName, String password) {
        new RegistrationTask(consumer, userName, password).run();
    }

    private final class RegistrationTask implements Runnable {

        private final Consumer<User> consumer;
        private final String userName;
        private final String password;

        protected RegistrationTask(Consumer<User> consumer, String userName, String password) {
            this.consumer = consumer;
            this.userName = userName;
            this.password = password;
        }

        public void run() { 
            final int userCount;
            PreparedStatement statement = null;
            ResultSet result = null;
            try {  
                (statement = createStatement(USERNAME_COUNT_QUERY)).setString(1, userName);
                (result = statement.executeQuery()).next();
                userCount = result.getInt(1);
            }
            catch (Exception ex) {
                Main.handleError("Failed to check availability of username: " + ex, true);
                return;
            }
            finally {
                closeResources(statement, result);
            }

            if(userCount > 0) {
                consumer.accept(null);
                return;
            }

            statement = null;
            result = null;

            try {
                statement = createStatement(REGISTRATION_COMMAND);
                statement.setString(1, userName);
                statement.setString(2, password);
                statement.executeUpdate();
            }
            catch (Exception ex) {
                Main.handleError("Failed to register user: " + ex, true);
            }
            finally {
                closeResources(statement, null);
            }
        }

    }

    private static final String SET_CREATION_COMMAND = "INSERT INTO vocabularyset (Username, Name) VALUES (?, ?);";
    private static final String[] SET_GENERATED_KEY_COLUMNS = { SET_ID_COLUMN }; 

    public void createVocabularySet(Consumer<VocabularySet> consumer, User user, String name, Set<Vocabulary> initialContent) {
        new Thread(new SetCreator(consumer, user, name, initialContent)).start();
    }

    private final class SetCreator implements Runnable {

        private final Consumer<VocabularySet> consumer;
        private final Set<Vocabulary> initialContent;
        private final String name;
        private final String userName;

        protected SetCreator(Consumer<VocabularySet> consumer, User user, String name, Set<Vocabulary> initialContent) {
            this.consumer = consumer;
            this.userName = user.name;
            this.name = name;
            this.initialContent = initialContent;
        }

        public void run() {
            final VocabularySet createdSet;
            PreparedStatement statement = null;
            ResultSet result = null;
            try {
                openConnection();
                synchronized (this) {
                    statement = connection.prepareStatement(SET_CREATION_COMMAND, SET_GENERATED_KEY_COLUMNS);
                }
                
                statement.setString(1, userName);
                statement.setString(2, name);
                statement.executeUpdate();
                result = statement.getGeneratedKeys();
                if(!result.next()) throw new SQLException("Insert failed - no generated key was returned");

                createdSet = new VocabularySet(result.getInt(1), name, false);
            }
            catch (Exception ex) {
                Main.handleError("Failed to create vocabulary set: " + ex, false);
                return;
            }
            finally {
                closeResources(statement, result);
            }

            createdSet.registerVocs(initialContent);
            consumer.accept(createdSet);
            new SetModifier(createdSet, initialContent, null).run();
        }

    }

    private static final String SET_ADD_VOC_COMMAND = "INSERT INTO setcontents VALUES (?, ?);";
    private static final String SET_REMOVE_VOC_COMMAND = "DELETE FROM setcontents WHERE SetID=? AND VocID=?;";

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
            PreparedStatement command = null;
            try {
                if(add != null) {
                    command = createStatement(SET_ADD_VOC_COMMAND);
                    command.setInt(1, setId);
                    for(Vocabulary voc: add) {
                        command.setInt(2, voc.id);
                        command.executeUpdate();
                    }
                    closeResources(command, null);
                }

                if(remove != null) {
                    command = createStatement(SET_REMOVE_VOC_COMMAND);
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
                closeResources(command, null);
            }
        }

    }

    private static final String SET_DELETION_COMMAND = "DELETE FROM vocabularyset WHERE SetID=?;";

    public void deleteVocabularySet(VocabularySet set) {
        new Thread(new SetDeletionTask(set)).start();
    }

    private final class SetDeletionTask implements Runnable {

        private final int setId;

        protected SetDeletionTask(VocabularySet set) {
            this.setId = set.id;
        }

        public void run() {
            PreparedStatement statement = null;
            try {
                (statement = createStatement(SET_DELETION_COMMAND)).setInt(1, setId);
                statement.executeUpdate();
            }
            catch (Exception ex) {
                Main.handleError("Failed to delete vocabulary set: " + ex, false);
            }
            finally {
                closeResources(statement, null);
            }
        }

    }

    public void updateStats(User user, Map<StatisticKey,Integer> newValues) {
        new Thread(new StatisticUpdater(user, newValues)).start();
    }

    private final class StatisticUpdater implements Runnable {

        private final String userName;
        private final Map<StatisticKey,Integer> newValues;

        protected StatisticUpdater(User user, Map<StatisticKey,Integer> newValues) {
            this.userName = user.name;
            this.newValues = newValues;
        }

        public void run() {
            PreparedStatement current = null;
            String currentCommand;
            try {
                for(Map.Entry<StatisticKey,Integer> e: newValues.entrySet()) {
                    currentCommand = "UPDATE user SET " + e.getKey().databaseColumn + "=? WHERE Username=?;";
                    current = createStatement(currentCommand);
                    current.setInt(1, e.getValue());
                    current.setString(2, userName);
                    current.executeUpdate();
                    closeResources(current, null);
                    current = null;
                }
            }
            catch (Exception ex) {
                Main.handleError("Failed to store statistics: " + ex, false);
            }
            finally {
                closeResources(current, null);
            }
        }

    }

    public void createRanking(Consumer<RankingTable[]> consumer, User user, StatisticKey... keys) {
       new Thread(new RankingCreator(consumer, user, keys)).start();
    }

    private final class RankingCreator implements Runnable {

        private final Consumer<RankingTable[]> consumer;
        private final User user;
        private final StatisticKey[] keys;

        protected RankingCreator(Consumer<RankingTable[]> consumer, User user, StatisticKey[] keys) {
            this.consumer = consumer;
            this.user = user;
            this.keys = keys;
        }

        public void run() {
            final RankingTable[] tables = new RankingTable[keys.length];
            PreparedStatement statement = null;
            ResultSet result = null;
            RankingTable.RankingBuilder current;
            try {
                for(int i = 0; i < keys.length; i++) {
                    current = new RankingTable.RankingBuilder();
                    statement = createStatement("SELECT * FROM (SELECT @rank:=@rank+1 AS Rank, Username, " + keys[i].databaseColumn + " AS Value FROM User, (SELECT @rank:=0) AS n ORDER BY Value DESC) AS r WHERE Rank<=? OR Username=?");
                    statement.setInt(1, 10);
                    statement.setString(2, user.name);
                    result = statement.executeQuery();
                    while (result.next())
                        current.addUser(result.getInt("Rank"), result.getString(USER_COLUMN), result.getInt("Value"));
                    tables[i] = current.build(keys[i]);
                    closeResources(statement, result);
                }
            }
            catch (Exception ex) {
                consumer.accept(null);
                Main.handleError("Failed to load statistics: " + ex, false);
                return;
            }
            finally {
                closeResources(statement, result);
            }

            consumer.accept(tables);
        }

    }

    private static void closeResources(Statement statement, ResultSet result) {
        if(result != null) {
            try {
                if(!result.isClosed())
                    result.close();
            }
            catch (Exception ex) {}
        }
        if(statement != null) {
            try {
                if(!statement.isClosed())
                    statement.close();
            }
            catch (Exception ex) {}
        }
    }

}
