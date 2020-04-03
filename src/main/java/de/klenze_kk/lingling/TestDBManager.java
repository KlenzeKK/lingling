package de.klenze_kk.lingling;

import java.util.*;
import java.util.function.Consumer;

import de.klenze_kk.lingling.logic.*;

public final class TestDBManager {

    public TestDBManager(String host, short port, String database, String userName, String password) {}

    public void loadVocabulary(Consumer<Set<Vocabulary>> consumer) {
        new Thread(new VocabularyLoader(consumer)).start();
    }

    private final class VocabularyLoader implements Runnable {

        private final Consumer<Set<Vocabulary>> consumer;

        protected VocabularyLoader(Consumer<Set<Vocabulary>> consumer) {
            this.consumer = consumer;
        }

        public void run() {
            final Set<Vocabulary> vocabulary = new HashSet<Vocabulary>();

            vocabulary.add(new Vocabulary("你", "nǐ", "ni", "du", "10", (short) 1));
            vocabulary.add(new Vocabulary("好", "hǎo", "hao", "gut", "10", (short) 1));
            vocabulary.add(new Vocabulary("蔬菜", "shūcài", "shucai", "Gemüsegericht", "11-1", (short) 30));

            consumer.accept(vocabulary);
        }

    }

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
            if(!name.equals("KlenzeKK") || !password.equals("123")) consumer.accept(null);

            consumer.accept(new User(name, new EnumMap<StatisticKey,Integer>(StatisticKey.class)));
        }

    }

    public User registerUser(String userName, String password) {
        return new User(userName, new EnumMap<StatisticKey,Integer>(StatisticKey.class));
    }

    public void updateStats(User user, StatisticKey key, int newValue) {}

    public void createRanking(Consumer<LinkedHashMap<String,Integer>> consumer, StatisticKey key) {
        new Thread(new RankingCreator(consumer)).start();
    }

    private final class RankingCreator implements Runnable {

        final Consumer<LinkedHashMap<String,Integer>> consumer;

        protected RankingCreator(Consumer<LinkedHashMap<String,Integer>> consumer) {
            this.consumer = consumer;
        }

        public void run() {
            final LinkedHashMap<String,Integer> m = new LinkedHashMap<String,Integer>();
            m.put("KlenzeKK", 5);

            consumer.accept(m);
        }

    }

}
