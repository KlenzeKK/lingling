package de.klenze_kk.lingling.logic;

import java.util.EnumMap;
import java.util.Map;

import de.klenze_kk.lingling.Main;

public final class User {

    public final String name;
    private final EnumMap<StatisticKey,Integer> stats;

    public User(String name, EnumMap<StatisticKey,Integer> stats) {
        this.name = name;
        this.stats = new EnumMap<StatisticKey,Integer>(stats);
    }

    public void setValue(StatisticKey key, int value) {
        synchronized (this) {
            stats.put(key, Integer.valueOf(value));
        }
        Main.getDatabaseManager().updateStats(this, key, value);
    }

    public void increaseValue(StatisticKey key, int amount) {
        this.setValue(key, this.getValue(key) + amount);
    }

    public void updateValues(Map<StatisticKey,Integer> newValues) {
        synchronized (this) {
            stats.putAll(newValues);
        }
    }

    public synchronized int getValue(StatisticKey key) {
        final Integer cachedValue = stats.get(key);
        return cachedValue != null ? cachedValue.intValue() : 0;
    }

}
