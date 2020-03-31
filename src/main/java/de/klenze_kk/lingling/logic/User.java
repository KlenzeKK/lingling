package de.klenze_kk.lingling.logic;

import java.util.EnumMap;

public final class User {

    public final String name;
    private final EnumMap<StatisticKey,Integer> stats;

    public User(String name, EnumMap<StatisticKey,Integer> stats) {
        this.name = name;
        this.stats = new EnumMap<StatisticKey,Integer>(stats);
    }

    public void setValue(StatisticKey key, int value) {
        stats.put(key, Integer.valueOf(value));
    }

    public int getValue(StatisticKey key) {
        final Integer cachedValue = stats.get(key);
        return cachedValue != null ? cachedValue.intValue() : 0;
    }

}
